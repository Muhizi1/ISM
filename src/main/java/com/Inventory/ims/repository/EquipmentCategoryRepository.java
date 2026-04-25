package com.Inventory.ims.repository;

import com.Inventory.ims.model.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, Long> {
    
    Optional<EquipmentCategory> findByName(String name);
    
    boolean existsByName(String name);
    
    List<EquipmentCategory> findByActiveTrue();
    
    @Query("SELECT c FROM EquipmentCategory c WHERE c.active = true AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<EquipmentCategory> searchCategories(@Param("search") String search);
    
    @Query("SELECT c FROM EquipmentCategory c WHERE c.active = true ORDER BY c.name")
    List<EquipmentCategory> findAllActiveOrderedByName();
}
