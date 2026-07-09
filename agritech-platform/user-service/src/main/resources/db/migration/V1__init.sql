-- ============================================================
-- User Service Initial Schema
-- Migration: V1__init.sql
-- Tables: user_profiles, user_addresses, farmer_details, retailer_details
-- ============================================================

CREATE TABLE IF NOT EXISTS user_profiles (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    auth_user_id        BIGINT          NOT NULL,
    first_name          VARCHAR(100)    NOT NULL,
    last_name           VARCHAR(100)    NOT NULL,
    phone               VARCHAR(20)     NULL,
    profile_image_url   VARCHAR(500)    NULL,
    role                ENUM('ROLE_FARMER','ROLE_CONSUMER','ROLE_RETAILER','ROLE_ADMIN') NOT NULL,
    account_status      ENUM('ACTIVE','INACTIVE','SUSPENDED','BANNED')                   NOT NULL DEFAULT 'ACTIVE',
    verification_status ENUM('PENDING','APPROVED','REJECTED')                            NOT NULL DEFAULT 'PENDING',
    verification_note   TEXT            NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          VARCHAR(255)    NULL,
    updated_by          VARCHAR(255)    NULL,
    CONSTRAINT uk_user_profiles_auth_user_id UNIQUE (auth_user_id),
    INDEX idx_auth_user_id      (auth_user_id),
    INDEX idx_role              (role),
    INDEX idx_verification_status (verification_status)
);

CREATE TABLE IF NOT EXISTS user_addresses (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    user_profile_id BIGINT          NOT NULL,
    label           VARCHAR(50)     NULL,
    address_line1   VARCHAR(255)    NOT NULL,
    address_line2   VARCHAR(255)    NULL,
    city            VARCHAR(100)    NOT NULL,
    state           VARCHAR(100)    NOT NULL,
    pincode         VARCHAR(10)     NOT NULL,
    country         VARCHAR(50)     NOT NULL DEFAULT 'India',
    is_default      BOOLEAN         NOT NULL DEFAULT FALSE,
    latitude        DECIMAL(10, 8)  NULL,
    longitude       DECIMAL(11, 8)  NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      VARCHAR(255)    NULL,
    updated_by      VARCHAR(255)    NULL,
    CONSTRAINT fk_addresses_profile
        FOREIGN KEY (user_profile_id) REFERENCES user_profiles (id) ON DELETE CASCADE,
    INDEX idx_user_profile_id (user_profile_id),
    INDEX idx_city            (city),
    INDEX idx_state           (state)
);

CREATE TABLE IF NOT EXISTS farmer_details (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    user_profile_id     BIGINT          NOT NULL,
    farm_name           VARCHAR(200)    NULL,
    farm_size_acres     DECIMAL(10, 2)  NULL,
    farming_type        ENUM('ORGANIC','CONVENTIONAL','MIXED') NULL DEFAULT 'CONVENTIONAL',
    experience_years    INT             NULL,
    government_id       VARCHAR(100)    NULL,
    government_id_type  ENUM('AADHAAR','PAN','VOTER_ID','KISAN_ID') NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          VARCHAR(255)    NULL,
    updated_by          VARCHAR(255)    NULL,
    CONSTRAINT uk_farmer_details_profile UNIQUE (user_profile_id),
    CONSTRAINT fk_farmer_profile
        FOREIGN KEY (user_profile_id) REFERENCES user_profiles (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS retailer_details (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    user_profile_id BIGINT          NOT NULL,
    business_name   VARCHAR(200)    NOT NULL,
    gst_number      VARCHAR(20)     NULL,
    business_type   ENUM('GROCERY','WHOLESALE','ORGANIC_STORE','RESTAURANT','OTHER') NULL,
    license_number  VARCHAR(100)    NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      VARCHAR(255)    NULL,
    updated_by      VARCHAR(255)    NULL,
    CONSTRAINT uk_retailer_details_profile UNIQUE (user_profile_id),
    CONSTRAINT fk_retailer_profile
        FOREIGN KEY (user_profile_id) REFERENCES user_profiles (id) ON DELETE CASCADE
);
