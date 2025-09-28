package com.koushaljhait.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/superadmin")
public class SuperAdminController {
    
    @GetMapping("/dashboard")
    public String superAdminDashboard() {
        return "admin/dashboard"; // Use same dashboard as admin
    }
}