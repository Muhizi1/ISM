package com.Inventory.ims.repository;

import com.Inventory.ims.model.Equipment;
import com.Inventory.ims.model.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
    Optional<Equipment> findByAssetTag(String assetTag);
    
    Optional<Equipment> findBySerialNumber(String serialNumber);
    
    boolean existsByAssetTag(String assetTag);
    
    boolean existsBySerialNumber(String serialNumber);
    
    List<Equipment> findByCategory(EquipmentCategory category);
    
    List<Equipment> findByStatus(Equipment.EquipmentStatus status);
    
    List<Equipment> findByConditionStatus(Equipment.ConditionStatus conditionStatus);
    
    List<Equipment> findByLocation(String location);
    
    List<Equipment> findByBrand(String brand);
    
    List<Equipment> findByActiveTrue();
    
    @Query("SELECT e FROM Equipment e WHERE e.active = true AND (LOWER(e.assetTag) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.serialNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.model) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.location) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Equipment> searchEquipment(@Param("search") String search);
    
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.status = :status AND e.active = true")
    long countByStatus(@Param("status") Equipment.EquipmentStatus status);
    
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.category.id = :categoryId AND e.active = true")
    long countByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT SUM(e.purchaseCost) FROM Equipment e WHERE e.active = true")
    BigDecimal sumTotalValue();
    
    @Query("SELECT SUM(e.purchaseCost) FROM Equipment e WHERE e.status = :status AND e.active = true")
    BigDecimal sumValueByStatus(@Param("status") Equipment.EquipmentStatus status);
    
    @Query("SELECT e FROM Equipment e WHERE e.warrantyExpiry <= :date AND e.active = true")
    List<Equipment> findEquipmentWithWarrantyExpiringBefore(@Param("date") java.time.LocalDate date);
    
    @Query("SELECT e FROM Equipment e WHERE e.active = true ORDER BY e.assetTag")
    List<Equipment> findAllActiveOrderedByAssetTag();
}
