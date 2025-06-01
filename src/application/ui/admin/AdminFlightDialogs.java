package application.ui.admin;

import application.model.Flight;
import application.service.AdminFlightService;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.BiConsumer;

public class AdminFlightDialogs {
    
    public interface AlertCallback extends BiConsumer<String, String> {}
    
    public static void addNewFlight(AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Dialog<Flight> dialog = new Dialog<>();
            dialog.setTitle("Add New Flight");
            dialog.setHeaderText("Add New Flight Information");

            // Create form fields
            TextField idField = new TextField();
            TextField flightNoField = new TextField();
            TextField airlineField = new TextField();
            TextField originField = new TextField();
            TextField destinationField = new TextField();
            DatePicker departureDatePicker = new DatePicker();
            TextField departureTimeField = new TextField();
            DatePicker arrivalDatePicker = new DatePicker();
            TextField arrivalTimeField = new TextField();
            TextField durationField = new TextField();
            TextField aircraftField = new TextField();
            TextField seatsField = new TextField();
            ComboBox<String> statusBox = new ComboBox<>();
            statusBox.getItems().addAll("Active", "Cancelled", "Delayed", "Completed");
            statusBox.setValue("Active");
            TextField priceField = new TextField();

            // Set placeholders
            setFlightFieldPlaceholders(idField, flightNoField, airlineField, originField, 
                                     destinationField, departureTimeField, arrivalTimeField,
                                     durationField, aircraftField, seatsField, priceField);

            // Create layout
            GridPane grid = createFlightFormLayout(idField, flightNoField, airlineField, 
                                                 originField, destinationField, departureDatePicker,
                                                 departureTimeField, arrivalDatePicker, arrivalTimeField,
                                                 durationField, aircraftField, seatsField, statusBox, priceField);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setPrefSize(600, 500);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return createFlightFromFields(idField, flightNoField, airlineField, originField,
                                                destinationField, departureDatePicker, departureTimeField,
                                                arrivalDatePicker, arrivalTimeField, durationField,
                                                aircraftField, seatsField, statusBox, priceField, alertCallback);
                }
                return null;
            });

            dialog.showAndWait().ifPresent(flight -> {
                try {
                    if (AdminFlightService.addFlight(flight)) {
                        alertCallback.accept("Success", "Flight added successfully!");
                        refreshCallback.run();
                    } else {
                        alertCallback.accept("Error", "Failed to add flight.");
                    }
                } catch (Exception e) {
                    alertCallback.accept("Database Error", "Error adding flight: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            alertCallback.accept("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    public static void editFlight(Flight flight, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Dialog<Flight> dialog = new Dialog<>();
            dialog.setTitle("Edit Flight");
            dialog.setHeaderText("Edit Flight Information");

            // Create form fields with existing values
            TextField idField = new TextField(String.valueOf(flight.getId()));
            idField.setDisable(true); // Don't allow changing ID
            TextField flightNoField = new TextField(flight.getFlightNo());
            TextField airlineField = new TextField(flight.getAirlineName());
            TextField originField = new TextField(flight.getOrigin());
            TextField destinationField = new TextField(flight.getDestination());

            DatePicker departureDatePicker = new DatePicker(flight.getDeparture().toLocalDate());
            TextField departureTimeField = new TextField(flight.getDeparture().toLocalTime().toString());
            DatePicker arrivalDatePicker = new DatePicker(flight.getArrival().toLocalDate());
            TextField arrivalTimeField = new TextField(flight.getArrival().toLocalTime().toString());

            TextField durationField = new TextField(flight.getDuration());
            TextField aircraftField = new TextField(flight.getAircraft());
            TextField seatsField = new TextField(String.valueOf(flight.getSeats()));
            ComboBox<String> statusBox = new ComboBox<>();
            statusBox.getItems().addAll("Active", "Cancelled", "Delayed", "Completed");
            statusBox.setValue(flight.getStatus());
            TextField priceField = new TextField(String.valueOf(flight.getPrice()));

            // Create layout
            GridPane grid = createFlightFormLayout(idField, flightNoField, airlineField, 
                                                 originField, destinationField, departureDatePicker,
                                                 departureTimeField, arrivalDatePicker, arrivalTimeField,
                                                 durationField, aircraftField, seatsField, statusBox, priceField);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setPrefSize(600, 500);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return updateFlightFromFields(flight, flightNoField, airlineField, originField,
                                                destinationField, departureDatePicker, departureTimeField,
                                                arrivalDatePicker, arrivalTimeField, durationField,
                                                aircraftField, seatsField, statusBox, priceField, alertCallback);
                }
                return null;
            });

            dialog.showAndWait().ifPresent(updatedFlight -> {
                try {
                    if (AdminFlightService.updateFlight(updatedFlight)) {
                        alertCallback.accept("Success", "Flight updated successfully!");
                        refreshCallback.run();
                    } else {
                        alertCallback.accept("Error", "Failed to update flight.");
                    }
                } catch (Exception e) {
                    alertCallback.accept("Database Error", "Error updating flight: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            alertCallback.accept("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    public static void deleteFlight(Flight flight, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Flight");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Delete flight: " + flight.getFlightNo() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (AdminFlightService.deleteFlight(flight.getId())) {
                        alertCallback.accept("Success", "Flight deleted successfully!");
                        refreshCallback.run();
                    } else {
                        alertCallback.accept("Error", "Failed to delete flight.");
                    }
                }
            });

        } catch (Exception e) {
            alertCallback.accept("Error", "Error deleting flight: " + e.getMessage());
        }
    }
    
    public static void showFlightDetails(Flight flight, AlertCallback alertCallback) {
        try {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Flight Details");
            dialog.setHeaderText("Flight Information - " + flight.getFlightNo());

            // Create GridPane for organized layout
            GridPane grid = new GridPane();
            grid.setHgap(20);
            grid.setVgap(15);
            grid.setPadding(new Insets(20));

            // Set column constraints
            ColumnConstraints labelCol = new ColumnConstraints();
            labelCol.setPercentWidth(30);
            ColumnConstraints valueCol1 = new ColumnConstraints();
            valueCol1.setPercentWidth(35);
            ColumnConstraints labelCol2 = new ColumnConstraints();
            labelCol2.setPercentWidth(30);
            ColumnConstraints valueCol2 = new ColumnConstraints();
            valueCol2.setPercentWidth(35);
            grid.getColumnConstraints().addAll(labelCol, valueCol1, labelCol2, valueCol2);

            // Style definitions
            String headerStyle = "-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;";
            String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #34495e;";
            String valueStyle = "-fx-text-fill: #2c3e50; -fx-font-size: 14px;";

            int currentRow = 0;

            // Basic Information Section
            Label basicHeader = new Label("✈ Basic Information");
            basicHeader.setStyle(headerStyle);
            grid.add(basicHeader, 0, currentRow++, 4, 1);

            addDetailRow(grid, currentRow++, "Flight ID:", String.valueOf(flight.getId()),
                        "Flight Number:", flight.getFlightNo(), labelStyle, valueStyle);
            addDetailRow(grid, currentRow++, "Airline:", flight.getAirlineName(),
                        "Aircraft:", flight.getAircraft(), labelStyle, valueStyle);

            // Route Information Section
            currentRow++; // Add space
            Label routeHeader = new Label("🌍 Route Information");
            routeHeader.setStyle(headerStyle);
            grid.add(routeHeader, 0, currentRow++, 4, 1);

            addDetailRow(grid, currentRow++, "Origin:", flight.getOrigin(),
                        "Destination:", flight.getDestination(), labelStyle, valueStyle);

            Label routeLabel = new Label("Route:");
            routeLabel.setStyle(labelStyle);
            Label routeValue = new Label(flight.getRoute());
            routeValue.setStyle(valueStyle + " -fx-font-size: 16px;");
            grid.add(routeLabel, 0, currentRow);
            grid.add(routeValue, 1, currentRow, 3, 1);
            currentRow++;

            // Schedule Information Section
            currentRow++; // Add space
            Label scheduleHeader = new Label("🕒 Schedule Information");
            scheduleHeader.setStyle(headerStyle);
            grid.add(scheduleHeader, 0, currentRow++, 4, 1);

            addDetailRow(grid, currentRow++, "Departure:", flight.getFormattedDeparture(),
                        "Arrival:", flight.getFormattedArrival(), labelStyle, valueStyle);

            Label durationLabel = new Label("Duration:");
            durationLabel.setStyle(labelStyle);
            Label durationValue = new Label(flight.getDuration());
            durationValue.setStyle(valueStyle);
            grid.add(durationLabel, 0, currentRow);
            grid.add(durationValue, 1, currentRow);
            currentRow++;

            // Flight Details Section
            currentRow++; // Add space
            Label detailsHeader = new Label("📋 Flight Details");
            detailsHeader.setStyle(headerStyle);
            grid.add(detailsHeader, 0, currentRow++, 4, 1);

            Label statusValue = new Label(flight.getStatus());
            String statusColor = getStatusColor(flight.getStatus());
            statusValue.setStyle(valueStyle + " -fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");

            addDetailRow(grid, currentRow++, "Available Seats:", String.valueOf(flight.getSeats()),
                        "Status:", "", labelStyle, valueStyle);
            grid.add(statusValue, 3, currentRow - 1);

            Label priceLabel = new Label("Base Price:");
            priceLabel.setStyle(labelStyle);
            Label priceValue = new Label(String.format("₱%.2f", flight.getPrice()));
            priceValue.setStyle(valueStyle + " -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
            grid.add(priceLabel, 0, currentRow);
            grid.add(priceValue, 1, currentRow);

            // Set content and buttons
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.getDialogPane().setPrefSize(650, 500);
            dialog.getDialogPane().setStyle("-fx-background-color: #f8f9fa;");

            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("Error showing flight details: " + e.getMessage());
            alertCallback.accept("Error", "Unable to display flight details: " + e.getMessage());
        }
    }
    
    // PRIVATE HELPER METHODS
    private static void setFlightFieldPlaceholders(TextField idField, TextField flightNoField, 
                                                 TextField airlineField, TextField originField,
                                                 TextField destinationField, TextField departureTimeField,
                                                 TextField arrivalTimeField, TextField durationField,
                                                 TextField aircraftField, TextField seatsField,
                                                 TextField priceField) {
        idField.setPromptText("Enter flight ID");
        flightNoField.setPromptText("e.g., PR123");
        airlineField.setPromptText("e.g., Philippine Airlines");
        originField.setPromptText("e.g., MNL");
        destinationField.setPromptText("e.g., CEB");
        departureTimeField.setPromptText("HH:MM (e.g., 08:30)");
        arrivalTimeField.setPromptText("HH:MM (e.g., 10:45)");
        durationField.setPromptText("e.g., 02:15");
        aircraftField.setPromptText("e.g., Boeing 737");
        seatsField.setPromptText("e.g., 180");
        priceField.setPromptText("e.g., 5500.00");
    }
    
    private static GridPane createFlightFormLayout(TextField idField, TextField flightNoField,
                                                 TextField airlineField, TextField originField,
                                                 TextField destinationField, DatePicker departureDatePicker,
                                                 TextField departureTimeField, DatePicker arrivalDatePicker,
                                                 TextField arrivalTimeField, TextField durationField,
                                                 TextField aircraftField, TextField seatsField,
                                                 ComboBox<String> statusBox, TextField priceField) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Set column constraints for better layout
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        int row = 0;

        // Basic Information Section
        Label basicInfoLabel = new Label("Basic Information");
        basicInfoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(basicInfoLabel, 0, row++, 4, 1);

        grid.add(new Label("Flight ID:"), 0, row);
        grid.add(idField, 1, row);
        grid.add(new Label("Flight Number:"), 2, row);
        grid.add(flightNoField, 3, row++);

        grid.add(new Label("Airline:"), 0, row);
        grid.add(airlineField, 1, row, 3, 1);
        row++;

        // Route Information
        Label routeLabel = new Label("Route Information");
        routeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(routeLabel, 0, row++, 4, 1);

        grid.add(new Label("Origin:"), 0, row);
        grid.add(originField, 1, row);
        grid.add(new Label("Destination:"), 2, row);
        grid.add(destinationField, 3, row++);

        // Departure Information
        Label departureLabel = new Label("Departure Information");
        departureLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(departureLabel, 0, row++, 4, 1);

        grid.add(new Label("Departure Date:"), 0, row);
        grid.add(departureDatePicker, 1, row);
        grid.add(new Label("Departure Time:"), 2, row);
        grid.add(departureTimeField, 3, row++);

        // Arrival Information
        Label arrivalLabel = new Label("Arrival Information");
        arrivalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(arrivalLabel, 0, row++, 4, 1);

        grid.add(new Label("Arrival Date:"), 0, row);
        grid.add(arrivalDatePicker, 1, row);
        grid.add(new Label("Arrival Time:"), 2, row);
        grid.add(arrivalTimeField, 3, row++);

        // Flight Details
        Label detailsLabel = new Label("Flight Details");
        detailsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(detailsLabel, 0, row++, 4, 1);

        grid.add(new Label("Duration:"), 0, row);
        grid.add(durationField, 1, row);
        grid.add(new Label("Aircraft Type:"), 2, row);
        grid.add(aircraftField, 3, row++);

        grid.add(new Label("Available Seats:"), 0, row);
        grid.add(seatsField, 1, row);
        grid.add(new Label("Status:"), 2, row);
        grid.add(statusBox, 3, row++);

        // Pricing
        Label pricingLabel = new Label("Pricing");
        pricingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(pricingLabel, 0, row++, 4, 1);

        grid.add(new Label("Base Price (₱):"), 0, row);
        grid.add(priceField, 1, row);

        return grid;
    }
    
    private static Flight createFlightFromFields(TextField idField, TextField flightNoField,
                                               TextField airlineField, TextField originField,
                                               TextField destinationField, DatePicker departureDatePicker,
                                               TextField departureTimeField, DatePicker arrivalDatePicker,
                                               TextField arrivalTimeField, TextField durationField,
                                               TextField aircraftField, TextField seatsField,
                                               ComboBox<String> statusBox, TextField priceField,
                                               AlertCallback alertCallback) {
        try {
            Flight flight = new Flight();
            flight.setId(Integer.parseInt(idField.getText()));
            flight.setFlightNo(flightNoField.getText());
            flight.setAirlineName(airlineField.getText());
            flight.setOrigin(originField.getText());
            flight.setDestination(destinationField.getText());

            LocalDate depDate = departureDatePicker.getValue();
            LocalTime depTime = LocalTime.parse(departureTimeField.getText());
            flight.setDeparture(LocalDateTime.of(depDate, depTime));

            LocalDate arrDate = arrivalDatePicker.getValue();
            LocalTime arrTime = LocalTime.parse(arrivalTimeField.getText());
            flight.setArrival(LocalDateTime.of(arrDate, arrTime));

            flight.setDuration(durationField.getText());
            flight.setAircraft(aircraftField.getText());
            flight.setSeats(Integer.parseInt(seatsField.getText()));
            flight.setStatus(statusBox.getValue());
            flight.setPrice(Double.parseDouble(priceField.getText()));

            String validationError = AdminFlightService.validateFlightData(flight, false);
            if (validationError != null) {
                alertCallback.accept("Validation Error", validationError);
                return null;
            }

            return flight;

        } catch (Exception e) {
            alertCallback.accept("Validation Error", "Please check all fields and try again: " + e.getMessage());
            return null;
        }
    }
    
    private static Flight updateFlightFromFields(Flight flight, TextField flightNoField,
                                               TextField airlineField, TextField originField,
                                               TextField destinationField, DatePicker departureDatePicker,
                                               TextField departureTimeField, DatePicker arrivalDatePicker,
                                               TextField arrivalTimeField, TextField durationField,
                                               TextField aircraftField, TextField seatsField,
                                               ComboBox<String> statusBox, TextField priceField,
                                               AlertCallback alertCallback) {
        try {
            flight.setFlightNo(flightNoField.getText());
            flight.setAirlineName(airlineField.getText());
            flight.setOrigin(originField.getText());
            flight.setDestination(destinationField.getText());

            LocalDate depDate = departureDatePicker.getValue();
            LocalTime depTime = LocalTime.parse(departureTimeField.getText());
            flight.setDeparture(LocalDateTime.of(depDate, depTime));

            LocalDate arrDate = arrivalDatePicker.getValue();
            LocalTime arrTime = LocalTime.parse(arrivalTimeField.getText());
            flight.setArrival(LocalDateTime.of(arrDate, arrTime));

            flight.setDuration(durationField.getText());
            flight.setAircraft(aircraftField.getText());
            flight.setSeats(Integer.parseInt(seatsField.getText()));
            flight.setStatus(statusBox.getValue());
            flight.setPrice(Double.parseDouble(priceField.getText()));

            String validationError = AdminFlightService.validateFlightData(flight, true);
            if (validationError != null) {
                alertCallback.accept("Validation Error", validationError);
                return null;
            }

            return flight;

        } catch (Exception e) {
            alertCallback.accept("Validation Error", "Please check all fields: " + e.getMessage());
            return null;
        }
    }
    
    private static void addDetailRow(GridPane grid, int row, String label1, String value1,
                                   String label2, String value2, String labelStyle, String valueStyle) {
        Label l1 = new Label(label1);
        l1.setStyle(labelStyle);
        Label v1 = new Label(value1);
        v1.setStyle(valueStyle);
        Label l2 = new Label(label2);
        l2.setStyle(labelStyle);
        Label v2 = new Label(value2);
        v2.setStyle(valueStyle);
        
        grid.add(l1, 0, row);
        grid.add(v1, 1, row);
        grid.add(l2, 2, row);
        grid.add(v2, 3, row);
    }
    
    private static String getStatusColor(String status) {
        if (status == null) return "#2c3e50";
        
        switch (status.toLowerCase()) {
            case "active":
                return "#27ae60"; // Green
            case "cancelled":
                return "#e74c3c"; // Red
            case "delayed":
                return "#f39c12"; // Orange
            case "completed":
                return "#3498db"; // Blue
            default:
                return "#2c3e50"; // Dark gray
        }
    }
}