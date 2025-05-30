package application.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking {
    private int id;
    private int userId;
    private int flightId;
    private String bookingReference;
    private String seatNumber;
    private LocalDateTime bookingDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional display properties
    private String customerName;
    private String customerEmail;
    private String flightInfo;
    private double paymentAmount;
    private String paymentStatus;
    private String paymentMethod;
    
    // Constructors
    public Booking() {}
    
    public Booking(int userId, int flightId, String bookingReference, String status) {
        this.userId = userId;
        this.flightId = flightId;
        this.bookingReference = bookingReference;
        this.status = status;
        this.bookingDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }
    
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Display properties getters and setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getFlightInfo() { return flightInfo; }
    public void setFlightInfo(String flightInfo) { this.flightInfo = flightInfo; }
    
    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    // Formatted display methods
    public String getFormattedBookingDate() {
        if (bookingDate == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return bookingDate.format(formatter);
    }
    
    public String getFormattedAmount() {
        return String.format("₱%.2f", paymentAmount);
    }
    
    public String getStatusColor() {
        if (status == null) return "#6c757d";
        
        switch (status.toLowerCase()) {
            case "confirmed":
                return "#28a745"; // Green
            case "pending":
                return "#ffc107"; // Yellow
            case "cancelled":
                return "#dc3545"; // Red
            case "completed":
                return "#007bff"; // Blue
            case "payment_failed":
                return "#fd7e14"; // Orange
            default:
                return "#6c757d"; // Gray
        }
    }
}