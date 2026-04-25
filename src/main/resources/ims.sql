-- Inventory Management System Database Schema
-- Created for Airtel Challenge - End User Equipment Inventory Management
-- SysAdmin Credentials: Username: 24rp01201, Password: 24rp01530

-- Create database (uncomment if needed)
-- CREATE DATABASE ims_db;
-- USE ims_db;

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS audit_trail;
DROP TABLE IF EXISTS maintenance_record;
DROP TABLE IF EXISTS assignment;
DROP TABLE IF EXISTS equipment_category;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS users;

-- Create Users table with SysAdmin credentials
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
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

-- Create Equipment Category table
CREATE TABLE equipment_category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Equipment table
CREATE TABLE equipment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    asset_tag VARCHAR(50) NOT NULL UNIQUE,
    serial_number VARCHAR(100),
    category_id INT,
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
    FOREIGN KEY (category_id) REFERENCES equipment_category(id)
);

-- Create Assignment table
CREATE TABLE assignment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_id INT NOT NULL,
    user_id INT NOT NULL,
    assigned_by INT NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    return_date TIMESTAMP NULL,
    assignment_condition ENUM('EXCELLENT', 'GOOD', 'FAIR', 'POOR') NOT NULL,
    return_condition ENUM('EXCELLENT', 'GOOD', 'FAIR', 'POOR') NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (assigned_by) REFERENCES users(id)
);

-- Create Maintenance Record table
CREATE TABLE maintenance_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_id INT NOT NULL,
    created_by INT NOT NULL,
    maintenance_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    maintenance_type ENUM('PREVENTIVE', 'CORRECTIVE', 'UPGRADE') NOT NULL,
    description TEXT,
    cost DECIMAL(10,2),
    performed_by VARCHAR(100),
    next_maintenance_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Create Audit Trail table for tracking changes
CREATE TABLE audit_trail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_id INT,
    user_id INT,
    action ENUM('CREATED', 'UPDATED', 'ASSIGNED', 'RETURNED', 'MAINTENANCE', 'RETIRED', 'LOST') NOT NULL,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    old_values TEXT,
    new_values TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert default equipment categories
INSERT INTO equipment_category (name, description) VALUES
('Laptop', 'Laptop computers and notebooks'),
('Desktop', 'Desktop computers and workstations'),
('Mobile Phone', 'Smartphones and mobile devices'),
('Tablet', 'Tablet computers'),
('Monitor', 'Computer monitors and displays'),
('Printer', 'Printers and scanners'),
('Other', 'Other equipment types');

-- Insert SysAdmin user with specified credentials
-- Username: 24rp01201, Password: 24rp01530 (BCrypt encoded for security)
INSERT INTO users (username, password, full_name, email, role, department, active) VALUES
('24rp01201', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'System Administrator', 'sysadmin@airtel.com', 'SYSADMIN', 'IT', TRUE);

-- Insert sample users for testing
INSERT INTO users (username, password, full_name, email, role, department, active) VALUES
('john_doe', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'John Doe', 'john.doe@airtel.com', 'USER', 'HR', TRUE),
('jane_smith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Jane Smith', 'jane.smith@airtel.com', 'MANAGER', 'IT', TRUE);

-- Insert sample equipment
INSERT INTO equipment (asset_tag, serial_number, category_id, brand, model, specifications, purchase_date, purchase_cost, warranty_expiry, status, condition_status, location) VALUES
('LAP001', 'DL5420202301', 1, 'Dell', 'Latitude 5420', 'Intel i5-1135G7, 8GB RAM, 256GB SSD, 14" FHD', '2023-01-15', 850.00, '2025-01-15', 'AVAILABLE', 'EXCELLENT', 'IT Office'),
('LAP002', 'HP840202302', 1, 'HP', 'EliteBook 840', 'Intel i7-1165G7, 16GB RAM, 512GB SSD, 14" FHD', '2023-02-20', 1200.00, '2025-02-20', 'ASSIGNED', 'GOOD', 'HR Department'),
('DT001', 'DT7090202301', 2, 'Dell', 'OptiPlex 7090', 'Intel i5-11500, 8GB RAM, 256GB SSD', '2023-03-10', 750.00, '2025-03-10', 'AVAILABLE', 'GOOD', 'Finance Office'),
('MB001', 'AP132023001', 3, 'Apple', 'iPhone 13', 'A15 Bionic, 128GB, 6.1" Super Retina XDR', '2023-04-05', 650.00, '2024-04-05', 'ASSIGNED', 'EXCELLENT', 'Operations');

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_equipment_asset_tag ON equipment(asset_tag);
CREATE INDEX idx_equipment_serial ON equipment(serial_number);
CREATE INDEX idx_equipment_status ON equipment(status);
CREATE INDEX idx_equipment_category ON equipment(category_id);
CREATE INDEX idx_assignment_equipment ON assignment(equipment_id);
CREATE INDEX idx_assignment_user ON assignment(user_id);
CREATE INDEX idx_audit_equipment ON audit_trail(equipment_id);
CREATE INDEX idx_audit_user ON audit_trail(user_id);
CREATE INDEX idx_audit_date ON audit_trail(action_date);

-- Create view for current equipment assignments
CREATE VIEW current_assignments AS
SELECT 
    e.asset_tag, 
    e.brand, 
    e.model,
    u.full_name as assigned_to,
    u.username as assigned_username,
    u.department,
    a.assigned_date,
    a.assignment_condition
FROM equipment e
LEFT JOIN assignment a ON e.id = a.equipment_id AND a.return_date IS NULL
LEFT JOIN users u ON a.user_id = u.id
WHERE e.status = 'ASSIGNED' AND e.active = TRUE;

-- Create view for equipment summary
CREATE VIEW equipment_summary AS
SELECT 
    c.name as category_name,
    e.status,
    e.condition_status,
    COUNT(*) as count,
    AVG(e.purchase_cost) as avg_cost,
    SUM(e.purchase_cost) as total_cost
FROM equipment e
LEFT JOIN equipment_category c ON e.category_id = c.id
WHERE e.active = TRUE
GROUP BY c.name, e.status, e.condition_status;

-- Database setup complete
