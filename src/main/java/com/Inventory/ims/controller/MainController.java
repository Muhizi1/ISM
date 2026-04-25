package com.Inventory.ims.controller;

import com.Inventory.ims.model.*;
import com.Inventory.ims.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    
    @Autowired
    private EquipmentService equipmentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AssignmentService assignmentService;
    
    @Autowired
    private MaintenanceRecordService maintenanceService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Get dashboard statistics
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalEquipment", equipmentService.getTotalEquipmentCount());
            stats.put("availableEquipment", equipmentService.getEquipmentCountByStatus(Equipment.EquipmentStatus.AVAILABLE));
            stats.put("assignedEquipment", equipmentService.getEquipmentCountByStatus(Equipment.EquipmentStatus.ASSIGNED));
            stats.put("underMaintenance", equipmentService.getEquipmentCountByStatus(Equipment.EquipmentStatus.UNDER_MAINTENANCE));
            stats.put("totalUsers", userService.getTotalUsersCount());
            stats.put("totalAssignments", assignmentService.getTotalAssignmentsCount());
            stats.put("activeAssignments", assignmentService.getAssignmentsCountByStatus(Assignment.AssignmentStatus.ACTIVE));
            stats.put("overdueAssignments", assignmentService.findOverdueAssignments().size());
            stats.put("totalMaintenanceRecords", maintenanceService.getTotalMaintenanceRecordsCount());
            stats.put("scheduledMaintenance", maintenanceService.findMaintenanceRecordsNeedingAttention().size());
        } catch (Exception e) {
            System.err.println("Error collecting home stats: " + e.getMessage());
        }
        
        // Get recent activities
        List<Assignment> recentAssignments = List.of();
        List<MaintenanceRecord> recentMaintenance = List.of();
        try {
            recentAssignments = assignmentService.findAssignmentsByDateRange(
                java.time.LocalDateTime.now().minusDays(7), 
                java.time.LocalDateTime.now()
            );
            recentMaintenance = maintenanceService.findMaintenanceRecordsByDateRange(
                java.time.LocalDate.now().minusDays(7), 
                java.time.LocalDate.now()
            );
        } catch (Exception e) {
            System.err.println("Error collecting recent activities: " + e.getMessage());
        }
        
        model.addAttribute("stats", stats);
        model.addAttribute("recentAssignments", recentAssignments);
        model.addAttribute("recentMaintenance", recentMaintenance);
        
        return "index";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User currentUser = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("currentUser", currentUser);
        
        // Get dashboard statistics
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalEquipment", equipmentService.getTotalEquipmentCount());
            stats.put("availableEquipment", equipmentService.getEquipmentCountByStatus(Equipment.EquipmentStatus.AVAILABLE));
            stats.put("assignedEquipment", equipmentService.getEquipmentCountByStatus(Equipment.EquipmentStatus.ASSIGNED));
            stats.put("underMaintenance", equipmentService.getEquipmentCountByStatus(Equipment.EquipmentStatus.UNDER_MAINTENANCE));
            stats.put("totalUsers", userService.getTotalUsersCount());
            stats.put("totalAssignments", assignmentService.getTotalAssignmentsCount());
            stats.put("activeAssignments", assignmentService.getAssignmentsCountByStatus(Assignment.AssignmentStatus.ACTIVE));
            stats.put("overdueAssignments", assignmentService.findOverdueAssignments().size());
            stats.put("totalMaintenanceRecords", maintenanceService.getTotalMaintenanceRecordsCount());
            stats.put("scheduledMaintenance", maintenanceService.findMaintenanceRecordsNeedingAttention().size());
        } catch (Exception e) {
            // Log error and provide empty/partial stats to avoid 500 error
            System.err.println("Error collecting dashboard stats: " + e.getMessage());
        }
        
        model.addAttribute("stats", stats);
        
        // Get recent activities for dashboard
        List<Assignment> recentAssignments = List.of();
        List<MaintenanceRecord> recentMaintenance = List.of();
        try {
            recentAssignments = assignmentService.findActiveAssignmentsOrderedByReturnDate();
            if (recentAssignments.size() > 5) recentAssignments = recentAssignments.subList(0, 5);
            
            recentMaintenance = maintenanceService.findMaintenanceRecordsNeedingAttention();
            if (recentMaintenance.size() > 5) recentMaintenance = recentMaintenance.subList(0, 5);
        } catch (Exception e) {
            System.err.println("Error collecting dashboard activities: " + e.getMessage());
        }
        
        model.addAttribute("recentAssignments", recentAssignments);
        model.addAttribute("recentMaintenance", recentMaintenance);
        
        // Add user-specific data
        if (currentUser.getRole() == User.UserRole.USER) {
            List<Assignment> userAssignments = assignmentService.findAssignmentsByUser(currentUser);
            model.addAttribute("userAssignments", userAssignments);
        }
        
        return "dashboard";
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User currentUser = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", currentUser);
        return "profile";
    }
}
