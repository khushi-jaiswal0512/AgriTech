package com.agritech.admin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "top_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Builder.Default
    @Column(name = "total_sold")
    private Integer totalSold = 0;

    @Builder.Default
    @Column(name = "total_revenue")
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(nullable = false)
    private String period; // DAILY, WEEKLY, MONTHLY

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;
}
