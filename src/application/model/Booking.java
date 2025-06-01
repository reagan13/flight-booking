package application.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking extends BaseEntity { // INHERITANCE
    // ENCAPSULATION - private fields
    private int userId;
    private int flightId;
    private String bookingReference;
    private String seatNumber;
    private LocalDateTime bookingDate;
    private String status;
    
    // Additional display properties
    private String customerName;
    private String customerEmail;
    private String flightInfo;
    private double paymentAmount;
    private String paymentStatus;
    private String paymentMethod;
    
    // Constructors
    public Booking() {
        super();
    }
    
    public Booking(int userId, int flightId, String bookingReference, String status) {
        super();
        this.userId = userId;
        this.flightId = flightId;
        this.bookingReference = bookingReference;
        this.status = status;
        this.bookingDate = LocalDateTime.now();
    }
    
    // ENCAPSULATION - controlled access through getters/setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { 
        this.userId = userId; 
        updateTimestamp();
    }
    
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { 
        this.flightId = flightId; 
        updateTimestamp();
    }
    
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { 
        this.bookingReference = bookingReference; 
        updateTimestamp();
    }
    
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { 
        this.seatNumber = seatNumber; 
        updateTimestamp();
    }
    
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { 
        this.bookingDate = bookingDate; 
        updateTimestamp();
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status; 
        updateTimestamp();
    }
    
    // Display properties
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
    
    // Business methods
    public String getFormattedBookingDate() {
        if (bookingDate == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return bookingDate.format(formatter);
    }
    
    public String getFormattedAmount() {
        return String.format("₱%.2f", paymentAmount);
    }
    
    // POLYMORPHISM - implementing abstract method
    @Override
    public String getDisplayName() {
        return bookingReference + " - " + customerName;
    }
}