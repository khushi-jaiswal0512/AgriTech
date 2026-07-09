package com.agritech.product.dto.request;

import com.agritech.product.enums.ProductUnit;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200)
    private String name;

    @Size(max = 5000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull
    private ProductUnit unit;

    private Long categoryId;

    private boolean organic;

    private LocalDate harvestDate;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;
}
