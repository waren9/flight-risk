package com.example.flightrisk.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.example.flightrisk.dto.PredictionResponse;

@Service
public class BirdstrikeRiskService {

    private final Map<String, Integer> birdstrikeCounts = new HashMap<>();

    @PostConstruct
    public void loadDummyData() {
        birdstrikeCounts.put("DEL", 1200);
        birdstrikeCounts.put("BOM", 800);
        birdstrikeCounts.put("BLR", 350);
        birdstrikeCounts.put("HYD", 200);
        birdstrikeCounts.put("CCU", 100);
    }

    public PredictionResponse assessRisk(String airportCode, int altitude) {
        int count = birdstrikeCounts.getOrDefault(airportCode, 0);
        String risk;

        if (altitude < 3000 && count > 500) {
            risk = "High Risk";
        } else if (altitude < 3000 && count > 200) {
            risk = "Medium Risk";
        } else {
            risk = "Low Risk";
        }

        String suggestedAlternate = null;
        if ("High Risk".equals(risk)) {
            suggestedAlternate = birdstrikeCounts.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(airportCode))
                    .min(Map.Entry.comparingByValue()) // lowest strike count
                    .map(Map.Entry::getKey)
                    .orElse("NONE");
        }

        return new PredictionResponse(airportCode, altitude, risk, suggestedAlternate);
    }
}
