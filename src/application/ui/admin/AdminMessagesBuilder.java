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
    
    public interface MessagesEventHandler {
        void onConversationSelect(int userId);
        void onMessageSend(String message);
        void onAutomationToggle(boolean enabled);
        void onMessagesLoaded(int count);
        void onMessagesError(String error);
    }
    
    private final MessagesEventHandler eventHandler;
    
    public AdminMessagesBuilder(MessagesEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    public void setupMessagesContent(ListView<Message> conversationsList, VBox chatArea,
                                   ScrollPane chatScrollPane, TextArea messageInputArea,
                                   Button sendMessageBtn, Label chatHeaderLabel,
                                   ToggleButton automationToggle, Label unreadCountLabel) {
        try {
            loadConversations(conversationsList, unreadCountLabel);
            setupMessageComponents(sendMessageBtn, automationToggle);
            
        } catch (Exception e) {
            System.err.println("Error setting up messages content: " + e.getMessage());
            eventHandler.onMessagesError(e.getMessage());
        }
    }
    
    private void loadConversations(ListView<Message> conversationsList, Label unreadCountLabel) {
        try {
            if (conversationsList != null) {
                conversationsList.getSelectionModel().clearSelection();
            }

            ObservableList<Message> conversations = AdminMessageService.getAllConversations();

            if (conversationsList != null) {
                conversationsList.setItems(conversations);
                setupConversationCellFactory(conversationsList);
                setupConversationSelection(conversationsList, unreadCountLabel);
            }

            updateUnreadCount(unreadCountLabel);
            eventHandler.onMessagesLoaded(conversations.size());

        } catch (Exception e) {
            System.err.println("Error loading conversations: " + e.getMessage());
            eventHandler.onMessagesError(e.getMessage());
        }
    }
    
    private void setupConversationCellFactory(ListView<Message> conversationsList) {
        conversationsList.setCellFactory(listView -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    try {
                        VBox conversationItem = createConversationItem(message);
                        setGraphic(conversationItem);
                        setText(null);
                    } catch (Exception e) {
                        System.err.println("Error creating conversation item: " + e.getMessage());
                        setText("Error loading conversation");
                        setGraphic(null);
                    }
                }
            }
        });
    }
    
    private void setupConversationSelection(ListView<Message> conversationsList, Label unreadCountLabel) {
        conversationsList.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    try {
                        if (newSelection != null && newSelection.getUserId() > 0) {
                            AdminMessageService.markMessagesAsRead(newSelection.getUserId());
                            eventHandler.onConversationSelect(newSelection.getUserId());
                            
                            Platform.runLater(() -> {
                                updateUnreadCount(unreadCountLabel);
                                ObservableList<Message> updatedConversations = AdminMessageService.getAllConversations();
                                conversationsList.setItems(updatedConversations);
                            });
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading conversation: " + e.getMessage());
                        eventHandler.onMessagesError("Failed to load conversation: " + e.getMessage());
                    }
                });
    }
    
    public void loadConversation(int userId, VBox chatArea, ScrollPane chatScrollPane,
                               Label chatHeaderLabel, ToggleButton automationToggle) {
        try {
            ObservableList<Message> messages = AdminMessageService.getConversationMessages(userId);

            if (chatArea != null) {
                chatArea.getChildren().clear();
            }

            if (chatHeaderLabel != null && !messages.isEmpty()) {
                Message firstMessage = messages.get(0);
                String userName = firstMessage.getUserName();
                if (userName == null || userName.trim().isEmpty()) {
                    userName = "Unknown User";
                }
                chatHeaderLabel.setText("Chat with " + userName);

                if (automationToggle != null) {
                    boolean isAutomationEnabled = AdminMessageService.isAutomationEnabled(userId);
                    updateAutomationToggle(automationToggle, isAutomationEnabled);
                }
            }

            if (chatArea != null) {
                for (Message message : messages) {
                    try {
                        VBox messageItem = createMessageItem(message);
                        chatArea.getChildren().add(messageItem);
                    } catch (Exception e) {
                        System.err.println("Error creating message item: " + e.getMessage());
                    }
                }
            }

            Platform.runLater(() -> {
                if (chatScrollPane != null) {
                    chatScrollPane.setVvalue(1.0);
                }
            });

        } catch (Exception e) {
            System.err.println("Error loading conversation: " + e.getMessage());
            eventHandler.onMessagesError("Failed to load conversation: " + e.getMessage());
        }
    }
    
    public void sendMessage(int userId, String messageText, Button sendMessageBtn,
                          BiConsumer<String, String> alertCallback, Consumer<Integer> conversationLoader) {
        try {
            if (sendMessageBtn != null) {
                sendMessageBtn.setText("Sending...");
                sendMessageBtn.setDisable(true);
            }
            
            if (AdminMessageService.sendMessage(userId, messageText, "admin", null)) {
                conversationLoader.accept(userId);
                
                if (sendMessageBtn != null) {
                    sendMessageBtn.setText("Send");
                    sendMessageBtn.setDisable(false);
                }
            } else {
                alertCallback.accept("Error", "Failed to send message.");
                
                if (sendMessageBtn != null) {
                    sendMessageBtn.setText("Send");
                    sendMessageBtn.setDisable(false);
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            alertCallback.accept("Error", "An error occurred while sending the message: " + e.getMessage());
            
            if (sendMessageBtn != null) {
                sendMessageBtn.setText("Send");
                sendMessageBtn.setDisable(false);
            }
        }
    }
    
    public void toggleAutomation(int userId, ToggleButton automationToggle, BiConsumer<String, String> alertCallback) {
        try {
            boolean isEnabled = automationToggle != null ? automationToggle.isSelected() : false;
            AdminMessageService.setAutomationEnabled(userId, isEnabled);
            
            if (automationToggle != null) {
                updateAutomationToggle(automationToggle, isEnabled);
            }
            
            String status = isEnabled ? "enabled" : "disabled";
            alertCallback.accept("Info", "Automated replies " + status + " for this user.\n" +
                                 "Bot will " + (isEnabled ? "automatically respond" : "NOT respond") + " to new messages.");
        } catch (Exception e) {
            System.err.println("Error toggling automation: " + e.getMessage());
            alertCallback.accept("Error", "Failed to toggle automation: " + e.getMessage());
        }
    }
    
    // PRIVATE HELPER METHODS
    private VBox createConversationItem(Message message) {
        VBox item = new VBox(5);
        item.setPadding(new Insets(10));
        item.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
        
        try {
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            
            String userName = message.getUserName();
            if (userName == null || userName.trim().isEmpty()) {
                userName = "Unknown User";
            }
            
            Label nameLabel = new Label(userName);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label timeLabel = new Label(message.getFormattedDateTime());
            timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
            
            if (message.getUnreadCount() > 0) {
                Label unreadBadge = new Label(String.valueOf(message.getUnreadCount()));
                unreadBadge.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; " +
                                   "-fx-background-radius: 10; -fx-padding: 2 6; -fx-font-size: 11px;");
                header.getChildren().addAll(nameLabel, spacer, unreadBadge, timeLabel);
            } else {
                header.getChildren().addAll(nameLabel, spacer, timeLabel);
            }
            
            item.getChildren().add(header);
            
            item.setOnMouseEntered(e -> item.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; " +
                                                      "-fx-background-color: #f8f9fa; -fx-cursor: hand;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));
            
        } catch (Exception e) {
            System.err.println("Error creating conversation item content: " + e.getMessage());
            Label errorLabel = new Label("Error loading conversation");
            errorLabel.setStyle("-fx-text-fill: red;");
            item.getChildren().clear();
            item.getChildren().add(errorLabel);
        }
        
        return item;
    }
    
    private VBox createMessageItem(Message message) {
        VBox messageContainer = new VBox(5);
        messageContainer.setPadding(new Insets(10));
        
        try {
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            
            String senderType = message.getSenderType();
            if (senderType == null) {
                senderType = "user";
            }
            
            String senderDisplay;
            switch (senderType.toLowerCase()) {
                case "admin":
                    senderDisplay = "👨‍💼 Admin";
                    break;
                case "bot":
                    senderDisplay = "🤖 Bot";
                    break;
                default:
                    String userName = message.getUserName();
                    if (userName == null || userName.trim().isEmpty()) {
                        userName = "User";
                    }
                    senderDisplay = "👤 " + userName;
                    break;
            }
            
            Label senderLabel = new Label(senderDisplay);
            senderLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + message.getSenderType() + ";");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label timeLabel = new Label(message.getFormattedDateTime());
            timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
            
            header.getChildren().addAll(senderLabel, spacer, timeLabel);
            
            String messageText = message.getMessageText();
            if (messageText == null || messageText.trim().isEmpty()) {
                messageText = "No content";
            }
            
            Label contentLabel = new Label(messageText);
            contentLabel.setWrapText(true);
            contentLabel.setMaxWidth(400);
            
            switch (senderType.toLowerCase()) {
                case "admin":
                    contentLabel.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; " +
                                        "-fx-background-radius: 10; -fx-padding: 10;");
                    messageContainer.setAlignment(Pos.CENTER_RIGHT);
                    break;
                case "bot":
                    contentLabel.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #6c757d; " +
                                        "-fx-background-radius: 10; -fx-padding: 10; " +
                                        "-fx-border-color: #dee2e6; -fx-border-radius: 10;");
                    messageContainer.setAlignment(Pos.CENTER_LEFT);
                    break;
                default:
                    contentLabel.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #495057; " +
                                        "-fx-background-radius: 10; -fx-padding: 10;");
                    messageContainer.setAlignment(Pos.CENTER_LEFT);
                    break;
            }
            
            messageContainer.getChildren().addAll(header, contentLabel);
            
        } catch (Exception e) {
            System.err.println("Error creating message item content: " + e.getMessage());
            Label errorLabel = new Label("Error loading message");
            errorLabel.setStyle("-fx-text-fill: red;");
            messageContainer.getChildren().clear();
            messageContainer.getChildren().add(errorLabel);
        }
        
        return messageContainer;
    }
    
    private void setupMessageComponents(Button sendMessageBtn, ToggleButton automationToggle) {
        if (sendMessageBtn != null) {
            sendMessageBtn.setText("Send");
            sendMessageBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; " +
                                   "-fx-font-weight: bold; -fx-padding: 10 20; " +
                                   "-fx-background-radius: 5; -fx-cursor: hand;");
            
            sendMessageBtn.setOnMouseEntered(e -> 
                sendMessageBtn.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white; " +
                                       "-fx-font-weight: bold; -fx-padding: 10 20; " +
                                       "-fx-background-radius: 5; -fx-cursor: hand;"));
            
            sendMessageBtn.setOnMouseExited(e -> 
                sendMessageBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; " +
                                       "-fx-font-weight: bold; -fx-padding: 10 20; " +
                                       "-fx-background-radius: 5; -fx-cursor: hand;"));
        }
        
        if (automationToggle != null) {
            updateAutomationToggle(automationToggle, false);
        }
    }
    
    private void updateAutomationToggle(ToggleButton automationToggle, boolean isEnabled) {
        automationToggle.setSelected(isEnabled);
        automationToggle.setText(isEnabled ? "ON" : "OFF");
        automationToggle.setStyle(isEnabled ? 
            "-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;" : 
            "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
    }
    
    private void updateUnreadCount(Label unreadCountLabel) {
        try {
            int unreadCount = AdminMessageService.getUnreadMessageCount();
            if (unreadCountLabel != null) {
                if (unreadCount > 0) {
                    unreadCountLabel.setText(String.valueOf(unreadCount));
                    unreadCountLabel.setVisible(true);
                } else {
                    unreadCountLabel.setVisible(false);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating unread count: " + e.getMessage());
        }
    }
}