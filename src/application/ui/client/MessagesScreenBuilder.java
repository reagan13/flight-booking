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
    private ScrollPane chatScrollPane;
    
    public MessagesScreenBuilder(MessagesEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    /**
     * Create messages screen content - matches other builders' pattern
     */
    public VBox createMessagesScreenContent() {
        VBox container = new VBox(0);
        container.setStyle("-fx-background-color: #f5f5f5;");
        
        // Header section (same style as PaymentScreenBuilder)
        VBox headerSection = createHeaderSection();
        
        // Chat content section
        VBox chatSection = createChatSection();
        
        // Input section
        VBox inputSection = createInputSection();
        
        container.getChildren().addAll(headerSection, chatSection, inputSection);
        
        // Load existing messages
        loadMessages();
        
        // Auto-scroll to bottom
        Platform.runLater(() -> {
            if (chatScrollPane != null) {
                chatScrollPane.setVvalue(1.0);
            }
        });
        
        return container;
    }
    
    /**
     * Create header section - matches other builders' header style
     */
    private VBox createHeaderSection() {
        VBox headerSection = new VBox();
        headerSection.setStyle(
            "-fx-background-color: white; -fx-border-color: #e0e0e0; " +
            "-fx-border-width: 0 0 1 0; -fx-padding: 15;"
        );
        
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        // Support icon and title
        Label supportIcon = new Label("💬");
        supportIcon.setStyle("-fx-font-size: 20px;");
        
        VBox titleBox = new VBox(2);
        Label titleLabel = new Label("Customer Support");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label subtitleLabel = new Label("We're here to help you 24/7");
        subtitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Status badge (matches other builders' badge style)
        Label statusBadge = createStatusBadge();
        
        headerBox.getChildren().addAll(supportIcon, titleBox, spacer, statusBadge);
        headerSection.getChildren().add(headerBox);
        
        return headerSection;
    }
    
    /**
     * Create status badge - matches booking status badge style
     */
    private Label createStatusBadge() {
        Label statusBadge = new Label("🟢 Online");
        statusBadge.setStyle(
            "-fx-background-color: #E8F5E8; -fx-text-fill: #4CAF50; " +
            "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 8; " +
            "-fx-background-radius: 10; -fx-border-color: #4CAF50; " +
            "-fx-border-width: 1; -fx-border-radius: 10;"
        );
        return statusBadge;
    }
    
    /**
     * Create chat section with scrollable messages
     */
    private VBox createChatSection() {
        VBox chatSection = new VBox();
        VBox.setVgrow(chatSection, Priority.ALWAYS);
        
        chatScrollPane = new ScrollPane();
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setStyle(
            "-fx-background-color: #f5f5f5; -fx-background: #f5f5f5; " +
            "-fx-border-color: transparent;"
        );
        
        chatContainer = new VBox(12);
        chatContainer.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");
        chatScrollPane.setContent(chatContainer);
        
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);
        chatSection.getChildren().add(chatScrollPane);
        
        return chatSection;
    }
    
    /**
     * Create input section - matches other builders' action section style
     */
    private VBox createInputSection() {
        VBox inputSection = new VBox(10);
        inputSection.setStyle(
            "-fx-background-color: white; -fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1 0 0 0; -fx-padding: 15;"
        );
        
        // Quick reply options (new feature matching your design)
        HBox quickReplies = createQuickRepliesSection();
        
        // Message input area
        HBox inputArea = createMessageInputArea();
        
        inputSection.getChildren().addAll(quickReplies, inputArea);
        return inputSection;
    }
    
    /**
     * Create quick replies section - matches detail row style
     */
    private HBox createQuickRepliesSection() {
        HBox quickReplies = new HBox(8);
        quickReplies.setAlignment(Pos.CENTER_LEFT);
        
        Label quickLabel = new Label("Quick:");
        quickLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        
        Button flightInfoBtn = createQuickReplyButton("✈️ Flight Info");
        Button bookingBtn = createQuickReplyButton("📝 Booking Help");
        Button refundBtn = createQuickReplyButton("💰 Refund");
        
        quickReplies.getChildren().addAll(quickLabel, flightInfoBtn, bookingBtn, refundBtn);
        return quickReplies;
    }
    
    /**
     * Create quick reply button - matches chip style
     */
    private Button createQuickReplyButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #f8f9fa; -fx-text-fill: #666; " +
            "-fx-font-size: 10px; -fx-padding: 4 8; -fx-background-radius: 12; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 12; " +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #e3f2fd; -fx-text-fill: #2196F3; " +
            "-fx-font-size: 10px; -fx-padding: 4 8; -fx-background-radius: 12; " +
            "-fx-border-color: #2196F3; -fx-border-width: 1; -fx-border-radius: 12; " +
            "-fx-cursor: hand;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #f8f9fa; -fx-text-fill: #666; " +
            "-fx-font-size: 10px; -fx-padding: 4 8; -fx-background-radius: 12; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 12; " +
            "-fx-cursor: hand;"
        ));
        
        button.setOnAction(e -> {
            messageInput.setText(text.substring(2)); // Remove emoji prefix
            sendMessage();
            refreshMessages();
        });
        
        return button;
    }
    
    /**
     * Create message input area - updated styling
     */
    private HBox createMessageInputArea() {
        HBox inputArea = new HBox(10);
        inputArea.setAlignment(Pos.CENTER);
        
        messageInput = new TextField();
        messageInput.setPromptText("Type your message...");
        messageInput.setStyle(
            "-fx-font-size: 12px; -fx-padding: 10; -fx-background-radius: 20; " +
            "-fx-border-radius: 20; -fx-border-color: #e0e0e0; -fx-border-width: 1; " +
            "-fx-background-color: #f8f9fa;"
        );
        HBox.setHgrow(messageInput, Priority.ALWAYS);
        
        Button sendButton = new Button("Send");
        sendButton.setStyle(
            "-fx-background-color: #2196F3; -fx-text-fill: white; " +
            "-fx-font-size: 12px; -fx-padding: 10 16; -fx-background-radius: 20; " +
            "-fx-border-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;"
        );
        
        // Hover effects (same as other builders)
        sendButton.setOnMouseEntered(e -> sendButton.setStyle(
            "-fx-background-color: #1976D2; -fx-text-fill: white; " +
            "-fx-font-size: 12px; -fx-padding: 10 16; -fx-background-radius: 20; " +
            "-fx-border-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;"
        ));
        
        sendButton.setOnMouseExited(e -> sendButton.setStyle(
            "-fx-background-color: #2196F3; -fx-text-fill: white; " +
            "-fx-font-size: 12px; -fx-padding: 10 16; -fx-background-radius: 20; " +
            "-fx-border-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;"
        ));
        
        // Send message handlers
        sendButton.setOnAction(e -> sendMessage());
        messageInput.setOnAction(e -> sendMessage());
        
        inputArea.getChildren().addAll(messageInput, sendButton);
        return inputArea;
    }
    
    /**
     * Send message (updated)
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
        
        // Auto-scroll to bottom
        Platform.runLater(() -> {
            if (chatScrollPane != null) {
                chatScrollPane.setVvalue(1.0);
            }
        });
        
        // Notify event handler about new message
        eventHandler.onMessageSent(messageText);

        // NOTIFY EVENT HANDLER TO REFRESH
        eventHandler.onRefreshMessages();
    }
    
    /**
     * Load messages - updated styling
     */
    private void loadMessages() {
        if (chatContainer == null) return;
        
        chatContainer.getChildren().clear();
        
        List<ChatService.Message> messages = ChatService.getUserMessages();
        
        if (messages.isEmpty()) {
            // Show welcome card instead of just bubble
            VBox welcomeCard = createWelcomeCard();
            chatContainer.getChildren().add(welcomeCard);
        } else {
            for (ChatService.Message message : messages) {
                VBox bubble = createMessageBubble(message.getText(), message.getSenderType());
                chatContainer.getChildren().add(bubble);
            }
        }
    }
    
    /**
     * Create welcome card - matches other builders' card style
     */
    private VBox createWelcomeCard() {
        VBox welcomeCard = new VBox(12);
        welcomeCard.setAlignment(Pos.CENTER);
        welcomeCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        Label welcomeIcon = new Label("👋");
        welcomeIcon.setStyle("-fx-font-size: 32px;");
        
        Label welcomeTitle = new Label("Welcome to JetSetGo Support!");
        welcomeTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label welcomeText = new Label("How can we help you today?");
        welcomeText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        // Quick action buttons
        HBox actionButtons = new HBox(8);
        actionButtons.setAlignment(Pos.CENTER);
        
        Button bookingHelpBtn = createWelcomeButton("📝 Booking Help");
        Button flightInfoBtn = createWelcomeButton("✈️ Flight Info");
        
        actionButtons.getChildren().addAll(bookingHelpBtn, flightInfoBtn);
        
        welcomeCard.getChildren().addAll(welcomeIcon, welcomeTitle, welcomeText, actionButtons);
        return welcomeCard;
    }
    
    /**
     * Create welcome button
     */
    private Button createWelcomeButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #f8f9fa; -fx-text-fill: #2196F3; " +
            "-fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 15; " +
            "-fx-border-color: #2196F3; -fx-border-width: 1; -fx-border-radius: 15; " +
            "-fx-cursor: hand;"
        );
        
        button.setOnAction(e -> {
            messageInput.setText(text.substring(2)); // Remove emoji prefix
            sendMessage();
            refreshMessages();
        });
        
        return button;
    }
    
    /**
     * Create message bubble - updated styling to match theme
     */
    private VBox createMessageBubble(String text, String senderType) {
        VBox bubble = new VBox(3);
        bubble.setStyle("-fx-padding: 2 0;");
        
        boolean isUser = "user".equals(senderType);
        bubble.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(280);
        
        if (isUser) {
            messageLabel.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; " +
                "-fx-padding: 10 12; -fx-background-radius: 15 15 4 15; " +
                "-fx-font-size: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"
            );
        } else {
            String bgColor = "bot".equals(senderType) ? "#f0f0f0" : "#4CAF50";
            String textColor = "bot".equals(senderType) ? "#2c2c2c" : "white";
            messageLabel.setStyle(
                "-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor +
                "; -fx-padding: 10 12; -fx-background-radius: 15 15 15 4; " +
                "-fx-font-size: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"
            );
        }
        
        Label timeLabel = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999; -fx-padding: 2 4 0 4;");
        timeLabel.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        bubble.getChildren().addAll(messageLabel, timeLabel);
        return bubble;
    }
    
    /**
     * Auto-scroll chat to bottom
     */
    public void scrollToBottom() {
        Platform.runLater(() -> {
            if (chatScrollPane != null) {
                chatScrollPane.setVvalue(1.0);
            }
        });
    }
    
    /**
     * Refresh messages and scroll to bottom
     */
    public void refreshMessages() {
        loadMessages();
        scrollToBottom();

        Platform.runLater(() -> {
            if (chatScrollPane != null) {
                chatScrollPane.setVvalue(1.0);
            }
        });
    }
    
    /**
     * Interface for handling messages screen events
     */
    public interface MessagesEventHandler {
        void onMessageSent(String message);
        void onRefreshMessages();
    }
}