package com.Inventory.ims.service;

import com.Inventory.ims.model.Assignment;
import com.Inventory.ims.model.Equipment;
import com.Inventory.ims.model.User;
import com.Inventory.ims.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssignmentService {
    
    @Autowired
    private AssignmentRepository assignmentRepository;
    
    @Autowired
    private EquipmentService equipmentService;
    
    @Autowired
    private UserService userService;
    
    public List<Assignment> findAllAssignments() {
        return assignmentRepository.findAll();
    }
    
    public Optional<Assignment> findAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }
    
    public Assignment assignEquipment(Equipment equipment, User user, User assignedBy, LocalDate expectedReturnDate) {
        // Check if equipment is available
        if (equipment.getStatus() != Equipment.EquipmentStatus.AVAILABLE) {
            throw new RuntimeException("Equipment is not available for assignment");
        }
        
        // Check if equipment is already assigned
        Optional<Assignment> existingAssignment = assignmentRepository.findByEquipmentAndStatus(equipment, Assignment.AssignmentStatus.ACTIVE);
        if (existingAssignment.isPresent()) {
            throw new RuntimeException("Equipment is already assigned");
        }
        
        // Create assignment
        Assignment assignment = new Assignment();
        assignment.setEquipment(equipment);
        assignment.setUser(user);
        assignment.setAssignedBy(assignedBy);
        assignment.setExpectedReturnDate(expectedReturnDate);
        assignment.setStatus(Assignment.AssignmentStatus.ACTIVE);
        
        // Update equipment status
        equipment.setStatus(Equipment.EquipmentStatus.ASSIGNED);
        equipmentService.updateEquipment(equipment.getId(), equipment);
        
        return assignmentRepository.save(assignment);
    }
    
    public Assignment returnEquipment(Long assignmentId, Equipment.ConditionStatus returnCondition, String returnNotes) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + assignmentId));
        
        if (assignment.getStatus() != Assignment.AssignmentStatus.ACTIVE) {
            throw new RuntimeException("Assignment is not active");
        }
        
        // Update assignment
        assignment.setActualReturnDate(LocalDateTime.now());
        assignment.setReturnCondition(returnCondition);
        assignment.setReturnNotes(returnNotes);
        assignment.setStatus(Assignment.AssignmentStatus.RETURNED);
        
        // Update equipment status and condition
        Equipment equipment = assignment.getEquipment();
        equipment.setStatus(Equipment.EquipmentStatus.AVAILABLE);
        equipment.setConditionStatus(returnCondition);
        equipmentService.updateEquipment(equipment.getId(), equipment);
        
        return assignmentRepository.save(assignment);
    }
    
    public void deleteAssignment(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));
        
        // If assignment is active, make equipment available again
        if (assignment.getStatus() == Assignment.AssignmentStatus.ACTIVE) {
            Equipment equipment = assignment.getEquipment();
            equipment.setStatus(Equipment.EquipmentStatus.AVAILABLE);
            equipmentService.updateEquipment(equipment.getId(), equipment);
        }
        
        assignmentRepository.delete(assignment);
    }
    
    public List<Assignment> findAssignmentsByEquipment(Equipment equipment) {
        return assignmentRepository.findByEquipment(equipment);
    }
    
    public List<Assignment> findAssignmentsByUser(User user) {
        return assignmentRepository.findByUser(user);
    }
    
    public List<Assignment> findAssignmentsByAssignedBy(User assignedBy) {
        return assignmentRepository.findByAssignedBy(assignedBy);
    }
    
    public List<Assignment> findAssignmentsByStatus(Assignment.AssignmentStatus status) {
        return assignmentRepository.findByStatus(status);
    }
    
    public List<Assignment> findOverdueAssignments() {
        return assignmentRepository.findOverdueAssignments(LocalDate.now());
    }
    
    public List<Assignment> findActiveAssignmentsOrderedByReturnDate() {
        return assignmentRepository.findActiveAssignmentsOrderedByReturnDate();
    }
    
    public List<Assignment> searchAssignments(String keyword) {
        return assignmentRepository.searchAssignments(keyword);
    }
    
    public List<Assignment> findAssignmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return assignmentRepository.findAssignmentsByDateRange(startDate, endDate);
    }
    
    public long getTotalAssignmentsCount() {
        return assignmentRepository.count();
    }
    
    public long getAssignmentsCountByStatus(Assignment.AssignmentStatus status) {
        return assignmentRepository.countByStatus(status);
    }
    
    public long getActiveAssignmentsCountByUser(Long userId) {
        return assignmentRepository.countActiveAssignmentsByUser(userId);
    }
    
    public Optional<Assignment> findActiveAssignmentForEquipment(Equipment equipment) {
        return assignmentRepository.findByEquipmentAndStatus(equipment, Assignment.AssignmentStatus.ACTIVE);
    }
    
    public Optional<Assignment> findActiveAssignmentForUser(User user) {
        return assignmentRepository.findByUserAndStatus(user, Assignment.AssignmentStatus.ACTIVE);
    }
}
