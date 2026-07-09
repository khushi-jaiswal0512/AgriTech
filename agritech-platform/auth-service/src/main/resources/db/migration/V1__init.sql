-- ============================================================
-- Auth Service — Initial Database Schema
-- V1: Create users and refresh_tokens tables
-- Author : Agritech Platform Team
-- Created: 2026-07-01
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    email           VARCHAR(255)    NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    role            ENUM(
                        'ROLE_FARMER',
                        'ROLE_CONSUMER',
                        'ROLE_RETAILER',
                        'ROLE_ADMIN'
                    )               NOT NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    is_verified     BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      VARCHAR(255)    NULL,
    updated_by      VARCHAR(255)    NULL,

    CONSTRAINT pk_users         PRIMARY KEY (id),
    CONSTRAINT uk_users_email   UNIQUE      (email),
    INDEX idx_email             (email),
    INDEX idx_role              (role)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL,
    token           VARCHAR(512)    NOT NULL,
    expiry_date     TIMESTAMP       NOT NULL,
    revoked         BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_refresh_tokens            PRIMARY KEY  (id),
    CONSTRAINT uk_refresh_tokens_token      UNIQUE       (token),
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES  users(id)
        ON DELETE   CASCADE
        ON UPDATE   RESTRICT,
    INDEX idx_refresh_token     (token),
    INDEX idx_refresh_user_id   (user_id)
);
