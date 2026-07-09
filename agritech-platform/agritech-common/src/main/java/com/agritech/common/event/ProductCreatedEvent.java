package com.agritech.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Kafka event published by Product Service when a new product is created and approved.
 * Consumed by Notification Service and Admin Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {

    private Long productId;
    private Long farmerId;
    private String productName;
    private BigDecimal price;
    private String unit;
    private String categoryName;
    private String city;
    private String state;
}
