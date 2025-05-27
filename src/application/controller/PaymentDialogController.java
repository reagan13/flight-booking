package application.controller;

import application.model.Flight;
import application.model.UserSession;
import application.service.FlightService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.NumberFormat;

public class PaymentDialogController {
    
    private FlightService flightService;
    private NumberFormat currencyFormat;
    
    public PaymentDialogController(FlightService flightService, NumberFormat currencyFormat) {
        this.flightService = flightService;
        this.currencyFormat = currencyFormat;
    }
    
    public void showPaymentDialog(Flight flight, String firstName, String lastName, 
                                String email, String phone, double totalPrice, String bookingRef) {
        System.out.println("=== DEBUG: Starting showPaymentDialog ===");
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Payment Information");
        dialog.setHeaderText("Complete Payment for " + flight.getAirlineName() + " " + flight.getFlightNo());

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // === BOOKING SUMMARY SECTION ===
        addBookingSummary(mainContent, flight, firstName, lastName, totalPrice);
        
        // === PAYMENT METHOD SECTION ===
        ToggleGroup paymentMethodGroup = addPaymentMethodSection(mainContent);
        
        // === PAYMENT DETAILS SECTION ===
        VBox paymentDetailsBox = addPaymentDetailsSection(mainContent, paymentMethodGroup);

        dialog.getDialogPane().setContent(mainContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Style buttons
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Pay Now");
        okButton.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-padding: 10 20;");

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String selectedPaymentMethod = getSelectedPaymentMethod(paymentMethodGroup);
                processPayment(flight, firstName, lastName, email, phone, totalPrice, bookingRef, selectedPaymentMethod);
            }
        });
    }
    
    private void addBookingSummary(VBox mainContent, Flight flight, String firstName, String lastName, double totalPrice) {
        Label summaryLabel = new Label("Booking Summary");
        summaryLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        summaryLabel.setStyle("-fx-text-fill: #1a237e;");

        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(15);
        summaryGrid.setVgap(8);
        summaryGrid.setPadding(new Insets(15));
        summaryGrid.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;");

        int row = 0;
        summaryGrid.add(new Label("Route:"), 0, row);
        Label routeLabel = new Label(flight.getOrigin() + " → " + flight.getDestination());
        routeLabel.setStyle("-fx-font-weight: bold;");
        summaryGrid.add(routeLabel, 1, row++);

        summaryGrid.add(new Label("Passenger:"), 0, row);
        Label passengerLabel = new Label(firstName + " " + lastName);
        passengerLabel.setStyle("-fx-font-weight: bold;");
        summaryGrid.add(passengerLabel, 1, row++);

        summaryGrid.add(new Label("Flight:"), 0, row);
        summaryGrid.add(new Label(flight.getFlightNo()), 1, row++);

        summaryGrid.add(new Separator(), 0, row++, 2, 1);

        summaryGrid.add(new Label("Total Amount:"), 0, row);
        Label amountLabel = new Label(currencyFormat.format(totalPrice));
        amountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1a237e;");
        summaryGrid.add(amountLabel, 1, row++);

        mainContent.getChildren().addAll(summaryLabel, summaryGrid);
    }
    
    private ToggleGroup addPaymentMethodSection(VBox mainContent) {
        Label paymentLabel = new Label("Payment Method");
        paymentLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        paymentLabel.setStyle("-fx-text-fill: #1a237e;");

        VBox paymentMethodBox = new VBox(12);
        paymentMethodBox.setPadding(new Insets(15));
        paymentMethodBox.setStyle("-fx-background-color: #fff3e0; -fx-background-radius: 5;");

        ToggleGroup paymentMethodGroup = new ToggleGroup();

        // === CREDIT/DEBIT CARDS ===
        Label cardLabel = new Label("Cards");
        cardLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        cardLabel.setStyle("-fx-text-fill: #333;");

        HBox cardBox = new HBox(15);
        RadioButton creditCardRadio = new RadioButton("Credit/Debit Card");
        creditCardRadio.setToggleGroup(paymentMethodGroup);
        creditCardRadio.setSelected(true);
        creditCardRadio.setUserData("credit_card");

        Label cardIcons = new Label("💳 Visa • Mastercard • JCB");
        cardIcons.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        cardBox.getChildren().addAll(creditCardRadio, cardIcons);

        // === E-WALLETS ===
        Label ewalletLabel = new Label("E-Wallets");
        ewalletLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        ewalletLabel.setStyle("-fx-text-fill: #333;");

        // GCash
        HBox gcashBox = new HBox(15);
        RadioButton gcashRadio = new RadioButton("GCash");
        gcashRadio.setToggleGroup(paymentMethodGroup);
        gcashRadio.setUserData("gcash");

        Label gcashInfo = new Label("🔵 Fast & Secure • 2.5% processing fee");
        gcashInfo.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        gcashBox.getChildren().addAll(gcashRadio, gcashInfo);

        // Maya (formerly PayMaya)
        HBox mayaBox = new HBox(15);
        RadioButton mayaRadio = new RadioButton("Maya");
        mayaRadio.setToggleGroup(paymentMethodGroup);
        mayaRadio.setUserData("maya");

        Label mayaInfo = new Label("🟢 Instant Payment • 2.5% processing fee");
        mayaInfo.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        mayaBox.getChildren().addAll(mayaRadio, mayaInfo);

        // PayPal
        HBox paypalBox = new HBox(15);
        RadioButton paypalRadio = new RadioButton("PayPal");
        paypalRadio.setToggleGroup(paymentMethodGroup);
        paypalRadio.setUserData("paypal");

        Label paypalInfo = new Label("🔷 Global Payment • 3.9% + ₱15 fee");
        paypalInfo.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        paypalBox.getChildren().addAll(paypalRadio, paypalInfo);

        paymentMethodBox.getChildren().addAll(
            cardLabel,
            cardBox,
            new Separator(),
            ewalletLabel,
            gcashBox,
            mayaBox,
            paypalBox
        );

        mainContent.getChildren().addAll(new Separator(), paymentLabel, paymentMethodBox);
        return paymentMethodGroup;
    }
    
    private VBox addPaymentDetailsSection(VBox mainContent, ToggleGroup paymentMethodGroup) {
        Label detailsLabel = new Label("Payment Details");
        detailsLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        detailsLabel.setStyle("-fx-text-fill: #1a237e;");

        VBox paymentDetailsBox = new VBox(10);
        paymentDetailsBox.setPadding(new Insets(15));
        paymentDetailsBox.setStyle("-fx-background-color: #e8f5e8; -fx-background-radius: 5;");

        // Default content for credit card
        updatePaymentDetails(paymentDetailsBox, "credit_card");

        // Listen for payment method changes
        paymentMethodGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedMethod = newValue.getUserData().toString();
                updatePaymentDetails(paymentDetailsBox, selectedMethod);
            }
        });

        mainContent.getChildren().addAll(new Separator(), detailsLabel, paymentDetailsBox);
        return paymentDetailsBox;
    }
    
    private void updatePaymentDetails(VBox paymentDetailsBox, String paymentMethod) {
        paymentDetailsBox.getChildren().clear();

        switch (paymentMethod) {
            case "credit_card":
                addCreditCardDetails(paymentDetailsBox);
                break;
            case "gcash":
                addGCashDetails(paymentDetailsBox);
                break;
            case "maya":
                addMayaDetails(paymentDetailsBox);
                break;
            case "paypal":
                addPayPalDetails(paymentDetailsBox);
                break;
        }
    }
    
    private void addCreditCardDetails(VBox container) {
        GridPane cardGrid = new GridPane();
        cardGrid.setHgap(10);
        cardGrid.setVgap(10);

        // Card Number
        cardGrid.add(new Label("Card Number:"), 0, 0);
        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("1234 5678 9012 3456");
        cardGrid.add(cardNumberField, 1, 0);

        // Expiry Date
        cardGrid.add(new Label("Expiry Date:"), 0, 1);
        TextField expiryField = new TextField();
        expiryField.setPromptText("MM/YY");
        expiryField.setPrefWidth(80);
        cardGrid.add(expiryField, 1, 1);

        // CVV
        cardGrid.add(new Label("CVV:"), 0, 2);
        TextField cvvField = new TextField();
        cvvField.setPromptText("123");
        cvvField.setPrefWidth(60);
        cardGrid.add(cvvField, 1, 2);

        // Cardholder Name
        cardGrid.add(new Label("Cardholder Name:"), 0, 3);
        TextField nameField = new TextField();
        nameField.setPromptText("Full name as on card");
        cardGrid.add(nameField, 1, 3);

        container.getChildren().add(cardGrid);
    }
    
    private void addGCashDetails(VBox container) {
        Label instructionLabel = new Label("GCash Payment Instructions:");
        instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        VBox instructions = new VBox(8);
        instructions.getChildren().addAll(
            new Label("1. You will be redirected to GCash app or website"),
            new Label("2. Log in with your GCash mobile number and MPIN"),
            new Label("3. Review payment details and confirm"),
            new Label("4. Wait for payment confirmation")
        );

        TextField mobileField = new TextField();
        mobileField.setPromptText("GCash Mobile Number (e.g., 09123456789)");

        Label feeLabel = new Label("Processing Fee: 2.5% will be added to total amount");
        feeLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

        container.getChildren().addAll(
            instructionLabel,
            instructions,
            new Separator(),
            new Label("Mobile Number:"),
            mobileField,
            feeLabel
        );
    }
    
    private void addMayaDetails(VBox container) {
        Label instructionLabel = new Label("Maya Payment Instructions:");
        instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        VBox instructions = new VBox(8);
        instructions.getChildren().addAll(
            new Label("1. You will be redirected to Maya payment portal"),
            new Label("2. Choose payment method (Maya Wallet, Card, or Bank)"),
            new Label("3. Enter your Maya credentials or payment details"),
            new Label("4. Authorize payment with OTP or biometrics")
        );

        TextField mobileField = new TextField();
        mobileField.setPromptText("Maya Mobile Number (e.g., 09123456789)");

        Label feeLabel = new Label("Processing Fee: 2.5% will be added to total amount");
        feeLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

        container.getChildren().addAll(
            instructionLabel,
            instructions,
            new Separator(),
            new Label("Mobile Number:"),
            mobileField,
            feeLabel
        );
    }
    
    private void addPayPalDetails(VBox container) {
        Label instructionLabel = new Label("PayPal Payment Instructions:");
        instructionLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        VBox instructions = new VBox(8);
        instructions.getChildren().addAll(
            new Label("1. You will be redirected to PayPal secure checkout"),
            new Label("2. Log in to your PayPal account"),
            new Label("3. Choose funding source (PayPal balance, card, or bank)"),
            new Label("4. Review and confirm payment")
        );

        TextField emailField = new TextField();
        emailField.setPromptText("PayPal Email Address");

        Label feeLabel = new Label("Processing Fee: 3.9% + ₱15 will be added to total amount");
        feeLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

        Label noteLabel = new Label("Note: You can also pay as guest with credit/debit card through PayPal");
        noteLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-font-size: 12px;");

        container.getChildren().addAll(
            instructionLabel,
            instructions,
            new Separator(),
            new Label("PayPal Email:"),
            emailField,
            feeLabel,
            noteLabel
        );
    }
    
    private String getSelectedPaymentMethod(ToggleGroup paymentMethodGroup) {
        RadioButton selectedRadio = (RadioButton) paymentMethodGroup.getSelectedToggle();
        return selectedRadio != null ? selectedRadio.getUserData().toString() : "credit_card";
    }
    
    private void processPayment(Flight flight, String firstName, String lastName, 
                              String email, String phone, double totalPrice, String bookingRef, String paymentMethod) {
        try {
            System.out.println("=== DEBUG: Processing payment with method: " + paymentMethod + " ===");
            
            int userId = 0;
            if (UserSession.getInstance().isLoggedIn()) {
                userId = UserSession.getInstance().getUserId();
            }

            // Calculate processing fees
            double finalAmount = calculateFinalAmount(totalPrice, paymentMethod);
            String transactionRef = generateTransactionReference(paymentMethod);

            System.out.println("Original amount: " + currencyFormat.format(totalPrice));
            System.out.println("Final amount (with fees): " + currencyFormat.format(finalAmount));
            System.out.println("Payment method: " + paymentMethod);
            System.out.println("Transaction ref: " + transactionRef);

            flightService.createBookingWithPayment(flight, userId, firstName, lastName, email, phone,
                    bookingRef, finalAmount, paymentMethod, transactionRef)
                .thenAccept(success -> {
                    Platform.runLater(() -> {
                        if (success) {
                            showConfirmation(bookingRef, flight, firstName, lastName, paymentMethod, finalAmount);
                        } else {
                            showError("Payment failed. Please try again.");
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        showError("An error occurred: " + ex.getMessage());
                    });
                    return null;
                });

        } catch (Exception e) {
            showError("Payment processing error: " + e.getMessage());
        }
    }
    
    private double calculateFinalAmount(double baseAmount, String paymentMethod) {
        switch (paymentMethod) {
            case "gcash":
            case "maya":
                return baseAmount * 1.025; // 2.5% fee
            case "paypal":
                return (baseAmount * 1.039) + 15; // 3.9% + ₱15 fee
            case "credit_card":
            default:
                return baseAmount; // No additional fee for credit cards
        }
    }
    
    private String generateTransactionReference(String paymentMethod) {
        String prefix;
        switch (paymentMethod) {
            case "gcash":
                prefix = "GC";
                break;
            case "maya":
                prefix = "MY";
                break;
            case "paypal":
                prefix = "PP";
                break;
            case "credit_card":
            default:
                prefix = "CC";
                break;
        }
        return prefix + "-" + System.currentTimeMillis() % 1000000;
    }
    
    private void showConfirmation(String bookingRef, Flight flight, String firstName, String lastName, 
                                String paymentMethod, double finalAmount) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText("Booking Confirmed!");
        
        String paymentMethodDisplay = getPaymentMethodDisplay(paymentMethod);
        
        String content = "Your booking has been confirmed!\n\n" +
                        "Booking Reference: " + bookingRef + "\n" +
                        "Flight: " + flight.getFlightNo() + "\n" +
                        "Passenger: " + firstName + " " + lastName + "\n" +
                        "Payment Method: " + paymentMethodDisplay + "\n" +
                        "Amount Paid: " + currencyFormat.format(finalAmount) + "\n\n" +
                        "A confirmation email will be sent shortly.\n" +
                        "Please save your booking reference for future reference.";
        
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private String getPaymentMethodDisplay(String paymentMethod) {
        switch (paymentMethod) {
            case "gcash":
                return "GCash";
            case "maya":
                return "Maya";
            case "paypal":
                return "PayPal";
            case "credit_card":
            default:
                return "Credit/Debit Card";
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Payment Error");
        alert.setHeaderText("Error Processing Payment");
        alert.setContentText(message);
        alert.showAndWait();
    }
}