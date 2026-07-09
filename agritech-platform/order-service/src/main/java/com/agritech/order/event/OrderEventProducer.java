package com.agritech.order.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendOrderPlacedEvent(Long orderId, String orderNumber) {
        sendEvent("agritech.order.placed", orderId, Map.of(
                "orderId", orderId,
                "orderNumber", orderNumber,
                "timestamp", System.currentTimeMillis()
        ));
    }

    public void sendOrderAcceptedEvent(Long orderId) {
        sendEvent("agritech.order.accepted", orderId, Map.of("orderId", orderId));
    }

    public void sendOrderCancelledEvent(Long orderId) {
        sendEvent("agritech.order.cancelled", orderId, Map.of("orderId", orderId));
    }

    public void sendOrderDeliveredEvent(Long orderId) {
        sendEvent("agritech.order.delivered", orderId, Map.of("orderId", orderId));
    }

    private void sendEvent(String topic, Long orderId, Object payload) {
        try {
            String message = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topic, String.valueOf(orderId), message);
            log.info("Sent Kafka event to topic {} for orderId {}", topic, orderId);
        } catch (Exception e) {
            log.error("Failed to send Kafka event to topic {}", topic, e);
        }
    }
}
