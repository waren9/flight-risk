package com.example.flightrisk.service;

import org.springframework.stereotype.Service;

@Service
public class TurbulenceRiskService {

    // Dummy rule-based turbulence logic
    public String assessTurbulence(int windSpeed) {
        if (windSpeed > 40) {
            return "High Risk";
        } else if (windSpeed > 20) {
            return "Medium Risk";
        } else {
            return "Low Risk";
        }
    }
}
