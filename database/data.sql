-- Create the customer management database with UTF-8 character set for multilingual support
CREATE DATABASE IF NOT EXISTS customer_mgmt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- Create a dedicated database user for the application with limited privileges
CREATE USER IF NOT EXISTS 'cms_user' @'localhost' IDENTIFIED BY 'cms_pass123';
-- Grant all privileges on the customer_mgmt database to the cms_user account
GRANT ALL PRIVILEGES ON customer_mgmt.* TO 'cms_user' @'localhost';
-- Reload privilege tables to apply changes immediately
FLUSH PRIVILEGES;