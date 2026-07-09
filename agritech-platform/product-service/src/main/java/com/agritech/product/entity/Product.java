package com.agritech.product.entity;

import com.agritech.common.entity.BaseEntity;
import com.agritech.product.enums.ApprovalStatus;
import com.agritech.product.enums.ProductStatus;
import com.agritech.product.enums.ProductUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Product entity — the core catalog item.
 * available_quantity has a DB-level CHECK >= 0 and JPA-level @Min(0).
 */
@Entity
@Table(name = "products",
       indexes = {
           @Index(name = "idx_farmer_id", columnList = "farmer_id"),
           @Index(name = "idx_category_id", columnList = "category_id"),
           @Index(name = "idx_approval_status", columnList = "approval_status"),
           @Index(name = "idx_product_status", columnList = "product_status"),
           @Index(name = "idx_is_organic", columnList = "is_organic"),
           @Index(name = "idx_price", columnList = "price"),
           @Index(name = "idx_city", columnList = "city"),
           @Index(name = "idx_state", columnList = "state"),
           @Index(name = "idx_avg_rating", columnList = "avg_rating")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** References auth_user_id in user-service — cross-service soft FK */
    @Column(name = "farmer_id", nullable = false)
    private Long farmerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Min(0)
    @Column(name = "available_quantity", nullable = false)
    @Builder.Default
    private int availableQuantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProductUnit unit;

    @Column(name = "is_organic", nullable = false)
    @Builder.Default
    private boolean organic = false;

    @Column(name = "harvest_date")
    private LocalDate harvestDate;

    @Column(name = "avg_rating", precision = 3, scale = 1)
    @Builder.Default
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "total_ratings", nullable = false)
    @Builder.Default
    private int totalRatings = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false, length = 20)
    @Builder.Default
    private ProductStatus productStatus = ProductStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();
}
