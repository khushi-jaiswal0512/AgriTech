package com.agritech.product.dto.request;

import com.agritech.product.enums.InventoryChangeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateInventoryRequest {

    @NotNull(message = "Change type is required")
    private InventoryChangeType changeType;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String reason;

    private String referenceId;
}
