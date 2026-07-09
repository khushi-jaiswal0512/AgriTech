package com.agritech.user.entity;

import com.agritech.common.entity.BaseEntity;
import com.agritech.user.enums.BusinessType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Retailer-specific details entity — linked 1-to-1 with UserProfile.
 * Stores business registration and GST information.
 */
@Entity
@Table(name = "retailer_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetailerDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private UserProfile userProfile;

    @Column(name = "business_name", nullable = false, length = 200)
    private String businessName;

    @Column(name = "gst_number", length = 20)
    private String gstNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", length = 30)
    private BusinessType businessType;

    @Column(name = "license_number", length = 100)
    private String licenseNumber;
}
