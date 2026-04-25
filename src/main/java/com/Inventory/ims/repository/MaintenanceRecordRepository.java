package com.Inventory.ims.repository;

import com.Inventory.ims.model.Equipment;
import com.Inventory.ims.model.MaintenanceRecord;
import com.Inventory.ims.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    
    List<MaintenanceRecord> findByEquipment(Equipment equipment);
    
    List<MaintenanceRecord> findByMaintenanceType(MaintenanceRecord.MaintenanceType maintenanceType);
    
    List<MaintenanceRecord> findByPerformedBy(String performedBy);
    
    List<MaintenanceRecord> findByPerformedDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<MaintenanceRecord> findByNextMaintenanceDateBefore(LocalDate date);
    
    List<MaintenanceRecord> findByCreatedBy(User createdBy);
    
    @Query("SELECT COUNT(m) FROM MaintenanceRecord m WHERE m.equipment.id = :equipmentId")
    long countByEquipment(@Param("equipmentId") Long equipmentId);
    
    @Query("SELECT SUM(m.cost) FROM MaintenanceRecord m WHERE m.equipment.id = :equipmentId")
    BigDecimal sumMaintenanceCostByEquipment(@Param("equipmentId") Long equipmentId);
    
    @Query("SELECT SUM(m.cost) FROM MaintenanceRecord m WHERE m.performedDate BETWEEN :startDate AND :endDate")
    BigDecimal sumMaintenanceCostByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m FROM MaintenanceRecord m WHERE (LOWER(m.equipment.assetTag) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.performedBy) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<MaintenanceRecord> searchMaintenanceRecords(@Param("search") String search);
    
    @Query("SELECT m FROM MaintenanceRecord m ORDER BY m.performedDate DESC")
    List<MaintenanceRecord> findAllOrderedByPerformedDate();
    
    @Query("SELECT m FROM MaintenanceRecord m WHERE m.nextMaintenanceDate <= :date ORDER BY m.nextMaintenanceDate ASC")
    List<MaintenanceRecord> findScheduledMaintenance(@Param("date") LocalDate date);
}
