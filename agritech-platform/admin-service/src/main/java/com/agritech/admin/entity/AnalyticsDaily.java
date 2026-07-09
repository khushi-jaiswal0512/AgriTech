package com.agritech.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_daily")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "snapshot_date", nullable = false, unique = true)
    private LocalDate snapshotDate;

    @Builder.Default
    @Column(name = "total_users")
    private Integer totalUsers = 0;

    @Builder.Default
    @Column(name = "total_farmers")
    private Integer totalFarmers = 0;

    @Builder.Default
    @Column(name = "total_consumers")
    private Integer totalConsumers = 0;

    @Builder.Default
    @Column(name = "total_retailers")
    private Integer totalRetailers = 0;

    @Builder.Default
    @Column(name = "total_products")
    private Integer totalProducts = 0;

    @Builder.Default
    @Column(name = "total_orders")
    private Integer totalOrders = 0;

    @Builder.Default
    @Column(name = "total_revenue")
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "orders_today")
    private Integer ordersToday = 0;

    @Builder.Default
    @Column(name = "revenue_today")
    private BigDecimal revenueToday = BigDecimal.ZERO;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
