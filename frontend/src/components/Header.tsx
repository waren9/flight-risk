import React from 'react';
import { Plane, Shield, Activity } from 'lucide-react';
import { motion } from 'framer-motion';

const Header: React.FC = () => {
  return (
    <motion.header 
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.6 }}
      className="gradient-primary text-white shadow-2xl"
    >
      <div className="container mx-auto px-6 py-8">
        <div className="flex items-center justify-between">
          <motion.div 
            className="flex items-center space-x-4"
            whileHover={{ scale: 1.02 }}
            transition={{ type: "spring", stiffness: 300 }}
          >
            <div className="bg-white/20 p-3 rounded-full backdrop-blur-sm">
              <Plane className="h-8 w-8 text-white" />
            </div>
            <div>
              <h1 className="text-3xl font-bold tracking-tight">
                Flight Risk Assessment System
              </h1>
              <p className="text-white/90 text-lg font-medium">
                Advanced Aviation Safety Analytics Platform v2.0
              </p>
            </div>
          </motion.div>
          
          <div className="flex items-center space-x-6">
            <motion.div 
              className="flex items-center space-x-2 bg-white/10 px-4 py-2 rounded-full backdrop-blur-sm"
              whileHover={{ scale: 1.05 }}
            >
              <Shield className="h-5 w-5" />
              <span className="font-medium">Real-time Analysis</span>
            </motion.div>
            
            <motion.div 
              className="flex items-center space-x-2 bg-white/10 px-4 py-2 rounded-full backdrop-blur-sm"
              whileHover={{ scale: 1.05 }}
            >
              <Activity className="h-5 w-5" />
              <span className="font-medium">Live Data</span>
            </motion.div>
          </div>
        </div>
      </div>
    </motion.header>
  );
};

export default Header;
