package application.service;

import application.database.DatabaseConnection;
import application.model.UserSession;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatService {
    
    public static class Message {
        private int id;
        private String text;
        private String senderType;
        private LocalDateTime timestamp;
        
        public Message(int id, String text, String senderType, LocalDateTime timestamp) {
            this.id = id;
            this.text = text;
            this.senderType = senderType;
            this.timestamp = timestamp;
        }
        
        public int getId() { return id; }
        public String getText() { return text; }
        public String getSenderType() { return senderType; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    // Send user message and get bot reply
    public static void sendMessage(String messageText) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Insert user message
            String sql = "INSERT INTO messages (user_id, message_text, sender_type) VALUES (?, ?, 'user')";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            if (UserSession.getInstance().isLoggedIn()) {
                stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, messageText);
            stmt.executeUpdate();
            
            // Get the message ID
            ResultSet rs = stmt.getGeneratedKeys();
            int messageId = 0;
            if (rs.next()) {
                messageId = rs.getInt(1);
            }
            
            // Generate and send bot reply (if no admin intervenes)
            String botReply = generateBotReply(messageText);
            sendBotReply(messageId, botReply);
            
        } catch (SQLException e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Simple bot that replies to common questions
    private static String generateBotReply(String userMessage) {
        String message = userMessage.toLowerCase();
        
        if (message.contains("book") || message.contains("reservation")) {
            return "I can help you book a flight! Go to the Home tab to search for available flights. Is there a specific destination you're looking for?";
        } else if (message.contains("cancel") || message.contains("refund")) {
            return "For cancellations and refunds, please check your booking details in the Bookings tab. Refund policies vary by airline. Would you like me to connect you with a support agent?";
        } else if (message.contains("payment") || message.contains("card")) {
            return "We accept various payment methods including Credit/Debit cards, GCash, Maya, PayPal, and Bank Transfer. All payments are processed securely. Do you have a specific payment question?";
        } else if (message.contains("flight") || message.contains("schedule")) {
            return "You can check flight schedules and availability on our Home page. We have flights to major destinations across the Philippines and internationally. What route are you interested in?";
        } else if (message.contains("seat") || message.contains("baggage")) {
            return "Seat selection is available during booking. For baggage allowances and policies, this depends on your chosen airline and fare type. Would you like specific information about a booking?";
        } else if (message.contains("hello") || message.contains("hi") || message.contains("help")) {
            return "Hello! Welcome to JetSetGo support. I'm here to help with your travel needs. You can ask me about bookings, payments, flights, or anything else. How can I assist you today?";
        } else if (message.contains("thank")) {
            return "You're welcome! Is there anything else I can help you with today? I'm here 24/7 for your travel assistance.";
        } else {
            return "I understand you're asking about: \"" + userMessage + "\". Let me connect you with a support agent who can provide more detailed assistance. In the meantime, you can check our FAQ or browse flights on the Home tab.";
        }
    }
    
    private static void sendBotReply(int replyToId, String replyText) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO messages (user_id, message_text, sender_type, reply_to) VALUES (?, ?, 'bot', ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            if (UserSession.getInstance().isLoggedIn()) {
                stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, replyText);
            stmt.setInt(3, replyToId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error sending bot reply: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Get messages for current user
    public static List<Message> getUserMessages() {
        List<Message> messages = new ArrayList<>();
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT id, message_text, sender_type, created_at FROM messages WHERE user_id = ? OR user_id IS NULL ORDER BY created_at ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            if (UserSession.getInstance().isLoggedIn()) {
                stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            } else {
                stmt.setInt(1, -1); // Get guest messages
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message message = new Message(
                    rs.getInt("id"),
                    rs.getString("message_text"),
                    rs.getString("sender_type"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                messages.add(message);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching messages: " + e.getMessage());
            e.printStackTrace();
        }
        
        return messages;
    }
}