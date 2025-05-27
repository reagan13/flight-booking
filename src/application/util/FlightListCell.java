package application.util;

import application.model.Flight;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FlightListCell extends ListCell<Flight> {
    
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    @Override
    protected void updateItem(Flight flight, boolean empty) {
        super.updateItem(flight, empty);
        
        if (empty || flight == null) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(createMobileFlightCard(flight));
            setText(null);
        }
    }
    
    private VBox createMobileFlightCard(Flight flight) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 15; " +
                     "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 3, 0, 0, 1);");
        card.setPrefWidth(320); // Fits in 350px mobile window
        
        // Header with airline and status
        HBox header = createHeaderSection(flight);
        
        // Route section
        HBox routeSection = createRouteSection(flight);
        
        // Footer with details and price
        HBox footer = createFooterSection(flight);
        
        card.getChildren().addAll(header, routeSection, footer);
        
        // Add margin for mobile list
        VBox.setMargin(card, new Insets(6, 0, 6, 0));
        
        return card;
    }
    
    private HBox createHeaderSection(Flight flight) {
        HBox header = new HBox(10);
        header.setStyle("-fx-alignment: center-left;");
        
        VBox airlineBox = new VBox(3);
        Label airlineLabel = new Label(flight.getAirlineName());
        airlineLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1565C0;");
        Label flightNoLabel = new Label("Flight " + flight.getFlightNo());
        flightNoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        airlineBox.getChildren().addAll(airlineLabel, flightNoLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(flight.getStatus().toUpperCase());
        statusLabel.setStyle(getMobileStatusStyle(flight.getStatus()));
        
        header.getChildren().addAll(airlineBox, spacer, statusLabel);
        return header;
    }
    
    private HBox createRouteSection(Flight flight) {
        HBox routeBox = new HBox(20);
        routeBox.setStyle("-fx-alignment: center; -fx-padding: 12 0;");
        
        // Origin
        VBox originBox = new VBox(3);
        originBox.setStyle("-fx-alignment: center;");
        Label originLabel = new Label(flight.getOrigin());
        originLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label depTimeLabel = new Label(flight.getDeparture().format(timeFormatter));
        depTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        Label depDateLabel = new Label(flight.getDeparture().format(DateTimeFormatter.ofPattern("MMM dd")));
        depDateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");
        originBox.getChildren().addAll(originLabel, depTimeLabel, depDateLabel);
        
        // Flight path
        VBox pathBox = new VBox(3);
        pathBox.setStyle("-fx-alignment: center;");
        Label arrowLabel = new Label("✈️");
        arrowLabel.setStyle("-fx-font-size: 16px;");
        Label durationLabel = new Label(flight.getDuration());
        durationLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666; -fx-font-weight: bold;");
        pathBox.getChildren().addAll(arrowLabel, durationLabel);
        
        // Destination
        VBox destBox = new VBox(3);
        destBox.setStyle("-fx-alignment: center;");
        Label destLabel = new Label(flight.getDestination());
        destLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label arrTimeLabel = new Label(flight.getArrival().format(timeFormatter));
        arrTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        Label arrDateLabel = new Label(flight.getArrival().format(DateTimeFormatter.ofPattern("MMM dd")));
        arrDateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");
        destBox.getChildren().addAll(destLabel, arrTimeLabel, arrDateLabel);
        
        routeBox.getChildren().addAll(originBox, pathBox, destBox);
        return routeBox;
    }
    
    private HBox createFooterSection(Flight flight) {
        HBox footer = new HBox(10);
        footer.setStyle("-fx-alignment: center-left;");
        
        VBox leftDetails = new VBox(3);
        Label aircraftLabel = new Label("🛫 " + flight.getAircraft());
        aircraftLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        Label seatsLabel = new Label(flight.getSeats() + " seats left");
        seatsLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + 
                           (flight.getSeats() < 10 ? "#ff5722" : "#4caf50") + "; -fx-font-weight: bold;");
        leftDetails.getChildren().addAll(aircraftLabel, seatsLabel);
        
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        
        VBox priceBox = new VBox(3);
        priceBox.setStyle("-fx-alignment: center-right;");
        
        Label fromLabel = new Label("from");
        fromLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        Label priceLabel = new Label(currencyFormat.format(flight.getPrice()));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        Label perPersonLabel = new Label("per person");
        perPersonLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #999;");
        
        priceBox.getChildren().addAll(fromLabel, priceLabel, perPersonLabel);
        
        footer.getChildren().addAll(leftDetails, footerSpacer, priceBox);
        return footer;
    }
    
    private String getMobileStatusStyle(String status) {
        String baseStyle = "-fx-text-fill: white; -fx-font-size: 9px; -fx-padding: 4 8; " +
                          "-fx-background-radius: 12; -fx-font-weight: bold;";
        
        switch (status.toLowerCase()) {
            case "scheduled":
                return "-fx-background-color: #4CAF50; " + baseStyle;
            case "delayed":
                return "-fx-background-color: #FF9800; " + baseStyle;
            case "cancelled":
                return "-fx-background-color: #F44336; " + baseStyle;
            case "boarding":
                return "-fx-background-color: #2196F3; " + baseStyle;
            default:
                return "-fx-background-color: #757575; " + baseStyle;
        }
    }
}