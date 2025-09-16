package com.example.flightrisk.dto;

public class PredictionResponse {
    private String airport;
    private String riskLevel;
    private String weather;

    public PredictionResponse(String airport, String riskLevel, String weather) {
        this.airport = airport;
        this.riskLevel = riskLevel;
        this.weather = weather;
    }

    public String getAirport() { return airport; }
    public String getRiskLevel() { return riskLevel; }
    public String getWeather() { return weather; }
}
