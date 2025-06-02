package application.service;

import application.model.Booking;
import application.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class AdminBookingService {
    
    public static ObservableList<Booking> getAllBookings() {
        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        String query = "SELECT b.*, u.first_name, u.last_name, u.email, " +
                      "f.flight_number, f.airline_name, f.origin, f.destination, f.departure_time, " +
                      "t.total_amount, t.payment_status, t.payment_method " +
                      "FROM bookings b " +
                      "LEFT JOIN users u ON b.user_id = u.id " +
                      "LEFT JOIN flights f ON b.flight_id = f.id " +
                      "LEFT JOIN transactions t ON b.id = t.booking_id " +
                      "ORDER BY b.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching bookings: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bookings;
    }
    
    private static Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setFlightId(rs.getInt("flight_id"));
        booking.setBookingReference(rs.getString("booking_reference"));
        booking.setSeatNumber(rs.getString("seat_number"));
        booking.setStatus(rs.getString("status"));
        
        Timestamp bookingDate = rs.getTimestamp("booking_date");
        if (bookingDate != null) {
            booking.setBookingDate(bookingDate.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            booking.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        // Set additional display properties
        booking.setCustomerName(getCustomerName(rs));
        booking.setCustomerEmail(rs.getString("email"));
        booking.setFlightInfo(getFlightInfo(rs));
        booking.setPaymentAmount(rs.getDouble("total_amount"));
        booking.setPaymentStatus(rs.getString("payment_status"));
        booking.setPaymentMethod(rs.getString("payment_method"));
        
        return booking;
    }
    
    private static String getCustomerName(ResultSet rs) throws SQLException {
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else {
            return "Guest User";
        }
    }
    
    private static String getFlightInfo(ResultSet rs) throws SQLException {
        String flightNumber = rs.getString("flight_number");
        String airline = rs.getString("airline_name");
        String origin = rs.getString("origin");
        String destination = rs.getString("destination");
        
        if (flightNumber != null) {
            return flightNumber + " - " + airline + " (" + origin + " → " + destination + ")";
        }
        return "Unknown Flight";
    }
    
    public static boolean updateBookingStatus(int bookingId, String newStatus) {
        String query = "UPDATE bookings SET status = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, bookingId);
            
            int rowsAffected = stmt.executeUpdate();
            
            // If booking is confirmed, also update payment status
            if (rowsAffected > 0 && "confirmed".equals(newStatus)) {
                updatePaymentStatus(bookingId, "paid");
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static void updatePaymentStatus(int bookingId, String paymentStatus) {
        String query = "UPDATE transactions SET payment_status = ?, updated_at = NOW() WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, paymentStatus);
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
        }
    }
    
    public static boolean deleteBooking(int bookingId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Delete transactions first (foreign key constraint)
            String deleteTransactions = "DELETE FROM transactions WHERE booking_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteTransactions)) {
                stmt.setInt(1, bookingId);
                stmt.executeUpdate();
            }
            
            // Delete booking
            String deleteBooking = "DELETE FROM bookings WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteBooking)) {
                stmt.setInt(1, bookingId);
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                }
            }
            
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static ObservableList<Booking> getBookingsByStatus(String status) {
        ObservableList<Booking> bookings = FXCollections.observableArrayList();
        String query = "SELECT b.*, u.first_name, u.last_name, u.email, " +
                      "f.flight_number, f.airline_name, f.origin, f.destination, f.departure_time, " +
                      "t.total_amount, t.payment_status, t.payment_method " +
                      "FROM bookings b " +
                      "LEFT JOIN users u ON b.user_id = u.id " +
                      "LEFT JOIN flights f ON b.flight_id = f.id " +
                      "LEFT JOIN transactions t ON b.id = t.booking_id " +
                      "WHERE b.status = ? " +
                      "ORDER BY b.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching bookings by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bookings;
    }
    
    public static int getBookingCountByStatus(String status) {
        String query = "SELECT COUNT(*) FROM bookings WHERE status = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting booking count by status: " + e.getMessage());
        }
        
        return 0;
    }
    
    public static int getTotalBookingsCount() {
        String query = "SELECT COUNT(*) FROM bookings";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total bookings count: " + e.getMessage());
        }
        
        return 0;
    }
}