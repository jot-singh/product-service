-- Create the base table structure for V0 fields
CREATE TABLE `categories` (
    `id` BINARY(36) PRIMARY KEY,
    `name` VARCHAR(255),
    `description` TEXT,
    `modified_by` VARCHAR(255),
    `modified_on` DATETIME,
    `created_by` VARCHAR(255),
    `created_on` DATETIME,
    `is_deleted` BOOLEAN
);

CREATE TABLE `prices` (
    `id` BINARY(36) PRIMARY KEY,
    `currency` VARCHAR(10),
    `price` DOUBLE,
    `modified_by` VARCHAR(255),
    `modified_on` DATETIME,
    `created_by` VARCHAR(255),
    `created_on` DATETIME,
    `is_deleted` BOOLEAN
);

CREATE TABLE `products` (
    `id` BINARY(36) PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `title` VARCHAR(255),
    `description` TEXT,
    `image` VARCHAR(255),
    `category_id` BINARY(36),
    `price_id` BINARY(36),
    `modified_by` VARCHAR(255),
    `modified_on` DATETIME,
    `created_by` VARCHAR(255),
    `created_on` DATETIME,
    `is_deleted` BOOLEAN,
    FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`),
    FOREIGN KEY (`price_id`) REFERENCES `prices`(`id`)
);

CREATE TABLE `orders` (
    `id` BINARY(36) PRIMARY KEY,
    `modified_by` VARCHAR(255),
    `modified_on` DATETIME,
    `created_by` VARCHAR(255),
    `created_on` DATETIME,
    `is_deleted` BOOLEAN
);

-- Junction table for many-to-many relationship between products and orders
CREATE TABLE `product_orders` (
    `order_id` BINARY(36),
    `product_id` BINARY(36),
    PRIMARY KEY (`order_id`, `product_id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
);

-- Add indexes for better performance
CREATE INDEX idx_category_name ON categories(name);
CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_price_currency ON prices(currency); 