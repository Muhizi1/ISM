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
@RequestMapping("/assignments")
public class AssignmentController {
    
    @Autowired
    private AssignmentService assignmentService;
    
    @Autowired
    private EquipmentService equipmentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuditTrailService auditTrailService;
    
    @GetMapping
    public String listAssignments(Model model, @RequestParam(required = false) String search) {
        List<Assignment> assignments;
        if (search != null && !search.trim().isEmpty()) {
            assignments = assignmentService.searchAssignments(search);
            model.addAttribute("search", search);
        } else {
            assignments = assignmentService.findAllAssignments();
        }
        
        model.addAttribute("assignments", assignments);
        model.addAttribute("statuses", Assignment.AssignmentStatus.values());
        return "assignments/list";
    }
    
    @GetMapping("/assign")
    public String showAssignForm(@RequestParam(required = false) Long equipmentId, Model model) {
        if (equipmentId != null) {
            model.addAttribute("selectedEquipment", equipmentId);
        }
        model.addAttribute("availableEquipment", equipmentService.findEquipmentByStatus(Equipment.EquipmentStatus.AVAILABLE));
        model.addAttribute("users", userService.findAllUsers());
        return "assignments/assign";
    }
    
    @PostMapping("/assign")
    public String assignEquipment(@RequestParam("equipmentId") Long equipmentId,
                                @RequestParam("userId") Long userId,
                                @RequestParam("expectedReturnDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expectedReturnDate,
                                RedirectAttributes redirectAttributes) {
        try {
            Equipment equipment = equipmentService.findEquipmentById(equipmentId)
                    .orElseThrow(() -> new RuntimeException("Equipment not found"));
            
            User user = userService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User assignedBy = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            
            Assignment assignment = assignmentService.assignEquipment(equipment, user, assignedBy, expectedReturnDate);
            
            // Log the action
            auditTrailService.logAssignmentAction(
                equipment, 
                user, 
                "ASSIGN", 
                "Equipment assigned to user. Expected return: " + expectedReturnDate,
                assignedBy
            );
            
            redirectAttributes.addFlashAttribute("success", "Equipment assigned successfully!");
            return "redirect:/assignments";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/assignments/assign";
        }
    }
    
    @GetMapping("/return/{id}")
    public String showReturnForm(@PathVariable Long id, Model model) {
        Assignment assignment = assignmentService.findAssignmentById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        if (assignment.getStatus() != Assignment.AssignmentStatus.ACTIVE) {
            throw new RuntimeException("This assignment is not active and cannot be returned");
        }
        
        model.addAttribute("assignment", assignment);
        model.addAttribute("conditions", Equipment.ConditionStatus.values());
        return "assignments/return";
    }
    
    @PostMapping("/return/{id}")
    public String returnEquipment(@PathVariable Long id,
                                @RequestParam("returnCondition") Equipment.ConditionStatus returnCondition,
                                @RequestParam(value = "returnNotes", required = false) String returnNotes,
                                RedirectAttributes redirectAttributes) {
        try {
            Assignment assignment = assignmentService.returnEquipment(id, returnCondition, returnNotes);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logAssignmentAction(
                assignment.getEquipment(), 
                assignment.getUser(), 
                "RETURN", 
                "Equipment returned. Condition: " + returnCondition + ". Notes: " + (returnNotes != null ? returnNotes : ""),
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Equipment returned successfully!");
            return "redirect:/assignments";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/assignments/return/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteAssignment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Assignment assignment = assignmentService.findAssignmentById(id)
                    .orElseThrow(() -> new RuntimeException("Assignment not found"));
            
            assignmentService.deleteAssignment(id);
            
            // Log the action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findUserByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            auditTrailService.logAssignmentAction(
                assignment.getEquipment(), 
                assignment.getUser(), 
                "DELETE", 
                "Assignment deleted",
                currentUser
            );
            
            redirectAttributes.addFlashAttribute("success", "Assignment deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/assignments";
    }
    
    @GetMapping("/view/{id}")
    public String viewAssignment(@PathVariable Long id, Model model) {
        Assignment assignment = assignmentService.findAssignmentById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        model.addAttribute("assignment", assignment);
        return "assignments/view";
    }
    
    @GetMapping("/overdue")
    public String listOverdueAssignments(Model model) {
        List<Assignment> overdueAssignments = assignmentService.findOverdueAssignments();
        model.addAttribute("assignments", overdueAssignments);
        model.addAttribute("title", "Overdue Assignments");
        return "assignments/list";
    }
    
    @GetMapping("/active")
    public String listActiveAssignments(Model model) {
        List<Assignment> activeAssignments = assignmentService.findActiveAssignmentsOrderedByReturnDate();
        model.addAttribute("assignments", activeAssignments);
        model.addAttribute("title", "Active Assignments");
        return "assignments/list";
    }
    
    @GetMapping("/my-assignments")
    public String listMyAssignments(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findUserByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Assignment> myAssignments = assignmentService.findAssignmentsByUser(currentUser);
        model.addAttribute("assignments", myAssignments);
        model.addAttribute("title", "My Assignments");
        return "assignments/list";
    }
    
    @GetMapping("/filter")
    public String filterAssignments(@RequestParam(required = false) String status,
                                   @RequestParam(required = false) Long userId,
                                   @RequestParam(required = false) Long equipmentId,
                                   Model model) {
        List<Assignment> assignments;
        
        if (status != null && !status.isEmpty()) {
            assignments = assignmentService.findAssignmentsByStatus(Assignment.AssignmentStatus.valueOf(status));
        } else if (userId != null) {
            User user = userService.findUserById(userId).orElse(null);
            assignments = assignmentService.findAssignmentsByUser(user);
        } else if (equipmentId != null) {
            Equipment equipment = equipmentService.findEquipmentById(equipmentId).orElse(null);
            assignments = assignmentService.findAssignmentsByEquipment(equipment);
        } else {
            assignments = assignmentService.findAllAssignments();
        }
        
        model.addAttribute("assignments", assignments);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedUser", userId);
        model.addAttribute("selectedEquipment", equipmentId);
        model.addAttribute("statuses", Assignment.AssignmentStatus.values());
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("equipment", equipmentService.findAllEquipment());
        return "assignments/list";
    }
}
