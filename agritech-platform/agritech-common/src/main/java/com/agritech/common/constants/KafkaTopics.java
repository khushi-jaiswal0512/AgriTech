package com.agritech.common.constants;

/**
 * Centralized Kafka topic name constants.
 * All topics follow the {@code agritech.{domain}.{event}} naming convention.
 *
 * <p>These constants must be used by all producers and consumers
 * to avoid topic name mismatches.</p>
 */
public final class KafkaTopics {

    private KafkaTopics() {
        // Prevent instantiation
    }

    // ── User Events ──
    public static final String USER_REGISTERED = "agritech.user.registered";

    // ── Farmer Events ──
    public static final String FARMER_VERIFIED = "agritech.farmer.verified";

    // ── Product Events ──
    public static final String PRODUCT_CREATED = "agritech.product.created";
    public static final String PRODUCT_APPROVED = "agritech.product.approved";
    public static final String PRODUCT_REJECTED = "agritech.product.rejected";

    // ── Order Events ──
    public static final String ORDER_PLACED = "agritech.order.placed";
    public static final String ORDER_ACCEPTED = "agritech.order.accepted";
    public static final String ORDER_CANCELLED = "agritech.order.cancelled";
    public static final String ORDER_DELIVERED = "agritech.order.delivered";

    // ── Rating Events ──
    public static final String RATING_SUBMITTED = "agritech.rating.submitted";

    // ── Stock Events ──
    public static final String STOCK_DEPLETED = "agritech.stock.depleted";
}
