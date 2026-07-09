package com.agritech.user.entity;

import com.agritech.common.entity.BaseEntity;
import com.agritech.user.enums.FarmingType;
import com.agritech.user.enums.GovernmentIdType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Farmer-specific details entity — linked 1-to-1 with UserProfile.
 * Stores farm metadata and government identity for KYC verification.
 */
@Entity
@Table(name = "farmer_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private UserProfile userProfile;

    @Column(name = "farm_name", length = 200)
    private String farmName;

    @Column(name = "farm_size_acres", precision = 10, scale = 2)
    private BigDecimal farmSizeAcres;

    @Enumerated(EnumType.STRING)
    @Column(name = "farming_type", length = 20)
    @Builder.Default
    private FarmingType farmingType = FarmingType.CONVENTIONAL;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "government_id", length = 100)
    private String governmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "government_id_type", length = 20)
    private GovernmentIdType governmentIdType;
}
