package application.ui.client;

import application.service.TimeAPIService;
import application.service.TimeAPIService.TimeData;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeScreenBuilder {
    
    /**
     * Create the main time screen content - matches other builders' pattern
     */
    public static VBox createTimeScreenContent() {
        VBox container = new VBox(15);
        container.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");
        
        // Header section (same style as other builders)
        VBox headerSection = createHeaderSection();
        
        // Time cards container
        VBox timeCardsSection = createTimeCardsSection();
        
        // Footer section
        VBox footerSection = createFooterSection();
        
        container.getChildren().addAll(headerSection, timeCardsSection, footerSection);
        return container;
    }
    
    /**
     * Create header section - matches PaymentScreenBuilder header style
     */
    private static VBox createHeaderSection() {
        VBox headerCard = new VBox(12);
        headerCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        // Title section
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleIcon = new Label("🌍");
        titleIcon.setStyle("-fx-font-size: 20px;");
        
        Label titleLabel = new Label("World Clock");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        titleBox.getChildren().addAll(titleIcon, titleLabel);
        
        // Subtitle
        Label subtitleLabel = new Label("Current time in major destinations worldwide");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        headerCard.getChildren().addAll(titleBox, subtitleLabel);
        return headerCard;
    }
    
    /**
     * Create time cards section with all destinations
     */
    private static VBox createTimeCardsSection() {
        VBox timeCardsContainer = new VBox(12);
        
        // Popular destinations (same as your flight data)
        timeCardsContainer.getChildren().addAll(
            createTimeCard("🇵🇭", "Philippines", "Asia/Manila", "Manila"),
            createTimeCard("🇺🇸", "United States", "America/New_York", "New York"),
            createTimeCard("🇬🇧", "United Kingdom", "Europe/London", "London"),
            createTimeCard("🇯🇵", "Japan", "Asia/Tokyo", "Tokyo"),
            createTimeCard("🇸🇬", "Singapore", "Asia/Singapore", "Singapore"),
            createTimeCard("🇦🇺", "Australia", "Australia/Sydney", "Sydney")
        );
        
        return timeCardsContainer;
    }
    
    /**
     * Create individual time card - matches BookingsScreenBuilder card style
     */
    private static VBox createTimeCard(String flag, String country, String timeZone, String city) {
        VBox card = new VBox(12);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        // Header with flag and country (same pattern as flight route)
        HBox headerBox = createCountryHeader(flag, country, city);
        
        // Loading state initially
        VBox contentBox = createLoadingContent();
        
        card.getChildren().addAll(headerBox, contentBox);
        
        // Fetch time asynchronously
        TimeAPIService.fetchTimeAsync(timeZone)
            .thenAccept(timeData -> {
                Platform.runLater(() -> {
                    // Replace loading with actual time
                    card.getChildren().remove(contentBox);
                    
                    if (timeData != null) {
                        VBox timeContent = createTimeContent(timeData);
                        card.getChildren().add(timeContent);
                    } else {
                        VBox errorContent = createErrorContent();
                        card.getChildren().add(errorContent);
                    }
                });
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    card.getChildren().remove(contentBox);
                    VBox errorContent = createErrorContent();
                    card.getChildren().add(errorContent);
                });
                return null;
            });
        
        return card;
    }
    
    /**
     * Create country header - matches airport code display pattern
     */
    private static HBox createCountryHeader(String flag, String country, String city) {
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label flagLabel = new Label(flag);
        flagLabel.setStyle("-fx-font-size: 24px;");
        
        VBox countryInfo = new VBox(2);
        
        Label countryLabel = new Label(country);
        countryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label cityLabel = new Label(city);
        cityLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        
        countryInfo.getChildren().addAll(countryLabel, cityLabel);
        headerBox.getChildren().addAll(flagLabel, countryInfo);
        
        return headerBox;
    }
    
    /**
     * Create loading content - matches your loading patterns
     */
    private static VBox createLoadingContent() {
        VBox loadingBox = new VBox(8);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setStyle(
            "-fx-background-color: #f8f9fa; -fx-padding: 12; -fx-background-radius: 6;"
        );
        
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(20, 20);
        
        Label loadingLabel = new Label("Loading time...");
        loadingLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        loadingBox.getChildren().addAll(progressIndicator, loadingLabel);
        return loadingBox;
    }
    
    /**
     * Create time content - matches detail row style from other builders
     */
    private static VBox createTimeContent(TimeData timeData) {
        VBox timeBox = new VBox(8);
        
        // Main time display
        HBox timeRow = createDetailRow("🕐 Time", timeData.getTime());
        timeRow.setStyle(
            "-fx-background-color: #E8F5E8; -fx-padding: 8; -fx-background-radius: 6;"
        );
        
        // Update the time label to be more prominent
        ((Label) timeRow.getChildren().get(2)).setStyle(
            "-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;"
        );
        
        // Date info
        String displayDate = timeData.getDayOfWeek().isEmpty() ? 
            timeData.getDate() : 
            timeData.getDayOfWeek() + ", " + timeData.getDate();
        HBox dateRow = createDetailRow("📅 Date", displayDate);
        
        // Timezone info
        HBox timezoneRow = createDetailRow("🌐 Zone", timeData.getTimeZone());
        
        // Source indicator
        String sourceText = timeData.isFromAPI() ? "Live" : "System";
        String sourceIcon = timeData.isFromAPI() ? "⚡" : "🔄";
        HBox sourceRow = createDetailRow(sourceIcon + " Source", sourceText);
        
        timeBox.getChildren().addAll(timeRow, dateRow, timezoneRow, sourceRow);
        return timeBox;
    }
    
    /**
     * Create error content
     */
    private static VBox createErrorContent() {
        VBox errorBox = new VBox(8);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setStyle(
            "-fx-background-color: #ffebee; -fx-padding: 12; -fx-background-radius: 6;"
        );
        
        Label errorIcon = new Label("⚠️");
        errorIcon.setStyle("-fx-font-size: 16px;");
        
        Label errorLabel = new Label("Unable to load time");
        errorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #c62828; -fx-font-weight: bold;");
        
        Label detailLabel = new Label("Check connection");
        detailLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        errorBox.getChildren().addAll(errorIcon, errorLabel, detailLabel);
        return errorBox;
    }
    
    /**
     * Create detail row - exactly matches other builders' pattern
     */
    private static HBox createDetailRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 6;");
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        labelText.setPrefWidth(80);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c2c2c; -fx-font-weight: bold;");
        
        row.getChildren().addAll(labelText, spacer, valueText);
        return row;
    }
    
    /**
     * Create footer section
     */
    private static VBox createFooterSection() {
        VBox footerCard = new VBox(8);
        footerCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        Label updateInfo = new Label("🔄 Auto-updates every minute");
        updateInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-style: italic;");
        
        Label lastUpdate = new Label("Last updated: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lastUpdate.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");
        
        Label apiInfo = new Label("⚡ Powered by TimeAPI.io");
        apiInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: #2196F3;");
        
        footerCard.getChildren().addAll(updateInfo, lastUpdate, apiInfo);
        return footerCard;
    }
}