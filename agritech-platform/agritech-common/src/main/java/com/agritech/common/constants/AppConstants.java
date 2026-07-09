package com.agritech.common.constants;

/**
 * Application-wide constants shared across all microservices.
 */
public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // ── Pagination Defaults ──
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIR = "desc";
    public static final int MAX_PAGE_SIZE = 50;

    // ── Security & JWT ──
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_EXPIRY = 30 * 60 * 1000L; // 30 minutes
    public static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000L; // 7 days

    // ── Headers & Logging ──
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_USER_EMAIL = "X-User-Email";

    // ── Product Image Constraints ──
    public static final int MIN_PRODUCT_IMAGES = 1;
    public static final int MAX_PRODUCT_IMAGES = 5;
    public static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024; // 5MB

    // ── Rating Constraints ──
    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 5;
}
