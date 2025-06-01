package application.ui.admin;

import application.model.Flight;
import application.service.AdminFlightService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class AdminFlightsBuilder {
    // Interface for handling flight events - POLYMORPHISM
    public interface FlightsEventHandler {
        void onFlightEdit(Flight flight);
        void onFlightDelete(Flight flight);
        void onFlightAdd();
        void onFlightDetails(Flight flight);
        void onFlightsLoaded(int count);
        void onFlightsError(String error);
    }
    
    private final FlightsEventHandler eventHandler;
    
    public AdminFlightsBuilder(FlightsEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    public void setupFlightsTable(TableView<Flight> flightsTable) {
        try {
            flightsTable.getItems().clear();
            flightsTable.getColumns().clear();
            
            createFlightColumns(flightsTable);
            
            // Load data
            ObservableList<Flight> flights = AdminFlightService.getAllFlights();
            flightsTable.setItems(flights);
            
            setupFlightRowClickHandler(flightsTable);
            
            eventHandler.onFlightsLoaded(flights.size());
            
        } catch (Exception e) {
            System.err.println("Error setting up flights table: " + e.getMessage());
            eventHandler.onFlightsError(e.getMessage());
        }
    }
    
    public void setupFlightSearch(TextField searchField, TableView<Flight> flightsTable) {
        ObservableList<Flight> allFlights = flightsTable.getItems();
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                flightsTable.setItems(allFlights);
            } else {
                ObservableList<Flight> filteredFlights = FXCollections.observableArrayList();
                String searchTerm = newValue.toLowerCase().trim();
                
                for (Flight flight : allFlights) {
                    if (matchesSearchTerm(flight, searchTerm)) {
                        filteredFlights.add(flight);
                    }
                }
                
                flightsTable.setItems(filteredFlights);
            }
        });
    }
    
    // ENCAPSULATION - private search logic
    private boolean matchesSearchTerm(Flight flight, String searchTerm) {
        return (flight.getFlightNo() != null && flight.getFlightNo().toLowerCase().contains(searchTerm)) ||
               (flight.getAirlineName() != null && flight.getAirlineName().toLowerCase().contains(searchTerm)) ||
               (flight.getOrigin() != null && flight.getOrigin().toLowerCase().contains(searchTerm)) ||
               (flight.getDestination() != null && flight.getDestination().toLowerCase().contains(searchTerm)) ||
               (flight.getStatus() != null && flight.getStatus().toLowerCase().contains(searchTerm)) ||
               (flight.getAircraft() != null && flight.getAircraft().toLowerCase().contains(searchTerm)) ||
               String.valueOf(flight.getId()).contains(searchTerm) ||
               String.valueOf(flight.getPrice()).contains(searchTerm);
    }
    
    private void createFlightColumns(TableView<Flight> flightsTable) {
        TableColumn<Flight, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Flight, String> flightNoCol = new TableColumn<>("Flight No");
        flightNoCol.setCellValueFactory(new PropertyValueFactory<>("flightNo"));
        flightNoCol.setPrefWidth(80);
        
        TableColumn<Flight, String> airlineCol = new TableColumn<>("Airline");
        airlineCol.setCellValueFactory(new PropertyValueFactory<>("airlineName"));
        airlineCol.setPrefWidth(100);
        
        TableColumn<Flight, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));
        routeCol.setPrefWidth(120);
        
        TableColumn<Flight, String> departureCol = new TableColumn<>("Departure");
        departureCol.setCellValueFactory(new PropertyValueFactory<>("formattedDeparture"));
        departureCol.setPrefWidth(130);
        
        TableColumn<Flight, String> arrivalCol = new TableColumn<>("Arrival");
        arrivalCol.setCellValueFactory(new PropertyValueFactory<>("formattedArrival"));
        arrivalCol.setPrefWidth(130);
        
        TableColumn<Flight, Integer> seatsCol = new TableColumn<>("Seats");
        seatsCol.setCellValueFactory(new PropertyValueFactory<>("seats"));
        seatsCol.setPrefWidth(60);
        
        TableColumn<Flight, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);
        
        TableColumn<Flight, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(80);
        
        TableColumn<Flight, Void> actionsCol = createFlightActionsColumn();
        
        flightsTable.getColumns().addAll(idCol, flightNoCol, airlineCol, routeCol,
                departureCol, arrivalCol, seatsCol, statusCol, priceCol, actionsCol);
    }
    
    private TableColumn<Flight, Void> createFlightActionsColumn() {
        TableColumn<Flight, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        
        actionsCol.setCellFactory(param -> new TableCell<Flight, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttonsBox = new HBox(5);
            
            {
                editBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
                deleteBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
                buttonsBox.getChildren().addAll(editBtn, deleteBtn);
                buttonsBox.setPadding(new Insets(5));
                
                editBtn.setOnAction(event -> {
                    Flight flight = getTableView().getItems().get(getIndex());
                    eventHandler.onFlightEdit(flight);
                });
                
                deleteBtn.setOnAction(event -> {
                    Flight flight = getTableView().getItems().get(getIndex());
                    eventHandler.onFlightDelete(flight);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });
        
        return actionsCol;
    }
    
    private void setupFlightRowClickHandler(TableView<Flight> flightsTable) {
        flightsTable.setRowFactory(tv -> {
            TableRow<Flight> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Flight selectedFlight = row.getItem();
                    eventHandler.onFlightDetails(selectedFlight);
                }
            });
            return row;
        });
    }
}