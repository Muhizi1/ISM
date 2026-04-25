package com.Inventory.ims.config;

import com.Inventory.ims.model.*;
import com.Inventory.ims.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EquipmentCategoryRepository categoryRepository;
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @Autowired
    private AssignmentRepository assignmentRepository;
    
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userRepository.count() > 0) {
            return; // Data already initialized
        }

        // Initialize categories
        initializeCategories();
        
        // Initialize users
        initializeUsers();
        
        // Initialize equipment
        initializeEquipment();
        
        // Initialize assignments
        initializeAssignments();
    }

    private void initializeCategories() {
        EquipmentCategory laptop = new EquipmentCategory();
        laptop.setName("Laptop");
        laptop.setDescription("Laptop computers and notebooks");
        categoryRepository.save(laptop);

        EquipmentCategory desktop = new EquipmentCategory();
        desktop.setName("Desktop");
        desktop.setDescription("Desktop computers and workstations");
        categoryRepository.save(desktop);

        EquipmentCategory mobile = new EquipmentCategory();
        mobile.setName("Mobile Phone");
        mobile.setDescription("Smartphones and mobile devices");
        categoryRepository.save(mobile);

        EquipmentCategory tablet = new EquipmentCategory();
        tablet.setName("Tablet");
        tablet.setDescription("Tablet computers");
        categoryRepository.save(tablet);

        EquipmentCategory monitor = new EquipmentCategory();
        monitor.setName("Monitor");
        monitor.setDescription("Computer monitors and displays");
        categoryRepository.save(monitor);

        EquipmentCategory printer = new EquipmentCategory();
        printer.setName("Printer");
        printer.setDescription("Printers and scanners");
        categoryRepository.save(printer);

        EquipmentCategory other = new EquipmentCategory();
        other.setName("Other");
        other.setDescription("Other equipment types");
        categoryRepository.save(other);
    }

    private void initializeUsers() {
        // Create SysAdmin user with correct BCrypt password for "24rp01530"
        User sysAdmin = new User();
        sysAdmin.setUsername("24rp01201");
        sysAdmin.setPassword(passwordEncoder.encode("24rp01530"));
        sysAdmin.setFullName("System Administrator");
        sysAdmin.setEmail("sysadmin@airtel.com");
        sysAdmin.setRole(User.UserRole.SYSADMIN);
        sysAdmin.setDepartment("IT");
        sysAdmin.setActive(true);
        userRepository.save(sysAdmin);

        // Create sample users
        User johnDoe = new User();
        johnDoe.setUsername("john_doe");
        johnDoe.setPassword(passwordEncoder.encode("password123"));
        johnDoe.setFullName("John Doe");
        johnDoe.setEmail("john.doe@airtel.com");
        johnDoe.setRole(User.UserRole.USER);
        johnDoe.setDepartment("HR");
        johnDoe.setActive(true);
        userRepository.save(johnDoe);

        User janeSmith = new User();
        janeSmith.setUsername("jane_smith");
        janeSmith.setPassword(passwordEncoder.encode("password123"));
        janeSmith.setFullName("Jane Smith");
        janeSmith.setEmail("jane.smith@airtel.com");
        janeSmith.setRole(User.UserRole.MANAGER);
        janeSmith.setDepartment("IT");
        janeSmith.setActive(true);
        userRepository.save(janeSmith);
    }

    private void initializeEquipment() {
        Equipment laptop1 = new Equipment();
        laptop1.setAssetTag("LAP001");
        laptop1.setSerialNumber("DL5420202301");
        laptop1.setCategory(categoryRepository.findByName("Laptop").orElse(null));
        laptop1.setBrand("Dell");
        laptop1.setModel("Latitude 5420");
        laptop1.setSpecifications("Intel i5-1135G7, 8GB RAM, 256GB SSD, 14\" FHD");
        laptop1.setPurchaseDate(LocalDate.of(2023, 1, 15));
        laptop1.setPurchaseCost(new BigDecimal("850.00"));
        laptop1.setWarrantyExpiry(LocalDate.of(2025, 1, 15));
        laptop1.setStatus(Equipment.EquipmentStatus.AVAILABLE);
        laptop1.setConditionStatus(Equipment.ConditionStatus.EXCELLENT);
        laptop1.setLocation("IT Office");
        laptop1.setActive(true);
        equipmentRepository.save(laptop1);

        Equipment laptop2 = new Equipment();
        laptop2.setAssetTag("LAP002");
        laptop2.setSerialNumber("HP840202302");
        laptop2.setCategory(categoryRepository.findByName("Laptop").orElse(null));
        laptop2.setBrand("HP");
        laptop2.setModel("EliteBook 840");
        laptop2.setSpecifications("Intel i7-1165G7, 16GB RAM, 512GB SSD, 14\" FHD");
        laptop2.setPurchaseDate(LocalDate.of(2023, 2, 20));
        laptop2.setPurchaseCost(new BigDecimal("1200.00"));
        laptop2.setWarrantyExpiry(LocalDate.of(2025, 2, 20));
        laptop2.setStatus(Equipment.EquipmentStatus.ASSIGNED);
        laptop2.setConditionStatus(Equipment.ConditionStatus.GOOD);
        laptop2.setLocation("HR Department");
        laptop2.setActive(true);
        equipmentRepository.save(laptop2);

        Equipment desktop1 = new Equipment();
        desktop1.setAssetTag("DT001");
        desktop1.setSerialNumber("DT7090202301");
        desktop1.setCategory(categoryRepository.findByName("Desktop").orElse(null));
        desktop1.setBrand("Dell");
        desktop1.setModel("OptiPlex 7090");
        desktop1.setSpecifications("Intel i5-11500, 8GB RAM, 256GB SSD");
        desktop1.setPurchaseDate(LocalDate.of(2023, 3, 10));
        desktop1.setPurchaseCost(new BigDecimal("750.00"));
        desktop1.setWarrantyExpiry(LocalDate.of(2025, 3, 10));
        desktop1.setStatus(Equipment.EquipmentStatus.AVAILABLE);
        desktop1.setConditionStatus(Equipment.ConditionStatus.GOOD);
        desktop1.setLocation("Finance Office");
        desktop1.setActive(true);
        equipmentRepository.save(desktop1);

        Equipment mobile1 = new Equipment();
        mobile1.setAssetTag("MB001");
        mobile1.setSerialNumber("AP132023001");
        mobile1.setCategory(categoryRepository.findByName("Mobile Phone").orElse(null));
        mobile1.setBrand("Apple");
        mobile1.setModel("iPhone 13");
        mobile1.setSpecifications("A15 Bionic, 128GB, 6.1\" Super Retina XDR");
        mobile1.setPurchaseDate(LocalDate.of(2023, 4, 5));
        mobile1.setPurchaseCost(new BigDecimal("650.00"));
        mobile1.setWarrantyExpiry(LocalDate.of(2024, 4, 5));
        mobile1.setStatus(Equipment.EquipmentStatus.ASSIGNED);
        mobile1.setConditionStatus(Equipment.ConditionStatus.EXCELLENT);
        mobile1.setLocation("Operations");
        mobile1.setActive(true);
        equipmentRepository.save(mobile1);
    }

    private void initializeAssignments() {
        User sysAdmin = userRepository.findByUsername("24rp01201").orElse(null);
        User johnDoe = userRepository.findByUsername("john_doe").orElse(null);
        User janeSmith = userRepository.findByUsername("jane_smith").orElse(null);
        
        Equipment laptop2 = equipmentRepository.findByAssetTag("LAP002").orElse(null);
        Equipment mobile1 = equipmentRepository.findByAssetTag("MB001").orElse(null);

        if (sysAdmin != null && johnDoe != null && laptop2 != null) {
            Assignment assignment1 = new Assignment();
            assignment1.setEquipment(laptop2);
            assignment1.setUser(johnDoe);
            assignment1.setAssignedBy(sysAdmin);
            assignment1.setAssignedDate(java.time.LocalDateTime.now());
            assignment1.setStatus(Assignment.AssignmentStatus.ACTIVE);
            assignmentRepository.save(assignment1);
        }

        if (sysAdmin != null && janeSmith != null && mobile1 != null) {
            Assignment assignment2 = new Assignment();
            assignment2.setEquipment(mobile1);
            assignment2.setUser(janeSmith);
            assignment2.setAssignedBy(sysAdmin);
            assignment2.setAssignedDate(java.time.LocalDateTime.now());
            assignment2.setStatus(Assignment.AssignmentStatus.ACTIVE);
            assignmentRepository.save(assignment2);
        }
    }
}
