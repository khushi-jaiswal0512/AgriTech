package com.agritech.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Generic Kafka event wrapper used for inter-service communication.
 * All Kafka messages should be wrapped in this envelope for consistency.
 *
 * <p>Topics follow the {@code agritech.{domain}.{event}} naming convention.</p>
 *
 * @param <T> the type of payload contained in the event
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaEvent<T> {

    /** Unique event identifier for idempotency and tracing */
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    /** The Kafka topic this event was published to */
    private String topic;

    /** The event payload */
    private T payload;

    /** Timestamp when the event was created */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /** The service that produced this event */
    private String source;

    /**
     * Factory method to create a KafkaEvent.
     *
     * @param topic   the Kafka topic name
     * @param payload the event payload
     * @param source  the producing service name
     * @param <T>     the payload type
     * @return a new KafkaEvent instance
     */
    public static <T> KafkaEvent<T> of(String topic, T payload, String source) {
        return KafkaEvent.<T>builder()
                .topic(topic)
                .payload(payload)
                .source(source)
                .build();
    }
}
