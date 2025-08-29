package com.example.flightrisk.controller;

import com.example.flightrisk.dto.PredictionResponse;
import com.example.flightrisk.service.BirdstrikeRiskService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/predict")
public class PredictionController {

    private final BirdstrikeRiskService birdstrikeRiskService;

    public PredictionController(BirdstrikeRiskService birdstrikeRiskService) {
        this.birdstrikeRiskService = birdstrikeRiskService;
    }

    @GetMapping
    public PredictionResponse predictRisk(
            @RequestParam String airport,
            @RequestParam int altitude) {

        // Directly return the PredictionResponse object from the service
        return birdstrikeRiskService.assessRisk(airport, altitude);
    }
}
