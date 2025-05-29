package application;

import application.model.Flight;
import application.model.User;
import application.model.UserSession;
import application.service.FlightService;
import application.util.FlightListCell;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class HomeController {

    // Header components
    @FXML private Label headerTitle;
    @FXML private Button profileButton;
    // @FXML private Button menuButton;
    @FXML private HBox statusBar;
    @FXML private ProgressIndicator headerProgress;
    @FXML private Label statusLabel;

    // Content screens
    @FXML private StackPane contentStack;
    @FXML private VBox homeScreen;
    @FXML private VBox bookingsScreen;
    @FXML private VBox messagesScreen;
    @FXML private VBox profileScreen;
    @FXML private VBox flightDetailsScreen;
    @FXML private VBox bookingFormScreen;
    @FXML private VBox paymentScreen;
    @FXML private VBox confirmationScreen;

    // Bottom navigation
    @FXML private HBox bottomNav;
    @FXML private VBox homeTab;
    @FXML private VBox bookingsTab;
    @FXML private VBox messagesTab;
    @FXML private VBox profileTab;

    // Home screen elements
    @FXML private ListView<Flight> flightListView;
    @FXML private TextField searchField;
    @FXML private Label sectionLabel;
    @FXML private ProgressIndicator loadingIndicator;

    // Content areas
    @FXML private VBox bookingsContent;
    @FXML private VBox messagesContent;
    @FXML private VBox profileContent;
    @FXML private VBox flightDetailsContent;
    @FXML private VBox bookingFormContent;
    @FXML private VBox paymentContent;
    @FXML
    private VBox confirmationContent;
    

    // Action buttons
    @FXML private Button bookFlightBtn;
    @FXML private Button continueToPaymentBtn;
    @FXML private Button confirmPaymentBtn;
    @FXML private Button newBookingBtn;

    // Services and data
    private FlightService flightService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
    
    // Current booking data
    private Flight currentFlight;
    private String currentBookingRef;
    private double currentTotalPrice;
    private TextField firstNameField, lastNameField, emailField, phoneField;
    private ToggleGroup paymentMethodGroup;
    

    // Current active tab
    private String currentTab = "home";

    // Banner carousel elements
    private int currentBannerIndex = 0;
    private final String[] bannerTitles = {
        "Dream. Discover. GO", 
        "Explore New Places", 
        "Fly with Confidence"
    };

    @FXML
    public void initialize() {
        System.out.println("Mobile HomeController initializing...");
        
        try {
            flightService = new FlightService();
            
            // Check if all required FXML elements are injected
            if (!checkFXMLElements()) {
                System.err.println("Some FXML elements are not properly injected");
                return;
            }
            
            setupBottomNavigation();
            setupHomeScreen();
            setupProfileScreen();
            setupBookingsScreen();
            setupMessagesScreen();
            loadAvailableFlights();
            
            // Setup menu button
            // if (menuButton != null) {
            //     menuButton.setOnAction(e -> toggleMenu());
            // }
            
            System.out.println("Mobile HomeController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing HomeController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Add this method to check FXML element injection
    private boolean checkFXMLElements() {
        boolean allElementsPresent = true;

        if (profileContent == null) {
            System.err.println("profileContent is null - check FXML fx:id");
            allElementsPresent = false;
        }
        if (bookingsContent == null) {
            System.err.println("bookingsContent is null - check FXML fx:id");
            allElementsPresent = false;
        }
        if (messagesContent == null) {
            System.err.println("messagesContent is null - check FXML fx:id");
            allElementsPresent = false;
        }
        if (flightDetailsContent == null) {
            System.err.println("flightDetailsContent is null - check FXML fx:id");
            allElementsPresent = false;
        }
        if (flightListView == null) {
            System.err.println("flightListView is null - check FXML fx:id");
            allElementsPresent = false;
        }

        return allElementsPresent;
    }

    
    // private void toggleMenu() {
    //     // Implementation for menu button action
    //     showMobileAlert("Menu", "Menu functionality coming soon!");
    // }

    private void setupBottomNavigation() {
        // Set up tab click handlers - add null checks
        if (homeTab != null) homeTab.setOnMouseClicked(e -> switchToTab("home"));
        if (bookingsTab != null) bookingsTab.setOnMouseClicked(e -> switchToTab("bookings"));
        if (messagesTab != null) messagesTab.setOnMouseClicked(e -> switchToTab("messages"));
        if (profileTab != null) profileTab.setOnMouseClicked(e -> switchToTab("profile"));
        
        // Profile button in header
        if (profileButton != null) profileButton.setOnAction(e -> switchToTab("profile"));
        
        // Action buttons - add null checks
        if (continueToPaymentBtn != null) continueToPaymentBtn.setOnAction(e -> showPaymentForm());
        if (confirmPaymentBtn != null) confirmPaymentBtn.setOnAction(e -> processPayment());
        if (newBookingBtn != null) newBookingBtn.setOnAction(e -> switchToTab("home"));
    }

    private void setupHomeScreen() {
        if (flightListView == null) {
            System.err.println("flightListView is null, skipping home setup");
            return;
        }
        
        // Flight list setup
        flightListView.setPlaceholder(new Label("No flights available"));
        
        // Store controller reference in the ListView for FlightListCell access
        flightListView.getProperties().put("controller", this);
        
        // Custom cell factory for card-like display
        flightListView.setCellFactory(listView -> new FlightListCell());
        
        // Configure ListView for vertical scrolling only
        flightListView.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                               "-fx-focus-color: transparent; -fx-faint-focus-color: transparent; " +
                               "-fx-selection-bar: transparent; -fx-selection-bar-non-focused: transparent;");
        
        // Search functionality
        if (searchField != null) {
            searchField.setOnAction(this::handleSearch);
        }
    }

    private void setupProfileScreen() {
        if (profileContent == null) {
            System.err.println("profileContent is null, skipping profile setup");
            return;
        }
        
        profileContent.getChildren().clear();
        
        try {
            if (UserSession.getInstance().isLoggedIn()) {
                User user = UserSession.getInstance().getCurrentUser();
                profileContent.getChildren().add(createLoggedInProfile(user));
            } else {
                profileContent.getChildren().add(createGuestProfile());
            }
        } catch (Exception e) {
            System.err.println("Error setting up profile screen: " + e.getMessage());
            profileContent.getChildren().add(createGuestProfile());
        }
    }
    

    private void setupBookingsScreen() {
        if (bookingsContent == null) {
            System.err.println("bookingsContent is null, skipping bookings setup");
            return;
        }
        
        bookingsContent.getChildren().clear();
        
        try {
            if (UserSession.getInstance().isLoggedIn()) {
                loadUserBookings();
            } else {
                VBox loginPrompt = createLoginPrompt();
                bookingsContent.getChildren().add(loginPrompt);
            }
        } catch (Exception e) {
            System.err.println("Error setting up bookings screen: " + e.getMessage());
            VBox errorCard = createErrorCard("Unable to load bookings");
            bookingsContent.getChildren().add(errorCard);
        }
    }

    private void setupMessagesScreen() {
        if (messagesContent == null) {
            System.err.println("messagesContent is null, skipping messages setup");
            return;
        }
        
        messagesContent.getChildren().clear();
        
        try {
            if (UserSession.getInstance().isLoggedIn()) {
                loadUserMessages();
            } else {
                VBox loginPrompt = createMessagesLoginPrompt();
                messagesContent.getChildren().add(loginPrompt);
            }
        } catch (Exception e) {
            System.err.println("Error setting up messages screen: " + e.getMessage());
            VBox errorCard = createErrorCard("Unable to load messages");
            messagesContent.getChildren().add(errorCard);
        }
    }

    private VBox createLoggedInProfile(User user) {
        VBox profileCard = new VBox(15);
        profileCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");
        
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
            createProfileOption("✈️", "My Bookings", "View your flight bookings"),
            createProfileOption("🔔", "Notifications", "Manage notifications"),
            createProfileOption("⚙️", "Settings", "App preferences"),
            createProfileOption("🔒", "Privacy", "Privacy settings"),
            createProfileOption("📞", "Support", "Get help"),
            createProfileOption("🚪", "Logout", "Sign out of your account")
        );
        
        profileCard.getChildren().addAll(headerBox, new Separator(), optionsBox);
        return profileCard;
    }

    private VBox createGuestProfile() {
        VBox guestCard = new VBox(20);
        guestCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; -fx-alignment: center;");
        
        Label iconLabel = new Label("👋");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label("Welcome to JetSetGO");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label subtitleLabel = new Label("Sign in to access your bookings and get personalized recommendations");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");
        
        Button loginButton = new Button("Sign In");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 20;");
        
        Button signupButton = new Button("Create Account");
        signupButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2196F3; -fx-font-size: 14px; -fx-padding: 12 30; -fx-border-color: #2196F3; -fx-border-radius: 20; -fx-background-radius: 20;");
        
        guestCard.getChildren().addAll(iconLabel, titleLabel, subtitleLabel, loginButton, signupButton);
        return guestCard;
    }

    private HBox createProfileOption(String icon, String title, String subtitle) {
        HBox option = new HBox(12);
        option.setStyle("-fx-alignment: center-left; -fx-padding: 12; -fx-background-color: #f8f9fa; -fx-background-radius: 10;");
        
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
        return option;
    }

    private VBox createLoginPrompt() {
        VBox promptCard = new VBox(15);
        promptCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; -fx-alignment: center;");
        
        Label iconLabel = new Label("📋");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label("Your Bookings");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label messageLabel = new Label("Sign in to view your flight bookings and travel history");
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");
        
        Button loginButton = new Button("Sign In to View Bookings");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 25; -fx-background-radius: 20;");
        
        promptCard.getChildren().addAll(iconLabel, titleLabel, messageLabel, loginButton);
        return promptCard;
    }

    private VBox createMessagesLoginPrompt() {
        VBox promptCard = new VBox(15);
        promptCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; -fx-alignment: center;");
        
        Label iconLabel = new Label("💬");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label("Messages");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label messageLabel = new Label("Sign in to view your conversations with travel agents and services");
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");
        
        Button loginButton = new Button("Sign In to View Messages");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 25; -fx-background-radius: 20;");
        
        promptCard.getChildren().addAll(iconLabel, titleLabel, messageLabel, loginButton);
        return promptCard;
    }

    private VBox createErrorCard(String message) {
        VBox errorCard = new VBox(15);
        errorCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; -fx-alignment: center;");
        
        Label iconLabel = new Label("⚠️");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");
        
        Button retryButton = new Button("Try Again");
        retryButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 25; -fx-background-radius: 20;");
        
        errorCard.getChildren().addAll(iconLabel, messageLabel, retryButton);
        return errorCard;
    }

    private void loadUserBookings() {
        if (bookingsContent == null) {
            System.err.println("bookingsContent is null in loadUserBookings");
            return;
        }
        
        // Implementation for loading user bookings
        VBox placeholderCard = new VBox(15);
        placeholderCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; -fx-alignment: center;");
        
        Label iconLabel = new Label("🎯");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label("No Bookings Yet");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label messageLabel = new Label("Book your first flight to see it here!");
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        Button browseButton = new Button("Browse Flights");
        browseButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 25; -fx-background-radius: 20;");
        browseButton.setOnAction(e -> switchToTab("home"));
        
        placeholderCard.getChildren().addAll(iconLabel, titleLabel, messageLabel, browseButton);
        bookingsContent.getChildren().add(placeholderCard);
    }

    private void loadUserMessages() {
        if (messagesContent == null) {
            System.err.println("messagesContent is null in loadUserMessages");
            return;
        }
        
        // Create messages placeholder - replace with actual message loading logic
        VBox placeholderCard = new VBox(15);
        placeholderCard.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; -fx-alignment: center;");
        
        Label iconLabel = new Label("💬");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label("No messages yet");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label messageLabel = new Label("Your conversations with travel agents and services will appear here.");
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");
        
        Button supportButton = new Button("Contact Support");
        supportButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 25; -fx-background-radius: 20;");
        supportButton.setOnAction(e -> openSupportChat());
        
        placeholderCard.getChildren().addAll(iconLabel, titleLabel, messageLabel, supportButton);
        messagesContent.getChildren().add(placeholderCard);
    }

    private void openSupportChat() {
        // Implementation for opening support chat
        showMobileAlert("Support", "Support chat feature coming soon!");
    }

    private void switchToTab(String tabName) {
        // Hide all screens
        homeScreen.setVisible(false);
        bookingsScreen.setVisible(false);
        messagesScreen.setVisible(false);
        profileScreen.setVisible(false);
        flightDetailsScreen.setVisible(false);
        bookingFormScreen.setVisible(false);
        paymentScreen.setVisible(false);
        confirmationScreen.setVisible(false);
        
        // Update tab styles
        updateTabStyles(tabName);
        
        // Update header
        updateHeader(tabName);
        
        // Show selected screen
        switch (tabName) {
            case "home":
                homeScreen.setVisible(true);
                bottomNav.setVisible(true);
                currentTab = "home";
                break;
            case "bookings":
                bookingsScreen.setVisible(true);
                bottomNav.setVisible(true);
                setupBookingsScreen(); // Refresh bookings
                currentTab = "bookings";
                break;
            case "messages":
                messagesScreen.setVisible(true);
                bottomNav.setVisible(true);
                setupMessagesScreen(); // Refresh messages
                currentTab = "messages";
                break;
            case "profile":
                profileScreen.setVisible(true);
                bottomNav.setVisible(true);
                setupProfileScreen(); // Refresh profile
                currentTab = "profile";
                break;
            case "flight-details":
                flightDetailsScreen.setVisible(true);
                bottomNav.setVisible(false);
                break;
            case "booking-form":
                bookingFormScreen.setVisible(true);
                bottomNav.setVisible(false);
                break;
            case "payment":
                paymentScreen.setVisible(true);
                bottomNav.setVisible(false);
                break;
            case "confirmation":
                confirmationScreen.setVisible(true);
                bottomNav.setVisible(false);
                break;
        }
    }

    private void updateTabStyles(String activeTab) {
        // Reset all tabs
        resetTabStyle(homeTab, "🏠", "Home");
        resetTabStyle(bookingsTab, "📋", "Bookings");
        resetTabStyle(messagesTab, "💬", "Messages");
        resetTabStyle(profileTab, "👤", "Profile");
        
        // Highlight active tab
        switch (activeTab) {
            case "home":
                setActiveTabStyle(homeTab, "🏠", "Home");
                break;
            case "bookings":
                setActiveTabStyle(bookingsTab, "📋", "Bookings");
                break;
            case "messages":
                setActiveTabStyle(messagesTab, "💬", "Messages");
                break;
            case "profile":
                setActiveTabStyle(profileTab, "👤", "Profile");
                break;
        }
    }

    private void resetTabStyle(VBox tab, String icon, String text) {
        tab.getChildren().clear();
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        tab.getChildren().addAll(iconLabel, textLabel);
    }

    private void setActiveTabStyle(VBox tab, String icon, String text) {
        tab.getChildren().clear();
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #2196F3; -fx-font-weight: bold;");
        tab.getChildren().addAll(iconLabel, textLabel);
    }

    private void updateHeader(String tabName) {
        switch (tabName) {
            case "home":
                headerTitle.setText("✈️ JetSetGO");
                break;
            case "bookings":
                headerTitle.setText("📋 My Bookings");
                break;
            case "messages":
                headerTitle.setText("💬 Messages");
                break;
            case "profile":
                headerTitle.setText("👤 Profile");
                break;
            case "flight-details":
                headerTitle.setText("✈️ Flight Details");
                break;
            case "booking-form":
                headerTitle.setText("📝 Passenger Info");
                break;
            case "payment":
                headerTitle.setText("💳 Payment");
                break;
            case "confirmation":
                headerTitle.setText("✅ Confirmed");
                break;
        }
    }

    private void showStatus(String message, boolean showProgress) {
        statusLabel.setText(message);
        headerProgress.setVisible(showProgress);
        statusBar.setVisible(true);
        statusBar.setManaged(true);
    }

    private void hideStatus() {
        statusBar.setVisible(false);
        statusBar.setManaged(false);
    }
    public void showFlightDetails(Flight flight) {
        currentFlight = flight;
        
        // Clear previous content
        flightDetailsContent.getChildren().clear();
        
        // Create enhanced mobile flight details
        VBox detailsCard = createEnhancedMobileFlightDetails(flight);
        flightDetailsContent.getChildren().add(detailsCard);
        
        // Show details screen
        switchToTab("flight-details");
    }

    private VBox createEnhancedMobileFlightDetails(Flight flight) {
        VBox container = new VBox(20);
        container.setStyle("-fx-padding: 10;");
        container.setFillWidth(true);
        container.setMaxWidth(Double.MAX_VALUE);

        // Back button
        Button backButton = new Button("Back to Flights");
        backButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #333; " +
                           "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; " +
                           "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1; " +
                           "-fx-cursor: hand; -fx-font-weight: 500;");
        
        // Add hover effect (optional)
        backButton.setOnMouseEntered(e -> {
            backButton.setStyle("-fx-background-color: #e8e8e8; -fx-text-fill: #333; " +
                               "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; " +
                               "-fx-border-color: #ccc; -fx-border-radius: 8; -fx-border-width: 1; " +
                               "-fx-cursor: hand; -fx-font-weight: 500;");
        });
        
        backButton.setOnMouseExited(e -> {
            backButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #333; " +
                               "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; " +
                               "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1; " +
                               "-fx-cursor: hand; -fx-font-weight: 500;");
        });
        
        // Main flight card
        VBox card = new VBox(20);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 3);");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setFillWidth(true);

        // Flight header
        VBox headerSection = createFlightHeader(flight);

        // Route section with improved layout
        VBox routeSection = createEnhancedRouteSection(flight);

        // Flight details in a more organized way
        VBox detailsSection = createComprehensiveFlightDetails(flight);

        // Price section
        VBox priceSection = createPriceSection(flight);

        // Book button
        Button bookButton = new Button("Book This Flight");
        bookButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                "-fx-font-size: 16px; -fx-padding: 15 30; -fx-background-radius: 25; " +
                "-fx-font-weight: bold; -fx-cursor: hand;");
        bookButton.setMaxWidth(Double.MAX_VALUE);
        bookButton.setOnAction(e -> handleBookFlight());

        card.getChildren().addAll(headerSection, routeSection, detailsSection, priceSection, bookButton);
        container.getChildren().addAll(backButton, card);

        return container;
    }

    private VBox createFlightHeader(Flight flight) {
        VBox headerSection = new VBox(15);

        // Airline and flight number
        VBox airlineInfo = new VBox(5);
        Label airlineLabel = new Label(flight.getAirlineName());
        airlineLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1565C0;");
        airlineLabel.setWrapText(true);

        Label flightNoLabel = new Label("Flight " + flight.getFlightNo());
        flightNoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");

        airlineInfo.getChildren().addAll(airlineLabel, flightNoLabel);

        // Status badge
        HBox statusRow = new HBox();
        statusRow.setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label(flight.getStatus().toUpperCase());
        statusLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-padding: 8 16; -fx-background-radius: 20; " +
                "-fx-font-size: 12px; -fx-font-weight: bold;");

        statusRow.getChildren().add(statusLabel);

        headerSection.getChildren().addAll(airlineInfo, statusRow);
        return headerSection;
    }
    
    private void handleBookFlight() {
        // Navigate to booking form
        switchToTab("booking-form");
    }

    
    
    private VBox createEnhancedRouteSection(Flight flight) {
        VBox routeSection = new VBox(20);
        routeSection.setAlignment(Pos.CENTER);
        routeSection.setStyle("-fx-padding: 20 10;");

        // Airport codes and labels
        HBox airportsRow = new HBox();
        airportsRow.setAlignment(Pos.CENTER);
        airportsRow.setSpacing(20);

        // Origin
        VBox originBox = new VBox(8);
        originBox.setAlignment(Pos.CENTER);
        originBox.setMaxWidth(80);

        Label originCode = new Label(flight.getOrigin());
        originCode.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        Label originLabel = new Label("Departure");
        originLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-weight: bold;");

        originBox.getChildren().addAll(originCode, originLabel);

        // Flight path with plane icon
        VBox pathBox = new VBox(8);
        pathBox.setAlignment(Pos.CENTER);
        pathBox.setMaxWidth(60);

        Label planeIcon = new Label("✈️");
        planeIcon.setStyle("-fx-font-size: 24px;");

        Label durationLabel = new Label(flight.getDuration());
        durationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        durationLabel.setWrapText(true);

        pathBox.getChildren().addAll(planeIcon, durationLabel);

        // Destination
        VBox destBox = new VBox(8);
        destBox.setAlignment(Pos.CENTER);
        destBox.setMaxWidth(80);

        Label destCode = new Label(flight.getDestination());
        destCode.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        Label destLabel = new Label("Arrival");
        destLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-weight: bold;");

        destBox.getChildren().addAll(destCode, destLabel);

        airportsRow.getChildren().addAll(originBox, pathBox, destBox);

        // Times section
        VBox timesSection = new VBox(15);
        timesSection.setAlignment(Pos.CENTER);

        // Departure time
        VBox depSection = createTimeSection("Departure", flight.getDeparture());

        // Arrival time  
        VBox arrSection = createTimeSection("Arrival", flight.getArrival());

        timesSection.getChildren().addAll(depSection, arrSection);

        routeSection.getChildren().addAll(airportsRow, timesSection);
        return routeSection;
    }

    private VBox createTimeSection(String label, java.time.LocalDateTime dateTime) {
        VBox timeSection = new VBox(5);
        timeSection.setAlignment(Pos.CENTER);
        timeSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 12; -fx-background-radius: 10;");

        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-font-weight: bold;");

        Label timeLabel = new Label(dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label dateLabel = new Label(dateTime.format(DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy")));
        dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        dateLabel.setWrapText(true);

        timeSection.getChildren().addAll(titleLabel, timeLabel, dateLabel);
        return timeSection;
    }
    
    private VBox createPriceSection(Flight flight) {
        VBox priceSection = new VBox(15);
        priceSection.setAlignment(Pos.CENTER);
        priceSection.setStyle("-fx-background-color: #E8F5E8; -fx-padding: 20; -fx-background-radius: 12;");

        Label priceTitle = new Label("Total Price");
        priceTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-font-weight: bold;");

        Label priceAmount = new Label(currencyFormat.format(flight.getPrice()));
        priceAmount.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        priceAmount.setWrapText(true);

        Label priceNote = new Label("per person • includes taxes & fees");
        priceNote.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        priceNote.setWrapText(true);

        priceSection.getChildren().addAll(priceTitle, priceAmount, priceNote);
        return priceSection;
    }
    
    private VBox createComprehensiveFlightDetails(Flight flight) {
        VBox detailsSection = new VBox(15);
        detailsSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-background-radius: 12;");

        Label detailsTitle = new Label("Flight Information");
        detailsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox detailsGrid = new VBox(15);

        // Flight details with better formatting
        detailsGrid.getChildren().addAll(
                createWideDetailRow("✈️ Aircraft Type", flight.getAircraft()),
                createWideDetailRow("💺 Available Seats", String.valueOf(flight.getSeats()) + " seats"),
                createWideDetailRow("📅 Flight Date",
                        flight.getDeparture().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))),
                createWideDetailRow("⏱️ Flight Duration", flight.getDuration()),
                createWideDetailRow("🛫 Departure Airport", flight.getOrigin()),
                createWideDetailRow("🛬 Arrival Airport", flight.getDestination()));

        detailsSection.getChildren().addAll(detailsTitle, detailsGrid);
        return detailsSection;
    }


    private VBox createWideDetailRow(String label, String value) {
        VBox row = new VBox(5);
        row.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 8;");
        row.setMaxWidth(Double.MAX_VALUE);

        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-font-weight: bold;");
        labelText.setWrapText(true);

        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 15px; -fx-text-fill: #333; -fx-font-weight: bold;");
        valueText.setWrapText(true);
        valueText.setMaxWidth(Double.MAX_VALUE);

        row.getChildren().addAll(labelText, valueText);
        return row;
    }

    
    
    private void showPaymentForm() {
        // Validate form
        if (!validateBookingForm()) {
            return;
        }
        
        // Clear previous content
        paymentContent.getChildren().clear();
        
        // Build mobile payment form
        buildMobilePaymentForm();
        
        // Show payment screen
        switchToTab("payment");
    }

    private boolean validateBookingForm() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showMobileAlert("Required Fields", "Please fill in your first name and last name.");
            return false;
        }
        
        return true;
    }

    private void buildMobilePaymentForm() {
        // Booking summary
        VBox bookingSummary = createMobileBookingSummary();
        paymentContent.getChildren().add(bookingSummary);
        
        // Payment methods
        VBox paymentMethods = createMobilePaymentMethods();
        paymentContent.getChildren().add(paymentMethods);
        
        // Security note
        VBox securityNote = createMobileSecurityNote();
        paymentContent.getChildren().add(securityNote);
    }

    private VBox createMobileBookingSummary() {
        VBox card = new VBox(15);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");

        Label titleLabel = new Label("Booking Summary");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        VBox detailsBox = new VBox(15);
        detailsBox.getChildren().addAll(
                createDetailRow("Flight", currentFlight.getFlightNo()),
                createDetailRow("Route", currentFlight.getOrigin() + " → " + currentFlight.getDestination()),
                createDetailRow("Passenger", firstNameField.getText() + " " + lastNameField.getText()),
                createDetailRow("Date",
                        currentFlight.getDeparture().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))),
                createDetailRow("Duration", currentFlight.getDuration()));

        VBox totalBox = new VBox(5);
        totalBox.setStyle("-fx-background-color: #FFF3E0; -fx-padding: 12; -fx-background-radius: 10;");

        Label totalLabel = new Label("Total Amount");
        totalLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label amountLabel = new Label(currencyFormat.format(currentTotalPrice));
        amountLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FF9800;");

        totalBox.getChildren().addAll(totalLabel, amountLabel);

        card.getChildren().addAll(titleLabel, detailsBox, totalBox);
        return card;
    }

    
    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(10);

        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-font-weight: 500;");
        labelText.setPrefWidth(120); // Fixed width for alignment

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 13px; -fx-text-fill: #333; -fx-font-weight: bold;");
        valueText.setWrapText(true);

        row.getChildren().addAll(labelText, spacer, valueText);
        return row;
    }
    

    private VBox createMobilePaymentMethods() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");
        
        Label titleLabel = new Label("Select Payment Method");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        paymentMethodGroup = new ToggleGroup();
        
        VBox methodsBox = new VBox(12);
        
        // Payment method options
        RadioButton creditCardRadio = createMobilePaymentMethod("💳", "Credit/Debit Card", "Visa, Mastercard, JCB", "credit_card");
        creditCardRadio.setSelected(true);
        
        RadioButton gcashRadio = createMobilePaymentMethod("🔵", "GCash", "Fast & Secure Payment", "gcash");
        RadioButton mayaRadio = createMobilePaymentMethod("🟢", "Maya (PayMaya)", "Instant Digital Payment", "maya");
        RadioButton paypalRadio = createMobilePaymentMethod("🔷", "PayPal", "Global Payment Solution", "paypal");
        
        methodsBox.getChildren().addAll(creditCardRadio, gcashRadio, mayaRadio, paypalRadio);
        
        card.getChildren().addAll(titleLabel, methodsBox);
        return card;
    }

    private RadioButton createMobilePaymentMethod(String icon, String title, String subtitle, String userData) {
        RadioButton radio = new RadioButton();
        radio.setToggleGroup(paymentMethodGroup);
        radio.setUserData(userData);
        
        HBox content = new HBox(15);
        content.setStyle("-fx-alignment: center-left; -fx-padding: 15; -fx-background-color: #f8f9fa; -fx-background-radius: 12;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");
        
        VBox textBox = new VBox(3);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        textBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        content.getChildren().addAll(iconLabel, textBox);
        radio.setGraphic(content);
        radio.setStyle("-fx-padding: 0;");
        
        return radio;
    }

    private VBox createMobileSecurityNote() {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #E3F2FD; -fx-background-radius: 15; -fx-padding: 15;");

        HBox headerBox = new HBox(10);
        headerBox.setStyle("-fx-alignment: center-left;");
        
        Label iconLabel = new Label("🔒");
        iconLabel.setStyle("-fx-font-size: 18px;");
        
        Label titleLabel = new Label("Secure Payment");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        
        headerBox.getChildren().addAll(iconLabel, titleLabel);

        Label securityLabel = new Label("Your payment information is encrypted and secure. We never store your card details.");
        securityLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-wrap-text: true;");

        card.getChildren().addAll(headerBox, securityLabel);
        return card;
    }

    private void processPayment() {
        try {
            showStatus("Processing payment...", true);
            
            String paymentMethod = getSelectedPaymentMethod();
            currentBookingRef = "JET-" + System.currentTimeMillis() % 10000;
            String transactionRef = generateTransactionReference(paymentMethod);
            
            int userId = 0;
            if (UserSession.getInstance().isLoggedIn()) {
                userId = UserSession.getInstance().getUserId();
            }
            
            flightService.createBookingWithPayment(
                currentFlight, userId,
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                currentBookingRef,
                currentTotalPrice,
                paymentMethod,
                transactionRef
            ).thenAccept(success -> {
                Platform.runLater(() -> {
                    hideStatus();
                    if (success) {
                        showConfirmation();
                    } else {
                        showMobileAlert("Payment Failed", "Payment could not be processed. Please try again.");
                    }
                });
            }).exceptionally(ex -> {
                Platform.runLater(() -> {
                    hideStatus();
                    showMobileAlert("Payment Error", "An error occurred: " + ex.getMessage());
                });
                return null;
            });
            
        } catch (Exception e) {
            hideStatus();
            showMobileAlert("Payment Error", "Payment processing error: " + e.getMessage());
        }
    }

    private String getSelectedPaymentMethod() {
        RadioButton selected = (RadioButton) paymentMethodGroup.getSelectedToggle();
        return selected != null ? selected.getUserData().toString() : "credit_card";
    }

    private String generateTransactionReference(String paymentMethod) {
        String prefix;
        switch (paymentMethod) {
            case "gcash": prefix = "GC"; break;
            case "maya": prefix = "MY"; break;
            case "paypal": prefix = "PP"; break;
            default: prefix = "CC"; break;
        }
        return prefix + "-" + System.currentTimeMillis() % 1000000;
    }

    private void showConfirmation() {
        // Clear previous content
        confirmationContent.getChildren().clear();
        
        // Create mobile confirmation
        VBox confirmationCard = createMobileConfirmation();
        confirmationContent.getChildren().add(confirmationCard);
        
        // Show confirmation screen
        switchToTab("confirmation");
    }

    private VBox createMobileConfirmation() {
        VBox card = new VBox(20);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 25; -fx-border-color: #4CAF50; -fx-border-radius: 15; -fx-border-width: 2;");
        
        // Success header
        VBox headerBox = new VBox(10);
        headerBox.setStyle("-fx-alignment: center;");
        
        Label successLabel = new Label("🎉");
        successLabel.setStyle("-fx-font-size: 40px;");
        
        Label titleLabel = new Label("Booking Confirmed!");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        Label subTitleLabel = new Label("Your flight has been successfully booked");
        subTitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        headerBox.getChildren().addAll(successLabel, titleLabel, subTitleLabel);
        
        // Booking details
        VBox detailsBox = new VBox(12);
        detailsBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");
        
        Label detailsTitle = new Label("Booking Details");
        detailsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        VBox detailsList = new VBox(8);
        detailsList.getChildren().addAll(
            createDetailRow("Booking Reference", currentBookingRef),
            createDetailRow("Flight", currentFlight.getFlightNo()),
            createDetailRow("Route", currentFlight.getOrigin() + " → " + currentFlight.getDestination()),
            createDetailRow("Date", currentFlight.getDeparture().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))),
            createDetailRow("Passenger", firstNameField.getText() + " " + lastNameField.getText()),
            createDetailRow("Amount Paid", currencyFormat.format(currentTotalPrice))
        );
        
        detailsBox.getChildren().addAll(detailsTitle, detailsList);
        
        // Important info
        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 15; -fx-background-radius: 10;");
        
        Label infoTitle = new Label("Important Information");
        infoTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        
        VBox infoList = new VBox(5);
        String[] infoItems = {
            "• Save your booking reference: " + currentBookingRef,
            "• Arrive at airport 2 hours before departure",
            "• Bring valid ID and travel documents",
            "• Check-in online or at airport counter",
            "• Confirmation email sent to " + emailField.getText()
        };
        
        for (String item : infoItems) {
            Label infoItem = new Label(item);
            infoItem.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-wrap-text: true;");
            infoList.getChildren().add(infoItem);
        }
        
        infoBox.getChildren().addAll(infoTitle, infoList);
        
        card.getChildren().addAll(headerBox, detailsBox, infoBox);
        return card;
    }

    private void showMobileAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style for mobile
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setPrefSize(300, 180);
        dialogPane.setStyle("-fx-font-size: 13px; -fx-background-radius: 15;");
        
        alert.showAndWait();
    }

    private void filterFlightsByDate(String period) {
        showStatus("Filtering flights by " + period + "...", true);
        
        // Simulate filtering (replace with actual implementation)
        Platform.runLater(() -> {
            hideStatus();
            System.out.println("Filtered flights by: " + period);
        });
    }

    private void loadAvailableFlights() {
        showStatus("Loading flights...", true);
        
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }
        
        if (sectionLabel != null) {
            sectionLabel.setText("Loading...");
        }
        
        flightService.getAvailableFlights()
            .thenAccept(flights -> {
                Platform.runLater(() -> {
                    try {
                        flightListView.setItems(flights);
                        
                        if (loadingIndicator != null) {
                            loadingIndicator.setVisible(false);
                        }
                        
                        if (sectionLabel != null) {
                            sectionLabel.setText("Available Flights (" + flights.size() + ")");
                        }
                        
                        hideStatus();
                    } catch (Exception e) {
                        System.err.println("Error updating UI with flights: " + e.getMessage());
                        e.printStackTrace();
                        hideStatus();
                    }
                });
            })
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    
                    if (sectionLabel != null) {
                        sectionLabel.setText("Error loading flights");
                    }
                    
                    hideStatus();
                });
                return null;
            });
    }

    private void handleSearch(ActionEvent event) {
        String query = searchField.getText().trim();
        
        if (query.isEmpty()) {
            loadAvailableFlights();
            return;
        }
        
        showStatus("Searching for \"" + query + "\"...", true);
        
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }
        
        flightService.searchFlights(query)
            .thenAccept(results -> {
                Platform.runLater(() -> {
                    flightListView.setItems(results);
                    
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    
                    if (sectionLabel != null) {
                        if (results.isEmpty()) {
                            sectionLabel.setText("No flights found for \"" + query + "\"");
                        } else {
                            sectionLabel.setText("Found " + results.size() + " flights for \"" + query + "\"");
                        }
                    }
                    
                    hideStatus();
                });
            });
    }

  
}