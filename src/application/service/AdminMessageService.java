package application.service;

import application.model.Message;
import application.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AdminMessageService {
    
    // Track automation settings per user
    private static Map<Integer, Boolean> automationSettings = new HashMap<>();
    
    public static ObservableList<Message> getAllConversations() {
        ObservableList<Message> conversations = FXCollections.observableArrayList();
        String query = "SELECT m.*, u.first_name, u.last_name, u.email, " +
                      "(SELECT COUNT(*) FROM messages m2 WHERE m2.user_id = m.user_id AND m2.is_read = FALSE AND m2.sender_type = 'user') as unread_count " +
                      "FROM messages m " +
                      "LEFT JOIN users u ON m.user_id = u.id " +
                      "WHERE m.id IN (" +
                      "    SELECT MAX(id) FROM messages " +
                      "    WHERE user_id IS NOT NULL " +
                      "    GROUP BY user_id" +
                      ") " +
                      "ORDER BY m.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Message message = mapResultSetToMessage(rs);
                conversations.add(message);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching conversations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return conversations;
    }
    
    public static ObservableList<Message> getConversationMessages(int userId) {
        ObservableList<Message> messages = FXCollections.observableArrayList();
        String query = "SELECT m.*, u.first_name, u.last_name, u.email " +
                      "FROM messages m " +
                      "LEFT JOIN users u ON m.user_id = u.id " +
                      "WHERE m.user_id = ? " +
                      "ORDER BY m.created_at ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Message message = mapResultSetToMessage(rs);
                messages.add(message);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching conversation messages: " + e.getMessage());
            e.printStackTrace();
        }
        
        return messages;
    }
    
    private static Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setUserId(rs.getInt("user_id"));
        message.setMessageText(rs.getString("message_text"));
        message.setSenderType(rs.getString("sender_type"));
        message.setIsRead(rs.getBoolean("is_read"));
        message.setReplyTo(rs.getInt("reply_to"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            message.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        // Set user info
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        if (firstName != null && lastName != null) {
            message.setUserName(firstName + " " + lastName);
        } else {
            message.setUserName("Guest User");
        }
        message.setUserEmail(rs.getString("email"));
        
        // Set unread count if available
        try {
            message.setUnreadCount(rs.getInt("unread_count"));
        } catch (SQLException e) {
            // Column might not exist in all queries
            message.setUnreadCount(0);
        }
        
        return message;
    }
    
    public static boolean sendMessage(int userId, String messageText, String senderType, Integer replyTo) {
        String query = "INSERT INTO messages (user_id, message_text, sender_type, reply_to, created_at) VALUES (?, ?, ?, ?, NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, messageText);
            stmt.setString(3, senderType);
            if (replyTo != null) {
                stmt.setInt(4, replyTo);
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean markMessagesAsRead(int userId) {
        String query = "UPDATE messages SET is_read = TRUE WHERE user_id = ? AND sender_type = 'user'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected >= 0; // Even 0 is okay (no unread messages)
            
        } catch (SQLException e) {
            System.err.println("Error marking messages as read: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean isAutomationEnabled(int userId) {
        return automationSettings.getOrDefault(userId, true); // Default is enabled
    }
    
    public static void setAutomationEnabled(int userId, boolean enabled) {
        automationSettings.put(userId, enabled);
        System.out.println("Automation for user " + userId + " set to: " + enabled);
    }
    
    public static void sendAutomatedReply(int userId, String userMessage) {
        if (!isAutomationEnabled(userId)) {
            return; // Don't send automated reply if disabled
        }
        
        String automatedResponse = generateAutomatedResponse(userMessage);
        sendMessage(userId, automatedResponse, "bot", null);
    }
    
    private static String generateAutomatedResponse(String userMessage) {
        String message = userMessage.toLowerCase();
        
        if (message.contains("booking") || message.contains("reservation")) {
            return "Hi! Thank you for contacting us about your booking. Our support team will assist you shortly. You can check your booking status in the 'My Bookings' section of your account.";
        } else if (message.contains("cancel") || message.contains("refund")) {
            return "We understand you'd like to cancel or get a refund. Please note that cancellation policies vary by fare type. An admin will review your request and get back to you soon.";
        } else if (message.contains("flight") || message.contains("schedule")) {
            return "For flight-related inquiries, please check our real-time flight status or contact our support team. We're here to help with any schedule changes or flight information.";
        } else if (message.contains("payment") || message.contains("charge")) {
            return "For payment-related concerns, please provide your booking reference and we'll investigate immediately. Our team will verify the transaction details for you.";
        } else if (message.contains("help") || message.contains("support")) {
            return "We're here to help! Please describe your issue in detail and our support team will assist you. You can also check our FAQ section for common questions.";
        } else {
            return "Thank you for your message! Our support team has received your inquiry and will respond within 24 hours. For urgent matters, please call our hotline.";
        }
    }
    
    public static int getUnreadMessageCount() {
        String query = "SELECT COUNT(*) FROM messages WHERE sender_type = 'user' AND is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unread message count: " + e.getMessage());
        }
        
        return 0;
    }
    
    public static int getUnreadMessageCountForUser(int userId) {
        String query = "SELECT COUNT(*) FROM messages WHERE user_id = ? AND sender_type = 'user' AND is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unread message count for user: " + e.getMessage());
        }
        
        return 0;
    }
    
    public static boolean deleteMessage(int messageId) {
        String query = "DELETE FROM messages WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, messageId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}