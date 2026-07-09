package com.agritech.rating.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitRatingRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Stars are required")
    @Min(value = 1, message = "Minimum 1 star")
    @Max(value = 5, message = "Maximum 5 stars")
    private Integer stars;

    private String review;
}
