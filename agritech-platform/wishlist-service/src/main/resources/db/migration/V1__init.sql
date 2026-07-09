-- V1: wishlists

CREATE TABLE IF NOT EXISTS wishlists (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    product_id      BIGINT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_product UNIQUE (user_id, product_id),
    INDEX idx_user_id (user_id)
);
