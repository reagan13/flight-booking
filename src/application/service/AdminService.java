package application.service;

import application.database.DatabaseConnection;
import java.sql.*;

public class AdminService {
    
    public static int getTotalUsersCount() {
        String query = "SELECT COUNT(*) as count FROM users";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("✅ Total Users: " + count);
                return count;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total users count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static int getTotalFlightsCount() {
        String query = "SELECT COUNT(*) as count FROM flights";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("✅ Total Flights: " + count);
                return count;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total flights count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static int getTotalBookingsCount() {
        String query = "SELECT COUNT(*) as count FROM bookings";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("✅ Total Bookings: " + count);
                return count;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total bookings count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static int getBookingCountByStatus(String status) {
        String query = "SELECT COUNT(*) as count FROM bookings WHERE status = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("✅ " + status + " Bookings: " + count);
                    return count;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting booking count by status '" + status + "': " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static double calculateTotalRevenue() {
        // Calculate revenue from paid transactions
        String query = "SELECT COALESCE(SUM(total_amount), 0.0) as total_revenue " +
                       "FROM transactions " +
                       "WHERE payment_status IN ('paid', 'completed')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                double revenue = rs.getDouble("total_revenue");
                System.out.println("✅ Total Revenue: ₱" + String.format("%.2f", revenue));
                return revenue;
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total revenue: " + e.getMessage());
            
            // Fallback: calculate from confirmed bookings if transactions table is empty
            try {
                String fallbackQuery = "SELECT COALESCE(SUM(b.total_amount), 0.0) as total_revenue " +
                                      "FROM bookings b " +
                                      "WHERE b.status = 'confirmed'";
                
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(fallbackQuery);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    if (rs.next()) {
                        double revenue = rs.getDouble("total_revenue");
                        System.out.println("✅ Total Revenue (fallback from bookings): ₱" + String.format("%.2f", revenue));
                        return revenue;
                    }
                }
            } catch (SQLException e2) {
                System.err.println("Fallback revenue calculation failed: " + e2.getMessage());
            }
        }
        
        return 0.0;
    }
    
    public static int getTotalMessagesCount() {
        String query = "SELECT COUNT(*) as count FROM messages";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("✅ Total Messages: " + count);
                return count;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total messages count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static int getUnreadMessagesCount() {
        String query = "SELECT COUNT(*) as count FROM messages WHERE is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("✅ Unread Messages: " + count);
                return count;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unread messages count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static int getTodayBookingsCount() {
        String query = "SELECT COUNT(*) as count FROM bookings WHERE DATE(booking_date) = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("✅ Today's Bookings: " + count);
                return count;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting today's bookings count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static String getSystemStatus() {
        try {
            // Check database connection and basic system health
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    // Test a simple query to ensure database is responsive
                    try (PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                         ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return "🟢 Online";
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("System health check failed: " + e.getMessage());
        }
        
        return "🔴 Offline";
    }
    
    public static int getNewUsersThisMonth() {
        String query = "SELECT COUNT(*) as count FROM users WHERE MONTH(created_at) = MONTH(CURRENT_DATE()) AND YEAR(created_at) = YEAR(CURRENT_DATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("✅ New Users This Month: " + count);
                return count;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting new users this month: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
}