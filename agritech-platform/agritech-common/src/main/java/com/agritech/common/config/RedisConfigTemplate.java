package com.agritech.common.config;

/**
 * Template for Redis Configuration.
 * 
 * Future services should implement their own @Configuration using this pattern
 * to ensure standardized serialization.
 *
 * <pre>
 * {@code
 * @Configuration
 * public class RedisConfig {
 *     @Bean
 *     public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
 *         RedisTemplate<String, Object> template = new RedisTemplate<>();
 *         template.setConnectionFactory(connectionFactory);
 *         template.setKeySerializer(new StringRedisSerializer());
 *         template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
 *         template.setHashKeySerializer(new StringRedisSerializer());
 *         template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
 *         template.afterPropertiesSet();
 *         return template;
 *     }
 * }
 * }
 * </pre>
 */
public abstract class RedisConfigTemplate {
    // Template placeholder
}
