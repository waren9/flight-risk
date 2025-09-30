package com.example.flightrisk.controller;

import com.example.flightrisk.dto.PredictionResponse;
import com.example.flightrisk.entity.FlightPrediction;
import com.example.flightrisk.repository.FlightPredictionRepository;
import com.example.flightrisk.service.BirdstrikeRiskService;
import com.example.flightrisk.service.WeatherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/predict")
public class PredictionController {

    private final BirdstrikeRiskService birdstrikeRiskService;
    private final WeatherService weatherService;
    private final FlightPredictionRepository flightPredictionRepository;

    public PredictionController(BirdstrikeRiskService birdstrikeRiskService, 
                              WeatherService weatherService,
                              FlightPredictionRepository flightPredictionRepository) {
        this.birdstrikeRiskService = birdstrikeRiskService;
        this.weatherService = weatherService;
        this.flightPredictionRepository = flightPredictionRepository;
    }

    @GetMapping
    public PredictionResponse predictRisk(@RequestParam String airport) {
        if (airport == null || airport.trim().isEmpty()) {
            throw new IllegalArgumentException("Airport code cannot be null or empty");
        }
        
        try {
            String riskLevel = birdstrikeRiskService.assessRisk(airport);
            String weatherInfo = weatherService.getWeather(airport);

            // Calculate risk score based on risk level
            double riskScore = calculateRiskScore(riskLevel);

            // Save prediction to database
            FlightPrediction prediction = new FlightPrediction(airport.toUpperCase(), riskLevel, weatherInfo, "N/A", riskScore);
            flightPredictionRepository.save(prediction);

            return new PredictionResponse(airport.toUpperCase(), riskLevel, weatherInfo);
        } catch (Exception e) {
            throw new RuntimeException("Failed to predict risk for airport " + airport + ": " + e.getMessage(), e);
        }
    }

    @GetMapping("/history")
    public List<FlightPrediction> getPredictionHistory() {
        return flightPredictionRepository.findAll();
    }

    @GetMapping("/history/{airport}")
    public List<FlightPrediction> getPredictionHistoryByAirport(@PathVariable String airport) {
        return flightPredictionRepository.findByAirport(airport);
    }

    @DeleteMapping("/history/{id}")
    public void deletePrediction(@PathVariable Long id) {
        flightPredictionRepository.deleteById(id);
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
