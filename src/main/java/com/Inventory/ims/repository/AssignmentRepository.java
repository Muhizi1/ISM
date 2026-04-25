package com.Inventory.ims.repository;

import com.Inventory.ims.model.Assignment;
import com.Inventory.ims.model.Equipment;
import com.Inventory.ims.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    List<Assignment> findByEquipment(Equipment equipment);
    
    List<Assignment> findByUser(User user);
    
    List<Assignment> findByAssignedBy(User assignedBy);
    
    List<Assignment> findByStatus(Assignment.AssignmentStatus status);
    
    Optional<Assignment> findByEquipmentAndStatus(Equipment equipment, Assignment.AssignmentStatus status);
    
    Optional<Assignment> findByUserAndStatus(User user, Assignment.AssignmentStatus status);
    
    @Query("SELECT a FROM Assignment a WHERE a.status = 'ACTIVE' AND a.expectedReturnDate < :currentDate")
    List<Assignment> findOverdueAssignments(@Param("currentDate") java.time.LocalDate currentDate);
    
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.status = :status")
    long countByStatus(@Param("status") Assignment.AssignmentStatus status);
    
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.user.id = :userId AND a.status = 'ACTIVE'")
    long countActiveAssignmentsByUser(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Assignment a WHERE a.assignedDate BETWEEN :startDate AND :endDate ORDER BY a.assignedDate DESC")
    List<Assignment> findAssignmentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Assignment a WHERE a.status = 'ACTIVE' ORDER BY a.expectedReturnDate ASC")
    List<Assignment> findActiveAssignmentsOrderedByReturnDate();
    
    @Query("SELECT a FROM Assignment a WHERE (LOWER(a.equipment.assetTag) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.equipment.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.equipment.model) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Assignment> searchAssignments(@Param("search") String search);
}
