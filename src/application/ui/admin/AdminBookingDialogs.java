package application.ui.admin;

import application.model.Booking;
import application.service.AdminBookingService;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.util.function.BiConsumer;

public class AdminBookingDialogs {
    
    public interface AlertCallback extends BiConsumer<String, String> {}
    
    public static void showBookingDetails(Booking booking, AlertCallback alertCallback) {
        try {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Booking Details");
            dialog.setHeaderText("Booking Information - " + booking.getBookingReference());

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

            String headerStyle = "-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;";
            String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #34495e;";
            String valueStyle = "-fx-text-fill: #2c3e50; -fx-font-size: 14px;";

            int currentRow = 0;

            // Booking Information
            Label bookingHeader = new Label("📋 Booking Information");
            bookingHeader.setStyle(headerStyle);
            grid.add(bookingHeader, 0, currentRow++, 4, 1);

            grid.add(new Label("Booking ID:"), 0, currentRow);
            grid.add(new Label(String.valueOf(booking.getId())), 1, currentRow);
            grid.add(new Label("Reference:"), 2, currentRow);
            grid.add(new Label(booking.getBookingReference()), 3, currentRow++);

            grid.add(new Label("Status:"), 0, currentRow);
            Label statusLabel = new Label(booking.getStatus().toUpperCase());
            statusLabel.setStyle(valueStyle + " -fx-text-fill: " + getBookingStatusColor(booking.getStatus()) + "; -fx-font-weight: bold;");
            grid.add(statusLabel, 1, currentRow);
            grid.add(new Label("Seat Number:"), 2, currentRow);
            grid.add(new Label(booking.getSeatNumber()), 3, currentRow++);

            // Customer Information
            currentRow++;
            Label customerHeader = new Label("👤 Customer Information");
            customerHeader.setStyle(headerStyle);
            grid.add(customerHeader, 0, currentRow++, 4, 1);

            grid.add(new Label("Name:"), 0, currentRow);
            grid.add(new Label(booking.getCustomerName()), 1, currentRow);
            grid.add(new Label("Email:"), 2, currentRow);
            grid.add(new Label(booking.getCustomerEmail() != null ? booking.getCustomerEmail() : "N/A"), 3, currentRow++);

            // Flight Information
            currentRow++;
            Label flightHeader = new Label("✈ Flight Information");
            flightHeader.setStyle(headerStyle);
            grid.add(flightHeader, 0, currentRow++, 4, 1);

            grid.add(new Label("Flight:"), 0, currentRow);
            grid.add(new Label(booking.getFlightInfo()), 1, currentRow, 3, 1);
            currentRow++;

            // Payment Information
            currentRow++;
            Label paymentHeader = new Label("💳 Payment Information");
            paymentHeader.setStyle(headerStyle);
            grid.add(paymentHeader, 0, currentRow++, 4, 1);

            grid.add(new Label("Amount:"), 0, currentRow);
            grid.add(new Label(booking.getFormattedAmount()), 1, currentRow);
            grid.add(new Label("Payment Status:"), 2, currentRow);
            grid.add(new Label(booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "N/A"), 3, currentRow++);

            grid.add(new Label("Payment Method:"), 0, currentRow);
            grid.add(new Label(booking.getPaymentMethod() != null ? booking.getPaymentMethod() : "N/A"), 1, currentRow);
            grid.add(new Label("Booking Date:"), 2, currentRow);
            grid.add(new Label(booking.getFormattedBookingDate()), 3, currentRow++);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.getDialogPane().setPrefSize(700, 400);
            dialog.getDialogPane().setStyle("-fx-background-color: #f8f9fa;");

            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("Error showing booking details: " + e.getMessage());
            alertCallback.accept("Error", "Unable to display booking details: " + e.getMessage());
        }
    }
    
    public static void changeBookingStatus(Booking booking, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Change Booking Status");
            dialog.setHeaderText("Change status for booking: " + booking.getBookingReference());

            VBox content = new VBox(15);
            content.setPadding(new Insets(20));

            Label currentStatusLabel = new Label("Current Status: " + booking.getStatus().toUpperCase());
            currentStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            ComboBox<String> statusBox = new ComboBox<>();
            statusBox.getItems().addAll("pending", "confirmed", "cancelled", "completed", "payment_failed");
            statusBox.setValue(booking.getStatus());
            statusBox.setPrefWidth(200);

            Label noteLabel = new Label("Note: Changing to 'confirmed' will also mark payment as 'paid'");
            noteLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #6c757d;");

            content.getChildren().addAll(currentStatusLabel, new Label("New Status:"), statusBox, noteLabel);

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return statusBox.getValue();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(newStatus -> {
                if (!newStatus.equals(booking.getStatus())) {
                    if (AdminBookingService.updateBookingStatus(booking.getId(), newStatus)) {
                        alertCallback.accept("Success", "Booking status updated successfully!");
                        refreshCallback.run();
                    } else {
                        alertCallback.accept("Error", "Failed to update booking status.");
                    }
                }
            });

        } catch (Exception e) {
            alertCallback.accept("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    public static void deleteBooking(Booking booking, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Booking");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Delete booking: " + booking.getBookingReference()
                    + "?\n\nThis action cannot be undone and will also delete associated transactions.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (AdminBookingService.deleteBooking(booking.getId())) {
                        alertCallback.accept("Success", "Booking deleted successfully!");
                        refreshCallback.run();
                    } else {
                        alertCallback.accept("Error", "Failed to delete booking.");
                    }
                }
            });

        } catch (Exception e) {
            alertCallback.accept("Error", "Error deleting booking: " + e.getMessage());
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