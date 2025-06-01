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
     * Create notifications screen
     */
    public VBox createNotificationsScreen() {
        List<NotificationService.Notification> notifications = NotificationService.getUserNotifications();

        VBox notificationScreen = new VBox(10);
        notificationScreen.setStyle("-fx-padding: 15;");

        // Back button - standalone at the top
        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 8 15; -fx-background-radius: 8;");
        backButton.setOnAction(e -> eventHandler.onBackToProfile());

        // Header with Notifications title and Mark All Read button
        HBox titleHeader = new HBox();
        titleHeader.setAlignment(Pos.CENTER_LEFT);
        titleHeader.setSpacing(15);

        // Notifications title
        Label title = new Label("Notifications");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Spacer to push Mark All Read to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Mark All Read button
        Button markAllButton = new Button("Mark All Read");
        markAllButton.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 8 15; -fx-background-radius: 8;");
        markAllButton.setOnAction(e -> {
            NotificationService.markAllAsRead();
            eventHandler.onRefreshNotifications();
        });

        titleHeader.getChildren().addAll(title, spacer, markAllButton);

        // Notifications list
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox notificationsList = new VBox(10);

        if (notifications.isEmpty()) {
            Label emptyLabel = new Label("No notifications yet");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20;");
            notificationsList.getChildren().add(emptyLabel);
        } else {
            for (NotificationService.Notification notification : notifications) {
                VBox notificationCard = createNotificationCard(notification);
                notificationsList.getChildren().add(notificationCard);
            }
        }

        scrollPane.setContent(notificationsList);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        notificationScreen.getChildren().addAll(backButton, titleHeader, scrollPane);
        return notificationScreen;
    }
    
    /**
     * Create notification card
     */
    private VBox createNotificationCard(NotificationService.Notification notification) {
        VBox card = new VBox(10);
        String backgroundColor = notification.isRead() ? "white" : "#F0F8FF";
        card.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 10; " +
                "-fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 10; " +
                "-fx-border-width: 1; -fx-cursor: hand;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        String typeIcon = "booking".equals(notification.getType()) ? "✈️" : "💬";
        Label icon = new Label(typeIcon);
        icon.setStyle("-fx-font-size: 16px;");

        Label title = new Label(notification.getTitle());
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label time = new Label(notification.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
        time.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        if (!notification.isRead()) {
            Label unreadBadge = new Label("●");
            unreadBadge.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 12px;");
            header.getChildren().addAll(icon, title, spacer, unreadBadge, time);
        } else {
            header.getChildren().addAll(icon, title, spacer, time);
        }

        Label message = new Label(notification.getMessage());
        message.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-wrap-text: true;");

        card.getChildren().addAll(header, message);

        // Mark as read and show details when clicked
        card.setOnMouseClicked(e -> {
            if (!notification.isRead()) {
                NotificationService.markAsRead(notification.getId());
            }
            eventHandler.onShowNotificationDetails(notification);
        });

        return card;
    }
    
    /**
     * Create notification details dialog
     */
    public void showNotificationDetails(NotificationService.Notification notification) {
        // Create custom dialog
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setTitle("Notification Details");
        dialog.setHeaderText(null);

        // Create custom content
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        content.setPrefWidth(450);
        content.setMaxWidth(450);

        // Title section
        Label titleLabel = new Label(notification.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(430);

        // Message section - expanded for full message display
        VBox messageBox = new VBox(10);
        messageBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-border-radius: 10;");

        Label messageText = new Label(notification.getMessage());
        messageText.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-wrap-text: true; -fx-line-spacing: 5;");
        messageText.setWrapText(true);
        messageText.setMaxWidth(410);
        messageText.setPrefWidth(410);
        messageText.autosize();

        messageBox.getChildren().add(messageText);

        // Details section
        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 15; -fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 8; -fx-border-width: 1;");

        // Date received
        Label dateLabel = new Label("Received: " + 
            notification.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm")));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Type
        String typeText = "booking".equals(notification.getType()) ? "Booking Notification" : "Support Message";
        Label typeLabel = new Label("Type: " + typeText);
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Status
        String statusText = notification.isRead() ? "Read" : "Unread";
        Label statusLabel = new Label("Status: " + statusText);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        detailsBox.getChildren().addAll(dateLabel, typeLabel, statusLabel);

        // Add all sections to content
        content.getChildren().addAll(titleLabel, messageBox, detailsBox);

        // Set custom content in dialog
        dialog.getDialogPane().setContent(content);

        // Style the dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefSize(470, 400);
        dialogPane.setMaxSize(470, 600);
        dialogPane.setStyle("-fx-background-radius: 15;");

        // Only Close button
        dialog.getButtonTypes().clear();
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dialog.getButtonTypes().add(closeButton);

        // Mark as read when dialog is opened
        if (!notification.isRead()) {
            NotificationService.markAsRead(notification.getId());
            Platform.runLater(() -> eventHandler.onRefreshNotifications());
        }

        // Show dialog
        dialog.showAndWait();
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