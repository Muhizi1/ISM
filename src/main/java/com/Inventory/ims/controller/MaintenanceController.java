package com.Inventory.ims.controller;

import com.Inventory.ims.model.*;
import com.Inventory.ims.service.*;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {
    
    @Autowired
    private MaintenanceRecordService maintenanceService;
    
    @Autowired
    private EquipmentService equipmentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuditTrailService auditTrailService;
    
    @GetMapping
    public String listMaintenanceRecords(Model model, @RequestParam(required = false) String search) {
        List<MaintenanceRecord> records;
        if (search != null && !search.trim().isEmpty()) {
            records = maintenanceService.searchMaintenanceRecords(search);
            model.addAttribute("search", search);
        } else {
            records = maintenanceService.findAllMaintenanceRecords();
        }
        
        model.addAttribute("records", records);
        model.addAttribute("types", MaintenanceRecord.MaintenanceType.values());
        return "maintenance/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("record", new MaintenanceRecord());
        model.addAttribute("equipment", equipmentService.findAllEquipment());
        model.addAttribute("types", MaintenanceRecord.MaintenanceType.values());
        return "maintenance/add";
    }
    
    @PostMapping("/add")
    public String addMaintenanceRecord(@Valid @ModelAttribute("record") MaintenanceRecord record,
                                      BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("equipment", equipmentService.findAllEquipment());
            model.addAttribute("types", MaintenanceRecord.MaintenanceType.values());
            return "maintenance/add";
        }
        
        try {
            // Set the current user as created by
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            record.setCreatedBy(currentUser);
            
            MaintenanceRecord savedRecord = maintenanceService.createMaintenanceRecord(record);
            
            // Log the action
            auditTrailService.logEquipmentAction(
                savedRecord.getEquipment(), 
                "MAINTENANCE", 
                "Maintenance record created: " + savedRecord.getDescription(),
                null,
                "Type: " + savedRecord.getMaintenanceType() + ", Cost: " + savedRecord.getCost(),
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Maintenance record added successfully!");
            return "redirect:/maintenance";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("equipment", equipmentService.findAllEquipment());
            model.addAttribute("types", MaintenanceRecord.MaintenanceType.values());
            return "maintenance/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        MaintenanceRecord record = maintenanceService.findMaintenanceRecordById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found"));
        
        model.addAttribute("record", record);
        model.addAttribute("equipment", equipmentService.findAllEquipment());
        model.addAttribute("types", MaintenanceRecord.MaintenanceType.values());
        return "maintenance/edit";
    }
    
    @PostMapping("/edit/{id}")
    public String updateMaintenanceRecord(@PathVariable Long id, @Valid @ModelAttribute("record") MaintenanceRecord record,
                                        BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("equipment", equipmentService.findAllEquipment());
            model.addAttribute("types", MaintenanceRecord.MaintenanceType.values());
            return "maintenance/edit";
        }
        
        try {
            // Get old values for audit trail
            MaintenanceRecord oldRecord = maintenanceService.findMaintenanceRecordById(id).orElse(null);
            
            // Set the current user as created by
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            record.setCreatedBy(currentUser);
            
            MaintenanceRecord updatedRecord = maintenanceService.updateMaintenanceRecord(id, record);
            
            // Log the action
            auditTrailService.logEquipmentAction(
                updatedRecord.getEquipment(), 
                "MAINTENANCE_UPDATE", 
                "Maintenance record updated",
                oldRecord != null ? "Description: " + oldRecord.getDescription() : null,
                "Description: " + updatedRecord.getDescription(),
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Maintenance record updated successfully!");
            return "redirect:/maintenance";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("equipment", equipmentService.findAllEquipment());
            model.addAttribute("types", MaintenanceRecord.MaintenanceType.values());
            return "maintenance/edit";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteMaintenanceRecord(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            MaintenanceRecord record = maintenanceService.findMaintenanceRecordById(id)
                    .orElseThrow(() -> new RuntimeException("Maintenance record not found"));
            
            maintenanceService.deleteMaintenanceRecord(id);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logEquipmentAction(
                record.getEquipment(), 
                "MAINTENANCE_DELETE", 
                "Maintenance record deleted: " + record.getDescription(),
                "Type: " + record.getMaintenanceType() + ", Cost: " + record.getCost(),
                null,
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Maintenance record deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/maintenance";
    }
    
    @GetMapping("/view/{id}")
    public String viewMaintenanceRecord(@PathVariable Long id, Model model) {
        MaintenanceRecord record = maintenanceService.findMaintenanceRecordById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found"));
        
        model.addAttribute("record", record);
        return "maintenance/view";
    }
    
    @GetMapping("/scheduled")
    public String listScheduledMaintenance(Model model) {
        List<MaintenanceRecord> scheduledRecords = maintenanceService.findMaintenanceRecordsNeedingAttention();
        model.addAttribute("records", scheduledRecords);
        model.addAttribute("title", "Scheduled Maintenance");
        return "maintenance/list";
    }
    
    @GetMapping("/equipment/{equipmentId}")
    public String listMaintenanceByEquipment(@PathVariable Long equipmentId, Model model) {
        Equipment equipment = equipmentService.findEquipmentById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        
        List<MaintenanceRecord> records = maintenanceService.findMaintenanceRecordsByEquipment(equipment);
        model.addAttribute("records", records);
        model.addAttribute("title", "Maintenance for " + equipment.getAssetTag());
        model.addAttribute("equipment", equipment);
        return "maintenance/list";
    }
    
    @GetMapping("/complete/{id}")
    public String completeMaintenance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            MaintenanceRecord record = maintenanceService.findMaintenanceRecordById(id)
                    .orElseThrow(() -> new RuntimeException("Maintenance record not found"));
            
            maintenanceService.completeMaintenance(id);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logEquipmentAction(
                record.getEquipment(), 
                "MAINTENANCE_COMPLETE", 
                "Maintenance completed for: " + record.getDescription(),
                null,
                "Equipment status changed to AVAILABLE",
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Maintenance marked as completed!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/maintenance";
    }
    
    @GetMapping("/filter")
    public String filterMaintenanceRecords(@RequestParam(required = false) String type,
                                         @RequestParam(required = false) String performedBy,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                         Model model) {
        List<MaintenanceRecord> records;
        
        if (type != null && !type.isEmpty()) {
            records = maintenanceService.findMaintenanceRecordsByType(MaintenanceRecord.MaintenanceType.valueOf(type));
        } else if (performedBy != null && !performedBy.isEmpty()) {
            records = maintenanceService.findMaintenanceRecordsByPerformedBy(performedBy);
        } else if (startDate != null && endDate != null) {
            records = maintenanceService.findMaintenanceRecordsByDateRange(startDate, endDate);
        } else {
            records = maintenanceService.findAllMaintenanceRecords();
        }
        
        model.addAttribute("records", records);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedPerformedBy", performedBy);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("types", MaintenanceRecord.MaintenanceType.values());
        return "maintenance/list";
    }
}
