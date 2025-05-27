package application.controller;

import application.model.Flight;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

public class FlightDetailsController {
    
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormatter;
    private BookingDialogController bookingController;
    
    public FlightDetailsController(NumberFormat currencyFormat, DateTimeFormatter dateFormatter, 
                                 BookingDialogController bookingController) {
        this.currencyFormat = currencyFormat;
        this.dateFormatter = dateFormatter;
        this.bookingController = bookingController;
    }
    
    public void showFlightDetails(Flight flight) {
        System.out.println("Showing details for flight: " + flight.getFlightNo());
        
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Flight Details");
        dialog.setHeaderText(flight.getAirlineName() + " - " + flight.getFlightNo());
        
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        
        int row = 0;
        
        // Status
        Label statusLabel = new Label("Status: " + flight.getStatus());
        statusLabel.setStyle(getStatusStyle(flight.getStatus()));
        gridPane.add(statusLabel, 0, row++, 2, 1);
        
        // Route
        Label routeLabel = new Label("Route:");
        routeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        gridPane.add(routeLabel, 0, row++, 2, 1);
        
        gridPane.add(new Label("Origin:"), 0, row);
        gridPane.add(new Label(flight.getOrigin()), 1, row++);
        
        gridPane.add(new Label("Destination:"), 0, row);
        gridPane.add(new Label(flight.getDestination()), 1, row++);
        
        // Schedule
        Label scheduleLabel = new Label("Schedule:");
        scheduleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        gridPane.add(scheduleLabel, 0, row++, 2, 1);
        
        gridPane.add(new Label("Departure:"), 0, row);
        gridPane.add(new Label(flight.getDeparture().format(dateFormatter)), 1, row++);
        
        gridPane.add(new Label("Arrival:"), 0, row);
        gridPane.add(new Label(flight.getArrival().format(dateFormatter)), 1, row++);
        
        gridPane.add(new Label("Duration:"), 0, row);
        gridPane.add(new Label(flight.getDuration()), 1, row++);
        
        // Aircraft
        Label aircraftLabel = new Label("Aircraft Information:");
        aircraftLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        gridPane.add(aircraftLabel, 0, row++, 2, 1);
        
        gridPane.add(new Label("Aircraft Type:"), 0, row);
        gridPane.add(new Label(flight.getAircraft()), 1, row++);
        
        gridPane.add(new Label("Available Seats:"), 0, row);
        gridPane.add(new Label(String.valueOf(flight.getSeats())), 1, row++);
        
        // Price
        Label priceLabel = new Label("Price Information:");
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        gridPane.add(priceLabel, 0, row++, 2, 1);
        
        Label fareLabel = new Label("Fare:");
        Label fareValueLabel = new Label(currencyFormat.format(flight.getPrice()));
        fareValueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        gridPane.add(fareLabel, 0, row);
        gridPane.add(fareValueLabel, 1, row++);
        
        gridPane.add(new Separator(), 0, row++, 2, 1);
        
        // Book button
        if (!"cancelled".equalsIgnoreCase(flight.getStatus())) {
            Button bookNowButton = new Button("Book This Flight");
            bookNowButton.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            bookNowButton.setMaxWidth(Double.MAX_VALUE);
            bookNowButton.setOnAction(e -> {
                dialog.close();
                bookingController.showBookingForm(flight);
            });
            
            gridPane.add(bookNowButton, 0, row++, 2, 1);
        } else {
            Label cancelledLabel = new Label("This flight is cancelled and cannot be booked.");
            cancelledLabel.setStyle("-fx-text-fill: red; -fx-font-style: italic;");
            gridPane.add(cancelledLabel, 0, row++, 2, 1);
        }
        
        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    private String getStatusStyle(String status) {
        switch (status.toLowerCase()) {
            case "scheduled":
                return "-fx-text-fill: green; -fx-font-weight: bold;";
            case "delayed":
                return "-fx-text-fill: orange; -fx-font-weight: bold;";
            case "cancelled":
                return "-fx-text-fill: red; -fx-font-weight: bold;";
            default:
                return "-fx-font-weight: bold;";
        }
    }
}