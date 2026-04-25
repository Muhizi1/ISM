package com.Inventory.ims.service;

import com.Inventory.ims.model.AuditTrail;
import com.Inventory.ims.model.Equipment;
import com.Inventory.ims.model.User;
import com.Inventory.ims.repository.AuditTrailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditTrailService {
    
    @Autowired
    private AuditTrailRepository auditTrailRepository;
    
    public List<AuditTrail> findAllAuditTrails() {
        return auditTrailRepository.findAllOrderedByPerformedAt();
    }
    
    public AuditTrail createAuditTrail(Equipment equipment, User user, String action, 
                                     String description, String oldValues, String newValues, User performedBy) {
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setEquipment(equipment);
        auditTrail.setUser(user);
        auditTrail.setAction(action);
        auditTrail.setDescription(description);
        auditTrail.setOldValues(oldValues);
        auditTrail.setNewValues(newValues);
        auditTrail.setPerformedBy(performedBy);
        
        return auditTrailRepository.save(auditTrail);
    }
    
    public void logEquipmentAction(Equipment equipment, String action, String description, 
                                 String oldValues, String newValues, User performedBy) {
        createAuditTrail(equipment, null, action, description, oldValues, newValues, performedBy);
    }
    
    public void logUserAction(User user, String action, String description, 
                            String oldValues, String newValues, User performedBy) {
        createAuditTrail(null, user, action, description, oldValues, newValues, performedBy);
    }
    
    public void logAssignmentAction(Equipment equipment, User user, String action, 
                                  String description, User performedBy) {
        createAuditTrail(equipment, user, action, description, null, null, performedBy);
    }
    
    public List<AuditTrail> findAuditTrailsByEquipment(Equipment equipment) {
        return auditTrailRepository.findByEquipment(equipment);
    }
    
    public List<AuditTrail> findAuditTrailsByUser(User user) {
        return auditTrailRepository.findByUser(user);
    }
    
    public List<AuditTrail> findAuditTrailsByAction(String action) {
        return auditTrailRepository.findByAction(action);
    }
    
    public List<AuditTrail> findAuditTrailsByPerformedBy(User performedBy) {
        return auditTrailRepository.findByPerformedBy(performedBy);
    }
    
    public List<AuditTrail> findAuditTrailsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditTrailRepository.findAuditTrailByDateRange(startDate, endDate);
    }
    
    public List<AuditTrail> searchAuditTrails(String keyword) {
        return auditTrailRepository.searchAuditTrail(keyword);
    }
    
    public long getTotalAuditTrailsCount() {
        return auditTrailRepository.count();
    }
    
    public long getAuditTrailsCountByAction(String action) {
        return auditTrailRepository.countByAction(action);
    }
    
    public List<AuditTrail> findRecentAuditTrails(int limit) {
        List<AuditTrail> allTrails = auditTrailRepository.findAllOrderedByPerformedAt();
        return allTrails.stream().limit(limit).toList();
    }
}
