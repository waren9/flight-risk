package com.example.flightrisk.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class FlightRiskFXApplication extends Application {

    private static ConfigurableApplicationContext springContext;
    private FXMLLoader fxmlLoader;

    @Override
    public void init() throws Exception {
        // Set system property to disable web server for JavaFX
        System.setProperty("spring.main.web-application-type", "none");
        springContext = SpringApplication.run(com.example.flightrisk.FlightRiskApplication.class);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(springContext::getBean);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        
        primaryStage.setTitle("✈️ Flight Risk Assessment System ✈️");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


