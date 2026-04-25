-- Inventory Management System Database Schema
-- Created for Airtel Challenge - End User Equipment Management

-- Create Database
CREATE DATABASE IF NOT EXISTS ims_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ims_db;

-- Users Table for Authentication
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('SYSADMIN', 'MANAGER', 'USER') NOT NULL,
    department VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

-- Departments Table
CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

-- Equipment Categories Table
CREATE TABLE equipment_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

-- Equipment/Assets Table
CREATE TABLE equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_tag VARCHAR(50) UNIQUE NOT NULL,
    serial_number VARCHAR(100),
    category_id BIGINT,
    brand VARCHAR(50),
    model VARCHAR(100),
    specifications TEXT,
    purchase_date DATE,
    purchase_cost DECIMAL(10,2),
    warranty_expiry DATE,
    status ENUM('AVAILABLE', 'ASSIGNED', 'UNDER_MAINTENANCE', 'RETIRED', 'LOST') DEFAULT 'AVAILABLE',
    condition_status ENUM('EXCELLENT', 'GOOD', 'FAIR', 'POOR') DEFAULT 'EXCELLENT',
    location VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
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
    status ENUM('ACTIVE', 'RETURNED', 'OVERDUE') DEFAULT 'ACTIVE',
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
    description TEXT NOT NULL,
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
    description TEXT,
    old_values TEXT,
    new_values TEXT,
    performed_by BIGINT NOT NULL,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (performed_by) REFERENCES users(id)
);

-- Insert Default Data

-- Insert Default Equipment Categories
INSERT INTO equipment_categories (name, description) VALUES
('Laptop', 'Portable computers for employees'),
('Desktop', 'Desktop computers for office use'),
('Mobile Phone', 'Company-issued mobile devices'),
('Tablet', 'Tablet computers for field work'),
('Monitor', 'External display monitors'),
('Printer', 'Office printing equipment'),
('Server', 'Server equipment and infrastructure');

-- Insert Default Departments
INSERT INTO departments (name, description) VALUES
('IT', 'Information Technology Department'),
('HR', 'Human Resources Department'),
('Finance', 'Finance and Accounting'),
('Operations', 'Operations Department'),
('Marketing', 'Marketing and Sales'),
('Administration', 'General Administration');

-- Insert SysAdmin User (Username: 24rp01201, Password: 24rp01530)
-- Password is BCrypt encoded for "24rp01530"
INSERT INTO users (username, password, full_name, email, role, department) VALUES
('24rp01201', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'HABINEZA Samuel', 'samuel.habineza@airtel.com', 'SYSADMIN', 'IT');

-- Insert Sample Manager User
INSERT INTO users (username, password, full_name, email, role, department) VALUES
('manager1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'MURINDA Noella Kayitesi', 'noella.murinda@airtel.com', 'MANAGER', 'IT');

-- Insert Sample Equipment
INSERT INTO equipment (asset_tag, serial_number, category_id, brand, model, specifications, purchase_date, purchase_cost, warranty_expiry, status, condition_status, location) VALUES
('LAP001', 'SN123456789', 1, 'Dell', 'Latitude 7420', 'Intel i7, 16GB RAM, 512GB SSD', '2024-01-15', 1200.00, '2025-01-15', 'AVAILABLE', 'EXCELLENT', 'IT Office'),
('LAP002', 'SN987654321', 1, 'HP', 'EliteBook 840', 'Intel i5, 8GB RAM, 256GB SSD', '2024-02-20', 950.00, '2025-02-20', 'ASSIGNED', 'GOOD', 'HR Department'),
('DT001', 'SN456789123', 2, 'Dell', 'OptiPlex 7090', 'Intel i7, 16GB RAM, 1TB SSD', '2024-01-10', 800.00, '2025-01-10', 'AVAILABLE', 'EXCELLENT', 'IT Office'),
('MOB001', 'SN789123456', 3, 'Samsung', 'Galaxy S23', '256GB Storage, 5G Enabled', '2024-03-01', 750.00, '2025-03-01', 'ASSIGNED', 'GOOD', 'Operations');

-- Create Indexes for Better Performance
CREATE INDEX idx_equipment_asset_tag ON equipment(asset_tag);
CREATE INDEX idx_equipment_serial_number ON equipment(serial_number);
CREATE INDEX idx_equipment_status ON equipment(status);
CREATE INDEX idx_assignments_equipment_id ON assignments(equipment_id);
CREATE INDEX idx_assignments_user_id ON assignments(user_id);
CREATE INDEX idx_assignments_status ON assignments(status);
CREATE INDEX idx_audit_trail_equipment_id ON audit_trail(equipment_id);
CREATE INDEX idx_audit_trail_performed_at ON audit_trail(performed_at);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);

-- Create Views for Reporting

-- Equipment Status Summary View
CREATE VIEW equipment_status_summary AS
SELECT 
    ec.name as category_name,
    e.status,
    COUNT(*) as count,
    SUM(e.purchase_cost) as total_value
FROM equipment e
JOIN equipment_categories ec ON e.category_id = ec.id
WHERE e.active = TRUE
GROUP BY ec.name, e.status;

-- User Assignment Summary View
CREATE VIEW user_assignment_summary AS
SELECT 
    u.username,
    u.full_name,
    d.name as department,
    COUNT(a.id) as assigned_items,
    COUNT(CASE WHEN a.status = 'OVERDUE' THEN 1 END) as overdue_items
FROM users u
LEFT JOIN assignments a ON u.id = a.user_id AND a.status = 'ACTIVE'
LEFT JOIN departments d ON u.department = d.name
WHERE u.active = TRUE
GROUP BY u.id, u.username, u.full_name, d.name;

-- Maintenance Summary View
CREATE VIEW maintenance_summary AS
SELECT 
    e.asset_tag,
    e.brand,
    e.model,
    ec.name as category,
    COUNT(mr.id) as maintenance_count,
    SUM(mr.cost) as total_maintenance_cost,
    MAX(mr.performed_date) as last_maintenance_date
FROM equipment e
JOIN equipment_categories ec ON e.category_id = ec.id
LEFT JOIN maintenance_records mr ON e.id = mr.equipment_id
WHERE e.active = TRUE
GROUP BY e.id, e.asset_tag, e.brand, e.model, ec.name;
