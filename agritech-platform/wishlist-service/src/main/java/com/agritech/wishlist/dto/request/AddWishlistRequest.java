package com.agritech.wishlist.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddWishlistRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;
}
