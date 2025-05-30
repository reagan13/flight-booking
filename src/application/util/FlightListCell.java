package application.util;

import application.model.Flight;
import application.ui.HomeScreenBuilder;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FlightListCell extends ListCell<Flight> {
    
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");
    
    @Override
    protected void updateItem(Flight flight, boolean empty) {
        super.updateItem(flight, empty);
        
        if (empty || flight == null) {
            setGraphic(null);
            return;
        }
        
        VBox card = createFlightCard(flight);
        setGraphic(card);
    }
    
    private VBox createFlightCard(Flight flight) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                     "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2); " +
                     "-fx-cursor: hand;");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setFillWidth(true);
        
        // Header with airline and price
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox airlineBox = new VBox(2);
        Label airlineLabel = new Label(flight.getAirlineName());
        airlineLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        Label flightNoLabel = new Label("Flight " + flight.getFlightNo());
        flightNoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        airlineBox.getChildren().addAll(airlineLabel, flightNoLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        Label priceLabel = new Label(currencyFormat.format(flight.getPrice()));
        priceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        Label perPersonLabel = new Label("per person");
        perPersonLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        priceBox.getChildren().addAll(priceLabel, perPersonLabel);
        
        header.getChildren().addAll(airlineBox, spacer, priceBox);
        
        // Route section
        HBox routeSection = new HBox();
        routeSection.setAlignment(Pos.CENTER);
        routeSection.setSpacing(20);
        routeSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");
        
        // Origin
        VBox originBox = new VBox(3);
        originBox.setAlignment(Pos.CENTER);
        Label originCode = new Label(flight.getOrigin());
        originCode.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label depTime = new Label(flight.getDeparture().format(timeFormatter));
        depTime.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        Label depDate = new Label(flight.getDeparture().format(dateFormatter));
        depDate.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");
        originBox.getChildren().addAll(originCode, depTime, depDate);
        
        // Duration and arrow
        VBox centerBox = new VBox(3);
        centerBox.setAlignment(Pos.CENTER);
        Label planeIcon = new Label("✈️");
        planeIcon.setStyle("-fx-font-size: 16px;");
        Label durationLabel = new Label(flight.getDuration());
        durationLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        centerBox.getChildren().addAll(planeIcon, durationLabel);
        
        // Destination
        VBox destBox = new VBox(3);
        destBox.setAlignment(Pos.CENTER);
        Label destCode = new Label(flight.getDestination());
        destCode.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label arrTime = new Label(flight.getArrival().format(timeFormatter));
        arrTime.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        Label arrDate = new Label(flight.getArrival().format(dateFormatter));
        arrDate.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");
        destBox.getChildren().addAll(destCode, arrTime, arrDate);
        
        routeSection.getChildren().addAll(originBox, centerBox, destBox);
        
        // Footer with details and book button
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setSpacing(15);
        
        VBox detailsBox = new VBox(2);
        Label aircraftLabel = new Label("Aircraft: " + flight.getAircraft());
        aircraftLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        Label seatsLabel = new Label(flight.getSeats() + " seats available");
        seatsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        detailsBox.getChildren().addAll(aircraftLabel, seatsLabel);
        
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        
        Button selectButton = new Button("Select Flight");
        selectButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                             "-fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 20; " +
                             "-fx-cursor: hand;");
        
        // Handle flight selection
        selectButton.setOnAction(e -> handleFlightSelection(flight));
        
        // Make the entire card clickable
        card.setOnMouseClicked(e -> handleFlightSelection(flight));
        
        footer.getChildren().addAll(detailsBox, footerSpacer, selectButton);
        
        card.getChildren().addAll(header, routeSection, footer);
        return card;
    }
    
    private void handleFlightSelection(Flight flight) {
        // Get the event handler from the ListView properties
        Object eventHandler = getListView().getProperties().get("controller");
        
        if (eventHandler instanceof HomeScreenBuilder.HomeEventHandler) {
            HomeScreenBuilder.HomeEventHandler handler = (HomeScreenBuilder.HomeEventHandler) eventHandler;
            handler.onShowFlightDetails(flight);
        } else {
            System.err.println("Event handler not found or incorrect type: " + eventHandler);
            System.err.println("Available properties: " + getListView().getProperties().keySet());
        }
    }
}