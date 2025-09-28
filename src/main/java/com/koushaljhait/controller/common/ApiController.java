package com.koushaljhait.controller.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koushaljhait.dto.common.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiController {
    
    @GetMapping("/api/health")
    public ApiResponse healthCheck() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("application", "Smart Edu Platform");
        healthData.put("company", "Koushal Jha I Technologies");
        
        return ApiResponse.success("Application is healthy", healthData);
    }
    
    // CHANGE: Remove or comment out the root path mapping
    // @GetMapping("/")
    // public String welcome() {
    //     return "Welcome to Smart Edu Platform v1.0.0.0 - Koushal Jha I Technologies";
    // }
    
    @GetMapping("/api/status")
    public ApiResponse systemStatus() {
        Map<String, Object> statusData = new HashMap<>();
        statusData.put("phase", "1 - Foundation");
        statusData.put("status", "Basic setup complete");
        
        return ApiResponse.success("System status", statusData);
    }
    
    // ADD: New welcome endpoint at a different path
    @GetMapping("/api/welcome")
    public String apiWelcome() {
        return "Welcome to Smart Edu Platform v1.0.0.0 - Koushal Jha I Technologies";
    }
}