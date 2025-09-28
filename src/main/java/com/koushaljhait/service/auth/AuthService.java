package com.koushaljhait.service.auth;

import com.koushaljhait.model.user.User;
import com.koushaljhait.repository.user.UserRepository;
import com.koushaljhait.service.common.OtpService;
import com.koushaljhait.service.common.ProductionEmailService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final ProductionEmailService emailService;
    private final PasswordEncoder passwordEncoder;
    
    public AuthService(UserRepository userRepository, OtpService otpService, 
                      ProductionEmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Optional<User> validateUserCredentials(String username, String email) {
        Optional<User> userByUsername = userRepository.findByUsername(username);
        if (userByUsername.isPresent()) {
            User user = userByUsername.get();
            if (user.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    
    public boolean initiatePasswordReset(String username, String email) {
        Optional<User> userOpt = validateUserCredentials(username, email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean otpSent = otpService.generateAndSendOtp(user.getEmail(), user.getFirstName());
            
            if (otpSent) {
                return true;
            } else {
                throw new RuntimeException("Failed to send OTP email");
            }
        }
        throw new RuntimeException("Invalid username or email combination");
    }
    
    public boolean validateOtp(String email, String otp) {
        return otpService.verifyOtp(email, otp);
    }
    
    public void resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with email: " + email);
        }
    }
    
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}