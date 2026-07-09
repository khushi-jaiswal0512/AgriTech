package com.agritech.product.dto.response;

import com.agritech.product.enums.ApprovalStatus;
import com.agritech.product.enums.ProductStatus;
import com.agritech.product.enums.ProductUnit;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private Long farmerId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private int availableQuantity;
    private ProductUnit unit;
    private boolean organic;
    private LocalDate harvestDate;
    private BigDecimal avgRating;
    private int totalRatings;
    private ProductStatus productStatus;
    private ApprovalStatus approvalStatus;
    private String rejectionReason;
    private String city;
    private String state;
    private List<ProductImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
