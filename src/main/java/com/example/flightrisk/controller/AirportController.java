package com.example.flightrisk.controller;

import com.example.flightrisk.entity.Airport;
import com.example.flightrisk.repository.AirportRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AirportController {

    private final AirportRepository airportRepository;

    public AirportController(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @GetMapping
    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    @GetMapping("/{code}")
    public Airport getAirportByCode(@PathVariable String code) {
        return airportRepository.findByCode(code);
    }

    @PostMapping
    public Airport createAirport(@RequestBody Airport airport) {
        airportRepository.save(airport);
        return airport;
    }

    @DeleteMapping("/{code}")
    public void deleteAirport(@PathVariable String code) {
        airportRepository.deleteByCode(code);
    }
}


