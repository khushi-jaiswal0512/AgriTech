package com.agritech.common.config;

/**
 * Template for Kafka Producer/Consumer Configuration.
 * 
 * Future services should implement their own @Configuration using this pattern
 * to ensure standardized serialization of the {@link com.agritech.common.event.KafkaEvent}.
 *
 * <pre>
 * {@code
 * @Configuration
 * public class KafkaConfig {
 *     @Bean
 *     public ProducerFactory<String, Object> producerFactory() {
 *         Map<String, Object> config = new HashMap<>();
 *         config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
 *         config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
 *         config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
 *         return new DefaultKafkaProducerFactory<>(config);
 *     }
 *
 *     @Bean
 *     public ConsumerFactory<String, Object> consumerFactory() {
 *         Map<String, Object> config = new HashMap<>();
 *         config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
 *         config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
 *         config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
 *         config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.agritech.*");
 *         return new DefaultKafkaConsumerFactory<>(config);
 *     }
 * }
 * }
 * </pre>
 */
public abstract class KafkaConfigTemplate {
    // Template placeholder
}
