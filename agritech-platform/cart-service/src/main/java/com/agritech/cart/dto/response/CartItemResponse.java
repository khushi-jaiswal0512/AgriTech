package com.agritech.cart.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private Long farmerId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
