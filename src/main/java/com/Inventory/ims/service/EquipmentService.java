package com.Inventory.ims.service;

import com.Inventory.ims.model.Equipment;
import com.Inventory.ims.model.EquipmentCategory;
import com.Inventory.ims.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipmentService {
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    public List<Equipment> findAllEquipment() {
        return equipmentRepository.findAllActiveOrderedByAssetTag();
    }
    
    public Optional<Equipment> findEquipmentById(Long id) {
        return equipmentRepository.findById(id);
    }
    
    public Optional<Equipment> findEquipmentByAssetTag(String assetTag) {
        return equipmentRepository.findByAssetTag(assetTag);
    }
    
    public Equipment createEquipment(Equipment equipment) {
        if (equipmentRepository.existsByAssetTag(equipment.getAssetTag())) {
            throw new RuntimeException("Asset tag already exists: " + equipment.getAssetTag());
        }
        
        if (equipment.getSerialNumber() != null && 
            equipmentRepository.existsBySerialNumber(equipment.getSerialNumber())) {
            throw new RuntimeException("Serial number already exists: " + equipment.getSerialNumber());
        }
        
        equipment.setActive(true);
        equipment.setStatus(Equipment.EquipmentStatus.AVAILABLE);
        equipment.setConditionStatus(Equipment.ConditionStatus.EXCELLENT);
        return equipmentRepository.save(equipment);
    }
    
    public Equipment updateEquipment(Long id, Equipment equipmentDetails) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));
        
        if (!equipment.getAssetTag().equals(equipmentDetails.getAssetTag()) && 
            equipmentRepository.existsByAssetTag(equipmentDetails.getAssetTag())) {
            throw new RuntimeException("Asset tag already exists: " + equipmentDetails.getAssetTag());
        }
        
        if (equipmentDetails.getSerialNumber() != null && 
            !equipmentDetails.getSerialNumber().equals(equipment.getSerialNumber()) && 
            equipmentRepository.existsBySerialNumber(equipmentDetails.getSerialNumber())) {
            throw new RuntimeException("Serial number already exists: " + equipmentDetails.getSerialNumber());
        }
        
        equipment.setAssetTag(equipmentDetails.getAssetTag());
        equipment.setSerialNumber(equipmentDetails.getSerialNumber());
        equipment.setCategory(equipmentDetails.getCategory());
        equipment.setBrand(equipmentDetails.getBrand());
        equipment.setModel(equipmentDetails.getModel());
        equipment.setSpecifications(equipmentDetails.getSpecifications());
        equipment.setPurchaseDate(equipmentDetails.getPurchaseDate());
        equipment.setPurchaseCost(equipmentDetails.getPurchaseCost());
        equipment.setWarrantyExpiry(equipmentDetails.getWarrantyExpiry());
        equipment.setStatus(equipmentDetails.getStatus());
        equipment.setConditionStatus(equipmentDetails.getConditionStatus());
        equipment.setLocation(equipmentDetails.getLocation());
        equipment.setNotes(equipmentDetails.getNotes());
        
        return equipmentRepository.save(equipment);
    }
    
    public void deleteEquipment(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));
        equipment.setActive(false);
        equipmentRepository.save(equipment);
    }
    
    public List<Equipment> searchEquipment(String keyword) {
        return equipmentRepository.searchEquipment(keyword);
    }
    
    public List<Equipment> findEquipmentByCategory(EquipmentCategory category) {
        return equipmentRepository.findByCategory(category);
    }
    
    public List<Equipment> findEquipmentByStatus(Equipment.EquipmentStatus status) {
        return equipmentRepository.findByStatus(status);
    }
    
    public List<Equipment> findEquipmentByCondition(Equipment.ConditionStatus condition) {
        return equipmentRepository.findByConditionStatus(condition);
    }
    
    public List<Equipment> findEquipmentByLocation(String location) {
        return equipmentRepository.findByLocation(location);
    }
    
    public List<Equipment> findEquipmentByBrand(String brand) {
        return equipmentRepository.findByBrand(brand);
    }
    
    public List<Equipment> findEquipmentWithWarrantyExpiringBefore(LocalDate date) {
        return equipmentRepository.findEquipmentWithWarrantyExpiringBefore(date);
    }
    
    public long getTotalEquipmentCount() {
        return equipmentRepository.count();
    }
    
    public long getEquipmentCountByStatus(Equipment.EquipmentStatus status) {
        return equipmentRepository.countByStatus(status);
    }
    
    public long getEquipmentCountByCategory(Long categoryId) {
        return equipmentRepository.countByCategory(categoryId);
    }
    
    public BigDecimal getTotalEquipmentValue() {
        BigDecimal totalValue = equipmentRepository.sumTotalValue();
        return totalValue != null ? totalValue : BigDecimal.ZERO;
    }
    
    public BigDecimal getEquipmentValueByStatus(Equipment.EquipmentStatus status) {
        BigDecimal value = equipmentRepository.sumValueByStatus(status);
        return value != null ? value : BigDecimal.ZERO;
    }
    
    public boolean isEquipmentAvailable(String assetTag) {
        Optional<Equipment> equipment = equipmentRepository.findByAssetTag(assetTag);
        return equipment.isPresent() && 
               equipment.get().getStatus() == Equipment.EquipmentStatus.AVAILABLE &&
               equipment.get().getActive();
    }
}
