package com.example.flightrisk.controller;

import com.example.flightrisk.entity.FlightPrediction;
import com.example.flightrisk.repository.FlightPredictionRepository;
import com.example.flightrisk.service.BirdstrikeRiskService;
import com.example.flightrisk.service.WeatherService;
import com.example.flightrisk.service.RiskCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PredictionController {

    private static final Logger logger = LoggerFactory.getLogger(PredictionController.class);

    @Autowired
    private BirdstrikeRiskService birdstrikeRiskService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private RiskCalculatorService riskCalculatorService;

    @Autowired
    private FlightPredictionRepository flightPredictionRepository;

    @PostMapping("/predict/{airport}")
    public ResponseEntity<Map<String, Object>> predictRisk(
            @PathVariable String airport,
            @RequestParam(required = false) String targetTime) {
        try {
            LocalDateTime predictionTime = targetTime != null ? 
                LocalDateTime.parse(targetTime) : LocalDateTime.now();
            
            logger.info("Predicting risk for airport: {} at time: {}", airport, predictionTime);
            
            // Get risk assessments
            String birdstrikeRisk = birdstrikeRiskService.assessRisk(airport);
            String weatherInfo = weatherService.getWeather(airport);
            String weatherRisk = weatherService.getWeatherRisk(airport);
            
            // Simulate time travel effects on weather and risk
            if (targetTime != null) {
                weatherInfo = simulateWeatherForTime(airport, predictionTime);
                // Adjust risk based on time factors (season, time of day, etc.)
                birdstrikeRisk = adjustRiskForTime(birdstrikeRisk, predictionTime);
                weatherRisk = adjustWeatherRiskForTime(weatherRisk, predictionTime);
            }
            
            // Get comprehensive risk breakdown
            Map<String, Object> riskBreakdown = riskCalculatorService.getRiskBreakdown(airport, birdstrikeRisk, weatherRisk);
            
            // Calculate final risk score
            double riskScore = riskCalculatorService.calculateRiskScore(airport, birdstrikeRisk, weatherRisk);
            String finalRisk = riskCalculatorService.calculateFinalRisk(airport, birdstrikeRisk, weatherRisk);
            
            // Save prediction to database
            FlightPrediction prediction = new FlightPrediction(airport, finalRisk, weatherInfo, "N/A", riskScore);
            flightPredictionRepository.save(prediction);
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("airport", airport);
            response.put("riskLevel", finalRisk);
            response.put("riskScore", riskScore);
            response.put("weather", weatherInfo);
            response.put("breakdown", riskBreakdown);
            response.put("timestamp", predictionTime.toString());
            response.put("isTimeTravel", targetTime != null);
            response.put("predictionTime", predictionTime.toString());
            
            logger.info("Risk prediction completed for {} at {}: {}", airport, predictionTime, finalRisk);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to assess risk for airport {}: {}", airport, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to assess risk: " + e.getMessage());
            errorResponse.put("airport", airport);
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    private String simulateWeatherForTime(String airport, LocalDateTime targetTime) {
        // Simulate weather conditions based on target time
        int month = targetTime.getMonthValue();
        int hour = targetTime.getHour();
        
        String season = getSeason(month);
        String timeOfDay = getTimeOfDay(hour);
        
        return String.format("Simulated weather for %s in %s during %s: %s conditions expected", 
            airport, season, timeOfDay, getSeasonalWeather(season));
    }
    
    private String adjustRiskForTime(String baseRisk, LocalDateTime targetTime) {
        // Adjust birdstrike risk based on migration seasons and time
        int month = targetTime.getMonthValue();
        int hour = targetTime.getHour();
        
        // Spring and fall are high migration periods
        if (month >= 3 && month <= 5 || month >= 9 && month <= 11) {
            if (baseRisk.contains("Low")) return baseRisk.replace("Low", "Medium");
            if (baseRisk.contains("Medium")) return baseRisk.replace("Medium", "High");
        }
        
        // Dawn and dusk are high bird activity times
        if (hour >= 5 && hour <= 8 || hour >= 17 && hour <= 20) {
            if (baseRisk.contains("Low")) return baseRisk.replace("Low", "Medium");
        }
        
        return baseRisk;
    }
    
    private String adjustWeatherRiskForTime(String baseRisk, LocalDateTime targetTime) {
        // Adjust weather risk based on seasonal patterns
        int month = targetTime.getMonthValue();
        
        // Winter months typically have more severe weather
        if (month >= 12 || month <= 2) {
            if (baseRisk.contains("Low")) return baseRisk.replace("Low", "Medium");
        }
        
        // Summer storm season
        if (month >= 6 && month <= 8) {
            if (baseRisk.contains("Low")) return baseRisk.replace("Low", "Medium");
        }
        
        return baseRisk;
    }
    
    private String getSeason(int month) {
        if (month >= 3 && month <= 5) return "Spring";
        if (month >= 6 && month <= 8) return "Summer";
        if (month >= 9 && month <= 11) return "Fall";
        return "Winter";
    }
    
    private String getTimeOfDay(int hour) {
        if (hour >= 5 && hour < 12) return "Morning";
        if (hour >= 12 && hour < 17) return "Afternoon";
        if (hour >= 17 && hour < 21) return "Evening";
        return "Night";
    }
    
    private String getSeasonalWeather(String season) {
        switch (season) {
            case "Spring": return "Mild with possible rain showers";
            case "Summer": return "Warm with thunderstorm potential";
            case "Fall": return "Cool with variable conditions";
            case "Winter": return "Cold with possible snow/ice";
            default: return "Variable conditions";
        }
    }

    @GetMapping("/predictions")
    public ResponseEntity<List<FlightPrediction>> getPredictions() {
        try {
            List<FlightPrediction> predictions = flightPredictionRepository.findAll();
            logger.info("Retrieved {} predictions", predictions.size());
            return ResponseEntity.ok(predictions);
        } catch (Exception e) {
            logger.error("Failed to retrieve predictions: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/predictions")
    public ResponseEntity<Map<String, String>> clearPredictions() {
        try {
            flightPredictionRepository.deleteAll();
            logger.info("Cleared all predictions");
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "All predictions cleared successfully");
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to clear predictions: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to clear predictions: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @DeleteMapping("/predictions/{id}")
    public ResponseEntity<Map<String, String>> deletePrediction(@PathVariable Long id) {
        try {
            if (flightPredictionRepository.findById(id) != null) {
                flightPredictionRepository.deleteById(id);
                logger.info("Deleted prediction with id: {}", id);
                
                Map<String, String> response = new HashMap<>();
                response.put("message", "Prediction deleted successfully");
                response.put("id", id.toString());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Prediction not found");
                errorResponse.put("id", id.toString());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Failed to delete prediction {}: {}", id, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete prediction: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            List<FlightPrediction> predictions = flightPredictionRepository.findAll();
            
            long total = predictions.size();
            long highRisk = predictions.stream()
                .filter(p -> p.getRiskLevel().toLowerCase().contains("high"))
                .count();
            long mediumRisk = predictions.stream()
                .filter(p -> p.getRiskLevel().toLowerCase().contains("medium"))
                .count();
            long lowRisk = predictions.stream()
                .filter(p -> p.getRiskLevel().toLowerCase().contains("low"))
                .count();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("total", total);
            statistics.put("highRisk", highRisk);
            statistics.put("mediumRisk", mediumRisk);
            statistics.put("lowRisk", lowRisk);
            statistics.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Failed to get statistics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("version", "2.0.0");
        health.put("service", "Flight Risk Assessment System");
        return ResponseEntity.ok(health);
    }

    // Legacy endpoints for backward compatibility
    @GetMapping("/history")
    public List<FlightPrediction> getPredictionHistory() {
        return flightPredictionRepository.findAll();
    }

    @GetMapping("/history/{airport}")
    public List<FlightPrediction> getPredictionHistoryByAirport(@PathVariable String airport) {
        return flightPredictionRepository.findByAirport(airport);
    }

    private double calculateRiskScore(String riskLevel) {
        switch (riskLevel.toLowerCase()) {
            case "low":
                return 0.2;
            case "medium":
                return 0.5;
            case "high":
                return 0.8;
            case "critical":
                return 1.0;
            default:
                return 0.5;
        }
    }
}
