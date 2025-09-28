package com.koushaljhait.controller.student;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentDashboardController {
    
    @GetMapping("/dashboard")
    public String studentDashboard(Model model) {
        model.addAttribute("pageTitle", "Student Dashboard");
        return "student/dashboard";
    }
}