# Flight Risk Assessment System v3.0

A comprehensive flight risk assessment platform featuring advanced analytics, time travel predictions, and modern UI. Built with React + Spring Boot + JDBC architecture.

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (OpenJDK recommended)
- **Node.js 18+** and npm
- **Git** (for cloning)

### Single Command Launch
```bash
./run-react-app.sh
```

This unified script will:
1. ✅ Verify Node.js and npm installation
2. 📦 Install frontend dependencies if needed
3. 🔧 Apply compatibility fixes for newer Node versions
4. 🚀 Start Spring Boot backend on port 8080
5. ⏳ Wait for backend health check
6. 🎨 Launch React frontend on port 3000
7. 🌐 Open application in your browser

## 🏗️ Architecture Overview

### Backend (Spring Boot + JDBC)
```
src/main/java/com/example/flightrisk/
├── controller/          # REST API endpoints
│   ├── AirportController.java      # Airport CRUD operations
│   ├── PredictionController.java   # Risk prediction & time travel
│   └── HelloController.java        # Health checks
├── entity/              # JPA entities
│   ├── Airport.java               # Airport data model
│   └── FlightPrediction.java      # Prediction results
├── repository/          # Data access layer
│   ├── AirportRepository.java     # Airport JDBC operations
│   └── FlightPredictionRepository.java
├── service/             # Business logic
│   ├── BirdstrikeRiskService.java # Bird collision risk
│   ├── WeatherService.java        # Weather analysis
│   └── RiskCalculatorService.java # Risk aggregation
└── config/              # Configuration
    └── DataLoader.java            # CSV data initialization
```

### Frontend (React)
```
frontend/src/
├── App.js              # Main application component
├── index.js            # React entry point
├── index.css           # Global styles + Tailwind
└── components/         # Reusable UI components (future)
```

## 🗄️ JDBC Implementation Details

### Database Configuration
The application uses **H2 in-memory database** with JDBC for development:

```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:flightrisk
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

### Data Access Pattern
1. **JPA Entities**: Define data models with annotations
2. **Repository Layer**: Extends JpaRepository for CRUD operations
3. **Service Layer**: Business logic and data transformation
4. **Controller Layer**: REST API endpoints

### Database Schema
```sql
-- Airports table
CREATE TABLE airports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    timezone VARCHAR(50) NOT NULL
);

-- Flight Predictions table
CREATE TABLE flight_predictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    airport VARCHAR(10) NOT NULL,
    risk_level VARCHAR(50) NOT NULL,
    weather_info TEXT,
    additional_info TEXT,
    risk_score DOUBLE NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Data Loading Process
1. **Startup**: `DataLoader.java` runs on application start
2. **CSV Import**: Reads `src/main/resources/data/airports.csv`
3. **Validation**: Checks for existing data to prevent duplicates
4. **Fallback**: Uses hardcoded defaults if CSV is missing

## 🌟 Key Features

### 1. Risk Assessment Engine
- **Multi-factor Analysis**: Weather, bird strikes, traffic, historical data
- **Real-time Calculations**: Dynamic risk scoring algorithms
- **Confidence Metrics**: Reliability indicators for predictions

### 2. Time Travel Predictions ⏰
- **Temporal Analysis**: Predict risks for any future/past date and time
- **Seasonal Adjustments**: Account for migration patterns and weather cycles
- **Time-based Risk Factors**:
  - Dawn/dusk = higher bird activity
  - Spring/fall = migration seasons
  - Winter = severe weather risk
  - Summer = thunderstorm potential

### 3. Interactive Dashboard
- **Multi-tab Interface**: Predict, History, Statistics, Time Travel
- **Airport Search**: Filter 94+ international airports
- **Visual Risk Indicators**: Color-coded risk levels
- **Historical Analysis**: Track prediction accuracy over time

### 4. Advanced UI/UX
- **Modern Design**: Glassmorphism effects with backdrop blur
- **Responsive Layout**: Works on desktop, tablet, and mobile
- **Real-time Updates**: Live status indicators and statistics
- **Accessibility**: Keyboard navigation and screen reader support

## 🛫 Airport Dataset

The system includes **94 major international airports** across:
- **Asia**: India, China, Japan, Korea, Southeast Asia, Middle East
- **Europe**: UK, Germany, France, Italy, Scandinavia, Eastern Europe
- **Americas**: USA, Canada, South America
- **Africa**: South Africa, Egypt
- **Oceania**: Australia

Each airport includes:
- IATA code, full name, city, country
- GPS coordinates (latitude/longitude)
- Timezone information for accurate time travel calculations

## 🔧 API Reference

### Core Endpoints

#### Airport Management
```http
GET /api/airports
# Returns: List of all airports with details

GET /api/airports/{code}
# Returns: Specific airport information

POST /api/airports
# Body: Airport JSON object
# Returns: Created airport
```

#### Risk Prediction
```http
POST /api/predict/{airport}?targetTime=2024-12-25T14:30:00
# Parameters:
#   - airport: IATA airport code (required)
#   - targetTime: ISO datetime for time travel (optional)
# Returns: Risk assessment with breakdown
```

#### Prediction History
```http
GET /api/predictions
# Returns: All prediction history

GET /api/history/{airport}
# Returns: Predictions for specific airport

DELETE /api/predictions
# Clears all prediction history
```

#### System Information
```http
GET /api/health
# Returns: System health and version info

GET /api/statistics
# Returns: Risk statistics and metrics
```

### Response Format
```json
{
  "airport": "JFK",
  "riskLevel": "Medium Risk",
  "riskScore": 0.65,
  "weather": "Clear skies, light winds",
  "breakdown": {
    "birdstrikeScore": 0.4,
    "weatherScore": 0.2,
    "trafficScore": 0.8,
    "historicalScore": 0.6,
    "confidence": 0.85
  },
  "timestamp": "2024-10-08T19:30:00",
  "isTimeTravel": true,
  "predictionTime": "2024-12-25T14:30:00"
}
```

## 🚀 Development Workflow

### Local Development
```bash
# Backend only (port 8080)
./mvnw spring-boot:run

# Frontend only (port 3000)
cd frontend && npm start

# Full stack (recommended)
./run-react-app.sh
```

### Database Console
Access H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:flightrisk`
- Username: `sa`
- Password: (empty)

### Adding New Airports
1. Edit `src/main/resources/data/airports.csv`
2. Add new row with: `code,name,city,country,latitude,longitude,timezone`
3. Restart application to reload data

### Customizing Risk Algorithms
1. Modify service classes in `src/main/java/.../service/`
2. Adjust risk factors in `RiskCalculatorService.java`
3. Update time travel logic in `PredictionController.java`

## 🔍 Troubleshooting

### Common Issues

**Port Conflicts**
```bash
# Check what's using ports
lsof -i :8080
lsof -i :3000

# Kill processes if needed
kill -9 <PID>
```

**Node.js Compatibility**
- Recommended: Node.js 18-22
- The script includes automatic compatibility fixes
- For persistent issues, try: `npm cache clean --force`

**Database Issues**
- H2 database resets on restart (by design)
- Check logs for CSV loading errors
- Verify airport data format in CSV

**Build Failures**
```bash
# Clean and rebuild
./mvnw clean install
cd frontend && rm -rf node_modules && npm install
```

### Health Checks
- Backend: `http://localhost:8080/actuator/health`
- Frontend: `http://localhost:3000`
- API Base: `http://localhost:8080/api`
- Database Console: `http://localhost:8080/h2-console`

## 📊 Performance & Scalability

### Current Capabilities
- **Airports**: 94 international airports loaded
- **Predictions**: Unlimited history storage (in-memory)
- **Concurrent Users**: Suitable for development/demo
- **Response Time**: < 100ms for predictions

### Production Considerations
- Replace H2 with PostgreSQL/MySQL for persistence
- Add Redis for caching frequently accessed data
- Implement rate limiting for API endpoints
- Add monitoring with Micrometer/Prometheus

## 🎯 Future Enhancements

### Planned Features
- **Real Weather Integration**: Live weather API integration
- **Machine Learning**: Historical pattern recognition
- **Notifications**: Email/SMS alerts for high-risk conditions
- **Mobile App**: React Native companion app
- **Multi-language**: Internationalization support

### Technical Improvements
- **TypeScript Migration**: Full type safety
- **Microservices**: Split into smaller services
- **Container Deployment**: Docker + Kubernetes
- **CI/CD Pipeline**: Automated testing and deployment

---

## 📝 License & Credits

**Flight Risk Assessment System v3.0**
- Built with ❤️ using Spring Boot + React
- Enhanced with modern UI/UX principles
- Time travel feature inspired by aviation safety research

**Technologies Used:**
- Backend: Spring Boot 2.7+, H2 Database, JPA/Hibernate
- Frontend: React 18, Tailwind CSS, Modern JavaScript
- Build Tools: Maven, npm, Node.js
- Development: Hot reload, auto-restart, live updates

For questions or contributions, please refer to the development team.


