package application.controller.client;

import application.model.Flight;
import application.controller.client.BookingFormController.BookingFormData;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentController {
    
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
    private String selectedPaymentMethod = "credit_card";
    
    public interface PaymentEventHandler {
        void onPaymentProcessed(PaymentData paymentData);
        void onShowAlert(String title, String message);
    }
    
    private final PaymentEventHandler eventHandler;
    
    public PaymentController(PaymentEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    public VBox createPaymentForm(Flight flight, BookingFormData formData) {
        VBox container = new VBox(15);
        container.setStyle("-fx-padding: 10;");
        
        // Booking summary
        VBox bookingSummary = createBookingSummary(flight, formData);
        container.getChildren().add(bookingSummary);

        // Payment method selection
        VBox paymentMethods = createPaymentMethodsSection();
        container.getChildren().add(paymentMethods);

        // Payment summary
        VBox paymentSummary = createPaymentSummary(flight);
        container.getChildren().add(paymentSummary);
        
        return container;
    }
    
    public void processPayment(Flight flight, BookingFormData formData) {
        double amount = flight.getPrice();
        double processingFee = calculateProcessingFee(amount, selectedPaymentMethod);
        double totalAmount = amount + processingFee;
        
        PaymentData paymentData = new PaymentData(
            formData, selectedPaymentMethod, getPaymentProvider(selectedPaymentMethod), totalAmount
        );
        
        eventHandler.onPaymentProcessed(paymentData);
    }
    
    private VBox createBookingSummary(Flight flight, BookingFormData formData) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                     "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");

        Label titleLabel = new Label("Booking Summary");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        VBox detailsBox = new VBox(15);
        detailsBox.getChildren().addAll(
                createDetailRow("Flight", flight.getFlightNo()),
                createDetailRow("Route", flight.getOrigin() + " → " + flight.getDestination()),
                createDetailRow("Passenger", formData.getFirstName() + " " + formData.getLastName()),
                createDetailRow("Date", flight.getDeparture().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))),
                createDetailRow("Duration", flight.getDuration()));

        VBox totalBox = new VBox(5);
        totalBox.setStyle("-fx-background-color: #FFF3E0; -fx-padding: 12; -fx-background-radius: 10;");

        Label totalLabel = new Label("Total Amount");
        totalLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label amountLabel = new Label(currencyFormat.format(flight.getPrice()));
        amountLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FF9800;");

        totalBox.getChildren().addAll(totalLabel, amountLabel);

        card.getChildren().addAll(titleLabel, detailsBox, totalBox);
        return card;
    }
    
    private VBox createPaymentMethodsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                        "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");
        
        Label titleLabel = new Label("Select Payment Method");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Toggle group for payment methods
        ToggleGroup paymentGroup = new ToggleGroup();
        
        VBox methodsContainer = new VBox(12);
        
        RadioButton creditCard = createPaymentOption("💳 Credit/Debit Card", "credit_card", paymentGroup, true);
        RadioButton gcash = createPaymentOption("🔵 GCash", "gcash", paymentGroup, false);
        RadioButton maya = createPaymentOption("🟢 Maya (PayMaya)", "maya", paymentGroup, false);
        RadioButton paypal = createPaymentOption("🅿️ PayPal", "paypal", paymentGroup, false);
        
        methodsContainer.getChildren().addAll(creditCard, gcash, maya, paypal);
        
        section.getChildren().addAll(titleLabel, methodsContainer);
        return section;
    }
    
    private VBox createPaymentSummary(Flight flight) {
        VBox detailsContainer = new VBox(15);
        detailsContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        Label titleLabel = new Label("Payment Summary");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Payment breakdown
        VBox summaryBox = new VBox(10);
        summaryBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");

        double flightPrice = flight.getPrice();
        double processingFee = calculateProcessingFee(flightPrice, selectedPaymentMethod);
        double totalAmount = flightPrice + processingFee;

        HBox flightPriceRow = createPriceSummaryRow("Flight Price", flightPrice);
        HBox processingFeeRow = createPriceSummaryRow("Processing Fee", processingFee);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #ddd;");

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        totalRow.setStyle("-fx-background-color: #E8F5E8; -fx-padding: 10; -fx-background-radius: 8;");

        Label totalLabel = new Label("Total Amount");
        totalLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label totalValue = new Label(currencyFormat.format(totalAmount));
        totalValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        totalRow.getChildren().addAll(totalLabel, spacer, totalValue);

        summaryBox.getChildren().addAll(flightPriceRow, processingFeeRow, separator, totalRow);

        detailsContainer.getChildren().addAll(titleLabel, summaryBox);
        return detailsContainer;
    }
    
    private RadioButton createPaymentOption(String text, String methodId, ToggleGroup group, boolean selected) {
        RadioButton radio = new RadioButton(text);
        radio.setToggleGroup(group);
        radio.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
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
    
    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        labelText.setPrefWidth(80);
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
        
        row.getChildren().addAll(labelText, valueText);
        return row;
    }
    
    private HBox createPriceSummaryRow(String label, double amount) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label valueText = new Label(currencyFormat.format(amount));
        valueText.setStyle("-fx-font-size: 13px; -fx-text-fill: #333; -fx-font-weight: bold;");
        
        row.getChildren().addAll(labelText, spacer, valueText);
        return row;
    }
    
    private double calculateProcessingFee(double amount, String paymentMethod) {
        switch (paymentMethod) {
            case "credit_card": return amount * 0.025; // 2.5%
            case "gcash":
            case "maya": return amount * 0.01; // 1%
            case "paypal": return amount * 0.034; // 3.4%
            default: return amount * 0.02; // 2%
        }
    }
    
    private String getPaymentProvider(String paymentMethod) {
        switch (paymentMethod) {
            case "credit_card": return "Visa/Mastercard";
            case "gcash": return "GCash";
            case "maya": return "Maya";
            case "paypal": return "PayPal";
            default: return "Unknown";
        }
    }
    
    // Data transfer object
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
        
        // Getters
        public BookingFormData getFormData() { return formData; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getPaymentProvider() { return paymentProvider; }
        public double getTotalAmount() { return totalAmount; }
    }
}