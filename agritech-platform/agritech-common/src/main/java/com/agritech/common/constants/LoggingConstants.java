package com.agritech.common.constants;

/**
 * Shared logging constants and guidelines for all microservices.
 * 
 * <p><b>Logging Guidelines:</b></p>
 * <ul>
 *   <li>NEVER use {@code System.out.println()} in production code.</li>
 *   <li>Use {@code log.info()} for significant lifecycle events (e.g., service started, order placed).</li>
 *   <li>Use {@code log.warn()} for recoverable errors, invalid inputs, or suspicious activity.</li>
 *   <li>Use {@code log.error()} for system failures, unhandled exceptions, and critical issues.</li>
 *   <li>Use {@code log.debug()} for detailed diagnostic information (disabled in PROD).</li>
 * </ul>
 */
public final class LoggingConstants {

    private LoggingConstants() {
        // Prevent instantiation
    }

    public static final String LOG_LEVEL_INFO = "INFO";
    public static final String LOG_LEVEL_WARN = "WARN";
    public static final String LOG_LEVEL_ERROR = "ERROR";
    public static final String LOG_LEVEL_DEBUG = "DEBUG";

    public static final String MSG_SERVICE_STARTED = "Service started successfully";
    public static final String MSG_DB_CONNECTED = "Database connection established";
    public static final String MSG_KAFKA_PUBLISHED = "Message published to Kafka topic: {}";
    public static final String MSG_VALIDATION_FAILED = "Validation failed for request";
}
