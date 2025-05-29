package application.service;

import application.model.Flight;
import application.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class AdminFlightService {
    
    public static ObservableList<Flight> getAllFlights() {
        ObservableList<Flight> flights = FXCollections.observableArrayList();
        String query = "SELECT id, flight_number, airline_name, origin, destination, " +
                      "departure_time, arrival_time, duration, aircraft_type, available_seats, " +
                      "status, base_price FROM flights ORDER BY departure_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Flight flight = mapResultSetToFlight(rs);
                flights.add(flight);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching flights: " + e.getMessage());
            e.printStackTrace();
        }
        
        return flights;
    }
    
    private static Flight mapResultSetToFlight(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setId(rs.getInt("id"));
        flight.setFlightNo(rs.getString("flight_number"));
        flight.setAirlineName(rs.getString("airline_name"));
        flight.setOrigin(rs.getString("origin"));
        flight.setDestination(rs.getString("destination"));
        
        // Handle datetime conversion
        Timestamp depTimestamp = rs.getTimestamp("departure_time");
        if (depTimestamp != null) {
            flight.setDeparture(depTimestamp.toLocalDateTime());
        }
        
        Timestamp arrTimestamp = rs.getTimestamp("arrival_time");
        if (arrTimestamp != null) {
            flight.setArrival(arrTimestamp.toLocalDateTime());
        }
        
        flight.setDestination(rs.getString("duration"));
        flight.setAircraft(rs.getString("aircraft_type"));
        flight.setSeats(rs.getInt("available_seats"));
        flight.setStatus(rs.getString("status"));
        flight.setPrice(rs.getDouble("base_price"));
        
        return flight;
    }
    
    public static boolean addFlight(Flight flight) {
        String query = "INSERT INTO flights (id, flight_number, airline_name, origin, destination, " +
                      "departure_time, arrival_time, duration, aircraft_type, available_seats, " +
                      "status, base_price, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, flight.getId());
            stmt.setString(2, flight.getFlightNo().trim());
            stmt.setString(3, flight.getAirlineName().trim());
            stmt.setString(4, flight.getOrigin().trim().toUpperCase());
            stmt.setString(5, flight.getDestination().trim().toUpperCase());
            stmt.setTimestamp(6, Timestamp.valueOf(flight.getDeparture()));
            stmt.setTimestamp(7, Timestamp.valueOf(flight.getArrival()));
            stmt.setString(8, flight.getDuration());
            stmt.setString(9, flight.getAircraft().trim());
            stmt.setInt(10, flight.getSeats());
            stmt.setString(11, flight.getStatus());
            stmt.setDouble(12, flight.getPrice());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding flight: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateFlight(Flight flight) {
        String query = "UPDATE flights SET flight_number = ?, airline_name = ?, origin = ?, destination = ?, " +
                      "departure_time = ?, arrival_time = ?, duration = ?, aircraft_type = ?, " +
                      "available_seats = ?, status = ?, base_price = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, flight.getFlightNo().trim());
            stmt.setString(2, flight.getAirlineName().trim());
            stmt.setString(3, flight.getOrigin().trim().toUpperCase());
            stmt.setString(4, flight.getDestination().trim().toUpperCase());
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getDeparture()));
            stmt.setTimestamp(6, Timestamp.valueOf(flight.getArrival()));
            stmt.setString(7, flight.getDuration());
            stmt.setString(8, flight.getAircraft().trim());
            stmt.setInt(9, flight.getSeats());
            stmt.setString(10, flight.getStatus());
            stmt.setDouble(11, flight.getPrice());
            stmt.setInt(12, flight.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating flight: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteFlight(int flightId) {
        // First check if there are any bookings for this flight
        if (hasActiveBookings(flightId)) {
            System.err.println("Cannot delete flight with active bookings");
            return false;
        }
        
        String query = "DELETE FROM flights WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, flightId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting flight: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean hasActiveBookings(int flightId) {
        String query = "SELECT COUNT(*) FROM bookings WHERE flight_id = ? AND status IN ('confirmed', 'pending')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking active bookings: " + e.getMessage());
        }
        
        return false;
    }
    
    public static boolean flightIdExists(int flightId) {
        String query = "SELECT COUNT(*) FROM flights WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking flight ID existence: " + e.getMessage());
        }
        
        return false;
    }
    
    public static boolean flightNumberExists(String flightNumber) {
        String query = "SELECT COUNT(*) FROM flights WHERE flight_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, flightNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking flight number existence: " + e.getMessage());
        }
        
        return false;
    }
    
    public static boolean flightNumberExistsForOtherFlight(String flightNumber, int flightId) {
        String query = "SELECT COUNT(*) FROM flights WHERE flight_number = ? AND id != ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, flightNumber);
            stmt.setInt(2, flightId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking flight number for other flight: " + e.getMessage());
        }
        
        return false;
    }
    
    public static String validateFlightData(Flight flight, boolean isUpdate) {
        // Validate flight ID
        if (flight.getId() <= 0) {
            return "Flight ID must be a positive number.";
        }
        
        // Check flight ID uniqueness for new flights
        if (!isUpdate && flightIdExists(flight.getId())) {
            return "Flight ID already exists. Please use a different ID.";
        }
        
        // Validate flight number
        if (flight.getFlightNo() == null || flight.getFlightNo().trim().isEmpty()) {
            return "Flight number is required.";
        }
        
        if (flight.getFlightNo().length() < 2 || flight.getFlightNo().length() > 10) {
            return "Flight number must be between 2 and 10 characters.";
        }
        
        // Validate airline name
        if (flight.getAirlineName() == null || flight.getAirlineName().trim().isEmpty()) {
            return "Airline name is required.";
        }
        
        // Validate origin
        if (flight.getOrigin() == null || flight.getOrigin().trim().isEmpty()) {
            return "Origin is required.";
        }
        
        if (flight.getOrigin().length() != 3) {
            return "Origin must be a 3-letter airport code (e.g., MNL).";
        }
        
        // Validate destination
        if (flight.getDestination() == null || flight.getDestination().trim().isEmpty()) {
            return "Destination is required.";
        }
        
        if (flight.getDestination().length() != 3) {
            return "Destination must be a 3-letter airport code (e.g., CEB).";
        }
        
        if (flight.getOrigin().equalsIgnoreCase(flight.getDestination())) {
            return "Origin and destination cannot be the same.";
        }
        
        // Validate departure and arrival times
        if (flight.getDeparture() == null) {
            return "Departure time is required.";
        }
        
        if (flight.getArrival() == null) {
            return "Arrival time is required.";
        }
        
        if (flight.getDeparture().isAfter(flight.getArrival())) {
            return "Departure time cannot be after arrival time.";
        }
        
        if (flight.getDeparture().isBefore(LocalDateTime.now().minusHours(1))) {
            return "Departure time cannot be in the past.";
        }
        
        // Validate duration
        if (flight.getDuration() == null || flight.getDuration().trim().isEmpty()) {
            return "Duration is required.";
        }
        
        // Validate aircraft
        if (flight.getAircraft() == null || flight.getAircraft().trim().isEmpty()) {
            return "Aircraft type is required.";
        }
        
        // Validate seats
        if (flight.getSeats() <= 0) {
            return "Available seats must be greater than 0.";
        }
        
        if (flight.getSeats() > 1000) {
            return "Available seats cannot exceed 1000.";
        }
        
        // Validate price
        if (flight.getPrice() <= 0) {
            return "Price must be greater than 0.";
        }
        
        if (flight.getPrice() > 1000000) {
            return "Price cannot exceed ₱1,000,000.";
        }
        
        // Validate status
        if (flight.getStatus() == null || flight.getStatus().trim().isEmpty()) {
            return "Status is required.";
        }
        
        String[] validStatuses = {"Active", "Cancelled", "Delayed", "Completed"};
        boolean validStatus = false;
        for (String status : validStatuses) {
            if (status.equalsIgnoreCase(flight.getStatus())) {
                validStatus = true;
                break;
            }
        }
        
        if (!validStatus) {
            return "Status must be one of: Active, Cancelled, Delayed, Completed.";
        }
        
        // Check flight number uniqueness
        if (isUpdate) {
            if (flightNumberExistsForOtherFlight(flight.getFlightNo(), flight.getId())) {
                return "Flight number already exists for another flight.";
            }
        } else {
            if (flightNumberExists(flight.getFlightNo())) {
                return "Flight number already exists.";
            }
        }
        
        return null; // No validation errors
    }
    
    // Get flight statistics for admin dashboard
    public static int getTotalFlightsCount() {
        String query = "SELECT COUNT(*) FROM flights";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total flights count: " + e.getMessage());
        }
        
        return 0;
    }
    
    public static int getFlightCountByStatus(String status) {
        String query = "SELECT COUNT(*) FROM flights WHERE status = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting flight count by status: " + e.getMessage());
        }
        
        return 0;
    }
}