package com.koushaljhait.service.auth;

import com.koushaljhait.dto.auth.RegistrationRequest;
import com.koushaljhait.model.user.User;
import com.koushaljhait.repository.user.UserRepository;
import com.koushaljhait.service.common.ProductionEmailService;
import com.koushaljhait.service.common.UsernameGeneratorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistrationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsernameGeneratorService usernameGeneratorService;
    private final ProductionEmailService productionEmailService;
    
    public RegistrationService(UserRepository userRepository, 
                             PasswordEncoder passwordEncoder,
                             UsernameGeneratorService usernameGeneratorService,
                             ProductionEmailService productionEmailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.usernameGeneratorService = usernameGeneratorService;
        this.productionEmailService = productionEmailService;
    }
    
    public User registerTeacher(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        String username = usernameGeneratorService.generateUniqueUsername();
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole("TEACHER");
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // Send SINGLE combined welcome email instead of two separate emails
        try {
            // FIXED: Pass the password that user entered during registration
            productionEmailService.sendCombinedWelcomeEmail(
                user.getEmail(), 
                username, 
                user.getFirstName(), 
                "Teacher",
                request.getPassword() // ADD THIS LINE - Send the actual password
            );
            System.out.println("✅ Combined welcome email sent successfully to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Email sending failed, but user registered: " + e.getMessage());
            e.printStackTrace();
        }
        
        return savedUser;
    }
    
    public User registerStudent(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        String username = usernameGeneratorService.generateUniqueUsername();
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole("STUDENT");
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // Send SINGLE combined welcome email instead of two separate emails
        try {
            // FIXED: Pass the password that user entered during registration
            productionEmailService.sendCombinedWelcomeEmail(
                user.getEmail(), 
                username, 
                user.getFirstName(), 
                "Student",
                request.getPassword() // ADD THIS LINE - Send the actual password
            );
            System.out.println("✅ Combined welcome email sent successfully to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Email sending failed, but user registered: " + e.getMessage());
            e.printStackTrace();
        }
        
        return savedUser;
    }
}
