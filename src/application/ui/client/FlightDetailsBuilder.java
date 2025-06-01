package application.ui.client;

import application.model.Flight;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FlightDetailsBuilder {
    
    private final Runnable onBookFlight;
    private final Runnable onGoBack;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
    
    public FlightDetailsBuilder(Runnable onBookFlight, Runnable onGoBack) {
        this.onBookFlight = onBookFlight;
        this.onGoBack = onGoBack;
    }
    
    public VBox createFlightDetailsView(Flight flight) {
        VBox container = new VBox(0);
        container.setStyle("-fx-background-color: #f5f5f5;");
        container.setFillWidth(true);
        container.setMaxWidth(500); // Mobile width constraint
        
        // Header section with back button
        VBox headerSection = createHeaderSection();
        
        // Main content section
        VBox contentSection = createContentSection(flight);
        
        // Bottom action section
        VBox actionSection = createActionSection();
        
        container.getChildren().addAll(headerSection, contentSection, actionSection);
        return container;
    }
    
    private VBox createHeaderSection() {
        VBox headerSection = new VBox();
        headerSection.setStyle("-fx-background-color: #d0d0d0; -fx-border-color: #808080; -fx-border-width: 0 0 1 0;");
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setStyle("-fx-padding: 10 15;");
        
        Button backButton = new Button("Go Back");
        backButton.setStyle(
            "-fx-background-color: linear-gradient(#f0f0f0, #d0d0d0); " +
            "-fx-text-fill: #2c2c2c; -fx-font-size: 12px; -fx-padding: 6 12; " +
            "-fx-border-color: #808080; -fx-border-width: 1; -fx-cursor: hand;"
        );
        backButton.setOnAction(e -> onGoBack.run());
        
        // Hover effects
        backButton.setOnMouseEntered(e -> 
            backButton.setStyle(
                "-fx-background-color: linear-gradient(#e0e0e0, #c0c0c0); " +
                "-fx-text-fill: #2c2c2c; -fx-font-size: 12px; -fx-padding: 6 12; " +
                "-fx-border-color: #606060; -fx-border-width: 1; -fx-cursor: hand;"
            )
        );
        
        backButton.setOnMouseExited(e -> 
            backButton.setStyle(
                "-fx-background-color: linear-gradient(#f0f0f0, #d0d0d0); " +
                "-fx-text-fill: #2c2c2c; -fx-font-size: 12px; -fx-padding: 6 12; " +
                "-fx-border-color: #808080; -fx-border-width: 1; -fx-cursor: hand;"
            )
        );
        
        headerBox.getChildren().add(backButton);
        headerSection.getChildren().add(headerBox);
        
        return headerSection;
    }
    
    private VBox createContentSection(Flight flight) {
        VBox contentSection = new VBox(15);
        contentSection.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");
        VBox.setVgrow(contentSection, Priority.ALWAYS);
        
        // Flight header card
        VBox headerCard = createFlightHeaderCard(flight);
        
        // Route card
        VBox routeCard = createRouteCard(flight);
        
        // Details card
        VBox detailsCard = createFlightDetailsCard(flight);
        
        // Price card
        VBox priceCard = createPriceCard(flight);
        
        contentSection.getChildren().addAll(headerCard, routeCard, detailsCard, priceCard);
        return contentSection;
    }
    
    private VBox createFlightHeaderCard(Flight flight) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        // Airline name
        Label airlineLabel = new Label(flight.getAirlineName());
        airlineLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        // Flight number and status
        HBox flightRow = new HBox();
        flightRow.setAlignment(Pos.CENTER_LEFT);
        
        Label flightNoLabel = new Label("Flight " + flight.getFlightNo());
        flightNoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(flight.getStatus().toUpperCase());
        statusLabel.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; " +
            "-fx-padding: 4 8; -fx-background-radius: 12; " +
            "-fx-font-size: 10px; -fx-font-weight: bold;"
        );
        
        flightRow.getChildren().addAll(flightNoLabel, spacer, statusLabel);
        card.getChildren().addAll(airlineLabel, flightRow);
        
        return card;
    }
    
    private VBox createRouteCard(Flight flight) {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        // Route header
        Label routeTitle = new Label("Flight Route");
        routeTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        // Airport codes row
        HBox airportsRow = new HBox();
        airportsRow.setAlignment(Pos.CENTER);
        airportsRow.setSpacing(15);
        
        VBox originBox = createCompactAirportBox(flight.getOrigin(), "From");
        Label arrowLabel = new Label("✈️");
        arrowLabel.setStyle("-fx-font-size: 20px;");
        VBox destBox = createCompactAirportBox(flight.getDestination(), "To");
        
        airportsRow.getChildren().addAll(originBox, arrowLabel, destBox);
        
        // Times section
        VBox timesSection = createCompactTimesSection(flight);
        
        card.getChildren().addAll(routeTitle, airportsRow, timesSection);
        return card;
    }
    
    private VBox createCompactAirportBox(String code, String label) {
        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);
        
        Label codeLabel = new Label(code);
        codeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        Label typeLabel = new Label(label);
        typeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666; -fx-font-weight: bold;");
        
        box.getChildren().addAll(codeLabel, typeLabel);
        return box;
    }
    
    private VBox createCompactTimesSection(Flight flight) {
        VBox timesSection = new VBox(8);
        
        HBox depRow = createTimeRow("Departure", flight.getDeparture());
        HBox arrRow = createTimeRow("Arrival", flight.getArrival());
        
        timesSection.getChildren().addAll(depRow, arrRow);
        return timesSection;
    }
    
    private HBox createTimeRow(String label, java.time.LocalDateTime dateTime) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 6;");
        
        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-weight: bold;");
        titleLabel.setPrefWidth(80);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        VBox timeBox = new VBox(2);
        timeBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label timeLabel = new Label(dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label dateLabel = new Label(dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        timeBox.getChildren().addAll(timeLabel, dateLabel);
        row.getChildren().addAll(titleLabel, spacer, timeBox);
        
        return row;
    }
    
    private VBox createFlightDetailsCard(Flight flight) {
        VBox card = new VBox(12);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        Label detailsTitle = new Label("Flight Information");
        detailsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        VBox detailsGrid = new VBox(8);
        detailsGrid.getChildren().addAll(
            createCompactDetailRow("✈️ Aircraft", flight.getAircraft()),
            createCompactDetailRow("💺 Seats", flight.getSeats() + " available"),
            createCompactDetailRow("⏱️ Duration", flight.getDuration()),
            createCompactDetailRow("📅 Date", 
                flight.getDeparture().format(DateTimeFormatter.ofPattern("EEEE, MMM dd")))
        );
        
        card.getChildren().addAll(detailsTitle, detailsGrid);
        return card;
    }
    
    private HBox createCompactDetailRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 6;");
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        labelText.setPrefWidth(100);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c2c2c; -fx-font-weight: bold;");
        
        row.getChildren().addAll(labelText, spacer, valueText);
        return row;
    }
    
    private VBox createPriceCard(Flight flight) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(76,175,80,0.2), 4, 0, 0, 2);"
        );
        
        Label priceTitle = new Label("Total Price");
        priceTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-weight: bold;");
        
        Label priceAmount = new Label(currencyFormat.format(flight.getPrice()));
        priceAmount.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        Label priceNote = new Label("per person • taxes included");
        priceNote.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        card.getChildren().addAll(priceTitle, priceAmount, priceNote);
        return card;
    }
    
    private VBox createActionSection() {
        VBox actionSection = new VBox();
        actionSection.setStyle(
            "-fx-background-color: #f0f0f0; -fx-border-color: #808080; " +
            "-fx-border-width: 1 0 0 0; -fx-padding: 15;"
        );
        
        Button bookButton = new Button("Book This Flight");
        bookButton.setStyle(
            "-fx-background-color: linear-gradient(#4CAF50, #45a049); " +
            "-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 25; " +
            "-fx-border-color: #388E3C; -fx-border-width: 1; -fx-background-radius: 6; " +
            "-fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"
        );
        bookButton.setMaxWidth(Double.MAX_VALUE);
        bookButton.setOnAction(e -> onBookFlight.run());
        
        // Hover effects
        bookButton.setOnMouseEntered(e ->
            bookButton.setStyle(
                "-fx-background-color: linear-gradient(#45a049, #3d8b40); " +
                "-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 25; " +
                "-fx-border-color: #2E7D32; -fx-border-width: 1; -fx-background-radius: 6; " +
                "-fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"
            )
        );
        
        bookButton.setOnMouseExited(e ->
            bookButton.setStyle(
                "-fx-background-color: linear-gradient(#4CAF50, #45a049); " +
                "-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 25; " +
                "-fx-border-color: #388E3C; -fx-border-width: 1; -fx-background-radius: 6; " +
                "-fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"
            )
        );
        
        actionSection.getChildren().add(bookButton);
        return actionSection;
    }
}