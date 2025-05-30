package application;

import application.model.Flight;
import application.model.User;
import application.service.BookingHistoryService;
import application.service.BookingService;
import application.ui.ProfileScreenBuilder;
import application.ui.ProfileScreenBuilder.ProfileEventHandler;
import application.ui.NotificationScreenBuilder;
import application.ui.HomeScreenBuilder;

import application.service.ChatService;
import application.service.FlightService;
import application.service.NotificationService;
import application.service.UserSession;
import application.util.FlightListCell;
import application.service.TimeAPIService;
import application.ui.TimeScreenBuilder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import application.ui.BookingsScreenBuilder;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeController {
    private ProfileScreenBuilder profileScreenBuilder;
    private NotificationScreenBuilder notificationScreenBuilder;
    private HomeScreenBuilder homeScreenBuilder;
    private BookingsScreenBuilder bookingsScreenBuilder;
    


    @FXML private VBox timeScreen;
    @FXML private VBox timeTab;
    @FXML
    private VBox timeContent;


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
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
    private ScheduledExecutorService timeUpdateService;

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
            setupTimeScreen(); // Add this line
            loadAvailableFlights();

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
        if (timeContent == null) { // Add this check
            System.err.println("timeContent is null - check FXML fx:id");
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
    


    private void setupBottomNavigation() {
        // Set up tab click handlers - add null checks
        if (homeTab != null)
            homeTab.setOnMouseClicked(e -> switchToTab("home"));
        if (bookingsTab != null)
            bookingsTab.setOnMouseClicked(e -> switchToTab("bookings"));
        if (timeTab != null)
            timeTab.setOnMouseClicked(e -> switchToTab("time")); // Add this line
        if (messagesTab != null)
            messagesTab.setOnMouseClicked(e -> switchToTab("messages"));
        if (profileTab != null)
            profileTab.setOnMouseClicked(e -> switchToTab("profile"));

        // Profile button in header
        if (profileButton != null)
            profileButton.setOnAction(e -> switchToTab("profile"));

        // Action buttons - add null checks
        if (continueToPaymentBtn != null)
            continueToPaymentBtn.setOnAction(e -> showPaymentForm());
        if (confirmPaymentBtn != null)
            confirmPaymentBtn.setOnAction(e -> processPayment());
        if (newBookingBtn != null)
            newBookingBtn.setOnAction(e -> switchToTab("home"));
    }
    
    private void setupTimeScreen() {
        if (timeContent == null) {
            System.err.println("timeContent is null, skipping time setup");
            return;
        }
        
        // Initialize time content
        loadWorldTimes();
        
        // Set up auto-refresh every minute
        timeUpdateService = Executors.newSingleThreadScheduledExecutor();
        timeUpdateService.scheduleAtFixedRate(() -> {
            Platform.runLater(this::loadWorldTimes);
        }, 60, 60, TimeUnit.SECONDS);
    }
    
    // Add this new method
    private void loadWorldTimes() {
        if (timeContent == null) return;
        
        timeContent.getChildren().clear();
        
        // Header
        VBox headerCard = TimeScreenBuilder.createHeaderCard();
        timeContent.getChildren().add(headerCard);
        
        // Create time cards for each timezone
        for (String[] timeZone : TimeAPIService.TIME_ZONES) {
            VBox timeCard = TimeScreenBuilder.createTimeCardWithAPI(
                timeZone[0], timeZone[1], timeZone[2], timeZone[3]);
            timeContent.getChildren().add(timeCard);
        }
        
        // Footer info
        VBox footerInfo = TimeScreenBuilder.createFooterInfo();
        timeContent.getChildren().add(footerInfo);
    }
    
    private void setupHomeScreen() {
        if (flightListView == null) {
            System.err.println("flightListView is null, skipping home setup");
            return;
        }

        // Initialize builder if not already done
        if (homeScreenBuilder == null) {
            homeScreenBuilder = new HomeScreenBuilder(new HomeScreenBuilder.HomeEventHandler() {
                @Override
                public void onShowFlightDetails(Flight flight) {
                    showFlightDetails(flight);
                }

                @Override
                public void onShowStatus(String message, boolean showProgress) {
                    showStatus(message, showProgress);
                }

                @Override
                public void onHideStatus() {
                    hideStatus();
                }

                @Override
                public void onLoadAvailableFlights() {
                    loadAvailableFlights();
                }

                @Override
                public void onSearchFlights(String query) {
                    handleSearchFlights(query);
                }

                @Override
                public void onExploreDestinations() {
                    // Implement explore destinations functionality
                    System.out.println("Explore destinations clicked");
                }

                @Override
                public void onViewAllDestinations() {
                    // Implement view all destinations functionality
                    System.out.println("View all destinations clicked");
                }

                @Override
                public void onSearchDestination(String destination) {
                    // Implement search by destination
                    handleSearchFlights(destination);
                }
            }, flightService);
        }

        // Setup flight list view
        homeScreenBuilder.setupFlightListView(flightListView);

        // Setup search field
        homeScreenBuilder.setupSearchField(searchField);
    }
    
    private void setupProfileScreen() {
        if (profileContent == null) {
            System.err.println("profileContent is null, skipping profile setup");
            return;
        }

        // Initialize builders if not already done
        if (profileScreenBuilder == null) {
            profileScreenBuilder = new ProfileScreenBuilder(new ProfileEventHandler() {
                @Override
                public void onSwitchToTab(String tabName) {
                    switchToTab(tabName);
                }

                @Override
                public void onLogout() {
                    handleLogout();
                }

                @Override
                public void onShowNotifications() {
                    showNotifications();
                }

                @Override
                public void onNavigateToLogin() {
                    navigateToLogin();
                }

                @Override
                public void onNavigateToSignup() {
                    // Implement signup navigation if needed
                    System.out.println("Navigate to signup - implement as needed");
                }
            });
        }

        profileContent.getChildren().clear();
        VBox profileScreen = profileScreenBuilder.createProfileContent();
        profileContent.getChildren().add(profileScreen);
    }

    
    

    private void setupBookingsScreen() {
        if (bookingsContent == null) {
            System.err.println("bookingsContent is null, skipping bookings setup");
            return;
        }

        // Initialize builder if not already done
        if (bookingsScreenBuilder == null) {
            bookingsScreenBuilder = new BookingsScreenBuilder(new BookingsScreenBuilder.BookingsEventHandler() {
                @Override
                public void onGoToLogin() {
                    switchToTab("profile");
                }

                @Override
                public void onExploreFlights() {
                    switchToTab("home");
                }

                @Override
                public void onViewBookingDetails(BookingHistoryService.BookingHistory booking) {
                    showBookingDetails(booking);
                }

                @Override
                public void onDownloadTicket(BookingHistoryService.BookingHistory booking) {
                    downloadTicket(booking);
                }
            });
        }

        bookingsScreenBuilder.setupBookingsContent(bookingsContent);
    }
    
    

   private void setupMessagesScreen() {
    if (messagesContent == null) {
        System.err.println("messagesContent is null");
        return;
    }
    
    messagesContent.getChildren().clear();
    messagesContent.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 0;");
    
    // Chat header
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
    
    // Chat messages container
    ScrollPane chatScroll = new ScrollPane();
    chatScroll.setFitToWidth(true);
    chatScroll.setStyle("-fx-background-color: #f5f5f5; -fx-background: #f5f5f5;");
    
    chatContainer = new VBox(10);
    chatContainer.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");
    chatScroll.setContent(chatContainer);
    
    VBox.setVgrow(chatScroll, Priority.ALWAYS);
    
    // Message input area
    HBox inputArea = new HBox(10);
    inputArea.setAlignment(Pos.CENTER);
    inputArea.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");
    
    messageInput = new TextField();
    messageInput.setPromptText("Type your message...");
    messageInput.setStyle("-fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ddd; -fx-border-width: 1;");
    HBox.setHgrow(messageInput, Priority.ALWAYS);
    
    Button sendButton = new Button("Send");
    sendButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 20;");
    
    inputArea.getChildren().addAll(messageInput, sendButton);
    
    // Send message handlers
    sendButton.setOnAction(e -> sendMessage());
    messageInput.setOnAction(e -> sendMessage());
    
    messagesContent.getChildren().addAll(header, chatScroll, inputArea);
    
    // Load existing messages
    loadMessages();
    
    // Auto-scroll to bottom
    Platform.runLater(() -> chatScroll.setVvalue(1.0));
}

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
        ScrollPane scroll = (ScrollPane) messagesContent.getChildren().get(1);
        scroll.setVvalue(1.0);
    });
}

private void loadMessages() {
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



private void handleLogout() {
    UserSession.getInstance().logout();

    // Show logout confirmation
    showMobileAlert("Logged Out", "You have been successfully logged out.");

    // Navigate to login screen
    navigateToLogin();
}
    
private void navigateToLogin() {
    try {
        Stage currentStage = (Stage) profileContent.getScene().getWindow();
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/resources/Login.fxml"));
        javafx.scene.Parent loginRoot = loader.load();
        javafx.scene.Scene loginScene = new javafx.scene.Scene(loginRoot);
        currentStage.setScene(loginScene);
        currentStage.setTitle("JetSetGO - Login");
        currentStage.centerOnScreen();
        System.out.println("Successfully navigated to login screen");
    } catch (Exception e) {
        System.err.println("Error navigating to login: " + e.getMessage());
        e.printStackTrace();
        setupProfileScreen();
    }
}



private void showNotifications() {
    if (notificationScreenBuilder == null) {
        notificationScreenBuilder = new NotificationScreenBuilder(
                new NotificationScreenBuilder.NotificationEventHandler() {
                    @Override
                    public void onBackToProfile() {
                        setupProfileScreen();
                    }

                    @Override
                    public void onRefreshNotifications() {
                        showNotifications(); // Refresh the notifications screen
                    }

                    @Override
                    public void onShowNotificationDetails(NotificationService.Notification notification) {
                        notificationScreenBuilder.showNotificationDetails(notification);
                    }
                });
    }

    VBox notificationScreen = notificationScreenBuilder.createNotificationsScreen();

    // Replace profile content
    profileContent.getChildren().clear();
    profileContent.getChildren().add(notificationScreen);
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

    private void switchToTab(String tabName) {
        // Hide all screens
        homeScreen.setVisible(false);
        bookingsScreen.setVisible(false);
        timeScreen.setVisible(false); // Add this line
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
            case "time": // Add this case
                timeScreen.setVisible(true);
                bottomNav.setVisible(true);
                loadWorldTimes(); // Refresh times
                currentTab = "time";
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
        resetTabStyle(timeTab, "🌍", "Time"); // Add this line
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
            case "time": // Add this case
                setActiveTabStyle(timeTab, "🌍", "Time");
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
            case "time": // Add this case
                headerTitle.setText("🌍 World Times");
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

    public void cleanup() {
        if (timeUpdateService != null && !timeUpdateService.isShutdown()) {
            timeUpdateService.shutdown();
            try {
                if (!timeUpdateService.awaitTermination(2, TimeUnit.SECONDS)) {
                    timeUpdateService.shutdownNow();
                }
            } catch (InterruptedException e) {
                timeUpdateService.shutdownNow();
                Thread.currentThread().interrupt();
            }
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
        backButton.setOnAction(e -> switchToTab("home"));
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
        priceAmount.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
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
        
        methodsContainer.getChildren().addAll(creditCard, gcash, maya, paypal);
        
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

       

        detailsContainer.getChildren().addAll(titleLabel, summaryBox);
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

   

    private void loadAvailableFlights() {
        homeScreenBuilder.loadAvailableFlights(flightListView, sectionLabel, loadingIndicator);
    }
    private void handleSearchFlights(String query) {
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