package application.ui.admin;

import application.model.Message;
import application.service.AdminMessageService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdminMessagesBuilder {
    
    // Traditional Java styling constants
    private static final String DIALOG_STYLE = "-fx-background-color: #f0f0f0; -fx-border-color: #808080; -fx-border-width: 2;";
    private static final String BUTTON_STYLE = "-fx-background-color: linear-gradient(#f0f0f0, #d0d0d0); -fx-text-fill: #2c2c2c; -fx-padding: 8 16; -fx-border-color: #808080; -fx-border-width: 1; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String SEND_BUTTON_STYLE = "-fx-background-color: linear-gradient(#4a90e2, #357abd); -fx-text-fill: white; -fx-padding: 10 20; -fx-border-color: #2c5282; -fx-border-width: 1; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String SCROLL_PANE_STYLE = "-fx-background-color: #f8f8f8; -fx-border-color: #808080; -fx-border-width: 1; -fx-background: #f8f8f8;";
    private static final String CONVERSATION_ITEM_STYLE = "-fx-background-color: white; -fx-border-color: #c0c0c0; -fx-border-width: 0 0 1 0; -fx-padding: 10; -fx-cursor: hand;";
    private static final String CONVERSATION_HOVER_STYLE = "-fx-background-color: #e8e8e8; -fx-border-color: #c0c0c0; -fx-border-width: 0 0 1 0; -fx-padding: 10; -fx-cursor: hand;";
    private static final String CONVERSATION_SELECTED_STYLE = "-fx-background-color: #e8e8e8; -fx-border-color: #4a90e2; -fx-border-width: 2; -fx-padding: 10; -fx-cursor: hand;";
    private static final String LABEL_STYLE = "-fx-text-fill: #2c2c2c; -fx-font-size: 12px; -fx-font-weight: bold;";
    private static final String VALUE_STYLE = "-fx-text-fill: #2c3e50; -fx-font-size: 12px;";
    private static final String TIME_STYLE = "-fx-text-fill: #666666; -fx-font-size: 11px;";
    private static final String UNREAD_BADGE_STYLE = "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 2 6; -fx-font-size: 10px; -fx-font-weight: bold;";
    
    // Automation toggle styles
    private static final String AUTOMATION_ENABLED_STYLE = "-fx-background-color: linear-gradient(#28a745, #1e7e34); " +
                                                          "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; " +
                                                          "-fx-border-color: #1e7e34; -fx-border-width: 1; -fx-cursor: hand;";
    
    private static final String AUTOMATION_ENABLED_HOVER_STYLE = "-fx-background-color: linear-gradient(#1e7e34, #155724); " +
                                                               "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; " +
                                                               "-fx-border-color: #155724; -fx-border-width: 1; -fx-cursor: hand;";
    
    private static final String AUTOMATION_DISABLED_STYLE = "-fx-background-color: linear-gradient(#dc3545, #c82333); " +
                                                           "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; " +
                                                           "-fx-border-color: #c82333; -fx-border-width: 1; -fx-cursor: hand;";
    
    private static final String AUTOMATION_DISABLED_HOVER_STYLE = "-fx-background-color: linear-gradient(#c82333, #a71e2a); " +
                                                                "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; " +
                                                                "-fx-border-color: #a71e2a; -fx-border-width: 1; -fx-cursor: hand;";
    
    public interface MessagesEventHandler {
        void onConversationSelect(int userId);
        void onMessageSend(String message);
        void onAutomationToggle(boolean enabled);
        void onMessagesLoaded(int count);
        void onMessagesError(String error);
    }
    
    private final MessagesEventHandler eventHandler;
    private VBox currentSelectedConversation = null;
    private boolean isProcessingAutomationToggle = false; // Prevent recursive calls
    
    public AdminMessagesBuilder(MessagesEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    // Keep backward compatibility with ListView but convert to ScrollPane approach
    public void setupMessagesContent(ListView<Message> conversationsList, VBox chatArea,
                                   ScrollPane chatScrollPane, TextArea messageInputArea,
                                   Button sendMessageBtn, Label chatHeaderLabel,
                                   ToggleButton automationToggle, Label unreadCountLabel) {
        try {
            // Convert ListView to ScrollPane approach
            if (conversationsList != null && conversationsList.getParent() instanceof VBox) {
                VBox parent = (VBox) conversationsList.getParent();
                
                // Create ScrollPane and VBox for conversations
                ScrollPane conversationsScrollPane = new ScrollPane();
                VBox conversationsContainer = new VBox();
                
                // Setup the new components
                conversationsScrollPane.setContent(conversationsContainer);
                setupConversationsScrollPane(conversationsScrollPane, conversationsContainer, unreadCountLabel);
                
                // Replace ListView with ScrollPane
                int index = parent.getChildren().indexOf(conversationsList);
                parent.getChildren().remove(conversationsList);
                parent.getChildren().add(index, conversationsScrollPane);
                VBox.setVgrow(conversationsScrollPane, Priority.ALWAYS);
            }
            
            setupChatScrollPane(chatScrollPane, chatArea);
            setupMessageComponents(sendMessageBtn, automationToggle);
            
        } catch (Exception e) {
            System.err.println("Error setting up messages content: " + e.getMessage());
            if (eventHandler != null) {
                eventHandler.onMessagesError(e.getMessage());
            }
        }
    }
    
    // New method signature for separate scroll panes
    public void setupMessagesContent(ScrollPane conversationsScrollPane, VBox conversationsContainer,
                                   ScrollPane chatScrollPane, VBox chatArea,
                                   TextArea messageInputArea, Button sendMessageBtn, 
                                   Label chatHeaderLabel, ToggleButton automationToggle, 
                                   Label unreadMessagesCount) {
        try {
            setupConversationsScrollPane(conversationsScrollPane, conversationsContainer, unreadMessagesCount);
            setupChatScrollPane(chatScrollPane, chatArea);
            setupMessageComponents(sendMessageBtn, automationToggle);
            
        } catch (Exception e) {
            System.err.println("Error setting up messages content: " + e.getMessage());
            if (eventHandler != null) {
                eventHandler.onMessagesError(e.getMessage());
            }
        }
    }
    
    private void setupConversationsScrollPane(ScrollPane conversationsScrollPane, VBox conversationsContainer, 
                                            Label unreadMessagesCount) {
        try {
            if (conversationsScrollPane != null) {
                // Traditional styling for conversations scroll pane
                conversationsScrollPane.setStyle(SCROLL_PANE_STYLE);
                conversationsScrollPane.setFitToWidth(true);
                conversationsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                conversationsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
            
            if (conversationsContainer != null) {
                conversationsContainer.setStyle("-fx-background-color: #f8f8f8; -fx-spacing: 0;");
                loadConversationsIntoContainer(conversationsContainer, unreadMessagesCount);
            }
            
        } catch (Exception e) {
            System.err.println("Error setting up conversations scroll pane: " + e.getMessage());
            if (eventHandler != null) {
                eventHandler.onMessagesError(e.getMessage());
            }
        }
    }
    
    private void setupChatScrollPane(ScrollPane chatScrollPane, VBox chatArea) {
        try {
            if (chatScrollPane != null) {
                // Traditional styling for chat scroll pane
                chatScrollPane.setStyle(SCROLL_PANE_STYLE);
                chatScrollPane.setFitToWidth(true);
                chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
            
            if (chatArea != null) {
                // Apply traditional styling to chat area
                chatArea.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 10; -fx-spacing: 8;");
            }
            
        } catch (Exception e) {
            System.err.println("Error setting up chat scroll pane: " + e.getMessage());
        }
    }
    
    private void loadConversationsIntoContainer(VBox conversationsContainer, Label unreadMessagesCount) {
        try {
            conversationsContainer.getChildren().clear();
            currentSelectedConversation = null;
            
            ObservableList<Message> conversations = AdminMessageService.getAllConversations();
            
            for (Message conversation : conversations) {
                try {
                    VBox conversationItem = createTraditionalConversationItem(conversation);
                    
                    // Add click handler for conversation selection
                    conversationItem.setOnMouseClicked(event -> {
                        try {
                            // Clear previous selection styling
                            if (currentSelectedConversation != null) {
                                currentSelectedConversation.setStyle(CONVERSATION_ITEM_STYLE);
                            }
                            
                            // Highlight selected conversation
                            conversationItem.setStyle(CONVERSATION_SELECTED_STYLE);
                            currentSelectedConversation = conversationItem;
                            
                            // Mark messages as read and load conversation
                            AdminMessageService.markMessagesAsRead(conversation.getUserId());
                            if (eventHandler != null) {
                                eventHandler.onConversationSelect(conversation.getUserId());
                            }
                            
                            Platform.runLater(() -> {
                                updateUnreadCount(unreadMessagesCount);
                                // Reload conversations to update unread counts
                                loadConversationsIntoContainer(conversationsContainer, unreadMessagesCount);
                            });
                        } catch (Exception e) {
                            System.err.println("Error selecting conversation: " + e.getMessage());
                            if (eventHandler != null) {
                                eventHandler.onMessagesError("Failed to load conversation: " + e.getMessage());
                            }
                        }
                    });
                    
                    conversationsContainer.getChildren().add(conversationItem);
                } catch (Exception e) {
                    System.err.println("Error creating conversation item: " + e.getMessage());
                }
            }
            
            updateUnreadCount(unreadMessagesCount);
            if (eventHandler != null) {
                eventHandler.onMessagesLoaded(conversations.size());
            }
            
        } catch (Exception e) {
            System.err.println("Error loading conversations: " + e.getMessage());
            if (eventHandler != null) {
                eventHandler.onMessagesError(e.getMessage());
            }
        }
    }
    
    public void loadConversation(int userId, VBox chatArea, ScrollPane chatScrollPane,
                               Label chatHeaderLabel, ToggleButton automationToggle) {
        try {
            ObservableList<Message> messages = AdminMessageService.getConversationMessages(userId);

            if (chatArea != null) {
                chatArea.getChildren().clear();
                // Apply traditional styling to chat area
                chatArea.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 10; -fx-spacing: 8;");
            }

            if (chatHeaderLabel != null && !messages.isEmpty()) {
                Message firstMessage = messages.get(0);
                String userName = firstMessage.getUserName();
                if (userName == null || userName.trim().isEmpty()) {
                    userName = "Unknown User";
                }
                chatHeaderLabel.setText("Chat with " + userName);
                chatHeaderLabel.setStyle(LABEL_STYLE + " -fx-font-size: 14px;");

                if (automationToggle != null) {
                    boolean isAutomationEnabled = AdminMessageService.isAutomationEnabled(userId);
                    updateTraditionalAutomationToggle(automationToggle, isAutomationEnabled);
                }
            }

            if (chatArea != null) {
                if (messages.isEmpty()) {
                    // Show empty state
                    VBox emptyState = createEmptyConversationState();
                    chatArea.getChildren().add(emptyState);
                } else {
                    for (Message message : messages) {
                        try {
                            VBox messageItem = createTraditionalMessageItem(message);
                            chatArea.getChildren().add(messageItem);
                        } catch (Exception e) {
                            System.err.println("Error creating message item: " + e.getMessage());
                        }
                    }
                }
            }

            // Auto-scroll to bottom of chat
            Platform.runLater(() -> {
                if (chatScrollPane != null) {
                    chatScrollPane.setVvalue(1.0);
                }
            });

        } catch (Exception e) {
            System.err.println("Error loading conversation: " + e.getMessage());
            if (eventHandler != null) {
                eventHandler.onMessagesError("Failed to load conversation: " + e.getMessage());
            }
        }
    }
    
    public void sendMessage(int userId, String messageText, Button sendMessageBtn,
                          BiConsumer<String, String> alertCallback, Consumer<Integer> conversationLoader) {
        try {
            if (sendMessageBtn != null) {
                sendMessageBtn.setText("Sending...");
                sendMessageBtn.setDisable(true);
                sendMessageBtn.setStyle(BUTTON_STYLE + " -fx-opacity: 0.6;");
            }
            
            if (AdminMessageService.sendMessage(userId, messageText, "admin", null)) {
                // Refresh the conversation view
                conversationLoader.accept(userId);
                
                if (sendMessageBtn != null) {
                    sendMessageBtn.setText("Send");
                    sendMessageBtn.setDisable(false);
                    sendMessageBtn.setStyle(SEND_BUTTON_STYLE);
                }
                
                if (eventHandler != null) {
                    eventHandler.onMessageSend(messageText);
                }
            } else {
                showTraditionalAlert("Error", "Failed to send message.", alertCallback);
                
                if (sendMessageBtn != null) {
                    sendMessageBtn.setText("Send");
                    sendMessageBtn.setDisable(false);
                    sendMessageBtn.setStyle(SEND_BUTTON_STYLE);
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            showTraditionalAlert("Error", "An error occurred while sending the message: " + e.getMessage(), alertCallback);
            
            if (sendMessageBtn != null) {
                sendMessageBtn.setText("Send");
                sendMessageBtn.setDisable(false);
                sendMessageBtn.setStyle(SEND_BUTTON_STYLE);
            }
        }
    }
    
    public void toggleAutomation(int userId, ToggleButton automationToggle, BiConsumer<String, String> alertCallback) {
        // Prevent recursive calls
        if (isProcessingAutomationToggle) {
            return;
        }
        
        try {
            isProcessingAutomationToggle = true;
            
            boolean isEnabled = automationToggle != null ? automationToggle.isSelected() : false;
            AdminMessageService.setAutomationEnabled(userId, isEnabled);
            
            if (automationToggle != null) {
                updateTraditionalAutomationToggle(automationToggle, isEnabled);
            }
            
            String status = isEnabled ? "enabled" : "disabled";
            showTraditionalAlert("Automation " + (isEnabled ? "Enabled" : "Disabled"), 
                               "Automated replies " + status + " for this user.\n" +
                               "Bot will " + (isEnabled ? "automatically respond" : "NOT respond") + " to new messages.", 
                               alertCallback);
            
            // DO NOT call eventHandler.onAutomationToggle here to prevent recursion
            
        } catch (Exception e) {
            System.err.println("Error toggling automation: " + e.getMessage());
            showTraditionalAlert("Error", "Failed to toggle automation: " + e.getMessage(), alertCallback);
        } finally {
            isProcessingAutomationToggle = false;
        }
    }
    
    public void refreshConversations(VBox conversationsContainer, Label unreadMessagesCount) {
        try {
            loadConversationsIntoContainer(conversationsContainer, unreadMessagesCount);
        } catch (Exception e) {
            System.err.println("Error refreshing conversations: " + e.getMessage());
            if (eventHandler != null) {
                eventHandler.onMessagesError("Failed to refresh conversations: " + e.getMessage());
            }
        }
    }
    
    // TRADITIONAL STYLING HELPER METHODS
    private VBox createTraditionalConversationItem(Message message) {
        VBox item = new VBox(8);
        item.setStyle(CONVERSATION_ITEM_STYLE);
        
        try {
            HBox header = new HBox(12);
            header.setAlignment(Pos.CENTER_LEFT);
            
            String userName = message.getUserName();
            if (userName == null || userName.trim().isEmpty()) {
                userName = "Unknown User";
            }
            
            Label nameLabel = new Label("👤 " + userName);
            nameLabel.setStyle(LABEL_STYLE + " -fx-font-size: 13px;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label timeLabel = new Label(message.getFormattedDateTime());
            timeLabel.setStyle(TIME_STYLE);
            
            if (message.getUnreadCount() > 0) {
                Label unreadBadge = new Label(String.valueOf(message.getUnreadCount()));
                unreadBadge.setStyle(UNREAD_BADGE_STYLE);
                header.getChildren().addAll(nameLabel, spacer, unreadBadge, timeLabel);
            } else {
                header.getChildren().addAll(nameLabel, spacer, timeLabel);
            }
            
            // Add last message preview
            String lastMessagePreview = message.getMessageText();
            if (lastMessagePreview != null && lastMessagePreview.length() > 40) {
                lastMessagePreview = lastMessagePreview.substring(0, 40) + "...";
            } else if (lastMessagePreview == null) {
                lastMessagePreview = "No messages yet";
            }
            
            Label previewLabel = new Label(lastMessagePreview);
            previewLabel.setStyle(VALUE_STYLE + " -fx-font-size: 11px;");
            previewLabel.setWrapText(true);
            
            // Add status indicator
            HBox statusRow = new HBox(8);
            statusRow.setAlignment(Pos.CENTER_LEFT);
            
            Label statusLabel = new Label("●");
            statusLabel.setStyle("-fx-text-fill: #28a745; -fx-font-size: 12px;");
            
            Label statusText = new Label("Online");
            statusText.setStyle(VALUE_STYLE + " -fx-font-size: 11px;");
            
            statusRow.getChildren().addAll(statusLabel, statusText);
            
            item.getChildren().addAll(header, previewLabel, statusRow);
            
            // Traditional hover effects
            item.setOnMouseEntered(e -> {
                if (!item.getStyle().contains("-fx-border-color: #4a90e2")) {
                    item.setStyle(CONVERSATION_HOVER_STYLE);
                }
            });
            item.setOnMouseExited(e -> {
                if (!item.getStyle().contains("-fx-border-color: #4a90e2")) {
                    item.setStyle(CONVERSATION_ITEM_STYLE);
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error creating conversation item content: " + e.getMessage());
            Label errorLabel = new Label("⚠ Error loading conversation");
            errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            item.getChildren().clear();
            item.getChildren().add(errorLabel);
        }
        
        return item;
    }
    
    private VBox createTraditionalMessageItem(Message message) {
        VBox messageContainer = new VBox(6);
        messageContainer.setPadding(new Insets(8));
        
        try {
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            
            String senderType = message.getSenderType();
            if (senderType == null) {
                senderType = "user";
            }
            
            String senderDisplay;
            String senderColor;
            switch (senderType.toLowerCase()) {
                case "admin":
                    senderDisplay = "👨‍💼 Admin";
                    senderColor = "#2c5282";
                    break;
                case "bot":
                    senderDisplay = "🤖 Bot";
                    senderColor = "#6b7280";
                    break;
                default:
                    String userName = message.getUserName();
                    if (userName == null || userName.trim().isEmpty()) {
                        userName = "User";
                    }
                    senderDisplay = "👤 " + userName;
                    senderColor = "#2c2c2c";
                    break;
            }
            
            Label senderLabel = new Label(senderDisplay);
            senderLabel.setStyle(LABEL_STYLE + " -fx-text-fill: " + senderColor + "; -fx-font-size: 12px;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label timeLabel = new Label(message.getFormattedDateTime());
            timeLabel.setStyle(TIME_STYLE);
            
            header.getChildren().addAll(senderLabel, spacer, timeLabel);
            
            String messageText = message.getMessageText();
            if (messageText == null || messageText.trim().isEmpty()) {
                messageText = "No content";
            }
            
            Label contentLabel = new Label(messageText);
            contentLabel.setWrapText(true);
            contentLabel.setMaxWidth(350);
            
            // Traditional message styling based on sender type
            switch (senderType.toLowerCase()) {
                case "admin":
                    contentLabel.setStyle("-fx-background-color: linear-gradient(#4a90e2, #357abd); " +
                                        "-fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10; " +
                                        "-fx-border-color: #2c5282; -fx-border-width: 1; -fx-border-radius: 8;");
                    messageContainer.setAlignment(Pos.CENTER_RIGHT);
                    break;
                case "bot":
                    contentLabel.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #2c2c2c; " +
                                        "-fx-background-radius: 8; -fx-padding: 10; " +
                                        "-fx-border-color: #c0c0c0; -fx-border-width: 1; -fx-border-radius: 8;");
                    messageContainer.setAlignment(Pos.CENTER_LEFT);
                    break;
                default:
                    contentLabel.setStyle("-fx-background-color: white; -fx-text-fill: #2c2c2c; " +
                                        "-fx-background-radius: 8; -fx-padding: 10; " +
                                        "-fx-border-color: #c0c0c0; -fx-border-width: 1; -fx-border-radius: 8;");
                    messageContainer.setAlignment(Pos.CENTER_LEFT);
                    break;
            }
            
            messageContainer.getChildren().addAll(header, contentLabel);
            
        } catch (Exception e) {
            System.err.println("Error creating message item content: " + e.getMessage());
            Label errorLabel = new Label("⚠ Error loading message");
            errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            messageContainer.getChildren().clear();
            messageContainer.getChildren().add(errorLabel);
        }
        
        return messageContainer;
    }
    
    private VBox createEmptyConversationState() {
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(50));
        
        Label titleLabel = new Label("No messages yet");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label messageLabel = new Label("This conversation will show messages once they are exchanged.");
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        
        emptyState.getChildren().addAll(titleLabel, messageLabel);
        
        return emptyState;
    }
    
    private void setupMessageComponents(Button sendMessageBtn, ToggleButton automationToggle) {
        if (sendMessageBtn != null) {
            sendMessageBtn.setText("Send");
            sendMessageBtn.setStyle(SEND_BUTTON_STYLE);
            
            // Traditional hover effects
            sendMessageBtn.setOnMouseEntered(e -> 
                sendMessageBtn.setStyle("-fx-background-color: linear-gradient(#357abd, #2c5282); " +
                                       "-fx-text-fill: white; -fx-padding: 10 20; " +
                                       "-fx-border-color: #2c5282; -fx-border-width: 1; " +
                                       "-fx-font-weight: bold; -fx-cursor: hand;"));
            
            sendMessageBtn.setOnMouseExited(e -> 
                sendMessageBtn.setStyle(SEND_BUTTON_STYLE));
        }
        
        if (automationToggle != null) {
            updateTraditionalAutomationToggle(automationToggle, false);
        }
    }
    
    private void updateTraditionalAutomationToggle(ToggleButton automationToggle, boolean isEnabled) {
        if (automationToggle == null) {
            return;
        }
        
        // Clear existing event handlers to prevent loops
        automationToggle.setOnMouseEntered(null);
        automationToggle.setOnMouseExited(null);
        
        automationToggle.setSelected(isEnabled);
        
        if (isEnabled) {
            automationToggle.setText("Auto-Reply: ON");
            automationToggle.setStyle(AUTOMATION_ENABLED_STYLE);
        } else {
            automationToggle.setText("Auto-Reply: OFF");
            automationToggle.setStyle(AUTOMATION_DISABLED_STYLE);
        }
        
        // Set hover effects without recursive calls
        automationToggle.setOnMouseEntered(e -> {
            if (automationToggle.isSelected()) {
                automationToggle.setStyle(AUTOMATION_ENABLED_HOVER_STYLE);
            } else {
                automationToggle.setStyle(AUTOMATION_DISABLED_HOVER_STYLE);
            }
        });
        
        automationToggle.setOnMouseExited(e -> {
            if (automationToggle.isSelected()) {
                automationToggle.setStyle(AUTOMATION_ENABLED_STYLE);
            } else {
                automationToggle.setStyle(AUTOMATION_DISABLED_STYLE);
            }
        });
    }
    
    private void updateUnreadCount(Label unreadMessagesCount) {
        try {
            if (unreadMessagesCount == null) {
                return;
            }
            
            int unreadCount = AdminMessageService.getUnreadMessageCount();
            if (unreadCount > 0) {
                unreadMessagesCount.setText(String.valueOf(unreadCount));
                unreadMessagesCount.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; " +
                                           "-fx-background-radius: 8; -fx-padding: 2 6; " +
                                           "-fx-font-size: 10px; -fx-font-weight: bold; " +
                                           "-fx-border-color: #808080; -fx-border-width: 1;");
                unreadMessagesCount.setVisible(true);
            } else {
                unreadMessagesCount.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("Error updating unread count: " + e.getMessage());
        }
    }
    
    private void showTraditionalAlert(String title, String message, BiConsumer<String, String> alertCallback) {
        if (alertCallback != null) {
            alertCallback.accept(title, message);
        }
    }
}