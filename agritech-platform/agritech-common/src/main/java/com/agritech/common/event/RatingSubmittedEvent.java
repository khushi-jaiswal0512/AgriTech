package com.agritech.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka event published by Rating Service when a new rating is submitted.
 * Consumed by Product Service to update avg_rating and total_ratings denormalized columns.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingSubmittedEvent {

    private Long productId;
    private Long raterId;
    private int rating;
    private double newAvgRating;
    private int totalRatings;
}
