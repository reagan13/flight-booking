package application.ui.client;

import application.model.Flight;
import application.service.FlightService;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HomeScreenBuilder {
    
    private final HomeEventHandler eventHandler;
    private final FlightService flightService;
    private final NumberFormat currencyFormat;
    private final DateTimeFormatter dateFormatter;
    
    public HomeScreenBuilder(HomeEventHandler eventHandler, FlightService flightService) {
        this.eventHandler = eventHandler;
        this.flightService = flightService;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
        this.dateFormatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
    }
    
    /**
     * Setup the flight grid view instead of ListView
     */
    public void setupFlightGridView(VBox homeScreen) {
        if (homeScreen == null) {
            System.err.println("homeScreen is null, skipping home setup");
            return;
        }
        
        // Clear existing content except header
        if (homeScreen.getChildren().size() > 1) {
            homeScreen.getChildren().remove(1, homeScreen.getChildren().size());
        }
        
        // Create scrollable grid container
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        // Grid container for flight cards
        FlowPane flightGrid = new FlowPane();
        flightGrid.setHgap(10);
        flightGrid.setVgap(10);
        flightGrid.setPrefWrapLength(500); // Adjust based on container width
        flightGrid.setAlignment(Pos.TOP_LEFT);
        flightGrid.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        
        scrollPane.setContent(flightGrid);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        homeScreen.getChildren().add(scrollPane);
        
        // Store grid reference for loading flights
        homeScreen.getProperties().put("flightGrid", flightGrid);
    }
    
    /**
     * Create simple flight card with main information only
     */
    /**
 * Create simple flight card with main information only
 */
    public VBox createSimpleFlightCard(Flight flight) {
        VBox card = new VBox(8);
        card.setPrefWidth(220);
        card.setMinHeight(140);
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8; " +
                "-fx-padding: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1); " +
                "-fx-cursor: hand;");

        // Route (From → To) - CORRECTED METHOD NAMES
        HBox routeBox = new HBox(5);
        routeBox.setAlignment(Pos.CENTER_LEFT);

        Label fromLabel = new Label(flight.getOrigin()); // Changed from getDepartureCity()
        fromLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label arrowLabel = new Label("→");
        arrowLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label toLabel = new Label(flight.getDestination()); // Changed from getArrivalCity()
        toLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        routeBox.getChildren().addAll(fromLabel, arrowLabel, toLabel);

        // Airline - CORRECTED METHOD NAME
        Label airlineLabel = new Label(flight.getAirlineName()); // Changed from getAirline()
        airlineLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Date & Time - CORRECTED METHOD NAME
        Label dateTimeLabel = new Label(flight.getDeparture().format(dateFormatter)); // Changed from getDepartureTime()
        dateTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Price
        Label priceLabel = new Label(currencyFormat.format(flight.getPrice()));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        // Available seats - CORRECTED TO USE EXISTING METHOD
        Label seatsLabel = new Label(flight.getSeats() + " seats"); // Changed from getAvailableSeats()
        seatsLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");

        card.getChildren().addAll(routeBox, airlineLabel, dateTimeLabel, priceLabel, seatsLabel);

        // Click handler
        card.setOnMouseClicked(e -> eventHandler.onShowFlightDetails(flight));

        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: #2196F3; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 8; " +
                    "-fx-padding: 11; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0, 0, 2); " +
                    "-fx-cursor: hand;");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 8; " +
                    "-fx-padding: 12; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1); " +
                    "-fx-cursor: hand;");
        });

        return card;
    }

    /**
     * Load available flights and display in grid
     */
    public void loadAvailableFlights(VBox homeScreen, Label sectionLabel, ProgressIndicator loadingIndicator) {
        eventHandler.onShowStatus("Loading flights...", true);
        
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }
        
        if (sectionLabel != null) {
            sectionLabel.setText("Loading flights...");
        }
        
        flightService.getAvailableFlights()
            .thenAccept(flights -> {
                Platform.runLater(() -> {
                    try {
                        // Get the flight grid from homeScreen
                        FlowPane flightGrid = (FlowPane) homeScreen.getProperties().get("flightGrid");
                        if (flightGrid != null) {
                            flightGrid.getChildren().clear();
                            
                            // Add flight cards to grid
                            for (Flight flight : flights) {
                                VBox flightCard = createSimpleFlightCard(flight);
                                flightGrid.getChildren().add(flightCard);
                            }
                        }
                        
                        if (loadingIndicator != null) {
                            loadingIndicator.setVisible(false);
                        }
                        
                        if (sectionLabel != null) {
                            sectionLabel.setText("Available Flights (" + flights.size() + ")");
                        }
                        
                        eventHandler.onHideStatus();
                    } catch (Exception e) {
                        System.err.println("Error updating UI with flights: " + e.getMessage());
                        e.printStackTrace();
                        eventHandler.onHideStatus();
                    }
                });
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    
                    if (sectionLabel != null) {
                        sectionLabel.setText("Error loading flights");
                    }
                    
                    eventHandler.onHideStatus();
                });
                return null;
            });
    }
    
    /**
     * Setup search functionality
     */
    public void setupSearchField(TextField searchField) {
        if (searchField != null) {
            searchField.setOnAction(this::handleSearch);
        }
    }
    
    // Helper methods
    private void handleSearch(ActionEvent event) {
        TextField searchField = (TextField) event.getSource();
        String query = searchField.getText().trim();
        
        if (query.isEmpty()) {
            eventHandler.onLoadAvailableFlights();
            return;
        }
        
        eventHandler.onSearchFlights(query);
    }
    
    /**
     * Interface for handling home screen events
     */
    public interface HomeEventHandler {
        void onShowFlightDetails(Flight flight);
        void onShowStatus(String message, boolean showProgress);
        void onHideStatus();
        void onLoadAvailableFlights();
        void onSearchFlights(String query);
        void onExploreDestinations();
        void onViewAllDestinations();
        void onSearchDestination(String destination);
    }
}