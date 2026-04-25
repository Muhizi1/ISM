package com.Inventory.ims.service;

import com.Inventory.ims.model.Equipment;
import com.Inventory.ims.model.MaintenanceRecord;
import com.Inventory.ims.model.User;
import com.Inventory.ims.repository.MaintenanceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaintenanceRecordService {
    
    @Autowired
    private MaintenanceRecordRepository maintenanceRepository;
    
    @Autowired
    private EquipmentService equipmentService;
    
    public List<MaintenanceRecord> findAllMaintenanceRecords() {
        return maintenanceRepository.findAllOrderedByPerformedDate();
    }
    
    public Optional<MaintenanceRecord> findMaintenanceRecordById(Long id) {
        return maintenanceRepository.findById(id);
    }
    
    public MaintenanceRecord createMaintenanceRecord(MaintenanceRecord record) {
        // Validate equipment exists
        Equipment equipment = equipmentService.findEquipmentById(record.getEquipment().getId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        
        record.setEquipment(equipment);
        
        // If equipment is available, set it to under maintenance
        if (equipment.getStatus() == Equipment.EquipmentStatus.AVAILABLE) {
            equipment.setStatus(Equipment.EquipmentStatus.UNDER_MAINTENANCE);
            equipmentService.updateEquipment(equipment.getId(), equipment);
        }
        
        return maintenanceRepository.save(record);
    }
    
    public MaintenanceRecord updateMaintenanceRecord(Long id, MaintenanceRecord recordDetails) {
        MaintenanceRecord record = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));
        
        record.setMaintenanceType(recordDetails.getMaintenanceType());
        record.setDescription(recordDetails.getDescription());
        record.setCost(recordDetails.getCost());
        record.setPerformedBy(recordDetails.getPerformedBy());
        record.setPerformedDate(recordDetails.getPerformedDate());
        record.setNextMaintenanceDate(recordDetails.getNextMaintenanceDate());
        record.setCreatedBy(recordDetails.getCreatedBy());
        
        return maintenanceRepository.save(record);
    }
    
    public void deleteMaintenanceRecord(Long id) {
        MaintenanceRecord record = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));
        
        // Check if this is the last maintenance record for the equipment
        // and if equipment is under maintenance, set it back to available
        List<MaintenanceRecord> otherRecords = maintenanceRepository.findByEquipment(record.getEquipment());
        if (otherRecords.size() == 1) { // This is the only record
            Equipment equipment = record.getEquipment();
            if (equipment.getStatus() == Equipment.EquipmentStatus.UNDER_MAINTENANCE) {
                equipment.setStatus(Equipment.EquipmentStatus.AVAILABLE);
                equipmentService.updateEquipment(equipment.getId(), equipment);
            }
        }
        
        maintenanceRepository.delete(record);
    }
    
    public List<MaintenanceRecord> findMaintenanceRecordsByEquipment(Equipment equipment) {
        return maintenanceRepository.findByEquipment(equipment);
    }
    
    public List<MaintenanceRecord> findMaintenanceRecordsByType(MaintenanceRecord.MaintenanceType type) {
        return maintenanceRepository.findByMaintenanceType(type);
    }
    
    public List<MaintenanceRecord> findMaintenanceRecordsByPerformedBy(String performedBy) {
        return maintenanceRepository.findByPerformedBy(performedBy);
    }
    
    public List<MaintenanceRecord> findMaintenanceRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.findByPerformedDateBetween(startDate, endDate);
    }
    
    public List<MaintenanceRecord> findScheduledMaintenance(LocalDate date) {
        return maintenanceRepository.findScheduledMaintenance(date);
    }
    
    public List<MaintenanceRecord> findMaintenanceRecordsNeedingAttention() {
        return maintenanceRepository.findScheduledMaintenance(LocalDate.now());
    }
    
    public List<MaintenanceRecord> searchMaintenanceRecords(String keyword) {
        return maintenanceRepository.searchMaintenanceRecords(keyword);
    }
    
    public long getTotalMaintenanceRecordsCount() {
        return maintenanceRepository.count();
    }
    
    public long getMaintenanceCountByEquipment(Long equipmentId) {
        return maintenanceRepository.countByEquipment(equipmentId);
    }
    
    public BigDecimal getTotalMaintenanceCostByEquipment(Long equipmentId) {
        BigDecimal cost = maintenanceRepository.sumMaintenanceCostByEquipment(equipmentId);
        return cost != null ? cost : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalMaintenanceCostByDateRange(LocalDate startDate, LocalDate endDate) {
        BigDecimal cost = maintenanceRepository.sumMaintenanceCostByDateRange(startDate, endDate);
        return cost != null ? cost : BigDecimal.ZERO;
    }
    
    public void completeMaintenance(Long maintenanceId) {
        MaintenanceRecord record = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found"));
        
        Equipment equipment = record.getEquipment();
        equipment.setStatus(Equipment.EquipmentStatus.AVAILABLE);
        equipmentService.updateEquipment(equipment.getId(), equipment);
    }
}
