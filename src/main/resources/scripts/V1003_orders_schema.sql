-- V1003: Enhanced Order Management Schema
-- This migration updates the order-related tables with comprehensive order management functionality

-- Update orders table with full order management fields
ALTER TABLE `orders` ADD COLUMN `order_number` VARCHAR(50) UNIQUE NOT NULL AFTER `id`;
ALTER TABLE `orders` ADD COLUMN `customer_id` VARCHAR(255) AFTER `order_number`;
ALTER TABLE `orders` ADD COLUMN `status` ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'PENDING' AFTER `customer_id`;
ALTER TABLE `orders` ADD COLUMN `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER `status`;
ALTER TABLE `orders` ADD COLUMN `currency` VARCHAR(3) DEFAULT 'USD' AFTER `total_amount`;
ALTER TABLE `orders` ADD COLUMN `shipping_address` TEXT AFTER `currency`;
ALTER TABLE `orders` ADD COLUMN `billing_address` TEXT AFTER `shipping_address`;
ALTER TABLE `orders` ADD COLUMN `order_date` DATETIME AFTER `billing_address`;
ALTER TABLE `orders` ADD COLUMN `shipped_date` DATETIME AFTER `order_date`;
ALTER TABLE `orders` ADD COLUMN `delivered_date` DATETIME AFTER `shipped_date`;

-- Create order_items table
CREATE TABLE `order_items` (
    `id` BINARY(36) PRIMARY KEY,
    `order_id` BINARY(36) NOT NULL,
    `product_id` BINARY(36) NOT NULL,
    `quantity` INT NOT NULL,
    `unit_price` DECIMAL(10,2) NOT NULL,
    `total_price` DECIMAL(10,2) NOT NULL,
    `created_on` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `modified_by` VARCHAR(255),
    `modified_on` DATETIME,
    `created_by` VARCHAR(255),
    `is_deleted` BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
);

-- Create order_status_history table
CREATE TABLE `order_status_history` (
    `id` BINARY(36) PRIMARY KEY,
    `order_id` BINARY(36) NOT NULL,
    `old_status` VARCHAR(20),
    `new_status` VARCHAR(20) NOT NULL,
    `changed_by` VARCHAR(255),
    `changed_on` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `notes` TEXT,
    `created_by` VARCHAR(255),
    `created_on` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `modified_by` VARCHAR(255),
    `modified_on` DATETIME,
    `is_deleted` BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX `idx_orders_customer_id` ON `orders` (`customer_id`);
CREATE INDEX `idx_orders_status` ON `orders` (`status`);
CREATE INDEX `idx_orders_order_number` ON `orders` (`order_number`);
CREATE INDEX `idx_orders_order_date` ON `orders` (`order_date`);

CREATE INDEX `idx_order_items_order_id` ON `order_items` (`order_id`);
CREATE INDEX `idx_order_items_product_id` ON `order_items` (`product_id`);

CREATE INDEX `idx_order_status_history_order_id` ON `order_status_history` (`order_id`);
CREATE INDEX `idx_order_status_history_changed_on` ON `order_status_history` (`changed_on`);

-- Insert sample data for testing
INSERT INTO `orders` (`id`, `order_number`, `customer_id`, `status`, `total_amount`, `currency`, `order_date`, `created_by`, `created_on`)
VALUES
    (UUID_TO_BIN(UUID()), 'ORD-001', 'user-123', 'PENDING', 299.99, 'USD', NOW(), 'system', NOW()),
    (UUID_TO_BIN(UUID()), 'ORD-002', 'user-456', 'CONFIRMED', 149.50, 'USD', NOW(), 'system', NOW()),
    (UUID_TO_BIN(UUID()), 'ORD-003', 'user-123', 'DELIVERED', 79.99, 'USD', DATE_SUB(NOW(), INTERVAL 2 DAY), 'system', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Insert sample order items
INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `quantity`, `unit_price`, `total_price`, `created_by`)
SELECT
    UUID_TO_BIN(UUID()),
    o.id,
    p.id,
    1,
    pr.price,
    pr.price,
    'system'
FROM `orders` o
CROSS JOIN `products` p
JOIN `prices` pr ON p.price_id = pr.id
WHERE o.order_number = 'ORD-001'
LIMIT 2;

-- Insert sample order status history
INSERT INTO `order_status_history` (`id`, `order_id`, `old_status`, `new_status`, `changed_by`, `notes`, `created_by`)
SELECT
    UUID_TO_BIN(UUID()),
    o.id,
    NULL,
    'PENDING',
    'system',
    'Order created',
    'system'
FROM `orders` o
WHERE o.order_number = 'ORD-001';
