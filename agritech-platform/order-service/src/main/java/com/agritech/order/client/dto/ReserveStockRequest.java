package com.agritech.order.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReserveStockRequest {
    private Long productId;
    private Integer quantity;
}
