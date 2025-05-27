package application.model;

import java.time.LocalDateTime;

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
}