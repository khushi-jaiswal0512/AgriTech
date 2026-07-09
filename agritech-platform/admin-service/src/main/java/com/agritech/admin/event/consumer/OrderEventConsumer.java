package com.agritech.admin.event.consumer;

import com.agritech.admin.service.AdminAnalyticsService;
import com.agritech.common.constants.KafkaTopics;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final AdminAnalyticsService adminAnalyticsService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.ORDER_DELIVERED, groupId = "admin-service-group")
    public void handleOrderDelivered(String message) {
        log.info("Received order delivered event: {}", message);
        try {
            JsonNode payload = objectMapper.readTree(message);
            BigDecimal totalAmount = new BigDecimal(payload.get("totalAmount").asText());
            
            // Increment analytics
            adminAnalyticsService.incrementOrderAndRevenue(totalAmount);
            
            // We could also update top products here by iterating over order items
            // but for simplicity, we'll focus on the main KPIs first.
        } catch (Exception e) {
            log.error("Error processing order delivered event", e);
        }
    }
}
