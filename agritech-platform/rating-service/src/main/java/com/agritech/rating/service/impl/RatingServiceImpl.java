package com.agritech.rating.service.impl;

import com.agritech.common.exception.ResourceNotFoundException;
import com.agritech.common.pagination.PagedResponse;
import com.agritech.rating.dto.request.SubmitRatingRequest;
import com.agritech.rating.dto.response.RatingResponse;
import com.agritech.rating.entity.Rating;
import com.agritech.rating.event.RatingEventProducer;
import com.agritech.rating.repository.RatingRepository;
import com.agritech.rating.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingEventProducer eventProducer;

    @Override
    @Transactional
    public RatingResponse submitRating(Long userId, SubmitRatingRequest request) {
        Rating rating = ratingRepository.findByProductIdAndUserId(request.getProductId(), userId)
                .orElseGet(() -> Rating.builder()
                        .productId(request.getProductId())
                        .userId(userId)
                        .build());

        rating.setStars(request.getStars());
        rating.setReview(request.getReview());

        Rating saved = ratingRepository.save(rating);

        // Publish event to update product average rating
        Double avgRating = ratingRepository.getAverageRatingForProduct(request.getProductId());
        Long totalRatings = ratingRepository.countRatingsForProduct(request.getProductId());
        
        eventProducer.sendRatingSubmittedEvent(request.getProductId(), avgRating, totalRatings);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RatingResponse> getProductRatings(Long productId, int page, int size) {
        Page<Rating> ratings = ratingRepository.findByProductId(
                productId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PagedResponse.from(ratings.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public RatingResponse getUserRatingForProduct(Long userId, Long productId) {
        Rating rating = ratingRepository.findByProductIdAndUserId(productId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating", "productId, userId", productId + ", " + userId));
        return mapToResponse(rating);
    }

    private RatingResponse mapToResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .productId(rating.getProductId())
                .userId(rating.getUserId())
                .stars(rating.getStars())
                .review(rating.getReview())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
}
