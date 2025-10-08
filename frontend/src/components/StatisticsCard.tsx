import React from 'react';
import { motion } from 'framer-motion';
import { LucideIcon } from 'lucide-react';

interface StatisticsCardProps {
  title: string;
  value: number;
  icon: LucideIcon;
  color: 'success' | 'warning' | 'danger' | 'primary';
  trend?: {
    value: number;
    isPositive: boolean;
  };
}

const colorClasses = {
  success: {
    bg: 'bg-success-50',
    icon: 'text-success-600',
    text: 'text-success-700',
    gradient: 'gradient-success'
  },
  warning: {
    bg: 'bg-warning-50',
    icon: 'text-warning-600',
    text: 'text-warning-700',
    gradient: 'gradient-warning'
  },
  danger: {
    bg: 'bg-danger-50',
    icon: 'text-danger-600',
    text: 'text-danger-700',
    gradient: 'gradient-danger'
  },
  primary: {
    bg: 'bg-primary-50',
    icon: 'text-primary-600',
    text: 'text-primary-700',
    gradient: 'gradient-primary'
  }
};

const StatisticsCard: React.FC<StatisticsCardProps> = ({ 
  title, 
  value, 
  icon: Icon, 
  color, 
  trend 
}) => {
  const classes = colorClasses[color];

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      whileHover={{ scale: 1.02, y: -2 }}
      transition={{ duration: 0.2 }}
      className="bg-white rounded-2xl p-6 card-shadow border border-gray-100"
    >
      <div className="flex items-center justify-between">
        <div className="flex-1">
          <p className="text-sm font-medium text-gray-600 mb-1">{title}</p>
          <div className="flex items-baseline space-x-2">
            <motion.p 
              className="text-3xl font-bold text-gray-900"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.2 }}
            >
              {value.toLocaleString()}
            </motion.p>
            {trend && (
              <span className={`text-sm font-medium ${
                trend.isPositive ? 'text-success-600' : 'text-danger-600'
              }`}>
                {trend.isPositive ? '+' : ''}{trend.value}%
              </span>
            )}
          </div>
        </div>
        
        <motion.div 
          className={`${classes.bg} p-3 rounded-xl`}
          whileHover={{ rotate: 5 }}
          transition={{ type: "spring", stiffness: 300 }}
        >
          <Icon className={`h-6 w-6 ${classes.icon}`} />
        </motion.div>
      </div>
      
      <div className="mt-4">
        <div className="w-full bg-gray-200 rounded-full h-2">
          <motion.div 
            className={`h-2 rounded-full ${classes.gradient}`}
            initial={{ width: 0 }}
            animate={{ width: `${Math.min(value / 10, 100)}%` }}
            transition={{ duration: 1, delay: 0.5 }}
          />
        </div>
      </div>
    </motion.div>
  );
};

export default StatisticsCard;
