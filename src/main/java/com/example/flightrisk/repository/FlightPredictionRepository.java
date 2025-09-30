package com.example.flightrisk.repository;

import com.example.flightrisk.entity.FlightPrediction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FlightPredictionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<FlightPrediction> rowMapper = new RowMapper<FlightPrediction>() {
        @Override
        public FlightPrediction mapRow(ResultSet rs, int rowNum) throws SQLException {
            FlightPrediction prediction = new FlightPrediction();
            prediction.setId(rs.getLong("id"));
            prediction.setAirport(rs.getString("airport"));
            prediction.setRiskLevel(rs.getString("risk_level"));
            prediction.setWeather(rs.getString("weather"));
            prediction.setRoute(rs.getString("route"));
            prediction.setRiskScore(rs.getDouble("risk_score"));
            prediction.setPredictionTime(rs.getTimestamp("prediction_time").toLocalDateTime());
            return prediction;
        }
    };

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS flight_predictions (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "airport VARCHAR(10) NOT NULL," +
                "risk_level VARCHAR(20) NOT NULL," +
                "weather VARCHAR(255)," +
                "route VARCHAR(255)," +
                "risk_score DOUBLE," +
                "prediction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        jdbcTemplate.execute(sql);
    }

    public void save(FlightPrediction prediction) {
        String sql = "INSERT INTO flight_predictions (airport, risk_level, weather, route, risk_score, prediction_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
            prediction.getAirport(), 
            prediction.getRiskLevel(), 
            prediction.getWeather(),
            prediction.getRoute(),
            prediction.getRiskScore(),
            prediction.getPredictionTime()
        );
    }

    public List<FlightPrediction> findAll() {
        String sql = "SELECT * FROM flight_predictions ORDER BY prediction_time DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<FlightPrediction> findByAirport(String airport) {
        String sql = "SELECT * FROM flight_predictions WHERE airport = ? ORDER BY prediction_time DESC";
        return jdbcTemplate.query(sql, rowMapper, airport);
    }

    public FlightPrediction findById(Long id) {
        String sql = "SELECT * FROM flight_predictions WHERE id = ?";
        List<FlightPrediction> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM flight_predictions WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteAll() {
        String sql = "DELETE FROM flight_predictions";
        jdbcTemplate.update(sql);
    }
}
