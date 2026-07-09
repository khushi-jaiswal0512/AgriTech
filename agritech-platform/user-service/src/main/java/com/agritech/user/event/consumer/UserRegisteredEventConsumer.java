package com.agritech.user.event.consumer;

import com.agritech.common.constants.KafkaTopics;
import com.agritech.common.constants.UserRole;
import com.agritech.common.event.UserRegisteredEvent;
import com.agritech.user.entity.UserProfile;
import com.agritech.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka consumer for {@code user.registered} events.
 * Idempotently creates a {@link UserProfile} when the Auth Service publishes
 * a {@link UserRegisteredEvent} upon successful user registration.
 *
 * <p>Idempotency is guaranteed by checking {@code existsByAuthUserId} before
 * persisting, which prevents duplicate profiles on message redelivery.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEventConsumer {

    private final UserProfileRepository profileRepository;

    /**
     * Handle a {@link UserRegisteredEvent} message from Kafka.
     * Creates a new {@link UserProfile} if one does not already exist for the given authUserId.
     *
     * @param event the deserialized user registration event payload
     */
    @KafkaListener(
            topics = KafkaTopics.USER_REGISTERED,
            groupId = "user-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for authUserId={}, role={}", event.getAuthUserId(), event.getRole());

        if (profileRepository.existsByAuthUserId(event.getAuthUserId())) {
            log.warn("UserProfile already exists for authUserId={}, skipping duplicate creation", event.getAuthUserId());
            return;
        }

        UserProfile profile = UserProfile.builder()
                .authUserId(event.getAuthUserId())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .phone(event.getPhone())
                .role(UserRole.valueOf(event.getRole()))
                .build();

        profileRepository.save(profile);
        log.info("Successfully created UserProfile for authUserId={}", event.getAuthUserId());
    }
}
