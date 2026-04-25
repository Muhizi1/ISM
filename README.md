# Inventory Management System (IMS)

**Airtel Challenge - End User Equipment Inventory Management**

A comprehensive Spring Boot MVC application for tracking and managing end-user devices (laptops, desktops, and mobile phones) with offline capabilities, accurate asset ownership tracking, and comprehensive reporting.

## 🎯 Project Overview

This Inventory Management System addresses Airtel's challenge of lacking a centralized system to accurately track and manage end-user devices. The system provides:

- **Asset Registration**: Complete device details and specifications tracking
- **Assignment Management**: Equipment assignment and ownership tracking
- **Condition Monitoring**: Device condition status tracking
- **Issue/Return Management**: Streamlined equipment issue and return processes
- **Comprehensive Reporting**: Reports by date, department, device type, and status
- **Search & Filtering**: Advanced search and filtering capabilities
- **Audit History**: Complete audit trail of asset movements

## 🏗️ System Architecture

- **Framework**: Spring Boot 3.5.14 with MVC Pattern
- **Database**: MySQL with JPA/Hibernate ORM
- **Security**: Spring Security with role-based access control
- **UI**: Thymeleaf templates with Bootstrap 5
- **Authentication**: BCrypt password encoding

## 👥 Team Information

- **RegNo1**: 24rp01201
- **RegNo2**: 24rp01530
- **Group ID**: com.Inventory
- **Project Name**: IMS (Inventory Management System)

## 🔐 System Administrator Credentials

**Username**: `24rp01201`  
**Password**: `24rp01530`

*Note: These credentials are pre-configured in the database and have full system administrator privileges.*

## 🚀 Setup Instructions

### Prerequisites

1. **Java 17** or higher
2. **MySQL 8.0** or higher
3. **Maven 3.6** or higher
4. **Git** (for cloning)

### Database Setup

1. **Install and start MySQL server**

2. **Create database**:
   ```sql
   CREATE DATABASE ims_db;
   ```

3. **Import the database schema**:
   ```bash
   mysql -u root -p ims_db < src/main/resources/ims.sql
   ```

   This will create all necessary tables and insert:
   - Default equipment categories
   - SysAdmin user with credentials (24rp01201/24rp01530)
   - Sample users and equipment for testing

### Application Configuration

1. **Update database credentials** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ims_db
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**:
   - URL: `http://localhost:8080`
   - Login with SysAdmin credentials: `24rp01201` / `24rp01530`

## 🎮 Navigation Guide

### Main Dashboard
- **URL**: `/dashboard`
- **Features**: Real-time statistics, recent activities, quick actions
- **Accessible by**: All authenticated users

### Equipment Management
- **URL**: `/equipment`
- **Features**: View, add, edit, assign, and maintain equipment
- **Accessible by**: SYSADMIN, MANAGER

### User Management
- **URL**: `/users`
- **Features**: User account management
- **Accessible by**: SYSADMIN only

### Assignment Management
- **URL**: `/assignments`
- **Features**: Equipment assignment and return tracking
- **Accessible by**: SYSADMIN, MANAGER

### Maintenance Tracking
- **URL**: `/maintenance`
- **Features**: Maintenance scheduling and tracking
- **Accessible by**: SYSADMIN, MANAGER

### Reports
- **URL**: `/reports`
- **Features**: Comprehensive reporting and analytics
- **Accessible by**: SYSADMIN, MANAGER

## 🔐 User Roles & Permissions

### SYSADMIN (System Administrator)
- Full system access
- User management
- System configuration
- All equipment operations
- Complete reporting access

### MANAGER
- Equipment management
- Assignment operations
- Maintenance scheduling
- Reporting access
- Limited user operations

### USER
- View assigned equipment
- Profile management
- Limited dashboard access

## 📊 Key Features

### Equipment Tracking
- **Asset Tagging**: Unique asset tags for all equipment
- **Categorization**: Equipment types (Laptop, Desktop, Mobile, Tablet, etc.)
- **Condition Monitoring**: Status tracking (Excellent, Good, Fair, Poor)
- **Location Tracking**: Physical location management

### Assignment Management
- **User Assignment**: Assign equipment to specific users
- **Return Tracking**: Monitor equipment returns
- **Condition Recording**: Record equipment condition during assignment/return
- **Assignment History**: Complete audit trail

### Maintenance Management
- **Preventive Maintenance**: Schedule regular maintenance
- **Corrective Maintenance**: Track repairs and fixes
- **Cost Tracking**: Monitor maintenance expenses
- **Vendor Management**: Track service providers

### Reporting & Analytics
- **Equipment Summary**: Status and condition reports
- **Assignment Reports**: User assignment history
- **Maintenance Reports**: Maintenance cost and frequency
- **Audit Trail**: Complete system activity logs

## 🗄️ Database Schema

### Core Tables
- **users**: User accounts and authentication
- **equipment**: Equipment inventory
- **equipment_category**: Equipment categories
- **assignment**: Equipment assignments
- **maintenance_record**: Maintenance tracking
- **audit_trail**: System audit logs

### Key Relationships
- Users can have multiple assignments
- Equipment can have multiple maintenance records
- All activities are tracked in audit trail

## 🛠️ Development Notes

### Security Implementation
- Password encryption using BCrypt
- Role-based access control
- Session management
- CSRF protection

### Data Validation
- Input validation on all forms
- Unique constraint enforcement
- Data integrity checks
- Business rule validation

### Performance Optimization
- Database indexing on key fields
- Lazy loading for relationships
- Pagination for large datasets
- Optimized queries

## 📱 Browser Compatibility

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## 🔧 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify MySQL is running
   - Check database credentials in application.properties
   - Ensure database `ims_db` exists

2. **Login Issues**
   - Verify SysAdmin user exists in database
   - Check password encoding (BCrypt)
   - Ensure user account is active

3. **Page Not Found Errors**
   - Verify application started successfully
   - Check console logs for errors
   - Ensure all template files exist

### Debug Mode
Enable debug logging in `application.properties`:
```properties
logging.level.com.Inventory.ims=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 📈 Project Impact

### Expected Benefits
- **Improved Asset Accountability**: Complete visibility of equipment ownership
- **Enhanced Operational Efficiency**: Streamlined assignment and return processes
- **Accurate Reporting**: Real-time data for decision making
- **Reduced Asset Loss**: Better tracking and monitoring
- **Cost Savings**: Optimized equipment utilization and maintenance

### Scalability
- Modular architecture for easy expansion
- Database design supports growth
- Role-based access for team scaling
- RESTful API potential for mobile integration

## 📞 Support

For technical support or questions regarding this project:
- **Developers**: Students with RegNo 24rp01201 and 24rp01530
- **Challenge Provider**: Airtel
- **Framework**: Spring Boot MVC

---

**Project Status**: ✅ Complete and Ready for Submission  
**Last Updated**: April 2026  
**Version**: 1.0.0
