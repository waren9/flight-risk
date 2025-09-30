package com.example.flightrisk.entity;

import java.time.LocalDateTime;

public class FlightPrediction {
    private Long id;
    private String airport;
    private String riskLevel;
    private String weather;
    private LocalDateTime predictionTime;
    private String route;
    private Double riskScore;

    public FlightPrediction() {}

    public FlightPrediction(String airport, String riskLevel, String weather, String route, Double riskScore) {
        this.airport = airport;
        this.riskLevel = riskLevel;
        this.weather = weather;
        this.route = route;
        this.riskScore = riskScore;
        this.predictionTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAirport() {
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public LocalDateTime getPredictionTime() {
        return predictionTime;
    }

    public void setPredictionTime(LocalDateTime predictionTime) {
        this.predictionTime = predictionTime;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }
}


