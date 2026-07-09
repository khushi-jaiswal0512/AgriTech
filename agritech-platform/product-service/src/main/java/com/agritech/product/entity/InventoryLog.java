package com.agritech.product.entity;

import com.agritech.product.enums.InventoryChangeType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable audit log for every inventory change on a product.
 */
@Entity
@Table(name = "inventory_log",
       indexes = {
           @Index(name = "idx_inv_product_id", columnList = "product_id"),
           @Index(name = "idx_inv_created_at", columnList = "created_at")
       })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 20)
    private InventoryChangeType changeType;

    @Column(name = "quantity_change", nullable = false)
    private int quantityChange;

    @Column(name = "previous_qty", nullable = false)
    private int previousQty;

    @Column(name = "new_qty", nullable = false)
    private int newQty;

    @Column(length = 255)
    private String reason;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
