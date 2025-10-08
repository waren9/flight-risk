import axios from 'axios';
import { Airport, FlightPrediction, PredictionRequest, PredictionResponse, Statistics } from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for logging
api.interceptors.request.use(
  (config) => {
    console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Response Error:', error);
    if (error.response?.status === 404) {
      throw new Error('Service not available. Please ensure the backend is running.');
    }
    if (error.response?.status >= 500) {
      throw new Error('Server error. Please try again later.');
    }
    if (error.code === 'ECONNABORTED') {
      throw new Error('Request timeout. Please check your connection.');
    }
    throw error;
  }
);

export const airportService = {
  getAll: async (): Promise<Airport[]> => {
    const response = await api.get('/airports');
    return response.data;
  },
};

export const predictionService = {
  predict: async (request: PredictionRequest): Promise<PredictionResponse> => {
    const response = await api.post(`/predict/${request.airport}`);
    return response.data;
  },

  getHistory: async (): Promise<FlightPrediction[]> => {
    const response = await api.get('/predictions');
    return response.data;
  },

  clearHistory: async (): Promise<void> => {
    await api.delete('/predictions');
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/predictions/${id}`);
  },
};

export const statisticsService = {
  get: async (): Promise<Statistics> => {
    const response = await api.get('/statistics');
    return response.data;
  },
};

export const weatherService = {
  get: async (airport: string): Promise<string> => {
    const response = await api.get(`/weather/${airport}`);
    return response.data;
  },
};

export default api;
