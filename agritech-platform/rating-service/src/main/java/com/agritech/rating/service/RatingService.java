package com.agritech.rating.service;

import com.agritech.common.pagination.PagedResponse;
import com.agritech.rating.dto.request.SubmitRatingRequest;
import com.agritech.rating.dto.response.RatingResponse;

public interface RatingService {
    RatingResponse submitRating(Long userId, SubmitRatingRequest request);
    PagedResponse<RatingResponse> getProductRatings(Long productId, int page, int size);
    RatingResponse getUserRatingForProduct(Long userId, Long productId);
}
