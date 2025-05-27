package application.service;

import application.database.DatabaseConnection;
import application.model.Flight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FlightService {
    
    // DateTimeFormatter for parsing database datetime values
    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Get all available flights from the database
     */
    public CompletableFuture<ObservableList<Flight>> getAvailableFlights() {
        return CompletableFuture.supplyAsync(() -> {
            List<Flight> flightList = new ArrayList<>();
            
            try {
                // Get database connection
                Connection conn = DatabaseConnection.getConnection();
                
                // Create SQL query to get all flights - using the correct column names
                String query = "SELECT * FROM flights ORDER BY departure_time";
                PreparedStatement stmt = conn.prepareStatement(query);
                
                // Execute query and process results
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Flight flight = mapResultSetToFlight(rs);
                    flightList.add(flight);
                }
                
            } catch (SQLException e) {
                System.err.println("Error fetching flights from database: " + e.getMessage());
                e.printStackTrace();
            }
            
            return FXCollections.observableArrayList(flightList);
        });
    }
    
    /**
     * Search for flights by destination, origin, or airline
     */
    public CompletableFuture<ObservableList<Flight>> searchFlights(String query) {
        return CompletableFuture.supplyAsync(() -> {
            List<Flight> flightList = new ArrayList<>();
            
            try {
                // Get database connection
                Connection conn = DatabaseConnection.getConnection();
                
                // Create SQL query with search parameters - using the correct column names
                String searchQuery = "SELECT * FROM flights WHERE " +
                        "origin LIKE ? OR " +
                        "destination LIKE ? OR " +
                        "airline_name LIKE ? OR " +
                        "flight_number LIKE ? " +
                        "ORDER BY departure_time";
                
                PreparedStatement stmt = conn.prepareStatement(searchQuery);
                
                // Set search parameters with wildcard for partial matches
                String searchParam = "%" + query + "%";
                stmt.setString(1, searchParam);
                stmt.setString(2, searchParam);
                stmt.setString(3, searchParam);
                stmt.setString(4, searchParam);
                
                // Execute query and process results
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Flight flight = mapResultSetToFlight(rs);
                    flightList.add(flight);
                }
                
            } catch (SQLException e) {
                System.err.println("Error searching flights in database: " + e.getMessage());
                e.printStackTrace();
            }
            
            return FXCollections.observableArrayList(flightList);
        });
    }
    
    /**
     * Helper method to map ResultSet row to Flight object
     */
    private Flight mapResultSetToFlight(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String flightNo = rs.getString("flight_number");
        String airlineName = rs.getString("airline_name");
        String origin = rs.getString("origin");
        String destination = rs.getString("destination");
        
        // Parse datetime strings to LocalDateTime
        String departureStr = rs.getString("departure_time");
        String arrivalStr = rs.getString("arrival_time");
        LocalDateTime departure = LocalDateTime.parse(departureStr.replace(".0", ""), DB_FORMATTER);
        LocalDateTime arrival = LocalDateTime.parse(arrivalStr.replace(".0", ""), DB_FORMATTER);
        
        String duration = rs.getString("duration");
        String aircraft = rs.getString("aircraft_type");
        int seats = rs.getInt("available_seats");
        String status = rs.getString("status");
        double price = rs.getDouble("base_price");
        
        return new Flight(id, flightNo, airlineName, origin, destination, 
                         departure, arrival, duration, aircraft, seats, status, price);
    }
    
    /**
     * Create a new booking in the database
     */
    public CompletableFuture<Boolean> createBooking(Flight flight, String firstName, String lastName, 
                                                  String email, String phone, int passengerCount, double totalPrice) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // In a real implementation, you would:
                // 1. Create a booking record in the 'bookings' table
                // 2. Create passenger records in a 'passengers' table
                // 3. Update available seats for the flight
                
                // For now, we'll just simulate a successful booking with a delay
                Thread.sleep(1000);
                return true;
            } catch (Exception e) {
                System.err.println("Error creating booking: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }
    

public CompletableFuture<Boolean> createBookingWithPayment(Flight flight, int userId, 
        String firstName, String lastName, String email, String phone,
        String bookingRef, double totalPrice, String paymentMethod, String transactionRef) {
    
    return CompletableFuture.supplyAsync(() -> {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            System.out.println("=== DEBUG: Starting booking transaction ===");
            System.out.println("Payment method: " + paymentMethod);
            System.out.println("Total amount: " + totalPrice);
            
            // Generate seats for flight if they don't exist
            SeatService.generateSeatsForFlight(flight.getId(), flight.getAircraft());
            
            // Get a random available seat
            String seatNumber = SeatService.getRandomAvailableSeat(flight.getId(), "economy", false, false);
            
            if (seatNumber == null) {
                System.err.println("No available seats for flight " + flight.getId());
                return false;
            }
            
            // Insert booking
            String bookingSQL = "INSERT INTO bookings (user_id, flight_id, booking_reference, seat_number, status) VALUES (?, ?, ?, ?, 'confirmed')";
            PreparedStatement bookingStmt = conn.prepareStatement(bookingSQL, Statement.RETURN_GENERATED_KEYS);
            
            if (userId == 0) {
                bookingStmt.setNull(1, Types.INTEGER);
            } else {
                bookingStmt.setInt(1, userId);
            }
            bookingStmt.setInt(2, flight.getId());
            bookingStmt.setString(3, bookingRef);
            bookingStmt.setString(4, seatNumber);
            
            int bookingResult = bookingStmt.executeUpdate();
            if (bookingResult == 0) {
                conn.rollback();
                return false;
            }
            
            // Get booking ID
            ResultSet bookingKeys = bookingStmt.getGeneratedKeys();
            int bookingId = 0;
            if (bookingKeys.next()) {
                bookingId = bookingKeys.getInt(1);
            }
            
            // Reserve the seat
            boolean seatReserved = SeatService.reserveSeat(flight.getId(), seatNumber, bookingId);
            if (!seatReserved) {
                System.err.println("Failed to reserve seat " + seatNumber);
                conn.rollback();
                return false;
            }
            
            // Calculate processing fee and total
            double processingFee = calculateProcessingFee(flight.getPrice(), paymentMethod);
            String paymentProvider = getPaymentProvider(paymentMethod);
            
            // Insert transaction with enhanced details
            String transactionSQL = "INSERT INTO transactions (booking_id, transaction_reference, payment_method, " +
                                  "payment_provider, amount, processing_fee, total_amount, payment_status, gateway_transaction_id) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, 'paid', ?)";
            PreparedStatement transactionStmt = conn.prepareStatement(transactionSQL);
            transactionStmt.setInt(1, bookingId);
            transactionStmt.setString(2, transactionRef);
            transactionStmt.setString(3, paymentMethod);
            transactionStmt.setString(4, paymentProvider);
            transactionStmt.setDouble(5, flight.getPrice());
            transactionStmt.setDouble(6, processingFee);
            transactionStmt.setDouble(7, totalPrice);
            transactionStmt.setString(8, generateGatewayTransactionId(paymentMethod));
            
            int transactionResult = transactionStmt.executeUpdate();
            if (transactionResult == 0) {
                conn.rollback();
                return false;
            }
            
            conn.commit();
            System.out.println("=== DEBUG: Booking completed successfully with seat " + seatNumber + " ===");
            System.out.println("Payment method: " + paymentMethod + " via " + paymentProvider);
            return true;
            
        } catch (SQLException e) {
            System.err.println("=== DEBUG: Database error in booking process ===");
            e.printStackTrace();
            try {
                DatabaseConnection.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        }
    });
}

private double calculateProcessingFee(double baseAmount, String paymentMethod) {
    switch (paymentMethod) {
        case "gcash":
        case "maya":
            return baseAmount * 0.025; // 2.5% fee
        case "paypal":
            return (baseAmount * 0.039) + 15; // 3.9% + ₱15 fee
        case "credit_card":
        default:
            return 0.0; // No fee for credit cards
    }
}

private String getPaymentProvider(String paymentMethod) {
    switch (paymentMethod) {
        case "gcash":
            return "Globe Telecom (GCash)";
        case "maya":
            return "Voyager Innovations (Maya)";
        case "paypal":
            return "PayPal Holdings Inc.";
        case "credit_card":
        default:
            return "Bank Payment Gateway";
    }
}

private String generateGatewayTransactionId(String paymentMethod) {
    String prefix;
    switch (paymentMethod) {
        case "gcash":
            prefix = "GCW";
            break;
        case "maya":
            prefix = "MYA";
            break;
        case "paypal":
            prefix = "PPL";
            break;
        case "credit_card":
        default:
            prefix = "BPG";
            break;
    }
    return prefix + System.currentTimeMillis();
}


}