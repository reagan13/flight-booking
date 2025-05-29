package application.service;

import application.database.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminService {
    
    // USER STATISTICS
    public static int getTotalUsersCount() {
        String query = "SELECT COUNT(*) FROM users WHERE user_type = 'customer'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total users count: " + e.getMessage());
        }
        return 0;
    }
    
    // FLIGHT STATISTICS
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
    
    // BOOKING STATISTICS
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
    
    public static int getTodayBookingsCount() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "SELECT COUNT(*) FROM bookings WHERE DATE(booking_date) = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, today);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's bookings count: " + e.getMessage());
        }
        return 0;
    }
    
    // REVENUE CALCULATIONS
    public static double calculateTotalRevenue() {
        String query = "SELECT SUM(total_amount) FROM bookings WHERE status = 'confirmed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total revenue: " + e.getMessage());
        }
        return 0.0;
    }
    
    public static double calculateWeeklyRevenue() {
        // Calculate revenue from the last 7 days
        String query = "SELECT SUM(total_amount) FROM bookings WHERE status = 'confirmed' " +
                      "AND booking_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating weekly revenue: " + e.getMessage());
        }
        return 0.0;
    }
    
    public static double calculateMonthlyRevenue() {
        String query = "SELECT SUM(total_amount) FROM bookings WHERE status = 'confirmed' " +
                      "AND MONTH(booking_date) = MONTH(CURDATE()) AND YEAR(booking_date) = YEAR(CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating monthly revenue: " + e.getMessage());
        }
        return 0.0;
    }
    
    // MESSAGE STATISTICS
    public static int getTotalMessagesCount() {
        // Assuming you have a messages/contact table
        String query = "SELECT COUNT(*) FROM messages";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total messages count: " + e.getMessage());
            // If messages table doesn't exist yet, return 0
        }
        return 0;
    }
    
    public static int getUnreadMessagesCount() {
        String query = "SELECT COUNT(*) FROM messages WHERE is_read = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting unread messages count: " + e.getMessage());
        }
        return 0;
    }
    
    // ADVANCED STATISTICS
    public static double getAverageBookingValue() {
        String query = "SELECT AVG(total_amount) FROM bookings WHERE status = 'confirmed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating average booking value: " + e.getMessage());
        }
        return 0.0;
    }
    
    public static int getActiveFlightsCount() {
        // Flights with departure date >= today
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "SELECT COUNT(*) FROM flights WHERE departure_date >= ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, today);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active flights count: " + e.getMessage());
        }
        return 0;
    }
    
    public static int getNewUsersThisMonth() {
        String query = "SELECT COUNT(*) FROM users WHERE user_type = 'customer' " +
                      "AND MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting new users this month: " + e.getMessage());
        }
        return 0;
    }
    
    // BOOKING ANALYTICS
    public static String getMostPopularDestination() {
        String query = "SELECT f.arrival_city, COUNT(b.booking_id) as booking_count " +
                      "FROM bookings b " +
                      "JOIN flights f ON b.flight_id = f.flight_id " +
                      "WHERE b.status = 'confirmed' " +
                      "GROUP BY f.arrival_city " +
                      "ORDER BY booking_count DESC " +
                      "LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getString("arrival_city");
            }
        } catch (SQLException e) {
            System.err.println("Error getting most popular destination: " + e.getMessage());
        }
        return "No data";
    }
    
    public static double getBookingSuccessRate() {
        String totalQuery = "SELECT COUNT(*) FROM bookings";
        String confirmedQuery = "SELECT COUNT(*) FROM bookings WHERE status = 'confirmed'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement totalStmt = conn.prepareStatement(totalQuery);
             PreparedStatement confirmedStmt = conn.prepareStatement(confirmedQuery)) {
            
            ResultSet totalRs = totalStmt.executeQuery();
            ResultSet confirmedRs = confirmedStmt.executeQuery();
            
            if (totalRs.next() && confirmedRs.next()) {
                int total = totalRs.getInt(1);
                int confirmed = confirmedRs.getInt(1);
                
                if (total > 0) {
                    return (double) confirmed / total * 100; // Return as percentage
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating booking success rate: " + e.getMessage());
        }
        return 0.0;
    }
    
    // SYSTEM HEALTH CHECKS
    public static boolean isDatabaseConnected() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    public static String getSystemStatus() {
        if (isDatabaseConnected()) {
            return "🟢 Online";
        } else {
            return "🔴 Database Error";
        }
    }
    
    // DATA EXPORT UTILITIES
    public static void logAdminAction(String adminEmail, String action) {
        String query = "INSERT INTO admin_logs (admin_email, action, timestamp) VALUES (?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, adminEmail);
            stmt.setString(2, action);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error logging admin action: " + e.getMessage());
        }
    }
    
    // DASHBOARD SUMMARY METHOD
    public static void printDashboardSummary() {
        System.out.println("=== JETSETGO ADMIN DASHBOARD SUMMARY ===");
        System.out.println("Total Users: " + getTotalUsersCount());
        System.out.println("Total Flights: " + getTotalFlightsCount());
        System.out.println("Total Bookings: " + getTotalBookingsCount());
        System.out.println("Total Revenue: $" + String.format("%.2f", calculateTotalRevenue()));
        System.out.println("Pending Bookings: " + getBookingCountByStatus("pending"));
        System.out.println("Confirmed Bookings: " + getBookingCountByStatus("confirmed"));
        System.out.println("Today's Bookings: " + getTodayBookingsCount());
        System.out.println("Weekly Revenue: $" + String.format("%.2f", calculateWeeklyRevenue()));
        System.out.println("System Status: " + getSystemStatus());
        System.out.println("========================================");
    }
}