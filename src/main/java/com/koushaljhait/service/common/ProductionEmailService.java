package com.koushaljhait.service.common;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service
public class ProductionEmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    public ProductionEmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }
    
    // UPDATED: Combined method with password support
    public void sendCombinedWelcomeEmail(String toEmail, String username, String firstName, String userType, String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("username", username);
            context.setVariable("userType", userType);
            context.setVariable("tempPassword", tempPassword);
            context.setVariable("loginUrl", "http://localhost:8080/auth/login");
            context.setVariable("supportEmail", "support@koushaljha.com");
            context.setVariable("companyName", "Koushal Jha I Technologies");
            
            String htmlContent = templateEngine.process("email/combined-welcome-email", context);
            
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Smart Edu Platform - Your " + userType + " Account is Ready!");
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@koushaljha.com", "Koushal Jha I Technologies");
            
            mailSender.send(message);
            System.out.println("✅ Combined welcome email sent successfully to: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send combined welcome email: " + e.getMessage());
            sendConsoleFallbackCombinedWelcomeEmail(toEmail, username, firstName, userType, tempPassword);
        }
    }
    
    // UPDATED: Admin welcome email - use admin-specific template
    public void sendAdminWelcomeEmail(String toEmail, String username, String firstName, String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("username", username);
            context.setVariable("tempPassword", tempPassword);
            context.setVariable("loginUrl", "http://localhost:8080/auth/login");
            context.setVariable("supportEmail", "support@koushaljha.com");
            context.setVariable("companyName", "Koushal Jha I Technologies");
            
            // Use admin-specific template
            String htmlContent = templateEngine.process("email/admin-welcome-email", context);
            
            helper.setTo(toEmail);
            helper.setSubject("Administrator Account Created - Smart Edu Platform");
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@koushaljha.com", "Koushal Jha I Technologies");
            
            mailSender.send(message);
            System.out.println("✅ Admin welcome email sent successfully to: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send admin welcome email: " + e.getMessage());
            sendConsoleFallbackAdminWelcomeEmail(toEmail, username, firstName, tempPassword);
        }
    }
    
    // KEEP EXISTING METHODS (they might be used for other purposes)
    public void sendUsernameEmail(String toEmail, String username, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("username", username);
            context.setVariable("loginUrl", "http://localhost:8080/auth/login");
            context.setVariable("supportEmail", "support@koushaljha.com");
            context.setVariable("companyName", "Koushal Jha I Technologies");
            
            String htmlContent = templateEngine.process("email/username-email", context);
            
            helper.setTo(toEmail);
            helper.setSubject("Your Smart Edu Platform Account Details");
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@koushaljha.com", "Koushal Jha I Technologies");
            
            mailSender.send(message);
            System.out.println("✅ Username email sent successfully to: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send username email: " + e.getMessage());
            sendConsoleFallbackEmail(toEmail, username, firstName, "Username");
        }
    }
    
    public void sendOtpEmail(String toEmail, String otp, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("otp", otp);
            context.setVariable("supportEmail", "support@koushaljha.com");
            context.setVariable("companyName", "Koushal Jha I Technologies");
            
            String htmlContent = templateEngine.process("email/otp-email", context);
            
            helper.setTo(toEmail);
            helper.setSubject("Password Reset OTP - Smart Edu Platform");
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@koushaljha.com", "Koushal Jha I Technologies");
            
            mailSender.send(message);
            System.out.println("✅ OTP email sent successfully to: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send OTP email: " + e.getMessage());
            sendConsoleFallbackOtp(toEmail, otp, firstName);
        }
    }
    
    public void sendVerificationOtpEmail(String toEmail, String otp, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("otp", otp);
            context.setVariable("supportEmail", "support@koushaljha.com");
            context.setVariable("companyName", "Koushal Jha I Technologies");
            
            String htmlContent = templateEngine.process("email/verification-otp-email", context);
            
            helper.setTo(toEmail);
            helper.setSubject("Email Verification - Smart Edu Platform");
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@koushaljha.com", "Koushal Jha I Technologies");
            
            mailSender.send(message);
            System.out.println("✅ Verification OTP email sent successfully to: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send verification OTP email: " + e.getMessage());
            sendConsoleFallbackVerificationOtp(toEmail, otp, firstName);
        }
    }
    
    // Fallback methods
    private void sendConsoleFallbackEmail(String toEmail, String username, String firstName, String type) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📧 FALLBACK EMAIL (Template missing)");
        System.out.println("=".repeat(70));
        System.out.println("To: " + toEmail);
        System.out.println("Type: " + type);
        System.out.println("Username: " + username);
        System.out.println("Name: " + firstName);
        System.out.println("=".repeat(70));
    }
    
    private void sendConsoleFallbackOtp(String toEmail, String otp, String firstName) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📧 FALLBACK OTP EMAIL (Template missing)");
        System.out.println("=".repeat(70));
        System.out.println("To: " + toEmail);
        System.out.println("OTP: " + otp);
        System.out.println("Name: " + firstName);
        System.out.println("=".repeat(70));
    }
    
    private void sendConsoleFallbackVerificationOtp(String toEmail, String otp, String firstName) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📧 FALLBACK VERIFICATION OTP EMAIL");
        System.out.println("=".repeat(70));
        System.out.println("To: " + toEmail);
        System.out.println("OTP: " + otp);
        System.out.println("Name: " + firstName);
        System.out.println("Purpose: Email verification for registration");
        System.out.println("=".repeat(70));
    }
    
    // UPDATED: Fallback method for combined email with password
    private void sendConsoleFallbackCombinedWelcomeEmail(String toEmail, String username, String firstName, String userType, String tempPassword) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📧 FALLBACK COMBINED WELCOME EMAIL");
        System.out.println("=".repeat(70));
        System.out.println("To: " + toEmail);
        System.out.println("Username: " + username);
        System.out.println("Name: " + firstName);
        System.out.println("User Type: " + userType);
        System.out.println("Temporary Password: " + (tempPassword != null ? tempPassword : "Not provided"));
        System.out.println("Message: Welcome to Smart Edu Platform! Your " + userType.toLowerCase() + " account has been created successfully.");
        System.out.println("Login URL: http://localhost:8080/auth/login");
        System.out.println("=".repeat(70));
    }
    
    // UPDATED: Fallback method for admin welcome email
    private void sendConsoleFallbackAdminWelcomeEmail(String toEmail, String username, String firstName, String tempPassword) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📧 FALLBACK ADMIN WELCOME EMAIL");
        System.out.println("=".repeat(70));
        System.out.println("To: " + toEmail);
        System.out.println("Username: " + username);
        System.out.println("Temporary Password: " + tempPassword);
        System.out.println("Name: " + firstName);
        System.out.println("Login URL: http://localhost:8080/auth/login");
        System.out.println("Role: Administrator");
        System.out.println("=".repeat(70));
    }
}