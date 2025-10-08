import React, { useState, useEffect } from 'react';

function App() {
  const [apiStatus, setApiStatus] = useState('Checking...');
  const [airports, setAirports] = useState([]);
  const [selectedAirport, setSelectedAirport] = useState('');
  const [prediction, setPrediction] = useState(null);
  const [loading, setLoading] = useState(false);
  const [predictions, setPredictions] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [timeTravel, setTimeTravel] = useState({
    enabled: false,
    targetDate: new Date().toISOString().split('T')[0],
    targetTime: new Date().toTimeString().split(' ')[0].slice(0, 5)
  });
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState('predict');

  useEffect(() => {
    loadInitialData();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const loadInitialData = async () => {
    try {
      // Test API connection
      const healthRes = await fetch('/api/health');
      await healthRes.json();
      setApiStatus('🟢 Connected');
      
      // Load airports
      const airportsRes = await fetch('/api/airports');
      const airportsData = await airportsRes.json();
      setAirports(airportsData);
      
      // Load predictions and statistics
      await loadPredictions();
      await loadStatistics();
    } catch (err) {
      setApiStatus('🔴 Disconnected');
      console.error('API Error:', err);
    }
  };

  const loadPredictions = async () => {
    try {
      const res = await fetch('/api/predictions');
      const data = await res.json();
      setPredictions(data);
    } catch (err) {
      console.error('Failed to load predictions:', err);
    }
  };

  const loadStatistics = async () => {
    try {
      const res = await fetch('/api/statistics');
      const data = await res.json();
      setStatistics(data);
    } catch (err) {
      console.error('Failed to load statistics:', err);
    }
  };

  const handlePredict = async () => {
    if (!selectedAirport) return;
    
    setLoading(true);
    try {
      let url = `/api/predict/${selectedAirport}`;
      if (timeTravel.enabled) {
        const targetDateTime = `${timeTravel.targetDate}T${timeTravel.targetTime}:00`;
        url += `?targetTime=${encodeURIComponent(targetDateTime)}`;
      }
      
      const response = await fetch(url, {
        method: 'POST'
      });
      const data = await response.json();
      setPrediction(data);
      
      // Refresh predictions and statistics
      await loadPredictions();
      await loadStatistics();
    } catch (err) {
      console.error('Prediction error:', err);
    } finally {
      setLoading(false);
    }
  };

  const clearPredictions = async () => {
    try {
      await fetch('/api/predictions', { method: 'DELETE' });
      setPredictions([]);
      setPrediction(null);
      await loadStatistics();
    } catch (err) {
      console.error('Failed to clear predictions:', err);
    }
  };

  const filteredAirports = airports.filter(airport => 
    airport.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
    airport.city.toLowerCase().includes(searchTerm.toLowerCase()) ||
    airport.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div style={{ 
      minHeight: '100vh', 
      background: 'linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #667eea 100%)',
      color: 'white',
      fontFamily: 'Inter, -apple-system, BlinkMacSystemFont, sans-serif',
      padding: '20px'
    }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: '40px' }}>
          <h1 style={{ fontSize: '4rem', marginBottom: '10px', textShadow: '2px 2px 4px rgba(0,0,0,0.3)' }}>🛫</h1>
          <h2 style={{ fontSize: '2.8rem', marginBottom: '10px', fontWeight: '700', textShadow: '1px 1px 2px rgba(0,0,0,0.3)' }}>Flight Risk Assessment System v3.0</h2>
          <p style={{ fontSize: '1.3rem', opacity: '0.9', fontWeight: '300' }}>Advanced Analytics • Time Travel • Real-time Predictions</p>
        </div>

        {/* Navigation Tabs */}
        <div style={{ 
          display: 'flex', 
          justifyContent: 'center', 
          marginBottom: '30px',
          background: 'rgba(255,255,255,0.1)',
          borderRadius: '15px',
          padding: '5px',
          backdropFilter: 'blur(10px)'
        }}>
          {['predict', 'history', 'statistics', 'time-travel'].map(tab => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              style={{
                background: activeTab === tab ? 'rgba(255,255,255,0.2)' : 'transparent',
                color: 'white',
                border: 'none',
                padding: '12px 24px',
                borderRadius: '10px',
                fontSize: '16px',
                fontWeight: activeTab === tab ? 'bold' : 'normal',
                cursor: 'pointer',
                transition: 'all 0.3s ease',
                textTransform: 'capitalize'
              }}
            >
              {tab.replace('-', ' ')} {tab === 'predict' && '🎯'} {tab === 'history' && '📊'} {tab === 'statistics' && '📈'} {tab === 'time-travel' && '⏰'}
            </button>
          ))}
        </div>

        {/* Status Panel */}
        <div style={{ 
          background: 'rgba(255,255,255,0.1)', 
          padding: '20px', 
          borderRadius: '15px',
          marginBottom: '30px',
          backdropFilter: 'blur(10px)',
          border: '1px solid rgba(255,255,255,0.2)'
        }}>
          <h3 style={{ marginBottom: '15px', fontSize: '1.4rem', fontWeight: '600' }}>🔧 System Status</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '15px' }}>
            <div style={{ padding: '10px', background: 'rgba(255,255,255,0.05)', borderRadius: '8px' }}>
              <strong>Backend API:</strong><br />
              {apiStatus}
            </div>
            <div style={{ padding: '10px', background: 'rgba(255,255,255,0.05)', borderRadius: '8px' }}>
              <strong>Airports Loaded:</strong><br />
              🛫 {airports.length} airports
            </div>
            <div style={{ padding: '10px', background: 'rgba(255,255,255,0.05)', borderRadius: '8px' }}>
              <strong>Frontend:</strong><br />
              🟢 React v3.0 Running
            </div>
            {statistics && (
              <div style={{ padding: '10px', background: 'rgba(255,255,255,0.05)', borderRadius: '8px' }}>
                <strong>Predictions:</strong><br />
                📊 {statistics.total} total
              </div>
            )}
          </div>
        </div>

        {/* Main Content based on active tab */}
        {activeTab === 'predict' && (
          <>
            {/* Risk Assessment Panel */}
            <div style={{ 
              background: 'rgba(255,255,255,0.1)', 
              padding: '25px', 
              borderRadius: '15px',
              marginBottom: '30px',
              backdropFilter: 'blur(10px)',
              border: '1px solid rgba(255,255,255,0.2)'
            }}>
              <h3 style={{ marginBottom: '20px', fontSize: '1.5rem', fontWeight: '600' }}>🎯 Risk Assessment</h3>
              
              {/* Airport Search */}
              <div style={{ marginBottom: '20px' }}>
                <label style={{ display: 'block', marginBottom: '10px', fontWeight: 'bold' }}>
                  Search Airports:
                </label>
                <input
                  type="text"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  placeholder="Search by code, city, or name..."
                  style={{
                    padding: '12px 15px',
                    borderRadius: '8px',
                    border: 'none',
                    fontSize: '16px',
                    width: '100%',
                    maxWidth: '400px',
                    color: '#333',
                    background: 'rgba(255,255,255,0.9)'
                  }}
                />
              </div>

              <div style={{ marginBottom: '20px' }}>
                <label style={{ display: 'block', marginBottom: '10px', fontWeight: 'bold' }}>
                  Select Airport:
                </label>
                <select 
                  value={selectedAirport}
                  onChange={(e) => setSelectedAirport(e.target.value)}
                  style={{
                    padding: '12px 15px',
                    borderRadius: '8px',
                    border: 'none',
                    fontSize: '16px',
                    width: '100%',
                    maxWidth: '400px',
                    color: '#333',
                    background: 'rgba(255,255,255,0.9)'
                  }}
                >
                  <option value="">Choose Airport...</option>
                  {filteredAirports.map(airport => (
                    <option key={airport.code} value={airport.code}>
                      {airport.code} - {airport.city}, {airport.country}
                    </option>
                  ))}
                </select>
              </div>

              {/* Time Travel Toggle */}
              <div style={{ marginBottom: '20px', padding: '15px', background: 'rgba(255,255,255,0.05)', borderRadius: '10px' }}>
                <label style={{ display: 'flex', alignItems: 'center', marginBottom: '10px', fontWeight: 'bold', cursor: 'pointer' }}>
                  <input
                    type="checkbox"
                    checked={timeTravel.enabled}
                    onChange={(e) => setTimeTravel({...timeTravel, enabled: e.target.checked})}
                    style={{ marginRight: '10px', transform: 'scale(1.2)' }}
                  />
                  ⏰ Enable Time Travel Prediction
                </label>
                {timeTravel.enabled && (
                  <div style={{ display: 'flex', gap: '15px', marginTop: '10px' }}>
                    <div>
                      <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px' }}>Target Date:</label>
                      <input
                        type="date"
                        value={timeTravel.targetDate}
                        onChange={(e) => setTimeTravel({...timeTravel, targetDate: e.target.value})}
                        style={{
                          padding: '8px',
                          borderRadius: '6px',
                          border: 'none',
                          fontSize: '14px',
                          color: '#333'
                        }}
                      />
                    </div>
                    <div>
                      <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px' }}>Target Time:</label>
                      <input
                        type="time"
                        value={timeTravel.targetTime}
                        onChange={(e) => setTimeTravel({...timeTravel, targetTime: e.target.value})}
                        style={{
                          padding: '8px',
                          borderRadius: '6px',
                          border: 'none',
                          fontSize: '14px',
                          color: '#333'
                        }}
                      />
                    </div>
                  </div>
                )}
              </div>

              <button
                onClick={handlePredict}
                disabled={!selectedAirport || loading}
                style={{
                  background: loading ? '#666' : 'linear-gradient(45deg, #ff6b6b, #ee5a24)',
                  color: 'white',
                  border: 'none',
                  padding: '15px 30px',
                  borderRadius: '10px',
                  fontSize: '18px',
                  fontWeight: 'bold',
                  cursor: loading ? 'not-allowed' : 'pointer',
                  transition: 'all 0.3s ease',
                  boxShadow: '0 4px 15px rgba(0,0,0,0.2)'
                }}
              >
                {loading ? '🔄 Analyzing...' : timeTravel.enabled ? '🔮 Predict Risk (Time Travel)' : '🔮 Predict Risk'}
              </button>
            </div>
          </>
        )}

        {/* History Tab */}
        {activeTab === 'history' && (
          <div style={{ 
            background: 'rgba(255,255,255,0.1)', 
            padding: '25px', 
            borderRadius: '15px',
            marginBottom: '30px',
            backdropFilter: 'blur(10px)',
            border: '1px solid rgba(255,255,255,0.2)'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h3 style={{ fontSize: '1.5rem', fontWeight: '600' }}>📊 Prediction History</h3>
              <button
                onClick={clearPredictions}
                style={{
                  background: 'linear-gradient(45deg, #e74c3c, #c0392b)',
                  color: 'white',
                  border: 'none',
                  padding: '8px 16px',
                  borderRadius: '6px',
                  fontSize: '14px',
                  cursor: 'pointer'
                }}
              >
                🗑️ Clear All
              </button>
            </div>
            
            {predictions.length === 0 ? (
              <p style={{ textAlign: 'center', opacity: '0.7', fontSize: '1.1rem' }}>No predictions yet. Make your first prediction!</p>
            ) : (
              <div style={{ display: 'grid', gap: '15px' }}>
                {predictions.slice(0, 10).map((pred, index) => (
                  <div key={index} style={{
                    background: 'rgba(255,255,255,0.05)',
                    padding: '15px',
                    borderRadius: '10px',
                    border: '1px solid rgba(255,255,255,0.1)'
                  }}>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '10px' }}>
                      <div><strong>Airport:</strong> {pred.airport}</div>
                      <div><strong>Risk:</strong> <span style={{
                        color: pred.riskLevel?.includes('High') ? '#ff6b6b' : 
                              pred.riskLevel?.includes('Medium') ? '#ffa726' : '#4caf50'
                      }}>{pred.riskLevel}</span></div>
                      <div><strong>Score:</strong> {(pred.riskScore * 100).toFixed(1)}%</div>
                      <div><strong>Time:</strong> {new Date(pred.timestamp).toLocaleString()}</div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Statistics Tab */}
        {activeTab === 'statistics' && (
          <div style={{ 
            background: 'rgba(255,255,255,0.1)', 
            padding: '25px', 
            borderRadius: '15px',
            marginBottom: '30px',
            backdropFilter: 'blur(10px)',
            border: '1px solid rgba(255,255,255,0.2)'
          }}>
            <h3 style={{ marginBottom: '20px', fontSize: '1.5rem', fontWeight: '600' }}>📈 Risk Statistics</h3>
            
            {statistics ? (
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px' }}>
                <div style={{ 
                  background: 'rgba(255,255,255,0.05)', 
                  padding: '20px', 
                  borderRadius: '10px',
                  textAlign: 'center'
                }}>
                  <div style={{ fontSize: '2.5rem', marginBottom: '10px' }}>📊</div>
                  <div style={{ fontSize: '2rem', fontWeight: 'bold' }}>{statistics.total}</div>
                  <div style={{ opacity: '0.8' }}>Total Predictions</div>
                </div>
                <div style={{ 
                  background: 'rgba(255,107,107,0.2)', 
                  padding: '20px', 
                  borderRadius: '10px',
                  textAlign: 'center'
                }}>
                  <div style={{ fontSize: '2.5rem', marginBottom: '10px' }}>🔴</div>
                  <div style={{ fontSize: '2rem', fontWeight: 'bold' }}>{statistics.highRisk}</div>
                  <div style={{ opacity: '0.8' }}>High Risk</div>
                </div>
                <div style={{ 
                  background: 'rgba(255,167,38,0.2)', 
                  padding: '20px', 
                  borderRadius: '10px',
                  textAlign: 'center'
                }}>
                  <div style={{ fontSize: '2.5rem', marginBottom: '10px' }}>🟡</div>
                  <div style={{ fontSize: '2rem', fontWeight: 'bold' }}>{statistics.mediumRisk}</div>
                  <div style={{ opacity: '0.8' }}>Medium Risk</div>
                </div>
                <div style={{ 
                  background: 'rgba(76,175,80,0.2)', 
                  padding: '20px', 
                  borderRadius: '10px',
                  textAlign: 'center'
                }}>
                  <div style={{ fontSize: '2.5rem', marginBottom: '10px' }}>🟢</div>
                  <div style={{ fontSize: '2rem', fontWeight: 'bold' }}>{statistics.lowRisk}</div>
                  <div style={{ opacity: '0.8' }}>Low Risk</div>
                </div>
              </div>
            ) : (
              <p style={{ textAlign: 'center', opacity: '0.7' }}>Loading statistics...</p>
            )}
          </div>
        )}

        {/* Time Travel Tab */}
        {activeTab === 'time-travel' && (
          <div style={{ 
            background: 'rgba(255,255,255,0.1)', 
            padding: '25px', 
            borderRadius: '15px',
            marginBottom: '30px',
            backdropFilter: 'blur(10px)',
            border: '1px solid rgba(255,255,255,0.2)'
          }}>
            <h3 style={{ marginBottom: '20px', fontSize: '1.5rem', fontWeight: '600' }}>⏰ Time Travel Feature</h3>
            
            <div style={{ 
              background: 'rgba(255,255,255,0.05)', 
              padding: '20px', 
              borderRadius: '10px',
              marginBottom: '20px'
            }}>
              <h4 style={{ marginBottom: '15px', fontSize: '1.2rem' }}>🔮 How Time Travel Works</h4>
              <ul style={{ paddingLeft: '20px', lineHeight: '1.6' }}>
                <li>Select any future or past date and time</li>
                <li>The system simulates weather conditions for that specific time</li>
                <li>Historical bird migration patterns are analyzed</li>
                <li>Traffic predictions are adjusted based on the target time</li>
                <li>Risk assessment is calculated as if you were making the prediction at that exact moment</li>
              </ul>
            </div>

            <div style={{ 
              background: 'rgba(255,255,255,0.05)', 
              padding: '20px', 
              borderRadius: '10px'
            }}>
              <h4 style={{ marginBottom: '15px', fontSize: '1.2rem' }}>🎯 Use Cases</h4>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '15px' }}>
                <div style={{ padding: '15px', background: 'rgba(255,255,255,0.05)', borderRadius: '8px' }}>
                  <strong>📅 Flight Planning</strong><br />
                  Plan flights weeks in advance with accurate risk predictions
                </div>
                <div style={{ padding: '15px', background: 'rgba(255,255,255,0.05)', borderRadius: '8px' }}>
                  <strong>📊 Historical Analysis</strong><br />
                  Analyze past incidents and validate model accuracy
                </div>
                <div style={{ padding: '15px', background: 'rgba(255,255,255,0.05)', borderRadius: '8px' }}>
                  <strong>🌤️ Weather Patterns</strong><br />
                  Study seasonal weather impact on flight safety
                </div>
                <div style={{ padding: '15px', background: 'rgba(255,255,255,0.05)', borderRadius: '8px' }}>
                  <strong>🦅 Migration Seasons</strong><br />
                  Understand bird migration impact on different airports
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Prediction Results */}
        {prediction && (
          <div style={{ 
            background: 'rgba(255,255,255,0.1)', 
            padding: '25px', 
            borderRadius: '15px',
            backdropFilter: 'blur(10px)',
            border: '1px solid rgba(255,255,255,0.2)',
            boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
          }}>
            <h3 style={{ marginBottom: '20px', fontSize: '1.5rem', fontWeight: '600' }}>📊 Prediction Results</h3>
            
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginBottom: '20px' }}>
              <div style={{ background: 'rgba(255,255,255,0.05)', padding: '15px', borderRadius: '10px' }}>
                <h4 style={{ marginBottom: '10px', fontSize: '1.1rem' }}>🛫 Airport</h4>
                <p style={{ fontSize: '1.3rem', fontWeight: 'bold' }}>{prediction.airport}</p>
              </div>
              
              <div style={{ background: 'rgba(255,255,255,0.05)', padding: '15px', borderRadius: '10px' }}>
                <h4 style={{ marginBottom: '10px', fontSize: '1.1rem' }}>⚠️ Risk Level</h4>
                <p style={{ 
                  fontSize: '1.3rem', 
                  fontWeight: 'bold',
                  color: prediction.riskLevel?.includes('High') ? '#ff6b6b' : 
                        prediction.riskLevel?.includes('Medium') ? '#ffa726' : '#4caf50'
                }}>
                  {prediction.riskLevel}
                </p>
              </div>
              
              <div style={{ background: 'rgba(255,255,255,0.05)', padding: '15px', borderRadius: '10px' }}>
                <h4 style={{ marginBottom: '10px', fontSize: '1.1rem' }}>📈 Risk Score</h4>
                <p style={{ fontSize: '1.3rem', fontWeight: 'bold' }}>
                  {(prediction.riskScore * 100).toFixed(1)}%
                </p>
              </div>

              {timeTravel.enabled && (
                <div style={{ background: 'rgba(255,255,255,0.05)', padding: '15px', borderRadius: '10px' }}>
                  <h4 style={{ marginBottom: '10px', fontSize: '1.1rem' }}>⏰ Time Travel</h4>
                  <p style={{ fontSize: '1rem', fontWeight: 'bold' }}>
                    {timeTravel.targetDate} {timeTravel.targetTime}
                  </p>
                </div>
              )}
            </div>

            <div style={{ marginBottom: '20px', background: 'rgba(255,255,255,0.05)', padding: '15px', borderRadius: '10px' }}>
              <h4 style={{ marginBottom: '10px', fontSize: '1.1rem' }}>🌤️ Weather Conditions</h4>
              <p style={{ fontSize: '1rem', opacity: '0.9', lineHeight: '1.5' }}>{prediction.weather}</p>
            </div>

            {prediction.breakdown && (
              <div style={{ background: 'rgba(255,255,255,0.05)', padding: '15px', borderRadius: '10px' }}>
                <h4 style={{ marginBottom: '15px', fontSize: '1.1rem' }}>🔍 Risk Breakdown</h4>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '15px', marginBottom: '15px' }}>
                  <div style={{ padding: '10px', background: 'rgba(255,255,255,0.05)', borderRadius: '6px' }}>
                    <strong>🦅 Birdstrike:</strong> {(prediction.breakdown.birdstrikeScore * 100).toFixed(0)}%
                  </div>
                  <div style={{ padding: '10px', background: 'rgba(255,255,255,0.05)', borderRadius: '6px' }}>
                    <strong>🌤️ Weather:</strong> {(prediction.breakdown.weatherScore * 100).toFixed(0)}%
                  </div>
                  <div style={{ padding: '10px', background: 'rgba(255,255,255,0.05)', borderRadius: '6px' }}>
                    <strong>✈️ Traffic:</strong> {(prediction.breakdown.trafficScore * 100).toFixed(0)}%
                  </div>
                  <div style={{ padding: '10px', background: 'rgba(255,255,255,0.05)', borderRadius: '6px' }}>
                    <strong>📊 Historical:</strong> {(prediction.breakdown.historicalScore * 100).toFixed(0)}%
                  </div>
                </div>
                <div style={{ textAlign: 'center', padding: '10px', background: 'rgba(255,255,255,0.05)', borderRadius: '6px' }}>
                  <strong>🎯 Confidence: {(prediction.breakdown.confidence * 100).toFixed(0)}%</strong>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Footer */}
        <div style={{ textAlign: 'center', marginTop: '40px', opacity: '0.8' }}>
          <div style={{ 
            background: 'rgba(255,255,255,0.05)', 
            padding: '20px', 
            borderRadius: '15px',
            marginBottom: '20px'
          }}>
            <p style={{ fontSize: '1.2rem', marginBottom: '10px' }}>🎉 Flight Risk Assessment System v3.0</p>
            <p style={{ fontSize: '1rem', marginBottom: '10px' }}>
              ✨ Enhanced with Time Travel • Advanced Analytics • Modern UI
            </p>
            <p style={{ fontSize: '0.9rem', opacity: '0.7' }}>
              React + Spring Boot + JDBC Architecture • {airports.length} Airports • Real-time Predictions
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
