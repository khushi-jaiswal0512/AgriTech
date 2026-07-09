package com.agritech.user.dto.request;

import com.agritech.user.enums.BusinessType;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Request DTO for creating or updating retailer-specific profile details.
 */
@Data
public class RetailerDetailsRequest {

    @NotBlank(message = "Business name is required")
    @Size(max = 200)
    private String businessName;

    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
             message = "Invalid GST number format")
    private String gstNumber;

    private BusinessType businessType;

    @Size(max = 100)
    private String licenseNumber;
}
