package application.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import application.model.Flight;
import application.HomeController;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FlightListCell extends ListCell<Flight> {
    
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    
    @Override
    protected void updateItem(Flight flight, boolean empty) {
        super.updateItem(flight, empty);
        
        if (empty || flight == null) {
            setGraphic(null);
            setStyle("-fx-background-color: transparent;");
        } else {
            VBox flightCard = createMobileFlightCard(flight);
            
            // Add spacing container around each flight card
            VBox cardWithSpacing = new VBox();
            cardWithSpacing.getChildren().add(flightCard);
            cardWithSpacing.setStyle("-fx-padding: 0 0 15 0;"); // 15px bottom spacing
            
            setGraphic(cardWithSpacing);
            setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        }
    }

    private VBox createMobileFlightCard(Flight flight) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 18; " +
                     "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2); " +
                     "-fx-cursor: hand;");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);

        // Header with airline and status
        HBox header = createHeaderSection(flight);

        // Route section
        HBox routeSection = createRouteSection(flight);

        // Footer with details and price
        HBox footer = createFooterSection(flight);

        // Book button
        Button bookButton = new Button("Select Flight");
        bookButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                           "-fx-font-size: 14px; -fx-padding: 12 24; -fx-background-radius: 20; " +
                           "-fx-font-weight: bold; -fx-cursor: hand;");
        bookButton.setMaxWidth(Double.MAX_VALUE);

        // Add click handler for the button
        bookButton.setOnAction(e -> {
            ListView listView = getListView();
            if (listView != null) {
                Object controller = listView.getProperties().get("controller");
                if (controller instanceof HomeController) {
                    ((HomeController) controller).showFlightDetails(flight);
                }
            }
        });

        card.getChildren().addAll(header, routeSection, footer, bookButton);

        
        // Add margin for mobile list
        VBox.setMargin(card, new Insets(8, 12, 8, 12));
        
        return card;
    }

    private HBox createHeaderSection(Flight flight) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        VBox airlineInfo = new VBox(2);
        Label airlineName = new Label(flight.getAirlineName());
        airlineName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1565C0;");
        
        Label flightNumber = new Label("Flight " + flight.getFlightNo());
        flightNumber.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        airlineInfo.getChildren().addAll(airlineName, flightNumber);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label(flight.getStatus().toUpperCase());
        status.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                       "-fx-padding: 6 12; -fx-background-radius: 12; " +
                       "-fx-font-size: 10px; -fx-font-weight: bold;");

        header.getChildren().addAll(airlineInfo, spacer, status);
        return header;
    }

    private HBox createRouteSection(Flight flight) {
        HBox routeBox = new HBox(20);
        routeBox.setAlignment(Pos.CENTER);
        routeBox.setStyle("-fx-padding: 10 0;");

        // Origin
        VBox originBox = new VBox(5);
        originBox.setAlignment(Pos.CENTER);
        Label originCode = new Label(flight.getOrigin());
        originCode.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        Label depTime = new Label(flight.getDeparture().format(DateTimeFormatter.ofPattern("HH:mm")));
        depTime.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");
        originBox.getChildren().addAll(originCode, depTime);

        // Flight path
        VBox pathBox = new VBox(5);
        pathBox.setAlignment(Pos.CENTER);
        Label planeIcon = new Label("✈️");
        planeIcon.setStyle("-fx-font-size: 16px;");
        Label duration = new Label(flight.getDuration());
        duration.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        pathBox.getChildren().addAll(planeIcon, duration);

        // Destination
        VBox destBox = new VBox(5);
        destBox.setAlignment(Pos.CENTER);
        Label destCode = new Label(flight.getDestination());
        destCode.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        Label arrTime = new Label(flight.getArrival().format(DateTimeFormatter.ofPattern("HH:mm")));
        arrTime.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");
        destBox.getChildren().addAll(destCode, arrTime);

        routeBox.getChildren().addAll(originBox, pathBox, destBox);
        return routeBox;
    }

    private HBox createFooterSection(Flight flight) {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setSpacing(10);

        VBox detailsBox = new VBox(2);
        Label aircraft = new Label("✈️ " + flight.getAircraft());
        aircraft.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        Label seats = new Label("💺 " + flight.getSeats() + " seats available");
        seats.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        detailsBox.getChildren().addAll(aircraft, seats);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label priceLabel = new Label("From");
        priceLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        Label price = new Label(currencyFormat.format(flight.getPrice()));
        price.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        priceBox.getChildren().addAll(priceLabel, price);

        footer.getChildren().addAll(detailsBox, spacer, priceBox);
        return footer;
    }
}