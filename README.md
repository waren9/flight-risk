# Flight Risk Assessment System

A Spring Boot application with JDBC connectivity and JavaFX frontend for assessing flight risks based on birdstrike data and weather conditions.

## Features

- **JDBC Database Integration**: Uses H2 in-memory database with MySQL support
- **JavaFX Frontend**: Modern desktop GUI for risk assessment
- **REST API**: Web endpoints for programmatic access
- **Risk Assessment**: Birdstrike and weather-based risk evaluation
- **Prediction History**: Store and view historical predictions
- **Airport Management**: Manage airport data

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 17 (included in dependencies)

## Database Schema

The application uses two main tables:

### Airports Table
- `id`: Primary key
- `code`: Airport code (e.g., JFK, LAX)
- `name`: Full airport name
- `city`: City name
- `country`: Country name
- `latitude`: Latitude coordinate
- `longitude`: Longitude coordinate
- `timezone`: Timezone information

### Flight Predictions Table
- `id`: Primary key
- `airport`: Airport code
- `risk_level`: Risk assessment (Low, Medium, High, Critical)
- `weather`: Weather information
- `route`: Flight route (currently N/A)
- `risk_score`: Numerical risk score (0.0-1.0)
- `prediction_time`: Timestamp of prediction

## Running the Application

### Web Application Mode
```bash
mvn spring-boot:run
```
Access the application at: http://localhost:8080

### JavaFX Desktop Application
```bash
./run-javafx.sh
```
Or manually:
```bash
mvn clean compile exec:java -Dexec.mainClass="com.example.flightrisk.fx.FlightRiskFXApplication"
```

## API Endpoints

### Risk Prediction
- `GET /predict?airport={code}` - Get risk prediction for airport
- `GET /predict/history` - Get all prediction history
- `GET /predict/history/{airport}` - Get prediction history for specific airport
- `DELETE /predict/history/{id}` - Delete specific prediction

### Airport Management
- `GET /airports` - Get all airports
- `GET /airports/{code}` - Get airport by code
- `POST /airports` - Create new airport
- `DELETE /airports/{code}` - Delete airport

## Database Console

When running in web mode, you can access the H2 database console at:
http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:flightrisk`
- Username: `sa`
- Password: (leave empty)

## Sample Data

The application automatically initializes with sample airport data including:
- JFK (New York)
- LAX (Los Angeles)
- LHR (London)
- CDG (Paris)
- NRT (Tokyo)
- SYD (Sydney)
- DXB (Dubai)
- SIN (Singapore)

## JavaFX Features

The desktop application provides:
- Airport selection dropdown
- One-click risk prediction
- Real-time risk level and weather display
- Historical prediction table
- Clear history functionality
- Refresh data capability

## Configuration

Database settings can be modified in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:flightrisk
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

## Building

```bash
mvn clean package
```

## Dependencies

- Spring Boot 3.5.5
- Spring JDBC
- H2 Database
- MySQL Connector
- JavaFX 17
- Maven

## Architecture

The application follows a layered architecture:
- **Controllers**: REST endpoints and JavaFX controllers
- **Services**: Business logic for risk assessment
- **Repositories**: Data access layer with JDBC
- **Entities**: Data models
- **DTOs**: Data transfer objects

## Troubleshooting

1. **JavaFX not starting**: Ensure JavaFX is properly installed and configured
2. **Database connection issues**: Check application.properties configuration
3. **Port conflicts**: Change server port in application.properties if needed

## License

This project is for educational purposes.


