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
    
    // Constructor
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
    
    public Flight(int int1, String string, String string2, String string3, String string4, LocalDateTime localDateTime,
            LocalDateTime localDateTime2, double double1, int int2, int int3) {
        //TODO Auto-generated constructor stub
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
    
    // Formatted string representations
    public String getFormattedDeparture() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return departure.format(formatter);
    }
    
    public String getFormattedArrival() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return arrival.format(formatter);
    }
    
    public String getRoute() {
        return origin + " → " + destination;
    }
    
    @Override
    public String toString() {
        return flightNo + " - " + airlineName + " (" + origin + " to " + destination + ")";
    }
}