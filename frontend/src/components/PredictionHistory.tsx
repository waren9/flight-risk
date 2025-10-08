import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  History, 
  Trash2, 
  AlertTriangle, 
  CheckCircle, 
  XCircle,
  Clock,
  MapPin,
  BarChart3,
  RefreshCw
} from 'lucide-react';
import { FlightPrediction, RiskLevel } from '../types';
import { predictionService } from '../services/api';

interface PredictionHistoryProps {
  predictions: FlightPrediction[];
  onRefresh: () => void;
  onClearHistory: () => void;
}

const PredictionHistory: React.FC<PredictionHistoryProps> = ({ 
  predictions, 
  onRefresh, 
  onClearHistory 
}) => {
  const [isClearing, setIsClearing] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const handleClearHistory = async () => {
    if (window.confirm('Are you sure you want to clear all prediction history? This action cannot be undone.')) {
      setIsClearing(true);
      try {
        await predictionService.clearHistory();
        onClearHistory();
      } catch (error) {
        console.error('Failed to clear history:', error);
      } finally {
        setIsClearing(false);
      }
    }
  };

  const handleDeletePrediction = async (id: number) => {
    setDeletingId(id);
    try {
      await predictionService.delete(id);
      onRefresh();
    } catch (error) {
      console.error('Failed to delete prediction:', error);
    } finally {
      setDeletingId(null);
    }
  };

  const getRiskColor = (riskLevel: string) => {
    switch (riskLevel) {
      case 'High Risk':
        return 'text-danger-600 bg-danger-50';
      case 'Medium Risk':
        return 'text-warning-600 bg-warning-50';
      case 'Low Risk':
        return 'text-success-600 bg-success-50';
      default:
        return 'text-gray-600 bg-gray-50';
    }
  };

  const getRiskIcon = (riskLevel: string) => {
    switch (riskLevel) {
      case 'High Risk':
        return <XCircle className="h-4 w-4" />;
      case 'Medium Risk':
        return <AlertTriangle className="h-4 w-4" />;
      case 'Low Risk':
        return <CheckCircle className="h-4 w-4" />;
      default:
        return <AlertTriangle className="h-4 w-4" />;
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay: 0.2 }}
      className="bg-white rounded-2xl p-8 card-shadow border border-gray-100"
    >
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center space-x-3">
          <div className="bg-primary-100 p-2 rounded-lg">
            <History className="h-6 w-6 text-primary-600" />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Prediction History</h2>
            <p className="text-gray-600">{predictions.length} total predictions</p>
          </div>
        </div>

        <div className="flex items-center space-x-3">
          <motion.button
            onClick={onRefresh}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="flex items-center space-x-2 px-4 py-2 bg-primary-100 text-primary-700 rounded-lg hover:bg-primary-200 transition-colors"
          >
            <RefreshCw className="h-4 w-4" />
            <span>Refresh</span>
          </motion.button>

          <motion.button
            onClick={handleClearHistory}
            disabled={isClearing || predictions.length === 0}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="flex items-center space-x-2 px-4 py-2 bg-danger-100 text-danger-700 rounded-lg hover:bg-danger-200 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Trash2 className="h-4 w-4" />
            <span>{isClearing ? 'Clearing...' : 'Clear All'}</span>
          </motion.button>
        </div>
      </div>

      {/* Predictions List */}
      <div className="space-y-4 max-h-96 overflow-y-auto">
        <AnimatePresence>
          {predictions.length === 0 ? (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="text-center py-12"
            >
              <History className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-500 text-lg">No predictions yet</p>
              <p className="text-gray-400">Make your first risk assessment to see results here</p>
            </motion.div>
          ) : (
            predictions.map((prediction, index) => (
              <motion.div
                key={prediction.id || index}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: 20 }}
                transition={{ duration: 0.3, delay: index * 0.05 }}
                className="bg-gray-50 rounded-xl p-4 hover:bg-gray-100 transition-colors"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4 flex-1">
                    {/* Airport */}
                    <div className="flex items-center space-x-2">
                      <MapPin className="h-4 w-4 text-gray-500" />
                      <span className="font-semibold text-gray-900">{prediction.airport}</span>
                    </div>

                    {/* Risk Level */}
                    <div className={`flex items-center space-x-2 px-3 py-1 rounded-full ${getRiskColor(prediction.riskLevel)}`}>
                      {getRiskIcon(prediction.riskLevel)}
                      <span className="font-medium text-sm">{prediction.riskLevel}</span>
                    </div>

                    {/* Risk Score */}
                    <div className="flex items-center space-x-2">
                      <BarChart3 className="h-4 w-4 text-gray-500" />
                      <span className="text-sm text-gray-700">
                        Score: {prediction.riskScore.toFixed(2)}
                      </span>
                    </div>

                    {/* Timestamp */}
                    <div className="flex items-center space-x-2 text-gray-500">
                      <Clock className="h-4 w-4" />
                      <span className="text-sm">{formatDate(prediction.predictionTime)}</span>
                    </div>
                  </div>

                  {/* Delete Button */}
                  {prediction.id && (
                    <motion.button
                      onClick={() => handleDeletePrediction(prediction.id!)}
                      disabled={deletingId === prediction.id}
                      whileHover={{ scale: 1.1 }}
                      whileTap={{ scale: 0.9 }}
                      className="p-2 text-gray-400 hover:text-danger-600 hover:bg-danger-50 rounded-lg transition-colors disabled:opacity-50"
                    >
                      <Trash2 className="h-4 w-4" />
                    </motion.button>
                  )}
                </div>

                {/* Weather Info */}
                {prediction.weather && (
                  <div className="mt-3 pt-3 border-t border-gray-200">
                    <p className="text-sm text-gray-600">{prediction.weather}</p>
                  </div>
                )}
              </motion.div>
            ))
          )}
        </AnimatePresence>
      </div>
    </motion.div>
  );
};

export default PredictionHistory;
