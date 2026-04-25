-- ================================================================
-- Inventory Management System Database Schema
-- Created for Airtel Challenge - End User Equipment Inventory Management
-- SysAdmin Credentials: Username: 24rp01201, Password: 24rp01530
-- ================================================================

-- Create database
CREATE DATABASE IF NOT EXISTS ims_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE ims_db;

-- ================================================================
-- DROP EXISTING TABLES AND VIEWS (Reverse dependency order)
-- ================================================================

DROP VIEW IF EXISTS current_assignments;
DROP VIEW IF EXISTS equipment_summary;
DROP VIEW IF EXISTS maintenance_summary;
DROP VIEW IF EXISTS user_assignment_summary;
DROP VIEW IF EXISTS equipment_status_summary;

DROP TABLE IF EXISTS audit_trail;
DROP TABLE IF EXISTS maintenance_records;
DROP TABLE IF EXISTS assignments;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS equipment_categories;
DROP TABLE IF EXISTS users;

-- ================================================================
-- TABLE STRUCTURES
-- ================================================================

-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    role ENUM('SYSADMIN', 'MANAGER', 'USER') NOT NULL DEFAULT 'USER',
    department VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Equipment Categories Table
CREATE TABLE equipment_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Equipment Table
CREATE TABLE equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_tag VARCHAR(50) NOT NULL UNIQUE,
    serial_number VARCHAR(100),
    category_id BIGINT,
    brand VARCHAR(50),
    model VARCHAR(100),
    specifications TEXT,
    purchase_date DATE,
    purchase_cost DECIMAL(10,2),
    warranty_expiry DATE,
    status ENUM('AVAILABLE', 'ASSIGNED', 'UNDER_MAINTENANCE', 'RETIRED', 'LOST') NOT NULL DEFAULT 'AVAILABLE',
    condition_status ENUM('EXCELLENT', 'GOOD', 'FAIR', 'POOR') NOT NULL DEFAULT 'EXCELLENT',
    location VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES equipment_categories(id)
);

-- Assignments Table
CREATE TABLE assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    assigned_by BIGINT NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expected_return_date DATE,
    actual_return_date TIMESTAMP NULL,
    return_condition ENUM('EXCELLENT', 'GOOD', 'FAIR', 'POOR'),
    return_notes TEXT,
    status ENUM('ACTIVE', 'RETURNED', 'OVERDUE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (assigned_by) REFERENCES users(id)
);

-- Maintenance Records Table
CREATE TABLE maintenance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id BIGINT NOT NULL,
    maintenance_type ENUM('PREVENTIVE', 'CORRECTIVE', 'UPGRADE') NOT NULL,
    description TEXT,
    cost DECIMAL(10,2),
    performed_by VARCHAR(100),
    performed_date DATE,
    next_maintenance_date DATE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Audit Trail Table
CREATE TABLE audit_trail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id BIGINT,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    old_values TEXT,
    new_values TEXT,
    performed_by BIGINT,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ================================================================
-- DATA INITIALIZATION
-- ================================================================

-- Categories
INSERT INTO equipment_categories (name, description) VALUES
('Laptop', 'Laptop computers and notebooks'),
('Desktop', 'Desktop computers and workstations'),
('Mobile Phone', 'Smartphones and mobile devices'),
('Tablet', 'Tablet computers'),
('Monitor', 'Computer monitors and displays'),
('Printer', 'Printers and scanners'),
('Other', 'Other equipment types');

-- Users
-- SysAdmin (24rp01201 / 24rp01530)
INSERT INTO users (username, password, full_name, email, role, department, active) VALUES
('24rp01201', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'System Administrator', 'sysadmin@airtel.com', 'SYSADMIN', 'IT', TRUE),
('john_doe', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'John Doe', 'john.doe@airtel.com', 'USER', 'HR', TRUE),
('jane_smith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Jane Smith', 'jane.smith@airtel.com', 'MANAGER', 'IT', TRUE);

-- Equipment
INSERT INTO equipment (asset_tag, serial_number, category_id, brand, model, specifications, purchase_date, purchase_cost, warranty_expiry, status, condition_status, location) VALUES
('LAP001', 'DL5420202301', 1, 'Dell', 'Latitude 5420', 'Intel i5-1135G7, 8GB RAM, 256GB SSD, 14" FHD', '2023-01-15', 850.00, '2025-01-15', 'AVAILABLE', 'EXCELLENT', 'IT Office'),
('LAP002', 'HP840202302', 1, 'HP', 'EliteBook 840', 'Intel i7-1165G7, 16GB RAM, 512GB SSD, 14" FHD', '2023-02-20', 1200.00, '2025-02-20', 'ASSIGNED', 'GOOD', 'HR Department'),
('DT001', 'DT7090202301', 2, 'Dell', 'OptiPlex 7090', 'Intel i5-11500, 8GB RAM, 256GB SSD', '2023-03-10', 750.00, '2025-03-10', 'AVAILABLE', 'GOOD', 'Finance Office'),
('MB001', 'AP132023001', 3, 'Apple', 'iPhone 13', 'A15 Bionic, 128GB, 6.1" Super Retina XDR', '2023-04-05', 650.00, '2024-04-05', 'ASSIGNED', 'EXCELLENT', 'Operations');

-- Sample Assignments
INSERT INTO assignments (equipment_id, user_id, assigned_by, assigned_date, status) VALUES
(2, 2, 1, CURRENT_TIMESTAMP, 'ACTIVE'),
(4, 3, 1, CURRENT_TIMESTAMP, 'ACTIVE');

-- ================================================================
-- INDEXES AND VIEWS
-- ================================================================

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_equipment_asset_tag ON equipment(asset_tag);
CREATE INDEX idx_assignments_status ON assignments(status);

-- Current Assignments View
CREATE VIEW current_assignments AS
SELECT 
    e.asset_tag, 
    e.brand, 
    e.model,
    u.full_name as assigned_to,
    u.department,
    a.assigned_date,
    a.status as assignment_status
FROM equipment e
JOIN assignments a ON e.id = a.equipment_id AND a.actual_return_date IS NULL
JOIN users u ON a.user_id = u.id
WHERE e.status = 'ASSIGNED' AND e.active = TRUE;

-- Equipment Summary View
CREATE VIEW equipment_summary AS
SELECT 
    c.name as category_name,
    e.status,
    COUNT(*) as count,
    SUM(e.purchase_cost) as total_value
FROM equipment e
JOIN equipment_categories c ON e.category_id = c.id
WHERE e.active = TRUE
GROUP BY c.name, e.status;

-- ================================================================
-- END OF FILE
-- ================================================================
