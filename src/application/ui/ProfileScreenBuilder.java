package application.ui;

import application.model.User;
import application.service.NotificationService;
import application.service.UserSession;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProfileScreenBuilder {
    
    private final ProfileEventHandler eventHandler;
    
    public ProfileScreenBuilder(ProfileEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    /**
     * Create the main profile screen content
     */
    public VBox createProfileContent() {
        try {
            if (UserSession.getInstance().isLoggedIn()) {
                User user = UserSession.getInstance().getCurrentUser();
                return createLoggedInProfile(user);
            } else {
                return createGuestProfile();
            }
        } catch (Exception e) {
            System.err.println("Error setting up profile screen: " + e.getMessage());
            return createGuestProfile();
        }
    }
    
    /**
     * Create logged in user profile
     */
    private VBox createLoggedInProfile(User user) {
        VBox profileCard = new VBox(15);
        profileCard.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");

        // User avatar and name
        VBox headerBox = new VBox(8);
        headerBox.setStyle("-fx-alignment: center;");

        Label avatarLabel = new Label("👤");
        avatarLabel.setStyle("-fx-font-size: 48px;");

        Label nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label emailLabel = new Label(user.getEmail());
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        headerBox.getChildren().addAll(avatarLabel, nameLabel, emailLabel);

        // Profile options
        VBox optionsBox = new VBox(10);
        optionsBox.getChildren().addAll(
                createProfileOption("✈️", "My Bookings", "View your flight bookings", 
                    () -> eventHandler.onSwitchToTab("bookings")),
                createNotificationOption(),
                createProfileOption("📞", "Support", "Get help", 
                    () -> eventHandler.onSwitchToTab("messages")),
                createProfileOption("🚪", "Logout", "Sign out of your account", 
                    eventHandler::onLogout));

        profileCard.getChildren().addAll(headerBox, new Separator(), optionsBox);
        return profileCard;
    }
    
    /**
     * Create guest profile (not logged in)
     */
    private VBox createGuestProfile() {
        VBox guestCard = new VBox(20);
        guestCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; -fx-alignment: center;");
        
        Label iconLabel = new Label("👋");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label("Welcome to JetSetGO");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label subtitleLabel = new Label("Sign in to access your bookings and get personalized recommendations");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");
        
        Button loginButton = new Button("Sign In");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 20;");
        loginButton.setOnAction(e -> eventHandler.onNavigateToLogin());
        
        Button signupButton = new Button("Create Account");
        signupButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2196F3; " +
                "-fx-font-size: 14px; -fx-padding: 12 30; -fx-border-color: #2196F3; " +
                "-fx-border-radius: 20; -fx-background-radius: 20;");
        signupButton.setOnAction(e -> eventHandler.onNavigateToSignup());
        
        guestCard.getChildren().addAll(iconLabel, titleLabel, subtitleLabel, loginButton, signupButton);
        return guestCard;
    }
    
    /**
     * Create special notification option with badge
     */
    private HBox createNotificationOption() {
        HBox option = new HBox(12);
        option.setStyle("-fx-alignment: center-left; -fx-padding: 12; -fx-background-color: #f8f9fa; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        
        Label iconLabel = new Label("🔔");
        iconLabel.setStyle("-fx-font-size: 20px;");
        
        VBox textBox = new VBox(2);
        Label titleLabel = new Label("Notifications");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label subtitleLabel = new Label("View your notifications");
        subtitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        textBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Notification badge
        int unreadCount = NotificationService.getUnreadCount();
        VBox badgeContainer = new VBox();
        badgeContainer.setAlignment(Pos.CENTER_RIGHT);
        
        if (unreadCount > 0) {
            Label badge = new Label(String.valueOf(unreadCount));
            badge.setStyle("-fx-background-color: #FF4444; -fx-text-fill: white; -fx-font-size: 11px; " +
                         "-fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 10; -fx-min-width: 20;");
            badgeContainer.getChildren().add(badge);
        }
        
        Label arrowLabel = new Label("›");
        arrowLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        
        option.getChildren().addAll(iconLabel, textBox, spacer, badgeContainer, arrowLabel);
        option.setOnMouseClicked(e -> eventHandler.onShowNotifications());
        
        return option;
    }
    
    /**
     * Create profile option with action
     */
    private HBox createProfileOption(String icon, String title, String subtitle, Runnable action) {
        HBox option = new HBox(12);
        option.setStyle("-fx-alignment: center-left; -fx-padding: 12; -fx-background-color: #f8f9fa; " +
                "-fx-background-radius: 10; -fx-cursor: hand;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");
        
        VBox textBox = new VBox(2);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        textBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label arrowLabel = new Label("›");
        arrowLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        
        option.getChildren().addAll(iconLabel, textBox, spacer, arrowLabel);
        option.setOnMouseClicked(e -> action.run());
        
        return option;
    }
    
    /**
     * Interface for handling profile events
     */
    public interface ProfileEventHandler {
        void onSwitchToTab(String tabName);
        void onLogout();
        void onShowNotifications();
        void onNavigateToLogin();
        void onNavigateToSignup();
    }
}