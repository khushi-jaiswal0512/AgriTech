package com.agritech.user.dto.request;

import com.agritech.user.enums.FarmingType;
import com.agritech.user.enums.GovernmentIdType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request DTO for creating or updating farmer-specific profile details.
 */
@Data
public class FarmerDetailsRequest {

    @Size(max = 200)
    private String farmName;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal farmSizeAcres;

    private FarmingType farmingType;

    @Min(0) @Max(60)
    private Integer experienceYears;

    @Size(max = 100)
    private String governmentId;

    private GovernmentIdType governmentIdType;
}
