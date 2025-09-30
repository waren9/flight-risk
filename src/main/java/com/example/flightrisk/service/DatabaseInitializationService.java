package com.example.flightrisk.service;

import com.example.flightrisk.entity.Airport;
import com.example.flightrisk.repository.AirportRepository;
import com.example.flightrisk.repository.FlightPredictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInitializationService implements CommandLineRunner {

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private FlightPredictionRepository flightPredictionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create tables
        airportRepository.createTable();
        flightPredictionRepository.createTable();

        // Insert sample airport data
        insertSampleAirports();
    }

    private void insertSampleAirports() {
        // Check if airports already exist
        if (airportRepository.findAll().isEmpty()) {
            // Indian airports from birdstrikes.csv
            airportRepository.save(new Airport("DEL", "Indira Gandhi International Airport", "New Delhi", "India", 28.5562, 77.1000, "Asia/Kolkata"));
            airportRepository.save(new Airport("BOM", "Chhatrapati Shivaji Maharaj International Airport", "Mumbai", "India", 19.0896, 72.8656, "Asia/Kolkata"));
            airportRepository.save(new Airport("BLR", "Kempegowda International Airport", "Bangalore", "India", 13.1986, 77.7066, "Asia/Kolkata"));
            airportRepository.save(new Airport("HYD", "Rajiv Gandhi International Airport", "Hyderabad", "India", 17.2403, 78.4294, "Asia/Kolkata"));
            airportRepository.save(new Airport("CCU", "Netaji Subhas Chandra Bose International Airport", "Kolkata", "India", 22.6547, 88.4467, "Asia/Kolkata"));
            
            // International airports
            airportRepository.save(new Airport("JFK", "John F. Kennedy International Airport", "New York", "USA", 40.6413, -73.7781, "America/New_York"));
            airportRepository.save(new Airport("LAX", "Los Angeles International Airport", "Los Angeles", "USA", 33.9416, -118.4085, "America/Los_Angeles"));
            airportRepository.save(new Airport("LHR", "London Heathrow Airport", "London", "UK", 51.4700, -0.4543, "Europe/London"));
            airportRepository.save(new Airport("CDG", "Charles de Gaulle Airport", "Paris", "France", 49.0097, 2.5479, "Europe/Paris"));
            airportRepository.save(new Airport("NRT", "Narita International Airport", "Tokyo", "Japan", 35.7720, 140.3928, "Asia/Tokyo"));
            airportRepository.save(new Airport("SYD", "Sydney Kingsford Smith Airport", "Sydney", "Australia", -33.9399, 151.1753, "Australia/Sydney"));
            airportRepository.save(new Airport("DXB", "Dubai International Airport", "Dubai", "UAE", 25.2532, 55.3657, "Asia/Dubai"));
            airportRepository.save(new Airport("SIN", "Singapore Changi Airport", "Singapore", "Singapore", 1.3644, 103.9915, "Asia/Singapore"));
        }
    }
}


