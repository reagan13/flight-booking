package application.controller.client;

import application.model.Flight;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FlightDetailsController {
    
    private final Runnable onBookFlight;
    private final Runnable onGoBack;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
    
    public FlightDetailsController(Runnable onBookFlight, Runnable onGoBack) {
        this.onBookFlight = onBookFlight;
        this.onGoBack = onGoBack;
    }
    
    public VBox createFlightDetailsView(Flight flight) {
        VBox container = new VBox(20);
        container.setStyle("-fx-padding: 10;");
        container.setFillWidth(true);
        container.setMaxWidth(Double.MAX_VALUE);

        // Back button
        Button backButton = createBackButton();
        
        // Main flight card
        VBox card = createFlightCard(flight);
        
        container.getChildren().addAll(backButton, card);
        return container;
    }
    
    private Button createBackButton() {
        Button backButton = new Button("Back to Flights");
        backButton.setStyle(
            "-fx-background-color: #f5f5f5; -fx-text-fill: #333; " +
            "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; " +
            "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1; " +
            "-fx-cursor: hand; -fx-font-weight: 500;"
        );
        backButton.setOnAction(e -> onGoBack.run());
        
        // Hover effects
        backButton.setOnMouseEntered(e -> 
            backButton.setStyle(
                "-fx-background-color: #e8e8e8; -fx-text-fill: #333; " +
                "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; " +
                "-fx-border-color: #ccc; -fx-border-radius: 8; -fx-border-width: 1; " +
                "-fx-cursor: hand; -fx-font-weight: 500;"
            )
        );
        
        backButton.setOnMouseExited(e -> 
            backButton.setStyle(
                "-fx-background-color: #f5f5f5; -fx-text-fill: #333; " +
                "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; " +
                "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1; " +
                "-fx-cursor: hand; -fx-font-weight: 500;"
            )
        );
        
        return backButton;
    }
    
    private VBox createFlightCard(Flight flight) {
        VBox card = new VBox(20);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
            "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 3);"
        );
        card.setMaxWidth(Double.MAX_VALUE);
        card.setFillWidth(true);

        VBox headerSection = createFlightHeader(flight);
        VBox routeSection = createRouteSection(flight);
        VBox detailsSection = createFlightDetails(flight);
        VBox priceSection = createPriceSection(flight);
        Button bookButton = createBookButton();

        card.getChildren().addAll(headerSection, routeSection, detailsSection, priceSection, bookButton);
        return card;
    }
    
    private VBox createFlightHeader(Flight flight) {
        VBox headerSection = new VBox(15);

        VBox airlineInfo = new VBox(5);
        Label airlineLabel = new Label(flight.getAirlineName());
        airlineLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1565C0;");
        airlineLabel.setWrapText(true);

        Label flightNoLabel = new Label("Flight " + flight.getFlightNo());
        flightNoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");

        airlineInfo.getChildren().addAll(airlineLabel, flightNoLabel);

        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label(flight.getStatus().toUpperCase());
        statusLabel.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; " +
            "-fx-padding: 8 16; -fx-background-radius: 20; " +
            "-fx-font-size: 12px; -fx-font-weight: bold;"
        );

        statusRow.getChildren().add(statusLabel);
        headerSection.getChildren().addAll(airlineInfo, statusRow);
        return headerSection;
    }
    
    private VBox createRouteSection(Flight flight) {
        VBox routeSection = new VBox(20);
        routeSection.setAlignment(Pos.CENTER);
        routeSection.setStyle("-fx-padding: 20 10;");

        HBox airportsRow = new HBox();
        airportsRow.setAlignment(Pos.CENTER);
        airportsRow.setSpacing(20);

        // Origin
        VBox originBox = createAirportBox(flight.getOrigin(), "Departure");
        
        // Flight path
        VBox pathBox = createFlightPathBox(flight.getDuration());
        
        // Destination
        VBox destBox = createAirportBox(flight.getDestination(), "Arrival");

        airportsRow.getChildren().addAll(originBox, pathBox, destBox);

        VBox timesSection = new VBox(15);
        timesSection.setAlignment(Pos.CENTER);

        VBox depSection = createTimeSection("Departure", flight.getDeparture());
        VBox arrSection = createTimeSection("Arrival", flight.getArrival());

        timesSection.getChildren().addAll(depSection, arrSection);
        routeSection.getChildren().addAll(airportsRow, timesSection);
        return routeSection;
    }
    
    private VBox createAirportBox(String code, String label) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(80);

        Label codeLabel = new Label(code);
        codeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        Label typeLabel = new Label(label);
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-weight: bold;");

        box.getChildren().addAll(codeLabel, typeLabel);
        return box;
    }
    
    private VBox createFlightPathBox(String duration) {
        VBox pathBox = new VBox(8);
        pathBox.setAlignment(Pos.CENTER);
        pathBox.setMaxWidth(60);

        Label planeIcon = new Label("✈️");
        planeIcon.setStyle("-fx-font-size: 24px;");

        Label durationLabel = new Label(duration);
        durationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        durationLabel.setWrapText(true);

        pathBox.getChildren().addAll(planeIcon, durationLabel);
        return pathBox;
    }
    
    private VBox createTimeSection(String label, java.time.LocalDateTime dateTime) {
        VBox timeSection = new VBox(5);
        timeSection.setAlignment(Pos.CENTER);
        timeSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 12; -fx-background-radius: 10;");

        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-font-weight: bold;");

        Label timeLabel = new Label(dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label dateLabel = new Label(dateTime.format(DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy")));
        dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        dateLabel.setWrapText(true);

        timeSection.getChildren().addAll(titleLabel, timeLabel, dateLabel);
        return timeSection;
    }
    
    private VBox createFlightDetails(Flight flight) {
        VBox detailsSection = new VBox(15);
        detailsSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-background-radius: 12;");

        Label detailsTitle = new Label("Flight Information");
        detailsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox detailsGrid = new VBox(15);
        detailsGrid.getChildren().addAll(
            createDetailRow("✈️ Aircraft Type", flight.getAircraft()),
            createDetailRow("💺 Available Seats", flight.getSeats() + " seats"),
            createDetailRow("📅 Flight Date", 
                flight.getDeparture().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))),
            createDetailRow("⏱️ Flight Duration", flight.getDuration()),
            createDetailRow("🛫 Departure Airport", flight.getOrigin()),
            createDetailRow("🛬 Arrival Airport", flight.getDestination())
        );

        detailsSection.getChildren().addAll(detailsTitle, detailsGrid);
        return detailsSection;
    }
    
    private VBox createDetailRow(String label, String value) {
        VBox row = new VBox(5);
        row.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 8;");
        row.setMaxWidth(Double.MAX_VALUE);

        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-font-weight: bold;");
        labelText.setWrapText(true);

        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 15px; -fx-text-fill: #333; -fx-font-weight: bold;");
        valueText.setWrapText(true);
        valueText.setMaxWidth(Double.MAX_VALUE);

        row.getChildren().addAll(labelText, valueText);
        return row;
    }
    
    private VBox createPriceSection(Flight flight) {
        VBox priceSection = new VBox(15);
        priceSection.setAlignment(Pos.CENTER);
        priceSection.setStyle("-fx-background-color: #E8F5E8; -fx-padding: 20; -fx-background-radius: 12;");

        Label priceTitle = new Label("Total Price");
        priceTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-font-weight: bold;");

        Label priceAmount = new Label(currencyFormat.format(flight.getPrice()));
        priceAmount.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        priceAmount.setWrapText(true);

        Label priceNote = new Label("per person • includes taxes & fees");
        priceNote.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        priceNote.setWrapText(true);

        priceSection.getChildren().addAll(priceTitle, priceAmount, priceNote);
        return priceSection;
    }
    
    private Button createBookButton() {
        Button bookButton = new Button("Book This Flight");
        bookButton.setStyle(
            "-fx-background-color: #2196F3; -fx-text-fill: white; " +
            "-fx-font-size: 16px; -fx-padding: 15 30; -fx-background-radius: 25; " +
            "-fx-font-weight: bold; -fx-cursor: hand;"
        );
        bookButton.setMaxWidth(Double.MAX_VALUE);
        bookButton.setOnAction(e -> onBookFlight.run());
        return bookButton;
    }
}