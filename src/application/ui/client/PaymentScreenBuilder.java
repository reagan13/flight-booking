package application.ui.client;

import application.model.Flight;
import application.ui.client.BookingFormScreenBuilder.BookingFormData;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentScreenBuilder {
    
    private final PaymentEventHandler eventHandler;
    private final NumberFormat currencyFormat;
    private String selectedPaymentMethod = "credit_card";
    private ToggleGroup paymentGroup;

    private VBox paymentSummaryDetails;
    private Flight currentFlight;
    
    public PaymentScreenBuilder(PaymentEventHandler eventHandler) {
        this.eventHandler = eventHandler;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
    }
    
    /**
     * Create payment form - matches naming convention
     */
    public VBox createPaymentForm(Flight flight, BookingFormData formData) {
        VBox container = new VBox(0);
        container.setStyle("-fx-background-color: #f5f5f5;");
        container.setFillWidth(true);
        container.setMaxWidth(500); // Mobile width constraint
        
        // Header section with back button (same as FlightDetailsBuilder)
        VBox headerSection = createHeaderSection();
        
        // Main content section
        VBox contentSection = createContentSection(flight, formData);
        
        // Bottom action section  
        VBox actionSection = createActionSection(flight, formData);
        
        container.getChildren().addAll(headerSection, contentSection, actionSection);
        return container;
    }
    
    /**
     * Process payment - PUBLIC method for HomeController
     */
    public void processPayment(Flight flight, BookingFormData formData) {
        double amount = flight.getPrice();
        double processingFee = calculateProcessingFee(amount, selectedPaymentMethod);
        double totalAmount = amount + processingFee;

        PaymentData paymentData = new PaymentData(
            formData, 
            selectedPaymentMethod, 
            getPaymentMethodName(selectedPaymentMethod), 
            totalAmount
        );

        eventHandler.onPaymentProcessed(paymentData);
        eventHandler.onShowConfirmation(paymentData);
    }
    
    private VBox createHeaderSection() {
        VBox headerSection = new VBox();
        headerSection.setStyle("-fx-background-color: #d0d0d0; -fx-border-color: #808080; -fx-border-width: 0 0 1 0;");
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setStyle("-fx-padding: 10 15;");
        
        Button backButton = new Button("Go Back");
        backButton.setStyle(
            "-fx-background-color: linear-gradient(#f0f0f0, #d0d0d0); " +
            "-fx-text-fill: #2c2c2c; -fx-font-size: 12px; -fx-padding: 6 12; " +
            "-fx-border-color: #808080; -fx-border-width: 1; -fx-cursor: hand;"
        );
        backButton.setOnAction(e -> eventHandler.onBackToBooking());
        
        // Hover effects (same as FlightDetailsBuilder)
        backButton.setOnMouseEntered(e -> 
            backButton.setStyle(
                "-fx-background-color: linear-gradient(#e0e0e0, #c0c0c0); " +
                "-fx-text-fill: #2c2c2c; -fx-font-size: 12px; -fx-padding: 6 12; " +
                "-fx-border-color: #606060; -fx-border-width: 1; -fx-cursor: hand;"
            )
        );
        
        backButton.setOnMouseExited(e -> 
            backButton.setStyle(
                "-fx-background-color: linear-gradient(#f0f0f0, #d0d0d0); " +
                "-fx-text-fill: #2c2c2c; -fx-font-size: 12px; -fx-padding: 6 12; " +
                "-fx-border-color: #808080; -fx-border-width: 1; -fx-cursor: hand;"
            )
        );
        
        headerBox.getChildren().add(backButton);
        headerSection.getChildren().add(headerBox);
        
        return headerSection;
    }
    
    private VBox createContentSection(Flight flight, BookingFormData formData) {
        VBox contentSection = new VBox(15);
        contentSection.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");
        VBox.setVgrow(contentSection, Priority.ALWAYS);
        
        // Flight summary card
        VBox flightCard = createFlightSummaryCard(flight, formData);
        
        // Payment methods card
        VBox methodsCard = createPaymentMethodsCard();
        
        // Payment summary card
        VBox summaryCard = createPaymentSummaryCard(flight);
        
        contentSection.getChildren().addAll(flightCard, methodsCard, summaryCard);
        return contentSection;
    }
    
    private VBox createFlightSummaryCard(Flight flight, BookingFormData formData) {
        VBox card = new VBox(15);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        // Flight header
        Label flightTitle = new Label("Flight Summary");
        flightTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        // Route section (same style as FlightDetailsBuilder)
        HBox routeSection = new HBox();
        routeSection.setAlignment(Pos.CENTER);
        routeSection.setSpacing(15);
        
        VBox originBox = createCompactAirportBox(flight.getOrigin(), "From");
        Label arrowLabel = new Label("✈️");
        arrowLabel.setStyle("-fx-font-size: 20px;");
        VBox destBox = createCompactAirportBox(flight.getDestination(), "To");
        
        routeSection.getChildren().addAll(originBox, arrowLabel, destBox);
        
        // Flight details
        VBox detailsGrid = new VBox(8);
        detailsGrid.getChildren().addAll(
            createCompactDetailRow("✈️ Flight", flight.getFlightNo()),
            createCompactDetailRow("👤 Passenger", formData.getFirstName() + " " + formData.getLastName()),
            createCompactDetailRow("📅 Date", flight.getDeparture().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))),
            createCompactDetailRow("⏱️ Duration", flight.getDuration())
        );
        
        card.getChildren().addAll(flightTitle, routeSection, detailsGrid);
        return card;
    }
    
    private VBox createPaymentMethodsCard() {
        VBox card = new VBox(12);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        Label methodsTitle = new Label("Payment Method");
        methodsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        // Initialize toggle group
        paymentGroup = new ToggleGroup();
        
        VBox methodsList = new VBox(8);
        
        // Payment methods (same style as BookingsScreenBuilder detail rows)
        RadioButton creditCard = createPaymentOption("💳 Credit/Debit Card", "credit_card", true);
        RadioButton gcash = createPaymentOption("🔵 GCash", "gcash", false);
        RadioButton maya = createPaymentOption("🟢 Maya", "maya", false);
        RadioButton paypal = createPaymentOption("🅿️ PayPal", "paypal", false);
        
        methodsList.getChildren().addAll(creditCard, gcash, maya, paypal);
        card.getChildren().addAll(methodsTitle, methodsList);
        
        return card;
    }
    
    private RadioButton createPaymentOption(String text, String methodId, boolean selected) {
        RadioButton radio = new RadioButton(text);
        radio.setToggleGroup(paymentGroup);
        radio.setStyle(
            "-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 6; " +
            "-fx-font-size: 12px; -fx-text-fill: #2c2c2c;"
        );
        radio.setSelected(selected);

        if (selected) {
            selectedPaymentMethod = methodId;
        }

        radio.setOnAction(e -> {
            selectedPaymentMethod = methodId;
            System.out.println("Selected payment method: " + methodId);
        });

        return radio;
    }
    
    private VBox createPaymentSummaryCard(Flight flight) {
        // Store flight reference for updates
        this.currentFlight = flight;

        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; " +
                        "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 8; " +
                        "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(76,175,80,0.2), 4, 0, 0, 2);");

        Label summaryTitle = new Label("Payment Summary");
        summaryTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");

        // CREATE CONTAINER THAT CAN BE UPDATED
        paymentSummaryDetails = new VBox(8);
        updatePaymentSummary(); // Initial population

        card.getChildren().addAll(summaryTitle, paymentSummaryDetails);
        return card;
    }
    
    private void updatePaymentSummary() {
        if (paymentSummaryDetails == null || currentFlight == null) return;
        
        // Clear existing content
        paymentSummaryDetails.getChildren().clear();
        
        // Recalculate with current payment method
        double flightPrice = currentFlight.getPrice();
        double processingFee = calculateProcessingFee(flightPrice, selectedPaymentMethod);
        double totalAmount = flightPrice + processingFee;
        
        // Add updated content
        paymentSummaryDetails.getChildren().addAll(
            createCompactDetailRow("Flight Price", currencyFormat.format(flightPrice)),
            createCompactDetailRow("Processing Fee (" + getPaymentMethodName(selectedPaymentMethod) + ")", 
                                  currencyFormat.format(processingFee)),
            new Separator(),
            createTotalRow("Total Amount", totalAmount)
        );
    }
    
    private HBox createTotalRow(String label, double amount) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #E8F5E8; -fx-padding: 8; -fx-background-radius: 6;");
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 12px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");
        labelText.setPrefWidth(100);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label valueText = new Label(currencyFormat.format(amount));
        valueText.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        
        row.getChildren().addAll(labelText, spacer, valueText);
        return row;
    }
    
    private VBox createActionSection(Flight flight, BookingFormData formData) {
        VBox actionSection = new VBox(10);
        actionSection.setStyle(
            "-fx-background-color: #f0f0f0; -fx-border-color: #808080; " +
            "-fx-border-width: 1 0 0 0; -fx-padding: 15;"
        );
        
        // Security notice
        HBox securityNotice = new HBox(8);
        securityNotice.setAlignment(Pos.CENTER);
        securityNotice.setStyle(
            "-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 6; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 6;"
        );
        
        Label lockIcon = new Label("🔒");
        Label securityText = new Label("Your payment is secured with SSL encryption");
        securityText.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        securityNotice.getChildren().addAll(lockIcon, securityText);
        
        Button payButton = new Button("Complete Payment");
        payButton.setStyle(
            "-fx-background-color: linear-gradient(#4CAF50, #45a049); " +
            "-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 25; " +
            "-fx-border-color: #388E3C; -fx-border-width: 1; -fx-background-radius: 6; " +
            "-fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"
        );
        payButton.setMaxWidth(Double.MAX_VALUE);
        payButton.setOnAction(e -> processPayment(flight, formData));
        
        // Hover effects (same as FlightDetailsBuilder)
        payButton.setOnMouseEntered(e ->
            payButton.setStyle(
                "-fx-background-color: linear-gradient(#45a049, #3d8b40); " +
                "-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 25; " +
                "-fx-border-color: #2E7D32; -fx-border-width: 1; -fx-background-radius: 6; " +
                "-fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"
            )
        );
        
        payButton.setOnMouseExited(e ->
            payButton.setStyle(
                "-fx-background-color: linear-gradient(#4CAF50, #45a049); " +
                "-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12 25; " +
                "-fx-border-color: #388E3C; -fx-border-width: 1; -fx-background-radius: 6; " +
                "-fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"
            )
        );
        
        actionSection.getChildren().addAll(securityNotice, payButton);
        return actionSection;
    }
    
    // Helper methods (same style as FlightDetailsBuilder)
    
    private VBox createCompactAirportBox(String code, String label) {
        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);
        
        Label codeLabel = new Label(code);
        codeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        Label typeLabel = new Label(label);
        typeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666; -fx-font-weight: bold;");
        
        box.getChildren().addAll(codeLabel, typeLabel);
        return box;
    }
    
    private HBox createCompactDetailRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 6;");
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        labelText.setPrefWidth(100);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c2c2c; -fx-font-weight: bold;");
        
        row.getChildren().addAll(labelText, spacer, valueText);
        return row;
    }
    
    private double calculateProcessingFee(double amount, String paymentMethod) {
        switch (paymentMethod) {
            case "credit_card": return amount * 0.035; // 3.5% - Higher for credit cards
            case "gcash": return amount * 0.015; // 1.5% - E-wallet fee
            case "maya": return amount * 0.012; // 1.2% - Competitive e-wallet rate
            case "paypal": return amount * 0.044; // 4.4% - PayPal's international rate
            default: return amount * 0.025; // 2.5% - Default fallback
        }
    }
    
    private String getPaymentMethodName(String paymentMethod) {
        switch (paymentMethod) {
            case "credit_card": return "Credit Card";
            case "gcash": return "GCash";
            case "maya": return "Maya";
            case "paypal": return "PayPal";
            default: return "Unknown";
        }
    }
    
    // Data Classes
    
    public static class PaymentData {
        private final BookingFormData formData;
        private final String paymentMethod;
        private final String paymentProvider;
        private final double totalAmount;
        
        public PaymentData(BookingFormData formData, String paymentMethod, String paymentProvider, double totalAmount) {
            this.formData = formData;
            this.paymentMethod = paymentMethod;
            this.paymentProvider = paymentProvider;
            this.totalAmount = totalAmount;
        }
        
        public BookingFormData getFormData() { return formData; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getPaymentProvider() { return paymentProvider; }
        public double getTotalAmount() { return totalAmount; }
    }
    
    /**
     * Interface for handling payment events
     */
    public interface PaymentEventHandler {
        void onBackToBooking();
        void onPaymentProcessed(PaymentData paymentData);

        void onShowAlert(String title, String message);
        void onShowConfirmation(PaymentData paymentData); 
    }
}