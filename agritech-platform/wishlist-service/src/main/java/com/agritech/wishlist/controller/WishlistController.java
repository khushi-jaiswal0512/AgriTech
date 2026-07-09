package com.agritech.wishlist.controller;

import com.agritech.common.response.ApiResponse;
import com.agritech.wishlist.dto.request.AddWishlistRequest;
import com.agritech.wishlist.dto.response.WishlistResponse;
import com.agritech.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Wishlist operations")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get current user's wishlist")
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getWishlist(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getUserWishlist(userId)));
    }

    @PostMapping
    @Operation(summary = "Add a product to wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddWishlistRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Added to wishlist", wishlistService.addToWishlist(userId, request)));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove a product from wishlist")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist"));
    }

    @GetMapping("/{productId}/exists")
    @Operation(summary = "Check if product is in wishlist")
    public ResponseEntity<ApiResponse<Boolean>> checkWishlist(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.isInWishlist(userId, productId)));
    }
}
