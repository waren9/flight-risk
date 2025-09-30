package com.example.flightrisk.service;

import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class BirdstrikeRiskService {

    // Store both altitude & count for each airport
    private final Map<String, AirportData> birdstrikeData = new HashMap<>();

    static class AirportData {
        int altitude;
        int count;
        AirportData(int altitude, int count) {
            this.altitude = altitude;
            this.count = count;
        }
    }

    @PostConstruct
    public void loadBirdstrikeData() {
        try {
            var resource = new ClassPathResource("data/birdstrikes.csv");

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                br.readLine(); // skip header

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        String airportCode = parts[0].trim(); // define inside the loop
                        int altitude = Integer.parseInt(parts[3].trim());
                        int count = Integer.parseInt(parts[4].trim());
                        birdstrikeData.put(airportCode, new AirportData(altitude, count));
                    }
                }
            }
            System.out.println("Birdstrike data loaded successfully!");
        } catch (Exception e) {
            System.err.println("Error loading birdstrike data: " + e.getMessage());
        }
    }

    public String assessRisk(String airportCode) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            return "Invalid airport code";
        }
        
        AirportData data = birdstrikeData.get(airportCode.trim().toUpperCase());
        if (data == null) {
            System.out.println("No birdstrike data found for airport: " + airportCode);
            return "No data available";
        }

        int altitude = data.altitude;
        int count = data.count;

        System.out.println("Assessing risk for " + airportCode + ": Altitude=" + altitude + ", Count=" + count);

        if (altitude < 3000 && count > 500) {
            return "High Risk";
        } else if (altitude < 3000 && count > 200) {
            return "Medium Risk";
        } else {
            return "Low Risk";
        }
    }

    public Map<String, AirportData> getBirdstrikeData() {
        return new HashMap<>(birdstrikeData);
    }

    public boolean hasDataForAirport(String airportCode) {
        return airportCode != null && birdstrikeData.containsKey(airportCode.trim().toUpperCase());
    }
}
