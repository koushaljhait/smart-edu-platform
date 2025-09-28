package com.koushaljhait.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @GetMapping("/dashboard")
    public String redirectToRoleBasedDashboard(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                
                if (role.equals("ROLE_STUDENT")) {
                    return "redirect:/student/dashboard";
                } else if (role.equals("ROLE_TEACHER")) {
                    return "redirect:/teacher/dashboard";
                } else {
                    // ADMIN and SUPER_ADMIN both go to admin dashboard
                    return "redirect:/admin/dashboard";
                }
            }
        }
        return "redirect:/auth/login";
    }
}