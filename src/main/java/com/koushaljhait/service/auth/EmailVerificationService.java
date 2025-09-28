package com.koushaljhait.service.auth;

import com.koushaljhait.model.common.EmailVerificationOtp;
import com.koushaljhait.model.user.User;
import com.koushaljhait.repository.common.EmailVerificationOtpRepository;
import com.koushaljhait.repository.user.UserRepository;
import com.koushaljhait.service.common.ProductionEmailService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailVerificationService {
    
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    
    private final EmailVerificationOtpRepository otpRepository;
    private final UserRepository userRepository;
    private final ProductionEmailService emailService;
    
    public EmailVerificationService(EmailVerificationOtpRepository otpRepository, 
                                   UserRepository userRepository,
                                   ProductionEmailService emailService) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    
    // Generate random OTP
    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    // Send OTP to email for verification
    public boolean sendVerificationOtp(String email, String firstName) {
        try {
            // Delete any existing OTP for this email
            otpRepository.findByEmail(email).ifPresent(otpRepository::delete);
            
            // Generate new OTP
            String otpCode = generateOtp();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
            
            // Save to database
            EmailVerificationOtp otpEntity = new EmailVerificationOtp(email, otpCode, expiresAt);
            otpRepository.save(otpEntity);
            
            // Send OTP via email
            emailService.sendVerificationOtpEmail(email, otpCode, firstName);
            
            System.out.println("✅ Verification OTP sent to " + email + ": " + otpCode);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error sending verification OTP: " + e.getMessage());
            return false;
        }
    }
    
    // Verify OTP
    public boolean verifyOtp(String email, String otpCode) {
        try {
            Optional<EmailVerificationOtp> otpOptional = otpRepository.findByEmail(email);
            
            if (otpOptional.isPresent()) {
                EmailVerificationOtp otp = otpOptional.get();
                // Check if OTP is valid (not expired and not used)
                if (otp.getOtpCode().equals(otpCode) && 
                    LocalDateTime.now().isBefore(otp.getExpiresAt()) && 
                    !otp.isVerified()) {
                    
                    // Mark OTP as verified
                    otp.setVerified(true);
                    otpRepository.save(otp);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error verifying OTP: " + e.getMessage());
            return false;
        }
    }
    
    // Check if email is already verified
    public boolean isEmailVerified(String email) {
        Optional<EmailVerificationOtp> otpOptional = otpRepository.findByEmail(email);
        return otpOptional.isPresent() && otpOptional.get().isVerified();
    }
    
    // Check if email already exists in system
    public boolean isEmailAlreadyRegistered(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        return existingUser.isPresent();
    }
    
    // Check email availability via API
    public Map<String, Object> checkEmailAvailability(String email) {
        Map<String, Object> response = new HashMap<>();
        
        // Basic email validation
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            response.put("available", false);
            response.put("message", "Invalid email format");
            return response;
        }
        
        // Check if email already registered
        if (isEmailAlreadyRegistered(email)) {
            response.put("available", false);
            response.put("message", "This email is already registered");
            return response;
        }
        
        response.put("available", true);
        response.put("message", "Email is available");
        return response;
    }
}