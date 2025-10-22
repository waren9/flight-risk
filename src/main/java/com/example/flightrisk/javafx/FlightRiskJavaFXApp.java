package com.example.flightrisk.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FlightRiskJavaFXApp extends Application {
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // UI Components
    private TabPane tabPane;
    private ComboBox<Airport> airportComboBox;
    private TextField searchField;
    private CheckBox timeTravelCheckBox;
    private DatePicker datePicker;
    private TextField timePicker;
    private Button predictButton;
    private Label statusLabel;
    private TextArea resultArea;
    private TableView<PredictionRecord> historyTable;
    private VBox statisticsPane;
    
    // Data
    private ObservableList<Airport> airports = FXCollections.observableArrayList();
    private ObservableList<Airport> filteredAirports = FXCollections.observableArrayList();
    private ObservableList<PredictionRecord> predictions = FXCollections.observableArrayList();
    private Statistics statistics;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("✈️ Flight Risk Assessment System v3.0 - JavaFX");
        
        // Create main layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background: linear-gradient(to bottom right, #1e3c72, #2a5298, #667eea);");
        
        // Header
        VBox header = createHeader();
        
        // Tab pane
        tabPane = createTabPane();
        
        root.getChildren().addAll(header, tabPane);
        
        Scene scene = new Scene(new ScrollPane(root), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Load initial data
        loadInitialData();
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; -fx-padding: 20;");
        
        Label title = new Label("✈️ Flight Risk Assessment System v3.0");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label subtitle = new Label("Advanced Analytics • Time Travel • Real-time Predictions");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: rgba(255,255,255,0.9);");
        
        statusLabel = new Label("🔄 Connecting...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        
        header.getChildren().addAll(title, subtitle, statusLabel);
        return header;
    }
    
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15;");
        
        // Predict Tab
        Tab predictTab = new Tab("🎯 Predict");
        predictTab.setContent(createPredictPane());
        predictTab.setClosable(false);
        
        // History Tab
        Tab historyTab = new Tab("📊 History");
        historyTab.setContent(createHistoryPane());
        historyTab.setClosable(false);
        
        // Statistics Tab
        Tab statisticsTab = new Tab("📈 Statistics");
        statisticsTab.setContent(createStatisticsPane());
        statisticsTab.setClosable(false);
        
        // Time Travel Tab
        Tab timeTravelTab = new Tab("⏰ Time Travel");
        timeTravelTab.setContent(createTimeTravelPane());
        timeTravelTab.setClosable(false);
        
        tabPane.getTabs().addAll(predictTab, historyTab, statisticsTab, timeTravelTab);
        return tabPane;
    }
    
    private VBox createPredictPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));
        pane.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10;");
        
        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search airports by code, city, or name...");
        searchField.setStyle("-fx-font-size: 14px; -fx-pref-width: 400;");
        searchField.textProperty().addListener((obs, oldText, newText) -> filterAirports(newText));
        
        // Airport selection
        airportComboBox = new ComboBox<>(filteredAirports);
        airportComboBox.setPromptText("Choose Airport...");
        airportComboBox.setStyle("-fx-font-size: 14px; -fx-pref-width: 400;");
        
        // Time travel controls
        VBox timeTravelBox = createTimeTravelControls();
        
        // Predict button
        predictButton = new Button("🔮 Predict Risk");
        predictButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 15 30;");
        predictButton.setOnAction(e -> handlePredict());
        
        // Results area
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(10);
        resultArea.setStyle("-fx-font-size: 12px; -fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white;");
        
        pane.getChildren().addAll(
            new Label("Search Airports:") {{ setStyle("-fx-text-fill: white; -fx-font-weight: bold;"); }},
            searchField,
            new Label("Select Airport:") {{ setStyle("-fx-text-fill: white; -fx-font-weight: bold;"); }},
            airportComboBox,
            timeTravelBox,
            predictButton,
            new Label("Prediction Results:") {{ setStyle("-fx-text-fill: white; -fx-font-weight: bold;"); }},
            resultArea
        );
        
        return pane;
    }
    
    private VBox createTimeTravelControls() {
        VBox timeTravelBox = new VBox(10);
        timeTravelBox.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10; -fx-padding: 15;");
        
        timeTravelCheckBox = new CheckBox("⏰ Enable Time Travel Prediction");
        timeTravelCheckBox.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        HBox dateTimeBox = new HBox(10);
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle("-fx-font-size: 12px;");
        datePicker.setDisable(true);
        
        timePicker = new TextField(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timePicker.setPromptText("HH:mm");
        timePicker.setStyle("-fx-font-size: 12px; -fx-pref-width: 80;");
        timePicker.setDisable(true);
        
        timeTravelCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            datePicker.setDisable(!newVal);
            timePicker.setDisable(!newVal);
        });
        
        dateTimeBox.getChildren().addAll(
            new Label("Date:") {{ setStyle("-fx-text-fill: white;"); }}, datePicker,
            new Label("Time:") {{ setStyle("-fx-text-fill: white;"); }}, timePicker
        );
        
        timeTravelBox.getChildren().addAll(timeTravelCheckBox, dateTimeBox);
        return timeTravelBox;
    }
    
    private VBox createHistoryPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));
        
        Button clearButton = new Button("🗑️ Clear All");
        clearButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
        clearButton.setOnAction(e -> clearPredictions());
        
        historyTable = new TableView<>(predictions);
        setupHistoryTable();
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label historyLabel = new Label("📊 Prediction History");
        historyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerBox.getChildren().addAll(historyLabel, spacer, clearButton);
        
        pane.getChildren().addAll(headerBox, historyTable);
        return pane;
    }
    
    private void setupHistoryTable() {
        TableColumn<PredictionRecord, String> airportCol = new TableColumn<>("Airport");
        airportCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().airport));
        
        TableColumn<PredictionRecord, String> riskCol = new TableColumn<>("Risk Level");
        riskCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().riskLevel));
        
        TableColumn<PredictionRecord, String> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.format("%.1f%%", data.getValue().riskScore * 100)));
        
        TableColumn<PredictionRecord, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().timestamp));
        
        historyTable.getColumns().add(airportCol);
        historyTable.getColumns().add(riskCol);
        historyTable.getColumns().add(scoreCol);
        historyTable.getColumns().add(timeCol);
        historyTable.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white;");
    }
    
    private VBox createStatisticsPane() {
        statisticsPane = new VBox(20);
        statisticsPane.setPadding(new Insets(20));
        
        Label statsLabel = new Label("📈 Risk Statistics");
        statsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        statisticsPane.getChildren().add(statsLabel);
        return statisticsPane;
    }
    
    private VBox createTimeTravelPane() {
        VBox pane = new VBox(20);
        pane.setPadding(new Insets(20));
        
        Label title = new Label("⏰ Time Travel Feature");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10; -fx-padding: 20;");
        
        Label howItWorks = new Label("🔮 How Time Travel Works");
        howItWorks.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        String[] features = {
            "• Select any future or past date and time",
            "• System simulates weather conditions for that specific time",
            "• Historical bird migration patterns are analyzed",
            "• Traffic predictions are adjusted based on target time",
            "• Risk assessment calculated as if predicting at that exact moment"
        };
        
        VBox featuresList = new VBox(5);
        for (String feature : features) {
            Label featureLabel = new Label(feature);
            featureLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 14px;");
            featuresList.getChildren().add(featureLabel);
        }
        
        infoBox.getChildren().addAll(howItWorks, featuresList);
        pane.getChildren().addAll(title, infoBox);
        
        return pane;
    }
    
    private void loadInitialData() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Test API connection
                    HttpRequest healthRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/health"))
                        .GET()
                        .build();
                    
                    httpClient.send(healthRequest, HttpResponse.BodyHandlers.ofString());
                    
                    Platform.runLater(() -> statusLabel.setText("🟢 Connected"));
                    
                    // Load airports
                    HttpRequest airportsRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/airports"))
                        .GET()
                        .build();
                    
                    HttpResponse<String> airportsResponse = httpClient.send(airportsRequest, HttpResponse.BodyHandlers.ofString());
                    List<Airport> airportList = objectMapper.readValue(airportsResponse.body(), new TypeReference<List<Airport>>() {});
                    
                    Platform.runLater(() -> {
                        airports.setAll(airportList);
                        filteredAirports.setAll(airportList);
                    });
                    
                    // Load predictions and statistics
                    loadPredictions();
                    loadStatistics();
                    
                } catch (Exception e) {
                    Platform.runLater(() -> statusLabel.setText("🔴 Disconnected"));
                    e.printStackTrace();
                }
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    private void filterAirports(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredAirports.setAll(airports);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            filteredAirports.setAll(airports.stream()
                .filter(airport -> 
                    airport.code.toLowerCase().contains(lowerCaseFilter) ||
                    airport.city.toLowerCase().contains(lowerCaseFilter) ||
                    airport.name.toLowerCase().contains(lowerCaseFilter))
                .collect(java.util.stream.Collectors.toList()));
        }
    }
    
    private void handlePredict() {
        Airport selectedAirport = airportComboBox.getValue();
        if (selectedAirport == null) return;
        
        predictButton.setDisable(true);
        predictButton.setText("🔄 Analyzing...");
        
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                String url = "http://localhost:8080/api/predict/" + selectedAirport.code;
                
                if (timeTravelCheckBox.isSelected()) {
                    String targetDateTime = datePicker.getValue() + "T" + timePicker.getText() + ":00";
                    url += "?targetTime=" + java.net.URLEncoder.encode(targetDateTime, "UTF-8");
                }
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            }
        };
        
        task.setOnSucceeded(e -> {
            try {
                String responseBody = task.getValue();
                @SuppressWarnings("unchecked")
                Map<String, Object> prediction = objectMapper.readValue(responseBody, Map.class);
                displayPredictionResult(prediction);
                loadPredictions();
                loadStatistics();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                predictButton.setDisable(false);
                predictButton.setText("🔮 Predict Risk");
            }
        });
        
        task.setOnFailed(e -> {
            predictButton.setDisable(false);
            predictButton.setText("🔮 Predict Risk");
            resultArea.setText("Error: " + task.getException().getMessage());
        });
        
        new Thread(task).start();
    }
    
    private void displayPredictionResult(Map<String, Object> prediction) {
        StringBuilder result = new StringBuilder();
        result.append("📊 PREDICTION RESULTS\n");
        result.append("==========================================\n\n");
        result.append("🛫 Airport: ").append(prediction.get("airport")).append("\n");
        result.append("⚠️ Risk Level: ").append(prediction.get("riskLevel")).append("\n");
        result.append("📈 Risk Score: ").append(String.format("%.1f%%", ((Number) prediction.get("riskScore")).doubleValue() * 100)).append("\n");
        result.append("🌤️ Weather: ").append(prediction.get("weather")).append("\n\n");
        
        if (prediction.containsKey("breakdown")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> breakdown = (Map<String, Object>) prediction.get("breakdown");
            result.append("🔍 RISK BREAKDOWN\n");
            result.append("------------------------------------------\n");
            result.append("🦅 Birdstrike: ").append(String.format("%.0f%%", ((Number) breakdown.get("birdstrikeScore")).doubleValue() * 100)).append("\n");
            result.append("🌤️ Weather: ").append(String.format("%.0f%%", ((Number) breakdown.get("weatherScore")).doubleValue() * 100)).append("\n");
            result.append("✈️ Traffic: ").append(String.format("%.0f%%", ((Number) breakdown.get("trafficScore")).doubleValue() * 100)).append("\n");
            result.append("📊 Historical: ").append(String.format("%.0f%%", ((Number) breakdown.get("historicalScore")).doubleValue() * 100)).append("\n");
            result.append("🎯 Confidence: ").append(String.format("%.0f%%", ((Number) breakdown.get("confidence")).doubleValue() * 100)).append("\n");
        }
        
        if (timeTravelCheckBox.isSelected()) {
            result.append("\n⏰ TIME TRAVEL PREDICTION\n");
            result.append("Target: ").append(datePicker.getValue()).append(" ").append(timePicker.getText()).append("\n");
        }
        
        resultArea.setText(result.toString());
    }
    
    private void loadPredictions() {
        CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/predictions"))
                    .GET()
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return objectMapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<Map<String, Object>>();
            }
        }).thenAccept(predictionList -> {
            Platform.runLater(() -> {
                predictions.clear();
                for (Map<String, Object> pred : predictionList) {
                    predictions.add(new PredictionRecord(
                        (String) pred.get("airport"),
                        (String) pred.get("riskLevel"),
                        ((Number) pred.get("riskScore")).doubleValue(),
                        (String) pred.get("timestamp")
                    ));
                }
            });
        });
    }
    
    private void loadStatistics() {
        CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/statistics"))
                    .GET()
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return objectMapper.readValue(response.body(), Statistics.class);
            } catch (Exception e) {
                e.printStackTrace();
                return new Statistics();
            }
        }).thenAccept(stats -> {
            Platform.runLater(() -> {
                this.statistics = stats;
                updateStatisticsDisplay();
            });
        });
    }
    
    private void updateStatisticsDisplay() {
        statisticsPane.getChildren().clear();
        
        Label title = new Label("📈 Risk Statistics");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));
        
        // Total predictions
        VBox totalBox = createStatBox("📊", String.valueOf(statistics.total), "Total Predictions", "rgba(255,255,255,0.1)");
        grid.add(totalBox, 0, 0);
        
        // High risk
        VBox highRiskBox = createStatBox("🔴", String.valueOf(statistics.highRisk), "High Risk", "rgba(255,107,107,0.3)");
        grid.add(highRiskBox, 1, 0);
        
        // Medium risk
        VBox mediumRiskBox = createStatBox("🟡", String.valueOf(statistics.mediumRisk), "Medium Risk", "rgba(255,167,38,0.3)");
        grid.add(mediumRiskBox, 0, 1);
        
        // Low risk
        VBox lowRiskBox = createStatBox("🟢", String.valueOf(statistics.lowRisk), "Low Risk", "rgba(76,175,80,0.3)");
        grid.add(lowRiskBox, 1, 1);
        
        statisticsPane.getChildren().addAll(title, grid);
    }
    
    private VBox createStatBox(String icon, String value, String label, String backgroundColor) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 10; -fx-padding: 20;");
        box.setPrefSize(200, 120);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label descLabel = new Label(label);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.8);");
        
        box.getChildren().addAll(iconLabel, valueLabel, descLabel);
        return box;
    }
    
    private void clearPredictions() {
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/predictions"))
                    .DELETE()
                    .build();
                
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                Platform.runLater(() -> {
                    predictions.clear();
                    resultArea.clear();
                    loadStatistics();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    // Data classes
    public static class Airport {
        public String code;
        public String name;
        public String city;
        public String country;
        
        @Override
        public String toString() {
            return code + " - " + city + ", " + country;
        }
    }
    
    public static class PredictionRecord {
        public final String airport;
        public final String riskLevel;
        public final double riskScore;
        public final String timestamp;
        
        public PredictionRecord(String airport, String riskLevel, double riskScore, String timestamp) {
            this.airport = airport;
            this.riskLevel = riskLevel;
            this.riskScore = riskScore;
            this.timestamp = timestamp;
        }
    }
    
    public static class Statistics {
        public int total = 0;
        public int highRisk = 0;
        public int mediumRisk = 0;
        public int lowRisk = 0;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
