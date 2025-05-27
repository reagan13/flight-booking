package application.service;

import application.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeatService {
    
    /**
     * Generate seats for a flight if they don't exist
     */
    public static void generateSeatsForFlight(int flightId, String aircraftType) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Check if seats already exist for this flight
            String checkSQL = "SELECT COUNT(*) FROM seats WHERE flight_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setInt(1, flightId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Seats already exist for flight " + flightId);
                return;
            }
            
            // Generate seats based on aircraft type
            List<String> seats = generateSeatLayout(aircraftType);
            
            String insertSQL = "INSERT INTO seats (flight_id, seat_number, seat_class, is_window, is_aisle) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
            
            for (String seatNumber : seats) {
                insertStmt.setInt(1, flightId);
                insertStmt.setString(2, seatNumber);
                insertStmt.setString(3, determineSeatClass(seatNumber));
                insertStmt.setBoolean(4, isWindowSeat(seatNumber));
                insertStmt.setBoolean(5, isAisleSeat(seatNumber));
                insertStmt.addBatch();
            }
            
            insertStmt.executeBatch();
            System.out.println("Generated " + seats.size() + " seats for flight " + flightId);
            
        } catch (SQLException e) {
            System.err.println("Error generating seats: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get a random available seat
     */
    public static String getRandomAvailableSeat(int flightId, String seatClass, boolean preferWindow, boolean preferAisle) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            StringBuilder sql = new StringBuilder("SELECT seat_number FROM seats WHERE flight_id = ? AND is_available = TRUE AND seat_class = ?");
            
            // Add preferences
            if (preferWindow) {
                sql.append(" AND is_window = TRUE");
            } else if (preferAisle) {
                sql.append(" AND is_aisle = TRUE");
            }
            
            sql.append(" ORDER BY RAND() LIMIT 1");
            
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            stmt.setInt(1, flightId);
            stmt.setString(2, seatClass);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("seat_number");
            }
            
            // If no seat found with preferences, try without preferences
            if (preferWindow || preferAisle) {
                return getRandomAvailableSeat(flightId, seatClass, false, false);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error getting random seat: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Reserve a seat for a booking
     */
    public static boolean reserveSeat(int flightId, String seatNumber, int bookingId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            String sql = "UPDATE seats SET is_available = FALSE WHERE flight_id = ? AND seat_number = ? AND is_available = TRUE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, flightId);
            stmt.setString(2, seatNumber);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Seat " + seatNumber + " reserved for flight " + flightId);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error reserving seat: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate seat layout based on aircraft type
     */
    private static List<String> generateSeatLayout(String aircraftType) {
        List<String> seats = new ArrayList<>();
        
        switch (aircraftType.toLowerCase()) {
            case "boeing 737":
            case "airbus a320":
                // Small aircraft: 3-3 configuration, rows 1-30
                for (int row = 1; row <= 30; row++) {
                    seats.add(row + "A");
                    seats.add(row + "B");
                    seats.add(row + "C");
                    seats.add(row + "D");
                    seats.add(row + "E");
                    seats.add(row + "F");
                }
                break;
                
            case "boeing 777":
            case "airbus a350":
                // Large aircraft: 3-3-3 configuration, rows 1-40
                for (int row = 1; row <= 40; row++) {
                    seats.add(row + "A");
                    seats.add(row + "B");
                    seats.add(row + "C");
                    seats.add(row + "D");
                    seats.add(row + "E");
                    seats.add(row + "F");
                    seats.add(row + "G");
                    seats.add(row + "H");
                    seats.add(row + "J");
                }
                break;
                
            default:
                // Default: 3-3 configuration, rows 1-25
                for (int row = 1; row <= 25; row++) {
                    seats.add(row + "A");
                    seats.add(row + "B");
                    seats.add(row + "C");
                    seats.add(row + "D");
                    seats.add(row + "E");
                    seats.add(row + "F");
                }
        }
        
        return seats;
    }
    
    /**
     * Determine seat class based on seat number
     */
    private static String determineSeatClass(String seatNumber) {
        int row = Integer.parseInt(seatNumber.replaceAll("[A-Z]", ""));
        
        if (row <= 3) {
            return "first";
        } else if (row <= 10) {
            return "business";
        } else {
            return "economy";
        }
    }
    
    /**
     * Check if seat is a window seat
     */
    private static boolean isWindowSeat(String seatNumber) {
        String letter = seatNumber.replaceAll("[0-9]", "");
        return letter.equals("A") || letter.equals("F") || letter.equals("J");
    }
    
    /**
     * Check if seat is an aisle seat
     */
    private static boolean isAisleSeat(String seatNumber) {
        String letter = seatNumber.replaceAll("[0-9]", "");
        return letter.equals("C") || letter.equals("D") || letter.equals("G");
    }
}