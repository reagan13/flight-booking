package application.ui.admin;

import application.model.Booking;
import application.service.AdminBookingService;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.function.BiConsumer;

public class AdminBookingDialogs {
    
    // Functional interface for alert callbacks
    public interface AlertCallback extends BiConsumer<String, String> {}
    
    // Traditional Java styling constants
    private static final String DIALOG_STYLE = "-fx-background-color: #f0f0f0; -fx-border-color: #808080; -fx-border-width: 2;";
    private static final String FIELD_STYLE = "-fx-background-color: white; -fx-border-color: #808080; -fx-border-width: 1; -fx-padding: 8; -fx-font-size: 12px;";
    private static final String LABEL_STYLE = "-fx-text-fill: #2c2c2c; -fx-font-size: 12px; -fx-font-weight: bold;";
    private static final String VALUE_STYLE = "-fx-text-fill: #2c3e50; -fx-font-size: 12px;";
    private static final String BUTTON_STYLE = "-fx-background-color: linear-gradient(#f0f0f0, #d0d0d0); -fx-text-fill: #2c2c2c; -fx-padding: 8 16; -fx-border-color: #808080; -fx-border-width: 1; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String SECTION_STYLE = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c; -fx-background-color: #e0e0e0; -fx-padding: 8; -fx-border-color: #808080; -fx-border-width: 1;";
    
    public static void showBookingDetails(Booking booking, AlertCallback alertCallback) {
        try {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Booking Details");
            dialog.setHeaderText("Booking Information - " + booking.getBookingReference());

            // Apply traditional styling to dialog
            styleDialog(dialog);

            // Create traditional details layout
            VBox content = createTraditionalDetailsLayout(booking);
            dialog.getDialogPane().setContent(content);
            
            // Style dialog buttons
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
            if (closeButton != null) {
                closeButton.setStyle(BUTTON_STYLE);
                closeButton.setText("Close");
            }

            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("Error showing booking details: " + e.getMessage());
            showTraditionalAlert("Error", "Unable to display booking details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    public static void changeBookingStatus(Booking booking, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Change Booking Status");
            dialog.setHeaderText("Change Status - " + booking.getBookingReference());

            // Apply traditional styling to dialog
            styleStatusDialog(dialog);

            // Create traditional status change layout
            VBox content = createTraditionalStatusChangeLayout(booking);
            dialog.getDialogPane().setContent(content);
            
            // Style dialog buttons
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            
            if (okButton != null) {
                okButton.setStyle(BUTTON_STYLE);
                okButton.setText("Update Status");
            }
            
            if (cancelButton != null) {
                cancelButton.setStyle(BUTTON_STYLE);
                cancelButton.setText("Cancel");
            }

            // Get the status ComboBox for result conversion
            ComboBox<String> statusBox = (ComboBox<String>) content.lookup("#statusComboBox");

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK && statusBox != null) {
                    return statusBox.getValue();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(newStatus -> {
                if (!newStatus.equals(booking.getStatus())) {
                    try {
                        if (AdminBookingService.updateBookingStatus(booking.getId(), newStatus)) {
                            showTraditionalAlert("Success", "Booking status updated successfully to: " + newStatus.toUpperCase(), Alert.AlertType.INFORMATION);
                            refreshCallback.run();
                        } else {
                            showTraditionalAlert("Error", "Failed to update booking status. Please check the database connection and try again.", Alert.AlertType.ERROR);
                        }
                    } catch (Exception e) {
                        showTraditionalAlert("Database Error", "An error occurred while updating the booking status: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                } else {
                    showTraditionalAlert("Information", "No changes were made to the booking status.", Alert.AlertType.INFORMATION);
                }
            });

        } catch (Exception e) {
            System.err.println("Error in changeBookingStatus dialog: " + e.getMessage());
            showTraditionalAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    public static void deleteBooking(Booking booking, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            // Show traditional confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Booking");
            alert.setHeaderText("Confirm Booking Deletion");
            alert.setContentText("Are you sure you want to delete booking: " + booking.getBookingReference() + "?\n\n" +
                                "Booking Details:\n" +
                                "• Customer: " + booking.getCustomerName() + "\n" +
                                "• Flight: " + booking.getFlightInfo() + "\n" +
                                "• Amount: " + booking.getFormattedAmount() + "\n" +
                                "• Status: " + booking.getStatus().toUpperCase() + "\n\n" +
                                "This action cannot be undone and will permanently remove:\n" +
                                "• Booking record and details\n" +
                                "• Associated transaction records\n" +
                                "• Payment information");
            
            // Style the confirmation dialog
            styleTraditionalAlert(alert);
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        if (AdminBookingService.deleteBooking(booking.getId())) {
                            showTraditionalAlert("Success", "Booking '" + booking.getBookingReference() + "' has been deleted successfully!", Alert.AlertType.INFORMATION);
                            refreshCallback.run();
                        } else {
                            showTraditionalAlert("Error", "Failed to delete booking. The booking may have dependencies that prevent deletion.", Alert.AlertType.ERROR);
                        }
                    } catch (Exception e) {
                        showTraditionalAlert("Database Error", "An error occurred while deleting the booking: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("Error in deleteBooking dialog: " + e.getMessage());
            showTraditionalAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // TRADITIONAL STYLING HELPER METHODS
    private static void styleDialog(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(DIALOG_STYLE + " -fx-padding: 20;");
        dialogPane.setPrefWidth(750);
        dialogPane.setPrefHeight(550);
    }
    
    private static void styleStatusDialog(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(DIALOG_STYLE + " -fx-padding: 20;");
        dialogPane.setPrefWidth(500);
        dialogPane.setPrefHeight(350);
    }
    
    private static Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle(LABEL_STYLE);
        label.setPrefWidth(130);
        label.setMinWidth(130);
        return label;
    }
    
    private static Label createStyledValue(String text) {
        Label label = new Label(text);
        label.setStyle(VALUE_STYLE);
        return label;
    }
    
    private static Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle(SECTION_STYLE);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }
    
    private static ComboBox<String> createStyledComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setStyle(FIELD_STYLE);
        comboBox.setPrefWidth(200);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        return comboBox;
    }
    
    private static VBox createTraditionalDetailsLayout(Booking booking) {
        VBox mainContainer = new VBox(12);
        mainContainer.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 20; -fx-border-color: #c0c0c0; -fx-border-width: 1;");
        
        // Booking Information Section
        Label bookingSection = createSectionLabel("📋 Booking Information");
        GridPane bookingGrid = createDetailsGrid();
        
        int row = 0;
        addDetailRow(bookingGrid, row++, "Booking ID:", String.valueOf(booking.getId()),
                    "Reference:", booking.getBookingReference());
        
        // Add status with color
        bookingGrid.add(createStyledLabel("Status:"), 0, row);
        Label statusValue = new Label(booking.getStatus().toUpperCase());
        String statusColor = getBookingStatusColor(booking.getStatus());
        statusValue.setStyle(VALUE_STYLE + " -fx-text-fill: " + statusColor + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        bookingGrid.add(statusValue, 1, row);
        
        bookingGrid.add(createStyledLabel("Seat Number:"), 2, row);
        bookingGrid.add(createStyledValue(booking.getSeatNumber()), 3, row);
        row++;
        
        // Customer Information Section
        Label customerSection = createSectionLabel("👤 Customer Information");
        GridPane customerGrid = createDetailsGrid();
        
        row = 0;
        addDetailRow(customerGrid, row++, "Customer Name:", booking.getCustomerName(),
                    "Email Address:", booking.getCustomerEmail() != null ? booking.getCustomerEmail() : "N/A");
        
        // Flight Information Section
        Label flightSection = createSectionLabel("✈ Flight Information");
        GridPane flightGrid = createDetailsGrid();
        
        row = 0;
        flightGrid.add(createStyledLabel("Flight Details:"), 0, row);
        Label flightValue = createStyledValue(booking.getFlightInfo());
        flightValue.setStyle(VALUE_STYLE + " -fx-font-size: 13px; -fx-text-fill: #2980b9;");
        GridPane.setColumnSpan(flightValue, 3);
        flightGrid.add(flightValue, 1, row);
        row++;
        
        // Payment Information Section
        Label paymentSection = createSectionLabel("💳 Payment Information");
        GridPane paymentGrid = createDetailsGrid();
        
        row = 0;
        // Add amount with special formatting
        paymentGrid.add(createStyledLabel("Total Amount:"), 0, row);
        Label amountValue = new Label(booking.getFormattedAmount());
        amountValue.setStyle(VALUE_STYLE + " -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        paymentGrid.add(amountValue, 1, row);
        
        paymentGrid.add(createStyledLabel("Payment Status:"), 2, row);
        paymentGrid.add(createStyledValue(booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "N/A"), 3, row);
        row++;
        
        addDetailRow(paymentGrid, row++, "Payment Method:", booking.getPaymentMethod() != null ? booking.getPaymentMethod() : "N/A",
                    "Booking Date:", booking.getFormattedBookingDate());
        
        mainContainer.getChildren().addAll(
            bookingSection, bookingGrid,
            customerSection, customerGrid,
            flightSection, flightGrid,
            paymentSection, paymentGrid
        );
        
        return mainContainer;
    }
    
    private static VBox createTraditionalStatusChangeLayout(Booking booking) {
        VBox mainContainer = new VBox(15);
        mainContainer.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 20; -fx-border-color: #c0c0c0; -fx-border-width: 1;");
        
        // Current Status Section
        Label currentSection = createSectionLabel("Current Booking Status");
        VBox currentStatusBox = new VBox(10);
        currentStatusBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #808080; -fx-border-width: 1;");
        
        Label currentStatusLabel = new Label("Current Status: " + booking.getStatus().toUpperCase());
        String statusColor = getBookingStatusColor(booking.getStatus());
        currentStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + statusColor + ";");
        
        Label bookingRefLabel = new Label("Booking Reference: " + booking.getBookingReference());
        bookingRefLabel.setStyle(VALUE_STYLE + " -fx-font-size: 11px;");
        
        currentStatusBox.getChildren().addAll(currentStatusLabel, bookingRefLabel);
        
        // New Status Section
        Label newSection = createSectionLabel("Select New Status");
        VBox newStatusBox = new VBox(12);
        newStatusBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #808080; -fx-border-width: 1;");
        
        Label newStatusLabel = createStyledLabel("New Status:");
        ComboBox<String> statusBox = createStyledComboBox();
        statusBox.setId("statusComboBox"); // Set ID for lookup
        statusBox.getItems().addAll("pending", "confirmed", "cancelled", "completed", "payment_failed");
        statusBox.setValue(booking.getStatus());
        
        // Instructions Section
        VBox instructionsBox = new VBox(8);
        instructionsBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 12; -fx-border-color: #808080; -fx-border-width: 1;");
        
        Label instructionsTitle = new Label("Status Change Guidelines:");
        instructionsTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label instruction1 = new Label("• Confirmed: Marks booking and payment as complete");
        instruction1.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");
        
        Label instruction2 = new Label("• Cancelled: Cancels the booking and processes refund");
        instruction2.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");
        
        Label instruction3 = new Label("• Completed: Flight has been completed successfully");
        instruction3.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");
        
        instructionsBox.getChildren().addAll(instructionsTitle, instruction1, instruction2, instruction3);
        
        newStatusBox.getChildren().addAll(newStatusLabel, statusBox);
        
        mainContainer.getChildren().addAll(
            currentSection, currentStatusBox,
            newSection, newStatusBox,
            instructionsBox
        );
        
        return mainContainer;
    }
    
    private static GridPane createDetailsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #808080; -fx-border-width: 1;");
        
        // Setup column constraints
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
        
        return grid;
    }
    
    private static void addDetailRow(GridPane grid, int row, String label1, String value1,
                                   String label2, String value2) {
        grid.add(createStyledLabel(label1), 0, row);
        grid.add(createStyledValue(value1), 1, row);
        grid.add(createStyledLabel(label2), 2, row);
        grid.add(createStyledValue(value2), 3, row);
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
    
    // PRIVATE HELPER METHODS
    private static String getBookingStatusColor(String status) {
        if (status == null) return "#2c3e50";
        
        switch (status.toLowerCase()) {
            case "confirmed":
                return "#27ae60"; // Green
            case "cancelled":
                return "#e74c3c"; // Red
            case "pending":
                return "#f39c12"; // Orange
            case "completed":
                return "#3498db"; // Blue
            case "payment_failed":
                return "#c0392b"; // Dark red
            default:
                return "#2c3e50"; // Dark gray
        }
    }
}