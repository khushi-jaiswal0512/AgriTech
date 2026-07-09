package com.agritech.product.event.consumer;

import com.agritech.common.constants.KafkaTopics;
import com.agritech.common.event.RatingSubmittedEvent;
import com.agritech.product.entity.Product;
import com.agritech.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Consumes RatingSubmittedEvent to update denormalized rating columns on Product.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RatingEventConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(
            topics = KafkaTopics.RATING_SUBMITTED,
            groupId = "product-service-rating-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    @CacheEvict(value = "product", key = "#event.productId")
    public void handleRatingSubmitted(RatingSubmittedEvent event) {
        log.info("Received RatingSubmittedEvent for productId={}", event.getProductId());

        productRepository.findById(event.getProductId()).ifPresentOrElse(
                product -> {
                    product.setAvgRating(BigDecimal.valueOf(event.getNewAvgRating()));
                    product.setTotalRatings(event.getTotalRatings());
                    productRepository.save(product);
                    log.info("Updated rating for productId={}: avg={}, total={}",
                            event.getProductId(), event.getNewAvgRating(), event.getTotalRatings());
                },
                () -> log.warn("Product not found for productId={}, ignoring rating event", event.getProductId())
        );
    }
}
