package application.service;

import application.database.DatabaseConnection;
import application.model.Flight;
import application.model.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    
    // ========================= BOOKING MANAGEMENT =========================
    
    public static class PendingBooking {
        private int bookingId;
        private int userId;
        private String bookingReference;
        private String flightNumber;
        private String origin;
        private String destination;
        private String passengerName;
        private String passengerEmail;
        private double amount;
        private String paymentMethod;
        private LocalDateTime bookingDate;
        private String seatNumber;
        
        public PendingBooking(int bookingId, int userId, String bookingReference, String flightNumber, 
                            String origin, String destination, String passengerName, String passengerEmail,
                            double amount, String paymentMethod, LocalDateTime bookingDate, String seatNumber) {
            this.bookingId = bookingId;
            this.userId = userId;
            this.bookingReference = bookingReference;
            this.flightNumber = flightNumber;
            this.origin = origin;
            this.destination = destination;
            this.passengerName = passengerName;
            this.passengerEmail = passengerEmail;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
            this.bookingDate = bookingDate;
            this.seatNumber = seatNumber;
        }
        
        // Getters
        public int getBookingId() { return bookingId; }
        public int getUserId() { return userId; }
        public String getBookingReference() { return bookingReference; }
        public String getFlightNumber() { return flightNumber; }
        public String getOrigin() { return origin; }
        public String getDestination() { return destination; }
        public String getPassengerName() { return passengerName; }
        public String getPassengerEmail() { return passengerEmail; }
        public double getAmount() { return amount; }
        public String getPaymentMethod() { return paymentMethod; }
        public LocalDateTime getBookingDate() { return bookingDate; }
        public String getSeatNumber() { return seatNumber; }
    }
    
    // Get all pending bookings for admin review
    public static List<PendingBooking> getPendingBookings() {
        List<PendingBooking> pendingBookings = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT b.id, b.user_id, b.booking_reference, f.flight_number, f.origin, f.destination, " +
                        "CONCAT(COALESCE(u.first_name, 'Guest'), ' ', COALESCE(u.last_name, 'User')) as passenger_name, " +
                        "COALESCE(u.email, 'N/A') as passenger_email, " +
                        "t.total_amount, t.payment_method, b.booking_date, b.seat_number " +
                        "FROM bookings b " +
                        "LEFT JOIN flights f ON b.flight_id = f.id " +
                        "LEFT JOIN users u ON b.user_id = u.id " +
                        "LEFT JOIN transactions t ON b.id = t.booking_id " +
                        "WHERE b.status = 'pending' " +
                        "ORDER BY b.booking_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                PendingBooking booking = new PendingBooking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("booking_reference"),
                    rs.getString("flight_number"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getString("passenger_name"),
                    rs.getString("passenger_email"),
                    rs.getDouble("total_amount"),
                    rs.getString("payment_method"),
                    rs.getTimestamp("booking_date").toLocalDateTime(),
                    rs.getString("seat_number")
                );
                pendingBookings.add(booking);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching pending bookings: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pendingBookings;
    }
    
    // Approve a booking
    public static boolean approveBooking(int bookingId, String adminNotes) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update booking status to confirmed
            String updateBookingSql = "UPDATE bookings SET status = 'confirmed' WHERE id = ?";
            PreparedStatement bookingStmt = conn.prepareStatement(updateBookingSql);
            bookingStmt.setInt(1, bookingId);
            int bookingResult = bookingStmt.executeUpdate();
            
            // Update transaction status to paid
            String updateTransactionSql = "UPDATE transactions SET payment_status = 'paid' WHERE booking_id = ?";
            PreparedStatement transactionStmt = conn.prepareStatement(updateTransactionSql);
            transactionStmt.setInt(1, bookingId);
            int transactionResult = transactionStmt.executeUpdate();
            
            if (bookingResult > 0 && transactionResult > 0) {
                // Get booking details for notification
                String getBookingSql = "SELECT user_id, booking_reference FROM bookings WHERE id = ?";
                PreparedStatement getBookingStmt = conn.prepareStatement(getBookingSql);
                getBookingStmt.setInt(1, bookingId);
                ResultSet rs = getBookingStmt.executeQuery();
                
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String bookingReference = rs.getString("booking_reference");
                    
                    // Create approval notification
                    if (userId > 0) {
                        NotificationService.createBookingApprovedNotification(userId, bookingReference, bookingId);
                    }
                }
                
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error approving booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Reject a booking
    public static boolean rejectBooking(int bookingId, String reason) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update booking status to rejected
            String updateBookingSql = "UPDATE bookings SET status = 'rejected' WHERE id = ?";
            PreparedStatement bookingStmt = conn.prepareStatement(updateBookingSql);
            bookingStmt.setInt(1, bookingId);
            int bookingResult = bookingStmt.executeUpdate();
            
            // Update transaction status to failed
            String updateTransactionSql = "UPDATE transactions SET payment_status = 'failed' WHERE booking_id = ?";
            PreparedStatement transactionStmt = conn.prepareStatement(updateTransactionSql);
            transactionStmt.setInt(1, bookingId);
            int transactionResult = transactionStmt.executeUpdate();
            
            if (bookingResult > 0 && transactionResult > 0) {
                // Get booking details for notification
                String getBookingSql = "SELECT user_id, booking_reference, flight_id FROM bookings WHERE id = ?";
                PreparedStatement getBookingStmt = conn.prepareStatement(getBookingSql);
                getBookingStmt.setInt(1, bookingId);
                ResultSet rs = getBookingStmt.executeQuery();
                
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String bookingReference = rs.getString("booking_reference");
                    int flightId = rs.getInt("flight_id");
                    
                    // Restore available seats
                    String updateSeatsSql = "UPDATE flights SET available_seats = available_seats + 1 WHERE id = ?";
                    PreparedStatement seatsStmt = conn.prepareStatement(updateSeatsSql);
                    seatsStmt.setInt(1, flightId);
                    seatsStmt.executeUpdate();
                    
                    // Create rejection notification
                    if (userId > 0) {
                        NotificationService.createBookingRejectedNotification(userId, bookingReference, bookingId, reason);
                    }
                }
                
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error rejecting booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ========================= DASHBOARD STATISTICS =========================
    
    public static int getTotalUsersCount() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM users WHERE user_type = 'regular'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total users count: " + e.getMessage());
        }
        
        return 0;
    }
    
    public static int getTotalFlightsCount() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM flights";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total flights count: " + e.getMessage());
        }
        
        return 0;
    }
    
    public static double getTotalRevenue() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT SUM(total_amount) FROM transactions WHERE payment_status = 'paid'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    // Get booking count by status
    public static int getBookingCountByStatus(String status) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM bookings WHERE status = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting booking count: " + e.getMessage());
        }
        
        return 0;
    }
    
    // ========================= FLIGHT MANAGEMENT =========================
    
    public static List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM flights ORDER BY departure_time";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Flight flight = new Flight(
                    rs.getInt("id"),
                    rs.getString("flight_number"),
                    rs.getString("airline_name"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getTimestamp("departure_time").toLocalDateTime(),
                    rs.getTimestamp("arrival_time").toLocalDateTime(),
                    rs.getDouble("price"),
                    rs.getInt("available_seats"),
                    rs.getInt("total_seats")
                );
                flights.add(flight);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching all flights: " + e.getMessage());
            e.printStackTrace();
        }
        
        return flights;
    }
    
    public static boolean addFlight(String flightNumber, String airlineName, String origin, String destination, 
                                  LocalDateTime departureTime, LocalDateTime arrivalTime, double price, 
                                  int totalSeats) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO flights (flight_number, airline_name, origin, destination, departure_time, arrival_time, price, available_seats, total_seats) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, flightNumber);
            stmt.setString(2, airlineName);
            stmt.setString(3, origin);
            stmt.setString(4, destination);
            stmt.setTimestamp(5, Timestamp.valueOf(departureTime));
            stmt.setTimestamp(6, Timestamp.valueOf(arrivalTime));
            stmt.setDouble(7, price);
            stmt.setInt(8, totalSeats); // Initially all seats are available
            stmt.setInt(9, totalSeats);
            
            int result = stmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding flight: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateFlight(int flightId, String flightNumber, String airlineName, String origin, 
                                     String destination, LocalDateTime departureTime, LocalDateTime arrivalTime, 
                                     double price, int totalSeats) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE flights SET flight_number = ?, airline_name = ?, origin = ?, destination = ?, departure_time = ?, arrival_time = ?, price = ?, total_seats = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, flightNumber);
            stmt.setString(2, airlineName);
            stmt.setString(3, origin);
            stmt.setString(4, destination);
            stmt.setTimestamp(5, Timestamp.valueOf(departureTime));
            stmt.setTimestamp(6, Timestamp.valueOf(arrivalTime));
            stmt.setDouble(7, price);
            stmt.setInt(8, totalSeats);
            stmt.setInt(9, flightId);
            
            int result = stmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating flight: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteFlight(int flightId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if there are any bookings for this flight
            String checkBookingsSql = "SELECT COUNT(*) FROM bookings WHERE flight_id = ? AND status IN ('confirmed', 'pending')";
            PreparedStatement checkStmt = conn.prepareStatement(checkBookingsSql);
            checkStmt.setInt(1, flightId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Cannot delete flight with active bookings
                conn.rollback();
                return false;
            }
            
            // Delete the flight
            String deleteSql = "DELETE FROM flights WHERE id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, flightId);
            
            int result = deleteStmt.executeUpdate();
            
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting flight: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ========================= USER MANAGEMENT =========================
    
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM users WHERE user_type = 'regular' ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("user_type")
                );
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    public static boolean deleteUser(int userId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if user has active bookings
            String checkBookingsSql = "SELECT COUNT(*) FROM bookings WHERE user_id = ? AND status IN ('confirmed', 'pending')";
            PreparedStatement checkStmt = conn.prepareStatement(checkBookingsSql);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Cannot delete user with active bookings
                conn.rollback();
                return false;
            }
            
            // Delete user notifications first
            String deleteNotificationsSql = "DELETE FROM notifications WHERE user_id = ?";
            PreparedStatement notifStmt = conn.prepareStatement(deleteNotificationsSql);
            notifStmt.setInt(1, userId);
            notifStmt.executeUpdate();
            
            // Delete user messages
            String deleteMessagesSql = "DELETE FROM messages WHERE user_id = ?";
            PreparedStatement msgStmt = conn.prepareStatement(deleteMessagesSql);
            msgStmt.setInt(1, userId);
            msgStmt.executeUpdate();
            
            // Delete user
            String deleteUserSql = "DELETE FROM users WHERE id = ? AND user_type = 'regular'";
            PreparedStatement userStmt = conn.prepareStatement(deleteUserSql);
            userStmt.setInt(1, userId);
            
            int result = userStmt.executeUpdate();
            
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ========================= MESSAGE MANAGEMENT =========================
    
    public static class AdminMessage {
        private int id;
        private int userId;
        private String userName;
        private String userEmail;
        private String message;
        private String senderType;
        private LocalDateTime createdAt;
        private boolean isRead;
        
        public AdminMessage(int id, int userId, String userName, String userEmail, String message, 
                          String senderType, LocalDateTime createdAt, boolean isRead) {
            this.id = id;
            this.userId = userId;
            this.userName = userName;
            this.userEmail = userEmail;
            this.message = message;
            this.senderType = senderType;
            this.createdAt = createdAt;
            this.isRead = isRead;
        }
        
        // Getters
        public int getId() { return id; }
        public int getUserId() { return userId; }
        public String getUserName() { return userName; }
        public String getUserEmail() { return userEmail; }
        public String getMessage() { return message; }
        public String getSenderType() { return senderType; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public boolean isRead() { return isRead; }
    }
    
    public static List<AdminMessage> getAllMessages() {
        List<AdminMessage> messages = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT m.*, CONCAT(u.first_name, ' ', u.last_name) as user_name, u.email as user_email " +
                        "FROM messages m " +
                        "LEFT JOIN users u ON m.user_id = u.id " +
                        "ORDER BY m.created_at DESC";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                AdminMessage message = new AdminMessage(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getString("user_email"),
                    rs.getString("message"),
                    rs.getString("sender_type"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getBoolean("is_read")
                );
                messages.add(message);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching messages: " + e.getMessage());
            e.printStackTrace();
        }
        
        return messages;
    }
    
    public static boolean sendAdminReply(int userId, String message, int adminId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO messages (user_id, message, sender_type, admin_id) VALUES (?, ?, 'admin', ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, userId);
            stmt.setString(2, message);
            stmt.setInt(3, adminId);
            
            int result = stmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error sending admin reply: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean markMessageAsRead(int messageId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE messages SET is_read = true WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, messageId);
            
            int result = stmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error marking message as read: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ========================= REPORTS & ANALYTICS =========================
    
    public static class RevenueReport {
        private String period;
        private double revenue;
        private int bookingCount;
        
        public RevenueReport(String period, double revenue, int bookingCount) {
            this.period = period;
            this.revenue = revenue;
            this.bookingCount = bookingCount;
        }
        
        public String getPeriod() { return period; }
        public double getRevenue() { return revenue; }
        public int getBookingCount() { return bookingCount; }
    }
    
    public static List<RevenueReport> getMonthlyRevenueReport() {
        List<RevenueReport> reports = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT DATE_FORMAT(payment_date, '%Y-%m') as month, " +
                        "SUM(total_amount) as revenue, COUNT(*) as booking_count " +
                        "FROM transactions " +
                        "WHERE payment_status = 'paid' " +
                        "GROUP BY DATE_FORMAT(payment_date, '%Y-%m') " +
                        "ORDER BY month DESC " +
                        "LIMIT 12";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                RevenueReport report = new RevenueReport(
                    rs.getString("month"),
                    rs.getDouble("revenue"),
                    rs.getInt("booking_count")
                );
                reports.add(report);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching revenue report: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reports;
    }
    
    public static int getUnreadMessagesCount() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM messages WHERE sender_type = 'user' AND is_read = false";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unread messages count: " + e.getMessage());
        }
        
        return 0;
    }
}