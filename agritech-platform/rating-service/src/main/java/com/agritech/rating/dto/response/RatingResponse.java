package com.agritech.rating.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RatingResponse {
    private Long id;
    private Long productId;
    private Long userId;
    private Integer stars;
    private String review;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
