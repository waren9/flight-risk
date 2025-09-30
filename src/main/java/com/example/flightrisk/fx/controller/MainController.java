package com.example.flightrisk.fx.controller;

import com.example.flightrisk.entity.Airport;
import com.example.flightrisk.entity.FlightPrediction;
import com.example.flightrisk.repository.AirportRepository;
import com.example.flightrisk.repository.FlightPredictionRepository;
import com.example.flightrisk.service.BirdstrikeRiskService;
import com.example.flightrisk.service.WeatherService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class MainController implements Initializable {

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private FlightPredictionRepository flightPredictionRepository;

    @Autowired
    private BirdstrikeRiskService birdstrikeRiskService;

    @Autowired
    private WeatherService weatherService;

    @FXML
    private ComboBox<String> airportComboBox;

    @FXML
    private Button predictButton;

    @FXML
    private Label riskLevelLabel;

    @FXML
    private Label weatherLabel;

    @FXML
    private TableView<FlightPrediction> predictionTable;

    @FXML
    private TableColumn<FlightPrediction, String> airportColumn;

    @FXML
    private TableColumn<FlightPrediction, String> riskLevelColumn;

    @FXML
    private TableColumn<FlightPrediction, String> weatherColumn;

    @FXML
    private TableColumn<FlightPrediction, Double> riskScoreColumn;

    @FXML
    private TableColumn<FlightPrediction, String> predictionTimeColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button clearHistoryButton;

    @FXML
    private Label riskScoreLabel;

    @FXML
    private Label totalPredictionsLabel;

    @FXML
    private Label highRiskLabel;

    @FXML
    private Label mediumRiskLabel;

    @FXML
    private Label lowRiskLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private TableColumn<FlightPrediction, String> actionColumn;

    @FXML
    private VBox mapContainer;

    @FXML
    private Button delMarker;

    @FXML
    private Button bomMarker;

    @FXML
    private Button blrMarker;

    @FXML
    private Button hydMarker;

    @FXML
    private Button ccuMarker;

    @FXML
    private Button jfkMarker;

    private ObservableList<FlightPrediction> predictionData = FXCollections.observableArrayList();
    private Timeline clockTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupAirportComboBox();
        setupPredictionTable();
        loadPredictionHistory();
        updateStatistics();
        startClock();
        setupActionColumn();
        setupMapMarkers();
    }

    private void setupAirportComboBox() {
        List<Airport> airports = airportRepository.findAll();
        ObservableList<String> airportCodes = FXCollections.observableArrayList();
        for (Airport airport : airports) {
            airportCodes.add(airport.getCode());
        }
        airportComboBox.setItems(airportCodes);
        if (!airportCodes.isEmpty()) {
            airportComboBox.setValue(airportCodes.get(0));
        }
    }

    private void setupPredictionTable() {
        airportColumn.setCellValueFactory(new PropertyValueFactory<>("airport"));
        riskLevelColumn.setCellValueFactory(new PropertyValueFactory<>("riskLevel"));
        weatherColumn.setCellValueFactory(new PropertyValueFactory<>("weather"));
        riskScoreColumn.setCellValueFactory(new PropertyValueFactory<>("riskScore"));
        predictionTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getPredictionTime();
            String formattedDate = dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
            return new javafx.beans.property.SimpleStringProperty(formattedDate);
        });

        // Format the prediction time column
        predictionTimeColumn.setCellFactory(column -> new TableCell<FlightPrediction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });

        // Color-code risk level column
        riskLevelColumn.setCellFactory(column -> new TableCell<FlightPrediction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item.toLowerCase()) {
                        case "high risk":
                            setStyle("-fx-background-color: #ffebee; -fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                            break;
                        case "medium risk":
                            setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #f57c00; -fx-font-weight: bold;");
                            break;
                        case "low risk":
                            setStyle("-fx-background-color: #e8f5e8; -fx-text-fill: #388e3c; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #757575;");
                    }
                }
            }
        });

        predictionTable.setItems(predictionData);
    }

    @FXML
    private void handlePredict() {
        String selectedAirport = airportComboBox.getValue();
        if (selectedAirport == null || selectedAirport.isEmpty()) {
            showAlert("Please select an airport", Alert.AlertType.WARNING);
            return;
        }

        try {
            statusLabel.setText("🔄 Processing...");
            predictButton.setDisable(true);
            
            String riskLevel = birdstrikeRiskService.assessRisk(selectedAirport);
            String weatherInfo = weatherService.getWeather(selectedAirport);

            // Update risk level display with color coding
            riskLevelLabel.setText(riskLevel);
            switch (riskLevel.toLowerCase()) {
                case "high risk":
                    riskLevelLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; -fx-font-size: 18px;");
                    break;
                case "medium risk":
                    riskLevelLabel.setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold; -fx-font-size: 18px;");
                    break;
                case "low risk":
                    riskLevelLabel.setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold; -fx-font-size: 18px;");
                    break;
                default:
                    riskLevelLabel.setStyle("-fx-text-fill: #757575; -fx-font-weight: bold; -fx-font-size: 18px;");
            }
            
            weatherLabel.setText(weatherInfo);
            weatherLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 16px;");

            // Calculate risk score
            double riskScore = calculateRiskScore(riskLevel);
            riskScoreLabel.setText(String.format("%.2f", riskScore));
            riskScoreLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 16px;");

            // Save prediction to database
            FlightPrediction prediction = new FlightPrediction(selectedAirport, riskLevel, weatherInfo, "N/A", riskScore);
            flightPredictionRepository.save(prediction);

            // Refresh the table and statistics
            loadPredictionHistory();
            updateStatistics();

            statusLabel.setText("✅ Prediction Complete");
            showAlert("🎯 Prediction completed successfully!\n\n" +
                     "Airport: " + selectedAirport + "\n" +
                     "Risk Level: " + riskLevel + "\n" +
                     "Risk Score: " + String.format("%.2f", riskScore), Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            statusLabel.setText("❌ Error");
            showAlert("Error making prediction: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            predictButton.setDisable(false);
            // Reset status after 3 seconds
            Timeline resetStatus = new Timeline(new KeyFrame(Duration.seconds(3), e -> statusLabel.setText("🟢 System Ready")));
            resetStatus.play();
        }
    }

    @FXML
    private void handleRefresh() {
        loadPredictionHistory();
        updateStatistics();
        updateMarkerColors();
        statusLabel.setText("🔄 Data Refreshed");
        Timeline resetStatus = new Timeline(new KeyFrame(Duration.seconds(2), e -> statusLabel.setText("🟢 System Ready")));
        resetStatus.play();
    }

    @FXML
    private void handleClearHistory() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("🗑️ Clear History");
        alert.setHeaderText("Are you sure you want to clear all prediction history?");
        alert.setContentText("This action cannot be undone and will remove all stored predictions.");

        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            flightPredictionRepository.deleteAll();
            loadPredictionHistory();
            updateStatistics();
            statusLabel.setText("🗑️ History Cleared");
            showAlert("✅ History cleared successfully!", Alert.AlertType.INFORMATION);
            Timeline resetStatus = new Timeline(new KeyFrame(Duration.seconds(2), e -> statusLabel.setText("🟢 System Ready")));
            resetStatus.play();
        }
    }

    private void loadPredictionHistory() {
        List<FlightPrediction> predictions = flightPredictionRepository.findAll();
        predictionData.clear();
        predictionData.addAll(predictions);
    }

    private double calculateRiskScore(String riskLevel) {
        switch (riskLevel.toLowerCase()) {
            case "low":
                return 0.2;
            case "medium":
                return 0.5;
            case "high":
                return 0.8;
            case "critical":
                return 1.0;
            default:
                return 0.5;
        }
    }

    private void updateStatistics() {
        List<FlightPrediction> predictions = flightPredictionRepository.findAll();
        
        long total = predictions.size();
        long highRisk = predictions.stream()
            .filter(p -> p.getRiskLevel().toLowerCase().contains("high"))
            .count();
        long mediumRisk = predictions.stream()
            .filter(p -> p.getRiskLevel().toLowerCase().contains("medium"))
            .count();
        long lowRisk = predictions.stream()
            .filter(p -> p.getRiskLevel().toLowerCase().contains("low"))
            .count();
        
        totalPredictionsLabel.setText(String.valueOf(total));
        highRiskLabel.setText(String.valueOf(highRisk));
        mediumRiskLabel.setText(String.valueOf(mediumRisk));
        lowRiskLabel.setText(String.valueOf(lowRisk));
    }

    private void startClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            timeLabel.setText(now.format(formatter));
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<FlightPrediction, String>() {
            private final Button deleteButton = new Button("🗑️");
            
            {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 3;");
                deleteButton.setOnAction(event -> {
                    FlightPrediction prediction = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Prediction");
                    alert.setHeaderText("Are you sure you want to delete this prediction?");
                    alert.setContentText("Airport: " + prediction.getAirport() + "\nRisk Level: " + prediction.getRiskLevel());
                    
                    if (alert.showAndWait().orElse(null) == ButtonType.OK) {
                        flightPredictionRepository.deleteById(prediction.getId());
                        loadPredictionHistory();
                        updateStatistics();
                    }
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void setupMapMarkers() {
        // Update marker colors based on risk levels
        updateMarkerColors();
    }

    @FXML
    private void onAirportMarkerClick(javafx.event.ActionEvent event) {
        Button clickedMarker = (Button) event.getSource();
        String airportCode = clickedMarker.getText().replace("🛫 ", "");
        
        // Set the airport in the combo box
        airportComboBox.setValue(airportCode);
        
        // Automatically predict risk for the clicked airport
        handlePredict();
        
        // Show info
        statusLabel.setText("📍 Map Selection: " + airportCode);
        Timeline resetStatus = new Timeline(new KeyFrame(Duration.seconds(3), e -> statusLabel.setText("🟢 System Ready")));
        resetStatus.play();
    }

    private void updateMarkerColors() {
        // Update marker colors based on current risk data
        updateMarkerColor(delMarker, "DEL");
        updateMarkerColor(bomMarker, "BOM");
        updateMarkerColor(blrMarker, "BLR");
        updateMarkerColor(hydMarker, "HYD");
        updateMarkerColor(ccuMarker, "CCU");
        updateMarkerColor(jfkMarker, "JFK");
    }

    private void updateMarkerColor(Button marker, String airportCode) {
        String riskLevel = birdstrikeRiskService.assessRisk(airportCode);
        
        String color;
        switch (riskLevel.toLowerCase()) {
            case "high risk":
                color = "#e74c3c"; // Red
                break;
            case "medium risk":
                color = "#f39c12"; // Orange
                break;
            case "low risk":
                color = "#27ae60"; // Green
                break;
            default:
                color = "#3498db"; // Blue (no data)
        }
        
        marker.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-padding: 8 12;");
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
