package com.agritech.order.dto.request;

import com.agritech.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull(message = "New status is required")
    private OrderStatus newStatus;

    private String cancellationReason;
    private String notes;
}
