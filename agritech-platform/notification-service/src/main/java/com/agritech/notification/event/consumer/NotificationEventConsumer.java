package com.agritech.notification.event.consumer;

import com.agritech.common.constants.KafkaTopics;
import com.agritech.notification.enums.NotificationType;
import com.agritech.notification.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.ORDER_PLACED, groupId = "notification-service-group")
    public void handleOrderPlaced(String message) {
        log.info("Received order placed event: {}", message);
        try {
            JsonNode payload = objectMapper.readTree(message);
            Long farmerId = payload.get("farmerId").asLong();
            String orderNumber = payload.get("orderNumber").asText();
            
            notificationService.createNotification(
                    farmerId,
                    NotificationType.ORDER_PLACED,
                    "New Order Received",
                    "You have received a new order (" + orderNumber + ")",
                    orderNumber,
                    "ORDER"
            );
        } catch (Exception e) {
            log.error("Error processing order placed event", e);
        }
    }

    @KafkaListener(topics = KafkaTopics.ORDER_ACCEPTED, groupId = "notification-service-group")
    public void handleOrderAccepted(String message) {
        log.info("Received order accepted event: {}", message);
        try {
            JsonNode payload = objectMapper.readTree(message);
            Long buyerId = payload.get("buyerId").asLong();
            String orderNumber = payload.get("orderNumber").asText();
            
            notificationService.createNotification(
                    buyerId,
                    NotificationType.ORDER_ACCEPTED,
                    "Order Accepted",
                    "Your order " + orderNumber + " has been accepted by the farmer",
                    orderNumber,
                    "ORDER"
            );
        } catch (Exception e) {
            log.error("Error processing order accepted event", e);
        }
    }

    // Add other event listeners (ORDER_DELIVERED, PRODUCT_APPROVED, etc) similarly
}
