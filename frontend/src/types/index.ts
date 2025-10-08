export interface Airport {
  code: string;
  name: string;
  city: string;
  country: string;
}

export interface FlightPrediction {
  id?: number;
  airport: string;
  riskLevel: string;
  weather: string;
  riskScore: number;
  predictionTime: string;
  confidence?: number;
}

export interface RiskBreakdown {
  birdstrikeScore: number;
  weatherScore: number;
  trafficScore: number;
  historicalScore: number;
  totalScore: number;
  finalRisk: string;
  confidence: number;
}

export interface WeatherData {
  temperature: number;
  windSpeed: number;
  condition: string;
  visibility: number;
}

export interface Statistics {
  total: number;
  highRisk: number;
  mediumRisk: number;
  lowRisk: number;
}

export type RiskLevel = 'Low Risk' | 'Medium Risk' | 'High Risk';

export interface PredictionRequest {
  airport: string;
}

export interface PredictionResponse {
  airport: string;
  riskLevel: RiskLevel;
  riskScore: number;
  weather: string;
  breakdown: RiskBreakdown;
  timestamp: string;
}
