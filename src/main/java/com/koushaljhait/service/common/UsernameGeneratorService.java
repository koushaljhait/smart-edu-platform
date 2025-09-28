package com.koushaljhait.service.common;

import org.springframework.stereotype.Service;

import com.koushaljhait.repository.user.UserRepository;

@Service
public class UsernameGeneratorService {
    
    private final UserRepository userRepository;
    
    public UsernameGeneratorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public String generateUniqueUsername() {
        long nextNumber = 1000000000L;
        String username;
        
        do {
            nextNumber++;
            if (nextNumber > 9999999999L) {
                throw new IllegalStateException("Username space exhausted");
            }
            username = String.format("%010d", nextNumber);
        } while (userRepository.existsByUsername(username));
        
        return username;
    }
}