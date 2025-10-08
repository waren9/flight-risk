# 🛠️ Development Setup & Implementation Guide

## 📋 Table of Contents
1. [Environment Setup](#environment-setup)
2. [Backend Development](#backend-development)
3. [Frontend Development](#frontend-development)
4. [Database Management](#database-management)
5. [API Development](#api-development)
6. [Testing Strategy](#testing-strategy)
7. [Troubleshooting](#troubleshooting)

## 🚀 Environment Setup

### Prerequisites Checklist
- [ ] **Java 11+** (OpenJDK recommended)
- [ ] **Maven 3.6+** (or use included wrapper)
- [ ] **Node.js 18-22** and npm 8+
- [ ] **Git** for version control
- [ ] **IDE**: IntelliJ IDEA, VS Code, or Eclipse
- [ ] **Browser**: Chrome, Firefox, Safari, or Edge

### Quick Environment Verification
```bash
# Check Java version
java -version
# Should show: openjdk version "11.0.x" or higher

# Check Maven
mvn -version
# Should show: Apache Maven 3.6.x or higher

# Check Node.js
node --version
# Should show: v18.x.x or higher

# Check npm
npm --version
# Should show: 8.x.x or higher
```

### Project Structure Overview
```
flight-risk/
├── 🏗️ Backend (Spring Boot)
│   ├── src/main/java/com/example/flightrisk/
│   │   ├── controller/     # REST API endpoints
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access
│   │   ├── entity/         # Data models
│   │   └── config/         # Configuration
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── data/           # CSV data files
│   └── pom.xml            # Maven dependencies
├── 🎨 Frontend (React)
│   ├── src/
│   │   ├── App.js         # Main component
│   │   ├── index.js       # Entry point
│   │   └── index.css      # Styles
│   ├── package.json       # npm dependencies
│   └── public/            # Static assets
└── 📜 Scripts
    ├── run-react-app.sh   # Full stack launcher
    └── run-javafx.sh      # JavaFX launcher
```

## 🏗️ Backend Development

### Spring Boot Application Structure

#### 1. Main Application Class
```java
// src/main/java/com/example/flightrisk/FlightRiskApplication.java
@SpringBootApplication
public class FlightRiskApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlightRiskApplication.class, args);
    }
}
```

#### 2. Controller Layer Implementation
```java
// Example: PredictionController.java
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PredictionController {
    
    @Autowired
    private RiskCalculatorService riskCalculatorService;
    
    @PostMapping("/predict/{airport}")
    public ResponseEntity<Map<String, Object>> predictRisk(
            @PathVariable String airport,
            @RequestParam(required = false) String targetTime) {
        
        try {
            // Implementation logic here
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Prediction failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
```

#### 3. Service Layer Best Practices
```java
// Example: RiskCalculatorService.java
@Service
public class RiskCalculatorService {
    
    private static final Logger logger = LoggerFactory.getLogger(RiskCalculatorService.class);
    
    // Constants for configuration
    private static final double BIRDSTRIKE_WEIGHT = 0.4;
    private static final double WEATHER_WEIGHT = 0.35;
    
    public double calculateRiskScore(String airport, String birdstrike, String weather) {
        // Implement weighted calculation
        // Include error handling
        // Add logging for debugging
    }
}
```

#### 4. Repository Pattern Implementation
```java
// Example: AirportRepository.java
@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    
    Optional<Airport> findByCode(String code);
    
    List<Airport> findByCountry(String country);
    
    @Query("SELECT a FROM Airport a WHERE a.city = :city")
    List<Airport> findByCity(@Param("city") String city);
}
```

### Configuration Management

#### application.properties
```properties
# Server Configuration
server.port=8080
spring.application.name=flight-risk

# Database Configuration
spring.datasource.url=jdbc:h2:mem:flightrisk
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# External API Configuration
weather.api.key=${OPENWEATHER_API_KEY:your_api_key_here}
weather.api.url=https://api.openweathermap.org/data/2.5/weather
weather.fallback.enabled=true

# Birdstrike Configuration
birdstrike.realtime.enabled=false
birdstrike.realtime.api.url=https://api.example.com/birdstrikes
birdstrike.fallback.csv=true

# Logging Configuration
logging.level.com.example.flightrisk=DEBUG
logging.level.org.springframework.web=INFO
```

### Maven Dependencies (pom.xml)
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 🎨 Frontend Development

### React Application Structure

#### 1. Main App Component
```javascript
// src/App.js
import React, { useState, useEffect } from 'react';
import './index.css';

function App() {
    const [airports, setAirports] = useState([]);
    const [selectedAirport, setSelectedAirport] = useState('');
    const [prediction, setPrediction] = useState(null);
    const [loading, setLoading] = useState(false);
    
    // Component implementation
    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-900 via-purple-900 to-indigo-900">
            {/* UI Components */}
        </div>
    );
}

export default App;
```

#### 2. API Integration
```javascript
// API service functions
const API_BASE = 'http://localhost:8080/api';

const apiService = {
    // Fetch all airports
    getAirports: async () => {
        const response = await fetch(`${API_BASE}/airports`);
        return response.json();
    },
    
    // Predict risk for airport
    predictRisk: async (airport, targetTime = null) => {
        const url = targetTime 
            ? `${API_BASE}/predict/${airport}?targetTime=${targetTime}`
            : `${API_BASE}/predict/${airport}`;
        
        const response = await fetch(url, { method: 'POST' });
        return response.json();
    },
    
    // Get prediction history
    getHistory: async () => {
        const response = await fetch(`${API_BASE}/predictions`);
        return response.json();
    }
};
```

#### 3. Tailwind CSS Configuration
```css
/* src/index.css */
@tailwind base;
@tailwind components;
@tailwind utilities;

/* Custom glassmorphism effects */
.glass {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.risk-high {
    @apply bg-red-500 text-white;
}

.risk-medium {
    @apply bg-yellow-500 text-black;
}

.risk-low {
    @apply bg-green-500 text-white;
}
```

### Package.json Configuration
```json
{
  "name": "flight-risk-frontend",
  "version": "1.0.0",
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-scripts": "5.0.1"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "devDependencies": {
    "tailwindcss": "^3.3.0",
    "autoprefixer": "^10.4.14",
    "postcss": "^8.4.24"
  }
}
```

## 🗄️ Database Management

### H2 Database Console Access
```bash
# Start the application
./mvnw spring-boot:run

# Access H2 Console
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:flightrisk
# Username: sa
# Password: (empty)
```

### Data Loading Implementation
```java
// DatabaseInitializationService.java
@Service
public class DatabaseInitializationService {
    
    @PostConstruct
    public void initializeData() {
        loadAirportsFromCsv();
        loadBirdstrikeData();
    }
    
    private void loadAirportsFromCsv() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    new ClassPathResource("data/airports.csv").getInputStream()))) {
            
            String line;
            br.readLine(); // Skip header
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    Airport airport = new Airport(
                        parts[0].trim(), // code
                        parts[1].trim(), // name
                        parts[2].trim(), // city
                        parts[3].trim(), // country
                        Double.parseDouble(parts[4].trim()), // latitude
                        Double.parseDouble(parts[5].trim()), // longitude
                        parts[6].trim()  // timezone
                    );
                    airportRepository.save(airport);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load airports: {}", e.getMessage());
        }
    }
}
```

### Sample CSV Data Format
```csv
# src/main/resources/data/airports.csv
code,name,city,country,latitude,longitude,timezone
DEL,Indira Gandhi International Airport,Delhi,India,28.5562,77.1000,Asia/Kolkata
BOM,Chhatrapati Shivaji Maharaj International Airport,Mumbai,India,19.0896,72.8656,Asia/Kolkata
BLR,Kempegowda International Airport,Bangalore,India,13.1986,77.7066,Asia/Kolkata
```

## 🌐 API Development

### RESTful API Design Principles

#### 1. Endpoint Naming Conventions
```
GET    /api/airports           # List all airports
GET    /api/airports/{code}    # Get specific airport
POST   /api/airports           # Create new airport
PUT    /api/airports/{code}    # Update airport
DELETE /api/airports/{code}    # Delete airport

POST   /api/predict/{airport}  # Generate prediction
GET    /api/predictions        # Get all predictions
GET    /api/history/{airport}  # Get airport history
DELETE /api/predictions        # Clear all predictions

GET    /api/statistics         # Get system statistics
GET    /api/health            # Health check
```

#### 2. Response Format Standards
```json
// Success Response
{
  "airport": "DEL",
  "riskLevel": "Medium Risk",
  "riskScore": 0.65,
  "weather": "Temp: 25.0°C, Wind: 8.0 m/s, Condition: Clear",
  "breakdown": {
    "birdstrikeScore": 0.4,
    "weatherScore": 0.2,
    "trafficScore": 0.8,
    "historicalScore": 0.6,
    "confidence": 0.85
  },
  "timestamp": "2024-10-08T19:30:00",
  "isTimeTravel": false
}

// Error Response
{
  "error": "Failed to assess risk: Invalid airport code",
  "airport": "INVALID",
  "timestamp": "2024-10-08T19:30:00",
  "code": "INVALID_AIRPORT"
}
```

#### 3. Input Validation
```java
@PostMapping("/predict/{airport}")
public ResponseEntity<Map<String, Object>> predictRisk(
        @PathVariable @Pattern(regexp = "[A-Z]{3}", message = "Airport code must be 3 uppercase letters") String airport,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime targetTime) {
    
    // Validation logic
    if (airport == null || airport.trim().isEmpty()) {
        return ResponseEntity.badRequest().body(
            Map.of("error", "Airport code is required")
        );
    }
    
    // Implementation
}
```

## 🧪 Testing Strategy

### Unit Testing
```java
// Test example: RiskCalculatorServiceTest.java
@ExtendWith(MockitoExtension.class)
class RiskCalculatorServiceTest {
    
    @InjectMocks
    private RiskCalculatorService riskCalculatorService;
    
    @Test
    void testCalculateRiskScore_HighRisk() {
        // Given
        String airport = "DEL";
        String birdstrike = "High Risk";
        String weather = "High Risk";
        
        // When
        double score = riskCalculatorService.calculateRiskScore(airport, birdstrike, weather);
        
        // Then
        assertThat(score).isGreaterThan(0.7);
    }
    
    @Test
    void testCalculateRiskScore_LowRisk() {
        // Given
        String airport = "DEL";
        String birdstrike = "Low Risk";
        String weather = "Low Risk";
        
        // When
        double score = riskCalculatorService.calculateRiskScore(airport, birdstrike, weather);
        
        // Then
        assertThat(score).isLessThan(0.4);
    }
}
```

### Integration Testing
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class PredictionControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testPredictRisk_Success() {
        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/api/predict/DEL", null, Map.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("riskLevel");
        assertThat(response.getBody()).containsKey("riskScore");
    }
}
```

### Frontend Testing
```javascript
// App.test.js
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import App from './App';

test('renders flight risk assessment title', () => {
  render(<App />);
  const titleElement = screen.getByText(/Flight Risk Assessment/i);
  expect(titleElement).toBeInTheDocument();
});

test('predicts risk for selected airport', async () => {
  render(<App />);
  
  // Select airport
  const airportSelect = screen.getByRole('combobox');
  fireEvent.change(airportSelect, { target: { value: 'DEL' } });
  
  // Click predict button
  const predictButton = screen.getByText(/Predict Risk/i);
  fireEvent.click(predictButton);
  
  // Wait for result
  await waitFor(() => {
    expect(screen.getByText(/Risk Level/i)).toBeInTheDocument();
  });
});
```

## 🔧 Development Commands

### Backend Development
```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Start development server
./mvnw spring-boot:run

# Build JAR
./mvnw clean package

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend Development
```bash
# Install dependencies
cd frontend && npm install

# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Install new package
npm install package-name
```

### Full Stack Development
```bash
# Start both backend and frontend
./run-react-app.sh

# Or manually in separate terminals
# Terminal 1: Backend
./mvnw spring-boot:run

# Terminal 2: Frontend
cd frontend && npm start
```

## 🐛 Troubleshooting

### Common Issues & Solutions

#### Port Conflicts
```bash
# Check what's using ports
lsof -i :8080  # Backend port
lsof -i :3000  # Frontend port

# Kill processes if needed
kill -9 <PID>

# Or use different ports
./mvnw spring-boot:run -Dserver.port=8081
cd frontend && PORT=3001 npm start
```

#### Node.js Compatibility Issues
```bash
# Check Node version
node --version

# If using Node 18+, you might need:
export NODE_OPTIONS="--openssl-legacy-provider"

# Or downgrade to Node 16
nvm install 16
nvm use 16
```

#### Database Connection Issues
```bash
# Check H2 console access
curl http://localhost:8080/h2-console

# Verify database URL in logs
grep "H2 console available" logs/application.log

# Reset database (restart application)
./mvnw spring-boot:run
```

#### API Integration Issues
```bash
# Test weather API directly
curl "https://api.openweathermap.org/data/2.5/weather?q=Delhi&appid=YOUR_API_KEY"

# Check environment variables
echo $OPENWEATHER_API_KEY

# Verify API endpoints
curl http://localhost:8080/api/health
curl http://localhost:8080/api/airports
```

### Debugging Tips

#### Backend Debugging
```properties
# Enable debug logging
logging.level.com.example.flightrisk=DEBUG
logging.level.org.springframework.web=DEBUG

# SQL logging
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

#### Frontend Debugging
```javascript
// Add console logging
console.log('API Response:', response);

// Use React Developer Tools
// Install browser extension for component inspection

// Network tab debugging
// Check browser developer tools for API calls
```

### Performance Monitoring
```bash
# Check application metrics
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics

# Monitor memory usage
jps -l  # Find Java process ID
jstat -gc <PID>  # Garbage collection stats
```

This development guide provides comprehensive instructions for setting up, developing, and troubleshooting the Flight Risk Assessment System.
