package com.Inventory.ims.controller;

import com.Inventory.ims.service.AssignmentService;
import com.Inventory.ims.service.EquipmentService;
import com.Inventory.ims.service.MaintenanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private MaintenanceRecordService maintenanceService;

    @GetMapping
    public String showReports(Model model) {
        Map<String, Object> reportStats = new HashMap<>();
        try {
            reportStats.put("totalEquipment", equipmentService.getTotalEquipmentCount());
            reportStats.put("totalAssignments", assignmentService.getTotalAssignmentsCount());
            reportStats.put("totalMaintenance", maintenanceService.getTotalMaintenanceRecordsCount());
            // Add more specific report data if needed
        } catch (Exception e) {
            System.err.println("Error generating report stats: " + e.getMessage());
        }
        
        model.addAttribute("stats", reportStats);
        model.addAttribute("title", "System Reports");
        return "reports/index";
    }
}
