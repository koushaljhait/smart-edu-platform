package com.koushaljhait.controller.common;

import com.koushaljhait.service.common.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class MaintenanceController {
    
    @Autowired
    private MaintenanceService maintenanceService;
    
    @Value("${app.maintenance.daily-start-time:23:40}")
    private String dailyStartTime;
    
    @Value("${app.maintenance.daily-end-time:00:30}")
    private String dailyEndTime;
    
    @GetMapping("/maintenance")
    public String maintenancePage(@RequestParam(required = false) String error, Model model) {
        MaintenanceService.MaintenanceStatus status = maintenanceService.getMaintenanceStatus();
        
        model.addAttribute("isActive", status.active);
        model.addAttribute("isManual", status.manualOverride);
        model.addAttribute("manualEndTime", status.manualEndTime);
        model.addAttribute("nextWindow", status.nextWindow);
        model.addAttribute("dailyStartTime", dailyStartTime);
        model.addAttribute("dailyEndTime", dailyEndTime);
        model.addAttribute("error", error);
        
        if (status.manualOverride && status.manualEndTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            model.addAttribute("extendedUntil", status.manualEndTime.format(formatter));
        }
        
        return "maintenance";
    }
    
    // API for super admin to extend maintenance
    @PostMapping("/api/maintenance/extend")
    @ResponseBody
    public String extendMaintenance(@RequestParam int minutes) {
        maintenanceService.extendMaintenance(minutes);
        return "Maintenance extended by " + minutes + " minutes";
    }
    
    // API for super admin to end maintenance
    @PostMapping("/api/maintenance/end")
    @ResponseBody
    public String endMaintenance() {
        maintenanceService.endMaintenance();
        return "Maintenance ended manually";
    }
    
    // API to check maintenance status (for AJAX calls)
    @GetMapping("/api/maintenance/status")
    @ResponseBody
    public MaintenanceService.MaintenanceStatus getMaintenanceStatus() {
        return maintenanceService.getMaintenanceStatus();
    }
}