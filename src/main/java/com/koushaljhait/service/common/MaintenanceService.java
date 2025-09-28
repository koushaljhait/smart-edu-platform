package com.koushaljhait.service.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Service
public class MaintenanceService {
    
    @Value("${app.maintenance.auto-enabled:true}")
    private boolean autoEnabled;
    
    @Value("${app.maintenance.daily-start-time:23:40}")
    private String dailyStartTime;
    
    @Value("${app.maintenance.daily-end-time:00:30}")
    private String dailyEndTime;
    
    @Value("${app.maintenance.manual-extension-minutes:0}")
    private int manualExtensionMinutes;
    
    @Value("${app.maintenance.super-admin-allowed:true}")
    private boolean superAdminAllowed;
    
    @Value("${app.maintenance.super-admin:superadmin}")
    private String superAdminUsername;
    
    private boolean manualOverride = false;
    private LocalDateTime manualEndTime = null;
    
    // Check if maintenance is currently active
    public boolean isMaintenanceActive() {
        if (manualOverride && manualEndTime != null) {
            return LocalDateTime.now().isBefore(manualEndTime);
        }
        
        if (!autoEnabled) {
            return false;
        }
        
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.parse(dailyStartTime);
        LocalTime endTime = LocalTime.parse(dailyEndTime);
        
        // Handle overnight maintenance (23:40 to 00:30)
        if (endTime.isBefore(startTime)) {
            return now.isAfter(startTime) || now.isBefore(endTime);
        } else {
            return now.isAfter(startTime) && now.isBefore(endTime);
        }
    }
    
    // Check if user can bypass maintenance (super admin)
    public boolean canBypassMaintenance(String username) {
        return superAdminAllowed && superAdminUsername.equals(username);
    }
    
    // Extend maintenance manually
    public void extendMaintenance(int additionalMinutes) {
        this.manualOverride = true;
        this.manualEndTime = LocalDateTime.now().plusMinutes(additionalMinutes);
    }
    
    // End maintenance manually
    public void endMaintenance() {
        this.manualOverride = false;
        this.manualEndTime = null;
    }
    
    // Get maintenance status info
    public MaintenanceStatus getMaintenanceStatus() {
        return new MaintenanceStatus(
            isMaintenanceActive(),
            manualOverride,
            manualEndTime,
            getNextMaintenanceWindow()
        );
    }
    
    // Auto-reset manual extension at scheduled end time
    @Scheduled(cron = "0 31 0 * * ?") // Runs at 00:31 AM daily
    public void resetManualOverride() {
        if (manualOverride && manualEndTime != null && 
            LocalDateTime.now().isAfter(manualEndTime)) {
            manualOverride = false;
            manualEndTime = null;
        }
    }
    
    private String getNextMaintenanceWindow() {
        return "Daily: " + dailyStartTime + " to " + dailyEndTime;
    }
    
    public static class MaintenanceStatus {
        public final boolean active;
        public final boolean manualOverride;
        public final LocalDateTime manualEndTime;
        public final String nextWindow;
        
        public MaintenanceStatus(boolean active, boolean manualOverride, 
                               LocalDateTime manualEndTime, String nextWindow) {
            this.active = active;
            this.manualOverride = manualOverride;
            this.manualEndTime = manualEndTime;
            this.nextWindow = nextWindow;
        }
    }
}