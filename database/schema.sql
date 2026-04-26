-- Select the customer management database for all subsequent queries
USE customer_mgmt;
-- Table to store country master data referenced by customers and addresses
CREATE TABLE IF NOT EXISTS countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
-- Table to store city master data with foreign key relationship to countries
CREATE TABLE IF NOT EXISTS cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country_id BIGINT NOT NULL,
    FOREIGN KEY (country_id) REFERENCES countries(id)
);
-- Core customers table storing customer information with unique NIC number and audit timestamps
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    nic_number VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
-- Junction table to store multiple phone numbers for each customer with cascade delete
CREATE TABLE IF NOT EXISTS customer_phones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);
-- Table to store customer address information with foreign keys to cities and countries
CREATE TABLE IF NOT EXISTS customer_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city_id BIGINT,
    country_id BIGINT,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (city_id) REFERENCES cities(id),
    FOREIGN KEY (country_id) REFERENCES countries(id)
);
-- Junction table for many-to-many relationships between customers representing family connections
CREATE TABLE IF NOT EXISTS family_members (
    customer_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    PRIMARY KEY (customer_id, member_id),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES customers(id) ON DELETE CASCADE
);
-- Table to track bulk customer upload jobs with status tracking and row counts
CREATE TABLE IF NOT EXISTS bulk_upload_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255),
    status ENUM('PENDING', 'PROCESSING', 'DONE', 'FAILED') DEFAULT 'PENDING',
    total_rows INT DEFAULT 0,
    processed_rows INT DEFAULT 0,
    failed_rows INT DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);