package application.ui;

import application.service.TimeAPIService;
import application.service.TimeAPIService.TimeData;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

public class TimeScreenBuilder {
    
    /**
     * Create the header card for the time screen
     */
    public static VBox createHeaderCard() {
        VBox headerCard = new VBox(15);
        headerCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                           "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleIcon = new Label("🌍");
        titleIcon.setStyle("-fx-font-size: 24px;");
        
        Label titleLabel = new Label("World Times");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        titleBox.getChildren().addAll(titleIcon, titleLabel);
        
        Label subtitleLabel = new Label("Current time in major destinations ");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        headerCard.getChildren().addAll(titleBox, subtitleLabel);
        return headerCard;
    }
    
    /**
     * Create the footer with update information
     */
    public static VBox createFooterInfo() {
        VBox updateInfo = new VBox(5);
        updateInfo.setAlignment(Pos.CENTER);
        updateInfo.setStyle("-fx-padding: 15;");
        
        Label updateLabel = new Label("🔄 Auto-updates every minute • Powered by TimeAPI.io");
        updateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #999; -fx-font-style: italic;");
        
        Label lastUpdateLabel = new Label("Last updated: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lastUpdateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");
        
        updateInfo.getChildren().addAll(updateLabel, lastUpdateLabel);
        return updateInfo;
    }
    
    /**
     * Create a time card that loads data asynchronously
     */
    public static VBox createTimeCardWithAPI(String flag, String country, String timeZone, String city) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                     "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");
        
        // Header with flag and country (immediate display)
        HBox headerBox = createCountryHeader(flag, country, city);
        
        // Loading state
        VBox loadingBox = createLoadingBox();
        
        card.getChildren().addAll(headerBox, loadingBox);
        
        // Fetch time from API asynchronously
        TimeAPIService.fetchTimeAsync(timeZone)
            .thenAccept(timeData -> {
                Platform.runLater(() -> {
                    // Remove loading box
                    card.getChildren().remove(loadingBox);
                    
                    if (timeData != null) {
                        // Add time display
                        VBox timeBox = createTimeDisplay(timeData);
                        card.getChildren().add(timeBox);
                    } else {
                        // Add error display
                        VBox errorBox = createErrorDisplay(country, timeZone);
                        card.getChildren().add(errorBox);
                    }
                });
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    card.getChildren().remove(loadingBox);
                    VBox errorBox = createErrorDisplay(country, timeZone);
                    card.getChildren().add(errorBox);
                });
                System.err.println("Error fetching time for " + country + ": " + ex.getMessage());
                return null;
            });
        
        return card;
    }
    
    /**
     * Create the country header section
     */
    private static HBox createCountryHeader(String flag, String country, String city) {
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label flagLabel = new Label(flag);
        flagLabel.setStyle("-fx-font-size: 28px;");
        
        VBox countryBox = new VBox(2);
        Label countryLabel = new Label(country);
        countryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label cityLabel = new Label(city);
        cityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        countryBox.getChildren().addAll(countryLabel, cityLabel);
        headerBox.getChildren().addAll(flagLabel, countryBox);
        
        return headerBox;
    }
    
    /**
     * Create loading indicator
     */
    private static VBox createLoadingBox() {
        VBox loadingBox = new VBox(8);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");
        
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(30, 30);
        
        Label loadingLabel = new Label("Loading time...");
        loadingLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        loadingBox.getChildren().addAll(progressIndicator, loadingLabel);
        return loadingBox;
    }
    
    /**
     * Create time display from API data
     */
    private static VBox createTimeDisplay(TimeData timeData) {
        VBox timeBox = new VBox(8);
        timeBox.setAlignment(Pos.CENTER);
        timeBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");
        
        // Current time (from API)
        Label timeLabel = new Label(timeData.getTime());
        timeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        // Date with day of week (from API)
        String displayDate = timeData.getDayOfWeek().isEmpty() ? 
            timeData.getDate() : 
            timeData.getDayOfWeek() + ", " + timeData.getDate();
            
        Label dateLabel = new Label(displayDate);
        dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        // Timezone info
        Label timezoneLabel = new Label("📡 " + timeData.getTimeZone());
        timezoneLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #999; " +
                              "-fx-background-color: #e3f2fd; -fx-padding: 4 8; -fx-background-radius: 12;");
        
        // API source indicator
        String sourceText = timeData.isFromAPI() ? "⚡ Live from TimeAPI.io" : "🔄 System Time (Fallback)";
        String sourceColor = timeData.isFromAPI() ? "#4CAF50" : "#FF9800";
        
        Label sourceLabel = new Label(sourceText);
        sourceLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + sourceColor + "; -fx-font-style: italic;");
        
        timeBox.getChildren().addAll(timeLabel, dateLabel, timezoneLabel, sourceLabel);
        return timeBox;
    }
    
    /**
     * Create error display with fallback
     */
    private static VBox createErrorDisplay(String country, String timeZone) {
        VBox errorBox = new VBox(8);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setStyle("-fx-background-color: #ffebee; -fx-padding: 15; -fx-background-radius: 10;");
        
        Label errorIcon = new Label("⚠️");
        errorIcon.setStyle("-fx-font-size: 20px;");
        
        Label errorLabel = new Label("Unable to load live time");
        errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #c62828; -fx-font-weight: bold;");
        
        Label fallbackLabel = new Label("Please check your internet connection");
        fallbackLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        errorBox.getChildren().addAll(errorIcon, errorLabel, fallbackLabel);
        return errorBox;
    }
}