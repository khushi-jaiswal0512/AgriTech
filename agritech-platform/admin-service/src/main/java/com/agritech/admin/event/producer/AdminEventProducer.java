package com.agritech.admin.event.producer;

import com.agritech.common.constants.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishFarmerVerified(Long farmerId) {
        String payload = String.format("{\"farmerId\":%d,\"status\":\"VERIFIED\"}", farmerId);
        kafkaTemplate.send(KafkaTopics.FARMER_VERIFIED, farmerId.toString(), payload);
        log.info("Published farmer verified event for farmer {}", farmerId);
    }

    public void publishProductApproved(Long productId) {
        String payload = String.format("{\"productId\":%d,\"status\":\"APPROVED\"}", productId);
        kafkaTemplate.send(KafkaTopics.PRODUCT_APPROVED, productId.toString(), payload);
        log.info("Published product approved event for product {}", productId);
    }

    public void publishProductRejected(Long productId) {
        String payload = String.format("{\"productId\":%d,\"status\":\"REJECTED\"}", productId);
        kafkaTemplate.send(KafkaTopics.PRODUCT_REJECTED, productId.toString(), payload);
        log.info("Published product rejected event for product {}", productId);
    }
}
