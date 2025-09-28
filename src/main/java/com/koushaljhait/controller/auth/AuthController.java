package com.koushaljhait.controller.auth;

import com.koushaljhait.service.auth.AuthService;
import com.koushaljhait.service.common.CaptchaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    
    private final CaptchaService captchaService;
    private final AuthService authService;
    
    public AuthController(CaptchaService captchaService, AuthService authService) {
        this.captchaService = captchaService;
        this.authService = authService;
    }
    
    @GetMapping("/auth/login")
    public String showLoginForm(Model model, 
                               @RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "captchaError", required = false) String captchaError,
                               @RequestParam(value = "refresh", required = false) Boolean refresh) {
        
        // Always generate new captcha when page loads or refresh requested
        model.addAttribute("captcha", captchaService.generateCaptcha());
        
        if (error != null) {
            model.addAttribute("error", "Invalid credentials or captcha");
        }
        if (captchaError != null) {
            model.addAttribute("captchaError", "Invalid captcha code");
        }
        return "login";
    }
    
    // ... rest of your existing methods remain the same ...
    @GetMapping("/auth/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("captcha", captchaService.generateCaptcha());
        return "auth/forgot-password";
    }
    
    @PostMapping("/auth/forgot-password")
    public String processForgotPassword(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String captchaInput,
            @RequestParam String captcha,
            RedirectAttributes redirectAttributes) {
        
        // Validate captcha
        if (!captchaService.validateCaptcha(captchaInput)) {
            redirectAttributes.addFlashAttribute("error", "Invalid captcha code");
            return "redirect:/auth/forgot-password";
        }
        
        try {
            // Send OTP to email
            boolean otpSent = authService.initiatePasswordReset(username, email);
            
            if (otpSent) {
                redirectAttributes.addFlashAttribute("username", username);
                redirectAttributes.addFlashAttribute("email", email);
                redirectAttributes.addFlashAttribute("success", "OTP sent to your email!");
                return "redirect:/auth/verify-otp";
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to send OTP. Please try again.");
                return "redirect:/auth/forgot-password";
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/forgot-password";
        }
    }
    
    @GetMapping("/auth/verify-otp")
    public String showVerifyOtpForm(Model model) {
        // Check for both username and email
        if (!model.containsAttribute("username") && !model.containsAttribute("email")) {
            return "redirect:/auth/forgot-password";
        }
        return "auth/verify-otp";
    }
    
    @PostMapping("/auth/verify-otp")
    public String verifyOtp(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String otp,
            RedirectAttributes redirectAttributes) {
        
        // Use email for OTP validation
        if (authService.validateOtp(email, otp)) {
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("success", "OTP verified successfully!");
            return "redirect:/auth/reset-password";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired OTP");
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/auth/verify-otp";
        }
    }
    
    @GetMapping("/auth/reset-password")
    public String showResetPasswordForm(Model model) {
        // Check for both username and email
        if (!model.containsAttribute("username") && !model.containsAttribute("email")) {
            return "redirect:/auth/forgot-password";
        }
        return "auth/reset-password";
    }
    
    @PostMapping("/auth/reset-password")
    public String resetPassword(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/auth/reset-password";
        }
        
        try {
            // Use email for password reset
            authService.resetPassword(email, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password reset successfully! You can now login with your new password.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Password reset failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/auth/reset-password";
        }
    }
}