package com.agritech.rating.controller;

import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;
import com.agritech.rating.dto.request.SubmitRatingRequest;
import com.agritech.rating.dto.response.RatingResponse;
import com.agritech.rating.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "Product Rating APIs")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @Operation(summary = "Submit or update a rating for a product")
    public ResponseEntity<ApiResponse<RatingResponse>> submitRating(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody SubmitRatingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Rating submitted successfully",
                ratingService.submitRating(userId, request)
        ));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get ratings for a product")
    public ResponseEntity<ApiResponse<PagedResponse<RatingResponse>>> getProductRatings(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                ratingService.getProductRatings(productId, page, size)
        ));
    }

    @GetMapping("/product/{productId}/me")
    @Operation(summary = "Get user's rating for a product")
    public ResponseEntity<ApiResponse<RatingResponse>> getMyRatingForProduct(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(
                ratingService.getUserRatingForProduct(userId, productId)
        ));
    }
}
