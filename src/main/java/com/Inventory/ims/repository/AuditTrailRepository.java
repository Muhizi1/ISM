package com.Inventory.ims.repository;

import com.Inventory.ims.model.AuditTrail;
import com.Inventory.ims.model.Equipment;
import com.Inventory.ims.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {
    
    List<AuditTrail> findByEquipment(Equipment equipment);
    
    List<AuditTrail> findByUser(User user);
    
    List<AuditTrail> findByAction(String action);
    
    List<AuditTrail> findByPerformedBy(User performedBy);
    
    List<AuditTrail> findByPerformedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT a FROM AuditTrail a WHERE a.performedAt BETWEEN :startDate AND :endDate ORDER BY a.performedAt DESC")
    List<AuditTrail> findAuditTrailByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM AuditTrail a WHERE (LOWER(a.action) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.equipment.assetTag) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<AuditTrail> searchAuditTrail(@Param("search") String search);
    
    @Query("SELECT a FROM AuditTrail a ORDER BY a.performedAt DESC")
    List<AuditTrail> findAllOrderedByPerformedAt();
    
    @Query("SELECT COUNT(a) FROM AuditTrail a WHERE a.action = :action")
    long countByAction(@Param("action") String action);
    
    @Query("SELECT a FROM AuditTrail a WHERE a.equipment.id = :equipmentId ORDER BY a.performedAt DESC")
    List<AuditTrail> findByEquipmentId(@Param("equipmentId") Long equipmentId);
    
    @Query("SELECT a FROM AuditTrail a WHERE a.user.id = :userId ORDER BY a.performedAt DESC")
    List<AuditTrail> findByUserId(@Param("userId") Long userId);
}
