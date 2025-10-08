import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Plane, 
  Cloud, 
  AlertTriangle, 
  CheckCircle, 
  XCircle, 
  Loader2,
  BarChart3,
  MapPin
} from 'lucide-react';
import { predictionService } from '../services/api';
import { PredictionResponse, RiskLevel } from '../types';

interface RiskAssessmentProps {
  airports: string[];
  onPredictionComplete: (prediction: PredictionResponse) => void;
}

const RiskAssessment: React.FC<RiskAssessmentProps> = ({ airports, onPredictionComplete }) => {
  const [selectedAirport, setSelectedAirport] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);
  const [currentPrediction, setCurrentPrediction] = useState<PredictionResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handlePredict = async () => {
    if (!selectedAirport) {
      setError('Please select an airport');
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const prediction = await predictionService.predict({ airport: selectedAirport });
      setCurrentPrediction(prediction);
      onPredictionComplete(prediction);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to get prediction');
    } finally {
      setIsLoading(false);
    }
  };

  const getRiskColor = (riskLevel: RiskLevel) => {
    switch (riskLevel) {
      case 'High Risk':
        return 'text-danger-600 bg-danger-50 border-danger-200';
      case 'Medium Risk':
        return 'text-warning-600 bg-warning-50 border-warning-200';
      case 'Low Risk':
        return 'text-success-600 bg-success-50 border-success-200';
      default:
        return 'text-gray-600 bg-gray-50 border-gray-200';
    }
  };

  const getRiskIcon = (riskLevel: RiskLevel) => {
    switch (riskLevel) {
      case 'High Risk':
        return <XCircle className="h-5 w-5" />;
      case 'Medium Risk':
        return <AlertTriangle className="h-5 w-5" />;
      case 'Low Risk':
        return <CheckCircle className="h-5 w-5" />;
      default:
        return <AlertTriangle className="h-5 w-5" />;
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="bg-white rounded-2xl p-8 card-shadow border border-gray-100"
    >
      <div className="flex items-center space-x-3 mb-6">
        <div className="bg-primary-100 p-2 rounded-lg">
          <Plane className="h-6 w-6 text-primary-600" />
        </div>
        <h2 className="text-2xl font-bold text-gray-900">Risk Assessment</h2>
      </div>

      <div className="space-y-6">
        {/* Airport Selection */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Select Airport
          </label>
          <div className="relative">
            <MapPin className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <select
              value={selectedAirport}
              onChange={(e) => setSelectedAirport(e.target.value)}
              className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all duration-200 bg-white"
              disabled={isLoading}
            >
              <option value="">Choose an airport...</option>
              {airports.map((airport) => (
                <option key={airport} value={airport}>
                  {airport}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Predict Button */}
        <motion.button
          onClick={handlePredict}
          disabled={!selectedAirport || isLoading}
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          className="w-full gradient-primary text-white py-4 px-6 rounded-xl font-semibold text-lg disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center space-x-2"
        >
          {isLoading ? (
            <>
              <Loader2 className="h-5 w-5 animate-spin" />
              <span>Analyzing Risk...</span>
            </>
          ) : (
            <>
              <BarChart3 className="h-5 w-5" />
              <span>Assess Flight Risk</span>
            </>
          )}
        </motion.button>

        {/* Error Message */}
        <AnimatePresence>
          {error && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              className="bg-danger-50 border border-danger-200 rounded-xl p-4"
            >
              <div className="flex items-center space-x-2">
                <XCircle className="h-5 w-5 text-danger-600" />
                <p className="text-danger-700 font-medium">{error}</p>
              </div>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Current Assessment Results */}
        <AnimatePresence>
          {currentPrediction && (
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              transition={{ duration: 0.3 }}
              className="space-y-4"
            >
              <h3 className="text-lg font-semibold text-gray-900 flex items-center space-x-2">
                <BarChart3 className="h-5 w-5" />
                <span>Assessment Results</span>
              </h3>

              {/* Risk Level */}
              <div className={`p-4 rounded-xl border-2 ${getRiskColor(currentPrediction.riskLevel)}`}>
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    {getRiskIcon(currentPrediction.riskLevel)}
                    <div>
                      <p className="font-semibold text-lg">{currentPrediction.riskLevel}</p>
                      <p className="text-sm opacity-75">Risk Score: {currentPrediction.riskScore.toFixed(2)}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-sm opacity-75">Confidence</p>
                    <p className="font-bold">{(currentPrediction.breakdown.confidence * 100).toFixed(0)}%</p>
                  </div>
                </div>
              </div>

              {/* Weather Info */}
              <div className="bg-blue-50 border border-blue-200 rounded-xl p-4">
                <div className="flex items-center space-x-2 mb-2">
                  <Cloud className="h-5 w-5 text-blue-600" />
                  <p className="font-semibold text-blue-900">Weather Conditions</p>
                </div>
                <p className="text-blue-800">{currentPrediction.weather}</p>
              </div>

              {/* Risk Breakdown */}
              <div className="bg-gray-50 rounded-xl p-4">
                <h4 className="font-semibold text-gray-900 mb-3">Risk Factor Breakdown</h4>
                <div className="space-y-3">
                  {[
                    { label: 'Birdstrike Risk', value: currentPrediction.breakdown.birdstrikeScore, weight: '40%' },
                    { label: 'Weather Risk', value: currentPrediction.breakdown.weatherScore, weight: '35%' },
                    { label: 'Traffic Risk', value: currentPrediction.breakdown.trafficScore, weight: '15%' },
                    { label: 'Historical Risk', value: currentPrediction.breakdown.historicalScore, weight: '10%' },
                  ].map((factor) => (
                    <div key={factor.label} className="flex items-center justify-between">
                      <div className="flex items-center space-x-2">
                        <span className="text-sm font-medium text-gray-700">{factor.label}</span>
                        <span className="text-xs text-gray-500">({factor.weight})</span>
                      </div>
                      <div className="flex items-center space-x-2">
                        <div className="w-20 bg-gray-200 rounded-full h-2">
                          <div 
                            className="bg-primary-500 h-2 rounded-full transition-all duration-500"
                            style={{ width: `${factor.value * 100}%` }}
                          />
                        </div>
                        <span className="text-sm font-semibold text-gray-900 w-12 text-right">
                          {(factor.value * 100).toFixed(0)}%
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </motion.div>
  );
};

export default RiskAssessment;
