package com.agritech.order.service.impl;

import com.agritech.common.exception.BadRequestException;
import com.agritech.common.exception.ResourceNotFoundException;
import com.agritech.order.client.ProductServiceClient;
import com.agritech.order.client.dto.ReserveStockRequest;
import com.agritech.order.dto.request.CreateOrderRequest;
import com.agritech.order.dto.request.UpdateOrderStatusRequest;
import com.agritech.order.dto.response.OrderResponse;
import com.agritech.order.dto.response.OrderStatusHistoryResponse;
import com.agritech.order.entity.Order;
import com.agritech.order.entity.OrderItem;
import com.agritech.order.entity.OrderStatusHistory;
import com.agritech.order.enums.OrderStatus;
import com.agritech.order.event.OrderEventProducer;
import com.agritech.order.mapper.OrderMapper;
import com.agritech.order.repository.OrderItemRepository;
import com.agritech.order.repository.OrderRepository;
import com.agritech.order.repository.OrderStatusHistoryRepository;
import com.agritech.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;
    private final OrderEventProducer orderEventProducer;

    @Override
    @Transactional
    public OrderResponse createOrder(Long buyerId, CreateOrderRequest request) {
        // 1. Calculate total
        BigDecimal totalAmount = request.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Build order
        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .buyerId(buyerId)
                .buyerType(request.getBuyerType())
                .farmerId(request.getFarmerId())
                .totalAmount(totalAmount)
                .shippingAddressId(request.getShippingAddressId())
                .notes(request.getNotes())
                .orderStatus(OrderStatus.PENDING)
                .build();

        // 3. Build items
        List<OrderItem> items = request.getItems().stream().map(req -> OrderItem.builder()
                .order(order)
                .productId(req.getProductId())
                .productName(req.getProductName())
                .quantity(req.getQuantity())
                .unitPrice(req.getUnitPrice())
                .totalPrice(req.getUnitPrice().multiply(new BigDecimal(req.getQuantity())))
                .build()).collect(Collectors.toList());
        order.setItems(items);

        // 4. Reserve Stock Synchronously via Feign
        try {
            List<ReserveStockRequest> reserveRequests = request.getItems().stream()
                    .map(item -> ReserveStockRequest.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .build())
                    .collect(Collectors.toList());
            productServiceClient.reserveStock(buyerId, reserveRequests);
        } catch (Exception e) {
            log.error("Failed to reserve stock for order {}", orderNumber, e);
            throw new BadRequestException("Failed to reserve stock. Items might be out of stock.");
        }

        // 5. Save order and history
        Order savedOrder = orderRepository.save(order);
        recordStatusHistory(savedOrder, null, OrderStatus.PENDING, buyerId, "Order placed");

        // 6. Publish event
        orderEventProducer.sendOrderPlacedEvent(savedOrder.getId(), savedOrder.getOrderNumber());

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Long userId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        OrderStatus oldStatus = order.getOrderStatus();
        OrderStatus newStatus = request.getNewStatus();

        if (oldStatus == newStatus) {
            return orderMapper.toResponse(order);
        }

        order.setOrderStatus(newStatus);
        
        if (newStatus == OrderStatus.CANCELLED) {
            order.setCancellationReason(request.getCancellationReason());
            order.setCancelledBy(userId.equals(order.getBuyerId()) ? "BUYER" : "FARMER");
        }

        Order savedOrder = orderRepository.save(order);
        recordStatusHistory(savedOrder, oldStatus, newStatus, userId, request.getNotes());

        // Publish events based on new status
        if (newStatus == OrderStatus.ACCEPTED) {
            orderEventProducer.sendOrderAcceptedEvent(orderId);
        } else if (newStatus == OrderStatus.CANCELLED) {
            orderEventProducer.sendOrderCancelledEvent(orderId);
        } else if (newStatus == OrderStatus.DELIVERED) {
            orderEventProducer.sendOrderDeliveredEvent(orderId);
        }

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponse> getOrderStatusHistory(Long orderId) {
        return statusHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
                .map(orderMapper::toHistoryResponse)
                .collect(Collectors.toList());
    }

    private void recordStatusHistory(Order order, OrderStatus oldStatus, OrderStatus newStatus, Long changedBy, String notes) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .notes(notes)
                .build();
        statusHistoryRepository.save(history);
    }
}
