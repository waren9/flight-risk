package com.example.flightrisk.repository;

import com.example.flightrisk.entity.Airport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AirportRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Airport> rowMapper = new RowMapper<Airport>() {
        @Override
        public Airport mapRow(ResultSet rs, int rowNum) throws SQLException {
            Airport airport = new Airport();
            airport.setId(rs.getLong("id"));
            airport.setCode(rs.getString("code"));
            airport.setName(rs.getString("name"));
            airport.setCity(rs.getString("city"));
            airport.setCountry(rs.getString("country"));
            airport.setLatitude(rs.getDouble("latitude"));
            airport.setLongitude(rs.getDouble("longitude"));
            airport.setTimezone(rs.getString("timezone"));
            return airport;
        }
    };

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS airports (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "code VARCHAR(10) NOT NULL UNIQUE," +
                "name VARCHAR(255) NOT NULL," +
                "city VARCHAR(100)," +
                "country VARCHAR(100)," +
                "latitude DOUBLE," +
                "longitude DOUBLE," +
                "timezone VARCHAR(50)" +
                ")";
        jdbcTemplate.execute(sql);
    }

    public void save(Airport airport) {
        String sql = "INSERT INTO airports (code, name, city, country, latitude, longitude, timezone) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
            airport.getCode(), 
            airport.getName(), 
            airport.getCity(),
            airport.getCountry(),
            airport.getLatitude(),
            airport.getLongitude(),
            airport.getTimezone()
        );
    }

    public List<Airport> findAll() {
        String sql = "SELECT * FROM airports ORDER BY code";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Airport findByCode(String code) {
        String sql = "SELECT * FROM airports WHERE code = ?";
        List<Airport> results = jdbcTemplate.query(sql, rowMapper, code);
        return results.isEmpty() ? null : results.get(0);
    }

    public Airport findById(Long id) {
        String sql = "SELECT * FROM airports WHERE id = ?";
        List<Airport> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void deleteByCode(String code) {
        String sql = "DELETE FROM airports WHERE code = ?";
        jdbcTemplate.update(sql, code);
    }

    public void deleteAll() {
        String sql = "DELETE FROM airports";
        jdbcTemplate.update(sql);
    }
}
