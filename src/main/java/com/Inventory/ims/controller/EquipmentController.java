package com.Inventory.ims.controller;

import com.Inventory.ims.model.Equipment;
import com.Inventory.ims.model.EquipmentCategory;
import com.Inventory.ims.model.User;
import com.Inventory.ims.service.EquipmentCategoryService;
import com.Inventory.ims.service.EquipmentService;
import com.Inventory.ims.service.AuditTrailService;
import com.Inventory.ims.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/equipment")
public class EquipmentController {
    
    @Autowired
    private EquipmentService equipmentService;
    
    @Autowired
    private EquipmentCategoryService categoryService;
    
    @Autowired
    private AuditTrailService auditTrailService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listEquipment(Model model, @RequestParam(required = false) String search) {
        List<Equipment> equipment;
        if (search != null && !search.trim().isEmpty()) {
            equipment = equipmentService.searchEquipment(search);
            model.addAttribute("search", search);
        } else {
            equipment = equipmentService.findAllEquipment();
        }
        
        model.addAttribute("equipmentList", equipment);
        model.addAttribute("categories", categoryService.findAllCategories());
        
        // Summary Stats
        model.addAttribute("totalEquipment", equipmentService.findAllEquipment().size());
        model.addAttribute("availableCount", equipmentService.findEquipmentByStatus(Equipment.EquipmentStatus.AVAILABLE).size());
        model.addAttribute("assignedCount", equipmentService.findEquipmentByStatus(Equipment.EquipmentStatus.ASSIGNED).size());
        model.addAttribute("maintenanceCount", equipmentService.findEquipmentByStatus(Equipment.EquipmentStatus.UNDER_MAINTENANCE).size());
        
        return "equipment/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("equipment", new Equipment());
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("statuses", Equipment.EquipmentStatus.values());
        model.addAttribute("conditions", Equipment.ConditionStatus.values());
        return "equipment/add";
    }
    
    @PostMapping("/add")
    public String addEquipment(@Valid @ModelAttribute("equipment") Equipment equipment, 
                              BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("statuses", Equipment.EquipmentStatus.values());
            model.addAttribute("conditions", Equipment.ConditionStatus.values());
            return "equipment/add";
        }
        
        try {
            Equipment savedEquipment = equipmentService.createEquipment(equipment);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logEquipmentAction(
                savedEquipment, 
                "CREATE", 
                "New equipment created", 
                null, 
                "Asset Tag: " + savedEquipment.getAssetTag() + ", Brand: " + savedEquipment.getBrand() + ", Model: " + savedEquipment.getModel(),
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Equipment successfully saved to the database!");
            return "redirect:/equipment";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("statuses", Equipment.EquipmentStatus.values());
            model.addAttribute("conditions", Equipment.ConditionStatus.values());
            return "equipment/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Equipment equipment = equipmentService.findEquipmentById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        
        model.addAttribute("equipment", equipment);
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("statuses", Equipment.EquipmentStatus.values());
        model.addAttribute("conditions", Equipment.ConditionStatus.values());
        return "equipment/edit";
    }
    
    @PostMapping("/edit/{id}")
    public String updateEquipment(@PathVariable Long id, @Valid @ModelAttribute("equipment") Equipment equipment, 
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("statuses", Equipment.EquipmentStatus.values());
            model.addAttribute("conditions", Equipment.ConditionStatus.values());
            return "equipment/edit";
        }
        
        try {
            // Get old values for audit trail
            Equipment oldEquipment = equipmentService.findEquipmentById(id).orElse(null);
            
            Equipment updatedEquipment = equipmentService.updateEquipment(id, equipment);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logEquipmentAction(
                updatedEquipment, 
                "UPDATE", 
                "Equipment updated", 
                oldEquipment != null ? "Asset Tag: " + oldEquipment.getAssetTag() + ", Status: " + oldEquipment.getStatus() : null,
                "Asset Tag: " + updatedEquipment.getAssetTag() + ", Status: " + updatedEquipment.getStatus(),
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Equipment successfully updated in the database!");
            return "redirect:/equipment";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("statuses", Equipment.EquipmentStatus.values());
            model.addAttribute("conditions", Equipment.ConditionStatus.values());
            return "equipment/edit";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Equipment equipment = equipmentService.findEquipmentById(id)
                    .orElseThrow(() -> new RuntimeException("Equipment not found"));
            
            equipmentService.deleteEquipment(id);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            auditTrailService.logEquipmentAction(
                equipment, 
                "DELETE", 
                "Equipment deleted", 
                "Asset Tag: " + equipment.getAssetTag(),
                null,
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Equipment deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/equipment";
    }
    
    @GetMapping("/view/{id}")
    public String viewEquipment(@PathVariable Long id, Model model) {
        Equipment equipment = equipmentService.findEquipmentById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        
        model.addAttribute("equipment", equipment);
        return "equipment/view";
    }
    
    @GetMapping("/filter")
    public String filterEquipment(@RequestParam(required = false) Long categoryId,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) String condition,
                                @RequestParam(required = false) String location,
                                Model model) {
        List<Equipment> equipment;
        
        if (categoryId != null) {
            EquipmentCategory category = categoryService.findCategoryById(categoryId).orElse(null);
            equipment = equipmentService.findEquipmentByCategory(category);
        } else if (status != null && !status.isEmpty()) {
            equipment = equipmentService.findEquipmentByStatus(Equipment.EquipmentStatus.valueOf(status));
        } else if (condition != null && !condition.isEmpty()) {
            equipment = equipmentService.findEquipmentByCondition(Equipment.ConditionStatus.valueOf(condition));
        } else if (location != null && !location.isEmpty()) {
            equipment = equipmentService.findEquipmentByLocation(location);
        } else {
            equipment = equipmentService.findAllEquipment();
        }
        
        model.addAttribute("equipment", equipment);
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCondition", condition);
        model.addAttribute("selectedLocation", location);
        return "equipment/list";
    }
}
