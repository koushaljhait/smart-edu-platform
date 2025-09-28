package com.koushaljhait.controller.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koushaljhait.dto.common.ApiResponse;

@RestController
@RequestMapping("/api/db")
public class DatabaseController {

    @GetMapping("/status")
    public ApiResponse databaseStatus() {
        return ApiResponse.success("Database connection established", 
            "PostgreSQL database is connected and ready");
    }
    
    @GetMapping("/info")
    public ApiResponse databaseInfo() {
        return ApiResponse.success("Database Information", 
            "Smart Edu Platform Database v1.0.0.0 - PostgreSQL");
    }
}