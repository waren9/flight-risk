#!/bin/bash

# Flight Risk Assessment System - JavaFX Launcher
echo "🚀 Starting Flight Risk Assessment System - JavaFX Edition"
echo "================================================"

# Check if backend is running
echo "🔍 Checking if Spring Boot backend is running..."
if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "✅ Backend is running at http://localhost:8080"
else
    echo "❌ Backend not detected. Starting Spring Boot application first..."
    echo "💡 Run: ./mvnw spring-boot:run"
    echo "   or: java -jar target/flightrisk-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "🔄 Attempting to start backend automatically..."
    ./mvnw spring-boot:run &
    BACKEND_PID=$!
    echo "⏳ Waiting for backend to start..."
    sleep 10
fi

echo ""
echo "🎨 Launching JavaFX Application..."
echo "📱 Modern desktop interface with all React features"
echo "⚡ Features: Risk Prediction, Time Travel, Statistics, History"
echo ""

# Run JavaFX application
./mvnw javafx:run

echo ""
echo "👋 JavaFX Application closed"
echo "================================================"
