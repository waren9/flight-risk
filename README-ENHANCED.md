# ✈️ Flight Risk Assessment System v2.0 - Enhanced Edition

A comprehensive JavaFX + Spring Boot application for aviation safety analytics with real-time weather integration, advanced risk calculation, and modern UI design.

## 🚀 New Features & Enhancements

### 🎨 Modern UI & UX
- **Modern CSS Theme**: Professional gradient-based design with cards, shadows, and animations
- **Responsive Layout**: Optimized for resolutions from 768×1024 to 1920×1080
- **Interactive Tooltips**: Comprehensive help text for all UI elements
- **Smooth Animations**: Fade transitions and hover effects for better user experience
- **Splash Screen**: Professional loading screen with progress indicators

### 🌤️ Enhanced Weather Integration
- **Real OpenWeatherMap API**: Live weather data with configurable API keys
- **Intelligent Caching**: 10-minute cache to reduce API calls and improve performance
- **Graceful Fallbacks**: Automatic fallback to realistic default data when API is unavailable
- **Enhanced Risk Factors**: Visibility, wind speed, and weather conditions analysis

### 📊 Advanced Risk Calculation
- **Weighted Risk Scoring**: Comprehensive algorithm considering multiple factors:
  - Birdstrike Risk (40% weight)
  - Weather Conditions (35% weight)
  - Airport Traffic (15% weight)
  - Historical Data (10% weight)
- **Confidence Scoring**: Risk assessment confidence levels
- **Detailed Breakdown**: Complete risk factor analysis in prediction results

### ⚡ Performance Optimizations
- **Background Processing**: Non-blocking UI with async task execution
- **Statistics Caching**: Atomic counters for instant statistics updates
- **Smart Data Management**: Efficient database operations with listeners
- **Memory Optimization**: Reduced object creation and improved garbage collection

### 🛠️ Technical Improvements
- **Error Handling**: Comprehensive error management with user-friendly messages
- **Logging**: Structured logging with appropriate levels (INFO, WARN, ERROR)
- **Configuration**: Environment variable support for API keys and settings
- **Data Validation**: Input validation and data cleansing

## 🏃‍♂️ Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- Optional: OpenWeatherMap API key for live weather data

### Running the Application

#### JavaFX GUI Mode (Recommended)
```bash
# Navigate to project directory
cd /home/loke/Downloads/cusor/flight-risk-1

# Run with JavaFX interface
./mvnw spring-boot:run -Dspring-boot.run.arguments="--javafx"

# Or use the provided script
./run-javafx.sh
```

#### Web Mode (REST API)
```bash
# Run as web service
./mvnw spring-boot:run

# Access at http://localhost:8080
```

### Configuration

#### Weather API Setup
1. Get a free API key from [OpenWeatherMap](https://openweathermap.org/api)
2. Set environment variable:
   ```bash
   export OPENWEATHER_API_KEY=your_api_key_here
   ```
3. Or update `application.properties`:
   ```properties
   weather.api.key=your_api_key_here
   ```

#### UI Customization
```properties
# Enable/disable animations
ui.animations.enabled=true

# Enable/disable splash screen
ui.splash.enabled=true

# Theme selection
ui.theme=modern
```

## 🎯 Features Overview

### 🛫 Airport Risk Assessment
- **Multi-Factor Analysis**: Comprehensive risk evaluation using:
  - Real-time birdstrike data from CSV datasets
  - Live weather conditions from OpenWeatherMap
  - Airport traffic patterns
  - Historical incident data
- **Interactive Selection**: Easy airport selection with IATA code support
- **Instant Results**: Fast risk calculation with detailed breakdown

### 📈 Data Visualization
- **Interactive Map**: Clickable airport markers with color-coded risk levels
- **Statistics Dashboard**: Real-time statistics with automatic updates
- **Prediction History**: Comprehensive table with sortable columns and actions
- **Risk Trends**: Visual indicators for risk level changes

### 🔧 Management Tools
- **Data Refresh**: Manual and automatic data synchronization
- **History Management**: Clear prediction history with confirmation
- **Export Capabilities**: Data export for further analysis
- **System Status**: Real-time system health monitoring

## 🏗️ Architecture

### Technology Stack
- **Frontend**: JavaFX 11 with FXML and CSS
- **Backend**: Spring Boot 2.7.18
- **Database**: H2 (in-memory) with JPA/Hibernate
- **External APIs**: OpenWeatherMap REST API
- **Build Tool**: Maven 3.x

### Key Components

#### Services
- `WeatherService`: Real-time weather data with caching
- `BirdstrikeRiskService`: Birdstrike risk assessment
- `RiskCalculatorService`: Comprehensive risk calculation engine

#### Controllers
- `MainController`: JavaFX UI controller with async operations
- `FlightRiskController`: REST API endpoints

#### Data Layer
- `FlightPrediction`: Risk prediction entity
- `Airport`: Airport information entity
- H2 database with automatic schema generation

## 🎨 UI Components

### Modern Design Elements
- **Gradient Headers**: Professional blue-purple gradients
- **Card-Based Layout**: Clean, organized information display
- **Interactive Buttons**: Hover effects and state management
- **Color-Coded Risk Levels**:
  - 🔴 High Risk: Red indicators
  - 🟡 Medium Risk: Orange indicators  
  - 🟢 Low Risk: Green indicators

### Responsive Features
- **Adaptive Layout**: Scales from tablet to desktop resolutions
- **Flexible Tables**: Auto-resizing columns with proper content display
- **Mobile-Friendly**: Touch-friendly button sizes and spacing

## 📊 Risk Calculation Details

### Scoring Algorithm
```
Total Risk Score = (Birdstrike × 0.4) + (Weather × 0.35) + (Traffic × 0.15) + (Historical × 0.1)

Risk Levels:
- High Risk: Score ≥ 0.7
- Medium Risk: 0.4 ≤ Score < 0.7  
- Low Risk: Score < 0.4
```

### Factor Weights
- **Birdstrike Risk (40%)**: Primary safety concern
- **Weather Conditions (35%)**: Critical operational factor
- **Airport Traffic (15%)**: Congestion and complexity
- **Historical Data (10%)**: Past incident patterns

## 🔧 Development

### Building from Source
```bash
# Clone repository
git clone <repository-url>
cd flight-risk-1

# Clean build
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw package
```

### Adding New Airports
1. Update `data/airports.csv` with new airport data
2. Add city mapping in `WeatherService.AIRPORT_TO_CITY`
3. Update traffic data in `RiskCalculatorService.AIRPORT_TRAFFIC`

### Customizing Risk Factors
Modify weights in `RiskCalculatorService`:
```java
private static final double BIRDSTRIKE_WEIGHT = 0.4;
private static final double WEATHER_WEIGHT = 0.35;
private static final double TRAFFIC_WEIGHT = 0.15;
private static final double HISTORICAL_WEIGHT = 0.1;
```

## 🐛 Troubleshooting

### Common Issues

#### JavaFX Launch Errors
- Ensure JavaFX runtime is available
- Check FXML file paths and CSS resources
- Verify Spring context initialization

#### Weather API Issues
- Validate API key configuration
- Check network connectivity
- Review rate limiting (60 calls/minute for free tier)

#### Performance Issues
- Monitor memory usage with large datasets
- Check database query performance
- Verify caching is working properly

### Debug Mode
```bash
# Enable debug logging
./mvnw spring-boot:run -Dlogging.level.com.example.flightrisk=DEBUG
```

## 📝 API Documentation

### REST Endpoints
- `GET /api/predict/{airport}` - Get risk prediction
- `GET /api/weather/{airport}` - Get weather data
- `GET /api/predictions` - List all predictions
- `DELETE /api/predictions` - Clear prediction history

### Response Format
```json
{
  "airport": "DEL",
  "riskLevel": "Medium Risk",
  "riskScore": 0.52,
  "weather": "Temp: 25.0°C, Wind: 8.0 m/s, Condition: Clear",
  "timestamp": "2025-10-08T15:30:00",
  "confidence": 0.85
}
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- OpenWeatherMap for weather data API
- Spring Boot team for the excellent framework
- JavaFX community for UI components
- Aviation safety organizations for risk assessment guidelines

---

**Flight Risk Assessment System v2.0** - Making aviation safer through intelligent risk analysis.
