import React, { useState, useEffect } from 'react';

function App() {
  const [apiStatus, setApiStatus] = useState('Checking...');
  const [airports, setAirports] = useState([]);
  const [selectedAirport, setSelectedAirport] = useState('');
  const [prediction, setPrediction] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Test API connection
    fetch('/api/health')
      .then(res => res.json())
      .then(data => {
        setApiStatus('🟢 Connected');
        // Load airports
        return fetch('/api/airports');
      })
      .then(res => res.json())
      .then(data => {
        setAirports(data);
      })
      .catch(err => {
        setApiStatus('🔴 Disconnected');
        console.error('API Error:', err);
      });
  }, []);

  const handlePredict = async () => {
    if (!selectedAirport) return;
    
    setLoading(true);
    try {
      const response = await fetch(`/api/predict/${selectedAirport}`, {
        method: 'POST'
      });
      const data = await response.json();
      setPrediction(data);
    } catch (err) {
      console.error('Prediction error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ 
      minHeight: '100vh', 
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      color: 'white',
      fontFamily: 'Arial, sans-serif',
      padding: '20px'
    }}>
      <div style={{ maxWidth: '800px', margin: '0 auto' }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: '40px' }}>
          <h1 style={{ fontSize: '3rem', marginBottom: '10px' }}>✈️</h1>
          <h2 style={{ fontSize: '2.5rem', marginBottom: '10px' }}>Flight Risk Assessment System v2.0</h2>
          <p style={{ fontSize: '1.2rem', opacity: '0.9' }}>Modern React + Spring Boot Architecture</p>
        </div>

        {/* Status Panel */}
        <div style={{ 
          background: 'rgba(255,255,255,0.1)', 
          padding: '20px', 
          borderRadius: '15px',
          marginBottom: '30px',
          backdropFilter: 'blur(10px)'
        }}>
          <h3 style={{ marginBottom: '15px' }}>🔧 System Status</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '15px' }}>
            <div>
              <strong>Backend API:</strong><br />
              {apiStatus}
            </div>
            <div>
              <strong>Airports Loaded:</strong><br />
              🛫 {airports.length} airports
            </div>
            <div>
              <strong>Frontend:</strong><br />
              🟢 React Running
            </div>
          </div>
        </div>

        {/* Risk Assessment Panel */}
        <div style={{ 
          background: 'rgba(255,255,255,0.1)', 
          padding: '25px', 
          borderRadius: '15px',
          marginBottom: '30px',
          backdropFilter: 'blur(10px)'
        }}>
          <h3 style={{ marginBottom: '20px' }}>🎯 Risk Assessment</h3>
          
          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', marginBottom: '10px', fontWeight: 'bold' }}>
              Select Airport:
            </label>
            <select 
              value={selectedAirport}
              onChange={(e) => setSelectedAirport(e.target.value)}
              style={{
                padding: '10px 15px',
                borderRadius: '8px',
                border: 'none',
                fontSize: '16px',
                width: '200px',
                color: '#333'
              }}
            >
              <option value="">Choose Airport...</option>
              {airports.map(airport => (
                <option key={airport.code} value={airport.code}>
                  {airport.code} - {airport.city}
                </option>
              ))}
            </select>
          </div>

          <button
            onClick={handlePredict}
            disabled={!selectedAirport || loading}
            style={{
              background: loading ? '#666' : 'linear-gradient(45deg, #ff6b6b, #ee5a24)',
              color: 'white',
              border: 'none',
              padding: '12px 25px',
              borderRadius: '8px',
              fontSize: '16px',
              fontWeight: 'bold',
              cursor: loading ? 'not-allowed' : 'pointer',
              transition: 'all 0.3s ease'
            }}
          >
            {loading ? '🔄 Analyzing...' : '🔮 Predict Risk'}
          </button>
        </div>

        {/* Prediction Results */}
        {prediction && (
          <div style={{ 
            background: 'rgba(255,255,255,0.1)', 
            padding: '25px', 
            borderRadius: '15px',
            backdropFilter: 'blur(10px)'
          }}>
            <h3 style={{ marginBottom: '20px' }}>📊 Prediction Results</h3>
            
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px' }}>
              <div>
                <h4 style={{ marginBottom: '10px' }}>🛫 Airport</h4>
                <p style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>{prediction.airport}</p>
              </div>
              
              <div>
                <h4 style={{ marginBottom: '10px' }}>⚠️ Risk Level</h4>
                <p style={{ 
                  fontSize: '1.2rem', 
                  fontWeight: 'bold',
                  color: prediction.riskLevel?.includes('High') ? '#ff6b6b' : 
                        prediction.riskLevel?.includes('Medium') ? '#ffa726' : '#4caf50'
                }}>
                  {prediction.riskLevel}
                </p>
              </div>
              
              <div>
                <h4 style={{ marginBottom: '10px' }}>📈 Risk Score</h4>
                <p style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>
                  {(prediction.riskScore * 100).toFixed(1)}%
                </p>
              </div>
            </div>

            <div style={{ marginTop: '20px' }}>
              <h4 style={{ marginBottom: '10px' }}>🌤️ Weather Conditions</h4>
              <p style={{ fontSize: '1rem', opacity: '0.9' }}>{prediction.weather}</p>
            </div>

            {prediction.breakdown && (
              <div style={{ marginTop: '20px' }}>
                <h4 style={{ marginBottom: '15px' }}>🔍 Risk Breakdown</h4>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '10px' }}>
                  <div>Birdstrike: {(prediction.breakdown.birdstrikeScore * 100).toFixed(0)}%</div>
                  <div>Weather: {(prediction.breakdown.weatherScore * 100).toFixed(0)}%</div>
                  <div>Traffic: {(prediction.breakdown.trafficScore * 100).toFixed(0)}%</div>
                  <div>Historical: {(prediction.breakdown.historicalScore * 100).toFixed(0)}%</div>
                </div>
                <div style={{ marginTop: '10px' }}>
                  <strong>Confidence: {(prediction.breakdown.confidence * 100).toFixed(0)}%</strong>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Footer */}
        <div style={{ textAlign: 'center', marginTop: '40px', opacity: '0.8' }}>
          <p>🎉 Migration from JavaFX to React + Spring Boot: COMPLETE!</p>
          <p style={{ fontSize: '0.9rem', marginTop: '10px' }}>
            Full TypeScript version available in App.tsx with advanced UI components
          </p>
        </div>
      </div>
    </div>
  );
}

export default App;
