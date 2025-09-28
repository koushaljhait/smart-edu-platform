package com.koushaljhait.controller.auth;

import com.koushaljhait.dto.auth.RegistrationRequest;
import com.koushaljhait.model.user.User;
import com.koushaljhait.service.auth.EmailVerificationService;
import com.koushaljhait.service.auth.RegistrationService;
import com.koushaljhait.service.common.CaptchaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class RegistrationController {
    
    private final RegistrationService registrationService;
    private final CaptchaService captchaService;
    private final EmailVerificationService emailVerificationService;
    
    public RegistrationController(RegistrationService registrationService, 
                                 CaptchaService captchaService,
                                 EmailVerificationService emailVerificationService) {
        this.registrationService = registrationService;
        this.captchaService = captchaService;
        this.emailVerificationService = emailVerificationService;
    }
    
    @GetMapping("/register/teacher")
    public String showTeacherRegistrationForm(Model model,
                                            @RequestParam(value = "refresh", required = false) Boolean refresh) {
        // Always generate new captcha when page loads or refresh requested
        model.addAttribute("captcha", captchaService.generateCaptcha());
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register-teacher";
    }
    
    @GetMapping("/register/student")
    public String showStudentRegistrationForm(Model model,
                                            @RequestParam(value = "refresh", required = false) Boolean refresh) {
        // Always generate new captcha when page loads or refresh requested
        model.addAttribute("captcha", captchaService.generateCaptcha());
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register-student";
    }
    
    // NEW: Check email availability endpoint
    @PostMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Basic email validation
            if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                response.put("available", false);
                response.put("message", "Invalid email format");
                return ResponseEntity.ok(response);
            }
            
            // Check if email already registered
            if (emailVerificationService.isEmailAlreadyRegistered(email)) {
                response.put("available", false);
                response.put("message", "This email is already registered");
                return ResponseEntity.ok(response);
            }
            
            response.put("available", true);
            response.put("message", "Email is available");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("available", false);
            response.put("message", "Error checking email: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // New endpoint for sending verification OTP
    @PostMapping("/send-verification-otp")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendVerificationOtp(
            @RequestParam String email,
            @RequestParam String firstName) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if email is already registered
            if (emailVerificationService.isEmailAlreadyRegistered(email)) {
                response.put("success", false);
                response.put("message", "This email is already registered");
                return ResponseEntity.ok(response);
            }
            
            boolean otpSent = emailVerificationService.sendVerificationOtp(email, firstName);
            
            if (otpSent) {
                response.put("success", true);
                response.put("message", "OTP sent successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to send OTP. Please try again.");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // New endpoint for verifying email OTP
    @PostMapping("/verify-email-otp")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyEmailOtp(
            @RequestParam String email,
            @RequestParam String otp) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean verified = emailVerificationService.verifyOtp(email, otp);
            
            if (verified) {
                response.put("success", true);
                response.put("message", "Email verified successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid or expired OTP");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/register/teacher")
    public String registerTeacher(
            @ModelAttribute RegistrationRequest registrationRequest,
            @RequestParam String confirmPassword,
            @RequestParam(required = false) String emailOtp,
            @RequestParam String captchaInput,
            @RequestParam String captcha,
            RedirectAttributes redirectAttributes) {
        
        // Validate captcha
        if (!captchaService.validateCaptcha(captchaInput)) {
            redirectAttributes.addFlashAttribute("error", "Invalid captcha code");
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/auth/register/teacher";
        }
        
        // Validate email verification
        if (!emailVerificationService.isEmailVerified(registrationRequest.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Please verify your email address first");
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/auth/register/teacher";
        }
        
        // Validate password match
        if (!registrationRequest.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/auth/register/teacher";
        }
        
        try {
            // Register teacher
            User user = registrationService.registerTeacher(registrationRequest);
            
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! Please check your email for account details.");
            return "redirect:/auth/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/auth/register/teacher";
        }
    }
    
    @PostMapping("/register/student")
    public String registerStudent(
            @ModelAttribute RegistrationRequest registrationRequest,
            @RequestParam String confirmPassword,
            @RequestParam(required = false) String emailOtp,
            @RequestParam String captchaInput,
            @RequestParam String captcha,
            RedirectAttributes redirectAttributes) {
        
        // Validate captcha
        if (!captchaService.validateCaptcha(captchaInput)) {
            redirectAttributes.addFlashAttribute("error", "Invalid captcha code");
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/auth/register/student";
        }
        
        // Validate email verification
        if (!emailVerificationService.isEmailVerified(registrationRequest.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Please verify your email address first");
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/auth/register/student";
        }
        
        // Validate password match
        if (!registrationRequest.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/auth/register/student";
        }
        
        try {
            // Register student
            User user = registrationService.registerStudent(registrationRequest);
            
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! Please check your email for account details.");
            return "redirect:/auth/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/auth/register/student";
        }
    }
}