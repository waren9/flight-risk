package com.example.flightrisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlightRiskApplication {

	public static void main(String[] args) {
		// Check if JavaFX is requested
		if (args.length > 0 && args[0].equals("--javafx")) {
			// Launch JavaFX application
			com.example.flightrisk.fx.FlightRiskFXApplication.main(args);
		} else {
			// Launch web application
			SpringApplication.run(FlightRiskApplication.class, args);
		}
	}

}
