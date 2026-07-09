package com.agritech.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Response DTO representing a user's saved address.
 */
@Data
@Builder
public class AddressResponse {
    private Long id;
    private String label;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private boolean isDefault;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
