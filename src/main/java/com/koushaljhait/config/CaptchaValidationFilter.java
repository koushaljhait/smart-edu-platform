package com.koushaljhait.config;

import com.koushaljhait.service.common.CaptchaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CaptchaValidationFilter extends OncePerRequestFilter {

    private final CaptchaService captchaService;

    public CaptchaValidationFilter(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }


    @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    
    // Only process login requests
    if ("/auth/login".equals(request.getServletPath()) && "POST".equalsIgnoreCase(request.getMethod())) {
        System.out.println("=== CAPTCHA FILTER: Temporarily disabled for testing ===");
        // Bypass CAPTCHA validation temporarily
        filterChain.doFilter(request, response);
        return;
    }
    
    filterChain.doFilter(request, response);
}
}