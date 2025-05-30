package application.service;

import application.model.Message;
import application.database.DatabaseConnection;
import javafx.collections.FXCollections;
import application.service.ChatService;
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
    
    public static boolean sendMessage(int userId, String messageText, String senderType, Integer replyToId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO messages (user_id, message_text, sender_type, reply_to) VALUES (?, ?, ?, ?)";
            // ADD THIS LINE - that's the only issue
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, userId);
            stmt.setString(2, messageText);
            stmt.setString(3, senderType);
            if (replyToId != null) {
                stmt.setInt(4, replyToId);
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int messageId = generatedKeys.getInt(1);

                    // Create notification
                    if ("admin".equals(senderType) || "bot".equals(senderType)) {
                        NotificationService.createMessageNotification(userId, senderType, messageId);
                    }
                }
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error sending message: " + e.getMessage());
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
        String query = "SELECT automation_enabled FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("automation_enabled");
            } else {
                // User not found, default to enabled
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking automation setting: " + e.getMessage());
            return true; // Default to enabled on error
        }
    }
    
    public static void setAutomationEnabled(int userId, boolean enabled) {
        String query = "UPDATE users SET automation_enabled = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setBoolean(1, enabled);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Automation setting updated for user " + userId + ": " + enabled);
            } else {
                System.err.println("User not found with ID: " + userId);
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating automation setting: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    public static void sendAutomatedReply(int userId, String userMessage) {
        // Check if automation is enabled for this user
        if (!isAutomationEnabled(userId)) {
            System.out.println("Automation disabled for user " + userId + ", skipping automated reply");
            return; // Don't send automated reply if disabled
        }
        
        // Use ChatService's bot response generator (more comprehensive)
        String automatedResponse = ChatService.generateBotReply(userMessage);
        boolean sent = sendMessage(userId, automatedResponse, "bot", null);
        
        if (sent) {
            System.out.println("Automated reply sent to user " + userId);
        } else {
            System.err.println("Failed to send automated reply to user " + userId);
        }
    }
    
    public static boolean handleIncomingUserMessage(int userId, String messageText) {
        try {
            // First, save the user's message
            boolean messageSaved = sendMessage(userId, messageText, "user", null);

            if (messageSaved) {
                // Check if automation is enabled for this user
                if (isAutomationEnabled(userId)) {
                    System.out.println("Automation enabled for user " + userId + ", sending auto-reply");
                    // Send automated reply after a small delay to make it feel more natural
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000); // 1 second delay
                            sendAutomatedReply(userId, messageText);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                } else {
                    System.out.println("Automation disabled for user " + userId + ", no auto-reply sent");
                }
                return true;
            }

            return false;

        } catch (Exception e) {
            System.err.println("Error handling incoming user message: " + e.getMessage());
            e.printStackTrace();
            return false;
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