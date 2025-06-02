package application.ui.admin;

import application.model.Flight;
import application.service.AdminFlightService;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.BiConsumer;

public class AdminFlightDialogs {
    
    // Functional interface for alert callbacks
    public interface AlertCallback extends BiConsumer<String, String> {}
    
    // Traditional Java styling constants
    private static final String DIALOG_STYLE = "-fx-background-color: #f0f0f0; -fx-border-color: #808080; -fx-border-width: 2;";
    private static final String FIELD_STYLE = "-fx-background-color: white; -fx-border-color: #808080; -fx-border-width: 1; -fx-padding: 8; -fx-font-size: 12px;";
    private static final String LABEL_STYLE = "-fx-text-fill: #2c2c2c; -fx-font-size: 12px; -fx-font-weight: bold;";
    private static final String BUTTON_STYLE = "-fx-background-color: linear-gradient(#f0f0f0, #d0d0d0); -fx-text-fill: #2c2c2c; -fx-padding: 8 16; -fx-border-color: #808080; -fx-border-width: 1; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String SECTION_STYLE = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c; -fx-background-color: #e0e0e0; -fx-padding: 5; -fx-border-color: #808080; -fx-border-width: 1;";
    
    public static void addNewFlight(AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Dialog<Flight> dialog = new Dialog<>();
            dialog.setTitle("Add New Flight");
            dialog.setHeaderText("Create New Flight Schedule");
            
            // Apply traditional styling to dialog
            styleDialog(dialog);

            // Create form fields with traditional styling
            TextField idField = createStyledTextField("Enter flight ID");
            TextField flightNoField = createStyledTextField("e.g., PR123");
            TextField airlineField = createStyledTextField("e.g., Philippine Airlines");
            TextField originField = createStyledTextField("e.g., MNL");
            TextField destinationField = createStyledTextField("e.g., CEB");
            DatePicker departureDatePicker = createStyledDatePicker();
            TextField departureTimeField = createStyledTextField("HH:MM (e.g., 08:30)");
            DatePicker arrivalDatePicker = createStyledDatePicker();
            TextField arrivalTimeField = createStyledTextField("HH:MM (e.g., 10:45)");
            TextField durationField = createStyledTextField("e.g., 02:15");
            TextField aircraftField = createStyledTextField("e.g., Boeing 737");
            TextField seatsField = createStyledTextField("e.g., 180");
            ComboBox<String> statusBox = createStyledComboBox();
            statusBox.getItems().addAll("Active", "Cancelled", "Delayed", "Completed");
            statusBox.setValue("Active");
            TextField priceField = createStyledTextField("e.g., 5500.00");

            // Create traditional form layout
            VBox content = createTraditionalFlightFormLayout(
                idField, flightNoField, airlineField, originField, destinationField,
                departureDatePicker, departureTimeField, arrivalDatePicker, arrivalTimeField,
                durationField, aircraftField, seatsField, statusBox, priceField
            );

            dialog.getDialogPane().setContent(content);
            
            // Style dialog buttons
            styleDialogButtons(dialog);

            // Handle dialog result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return createFlightFromFields(idField, flightNoField, airlineField, originField,
                                                destinationField, departureDatePicker, departureTimeField,
                                                arrivalDatePicker, arrivalTimeField, durationField,
                                                aircraftField, seatsField, statusBox, priceField, alertCallback);
                }
                return null;
            });

            // Show dialog and process result
            dialog.showAndWait().ifPresent(flight -> {
                try {
                    if (AdminFlightService.addFlight(flight)) {
                        showTraditionalAlert("Success", "Flight added successfully!", Alert.AlertType.INFORMATION);
                        refreshCallback.run();
                    } else {
                        showTraditionalAlert("Error", "Failed to add flight. Please check the database connection and try again.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showTraditionalAlert("Database Error", "An error occurred while adding the flight: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

        } catch (Exception e) {
            System.err.println("Error in addNewFlight dialog: " + e.getMessage());
            showTraditionalAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    public static void editFlight(Flight flight, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Dialog<Flight> dialog = new Dialog<>();
            dialog.setTitle("Edit Flight");
            dialog.setHeaderText("Edit Flight Information");
            
            // Apply traditional styling to dialog
            styleDialog(dialog);

            // Create form fields with existing values
            TextField idField = createStyledTextField("Flight ID");
            idField.setText(String.valueOf(flight.getId()));
            idField.setDisable(true); // Don't allow changing ID
            
            TextField flightNoField = createStyledTextField("Flight Number");
            flightNoField.setText(flight.getFlightNo());
            
            TextField airlineField = createStyledTextField("Airline Name");
            airlineField.setText(flight.getAirlineName());
            
            TextField originField = createStyledTextField("Origin");
            originField.setText(flight.getOrigin());
            
            TextField destinationField = createStyledTextField("Destination");
            destinationField.setText(flight.getDestination());

            DatePicker departureDatePicker = createStyledDatePicker();
            departureDatePicker.setValue(flight.getDeparture().toLocalDate());
            
            TextField departureTimeField = createStyledTextField("Departure Time");
            departureTimeField.setText(flight.getDeparture().toLocalTime().toString());
            
            DatePicker arrivalDatePicker = createStyledDatePicker();
            arrivalDatePicker.setValue(flight.getArrival().toLocalDate());
            
            TextField arrivalTimeField = createStyledTextField("Arrival Time");
            arrivalTimeField.setText(flight.getArrival().toLocalTime().toString());

            TextField durationField = createStyledTextField("Duration");
            durationField.setText(flight.getDuration());
            
            TextField aircraftField = createStyledTextField("Aircraft Type");
            aircraftField.setText(flight.getAircraft());
            
            TextField seatsField = createStyledTextField("Available Seats");
            seatsField.setText(String.valueOf(flight.getSeats()));
            
            ComboBox<String> statusBox = createStyledComboBox();
            statusBox.getItems().addAll("Active", "Cancelled", "Delayed", "Completed");
            statusBox.setValue(flight.getStatus());
            
            TextField priceField = createStyledTextField("Base Price");
            priceField.setText(String.valueOf(flight.getPrice()));

            // Create traditional form layout
            VBox content = createTraditionalFlightFormLayout(
                idField, flightNoField, airlineField, originField, destinationField,
                departureDatePicker, departureTimeField, arrivalDatePicker, arrivalTimeField,
                durationField, aircraftField, seatsField, statusBox, priceField
            );

            dialog.getDialogPane().setContent(content);
            
            // Style dialog buttons
            styleDialogButtons(dialog);

            // Handle dialog result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return updateFlightFromFields(flight, flightNoField, airlineField, originField,
                                                destinationField, departureDatePicker, departureTimeField,
                                                arrivalDatePicker, arrivalTimeField, durationField,
                                                aircraftField, seatsField, statusBox, priceField, alertCallback);
                }
                return null;
            });

            // Show dialog and process result
            dialog.showAndWait().ifPresent(updatedFlight -> {
                try {
                    if (AdminFlightService.updateFlight(updatedFlight)) {
                        showTraditionalAlert("Success", "Flight updated successfully!", Alert.AlertType.INFORMATION);
                        refreshCallback.run();
                    } else {
                        showTraditionalAlert("Error", "Failed to update flight. Please check the database connection and try again.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showTraditionalAlert("Database Error", "An error occurred while updating the flight: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

        } catch (Exception e) {
            System.err.println("Error in editFlight dialog: " + e.getMessage());
            showTraditionalAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    public static void deleteFlight(Flight flight, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            // Show traditional confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Flight");
            alert.setHeaderText("Confirm Flight Deletion");
            alert.setContentText("Are you sure you want to delete flight: " + flight.getFlightNo() + "?\n\n" +
                                "Flight Details:\n" +
                                "• Route: " + flight.getRoute() + "\n" +
                                "• Departure: " + flight.getFormattedDeparture() + "\n" +
                                "• Status: " + flight.getStatus() + "\n\n" +
                                "This action cannot be undone and will permanently remove:\n" +
                                "• Flight schedule and details\n" +
                                "• All associated bookings\n" +
                                "• Passenger reservations");
            
            // Style the confirmation dialog
            styleTraditionalAlert(alert);
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        if (AdminFlightService.deleteFlight(flight.getId())) {
                            showTraditionalAlert("Success", "Flight '" + flight.getFlightNo() + "' has been deleted successfully!", Alert.AlertType.INFORMATION);
                            refreshCallback.run();
                        } else {
                            showTraditionalAlert("Error", "Failed to delete flight. The flight may have associated bookings that prevent deletion.", Alert.AlertType.ERROR);
                        }
                    } catch (Exception e) {
                        showTraditionalAlert("Database Error", "An error occurred while deleting the flight: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("Error in deleteFlight dialog: " + e.getMessage());
            showTraditionalAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    public static void showFlightDetails(Flight flight, AlertCallback alertCallback) {
        try {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Flight Details");
            dialog.setHeaderText("Flight Information - " + flight.getFlightNo());

            // Apply traditional styling to dialog
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setStyle(DIALOG_STYLE + " -fx-padding: 20;");
            dialogPane.setPrefWidth(700);
            dialogPane.setPrefHeight(600);

            // Create traditional details layout
            VBox content = createTraditionalDetailsLayout(flight);
            dialog.getDialogPane().setContent(content);
            
            // Style dialog buttons
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            Button closeButton = (Button) dialogPane.lookupButton(ButtonType.CLOSE);
            if (closeButton != null) {
                closeButton.setStyle(BUTTON_STYLE);
                closeButton.setText("Close");
            }

            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("Error showing flight details: " + e.getMessage());
            showTraditionalAlert("Error", "Unable to display flight details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // TRADITIONAL STYLING HELPER METHODS
    private static void styleDialog(Dialog<?> dialog) {
        // Style the dialog pane
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(DIALOG_STYLE + " -fx-padding: 15;");
        
        // Set preferred size for flight forms
        dialogPane.setPrefWidth(700);
        dialogPane.setPrefHeight(650);
    }
    
    private static void styleDialogButtons(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Style the buttons
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        
        if (okButton != null) {
            okButton.setStyle(BUTTON_STYLE);
            okButton.setText("Save");
        }
        
        if (cancelButton != null) {
            cancelButton.setStyle(BUTTON_STYLE);
            cancelButton.setText("Cancel");
        }
    }
    
    private static TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle(FIELD_STYLE);
        field.setPrefWidth(200);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }
    
    private static DatePicker createStyledDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setStyle(FIELD_STYLE);
        datePicker.setPrefWidth(200);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        return datePicker;
    }
    
    private static ComboBox<String> createStyledComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setStyle(FIELD_STYLE);
        comboBox.setPrefWidth(200);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        return comboBox;
    }
    
    private static Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle(LABEL_STYLE);
        label.setPrefWidth(130);
        label.setMinWidth(130);
        return label;
    }
    
    private static Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle(SECTION_STYLE);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }
    
    private static VBox createTraditionalFlightFormLayout(TextField idField, TextField flightNoField,
                                                         TextField airlineField, TextField originField,
                                                         TextField destinationField, DatePicker departureDatePicker,
                                                         TextField departureTimeField, DatePicker arrivalDatePicker,
                                                         TextField arrivalTimeField, TextField durationField,
                                                         TextField aircraftField, TextField seatsField,
                                                         ComboBox<String> statusBox, TextField priceField) {
        VBox mainContainer = new VBox(8);
        mainContainer.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15; -fx-border-color: #c0c0c0; -fx-border-width: 1;");
        
        // Basic Information Section
        Label basicInfoSection = createSectionLabel("Basic Flight Information");
        GridPane basicInfoGrid = createStyledGrid();
        
        int row = 0;
        basicInfoGrid.add(createStyledLabel("Flight ID:"), 0, row);
        basicInfoGrid.add(idField, 1, row);
        basicInfoGrid.add(createStyledLabel("Flight Number:"), 2, row);
        basicInfoGrid.add(flightNoField, 3, row++);
        
        basicInfoGrid.add(createStyledLabel("Airline:"), 0, row);
        GridPane.setColumnSpan(airlineField, 3);
        basicInfoGrid.add(airlineField, 1, row++);
        
        // Route Information Section
        Label routeSection = createSectionLabel("Route Information");
        GridPane routeGrid = createStyledGrid();
        
        row = 0;
        routeGrid.add(createStyledLabel("Origin:"), 0, row);
        routeGrid.add(originField, 1, row);
        routeGrid.add(createStyledLabel("Destination:"), 2, row);
        routeGrid.add(destinationField, 3, row++);
        
        // Schedule Information Section
        Label scheduleSection = createSectionLabel("Schedule Information");
        GridPane scheduleGrid = createStyledGrid();
        
        row = 0;
        scheduleGrid.add(createStyledLabel("Departure Date:"), 0, row);
        scheduleGrid.add(departureDatePicker, 1, row);
        scheduleGrid.add(createStyledLabel("Departure Time:"), 2, row);
        scheduleGrid.add(departureTimeField, 3, row++);
        
        scheduleGrid.add(createStyledLabel("Arrival Date:"), 0, row);
        scheduleGrid.add(arrivalDatePicker, 1, row);
        scheduleGrid.add(createStyledLabel("Arrival Time:"), 2, row);
        scheduleGrid.add(arrivalTimeField, 3, row++);
        
        scheduleGrid.add(createStyledLabel("Duration:"), 0, row);
        scheduleGrid.add(durationField, 1, row);
        row++;
        
        // Flight Details Section
        Label detailsSection = createSectionLabel("Flight Details");
        GridPane detailsGrid = createStyledGrid();
        
        row = 0;
        detailsGrid.add(createStyledLabel("Aircraft Type:"), 0, row);
        detailsGrid.add(aircraftField, 1, row);
        detailsGrid.add(createStyledLabel("Available Seats:"), 2, row);
        detailsGrid.add(seatsField, 3, row++);
        
        detailsGrid.add(createStyledLabel("Status:"), 0, row);
        detailsGrid.add(statusBox, 1, row);
        detailsGrid.add(createStyledLabel("Base Price (₱):"), 2, row);
        detailsGrid.add(priceField, 3, row++);
        
        mainContainer.getChildren().addAll(
            basicInfoSection, basicInfoGrid,
            routeSection, routeGrid,
            scheduleSection, scheduleGrid,
            detailsSection, detailsGrid
        );
        
        return mainContainer;
    }
    
    private static VBox createTraditionalDetailsLayout(Flight flight) {
        VBox mainContainer = new VBox(12);
        mainContainer.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 20; -fx-border-color: #c0c0c0; -fx-border-width: 1;");
        
        // Basic Information Section
        Label basicInfoSection = createSectionLabel("✈ Basic Flight Information");
        GridPane basicInfoGrid = createDetailsGrid();
        
        int row = 0;
        addDetailRow(basicInfoGrid, row++, "Flight ID:", String.valueOf(flight.getId()),
                    "Flight Number:", flight.getFlightNo());
        addDetailRow(basicInfoGrid, row++, "Airline:", flight.getAirlineName(),
                    "Aircraft Type:", flight.getAircraft());
        
        // Route Information Section
        Label routeSection = createSectionLabel("🌍 Route Information");
        GridPane routeGrid = createDetailsGrid();
        
        row = 0;
        addDetailRow(routeGrid, row++, "Origin:", flight.getOrigin(),
                    "Destination:", flight.getDestination());
        
        Label routeLabel = createStyledLabel("Full Route:");
        Label routeValue = new Label(flight.getRoute());
        routeValue.setStyle(LABEL_STYLE + " -fx-font-size: 14px; -fx-text-fill: #2980b9;");
        routeGrid.add(routeLabel, 0, row);
        GridPane.setColumnSpan(routeValue, 3);
        routeGrid.add(routeValue, 1, row++);
        
        // Schedule Information Section
        Label scheduleSection = createSectionLabel("🕒 Schedule Information");
        GridPane scheduleGrid = createDetailsGrid();
        
        row = 0;
        addDetailRow(scheduleGrid, row++, "Departure:", flight.getFormattedDeparture(),
                    "Arrival:", flight.getFormattedArrival());
        
        Label durationLabel = createStyledLabel("Flight Duration:");
        Label durationValue = new Label(flight.getDuration());
        durationValue.setStyle(LABEL_STYLE + " -fx-text-fill: #8e44ad;");
        scheduleGrid.add(durationLabel, 0, row);
        scheduleGrid.add(durationValue, 1, row++);
        
        // Flight Details Section
        Label detailsSection = createSectionLabel("📋 Flight Details");
        GridPane detailsGrid = createDetailsGrid();
        
        row = 0;
        addDetailRow(detailsGrid, row++, "Available Seats:", String.valueOf(flight.getSeats()),
                    "Status:", "");
        
        // Add status with color
        Label statusValue = new Label(flight.getStatus());
        String statusColor = getStatusColor(flight.getStatus());
        statusValue.setStyle(LABEL_STYLE + " -fx-text-fill: " + statusColor + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        detailsGrid.add(statusValue, 3, row - 1);
        
        Label priceLabel = createStyledLabel("Base Price:");
        Label priceValue = new Label(String.format("₱%.2f", flight.getPrice()));
        priceValue.setStyle(LABEL_STYLE + " -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        detailsGrid.add(priceLabel, 0, row);
        detailsGrid.add(priceValue, 1, row++);
        
        mainContainer.getChildren().addAll(
            basicInfoSection, basicInfoGrid,
            routeSection, routeGrid,
            scheduleSection, scheduleGrid,
            detailsSection, detailsGrid
        );
        
        return mainContainer;
    }
    
    private static GridPane createStyledGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #808080; -fx-border-width: 1;");
        setupGridColumns(grid);
        return grid;
    }
    
    private static GridPane createDetailsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #808080; -fx-border-width: 1;");
        setupGridColumns(grid);
        return grid;
    }
    
    private static void setupGridColumns(GridPane grid) {
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(130);
        col1.setPrefWidth(130);
        
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setMinWidth(150);
        
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setMinWidth(130);
        col3.setPrefWidth(130);
        
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setHgrow(Priority.ALWAYS);
        col4.setMinWidth(150);
        
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);
    }
    
    private static void addDetailRow(GridPane grid, int row, String label1, String value1,
                                   String label2, String value2) {
        Label l1 = createStyledLabel(label1);
        Label v1 = new Label(value1);
        v1.setStyle(LABEL_STYLE + " -fx-text-fill: #2c3e50;");
        Label l2 = createStyledLabel(label2);
        Label v2 = new Label(value2);
        v2.setStyle(LABEL_STYLE + " -fx-text-fill: #2c3e50;");
        
        grid.add(l1, 0, row);
        grid.add(v1, 1, row);
        grid.add(l2, 2, row);
        grid.add(v2, 3, row);
    }
    
    private static void showTraditionalAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        
        styleTraditionalAlert(alert);
        alert.showAndWait();
    }
    
    private static void styleTraditionalAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(DIALOG_STYLE + " -fx-padding: 20;");
        
        // Style alert buttons
        for (ButtonType buttonType : dialogPane.getButtonTypes()) {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            if (button != null) {
                button.setStyle(BUTTON_STYLE);
            }
        }
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
    
    // VALIDATION AND DATA PROCESSING METHODS
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
            flight.setId(Integer.parseInt(idField.getText().trim()));
            flight.setFlightNo(flightNoField.getText().trim());
            flight.setAirlineName(airlineField.getText().trim());
            flight.setOrigin(originField.getText().trim());
            flight.setDestination(destinationField.getText().trim());

            LocalDate depDate = departureDatePicker.getValue();
            LocalTime depTime = LocalTime.parse(departureTimeField.getText().trim());
            flight.setDeparture(LocalDateTime.of(depDate, depTime));

            LocalDate arrDate = arrivalDatePicker.getValue();
            LocalTime arrTime = LocalTime.parse(arrivalTimeField.getText().trim());
            flight.setArrival(LocalDateTime.of(arrDate, arrTime));

            flight.setDuration(durationField.getText().trim());
            flight.setAircraft(aircraftField.getText().trim());
            flight.setSeats(Integer.parseInt(seatsField.getText().trim()));
            flight.setStatus(statusBox.getValue());
            flight.setPrice(Double.parseDouble(priceField.getText().trim()));

            String validationError = AdminFlightService.validateFlightData(flight, false);
            if (validationError != null) {
                showTraditionalAlert("Validation Error", validationError, Alert.AlertType.WARNING);
                return null;
            }

            return flight;

        } catch (NumberFormatException e) {
            showTraditionalAlert("Validation Error", "Please enter valid numbers for ID, seats, and price.", Alert.AlertType.WARNING);
            return null;
        } catch (Exception e) {
            showTraditionalAlert("Validation Error", "Please check all fields and try again: " + e.getMessage(), Alert.AlertType.WARNING);
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
            flight.setFlightNo(flightNoField.getText().trim());
            flight.setAirlineName(airlineField.getText().trim());
            flight.setOrigin(originField.getText().trim());
            flight.setDestination(destinationField.getText().trim());

            LocalDate depDate = departureDatePicker.getValue();
            LocalTime depTime = LocalTime.parse(departureTimeField.getText().trim());
            flight.setDeparture(LocalDateTime.of(depDate, depTime));

            LocalDate arrDate = arrivalDatePicker.getValue();
            LocalTime arrTime = LocalTime.parse(arrivalTimeField.getText().trim());
            flight.setArrival(LocalDateTime.of(arrDate, arrTime));

            flight.setDuration(durationField.getText().trim());
            flight.setAircraft(aircraftField.getText().trim());
            flight.setSeats(Integer.parseInt(seatsField.getText().trim()));
            flight.setStatus(statusBox.getValue());
            flight.setPrice(Double.parseDouble(priceField.getText().trim()));

            String validationError = AdminFlightService.validateFlightData(flight, true);
            if (validationError != null) {
                showTraditionalAlert("Validation Error", validationError, Alert.AlertType.WARNING);
                return null;
            }

            return flight;

        } catch (NumberFormatException e) {
            showTraditionalAlert("Validation Error", "Please enter valid numbers for seats and price.", Alert.AlertType.WARNING);
            return null;
        } catch (Exception e) {
            showTraditionalAlert("Validation Error", "Please check all fields: " + e.getMessage(), Alert.AlertType.WARNING);
            return null;
        }
    }
}