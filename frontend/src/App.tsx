import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  BarChart3, 
  AlertTriangle, 
  CheckCircle, 
  XCircle,
  TrendingUp
} from 'lucide-react';

import Header from './components/Header';
import StatisticsCard from './components/StatisticsCard';
import RiskAssessment from './components/RiskAssessment';
import PredictionHistory from './components/PredictionHistory';
import LoadingSpinner from './components/LoadingSpinner';

import { airportService, predictionService, statisticsService } from './services/api';
import { Airport, FlightPrediction, Statistics, PredictionResponse } from './types';

const App: React.FC = () => {
  const [airports, setAirports] = useState<Airport[]>([]);
  const [predictions, setPredictions] = useState<FlightPrediction[]>([]);
  const [statistics, setStatistics] = useState<Statistics>({ total: 0, highRisk: 0, mediumRisk: 0, lowRisk: 0 });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Load initial data
  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    setIsLoading(true);
    setError(null);

    try {
      // Load airports and predictions in parallel
      const [airportsData, predictionsData] = await Promise.all([
        airportService.getAll().catch(() => []),
        predictionService.getHistory().catch(() => [])
      ]);

      setAirports(airportsData);
      setPredictions(predictionsData);
      
      // Calculate statistics from predictions
      calculateStatistics(predictionsData);
    } catch (err) {
      console.error('Failed to load initial data:', err);
      setError('Failed to load application data. Please ensure the backend is running.');
    } finally {
      setIsLoading(false);
    }
  };

  const calculateStatistics = (predictionData: FlightPrediction[]) => {
    const stats = predictionData.reduce(
      (acc, prediction) => {
        acc.total++;
        switch (prediction.riskLevel) {
          case 'High Risk':
            acc.highRisk++;
            break;
          case 'Medium Risk':
            acc.mediumRisk++;
            break;
          case 'Low Risk':
            acc.lowRisk++;
            break;
        }
        return acc;
      },
      { total: 0, highRisk: 0, mediumRisk: 0, lowRisk: 0 }
    );
    setStatistics(stats);
  };

  const handlePredictionComplete = (prediction: PredictionResponse) => {
    // Add new prediction to the list
    const newPrediction: FlightPrediction = {
      airport: prediction.airport,
      riskLevel: prediction.riskLevel,
      weather: prediction.weather,
      riskScore: prediction.riskScore,
      predictionTime: prediction.timestamp,
      confidence: prediction.breakdown.confidence
    };

    const updatedPredictions = [newPrediction, ...predictions];
    setPredictions(updatedPredictions);
    calculateStatistics(updatedPredictions);
  };

  const handleRefreshHistory = async () => {
    try {
      const predictionsData = await predictionService.getHistory();
      setPredictions(predictionsData);
      calculateStatistics(predictionsData);
    } catch (err) {
      console.error('Failed to refresh history:', err);
    }
  };

  const handleClearHistory = () => {
    setPredictions([]);
    setStatistics({ total: 0, highRisk: 0, mediumRisk: 0, lowRisk: 0 });
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="bg-white p-8 rounded-2xl shadow-xl text-center max-w-md"
        >
          <XCircle className="h-16 w-16 text-danger-500 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-gray-900 mb-2">Connection Error</h2>
          <p className="text-gray-600 mb-6">{error}</p>
          <motion.button
            onClick={loadInitialData}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="gradient-primary text-white px-6 py-3 rounded-xl font-semibold"
          >
            Retry Connection
          </motion.button>
        </motion.div>
      </div>
    );
  }

  const airportCodes = airports.map(airport => airport.code);

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      
      <main className="container mx-auto px-6 py-8">
        {/* Statistics Cards */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8"
        >
          <StatisticsCard
            title="Total Predictions"
            value={statistics.total}
            icon={BarChart3}
            color="primary"
          />
          <StatisticsCard
            title="High Risk"
            value={statistics.highRisk}
            icon={XCircle}
            color="danger"
          />
          <StatisticsCard
            title="Medium Risk"
            value={statistics.mediumRisk}
            icon={AlertTriangle}
            color="warning"
          />
          <StatisticsCard
            title="Low Risk"
            value={statistics.lowRisk}
            icon={CheckCircle}
            color="success"
          />
        </motion.div>

        {/* Main Content Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Risk Assessment */}
          <RiskAssessment
            airports={airportCodes}
            onPredictionComplete={handlePredictionComplete}
          />

          {/* Prediction History */}
          <PredictionHistory
            predictions={predictions}
            onRefresh={handleRefreshHistory}
            onClearHistory={handleClearHistory}
          />
        </div>

        {/* Footer */}
        <motion.footer
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1 }}
          className="mt-16 text-center text-gray-500"
        >
          <p className="flex items-center justify-center space-x-2">
            <TrendingUp className="h-4 w-4" />
            <span>Flight Risk Assessment System v2.0 - Advanced Aviation Safety Analytics</span>
          </p>
        </motion.footer>
      </main>
    </div>
  );
};

export default App;
