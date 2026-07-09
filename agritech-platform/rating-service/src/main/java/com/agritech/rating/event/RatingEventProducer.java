package com.agritech.rating.event;

import com.agritech.common.constants.KafkaTopics;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RatingEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendRatingSubmittedEvent(Long productId, Double averageRating, Long totalRatings) {
        try {
            Map<String, Object> payload = Map.of(
                    "productId", productId,
                    "averageRating", averageRating,
                    "totalRatings", totalRatings
            );
            String message = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(KafkaTopics.RATING_SUBMITTED, String.valueOf(productId), message);
            log.info("Published rating submitted event for productId: {}", productId);
        } catch (Exception e) {
            log.error("Failed to publish rating event for productId: {}", productId, e);
        }
    }
}
