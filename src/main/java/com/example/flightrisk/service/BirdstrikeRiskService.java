package com.example.flightrisk.service;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class BirdstrikeRiskService {

    @Value("${birdstrike.realtime.enabled:false}")
    private boolean realtimeEnabled;

    @Value("${birdstrike.realtime.api.url:}")
    private String apiUrl;

    @Value("${birdstrike.fallback.csv:true}")
    private boolean fallbackToCsv;

    private final RestTemplate restTemplate = new RestTemplate();

    // Store both altitude & count for each airport
    private final Map<String, AirportData> birdstrikeData = new HashMap<>();
    private LocalDateTime lastUpdate;

    static class AirportData {
        int altitude;
        int count;
        AirportData(int altitude, int count) {
            this.altitude = altitude;
            this.count = count;
        }
    }

    @PostConstruct
    public void initializeBirdstrikeData() {
        if (realtimeEnabled) {
            System.out.println("Realtime birdstrike data enabled. Fetching from API...");
            fetchRealtimeData();
        } else {
            System.out.println("Realtime data disabled. Loading from CSV...");
            loadBirdstrikeDataFromCsv();
        }
    }

    @Scheduled(fixedDelayString = "${birdstrike.realtime.refresh.interval:300000}")
    public void refreshRealtimeData() {
        if (realtimeEnabled) {
            System.out.println("Refreshing birdstrike data from API at " + LocalDateTime.now());
            fetchRealtimeData();
        }
    }

    private void fetchRealtimeData() {
        try {
            // Example API call - adjust based on actual API response format
            // Expected JSON format: [{"airport":"DEL","altitude":2500,"count":600}, ...]
            @SuppressWarnings("unchecked")
            Map<String, Object>[] response = restTemplate.getForObject(apiUrl, Map[].class);
            
            if (response != null && response.length > 0) {
                synchronized (birdstrikeData) {
                    birdstrikeData.clear();
                    for (Map<String, Object> record : response) {
                        String airportCode = (String) record.get("airport");
                        int altitude = ((Number) record.get("altitude")).intValue();
                        int count = ((Number) record.get("count")).intValue();
                        birdstrikeData.put(airportCode.toUpperCase(), new AirportData(altitude, count));
                    }
                }
                lastUpdate = LocalDateTime.now();
                System.out.println("Realtime birdstrike data loaded successfully! Records: " + response.length);
            } else {
                throw new Exception("Empty response from API");
            }
        } catch (Exception e) {
            System.err.println("Error fetching realtime birdstrike data: " + e.getMessage());
            if (fallbackToCsv && birdstrikeData.isEmpty()) {
                System.out.println("Falling back to CSV data...");
                loadBirdstrikeDataFromCsv();
            }
        }
    }

    private void loadBirdstrikeDataFromCsv() {
        try {
            var resource = new ClassPathResource("data/birdstrikes.csv");

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                br.readLine(); // skip header

                synchronized (birdstrikeData) {
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 5) {
                            String airportCode = parts[0].trim();
                            int altitude = Integer.parseInt(parts[3].trim());
                            int count = Integer.parseInt(parts[4].trim());
                            birdstrikeData.put(airportCode.toUpperCase(), new AirportData(altitude, count));
                        }
                    }
                }
            }
            lastUpdate = LocalDateTime.now();
            System.out.println("Birdstrike data loaded from CSV successfully!");
        } catch (Exception e) {
            System.err.println("Error loading birdstrike data from CSV: " + e.getMessage());
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

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public boolean isRealtimeEnabled() {
        return realtimeEnabled;
    }
}
