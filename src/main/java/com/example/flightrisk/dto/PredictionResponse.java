package com.example.flightrisk.dto;

public class PredictionResponse {
    private String airport;
    private int altitude;
    private String riskLevel;
    private String suggestedAlternate;

    public PredictionResponse(String airport, int altitude, String riskLevel, String suggestedAlternate) {
        this.airport = airport;
        this.altitude = altitude;
        this.riskLevel = riskLevel;
        this.suggestedAlternate = suggestedAlternate;
    }

    public String getAirport() {
        return airport;
    }

    public int getAltitude() {
        return altitude;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getSuggestedAlternate() {
        return suggestedAlternate;
    }
}
