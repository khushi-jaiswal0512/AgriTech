CREATE TABLE IF NOT EXISTS ratings (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    stars           TINYINT NOT NULL CHECK (stars >= 1 AND stars <= 5),
    review          TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_product_user (product_id, user_id),
    INDEX idx_product_id (product_id)
);
