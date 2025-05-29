package application.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class Flight {
    private int id;
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
    }
    
    // Full constructor
    public Flight(int id, String flightNo, String airlineName, String origin, String destination,
                 LocalDateTime departure, LocalDateTime arrival, String duration, 
                 String aircraft, int seats, String status, double price) {
        this.id = id;
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
    
    // Alternative constructor (for compatibility)
    public Flight(int id, String flightNo, String airlineName, String origin, String destination, 
                 LocalDateTime departure, LocalDateTime arrival, double price, int seats, int status) {
        this.id = id;
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

    // Getters
    public int getId() {
        return id;
    }
    
    public String getFlightNo() {
        return flightNo;
    }
    
    public String getAirlineName() {
        return airlineName;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public LocalDateTime getDeparture() {
        return departure;
    }
    
    public LocalDateTime getArrival() {
        return arrival;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public String getAircraft() {
        return aircraft;
    }
    
    public int getSeats() {
        return seats;
    }
    
    public String getStatus() {
        return status;
    }
    
    public double getPrice() {
        return price;
    }
    
    // Setters - These were missing and causing the errors
    public void setId(int id) {
        this.id = id;
    }
    
    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }
    
    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public void setDeparture(LocalDateTime departure) {
        this.departure = departure;
    }
    
    public void setArrival(LocalDateTime arrival) {
        this.arrival = arrival;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public void setAircraft(String aircraft) {
        this.aircraft = aircraft;
    }
    
    public void setSeats(int seats) {
        this.seats = seats;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    // Formatted string representations
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
    
    @Override
    public String toString() {
        return flightNo + " - " + airlineName + " (" + origin + " to " + destination + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Flight flight = (Flight) obj;
        return id == flight.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}