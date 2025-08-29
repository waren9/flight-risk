package com.example.flightrisk;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PredictionController {

    @PostMapping("/predict")
    public PredictionResult predict(@RequestBody FlightRoute route) {
        // For now, return dummy values
        return new PredictionResult(0.25, 0.10, "Try altitude +2000 ft");
    }
}

// Response class
class PredictionResult {
    private double turbulenceProbability;
    private double birdstrikeProbability;
    private String suggestion;

    public PredictionResult(double turbulenceProbability, double birdstrikeProbability, String suggestion) {
        this.turbulenceProbability = turbulenceProbability;
        this.birdstrikeProbability = birdstrikeProbability;
        this.suggestion = suggestion;
    }

    // getters (needed for JSON serialization)
    public double getTurbulenceProbability() { return turbulenceProbability; }
    public double getBirdstrikeProbability() { return birdstrikeProbability; }
    public String getSuggestion() { return suggestion; }
}
