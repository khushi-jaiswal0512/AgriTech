CREATE TABLE IF NOT EXISTS analytics_daily (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    snapshot_date       DATE NOT NULL UNIQUE,
    total_users         INT DEFAULT 0,
    total_farmers       INT DEFAULT 0,
    total_consumers     INT DEFAULT 0,
    total_retailers     INT DEFAULT 0,
    total_products      INT DEFAULT 0,
    total_orders        INT DEFAULT 0,
    total_revenue       DECIMAL(15, 2) DEFAULT 0.00,
    orders_today        INT DEFAULT 0,
    revenue_today       DECIMAL(15, 2) DEFAULT 0.00,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_snapshot_date (snapshot_date)
);

CREATE TABLE IF NOT EXISTS top_products (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT NOT NULL,
    product_name    VARCHAR(200),
    total_sold      INT DEFAULT 0,
    total_revenue   DECIMAL(12, 2) DEFAULT 0.00,
    period          VARCHAR(20) NOT NULL, -- 'DAILY', 'WEEKLY', 'MONTHLY'
    snapshot_date   DATE NOT NULL,
    INDEX idx_period_date (period, snapshot_date)
);
