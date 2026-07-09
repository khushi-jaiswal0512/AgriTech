package com.agritech.order.dto.response;

import com.agritech.order.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatusHistoryResponse {
    private Long id;
    private Long orderId;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private Long changedBy;
    private String notes;
    private LocalDateTime createdAt;
}
