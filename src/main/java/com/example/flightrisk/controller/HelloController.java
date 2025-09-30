package com.example.flightrisk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Flight Risk Assessment System API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("endpoints", Map.of(
            "airports", "/airports",
            "predict", "/predict?airport={code}",
            "prediction_history", "/predict/history",
            "health", "/hello"
        ));
        return response;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Spring Boot is working! 🚀 Flight Risk Assessment System is ready!";
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Flight Risk Assessment System");
        return health;
    }
}
