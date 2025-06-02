package application.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flight extends BaseEntity { // INHERITANCE
    // ENCAPSULATION - private fields
    private String flightNo;
    private String airlineName;
    private String origin;
    private String destination;
    private LocalDateTime departure;
    private LocalDateTime arrival;
    private String duration;
    private String aircraft;
    private int seats;
    private String status;
    private double price;
    
    // Default constructor
    public Flight() {
        super();
    }
    
    // Full constructor
    public Flight(int id, String flightNo, String airlineName, String origin, String destination,
                 LocalDateTime departure, LocalDateTime arrival, String duration, 
                 String aircraft, int seats, String status, double price) {
        super(id);
        this.flightNo = flightNo;
        this.airlineName = airlineName;
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.duration = duration;
        this.aircraft = aircraft;
        this.seats = seats;
        this.status = status;
        this.price = price;
    }
    
    // Alternative constructor
    public Flight(int id, String flightNo, String airlineName, String origin, String destination, 
                 LocalDateTime departure, LocalDateTime arrival, double price, int seats, int status) {
        super(id);
        this.flightNo = flightNo;
        this.airlineName = airlineName;
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.price = price;
        this.seats = seats;
        this.status = String.valueOf(status);
    }

    // ENCAPSULATION - controlled access through getters/setters
    public String getFlightNo() { return flightNo; }
    public void setFlightNo(String flightNo) { 
        this.flightNo = flightNo; 
        updateTimestamp();
    }
    
    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { 
        this.airlineName = airlineName; 
        updateTimestamp();
    }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { 
        this.origin = origin; 
        updateTimestamp();
    }
    
    public String getDestination() { return destination; }
    public void setDestination(String destination) { 
        this.destination = destination; 
        updateTimestamp();
    }
    
    public LocalDateTime getDeparture() { return departure; }
    public void setDeparture(LocalDateTime departure) { 
        this.departure = departure; 
        updateTimestamp();
    }
    
    public LocalDateTime getArrival() { return arrival; }
    public void setArrival(LocalDateTime arrival) { 
        this.arrival = arrival; 
        updateTimestamp();
    }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { 
        this.duration = duration; 
        updateTimestamp();
    }
    
    public String getAircraft() { return aircraft; }
    public void setAircraft(String aircraft) { 
        this.aircraft = aircraft; 
        updateTimestamp();
    }
    
    public int getSeats() { return seats; }
    public void setSeats(int seats) { 
        this.seats = seats; 
        updateTimestamp();
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status; 
        updateTimestamp();
    }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { 
        this.price = price; 
        updateTimestamp();
    }
    
    // Business methods
    public String getFormattedDeparture() {
        if (departure == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return departure.format(formatter);
    }
    
    public String getFormattedArrival() {
        if (arrival == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return arrival.format(formatter);
    }
    
    public String getRoute() {
        if (origin == null || destination == null) return "N/A";
        return origin + " → " + destination;
    }
    
    // POLYMORPHISM - implementing abstract method
    @Override
    public String getDisplayName() {
        return flightNo + " - " + airlineName;
    }
    
    @Override
    public String toString() {
        return flightNo + " - " + airlineName + " (" + origin + " to " + destination + ")";
    }
}