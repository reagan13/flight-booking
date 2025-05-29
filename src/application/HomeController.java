package application;

import application.model.Flight;
import application.model.User;
import application.model.UserSession;
import application.service.BookingHistoryService;
import application.service.BookingService;
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
import java.util.List;
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
    private TextField ageField, addressField;
    private String currentBookingReference;
    private String selectedPaymentMethod = "credit_card";
    private VBox methodsContainer;
    private TextField messageInput;
    private VBox chatContainer;





    

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
        if (bookingsContent != null) {
            loadUserBookings();
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
            System.err.println("bookingsContent is null");
            return;
        }

        bookingsContent.getChildren().clear();

        if (!UserSession.getInstance().isLoggedIn()) {
            showLoginPrompt();
            return;
        }

        List<BookingHistoryService.BookingHistory> bookings = BookingHistoryService.getUserBookings();

        if (bookings.isEmpty()) {
            showNoBookingsMessage();
        } else {
            for (BookingHistoryService.BookingHistory booking : bookings) {
                VBox bookingCard = createBookingCard(booking);
                bookingsContent.getChildren().add(bookingCard);
            }
        }
    }

    private void showLoginPrompt() {
        VBox loginPrompt = new VBox(20);
        loginPrompt.setAlignment(Pos.CENTER);
        loginPrompt.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; " +
                            "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");
        
        Label icon = new Label("🔒");
        icon.setStyle("-fx-font-size: 48px;");
        
        Label title = new Label("Login Required");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label message = new Label("Please log in to view your booking history");
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        Button loginButton = new Button("Go to Login");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                            "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 20;");
        loginButton.setOnAction(e -> switchToTab("profile"));
        
        loginPrompt.getChildren().addAll(icon, title, message, loginButton);
        bookingsContent.getChildren().add(loginPrompt);
    }
    
    private void showNoBookingsMessage() {
        VBox noBookingsBox = new VBox(20);
        noBookingsBox.setAlignment(Pos.CENTER);
        noBookingsBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; " +
                              "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");
        
        Label icon = new Label("✈️");
        icon.setStyle("-fx-font-size: 48px;");
        
        Label title = new Label("No Bookings Yet");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label message = new Label("Your travel bookings and reservations will appear here after you make a booking.");
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");
        message.setMaxWidth(300);
        
        Button exploreButton = new Button("Explore Flights");
        exploreButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                              "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 20;");
        exploreButton.setOnAction(e -> switchToTab("home"));
        
        noBookingsBox.getChildren().addAll(icon, title, message, exploreButton);
        bookingsContent.getChildren().add(noBookingsBox);
    }
    
    private VBox createBookingCard(BookingHistoryService.BookingHistory booking) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                     "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        
        // Header with booking reference and status
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox refBox = new VBox(2);
        Label refLabel = new Label("Booking Reference");
        refLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        Label refValue = new Label(booking.getBookingReference());
        refValue.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        refBox.getChildren().addAll(refLabel, refValue);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusBadge = createStatusBadge(booking.getBookingStatus(), booking.getPaymentStatus());
        
        header.getChildren().addAll(refBox, spacer, statusBadge);
        
        // Flight info
        HBox flightInfo = new HBox();
        flightInfo.setAlignment(Pos.CENTER);
        flightInfo.setSpacing(15);
        flightInfo.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");
        
        VBox originBox = new VBox(3);
        originBox.setAlignment(Pos.CENTER);
        Label originCode = new Label(booking.getOrigin());
        originCode.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        Label depTime = new Label(booking.getDeparture().format(DateTimeFormatter.ofPattern("HH:mm")));
        depTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        originBox.getChildren().addAll(originCode, depTime);
        
        Label arrow = new Label("→");
        arrow.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        
        VBox destBox = new VBox(3);
        destBox.setAlignment(Pos.CENTER);
        Label destCode = new Label(booking.getDestination());
        destCode.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        Label arrTime = new Label(booking.getArrival().format(DateTimeFormatter.ofPattern("HH:mm")));
        arrTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        destBox.getChildren().addAll(destCode, arrTime);
        
        flightInfo.getChildren().addAll(originBox, arrow, destBox);
        
        // Details
        VBox details = new VBox(8);
        details.getChildren().addAll(
            createDetailRow("✈️ Flight", booking.getFlightNo() + " • " + booking.getAirlineName()),
            createDetailRow("📅 Date", booking.getDeparture().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))),
            createDetailRow("💺 Seat", booking.getSeatNumber()),
            createDetailRow("💳 Payment", booking.getPaymentMethod() + " • " + currencyFormat.format(booking.getAmount()))
        );
        
        // Action buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);
        
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                           "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 15;");
        viewButton.setOnAction(e -> showBookingDetails(booking));
        
        Button downloadButton = new Button("Download Ticket");
        downloadButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                               "-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 15;");
        downloadButton.setOnAction(e -> downloadTicket(booking));
        
        actions.getChildren().addAll(viewButton, downloadButton);
        
        card.getChildren().addAll(header, flightInfo, details, actions);
        return card;
    }
    
    private Label createStatusBadge(String bookingStatus, String paymentStatus) {
        String text = "";
        String style = "";

        if ("confirmed".equals(bookingStatus) && "paid".equals(paymentStatus)) {
            text = "✅ Confirmed";
            style = "-fx-background-color: #E8F5E8; -fx-text-fill: #4CAF50;";
        } else if ("pending".equals(paymentStatus)) {
            text = "⏳ Pending";
            style = "-fx-background-color: #FFF3E0; -fx-text-fill: #FF9800;";
        } else if ("failed".equals(paymentStatus)) {
            text = "❌ Failed";
            style = "-fx-background-color: #FFEBEE; -fx-text-fill: #F44336;";
        } else {
            text = "📋 " + bookingStatus;
            style = "-fx-background-color: #E3F2FD; -fx-text-fill: #2196F3;";
        }

        Label badge = new Label(text);
        badge.setStyle(style + " -fx-font-size: 11px; -fx-font-weight: bold; " +
                "-fx-padding: 5 10; -fx-background-radius: 12;");

        return badge;
    }
    
    private void showBookingDetails(BookingHistoryService.BookingHistory booking) {
        showMobileAlert("Booking Details",
                "Reference: " + booking.getBookingReference() + "\n" +
                        "Flight: " + booking.getFlightNo() + "\n" +
                        "Route: " + booking.getOrigin() + " → " + booking.getDestination() + "\n" +
                        "Date: " + booking.getDeparture().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                        + "\n" +
                        "Seat: " + booking.getSeatNumber() + "\n" +
                        "Amount: " + currencyFormat.format(booking.getAmount()));
    }
    private void downloadTicket(BookingHistoryService.BookingHistory booking) {
        showMobileAlert("Download", "Ticket download feature will be available soon!");
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
    @FXML
    private void goBackToFlightDetails() {
        switchToTab("flight-details");
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
    
    // Add this method after handleBookFlight()
private void handleBookFlight() {
    // Set the current total price
    currentTotalPrice = currentFlight.getPrice();
    
    // Clear previous content and build booking form
    buildMobileBookingForm();
    
    // Navigate to booking form
    switchToTab("booking-form");
}

private void buildMobileBookingForm() {
    if (bookingFormContent == null) {
        System.err.println("bookingFormContent is null, cannot build booking form");
        return;
    }
    
    bookingFormContent.getChildren().clear();
    
    // Flight summary card
    VBox flightSummary = createMobileFlightSummary();
    bookingFormContent.getChildren().add(flightSummary);
    
    // Passenger information form
    VBox passengerForm = createMobilePassengerForm();
    bookingFormContent.getChildren().add(passengerForm);
    
    // Contact information form
    VBox contactForm = createMobileContactForm();
    bookingFormContent.getChildren().add(contactForm);
    
    // Terms and conditions
    VBox termsSection = createMobileTermsSection();
    bookingFormContent.getChildren().add(termsSection);
}

private VBox createMobileFlightSummary() {
    VBox card = new VBox(15);
    card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                  "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                  "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
    
    // Header
    HBox headerBox = new HBox();
    headerBox.setAlignment(Pos.CENTER_LEFT);
    headerBox.setSpacing(10);
    
    Label flightIcon = new Label("✈️");
    flightIcon.setStyle("-fx-font-size: 20px;");
    
    Label titleLabel = new Label("Flight Summary");
    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
    
    headerBox.getChildren().addAll(flightIcon, titleLabel);
    
    // Flight details
    VBox detailsBox = new VBox(12);
    
    // Airline and flight number
    VBox flightInfo = new VBox(2);
    Label airlineLabel = new Label(currentFlight.getAirlineName());
    airlineLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
    Label flightNoLabel = new Label("Flight " + currentFlight.getFlightNo());
    flightNoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
    flightInfo.getChildren().addAll(airlineLabel, flightNoLabel);
    
    // Route
    HBox routeRow = new HBox();
    routeRow.setAlignment(Pos.CENTER);
    routeRow.setSpacing(15);
    routeRow.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 12; -fx-background-radius: 8;");
    
    VBox originBox = new VBox(3);
    originBox.setAlignment(Pos.CENTER);
    Label originCode = new Label(currentFlight.getOrigin());
    originCode.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
    Label depTime = new Label(currentFlight.getDeparture().format(DateTimeFormatter.ofPattern("HH:mm")));
    depTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
    originBox.getChildren().addAll(originCode, depTime);
    
    Label arrowLabel = new Label("→");
    arrowLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
    
    VBox destBox = new VBox(3);
    destBox.setAlignment(Pos.CENTER);
    Label destCode = new Label(currentFlight.getDestination());
    destCode.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
    Label arrTime = new Label(currentFlight.getArrival().format(DateTimeFormatter.ofPattern("HH:mm")));
    arrTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
    destBox.getChildren().addAll(destCode, arrTime);
    
    routeRow.getChildren().addAll(originBox, arrowLabel, destBox);
    
    // Date and duration
    VBox scheduleBox = new VBox(8);
    Label dateLabel = new Label("📅 " + currentFlight.getDeparture().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
    dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
    Label durationLabel = new Label("⏱️ " + currentFlight.getDuration() + " flight time");
    durationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
    scheduleBox.getChildren().addAll(dateLabel, durationLabel);
    
    // Price section (moved below flight details)
    VBox priceBox = new VBox(5);
    priceBox.setAlignment(Pos.CENTER);
    priceBox.setStyle("-fx-background-color: #E8F5E8; -fx-padding: 15; -fx-background-radius: 10;");
    
    Label priceTitle = new Label("Flight Price");
    priceTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-weight: bold;");
    
    Label priceLabel = new Label(currencyFormat.format(currentFlight.getPrice()));
    priceLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
    
    Label priceNote = new Label("per person • includes taxes & fees");
    priceNote.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
    
    priceBox.getChildren().addAll(priceTitle, priceLabel, priceNote);
    
    detailsBox.getChildren().addAll(flightInfo, routeRow, scheduleBox, priceBox);
    card.getChildren().addAll(headerBox, detailsBox);
    
    return card;
}

private VBox createMobilePassengerForm() {
    VBox card = new VBox(15);
    card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
            "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");

    // Header
    HBox headerBox = new HBox();
    headerBox.setAlignment(Pos.CENTER_LEFT);
    headerBox.setSpacing(10);

    Label passengerIcon = new Label("👤");
    passengerIcon.setStyle("-fx-font-size: 20px;");

    Label titleLabel = new Label("Passenger Information");
    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

    headerBox.getChildren().addAll(passengerIcon, titleLabel);

    // Form fields
    VBox formBox = new VBox(15);

    // First Name
    VBox firstNameBox = new VBox(5);
    Label firstNameLabel = new Label("First Name *");
    firstNameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
    firstNameField = new TextField();
    firstNameField.setPromptText("Enter your first name");
    firstNameField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
            "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
    firstNameBox.getChildren().addAll(firstNameLabel, firstNameField);

    // Last Name
    VBox lastNameBox = new VBox(5);
    Label lastNameLabel = new Label("Last Name *");
    lastNameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
    lastNameField = new TextField();
    lastNameField.setPromptText("Enter your last name");
    lastNameField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
            "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
    lastNameBox.getChildren().addAll(lastNameLabel, lastNameField);

    // Age
    VBox ageBox = new VBox(5);
    Label ageLabel = new Label("Age *");
    ageLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
    ageField = new TextField();
    ageField.setPromptText("Enter your age");
    ageField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
            "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
    ageBox.getChildren().addAll(ageLabel, ageField);

    // Address
    VBox addressBox = new VBox(5);
    Label addressLabel = new Label("Address *");
    addressLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
    addressField = new TextField();
    addressField.setPromptText("Enter your address");
    addressField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
            "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
    addressBox.getChildren().addAll(addressLabel, addressField);

    // Pre-fill data if user is logged in
    if (UserSession.getInstance().isLoggedIn()) {
        User user = UserSession.getInstance().getCurrentUser();
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        if (user.getAge() > 0) {
            ageField.setText(String.valueOf(user.getAge()));
        }
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            addressField.setText(user.getAddress());
        }
    }

    formBox.getChildren().addAll(firstNameBox, lastNameBox, ageBox, addressBox);

    // Important note
    VBox noteBox = new VBox(5);
    noteBox.setStyle("-fx-background-color: #FFF3E0; -fx-padding: 12; -fx-background-radius: 8;");

    Label noteIcon = new Label("ℹ️");
    noteIcon.setStyle("-fx-font-size: 14px;");

    Label noteText = new Label(
            "Important: Please ensure the name matches exactly as shown on your government-issued ID.");
    noteText.setStyle("-fx-font-size: 11px; -fx-text-fill: #E65100; -fx-wrap-text: true;");

    HBox noteRow = new HBox(8);
    noteRow.setAlignment(Pos.TOP_LEFT);
    noteRow.getChildren().addAll(noteIcon, noteText);
    noteBox.getChildren().add(noteRow);

    card.getChildren().addAll(headerBox, formBox, noteBox);
    return card;
}

private VBox createMobileContactForm() {
    VBox card = new VBox(15);
    card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                  "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");
    
    // Header
    HBox headerBox = new HBox();
    headerBox.setAlignment(Pos.CENTER_LEFT);
    headerBox.setSpacing(10);
    
    Label contactIcon = new Label("📧");
    contactIcon.setStyle("-fx-font-size: 20px;");
    
    Label titleLabel = new Label("Contact Information");
    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
    
    headerBox.getChildren().addAll(contactIcon, titleLabel);
    
    // Form fields
    VBox formBox = new VBox(15);
    
    // Email
    VBox emailBox = new VBox(5);
    Label emailLabel = new Label("Email Address *");
    emailLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
    emailField = new TextField();
    emailField.setPromptText("Enter your email address");
    emailField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                       "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
    
    // Pre-fill if user is logged in
    if (UserSession.getInstance().isLoggedIn()) {
        emailField.setText(UserSession.getInstance().getCurrentUser().getEmail());
    }
    
    emailBox.getChildren().addAll(emailLabel, emailField);
    
    // Phone
    VBox phoneBox = new VBox(5);
    Label phoneLabel = new Label("Phone Number *");
    phoneLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
    phoneField = new TextField();
    phoneField.setPromptText("Enter your phone number");
    phoneField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                       "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
    phoneBox.getChildren().addAll(phoneLabel, phoneField);
    
    // Emergency Contact
    VBox emergencyBox = new VBox(5);
    Label emergencyLabel = new Label("Emergency Contact (Optional)");
    emergencyLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
    TextField emergencyField = new TextField();
    emergencyField.setPromptText("Emergency contact number");
    emergencyField.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 8; " +
                           "-fx-border-color: #ddd; -fx-border-radius: 8; -fx-border-width: 1;");
    emergencyBox.getChildren().addAll(emergencyLabel, emergencyField);
    
    formBox.getChildren().addAll(emailBox, phoneBox, emergencyBox);
    
    // Contact note
    VBox noteBox = new VBox(5);
    noteBox.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 12; -fx-background-radius: 8;");
    
    Label noteText = new Label("📱 We'll send your booking confirmation and flight updates to this email and phone number.");
    noteText.setStyle("-fx-font-size: 11px; -fx-text-fill: #1976D2; -fx-wrap-text: true;");
    noteBox.getChildren().add(noteText);
    
    card.getChildren().addAll(headerBox, formBox, noteBox);
    return card;
}

private VBox createMobileTermsSection() {
    VBox card = new VBox(15);
    card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
            "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");

    // Header
    HBox headerBox = new HBox();
    headerBox.setAlignment(Pos.CENTER_LEFT);
    headerBox.setSpacing(10);

    Label termsIcon = new Label("📋");
    termsIcon.setStyle("-fx-font-size: 20px;");

    Label titleLabel = new Label("Terms & Conditions");
    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

    headerBox.getChildren().addAll(termsIcon, titleLabel);

    // Checkboxes only
    VBox checkboxBox = new VBox(12);

    CheckBox termsCheckbox = new CheckBox("I agree to the Terms and Conditions");
    termsCheckbox.setStyle("-fx-font-size: 13px;");

    CheckBox privacyCheckbox = new CheckBox("I agree to the Privacy Policy");
    privacyCheckbox.setStyle("-fx-font-size: 13px;");

    CheckBox marketingCheckbox = new CheckBox("I want to receive promotional emails and updates (Optional)");
    marketingCheckbox.setStyle("-fx-font-size: 13px;");
    marketingCheckbox.setSelected(true);

    checkboxBox.getChildren().addAll(termsCheckbox, privacyCheckbox, marketingCheckbox);

    card.getChildren().addAll(headerBox, checkboxBox);
    return card;
}

// Update the validateBookingForm method
private boolean validateBookingForm() {
    String firstName = firstNameField.getText().trim();
    String lastName = lastNameField.getText().trim();
    String email = emailField.getText().trim();
    String phone = phoneField.getText().trim();
    String age = ageField.getText().trim();
    String address = addressField.getText().trim();

    if (firstName.isEmpty()) {
        showMobileAlert("Required Field", "Please enter your first name.");
        firstNameField.requestFocus();
        return false;
    }

    if (lastName.isEmpty()) {
        showMobileAlert("Required Field", "Please enter your last name.");
        lastNameField.requestFocus();
        return false;
    }

    if (age.isEmpty()) {
        showMobileAlert("Required Field", "Please enter your age.");
        ageField.requestFocus();
        return false;
    }

    try {
        int ageValue = Integer.parseInt(age);
        if (ageValue < 1 || ageValue > 120) {
            showMobileAlert("Invalid Age", "Please enter a valid age between 1 and 120.");
            ageField.requestFocus();
            return false;
        }
    } catch (NumberFormatException e) {
        showMobileAlert("Invalid Age", "Please enter a valid numeric age.");
        ageField.requestFocus();
        return false;
    }

    if (address.isEmpty()) {
        showMobileAlert("Required Field", "Please enter your address.");
        addressField.requestFocus();
        return false;
    }

    if (email.isEmpty()) {
        showMobileAlert("Required Field", "Please enter your email address.");
        emailField.requestFocus();
        return false;
    }

    if (!isValidEmail(email)) {
        showMobileAlert("Invalid Email", "Please enter a valid email address.");
        emailField.requestFocus();
        return false;
    }

    if (phone.isEmpty()) {
        showMobileAlert("Required Field", "Please enter your phone number.");
        phoneField.requestFocus();
        return false;
    }

    if (!isValidPhone(phone)) {
        showMobileAlert("Invalid Phone", "Please enter a valid phone number.");
        phoneField.requestFocus();
        return false;
    }

    return true;
}

private boolean isValidEmail(String email) {
    return email.contains("@") && email.contains(".");
}

private boolean isValidPhone(String phone) {
    return phone.replaceAll("[^0-9]", "").length() >= 10;
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
        if (!validateBookingForm()) {
            return;
        }
        
        // Build payment form content
        buildMobilePaymentForm();
        switchToTab("payment");
    }
    
    private void buildMobilePaymentForm() {
        if (paymentContent == null) {
            System.err.println("paymentContent is null, cannot build payment form");
            return;
        }

        paymentContent.getChildren().clear();

        // Booking summary
        VBox bookingSummary = createMobileBookingSummary();
        paymentContent.getChildren().add(bookingSummary);

        // Payment method selection
        VBox paymentMethods = createPaymentMethodsSection();
        paymentContent.getChildren().add(paymentMethods);

        // Payment summary and pay button
        VBox paymentSummary = createPaymentDetailsForm();
        paymentContent.getChildren().add(paymentSummary);
    }
    
    private VBox createPaymentMethodsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                        "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");
        
        Label titleLabel = new Label("Select Payment Method");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Toggle group for payment methods
        ToggleGroup paymentGroup = new ToggleGroup();
        
        VBox methodsContainer = new VBox(12);
        
        // Create payment options without complex styling
        RadioButton creditCard = createSimplePaymentOption("💳 Credit/Debit Card", "credit_card", paymentGroup, true);
        RadioButton gcash = createSimplePaymentOption("🔵 GCash", "gcash", paymentGroup, false);
        RadioButton maya = createSimplePaymentOption("🟢 Maya (PayMaya)", "maya", paymentGroup, false);
        RadioButton paypal = createSimplePaymentOption("🅿️ PayPal", "paypal", paymentGroup, false);
        RadioButton bankTransfer = createSimplePaymentOption("🏦 Bank Transfer", "bank_transfer", paymentGroup, false);
        
        methodsContainer.getChildren().addAll(creditCard, gcash, maya, paypal, bankTransfer);
        
        section.getChildren().addAll(titleLabel, methodsContainer);
        return section;
    }
    
    private RadioButton createSimplePaymentOption(String text, String methodId, ToggleGroup group, boolean selected) {
        RadioButton radio = new RadioButton(text);
        radio.setToggleGroup(group);
        radio.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        radio.setSelected(selected);

        if (selected) {
            selectedPaymentMethod = methodId;
        }

        radio.setOnAction(e -> {
            selectedPaymentMethod = methodId;
            System.out.println("Selected payment method: " + methodId);
        });

        return radio;
    }
    
    private HBox createPaymentMethodOption(String icon, String title, String description, ToggleGroup group, String methodId) {
        HBox option = new HBox(15);
        option.setAlignment(Pos.CENTER_LEFT);
        option.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15; " +
                       "-fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-border-width: 1; " +
                       "-fx-cursor: hand;");
        
        RadioButton radioButton = new RadioButton();
        radioButton.setToggleGroup(group);
        radioButton.setStyle("-fx-font-size: 14px;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        VBox textBox = new VBox(3);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        
        textBox.getChildren().addAll(titleLabel, descLabel);
        
        // Add processing fee info
        Label feeLabel = createProcessingFeeLabel(methodId);
        if (feeLabel != null) {
            textBox.getChildren().add(feeLabel);
        }
        
        option.getChildren().addAll(radioButton, iconLabel, textBox);
        
        // Handle selection - simplified without the problematic updateSelectedPaymentStyle call
        radioButton.setOnAction(e -> {
            selectedPaymentMethod = methodId;
            // Just update the selected method without the visual styling for now
            System.out.println("Selected payment method: " + methodId);
        });
        
        // Make entire option clickable
        option.setOnMouseClicked(e -> radioButton.fire());
        
        return option;
    }
    
    private Label createProcessingFeeLabel(String methodId) {
        String feeText = "";
        String feeColor = "#4CAF50";
        
        switch (methodId) {
            case "credit_card":
                feeText = "Processing fee: 2.5%";
                feeColor = "#FF9800";
                break;
            case "gcash":
            case "maya":
                feeText = "Processing fee: 1.0%";
                feeColor = "#4CAF50";
                break;
            case "paypal":
                feeText = "Processing fee: 3.4%";
                feeColor = "#FF5722";
                break;
            case "bank_transfer":
                feeText = "No processing fee";
                feeColor = "#4CAF50";
                break;
        }
        
        if (!feeText.isEmpty()) {
            Label feeLabel = new Label(feeText);
            feeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + feeColor + "; -fx-font-weight: bold;");
            return feeLabel;
        }
        
        return null;
    }
    
    private void updateSelectedPaymentStyle(HBox selectedOption, VBox container) {
        // Reset all options to default style
        for (javafx.scene.Node node : container.getChildren()) {
            if (node instanceof HBox) {
                node.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15; " +
                             "-fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-border-width: 1; " +
                             "-fx-cursor: hand;");
            }
        }
        
        // Highlight selected option
        selectedOption.setStyle("-fx-background-color: #E3F2FD; -fx-background-radius: 10; -fx-padding: 15; " +
                               "-fx-border-color: #2196F3; -fx-border-radius: 10; -fx-border-width: 2; " +
                               "-fx-cursor: hand;");
    }
    
    private VBox createPaymentDetailsForm() {
        VBox detailsContainer = new VBox(15);
        detailsContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        Label titleLabel = new Label("Payment Summary");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Payment breakdown
        VBox summaryBox = new VBox(10);
        summaryBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10;");

        double flightPrice = currentFlight.getPrice();
        double processingFee = calculateProcessingFee(flightPrice, selectedPaymentMethod);
        double totalAmount = flightPrice + processingFee;

        // Create summary rows without problematic casting
        HBox flightPriceRow = createPriceSummaryRow("Flight Price", flightPrice);
        HBox processingFeeRow = createPriceSummaryRow("Processing Fee", processingFee);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #ddd;");

        // Create total row with special styling
        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        totalRow.setStyle("-fx-background-color: #E8F5E8; -fx-padding: 10; -fx-background-radius: 8;");

        Label totalLabel = new Label("Total Amount");
        totalLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label totalValue = new Label(currencyFormat.format(totalAmount));
        totalValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        totalRow.getChildren().addAll(totalLabel, spacer, totalValue);

        summaryBox.getChildren().addAll(flightPriceRow, processingFeeRow, separator, totalRow);

        // Payment info
        VBox infoBox = new VBox(8);
        infoBox.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 15; -fx-background-radius: 10;");

        Label infoTitle = new Label("🔒 Secure Payment");
        infoTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        Label infoText = new Label(
                "Your payment will be processed securely. You'll be redirected to complete the payment after clicking 'Pay Now'.");
        infoText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-wrap-text: true;");

        infoBox.getChildren().addAll(infoTitle, infoText);

        // Pay Now button
        Button payButton = new Button("Pay Now - " + currencyFormat.format(totalAmount));
        payButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 30; " +
                "-fx-background-radius: 25; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);");
        payButton.setMaxWidth(Double.MAX_VALUE);
        payButton.setOnAction(e -> processPayment());

        detailsContainer.getChildren().addAll(titleLabel, summaryBox, infoBox, payButton);
        return detailsContainer;
    }
    
    private HBox createPriceSummaryRow(String label, double amount) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox valueBox = new HBox();
        valueBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label valueText = new Label(currencyFormat.format(amount));
        valueText.setStyle("-fx-font-size: 13px; -fx-text-fill: #333; -fx-font-weight: bold;");
        
        valueBox.getChildren().add(valueText);
        row.getChildren().addAll(labelText, spacer, valueBox);
        
        return row;
    }
    
    private double calculateProcessingFee(double amount, String paymentMethod) {
        switch (paymentMethod) {
            case "credit_card":
                return amount * 0.025; // 2.5%
            case "gcash":
            case "maya":
                return amount * 0.01; // 1%
            case "paypal":
                return amount * 0.034; // 3.4%
            case "bank_transfer":
                return 0.0; // No fee
            default:
                return amount * 0.02; // 2%
        }
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
    HBox row = new HBox(10);
    row.setAlignment(Pos.CENTER_LEFT);
    
    Label labelText = new Label(label);
    labelText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
    labelText.setPrefWidth(80);
    
    Label valueText = new Label(value);
    valueText.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
    
    row.getChildren().addAll(labelText, valueText);
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
        if (!validateBookingForm()) {
            return;
        }

        // Show processing status
        showStatus("Processing payment...", true);

        // Get form data
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        int age = Integer.parseInt(ageField.getText().trim());
        String address = addressField.getText().trim();

        // Get payment details (you'll need to implement payment form)
        String paymentMethod = getSelectedPaymentMethod(); // Implement this
        String paymentProvider = getPaymentProvider(paymentMethod); // Implement this
        double amount = currentFlight.getPrice();

        // Process booking in background thread
        Platform.runLater(() -> {
            BookingService.BookingResult result = BookingService.completeBooking(
                    currentFlight, firstName, lastName, age, address, email, phone,
                    amount, paymentMethod, paymentProvider);

            hideStatus();

            if (result.isSuccess()) {
                // Store booking reference for confirmation screen
                currentBookingReference = result.getBookingReference();

                // Show confirmation screen
                buildConfirmationScreen(result);
                switchToTab("confirmation");

                showMobileAlert("Booking Confirmed",
                        "Your booking has been confirmed! Reference: " + result.getBookingReference());
            } else {
                showMobileAlert("Booking Failed", result.getMessage());
            }
        });
    }
    
    private void buildConfirmationScreen(BookingService.BookingResult result) {
        if (confirmationContent == null) {
            return;
        }

        confirmationContent.getChildren().clear();

        // Success card
        VBox successCard = new VBox(20);
        successCard.setAlignment(Pos.CENTER);
        successCard.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1;");

        Label successIcon = new Label("✅");
        successIcon.setStyle("-fx-font-size: 48px;");

        Label titleLabel = new Label("Booking Confirmed!");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        Label referenceLabel = new Label("Booking Reference: " + result.getBookingReference());
        referenceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label messageLabel = new Label(
                "Your flight has been successfully booked. You will receive a confirmation email shortly.");
        messageLabel
                .setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");

        successCard.getChildren().addAll(successIcon, titleLabel, referenceLabel, messageLabel);
        confirmationContent.getChildren().add(successCard);
    }


    private String getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }
    
    private String getPaymentProvider(String paymentMethod) {
        switch (paymentMethod) {
            case "credit_card":
                return "Visa/Mastercard";
            case "gcash":
                return "GCash";
            case "maya":
                return "Maya";
            case "paypal":
                return "PayPal";
            default:
                return "Unknown";
        }
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