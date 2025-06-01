package application.ui.client;

import application.service.NotificationService;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationScreenBuilder {
    
    private final NotificationEventHandler eventHandler;
    
    public NotificationScreenBuilder(NotificationEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    /**
     * Create notifications screen content - matches other builders' pattern
     */
    public VBox createNotificationScreenContent() {
        VBox container = new VBox(15);
        container.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");
        
        // Header section (same style as other builders)
        VBox headerSection = createHeaderSection();
        
        // Notifications content section
        VBox contentSection = createNotificationsContentSection();
        
        container.getChildren().addAll(headerSection, contentSection);
        return container;
    }
    
    /**
     * Create header section - matches other builders' header style
     */
    private VBox createHeaderSection() {
        VBox headerCard = new VBox(12);
        headerCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        // Title section with back button and mark all read
        HBox titleBox = new HBox(12);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        // Back button (small and consistent with other builders)
        Button backButton = new Button("‹ Back");
        backButton.setStyle(
            "-fx-background-color: #f8f9fa; -fx-text-fill: #2196F3; " +
            "-fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 15; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 15; " +
            "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> eventHandler.onBackToProfile());
        
        // Hover effects for back button
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: #e3f2fd; -fx-text-fill: #1976D2; " +
            "-fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 15; " +
            "-fx-border-color: #2196F3; -fx-border-width: 1; -fx-border-radius: 15; " +
            "-fx-cursor: hand;"
        ));
        
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: #f8f9fa; -fx-text-fill: #2196F3; " +
            "-fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 15; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 15; " +
            "-fx-cursor: hand;"
        ));
        
        // Icon and title
        Label notificationIcon = new Label("🔔");
        notificationIcon.setStyle("-fx-font-size: 20px;");
        
        VBox titleSection = new VBox(2);
        Label titleLabel = new Label("Notifications");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        // Notification count
        int unreadCount = NotificationService.getUnreadCount();
        Label subtitleLabel = new Label(unreadCount > 0 ? 
            unreadCount + " unread notifications" : "All caught up!");
        subtitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Mark all read button (only show if there are unread notifications)
        Button markAllButton = null;
        if (unreadCount > 0) {
            markAllButton = new Button("Mark All Read");
            markAllButton.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 15; " +
                "-fx-border-radius: 15; -fx-font-weight: bold; -fx-cursor: hand;"
            );
            markAllButton.setOnAction(e -> {
                NotificationService.markAllAsRead();
                eventHandler.onRefreshNotifications();
            });
            
            // Hover effects
            final Button finalMarkAllButton = markAllButton;
            markAllButton.setOnMouseEntered(e -> finalMarkAllButton.setStyle(
                "-fx-background-color: #45a049; -fx-text-fill: white; " +
                "-fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 15; " +
                "-fx-border-radius: 15; -fx-font-weight: bold; -fx-cursor: hand;"
            ));
            
            markAllButton.setOnMouseExited(e -> finalMarkAllButton.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 15; " +
                "-fx-border-radius: 15; -fx-font-weight: bold; -fx-cursor: hand;"
            ));
        }
        
        titleBox.getChildren().addAll(backButton, notificationIcon, titleSection, spacer);
        if (markAllButton != null) {
            titleBox.getChildren().add(markAllButton);
        }
        
        headerCard.getChildren().add(titleBox);
        return headerCard;
    }
    
    /**
     * Create notifications content section
     */
    private VBox createNotificationsContentSection() {
        VBox contentCard = new VBox(12);
        contentCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        List<NotificationService.Notification> notifications = NotificationService.getUserNotifications();
        
        if (notifications.isEmpty()) {
            VBox emptyState = createEmptyState();
            contentCard.getChildren().add(emptyState);
        } else {
            // Notifications list with scroll
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; " +
                "-fx-border-color: transparent;"
            );
            
            VBox notificationsList = new VBox(8);
            for (NotificationService.Notification notification : notifications) {
                VBox notificationCard = createNotificationCard(notification);
                notificationsList.getChildren().add(notificationCard);
            }
            
            scrollPane.setContent(notificationsList);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            contentCard.getChildren().add(scrollPane);
        }
        
        return contentCard;
    }
    
    /**
     * Create empty state - matches welcome card pattern
     */
    private VBox createEmptyState() {
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setStyle("-fx-padding: 30;");
        
        Label emptyIcon = new Label("📭");
        emptyIcon.setStyle("-fx-font-size: 32px;");
        
        Label emptyTitle = new Label("No Notifications");
        emptyTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label emptyText = new Label("You're all caught up! We'll notify you when something important happens.");
        emptyText.setStyle(
            "-fx-font-size: 12px; -fx-text-fill: #666; -fx-wrap-text: true; " +
            "-fx-text-alignment: center; -fx-max-width: 250;"
        );
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyText);
        return emptyState;
    }
    
    /**
     * Create notification card - matches other builders' card style
     */
    private VBox createNotificationCard(NotificationService.Notification notification) {
        VBox card = new VBox(8);
        
        // Different background for unread notifications
        String backgroundColor = notification.isRead() ? "#f8f9fa" : "#e3f2fd";
        String borderColor = notification.isRead() ? "#e0e0e0" : "#2196F3";
        
        card.setStyle(
            "-fx-background-color: " + backgroundColor + "; -fx-background-radius: 6; " +
            "-fx-border-color: " + borderColor + "; -fx-border-width: 1; -fx-border-radius: 6; " +
            "-fx-padding: 12; -fx-cursor: hand;"
        );
        
        // Header with icon, title, and time
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Type icon
        String typeIcon = "booking".equals(notification.getType()) ? "✈️" : "💬";
        Label icon = new Label(typeIcon);
        icon.setStyle("-fx-font-size: 14px;");
        icon.setPrefWidth(20);
        
        // Title and unread indicator
        VBox titleBox = new VBox(2);
        
        HBox titleRow = new HBox(6);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label(notification.getTitle());
        title.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; " +
            "-fx-text-fill: " + (notification.isRead() ? "#2c2c2c" : "#1976D2") + ";"
        );
        
        // Unread badge
        if (!notification.isRead()) {
            Label unreadBadge = new Label("NEW");
            unreadBadge.setStyle(
                "-fx-background-color: #FF4444; -fx-text-fill: white; " +
                "-fx-font-size: 8px; -fx-font-weight: bold; -fx-padding: 2 4; " +
                "-fx-background-radius: 6;"
            );
            titleRow.getChildren().addAll(title, unreadBadge);
        } else {
            titleRow.getChildren().add(title);
        }
        
        titleBox.getChildren().add(titleRow);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Time
        Label time = new Label(notification.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
        time.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        header.getChildren().addAll(icon, titleBox, spacer, time);
        
        // Message preview (truncated)
        String messageText = notification.getMessage();
        if (messageText.length() > 80) {
            messageText = messageText.substring(0, 80) + "...";
        }
        
        Label message = new Label(messageText);
        message.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-wrap-text: true;");
        message.setMaxWidth(350);
        
        card.getChildren().addAll(header, message);
        
        // Hover effects (same as other builders)
        card.setOnMouseEntered(e -> {
            String hoverBg = notification.isRead() ? "#e8f4fd" : "#d1e7fd";
            card.setStyle(
                "-fx-background-color: " + hoverBg + "; -fx-background-radius: 6; " +
                "-fx-border-color: " + borderColor + "; -fx-border-width: 1; -fx-border-radius: 6; " +
                "-fx-padding: 12; -fx-cursor: hand;"
            );
        });
        
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: " + backgroundColor + "; -fx-background-radius: 6; " +
            "-fx-border-color: " + borderColor + "; -fx-border-width: 1; -fx-border-radius: 6; " +
            "-fx-padding: 12; -fx-cursor: hand;"
        ));
        
        // Click handler
        card.setOnMouseClicked(e -> {
            if (!notification.isRead()) {
                NotificationService.markAsRead(notification.getId());
            }
            eventHandler.onShowNotificationDetails(notification);
        });
        
        return card;
    }
    
    /**
     * Create notification details dialog - updated styling
     */
    public void showNotificationDetails(NotificationService.Notification notification) {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setTitle("Notification Details");
        dialog.setHeaderText(null);
        
        // Create custom content with consistent styling
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        content.setPrefWidth(450);
        content.setMaxWidth(450);
        
        // Header section
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        String typeIcon = "booking".equals(notification.getType()) ? "✈️" : "💬";
        Label icon = new Label(typeIcon);
        icon.setStyle("-fx-font-size: 24px;");
        
        VBox titleSection = new VBox(3);
        Label titleLabel = new Label(notification.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(380);
        
        String typeText = "booking".equals(notification.getType()) ? "Booking Notification" : "Support Message";
        Label typeLabel = new Label(typeText);
        typeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        
        titleSection.getChildren().addAll(titleLabel, typeLabel);
        headerBox.getChildren().addAll(icon, titleSection);
        
        // Message section - matches detail row style
        VBox messageBox = new VBox(8);
        messageBox.setStyle(
            "-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 6;"
        );
        
        Label messageLabel = new Label("Message:");
        messageLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        
        Label messageText = new Label(notification.getMessage());
        messageText.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c2c2c; -fx-wrap-text: true; -fx-line-spacing: 3;");
        messageText.setWrapText(true);
        messageText.setMaxWidth(410);
        
        messageBox.getChildren().addAll(messageLabel, messageText);
        
        // Details section - matches detail row pattern
        VBox detailsBox = new VBox(6);
        
        HBox dateRow = createDetailRow("📅 Received", 
            notification.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm")));
        
        HBox statusRow = createDetailRow("📍 Status", 
            notification.isRead() ? "Read" : "Unread");
        
        detailsBox.getChildren().addAll(dateRow, statusRow);
        
        content.getChildren().addAll(headerBox, messageBox, detailsBox);
        
        // Set content and styling
        dialog.getDialogPane().setContent(content);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefSize(470, 400);
        dialogPane.setMaxSize(470, 600);
        dialogPane.setStyle("-fx-background-radius: 8;");
        
        // Close button
        dialog.getButtonTypes().clear();
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dialog.getButtonTypes().add(closeButton);
        
        // Mark as read when dialog is opened
        if (!notification.isRead()) {
            NotificationService.markAsRead(notification.getId());
            Platform.runLater(() -> eventHandler.onRefreshNotifications());
        }
        
        dialog.showAndWait();
    }
    
    /**
     * Create detail row - exactly matches other builders' pattern
     */
    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 6;");
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        labelText.setPrefWidth(100);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c2c2c; -fx-font-weight: bold;");
        
        row.getChildren().addAll(labelText, spacer, valueText);
        return row;
    }
    
    /**
     * Interface for handling notification events
     */
    public interface NotificationEventHandler {
        void onBackToProfile();
        void onRefreshNotifications();
        void onShowNotificationDetails(NotificationService.Notification notification);
    }
}