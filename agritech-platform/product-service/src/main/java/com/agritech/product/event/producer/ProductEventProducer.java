package com.agritech.product.event.producer;

import com.agritech.common.constants.KafkaTopics;
import com.agritech.common.event.ProductCreatedEvent;
import com.agritech.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka event producer for product lifecycle events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a product created event when a new product is approved.
     */
    public void publishProductCreated(Product product) {
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId(product.getId())
                .farmerId(product.getFarmerId())
                .productName(product.getName())
                .price(product.getPrice())
                .unit(product.getUnit().name())
                .categoryName(product.getCategory().getName())
                .city(product.getCity())
                .state(product.getState())
                .build();

        kafkaTemplate.send(KafkaTopics.PRODUCT_CREATED, String.valueOf(product.getId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ProductCreatedEvent for productId={}", product.getId(), ex);
                    } else {
                        log.info("Published ProductCreatedEvent for productId={}", product.getId());
                    }
                });
    }
}
