package com.example.flightrisk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RiskCalculatorService {

    private static final Logger logger = LoggerFactory.getLogger(RiskCalculatorService.class);

    // Risk weights for different factors (total should be 1.0)
    private static final double BIRDSTRIKE_WEIGHT = 0.4;
    private static final double WEATHER_WEIGHT = 0.35;
    private static final double TRAFFIC_WEIGHT = 0.15;
    private static final double HISTORICAL_WEIGHT = 0.1;

    // Risk score thresholds
    private static final double HIGH_RISK_THRESHOLD = 0.7;
    private static final double MEDIUM_RISK_THRESHOLD = 0.4;

    // Airport traffic data (flights per day - simplified)
    private static final Map<String, Integer> AIRPORT_TRAFFIC = new HashMap<>();
    static {
        AIRPORT_TRAFFIC.put("DEL", 1200); // High traffic
        AIRPORT_TRAFFIC.put("BOM", 1000); // High traffic
        AIRPORT_TRAFFIC.put("BLR", 800);  // Medium traffic
        AIRPORT_TRAFFIC.put("HYD", 600);  // Medium traffic
        AIRPORT_TRAFFIC.put("CCU", 500);  // Medium traffic
        AIRPORT_TRAFFIC.put("JFK", 1500); // Very high traffic
    }

    /**
     * Calculate comprehensive risk score using weighted factors
     */
    public double calculateRiskScore(String airportCode, String birdstrikeRisk, String weatherRisk) {
        try {
            double birdstrikeScore = convertRiskToScore(birdstrikeRisk);
            double weatherScore = convertRiskToScore(weatherRisk);
            double trafficScore = calculateTrafficRisk(airportCode);
            double historicalScore = calculateHistoricalRisk(airportCode);

            double totalScore = (birdstrikeScore * BIRDSTRIKE_WEIGHT) +
                               (weatherScore * WEATHER_WEIGHT) +
                               (trafficScore * TRAFFIC_WEIGHT) +
                               (historicalScore * HISTORICAL_WEIGHT);

            // Normalize to 0-1 range
            totalScore = Math.max(0.0, Math.min(1.0, totalScore));

            logger.debug("Risk calculation for {}: Birdstrike={}, Weather={}, Traffic={}, Historical={}, Total={}",
                    airportCode, birdstrikeScore, weatherScore, trafficScore, historicalScore, totalScore);

            return totalScore;

        } catch (Exception e) {
            logger.warn("Error calculating risk score for {}: {}", airportCode, e.getMessage());
            return 0.3; // Safe default (Low-Medium risk)
        }
    }

    /**
     * Calculate final risk level from comprehensive factors
     */
    public String calculateFinalRisk(String airportCode, String birdstrikeRisk, String weatherRisk) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            return "Low Risk"; // Safe default
        }

        try {
            double riskScore = calculateRiskScore(airportCode, birdstrikeRisk, weatherRisk);
            return convertScoreToRisk(riskScore);

        } catch (Exception e) {
            logger.warn("Error calculating final risk for {}: {}", airportCode, e.getMessage());
            return getFallbackRisk(birdstrikeRisk, weatherRisk);
        }
    }

    /**
     * Legacy weather risk calculation for backward compatibility
     */
    public String calculateWeatherRisk(double windSpeed) {
        if (windSpeed > 20) {
            return "High Risk";
        } else if (windSpeed > 10) {
            return "Medium Risk";
        } else {
            return "Low Risk";
        }
    }

    // Helper methods

    private double convertRiskToScore(String risk) {
        if (risk == null) return 0.2; // Default low risk

        switch (risk.toLowerCase().trim()) {
            case "high risk":
            case "high":
                return 0.9;
            case "medium risk":
            case "medium":
                return 0.5;
            case "low risk":
            case "low":
                return 0.2;
            case "critical risk":
            case "critical":
                return 1.0;
            default:
                logger.debug("Unknown risk level '{}', defaulting to low", risk);
                return 0.2;
        }
    }

    private String convertScoreToRisk(double score) {
        if (score >= HIGH_RISK_THRESHOLD) {
            return "High Risk";
        } else if (score >= MEDIUM_RISK_THRESHOLD) {
            return "Medium Risk";
        } else {
            return "Low Risk";
        }
    }

    private double calculateTrafficRisk(String airportCode) {
        if (airportCode == null) return 0.2;

        Integer traffic = AIRPORT_TRAFFIC.get(airportCode.trim().toUpperCase());
        if (traffic == null) {
            return 0.3; // Default for unknown airports
        }

        // Normalize traffic to risk score (more traffic = higher risk)
        if (traffic > 1200) return 0.8;      // Very high traffic
        else if (traffic > 800) return 0.6;  // High traffic
        else if (traffic > 500) return 0.4;  // Medium traffic
        else return 0.2;                     // Low traffic
    }

    private double calculateHistoricalRisk(String airportCode) {
        if (airportCode == null) return 0.2;

        // Simplified historical risk based on known patterns
        switch (airportCode.trim().toUpperCase()) {
            case "DEL": return 0.6; // Delhi has higher historical incidents
            case "BOM": return 0.7; // Mumbai has monsoon challenges
            case "CCU": return 0.5; // Kolkata has weather issues
            case "JFK": return 0.4; // Generally well-managed
            default: return 0.3;   // Default for other airports
        }
    }

    private String getFallbackRisk(String birdstrikeRisk, String weatherRisk) {
        // Simple fallback logic when advanced calculation fails
        if ("High Risk".equals(birdstrikeRisk) || "High Risk".equals(weatherRisk)) {
            return "High Risk";
        } else if ("Medium Risk".equals(birdstrikeRisk) || "Medium Risk".equals(weatherRisk)) {
            return "Medium Risk";
        } else {
            return "Low Risk";
        }
    }

    /**
     * Get detailed risk breakdown for UI display
     */
    public Map<String, Object> getRiskBreakdown(String airportCode, String birdstrikeRisk, String weatherRisk) {
        Map<String, Object> breakdown = new HashMap<>();
        
        try {
            breakdown.put("birdstrikeScore", convertRiskToScore(birdstrikeRisk));
            breakdown.put("weatherScore", convertRiskToScore(weatherRisk));
            breakdown.put("trafficScore", calculateTrafficRisk(airportCode));
            breakdown.put("historicalScore", calculateHistoricalRisk(airportCode));
            breakdown.put("totalScore", calculateRiskScore(airportCode, birdstrikeRisk, weatherRisk));
            breakdown.put("finalRisk", calculateFinalRisk(airportCode, birdstrikeRisk, weatherRisk));
            breakdown.put("confidence", calculateConfidence(airportCode));
            
        } catch (Exception e) {
            logger.warn("Error creating risk breakdown for {}: {}", airportCode, e.getMessage());
            // Return safe defaults
            breakdown.put("birdstrikeScore", 0.2);
            breakdown.put("weatherScore", 0.2);
            breakdown.put("trafficScore", 0.3);
            breakdown.put("historicalScore", 0.3);
            breakdown.put("totalScore", 0.25);
            breakdown.put("finalRisk", "Low Risk");
            breakdown.put("confidence", 0.5);
        }
        
        return breakdown;
    }

    private double calculateConfidence(String airportCode) {
        // Higher confidence for airports we have more data about
        if (AIRPORT_TRAFFIC.containsKey(airportCode)) {
            return 0.85; // High confidence for known airports
        } else {
            return 0.6;  // Lower confidence for unknown airports
        }
    }
}
