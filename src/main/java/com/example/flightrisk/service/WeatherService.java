package com.example.flightrisk.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    private static final String API_KEY = "16902d26e96269e9792b9fdc1015a6a5";
    private static final String API_URL =
            "https://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}&units=metric";

    private final RestTemplate restTemplate = new RestTemplate();

    // IATA → City mapping
    private static final Map<String, String> AIRPORT_TO_CITY = new HashMap<>();
    static {
        AIRPORT_TO_CITY.put("DEL", "Delhi");
        AIRPORT_TO_CITY.put("BOM", "Mumbai");
        AIRPORT_TO_CITY.put("BLR", "Bangalore");
        AIRPORT_TO_CITY.put("HYD", "Hyderabad");
        AIRPORT_TO_CITY.put("CCU", "Kolkata");
    }

    // ✅ Fetch raw weather info
    public String getWeather(String airportCode) {
        String city = AIRPORT_TO_CITY.getOrDefault(airportCode.trim(), airportCode.trim());

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("city", city);
        uriVariables.put("apiKey", API_KEY);

        try {
            Map<String, Object> response =
                    restTemplate.getForObject(API_URL, Map.class, uriVariables);

            if (response == null) return "No weather data available";

            Map<String, Object> main = (Map<String, Object>) response.get("main");
            Map<String, Object> wind = (Map<String, Object>) response.get("wind");
            var weatherArray = (java.util.List<Map<String, Object>>) response.get("weather");

            double temp = main != null ? ((Number) main.get("temp")).doubleValue() : 0.0;
            double windSpeed = wind != null ? ((Number) wind.get("speed")).doubleValue() : 0.0;
            String condition = weatherArray != null && !weatherArray.isEmpty()
                    ? (String) weatherArray.get(0).get("main")
                    : "Clear";

            return String.format("Temp: %.1f°C, Wind: %.1f m/s, Condition: %s", temp, windSpeed, condition);

        } catch (Exception e) {
            return "Error fetching weather: " + e.getMessage();
        }
    }

    // ✅ Convert weather data into Risk Level
    public String getWeatherRisk(String airportCode) {
        String city = AIRPORT_TO_CITY.getOrDefault(airportCode.trim(), airportCode.trim());

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("city", city);
        uriVariables.put("apiKey", API_KEY);

        try {
            Map<String, Object> response =
                    restTemplate.getForObject(API_URL, Map.class, uriVariables);

            if (response == null) return "No data available";

            Map<String, Object> wind = (Map<String, Object>) response.get("wind");
            var weatherArray = (java.util.List<Map<String, Object>>) response.get("weather");

            double windSpeed = wind != null ? ((Number) wind.get("speed")).doubleValue() : 0.0;
            String condition = weatherArray != null && !weatherArray.isEmpty()
                    ? (String) weatherArray.get(0).get("main")
                    : "Clear";

            // Risk calculation logic
            if (windSpeed > 15 || condition.equalsIgnoreCase("Thunderstorm")) {
                return "High Risk";
            } else if (windSpeed > 8 || condition.equalsIgnoreCase("Rain")) {
                return "Medium Risk";
            } else {
                return "Low Risk";
            }

        } catch (Exception e) {
            return "Error fetching weather: " + e.getMessage();
        }
    }
}
