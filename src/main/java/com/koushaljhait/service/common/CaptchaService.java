package com.koushaljhait.service.common;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Random;

@Service
public class CaptchaService {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL = "!@#$%&*";
    private static final String ALL_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL;
    private static final String CAPTCHA_SESSION_KEY = "captcha";
    
    private Random random = new Random();
    
    public String generateCaptcha() {
        StringBuilder captcha = new StringBuilder();
        
        // Ensure at least one character from each category
        captcha.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        captcha.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        captcha.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
        captcha.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        
        // Add 2 more random characters
        for (int i = 0; i < 2; i++) {
            captcha.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        
        // Shuffle the characters
        String finalCaptcha = shuffleString(captcha.toString());
        
        // Store in session
        getSession().setAttribute(CAPTCHA_SESSION_KEY, finalCaptcha);
        
        System.out.println("=== CAPTCHA GENERATED ===");
        System.out.println("Stored CAPTCHA: " + finalCaptcha);
        System.out.println("Session ID: " + getSession().getId());
        
        return finalCaptcha;
    }
    
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
    
    public boolean validateCaptcha(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            System.out.println("CAPTCHA VALIDATION: User input is null or empty");
            return false;
        }
        
        // Get stored CAPTCHA from session
        String storedCaptcha = getStoredCaptchaFromSession();
        
        System.out.println("=== CAPTCHA VALIDATION ===");
        System.out.println("User input: " + userInput);
        System.out.println("Stored CAPTCHA: " + storedCaptcha);
        System.out.println("Session ID: " + getSession().getId());
        
        if (storedCaptcha == null) {
            System.out.println("CAPTCHA VALIDATION: No CAPTCHA found in session");
            return false; // No CAPTCHA in session
        }
        
        // Clean up session after validation
        getSession().removeAttribute(CAPTCHA_SESSION_KEY);
        
        boolean isValid = storedCaptcha.equalsIgnoreCase(userInput.trim());
        System.out.println("CAPTCHA VALIDATION RESULT: " + isValid);
        
        return isValid;
    }
    
    // FIXED: Add the missing session management methods
    private String getStoredCaptchaFromSession() {
        HttpSession session = getSession();
        if (session != null) {
            Object captchaObj = session.getAttribute(CAPTCHA_SESSION_KEY);
            return (captchaObj != null) ? captchaObj.toString() : null;
        }
        return null;
    }
    
    private HttpSession getSession() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) 
                RequestContextHolder.currentRequestAttributes();
            return attr.getRequest().getSession();
        } catch (IllegalStateException e) {
            System.err.println("Session error: " + e.getMessage());
            return null;
        }
    }
}