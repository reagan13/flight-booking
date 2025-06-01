package application.ui.admin;

import application.model.Booking;
import application.service.AdminBookingService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class AdminBookingsBuilder {
    
    public interface BookingsEventHandler {
        void onBookingView(Booking booking);
        void onBookingStatusChange(Booking booking);
        void onBookingDelete(Booking booking);
        void onBookingsLoaded(int count);
        void onBookingsError(String error);
    }
    
    private final BookingsEventHandler eventHandler;
    
    public AdminBookingsBuilder(BookingsEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    public void setupBookingsTable(TableView<Booking> bookingsTable) {
        try {
            bookingsTable.getItems().clear();
            bookingsTable.getColumns().clear();
            
            createBookingColumns(bookingsTable);
            
            ObservableList<Booking> bookings = AdminBookingService.getAllBookings();
            bookingsTable.setItems(bookings);
            
            eventHandler.onBookingsLoaded(bookings.size());
            
        } catch (Exception e) {
            System.err.println("Error setting up bookings table: " + e.getMessage());
            eventHandler.onBookingsError(e.getMessage());
        }
    }
    
    public void setupBookingSearch(TextField searchField, TableView<Booking> bookingsTable) {
        ObservableList<Booking> allBookings = bookingsTable.getItems();
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                bookingsTable.setItems(allBookings);
            } else {
                ObservableList<Booking> filteredBookings = FXCollections.observableArrayList();
                String searchTerm = newValue.toLowerCase().trim();
                
                for (Booking booking : allBookings) {
                    if (matchesBookingSearchTerm(booking, searchTerm)) {
                        filteredBookings.add(booking);
                    }
                }
                
                bookingsTable.setItems(filteredBookings);
            }
        });
    }
    
    private boolean matchesBookingSearchTerm(Booking booking, String searchTerm) {
        return (booking.getBookingReference() != null && booking.getBookingReference().toLowerCase().contains(searchTerm)) ||
               (booking.getCustomerName() != null && booking.getCustomerName().toLowerCase().contains(searchTerm)) ||
               (booking.getCustomerEmail() != null && booking.getCustomerEmail().toLowerCase().contains(searchTerm)) ||
               (booking.getFlightInfo() != null && booking.getFlightInfo().toLowerCase().contains(searchTerm)) ||
               (booking.getStatus() != null && booking.getStatus().toLowerCase().contains(searchTerm)) ||
               (booking.getSeatNumber() != null && booking.getSeatNumber().toLowerCase().contains(searchTerm)) ||
               String.valueOf(booking.getId()).contains(searchTerm);
    }
    
    private void createBookingColumns(TableView<Booking> bookingsTable) {
        TableColumn<Booking, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Booking, String> refCol = new TableColumn<>("Reference");
        refCol.setCellValueFactory(new PropertyValueFactory<>("bookingReference"));
        refCol.setPrefWidth(100);
        
        TableColumn<Booking, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerCol.setPrefWidth(120);
        
        TableColumn<Booking, String> flightCol = new TableColumn<>("Flight");
        flightCol.setCellValueFactory(new PropertyValueFactory<>("flightInfo"));
        flightCol.setPrefWidth(200);
        
        TableColumn<Booking, String> seatCol = new TableColumn<>("Seat");
        seatCol.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        seatCol.setPrefWidth(60);
        
        TableColumn<Booking, String> dateCol = new TableColumn<>("Booking Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedBookingDate"));
        dateCol.setPrefWidth(130);
        
        TableColumn<Booking, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("formattedAmount"));
        amountCol.setPrefWidth(80);
        
        TableColumn<Booking, String> paymentStatusCol = new TableColumn<>("Payment");
        paymentStatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        paymentStatusCol.setPrefWidth(80);
        
        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(90);
        
        TableColumn<Booking, Void> actionsCol = createBookingActionsColumn();
        
        bookingsTable.getColumns().addAll(idCol, refCol, customerCol, flightCol, seatCol,
                dateCol, amountCol, paymentStatusCol, statusCol, actionsCol);
    }
    
    private TableColumn<Booking, Void> createBookingActionsColumn() {
        TableColumn<Booking, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        
        actionsCol.setCellFactory(param -> new TableCell<Booking, Void>() {
            private final Button viewBtn = new Button("View");
            private final Button statusBtn = new Button("Status");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttonsBox = new HBox(5);
            
            {
                viewBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 8;");
                statusBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 8;");
                deleteBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 8;");
                buttonsBox.getChildren().addAll(viewBtn, statusBtn, deleteBtn);
                buttonsBox.setPadding(new Insets(3));
                
                viewBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    eventHandler.onBookingView(booking);
                });
                
                statusBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    eventHandler.onBookingStatusChange(booking);
                });
                
                deleteBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    eventHandler.onBookingDelete(booking);
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
}