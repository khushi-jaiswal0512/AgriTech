package com.agritech.order.service;

import com.agritech.order.dto.request.CreateOrderRequest;
import com.agritech.order.dto.request.UpdateOrderStatusRequest;
import com.agritech.order.dto.response.OrderResponse;
import com.agritech.order.dto.response.OrderStatusHistoryResponse;
import com.agritech.order.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long buyerId, CreateOrderRequest request);
    OrderResponse getOrder(Long orderId);
    OrderResponse getOrderByNumber(String orderNumber);
    OrderResponse updateOrderStatus(Long orderId, Long userId, UpdateOrderStatusRequest request);
    List<OrderStatusHistoryResponse> getOrderStatusHistory(Long orderId);
}
