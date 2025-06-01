package application.ui.client;

import application.model.User;
import application.service.NotificationService;
import application.service.UserSession;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class ProfileScreenBuilder {
    
    private final ProfileEventHandler eventHandler;
    
    public ProfileScreenBuilder(ProfileEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    /**
     * Create profile screen content - matches other builders' pattern
     */
    public VBox createProfileScreenContent() {
        VBox container = new VBox(15);
        container.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");
        
        try {
            if (UserSession.getInstance().isLoggedIn()) {
                User user = UserSession.getInstance().getCurrentUser();
                
                // Header section
                VBox headerSection = createLoggedInHeaderSection(user);
                
                // Menu options section
                VBox menuSection = createMenuOptionsSection();
                
                // Account section (simplified - no settings/privacy)
                VBox accountSection = createAccountSection();
                
                container.getChildren().addAll(headerSection, menuSection, accountSection);
            } else {
                // Guest welcome section
                VBox guestSection = createGuestWelcomeSection();
                container.getChildren().add(guestSection);
            }
        } catch (Exception e) {
            System.err.println("Error setting up profile screen: " + e.getMessage());
            VBox guestSection = createGuestWelcomeSection();
            container.getChildren().add(guestSection);
        }
        
        return container;
    }
    
    /**
     * Create logged in user header section - matches other builders' header style
     */
    private VBox createLoggedInHeaderSection(User user) {
        VBox headerCard = new VBox(15);
        headerCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        // User avatar and info
        HBox userInfo = new HBox(15);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        
        // Avatar circle
        VBox avatarBox = new VBox();
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.setStyle(
            "-fx-background-color: #2196F3; -fx-background-radius: 35; " +
            "-fx-min-width: 70; -fx-min-height: 70; -fx-max-width: 70; -fx-max-height: 70;"
        );
        
        Label avatarLabel = new Label("👤");
        avatarLabel.setStyle("-fx-font-size: 32px;");
        avatarBox.getChildren().add(avatarLabel);
        
        // User details
        VBox userDetails = new VBox(5);
        
        Label nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label emailLabel = new Label(user.getEmail());
        emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
      
        
        userDetails.getChildren().addAll(nameLabel, emailLabel);
        userInfo.getChildren().addAll(avatarBox, userDetails);
        
      
        headerCard.getChildren().addAll(userInfo);
        return headerCard;
    }
    
   
    /**
     * Create menu options section - matches card style
     */
    private VBox createMenuOptionsSection() {
        VBox menuCard = new VBox(8);
        menuCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        Label menuTitle = new Label("Quick Actions");
        menuTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        VBox menuItems = new VBox(8);
        menuItems.getChildren().addAll(
            createMenuOption("✈️", "My Bookings", "View your flight history", 
                () -> eventHandler.onSwitchToTab("bookings")),
            createNotificationMenuOption(),
            createMenuOption("💬", "Support", "Get help with your booking", 
                () -> eventHandler.onSwitchToTab("messages")),
            createMenuOption("🌍", "World Clock", "Check time zones", 
                () -> eventHandler.onSwitchToTab("time"))
        );
        
        menuCard.getChildren().addAll(menuTitle, menuItems);
        return menuCard;
    }
    
    /**
     * Create account section - SIMPLIFIED (no settings/privacy)
     */
    private VBox createAccountSection() {
        VBox accountCard = new VBox(8);
        accountCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        Label accountTitle = new Label("Account");
        accountTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        VBox accountItems = new VBox(8);
        // REMOVED: Settings and Privacy options
        accountItems.getChildren().add(createLogoutOption());
        
        accountCard.getChildren().addAll(accountTitle, accountItems);
        return accountCard;
    }
    
    /**
     * Create menu option - matches detail row style from other builders
     */
    private HBox createMenuOption(String icon, String title, String subtitle, Runnable action) {
        HBox option = new HBox(12);
        option.setAlignment(Pos.CENTER_LEFT);
        option.setStyle(
            "-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");
        iconLabel.setPrefWidth(24);
        
        VBox textBox = new VBox(2);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        textBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label arrowLabel = new Label("›");
        arrowLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        option.getChildren().addAll(iconLabel, textBox, spacer, arrowLabel);
        
        // Hover effects (same as other builders)
        option.setOnMouseEntered(e -> option.setStyle(
            "-fx-background-color: #e3f2fd; -fx-padding: 10; -fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        ));
        
        option.setOnMouseExited(e -> option.setStyle(
            "-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        ));
        
        option.setOnMouseClicked(e -> action.run());
        
        return option;
    }
    
    /**
     * Create notification menu option with badge - special styling
     */
    private HBox createNotificationMenuOption() {
        HBox option = new HBox(12);
        option.setAlignment(Pos.CENTER_LEFT);
        option.setStyle(
            "-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        );
        
        Label iconLabel = new Label("🔔");
        iconLabel.setStyle("-fx-font-size: 16px;");
        iconLabel.setPrefWidth(24);
        
        VBox textBox = new VBox(2);
        
        Label titleLabel = new Label("Notifications");
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label subtitleLabel = new Label("View your alerts and updates");
        subtitleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        
        textBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Notification badge
        int unreadCount = NotificationService.getUnreadCount();
        HBox badgeContainer = new HBox(8);
        badgeContainer.setAlignment(Pos.CENTER_RIGHT);
        
        if (unreadCount > 0) {
            Label badge = new Label(String.valueOf(unreadCount));
            badge.setStyle(
                "-fx-background-color: #FF4444; -fx-text-fill: white; " +
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6; " +
                "-fx-background-radius: 8; -fx-min-width: 16;"
            );
            badgeContainer.getChildren().add(badge);
        }
        
        Label arrowLabel = new Label("›");
        arrowLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        badgeContainer.getChildren().add(arrowLabel);
        
        option.getChildren().addAll(iconLabel, textBox, spacer, badgeContainer);
        
        // Hover effects
        option.setOnMouseEntered(e -> option.setStyle(
            "-fx-background-color: #e3f2fd; -fx-padding: 10; -fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        ));
        
        option.setOnMouseExited(e -> option.setStyle(
            "-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        ));
        
        option.setOnMouseClicked(e -> eventHandler.onShowNotifications());
        
        return option;
    }
    
    /**
     * Create logout option - special red styling
     */
    private HBox createLogoutOption() {
        HBox option = new HBox(12);
        option.setAlignment(Pos.CENTER_LEFT);
        option.setStyle(
            "-fx-background-color: #ffebee; -fx-padding: 10; -fx-background-radius: 6; " +
            "-fx-border-color: #ffcdd2; -fx-border-width: 1; -fx-border-radius: 6; " +
            "-fx-cursor: hand;"
        );
        
        Label iconLabel = new Label("🚪");
        iconLabel.setStyle("-fx-font-size: 16px;");
        iconLabel.setPrefWidth(24);
        
        VBox textBox = new VBox(2);
        
        Label titleLabel = new Label("Sign Out");
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #c62828;");
        
        Label subtitleLabel = new Label("Log out of your account");
        subtitleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #e57373;");
        
        textBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label arrowLabel = new Label("›");
        arrowLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #c62828;");
        
        option.getChildren().addAll(iconLabel, textBox, spacer, arrowLabel);
        
        // Hover effects - red theme
        option.setOnMouseEntered(e -> option.setStyle(
            "-fx-background-color: #f8bbd9; -fx-padding: 10; -fx-background-radius: 6; " +
            "-fx-border-color: #f48fb1; -fx-border-width: 1; -fx-border-radius: 6; " +
            "-fx-cursor: hand;"
        ));
        
        option.setOnMouseExited(e -> option.setStyle(
            "-fx-background-color: #ffebee; -fx-padding: 10; -fx-background-radius: 6; " +
            "-fx-border-color: #ffcdd2; -fx-border-width: 1; -fx-border-radius: 6; " +
            "-fx-cursor: hand;"
        ));
        
        option.setOnMouseClicked(e -> eventHandler.onLogout());
        
        return option;
    }
    
    /**
     * Create guest welcome section - matches welcome card pattern
     */
    private VBox createGuestWelcomeSection() {
        VBox guestCard = new VBox(20);
        guestCard.setAlignment(Pos.CENTER);
        guestCard.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; " +
            "-fx-padding: 30; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        
        Label iconLabel = new Label("👋");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label("Welcome to JetSetGo");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c2c2c;");
        
        Label subtitleLabel = new Label("Sign in to access your bookings and get personalized recommendations");
        subtitleLabel.setStyle(
            "-fx-font-size: 12px; -fx-text-fill: #666; -fx-wrap-text: true; " +
            "-fx-text-alignment: center; -fx-max-width: 300;"
        );
        
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button loginButton = new Button("Sign In");
        loginButton.setStyle(
            "-fx-background-color: #2196F3; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-padding: 12 25; -fx-background-radius: 20; " +
            "-fx-border-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;"
        );
        loginButton.setMaxWidth(200);
        
        // Hover effects
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
            "-fx-background-color: #1976D2; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-padding: 12 25; -fx-background-radius: 20; " +
            "-fx-border-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;"
        ));
        
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
            "-fx-background-color: #2196F3; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-padding: 12 25; -fx-background-radius: 20; " +
            "-fx-border-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;"
        ));
        
        loginButton.setOnAction(e -> eventHandler.onNavigateToLogin());
        
        Button signupButton = new Button("Create Account");
        signupButton.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #2196F3; " +
            "-fx-font-size: 14px; -fx-padding: 12 25; -fx-border-color: #2196F3; " +
            "-fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 1; " +
            "-fx-font-weight: bold; -fx-cursor: hand;"
        );
        signupButton.setMaxWidth(200);
        signupButton.setOnAction(e -> eventHandler.onNavigateToSignup());
        
        buttonBox.getChildren().addAll(loginButton, signupButton);
        
        guestCard.getChildren().addAll(iconLabel, titleLabel, subtitleLabel, buttonBox);
        return guestCard;
    }
    
    
    public interface ProfileEventHandler {
        void onSwitchToTab(String tabName);
        void onLogout();
        void onShowNotifications();
        void onNavigateToLogin();
        void onNavigateToSignup();
    }
}