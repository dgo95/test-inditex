DROP TABLE IF EXISTS prices;
DROP INDEX IF EXISTS idx_prices_product_brand_dates_priority;

CREATE TABLE prices (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        brand_id BIGINT NOT NULL,
                        start_date TIMESTAMP NOT NULL,
                        end_date TIMESTAMP NOT NULL,
                        price_list INTEGER NOT NULL,
                        product_id BIGINT NOT NULL,
                        priority INTEGER NOT NULL,
                        price DECIMAL(10, 2) NOT NULL,
                        curr VARCHAR(3) NOT NULL
);

CREATE INDEX idx_prices_product_brand_dates_priority
    ON prices (product_id, brand_id, start_date, end_date, priority DESC);