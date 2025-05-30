package application.service;

import application.database.DatabaseConnection;
import application.service.AdminMessageService; // Add this import

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
    
    // Updated sendMessage method with automation check
    public static void sendMessage(String messageText) {
        try {
            if (!UserSession.getInstance().isLoggedIn()) {
                System.err.println("User not logged in, cannot send message");
                return;
            }
            
            int userId = UserSession.getInstance().getCurrentUser().getId();
            
            // Use AdminMessageService to handle the message with automation check
            boolean messageSent = AdminMessageService.handleIncomingUserMessage(userId, messageText);
            
            if (messageSent) {
                System.out.println("Message sent successfully by user " + userId);
            } else {
                System.err.println("Failed to send message for user " + userId);
            }
            
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Keep this method for backward compatibility, but add automation check
    public static void sendMessageOldWay(String messageText) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // Insert user message
            String sql = "INSERT INTO messages (user_id, message_text, sender_type) VALUES (?, ?, 'user')";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            int userId = 0;
            if (UserSession.getInstance().isLoggedIn()) {
                userId = UserSession.getInstance().getCurrentUser().getId();
                stmt.setInt(1, userId);
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

            // Check if automation is enabled before sending bot reply
            if (userId > 0 && AdminMessageService.isAutomationEnabled(userId)) {
                System.out.println("Automation enabled for user " + userId + ", sending bot reply");
                // Use the same bot reply generator
                String botReply = generateBotReply(messageText);
                sendBotReply(userId, messageId, botReply);
            } else {
                System.out.println("Automation disabled for user " + userId + ", no bot reply sent");
            }

        } catch (SQLException e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Method to check if automation is enabled for current user
    public static boolean isAutomationEnabled() {
        try {
            if (!UserSession.getInstance().isLoggedIn()) {
                return true; // Default for guest users
            }
            
            int userId = UserSession.getInstance().getCurrentUser().getId();
            return AdminMessageService.isAutomationEnabled(userId);
            
        } catch (Exception e) {
            System.err.println("Error checking automation status: " + e.getMessage());
            return true; // Default to enabled on error
        }
    }
    
    public static String generateBotReply(String userMessage) {
        String message = userMessage.toLowerCase();

        if (containsBadWords(message)) {
            return "⚠️ Language Policy Reminder\n\n" +
                    "We understand you might be frustrated, but please keep our chat respectful and professional.\n\n" +
                    "Our team is here to help resolve any issues you're experiencing. Let's work together to find a solution.\n\n"
                    +
                    "How can we assist you with your travel needs today? 🤝";
        }

        // Automated Response 1: Booking & Reservations
        if (message.contains("book") || message.contains("reservation") || message.contains("reserve")) {
            return "🛫 I can help you book a flight! Here's how:\n\n" +
                    "1. Go to the Home tab\n" +
                    "2. Search for your destination\n" +
                    "3. Select your preferred flight\n" +
                    "4. Fill in passenger details\n" +
                    "5. Complete payment\n\n" +
                    "Need help with a specific route? Just tell me where you want to go! ✈️";
        }

        // Automated Response 2: Payment & Billing
        else if (message.contains("payment") || message.contains("pay") || message.contains("card") ||
                message.contains("billing") || message.contains("cost") || message.contains("price")) {
            return "💳 Payment Information:\n\n" +
                    "We accept:\n" +
                    "• Credit/Debit Cards (2.5% fee)\n" +
                    "• GCash (1% fee)\n" +
                    "• Maya/PayMaya (1% fee)\n" +
                    "• PayPal (3.4% fee)\n" +
                    "• Bank Transfer (No fee)\n\n" +
                    "All payments are 100% secure and encrypted. Need help with a specific payment method?";
        }

        // Automated Response 3: Flight Status & Changes
        else if (message.contains("cancel") || message.contains("change") || message.contains("reschedule") ||
                message.contains("refund") || message.contains("modify")) {
            return "📋 Flight Changes & Cancellations:\n\n" +
                    "• Check your booking in the 'Bookings' tab\n" +
                    "• Free changes up to 24 hours before departure\n" +
                    "• Cancellation fees may apply based on fare type\n" +
                    "• Refunds processed within 7-14 business days\n\n" +
                    "Have your booking reference ready? I can connect you with our support team for immediate assistance.";
        }

        // Automated Response 4: Check-in & Travel Requirements
        else if (message.contains("check") || message.contains("checkin") || message.contains("baggage") ||
                message.contains("luggage") || message.contains("document") || message.contains("id")) {
            return "📝 Check-in & Travel Guide:\n\n" +
                    "✅ Check-in: Online 24hrs before, Airport 3hrs before\n" +
                    "✅ Documents: Valid ID + Passport (international)\n" +
                    "✅ Baggage: 7kg carry-on, 20kg checked (economy)\n" +
                    "✅ Arrive: 2hrs early (domestic), 3hrs (international)\n\n" +
                    "Traveling internationally? Make sure your passport is valid for 6+ months!";
        }

        // Automated Response 5: General Support & Contact
        else if (message.contains("help") || message.contains("support") || message.contains("contact") ||
                message.contains("agent") || message.contains("human") || message.contains("representative")) {
            return "🆘 JetSetGo Customer Support:\n\n" +
                    "I'm here 24/7 for quick help! For complex issues:\n\n" +
                    "📞 Hotline: +63 2 8888-JSET (5738)\n" +
                    "📧 Email: support@jetsetgo.ph\n" +
                    "💬 Live Chat: Available 8AM-10PM\n" +
                    "🏢 Airport Counter: 2hrs before departure\n\n" +
                    "Would you like me to connect you with a live agent now?";
        }

        // Default greeting responses
        else if (message.contains("hello") || message.contains("hi") || message.contains("good")) {
            return "👋 Hello there! Welcome to JetSetGo!\n\n" +
                    "I'm your virtual travel assistant. I can help you with:\n" +
                    "✈️ Flight bookings\n" +
                    "💳 Payment questions\n" +
                    "📋 Booking changes\n" +
                    "📝 Travel requirements\n" +
                    "🆘 General support\n\n" +
                    "What can I help you with today?";
        }

        // Thank you responses
        else if (message.contains("thank") || message.contains("thanks")) {
            return "😊 You're very welcome!\n\n" +
                    "Happy to help make your travel experience smooth and enjoyable! If you need anything else, just ask.\n\n"
                    +
                    "Have a wonderful flight with JetSetGo! ✈️🌟";
        }

        // Default fallback response
        else {
            return "🤔 I understand you're asking about: \"" + userMessage + "\"\n\n" +
                    "Let me connect you with one of our travel specialists who can provide detailed assistance.\n\n" +
                    "In the meantime, you can:\n" +
                    "• Browse flights on the Home tab\n" +
                    "• Check your bookings\n" +
                    "• Call our hotline: +63 2 8888-JSET\n\n" +
                    "A live agent will be with you shortly! 👨‍💼👩‍💼";
        }
    }
    
    private static boolean containsBadWords(String message) {
        String[] badWords = {
                "fuck", "fck", "f*ck", "f**k",
                "shit", "sht", "s*it", "s**t",
                "damn", "dmn", "d*mn",
                "bitch", "btch", "b*tch",
                "ass", "a**", "a*s",
                "stupid", "idiot", "moron", "dumb",
                "hate", "suck", "sucks", "worst",
                "trash", "garbage", "crap",
                "bastard", "jerk", "loser",
                "wtf", "omg", "hell",
                "piss", "pissed", "angry",
                "terrible", "horrible", "awful"
        };

        String lowerMessage = message.toLowerCase();

        for (String badWord : badWords) {
            if (lowerMessage.contains(badWord)) {
                return true;
            }
        }

        return false;
    }
    
    private static void sendBotReply(int userId, int replyToId, String replyText) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO messages (user_id, message_text, sender_type, reply_to) VALUES (?, ?, 'bot', ?)";
            // Add Statement.RETURN_GENERATED_KEYS here too
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, userId);
            stmt.setString(2, replyText);
            stmt.setInt(3, replyToId);
            stmt.executeUpdate();
            
            // Create notification for bot reply
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int messageId = rs.getInt(1);
                // This will now show "Automated Response 🤖"
                NotificationService.createMessageNotification(userId, "bot", messageId);
            }
            
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
    
    // New method to get automation status text for UI
    public static String getAutomationStatusText() {
        boolean isEnabled = isAutomationEnabled();
        return isEnabled ? "🤖 Auto-replies: ON" : "🤖 Auto-replies: OFF (Admin disabled)";
    }
    
    // New method to get automation status color for UI
    public static String getAutomationStatusColor() {
        boolean isEnabled = isAutomationEnabled();
        return isEnabled ? "green" : "red";
    }
}