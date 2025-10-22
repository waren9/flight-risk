package com.example.flightrisk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db")
    public String checkDbConnection() {
        try {
            String databaseProductName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            return "Database connection successful! Connected to: " + databaseProductName;
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}
