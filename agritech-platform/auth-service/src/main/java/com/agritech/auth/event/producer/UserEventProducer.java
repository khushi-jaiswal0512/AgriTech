package com.agritech.auth.event.producer;

import com.agritech.auth.entity.User;
import com.agritech.common.constants.KafkaTopics;
import com.agritech.common.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka event producer for user lifecycle events.
 *
 * <p>Publishes domain events so downstream services (e.g., User Service,
 * Notification Service) can react without tight coupling to Auth Service.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a {@link UserRegisteredEvent} to the Kafka {@code user.registered} topic.
     *
     * <p>The User Service subscribes to this topic and creates the full user profile
     * upon receiving this event.
     *
     * @param user      the newly registered user entity
     * @param firstName the user's first name (from the registration form)
     * @param lastName  the user's last name (from the registration form)
     * @param phone     the user's phone number; may be {@code null}
     */
    public void publishUserRegistered(User user, String firstName, String lastName, String phone) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .authUserId(user.getId())
                .email(user.getEmail())
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .role(user.getRole().name())
                .build();

        kafkaTemplate.send(KafkaTopics.USER_REGISTERED, String.valueOf(user.getId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish UserRegisteredEvent for userId={}", user.getId(), ex);
                    } else {
                        log.info("Published UserRegisteredEvent for userId={}, topic={}, offset={}",
                                user.getId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
