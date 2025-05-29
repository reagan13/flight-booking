package application.service;

import application.database.DatabaseConnection;
import application.model.UserSession;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    
    public static class Notification {
        private int id;
        private String type;
        private String title;
        private String message;
        private int relatedId;
        private boolean isRead;
        private LocalDateTime createdAt;
        
        public Notification(int id, String type, String title, String message, int relatedId, boolean isRead, LocalDateTime createdAt) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.message = message;
            this.relatedId = relatedId;
            this.isRead = isRead;
            this.createdAt = createdAt;
        }
        
        // Getters
        public int getId() { return id; }
        public String getType() { return type; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public int getRelatedId() { return relatedId; }
        public boolean isRead() { return isRead; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
    
    // Create booking notification when booking is confirmed
    public static void createBookingNotification(int userId, String bookingReference, int bookingId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO notifications (user_id, type, title, message, related_id) VALUES (?, 'booking', ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, userId);
            stmt.setString(2, "Booking Confirmed ✈️");
            stmt.setString(3, "Your flight booking " + bookingReference + " has been confirmed! Have a great trip.");
            stmt.setInt(4, bookingId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error creating booking notification: " + e.getMessage());
        }
    }
    
    // Create message notification when new message arrives
    public static void createMessageNotification(int userId, String senderType, int messageId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO notifications (user_id, type, title, message, related_id) VALUES (?, 'message', ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            String title = "bot".equals(senderType) ? "New Message 🤖" : "Support Reply 👨‍💼";
            String message = "bot".equals(senderType) ? 
                "You have a new automated response from our support bot." :
                "A support agent has replied to your message.";
            
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.setInt(4, messageId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error creating message notification: " + e.getMessage());
        }
    }
    
    // Get all notifications for user
    public static List<Notification> getUserNotifications() {
        List<Notification> notifications = new ArrayList<>();
        
        if (!UserSession.getInstance().isLoggedIn()) {
            return notifications;
        }
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification(
                    rs.getInt("id"),
                    rs.getString("type"),
                    rs.getString("title"),
                    rs.getString("message"),
                    rs.getInt("related_id"),
                    rs.getBoolean("is_read"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                notifications.add(notification);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
        }
        
        return notifications;
    }
    
    // Get unread notification count
    public static int getUnreadCount() {
        if (!UserSession.getInstance().isLoggedIn()) {
            return 0;
        }
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unread count: " + e.getMessage());
        }
        
        return 0;
    }
    
    // Mark notification as read
    public static void markAsRead(int notificationId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, notificationId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
        }
    }
    
    // Mark all notifications as read
    public static void markAllAsRead() {
        if (!UserSession.getInstance().isLoggedIn()) {
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error marking all notifications as read: " + e.getMessage());
        }
    }

    public static void createPendingBookingNotification(int userId, String bookingReference, int bookingId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO notifications (user_id, type, title, message, related_id) VALUES (?, 'booking', ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, userId);
            stmt.setString(2, "Booking Submitted ⏳");
            stmt.setString(3, "Your booking " + bookingReference
                    + " has been submitted and is pending admin approval. You'll be notified once it's confirmed.");
            stmt.setInt(4, bookingId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error creating pending booking notification: " + e.getMessage());
        }
    }

    public static void createBookingApprovedNotification(int userId, String bookingReference, int bookingId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO notifications (user_id, type, title, message, related_id) VALUES (?, 'booking', ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, userId);
            stmt.setString(2, "Booking Confirmed ✅");
            stmt.setString(3, "Great news! Your booking " + bookingReference
                    + " has been approved and confirmed. Have a great trip!");
            stmt.setInt(4, bookingId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error creating booking approved notification: " + e.getMessage());
        }
    }
    public static void createBookingRejectedNotification(int userId, String bookingReference, int bookingId, String reason) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO notifications (user_id, type, title, message, related_id) VALUES (?, 'booking', ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, userId);
            stmt.setString(2, "Booking Rejected ❌");
            stmt.setString(3, "Unfortunately, your booking " + bookingReference + " has been rejected. Reason: " + reason + ". Please contact support for assistance.");
            stmt.setInt(4, bookingId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error creating booking rejected notification: " + e.getMessage());
        }
    }
}