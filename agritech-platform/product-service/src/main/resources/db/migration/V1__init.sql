-- Product Service Initial Schema
-- V1: categories, products, product_images, inventory_log

CREATE TABLE IF NOT EXISTS categories (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     TEXT NULL,
    image_url       VARCHAR(500) NULL,
    parent_id       BIGINT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order      INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      VARCHAR(255) NULL,
    updated_by      VARCHAR(255) NULL,
    CONSTRAINT uk_categories_name UNIQUE (name),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_parent_id (parent_id),
    INDEX idx_name (name)
);

CREATE TABLE IF NOT EXISTS products (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    farmer_id           BIGINT NOT NULL,
    category_id         BIGINT NOT NULL,
    name                VARCHAR(200) NOT NULL,
    description         TEXT NULL,
    price               DECIMAL(10,2) NOT NULL,
    available_quantity  INT NOT NULL DEFAULT 0 CHECK (available_quantity >= 0),
    unit                ENUM('KG','GRAM','LITRE','PIECE','DOZEN','QUINTAL','TON') NOT NULL,
    is_organic          BOOLEAN NOT NULL DEFAULT FALSE,
    harvest_date        DATE NULL,
    avg_rating          DECIMAL(3,1) NOT NULL DEFAULT 0.0,
    total_ratings       INT NOT NULL DEFAULT 0,
    product_status      ENUM('AVAILABLE','OUT_OF_STOCK','DISCONTINUED') NOT NULL DEFAULT 'AVAILABLE',
    approval_status     ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    rejection_reason    TEXT NULL,
    city                VARCHAR(100) NULL,
    state               VARCHAR(100) NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          VARCHAR(255) NULL,
    updated_by          VARCHAR(255) NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_farmer_id (farmer_id),
    INDEX idx_category_id (category_id),
    INDEX idx_approval_status (approval_status),
    INDEX idx_product_status (product_status),
    INDEX idx_is_organic (is_organic),
    INDEX idx_price (price),
    INDEX idx_city (city),
    INDEX idx_state (state),
    INDEX idx_avg_rating (avg_rating),
    FULLTEXT INDEX idx_search (name, description)
);

CREATE TABLE IF NOT EXISTS product_images (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id  BIGINT NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    s3_key      VARCHAR(500) NOT NULL,
    sort_order  INT NOT NULL DEFAULT 0,
    is_primary  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id)
);

CREATE TABLE IF NOT EXISTS inventory_log (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT NOT NULL,
    change_type     ENUM('STOCK_IN','STOCK_OUT','ADJUSTMENT') NOT NULL,
    quantity_change INT NOT NULL,
    previous_qty    INT NOT NULL,
    new_qty         INT NOT NULL,
    reason          VARCHAR(255) NULL,
    reference_id    VARCHAR(100) NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_inv_product_id (product_id),
    INDEX idx_inv_created_at (created_at)
);

-- Seed default categories
INSERT IGNORE INTO categories (name, description, sort_order) VALUES
    ('Vegetables', 'Fresh farm vegetables', 1),
    ('Fruits', 'Seasonal and exotic fruits', 2),
    ('Grains & Pulses', 'Rice, wheat, lentils and legumes', 3),
    ('Dairy & Eggs', 'Fresh milk, cheese, butter and eggs', 4),
    ('Spices & Herbs', 'Dry and fresh spices and herbs', 5),
    ('Organic Products', 'Certified organic produce', 6),
    ('Flowers & Plants', 'Decorative and medicinal plants', 7),
    ('Processed Foods', 'Pickles, jams, and value-added products', 8);
