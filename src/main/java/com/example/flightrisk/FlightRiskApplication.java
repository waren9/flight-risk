package com.example.flightrisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlightRiskApplication {

	public static void main(String[] args) {
		System.out.println("🚀 Starting Flight Risk Assessment System v2.0");
		System.out.println("📱 Modern React + Spring Boot Architecture");
		System.out.println("🌐 Access the application at: http://localhost:8080");
		System.out.println("🔧 API endpoints available at: http://localhost:8080/api");
		System.out.println("================================================");
		
		SpringApplication.run(FlightRiskApplication.class, args);
	}

}
