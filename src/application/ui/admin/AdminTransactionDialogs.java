package application.ui.admin;

import application.model.Transaction;
import application.service.TransactionService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.BiConsumer;

public class AdminTransactionDialogs {
    
    
  
    public static void showTransactionDetails(Transaction transaction, BiConsumer<String, String> alertCallback) {
        try {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Transaction Details");
            dialog.setHeaderText("Transaction #" + transaction.getTransactionId());
            dialog.setResizable(true);

            // Create a comprehensive details view
            VBox mainContent = new VBox(15);
            mainContent.setPadding(new Insets(20));
            mainContent.setStyle("-fx-background-color: #f8f9fa;");

            // Basic Info Section
            VBox basicInfo = createDetailSection("Basic Information", new String[][] {
                    { "Transaction ID:", String.valueOf(transaction.getTransactionId()) },
                    { "Transaction Reference:",
                            transaction.getDescription() != null ? transaction.getDescription() : "N/A" },
                    { "Transaction Type:",
                            transaction.getTransactionType() != null ? transaction.getTransactionType() : "N/A" },
                    { "Status:", transaction.getStatus() != null ? transaction.getStatus().toUpperCase() : "UNKNOWN" }
            });

            // Customer & Booking Info Section
            VBox customerInfo = createDetailSection("Customer & Booking Information", new String[][] {
                    { "Customer Name:", transaction.getUserName() != null ? transaction.getUserName() : "Unknown" },
                    { "Booking ID:", String.valueOf(transaction.getBookingId()) },
                    { "Booking Reference:",
                            transaction.getBookingReference() != null ? transaction.getBookingReference() : "N/A" }
            });

            // Payment Info Section
            VBox paymentInfo = createDetailSection("Payment Information", new String[][] {
                    { "Payment Method:",
                            transaction.getPaymentMethod() != null ? transaction.getPaymentMethod() : "N/A" },
                    { "Payment Provider:",
                            transaction.getPaymentProvider() != null ? transaction.getPaymentProvider() : "N/A" },
                    { "Base Amount:", transaction.getFormattedAmount() },
                    { "Processing Fee:", transaction.getFormattedProcessingFee() },
                    { "Total Amount:", transaction.getFormattedTotalAmount() },
                    { "Payment Date:", transaction.getFormattedDateTime() }
            });

            // Timeline Section
            VBox timelineInfo = createDetailSection("Timeline", new String[][] {
                    { "Created:", transaction.getFormattedCreatedAt() },
                    { "Last Updated:", transaction.getFormattedUpdatedAt() }
            });

            mainContent.getChildren().addAll(basicInfo, customerInfo, paymentInfo, timelineInfo);

            // Wrap in ScrollPane for long content
            ScrollPane scrollPane = new ScrollPane(mainContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(500, 400);

            dialog.getDialogPane().setContent(scrollPane);
            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("Error showing transaction details: " + e.getMessage());
            if (alertCallback != null) {
                alertCallback.accept("Error", "Failed to show transaction details: " + e.getMessage());
            }
        }
    }

    private static VBox createDetailSection(String title, String[][] details) {
        VBox section = new VBox(8);
        
        // Section title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Details grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 5;");
        
        for (int i = 0; i < details.length; i++) {
            Label keyLabel = new Label(details[i][0]);
            keyLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #495057;");
            
            Label valueLabel = new Label(details[i][1]);
            valueLabel.setStyle("-fx-text-fill: #212529;");
            
            grid.add(keyLabel, 0, i);
            grid.add(valueLabel, 1, i);
        }
        
        section.getChildren().addAll(titleLabel, grid);
        return section;
    }
    
    public static void changeTransactionStatus(Transaction transaction, BiConsumer<String, String> alertCallback, Runnable onSuccess) {
        try {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("pending", "completed", "failed", "cancelled", "refunded");
            dialog.setTitle("Change Transaction Status");
            dialog.setHeaderText("Transaction #" + transaction.getTransactionId());
            dialog.setContentText("Current Status: " + transaction.getStatus().toUpperCase() + 
                                "\n\n⚠️ Note: This will also update the booking status automatically." +
                                "\n\nSelect new status:");
            
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(status -> {
                try {
                    if (TransactionService.updateTransactionStatus(transaction.getTransactionId(), status)) {
                        String bookingStatus = mapPaymentToBookingStatus(status);
                        if (alertCallback != null) {
                            alertCallback.accept("Success", 
                                "✅ Transaction status updated to: " + status.toUpperCase() + 
                                "\n📋 Booking status updated to: " + bookingStatus.toUpperCase());
                        }
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    } else {
                        if (alertCallback != null) {
                            alertCallback.accept("Error", "Failed to update transaction status.");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error updating transaction status: " + e.getMessage());
                    if (alertCallback != null) {
                        alertCallback.accept("Error", "Error updating status: " + e.getMessage());
                    }
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error showing status change dialog: " + e.getMessage());
            if (alertCallback != null) {
                alertCallback.accept("Error", "Failed to show status change dialog: " + e.getMessage());
            }
        }
    }
    
    private static String mapPaymentToBookingStatus(String paymentStatus) {
        switch (paymentStatus.toLowerCase()) {
            case "completed":
                return "confirmed";
            case "failed":
            case "cancelled":
            case "refunded":
                return "cancelled";
            default:
                return "pending";
        }
    }
    
}