package com.agritech.order.dto.response;

import com.agritech.order.enums.BuyerType;
import com.agritech.order.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long buyerId;
    private BuyerType buyerType;
    private Long farmerId;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private String cancellationReason;
    private String cancelledBy;
    private Long shippingAddressId;
    private String notes;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
