#!/bin/bash

# Flight Risk Assessment System - JavaFX Launcher
echo "Starting Flight Risk Assessment System (JavaFX Mode)..."

# Compile and run the JavaFX application
mvn clean compile exec:java -Dexec.mainClass="com.example.flightrisk.fx.FlightRiskFXApplication"


