package com.koushaljhait.service.common;

import com.koushaljhait.model.common.PasswordResetOtp;
import com.koushaljhait.repository.common.PasswordResetOtpRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class OtpService {
    
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    
    @Autowired
    private PasswordResetOtpRepository otpRepository;
    
    @Autowired
    private ProductionEmailService emailService;
    
    // Generate random OTP
    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    // Generate and send OTP
    public boolean generateAndSendOtp(String email, String firstName) {
        try {
            // Delete any existing OTP for this email
            otpRepository.findByEmail(email).ifPresent(existingOtp -> 
                otpRepository.delete(existingOtp));
            
            // Generate new OTP
            String otpCode = generateOtp();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
            
            // Save to database
            PasswordResetOtp otpEntity = new PasswordResetOtp(email, otpCode, expiresAt);
            otpRepository.save(otpEntity);
            
            // Send OTP via email
            emailService.sendOtpEmail(email, otpCode, firstName);
            
            System.out.println("✅ OTP generated for " + email + ": " + otpCode + " (Expires: " + expiresAt + ")");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error generating OTP: " + e.getMessage());
            return false;
        }
    }
    
    // Verify OTP - FIXED VERSION
    public boolean verifyOtp(String email, String otpCode) {
        try {
            // Debug: Check current OTP status
            debugOtpStatus(email);
            
            Optional<PasswordResetOtp> otpOptional = otpRepository.findValidOtpByEmail(email, LocalDateTime.now());
            
            if (otpOptional.isPresent()) {
                PasswordResetOtp otp = otpOptional.get();
                System.out.println("🔍 Comparing OTP: Input='" + otpCode + "' vs Stored='" + otp.getOtpCode() + "'");
                
                if (otp.getOtpCode().equals(otpCode)) {
                    // FIX: Mark OTP as used by updating the entity directly
                    otp.setUsed(true);
                    otpRepository.save(otp);
                    System.out.println("✅ OTP verified successfully for: " + email);
                    return true;
                }
            }
            System.out.println("❌ Invalid OTP for: " + email);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error verifying OTP: " + e.getMessage());
            e.printStackTrace(); // This will show the full stack trace
            return false;
        }
    }
    
    // Debug method to check OTP status
    public void debugOtpStatus(String email) {
        Optional<PasswordResetOtp> currentOtp = otpRepository.findByEmail(email);
        if (currentOtp.isPresent()) {
            PasswordResetOtp otp = currentOtp.get();
            System.out.println("🔍 OTP Debug for " + email + ":");
            System.out.println("   OTP Code: " + otp.getOtpCode());
            System.out.println("   Created: " + otp.getCreatedAt());
            System.out.println("   Expires: " + otp.getExpiresAt());
            System.out.println("   Used: " + otp.isUsed());
            System.out.println("   Valid: " + otp.isValid());
            System.out.println("   Current Time: " + LocalDateTime.now());
        } else {
            System.out.println("🔍 No OTP found for: " + email);
        }
    }
    
    // Clean up expired OTPs every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredOtps() {
        otpRepository.deleteExpiredOtps(LocalDateTime.now());
        System.out.println("✅ Expired OTPs cleaned up");
    }
}