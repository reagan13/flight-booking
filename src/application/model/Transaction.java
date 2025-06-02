package application.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    
    private final IntegerProperty transactionId;
    private final IntegerProperty userId;
    private final IntegerProperty bookingId;
    private final StringProperty transactionType;
    private final DoubleProperty amount;
    private final StringProperty status;
    private final StringProperty paymentMethod;
    private final StringProperty description;
    private final ObjectProperty<LocalDateTime> transactionDate;
    private final StringProperty userName;
    private final StringProperty bookingReference;
    
    // Additional fields to match your database schema
    private final StringProperty paymentProvider;
    private final DoubleProperty processingFee;
    private final DoubleProperty totalAmount;
    private final StringProperty gatewayTransactionId;
    private final StringProperty gatewayResponseCode;
    private final ObjectProperty<LocalDateTime> createdAt;
    private final ObjectProperty<LocalDateTime> updatedAt;

    // Default constructor
    public Transaction() {
        this(0, 0, 0, "", 0.0, "", "", "", LocalDateTime.now());
    }
    
    // Full constructor
    public Transaction(int transactionId, int userId, int bookingId, String transactionType, 
                      double amount, String status, String paymentMethod, String description, 
                      LocalDateTime transactionDate) {
        this.transactionId = new SimpleIntegerProperty(transactionId);
        this.userId = new SimpleIntegerProperty(userId);
        this.bookingId = new SimpleIntegerProperty(bookingId);
        this.transactionType = new SimpleStringProperty(transactionType);
        this.amount = new SimpleDoubleProperty(amount);
        this.status = new SimpleStringProperty(status);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
        this.description = new SimpleStringProperty(description);
        this.transactionDate = new SimpleObjectProperty<>(transactionDate);
        this.userName = new SimpleStringProperty("");
        this.bookingReference = new SimpleStringProperty("");
        
        // Initialize additional fields
        this.paymentProvider = new SimpleStringProperty("");
        this.processingFee = new SimpleDoubleProperty(0.0);
        this.totalAmount = new SimpleDoubleProperty(amount); // Default to amount if not set
        this.gatewayTransactionId = new SimpleStringProperty("");
        this.gatewayResponseCode = new SimpleStringProperty("");
        this.createdAt = new SimpleObjectProperty<>(LocalDateTime.now());
        this.updatedAt = new SimpleObjectProperty<>(LocalDateTime.now());
    }
    
    // Property getters
    public IntegerProperty transactionIdProperty() { return transactionId; }
    public IntegerProperty userIdProperty() { return userId; }
    public IntegerProperty bookingIdProperty() { return bookingId; }
    public StringProperty transactionTypeProperty() { return transactionType; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty statusProperty() { return status; }
    public StringProperty paymentMethodProperty() { return paymentMethod; }
    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<LocalDateTime> transactionDateProperty() { return transactionDate; }
    public StringProperty userNameProperty() { return userName; }
    public StringProperty bookingReferenceProperty() { return bookingReference; }
    public StringProperty paymentProviderProperty() { return paymentProvider; }
    public DoubleProperty processingFeeProperty() { return processingFee; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }
    public StringProperty gatewayTransactionIdProperty() { return gatewayTransactionId; }
    public StringProperty gatewayResponseCodeProperty() { return gatewayResponseCode; }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }
    
    // Basic getters
    public int getTransactionId() { return transactionId.get(); }
    public int getUserId() { return userId.get(); }
    public int getBookingId() { return bookingId.get(); }
    public String getTransactionType() { return transactionType.get(); }
    public double getAmount() { return amount.get(); }
    public String getStatus() { return status.get(); }
    public String getPaymentMethod() { return paymentMethod.get(); }
    public String getDescription() { return description.get(); }
    public LocalDateTime getTransactionDate() { return transactionDate.get(); }
    public String getUserName() { return userName.get(); }
    public String getBookingReference() { return bookingReference.get(); }
    
    // Additional getters for database fields
    public String getPaymentProvider() { return paymentProvider.get(); }
    public double getProcessingFee() { return processingFee.get(); }
    public double getTotalAmount() { return totalAmount.get(); }
    public String getGatewayTransactionId() { return gatewayTransactionId.get(); }
    public String getGatewayResponseCode() { return gatewayResponseCode.get(); }
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    
    // Basic setters
    public void setTransactionId(int transactionId) { this.transactionId.set(transactionId); }
    public void setUserId(int userId) { this.userId.set(userId); }
    public void setBookingId(int bookingId) { this.bookingId.set(bookingId); }
    public void setTransactionType(String transactionType) { this.transactionType.set(transactionType); }
    public void setAmount(double amount) { this.amount.set(amount); }
    public void setStatus(String status) { this.status.set(status); }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod.set(paymentMethod); }
    public void setDescription(String description) { this.description.set(description); }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate.set(transactionDate); }
    public void setUserName(String userName) { this.userName.set(userName); }
    public void setBookingReference(String bookingReference) { this.bookingReference.set(bookingReference); }
    
    // Additional setters for database fields
    public void setPaymentProvider(String paymentProvider) { this.paymentProvider.set(paymentProvider); }
    public void setProcessingFee(double processingFee) { this.processingFee.set(processingFee); }
    public void setTotalAmount(double totalAmount) { this.totalAmount.set(totalAmount); }
    public void setGatewayTransactionId(String gatewayTransactionId) { this.gatewayTransactionId.set(gatewayTransactionId); }
    public void setGatewayResponseCode(String gatewayResponseCode) { this.gatewayResponseCode.set(gatewayResponseCode); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt.set(updatedAt); }
    
    // Utility methods
    public String getFormattedDateTime() {
        if (transactionDate.get() != null) {
            return transactionDate.get().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        }
        return "Unknown";
    }
    
    public String getFormattedCreatedAt() {
        if (createdAt.get() != null) {
            return createdAt.get().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        }
        return "Unknown";
    }
    
    public String getFormattedUpdatedAt() {
        if (updatedAt.get() != null) {
            return updatedAt.get().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        }
        return "Unknown";
    }
    
    public String getFormattedAmount() {
        return String.format("₱%.2f", amount.get());
    }
    
    public String getFormattedTotalAmount() {
        return String.format("₱%.2f", totalAmount.get());
    }
    
    public String getFormattedProcessingFee() {
        return String.format("₱%.2f", processingFee.get());
    }
    
    public String getStatusBadgeColor() {
        switch (status.get().toLowerCase()) {
            case "completed":
            case "success":
                return "#28a745";
            case "pending":
                return "#ffc107";
            case "failed":
            case "cancelled":
                return "#dc3545";
            case "refunded":
                return "#6c757d";
            default:
                return "#6c757d";
        }
    }
    
    // Get transaction reference
    public String getTransactionReference() {
        return getDescription();
    }
    
    public void setTransactionReference(String transactionReference) {
        setDescription(transactionReference);
    }
    
    
  
    @Override
    public String toString() {
        return String.format("Transaction{id=%d, ref=%s, type=%s, amount=%.2f, total=%.2f, status=%s, method=%s, provider=%s, date=%s}", 
                           getTransactionId(), getDescription(), getTransactionType(), 
                           getAmount(), getTotalAmount(), getStatus(), getPaymentMethod(), 
                           getPaymentProvider(), getFormattedDateTime());
    }
}