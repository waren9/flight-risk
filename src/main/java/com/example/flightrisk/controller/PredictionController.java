package com.example.flightrisk.controller;

import com.example.flightrisk.dto.PredictionResponse;
import com.example.flightrisk.service.BirdstrikeRiskService;
import com.example.flightrisk.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/predict")
public class PredictionController {

    private final BirdstrikeRiskService birdstrikeRiskService;
    private final WeatherService weatherService;

    public PredictionController(BirdstrikeRiskService birdstrikeRiskService, WeatherService weatherService) {
        this.birdstrikeRiskService = birdstrikeRiskService;
        this.weatherService = weatherService;
    }

    @GetMapping
    public PredictionResponse predictRisk(@RequestParam String airport) {

        String riskLevel = birdstrikeRiskService.assessRisk(airport);
        String weatherInfo = weatherService.getWeather(airport);

        return new PredictionResponse(airport, riskLevel, weatherInfo);
    }
}
