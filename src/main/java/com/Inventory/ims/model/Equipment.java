package com.Inventory.ims.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "equipment")
public class Equipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "asset_tag", unique = true, nullable = false)
    @NotBlank(message = "Asset tag is required")
    @Size(max = 50, message = "Asset tag must not exceed 50 characters")
    private String assetTag;
    
    @Column(name = "serial_number")
    @Size(max = 100, message = "Serial number must not exceed 100 characters")
    private String serialNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private EquipmentCategory category;
    
    @Size(max = 50, message = "Brand must not exceed 50 characters")
    private String brand;
    
    @Size(max = 100, message = "Model must not exceed 100 characters")
    private String model;
    
    @Column(columnDefinition = "TEXT")
    private String specifications;
    
    @Column(name = "purchase_date")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
    
    @Column(name = "purchase_cost", precision = 10, scale = 2)
    private BigDecimal purchaseCost;
    
    @Column(name = "warranty_expiry")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate warrantyExpiry;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", nullable = false)
    private ConditionStatus conditionStatus = ConditionStatus.EXCELLENT;
    
    @Size(max = 100)
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Assignment> assignments;
    
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceRecord> maintenanceRecords;
    
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditTrail> auditTrails;
    
    public enum EquipmentStatus {
        AVAILABLE, ASSIGNED, UNDER_MAINTENANCE, RETIRED, LOST
    }
    
    public enum ConditionStatus {
        EXCELLENT, GOOD, FAIR, POOR
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Equipment() {}
    
    public Equipment(String assetTag, String serialNumber, EquipmentCategory category, String brand, String model) {
        this.assetTag = assetTag;
        this.serialNumber = serialNumber;
        this.category = category;
        this.brand = brand;
        this.model = model;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAssetTag() { return assetTag; }
    public void setAssetTag(String assetTag) { this.assetTag = assetTag; }
    
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    
    public EquipmentCategory getCategory() { return category; }
    public void setCategory(EquipmentCategory category) { this.category = category; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }
    
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public BigDecimal getPurchaseCost() { return purchaseCost; }
    public void setPurchaseCost(BigDecimal purchaseCost) { this.purchaseCost = purchaseCost; }
    
    public LocalDate getWarrantyExpiry() { return warrantyExpiry; }
    public void setWarrantyExpiry(LocalDate warrantyExpiry) { this.warrantyExpiry = warrantyExpiry; }
    
    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }
    
    public ConditionStatus getConditionStatus() { return conditionStatus; }
    public void setConditionStatus(ConditionStatus conditionStatus) { this.conditionStatus = conditionStatus; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    @Transient
    public Assignment getCurrentAssignment() {
        if (assignments == null || assignments.isEmpty()) {
            return null;
        }
        return assignments.stream()
                .filter(a -> a.getStatus() == Assignment.AssignmentStatus.ACTIVE)
                .findFirst()
                .orElse(null);
    }
    
    public List<Assignment> getAssignments() { return assignments; }
    public void setAssignments(List<Assignment> assignments) { this.assignments = assignments; }
    
    public List<MaintenanceRecord> getMaintenanceRecords() { return maintenanceRecords; }
    public void setMaintenanceRecords(List<MaintenanceRecord> maintenanceRecords) { this.maintenanceRecords = maintenanceRecords; }
    
    public List<AuditTrail> getAuditTrails() { return auditTrails; }
    public void setAuditTrails(List<AuditTrail> auditTrails) { this.auditTrails = auditTrails; }
}
