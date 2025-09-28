package com.koushaljhait.controller.teacher;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teacher")
public class TeacherDashboardController {
    
    @GetMapping("/dashboard")
    public String teacherDashboard(Model model) {
        model.addAttribute("pageTitle", "Teacher Dashboard");
        return "teacher/dashboard";
    }
}