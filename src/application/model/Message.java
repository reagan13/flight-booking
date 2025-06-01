package application.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message extends BaseEntity { // INHERITANCE
    // ENCAPSULATION - private fields
    private int userId;
    private String messageText;
    private String senderType;
    private boolean isRead;
    private int replyTo;
    
    // Additional display properties
    private String userName;
    private String userEmail;
    private int unreadCount;
    
    // Constructors
    public Message() {
        super();
    }
    
    public Message(int userId, String messageText, String senderType) {
        super();
        this.userId = userId;
        this.messageText = messageText;
        this.senderType = senderType;
        this.isRead = false;
    }
    
    // ENCAPSULATION - controlled access through getters/setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { 
        this.userId = userId; 
        updateTimestamp();
    }
    
    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { 
        this.messageText = messageText; 
        updateTimestamp();
    }
    
    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { 
        this.senderType = senderType; 
        updateTimestamp();
    }
    
    public boolean getIsRead() { return isRead; }
    public void setIsRead(boolean isRead) { 
        this.isRead = isRead; 
        updateTimestamp();
    }
    
    public int getReplyTo() { return replyTo; }
    public void setReplyTo(int replyTo) { 
        this.replyTo = replyTo; 
        updateTimestamp();
    }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    
    // Business methods
    public String getFormattedDateTime() {
        if (createdAt == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return createdAt.format(formatter);
    }
    
    public String getPreviewText() {
        if (messageText == null) return "";
        return messageText.length() > 50 ? messageText.substring(0, 50) + "..." : messageText;
    }
    
    // POLYMORPHISM - implementing abstract method
    @Override
    public String getDisplayName() {
        return userName + " - " + getPreviewText();
    }
}