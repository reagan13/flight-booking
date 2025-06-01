package application.ui.client;

import application.service.ChatService;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessagesScreenBuilder {
    
    private final MessagesEventHandler eventHandler;
    private VBox chatContainer;
    private TextField messageInput;
    
    public MessagesScreenBuilder(MessagesEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    /**
     * Setup the messages screen content (refactored from setupMessagesScreen)
     */
    public void setupMessagesContent(VBox messagesContent) {
        if (messagesContent == null) {
            System.err.println("messagesContent is null");
            return;
        }
        
        messagesContent.getChildren().clear();
        messagesContent.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 0;");
        
        // Chat header
        HBox header = createChatHeader();
        
        // Chat messages container
        ScrollPane chatScroll = createChatScrollPane();
        
        // Message input area
        HBox inputArea = createMessageInputArea();
        
        messagesContent.getChildren().addAll(header, chatScroll, inputArea);
        
        // Load existing messages
        loadMessages();
        
        // Auto-scroll to bottom
        Platform.runLater(() -> chatScroll.setVvalue(1.0));
    }
    
    /**
     * Create chat header (moved from setupMessagesScreen)
     */
    private HBox createChatHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #2196F3; -fx-padding: 15;");
        
        Label headerTitle = new Label("💬 Customer Support");
        headerTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label statusLabel = new Label("🟢 Online");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #E8F5E8;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(headerTitle, spacer, statusLabel);
        return header;
    }
    
    /**
     * Create chat scroll pane (moved from setupMessagesScreen)
     */
    private ScrollPane createChatScrollPane() {
        ScrollPane chatScroll = new ScrollPane();
        chatScroll.setFitToWidth(true);
        chatScroll.setStyle("-fx-background-color: #f5f5f5; -fx-background: #f5f5f5;");
        
        chatContainer = new VBox(10);
        chatContainer.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");
        chatScroll.setContent(chatContainer);
        
        VBox.setVgrow(chatScroll, Priority.ALWAYS);
        
        return chatScroll;
    }
    
    /**
     * Create message input area (moved from setupMessagesScreen)
     */
    private HBox createMessageInputArea() {
        HBox inputArea = new HBox(10);
        inputArea.setAlignment(Pos.CENTER);
        inputArea.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");
        
        messageInput = new TextField();
        messageInput.setPromptText("Type your message...");
        messageInput.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ddd; -fx-border-width: 1;");
        HBox.setHgrow(messageInput, Priority.ALWAYS);
        
        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 20;");
        
        // Send message handlers
        sendButton.setOnAction(e -> sendMessage());
        messageInput.setOnAction(e -> sendMessage());
        
        inputArea.getChildren().addAll(messageInput, sendButton);
        return inputArea;
    }
    
    /**
     * Send message (moved from HomeController)
     */
    private void sendMessage() {
        String messageText = messageInput.getText().trim();
        if (messageText.isEmpty()) return;
        
        // Clear input
        messageInput.clear();
        
        // Send message through service
        ChatService.sendMessage(messageText);
        
        // Reload messages
        loadMessages();
        
        // Notify event handler about new message
        eventHandler.onMessageSent(messageText);
    }
    
    /**
     * Load messages (moved from HomeController)
     */
    private void loadMessages() {
        if (chatContainer == null) return;
        
        chatContainer.getChildren().clear();
        
        List<ChatService.Message> messages = ChatService.getUserMessages();
        
        if (messages.isEmpty()) {
            // Show welcome message
            VBox welcomeBox = createMessageBubble("👋 Hello! Welcome to JetSetGo customer support. How can I help you today?", "bot");
            chatContainer.getChildren().add(welcomeBox);
        } else {
            for (ChatService.Message message : messages) {
                VBox bubble = createMessageBubble(message.getText(), message.getSenderType());
                chatContainer.getChildren().add(bubble);
            }
        }
    }
    
    /**
     * Create message bubble (moved from HomeController)
     */
    private VBox createMessageBubble(String text, String senderType) {
        VBox bubble = new VBox(5);

        boolean isUser = "user".equals(senderType);
        bubble.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(250);

        if (isUser) {
            messageLabel.setStyle(
                    "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 12; -fx-background-radius: 18 18 4 18; -fx-font-size: 13px;");
        } else {
            String bgColor = "bot".equals(senderType) ? "#e0e0e0" : "#4CAF50";
            String textColor = "bot".equals(senderType) ? "#333" : "white";
            messageLabel.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor
                    + "; -fx-padding: 12; -fx-background-radius: 18 18 18 4; -fx-font-size: 13px;");
        }

        Label timeLabel = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");
        timeLabel.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        bubble.getChildren().addAll(messageLabel, timeLabel);
        return bubble;
    }
    
    /**
     * Auto-scroll chat to bottom
     */
    public void scrollToBottom(ScrollPane chatScroll) {
        Platform.runLater(() -> chatScroll.setVvalue(1.0));
    }
    
    /**
     * Refresh messages and scroll to bottom
     */
    public void refreshMessages(ScrollPane chatScroll) {
        loadMessages();
        scrollToBottom(chatScroll);
    }
    
    /**
     * Interface for handling messages screen events
     */
    public interface MessagesEventHandler {
        void onMessageSent(String message);
        void onRefreshMessages();
    }
}