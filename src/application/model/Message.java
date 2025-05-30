package application.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private int id;
    private int userId;
    private String messageText;
    private String senderType; // 'user', 'bot', 'admin'
    private boolean isRead;
    private int replyTo;
    private LocalDateTime createdAt;
    
    // Additional display properties
    private String userName;
    private String userEmail;
    private int unreadCount;
    
    // Constructors
    public Message() {}
    
    public Message(int userId, String messageText, String senderType) {
        this.userId = userId;
        this.messageText = messageText;
        this.senderType = senderType;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }
    
    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }
    
    public boolean getIsRead() { return isRead; }
    public void setIsRead(boolean isRead) { this.isRead = isRead; }
    
    public int getReplyTo() { return replyTo; }
    public void setReplyTo(int replyTo) { this.replyTo = replyTo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    
    // Formatted display methods
    public String getFormattedDateTime() {
        if (createdAt == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return createdAt.format(formatter);
    }
    
    public String getFormattedTime() {
        if (createdAt == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return createdAt.format(formatter);
    }
    
    public String getSenderTypeColor() {
        switch (senderType.toLowerCase()) {
            case "admin":
                return "#007bff"; // Blue
            case "bot":
                return "#6c757d"; // Gray
            case "user":
            default:
                return "#28a745"; // Green
        }
    }
    
    public String getSenderTypeIcon() {
        switch (senderType.toLowerCase()) {
            case "admin":
                return "👨‍💼";
            case "bot":
                return "🤖";
            case "user":
            default:
                return "👤";
        }
    }
    
    public String getPreviewText() {
        if (messageText == null) return "";
        return messageText.length() > 50 ? messageText.substring(0, 50) + "..." : messageText;
    }
}