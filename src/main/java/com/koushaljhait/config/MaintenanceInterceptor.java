package com.koushaljhait.config;

import com.koushaljhait.service.common.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;

@Component
public class MaintenanceInterceptor implements HandlerInterceptor {
    
    @Autowired
    private MaintenanceService maintenanceService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        // Skip maintenance check for certain paths
        if (isExcludedPath(request.getRequestURI())) {
            return true;
        }
        
        // Check if maintenance is active
        if (maintenanceService.isMaintenanceActive()) {
            // Check if user is super admin
            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                String username = principal.getName();
                if (maintenanceService.canBypassMaintenance(username)) {
                    return true; // Super admin can proceed
                }
            }
            
            // For login attempts during maintenance
            if (request.getRequestURI().equals("/auth/login") && 
                "POST".equalsIgnoreCase(request.getMethod())) {
                
                // Allow super admin to login
                String username = request.getParameter("username");
                if (username != null && maintenanceService.canBypassMaintenance(username)) {
                    return true;
                }
                
                // Redirect others to maintenance page
                response.sendRedirect("/maintenance?error=login-disabled");
                return false;
            }
            
            // Redirect to maintenance page
            if (!request.getRequestURI().equals("/maintenance")) {
                response.sendRedirect("/maintenance");
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isExcludedPath(String path) {
        return path.equals("/") || 
               path.equals("/maintenance") || 
               path.startsWith("/static/") || 
               path.startsWith("/css/") || 
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.equals("/api/maintenance/status") || // API for checking status
               path.equals("/favicon.ico");
    }
}