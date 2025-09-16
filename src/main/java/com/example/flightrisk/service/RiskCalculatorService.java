package com.example.flightrisk.service;

import org.springframework.stereotype.Service;

@Service
public class RiskCalculatorService {

    public String calculateWeatherRisk(double windSpeed) {
        if (windSpeed > 20) {
            return "High Risk";
        } else if (windSpeed > 10) {
            return "Medium Risk";
        } else {
            return "Low Risk";
        }
    }

    public String calculateFinalRisk(String birdstrikeRisk, String weatherRisk) {
        if ("High Risk".equals(birdstrikeRisk) || "High Risk".equals(weatherRisk)) {
            return "High Risk";
        } else if ("Medium Risk".equals(birdstrikeRisk) || "Medium Risk".equals(weatherRisk)) {
            return "Medium Risk";
        } else {
            return "Low Risk";
        }
    }
}
