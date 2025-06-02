package application.controller.client;

import application.ui.client.*;
import application.ui.client.BookingFormScreenBuilder.BookingFormData;
import application.ui.client.PaymentScreenBuilder.PaymentData;
import application.model.Flight;
import application.service.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HomeController extends BaseController {
    
    // COMPOSITION - Controllers for different functionalities
    private FlightDetailsBuilder flightDetailsController;
    private BookingFormScreenBuilder bookingFormController;
    public PaymentScreenBuilder paymentController;
    
    // COMPOSITION - UI Builders
    private HomeScreenBuilder homeScreenBuilder;
    private ProfileScreenBuilder profileScreenBuilder;
    private NotificationScreenBuilder notificationScreenBuilder;
    private BookingsScreenBuilder bookingsScreenBuilder;
    private MessagesScreenBuilder messagesScreenBuilder;
    
    // FXML Components - Core Navigation
    @FXML private VBox timeScreen, timeContent;
    @FXML private Label headerTitle;
    @FXML private Button profileButton;
    @FXML private HBox statusBar, bottomNav;
    @FXML private ProgressIndicator headerProgress;
    @FXML private Label statusLabel;
    
    // FXML Components - Screen Management
    @FXML private StackPane contentStack;
    @FXML
    private VBox homeScreen, bookingsScreen, messagesScreen, profileScreen;
    @FXML private VBox flightDetailsScreen, bookingFormScreen, paymentScreen, confirmationScreen;
    
    // FXML Components - Navigation Tabs
    @FXML
    private VBox homeTab, bookingsTab, messagesTab, profileTab, timeTab;
        
    @FXML private TextField searchField;
    @FXML private Label sectionLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    // FXML Components - Content Areas
    @FXML private VBox bookingsContent, messagesContent, profileContent;
    @FXML private VBox flightDetailsContent, bookingFormContent, paymentContent, confirmationContent;
    
    // FXML Components - Action Buttons
    @FXML private Button bookFlightBtn, continueToPaymentBtn, confirmPaymentBtn, newBookingBtn;
    
    // ENCAPSULATION - Services and state
    private FlightService flightService;
    private ScheduledExecutorService timeUpdateService;
    private Flight currentFlight;
    private BookingFormData currentBookingData;
    @Override
    @FXML
    public void initialize() {
        System.out.println("Mobile HomeController initializing...");
        
        try {
            flightService = new FlightService();
            
            if (!checkFXMLElements()) {
                System.err.println("Some FXML elements are not properly injected");
                return;
            }
            
            initializeControllers();
            setupNavigation();
            setupScreens();
            loadAvailableFlights();
            
            System.out.println("Mobile HomeController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing HomeController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
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
    
    // ENCAPSULATION - Initialization methods
    private boolean checkFXMLElements() {
        return profileContent != null && bookingsContent != null && 
               messagesContent != null && timeContent != null && 
               flightDetailsContent != null && homeScreen != null; 
    }
    
    private void initializeControllers() {
        // Initialize flight details controller
        flightDetailsController = new FlightDetailsBuilder(
                this::handleBookFlight,
                this::returnToHome);

        // Initialize booking form controller
        bookingFormController = new BookingFormScreenBuilder(
                new BookingFormScreenBuilder.BookingFormEventHandler() {
                    @Override
                    public void onFormValidated(BookingFormData formData) {
                        currentBookingData = formData;
                        showPaymentForm();
                    }

                    @Override
                    public void onShowAlert(String title, String message) {
                        showMobileAlert(title, message);
                    }
                });

        // Initialize payment controller
        paymentController = new PaymentScreenBuilder(
                new PaymentScreenBuilder.PaymentEventHandler() {
                    @Override
                    public void onPaymentProcessed(PaymentData paymentData) {
                        processBooking(paymentData);
                    }

                    @Override
                    public void onShowAlert(String title, String message) {
                        showMobileAlert(title, message);
                    }

                    @Override
                    public void onBackToBooking() {
                        // Add this missing method
                        switchToTab("booking-form");
                    }

                    @Override
                    public void onShowConfirmation(PaymentData paymentData) {
                        // Navigate to confirmation screen after successful payment
                        showConfirmationAfterPayment(paymentData);
                    }

                });

        // Initialize UI builders with proper event handlers
        initializeUIBuilders();
    }

    private void showConfirmationAfterPayment(PaymentData paymentData) {
        // Process the booking first
        BookingService.BookingResult result = BookingService.completeBooking(
                currentFlight,
                paymentData.getFormData().getFirstName(),
                paymentData.getFormData().getLastName(),
                paymentData.getFormData().getAge(),
                paymentData.getFormData().getAddress(),
                paymentData.getFormData().getEmail(),
                paymentData.getFormData().getPhone(),
                paymentData.getTotalAmount(),
                paymentData.getPaymentMethod(),
                paymentData.getPaymentProvider());

        if (result.isSuccess()) {
            // Show confirmation screen
            confirmationContent.getChildren().clear();
            VBox confirmationScreen = ConfirmationScreenBuilder.createConfirmationScreen(
                    result,
                    currentFlight,
                    paymentData.getFormData(),
                    new ConfirmationScreenBuilder.ConfirmationEventHandler() {
                        @Override
                        public void onNavigateToHome() {
                            switchToTab("home");
                        }
                    });
            confirmationContent.getChildren().add(confirmationScreen);

            // Switch to confirmation screen (NOT home)
            switchToTab("confirmation");

            // Show success alert
            showMobileAlert("Payment Successful",
                    "Your booking has been confirmed! Reference: " + result.getBookingReference());
        } else {
            // Show error alert if booking failed
            showMobileAlert("Booking Failed", result.getMessage());
        }
    }
    
    
    
    private void returnToHome() {
        switchToTab("home");
    }
    private void initializeUIBuilders() {
        // Initialize home screen builder
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
                System.out.println("Explore destinations clicked");
            }
            
            @Override
            public void onViewAllDestinations() {
                System.out.println("View all destinations clicked");
            }
            
            @Override
            public void onSearchDestination(String destination) {
                handleSearchFlights(destination);
            }
        }, flightService);
        
        // Initialize other builders...
        initializeOtherBuilders();
    }
    
    private void initializeOtherBuilders() {
        // Profile screen builder
        profileScreenBuilder = new ProfileScreenBuilder(new ProfileScreenBuilder.ProfileEventHandler() {
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
                navigateToLogin((Stage) profileContent.getScene().getWindow());
            }
            
            @Override
            public void onNavigateToSignup() {
                System.out.println("Navigate to signup - implement as needed");
            }
        });
        
        // Bookings screen builder
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
            
        
        });
        
        // Messages screen builder
        messagesScreenBuilder = new MessagesScreenBuilder(new MessagesScreenBuilder.MessagesEventHandler() {
            @Override
            public void onMessageSent(String message) {
                Platform.runLater(() -> {
                    if (messagesContent.getChildren().size() > 1) {
                        ScrollPane chatScroll = (ScrollPane) messagesContent.getChildren().get(1);
                        chatScroll.setVvalue(1.0);
                    }
                });
            }
            
            @Override
            public void onRefreshMessages() {
                setupMessagesScreen();
            }
        });
    }
    
    private void setupNavigation() {
        // Set up tab click handlers for VBox tabs
        if (homeTab != null) homeTab.setOnMouseClicked(e -> switchToTab("home"));
        if (bookingsTab != null) bookingsTab.setOnMouseClicked(e -> switchToTab("bookings"));
        if (timeTab != null) timeTab.setOnMouseClicked(e -> switchToTab("time"));
        if (messagesTab != null) messagesTab.setOnMouseClicked(e -> switchToTab("messages"));
        if (profileTab != null) profileTab.setOnMouseClicked(e -> switchToTab("profile"));
        
        // Profile button in header
        if (profileButton != null) profileButton.setOnAction(e -> switchToTab("profile"));
        
        // Action buttons
        if (continueToPaymentBtn != null) continueToPaymentBtn.setOnAction(e -> bookingFormController.validateAndSubmitForm());
        if (confirmPaymentBtn != null) confirmPaymentBtn.setOnAction(e -> paymentController.processPayment(currentFlight, currentBookingData));
        if (newBookingBtn != null) newBookingBtn.setOnAction(e -> switchToTab("home"));
    }
    
    private void setupScreens() {
        setupHomeScreen();
        setupProfileScreen();
        setupBookingsScreen();
        setupMessagesScreen();
        setupTimeScreen();
    }
    
    
    private void setupTimeScreen() {
        if (timeContent == null) {
            System.err.println("timeContent is null, skipping time setup");
            return;
        }
        
        loadWorldTimes();
        
        // Keep the auto-update service
        timeUpdateService = Executors.newSingleThreadScheduledExecutor();
        timeUpdateService.scheduleAtFixedRate(() -> 
            Platform.runLater(this::loadWorldTimes), 60, 60, TimeUnit.SECONDS);
    }

    private void loadWorldTimes() {
        if (timeContent == null)
            return;

        timeContent.getChildren().clear();

        VBox timeScreenContent = TimeScreenBuilder.createTimeScreenContent();
        timeContent.getChildren().add(timeScreenContent);
    }

    
    private void setupHomeScreen() {
        if (homeScreenBuilder != null) {
            
            homeScreenBuilder.setupFlightGridView(homeScreen);
            
            // Setup search field
            homeScreenBuilder.setupSearchField(searchField);
            
            // Load flights initially
            homeScreenBuilder.loadAvailableFlights(homeScreen, sectionLabel, loadingIndicator);
        }
    }
    
    private void setupProfileScreen() {
        if (profileContent == null) {
            System.err.println("profileContent is null, skipping profile setup");
            return;
        }
        
        profileContent.getChildren().clear();
        VBox profileScreen = profileScreenBuilder.createProfileScreenContent();
    profileContent.getChildren().add(profileScreen);
    }
    
    private void setupBookingsScreen() {
        if (bookingsContent == null) {
            System.err.println("bookingsContent is null, skipping bookings setup");
            return;
        }
        
        bookingsScreenBuilder.setupBookingsContent(bookingsContent);
    }
    
    private void setupMessagesScreen() {
        if (messagesContent == null) return;
        
        messagesContent.getChildren().clear();
        VBox messageScreenContent = messagesScreenBuilder.createMessagesScreenContent();
        messagesContent.getChildren().add(messageScreenContent);
    }
    
    // PUBLIC INTERFACE - Tab switching
    public void switchToTab(String tabName) {
        hideAllScreens();
        updateTabStyles(tabName);
        updateHeader(tabName);
        showSelectedScreen(tabName);
    }
    
    private void hideAllScreens() {
        homeScreen.setVisible(false);
        bookingsScreen.setVisible(false);
        timeScreen.setVisible(false);
        messagesScreen.setVisible(false);
        profileScreen.setVisible(false);
        flightDetailsScreen.setVisible(false);
        bookingFormScreen.setVisible(false);
        paymentScreen.setVisible(false);
        confirmationScreen.setVisible(false);
    }
    
    private void showSelectedScreen(String tabName) {
        switch (tabName) {
            case "home":
                homeScreen.setVisible(true);
                bottomNav.setVisible(true);
                break;
            case "bookings":
                bookingsScreen.setVisible(true);
                bottomNav.setVisible(true);
                setupBookingsScreen();
                break;
            case "time":
                timeScreen.setVisible(true);
                bottomNav.setVisible(true);
                loadWorldTimes();
                break;
            case "messages":
                messagesScreen.setVisible(true);
                bottomNav.setVisible(true);
                setupMessagesScreen();
                break;
            case "profile":
                profileScreen.setVisible(true);
                bottomNav.setVisible(true);
                setupProfileScreen();
                break;
            case "flight-details":
                flightDetailsScreen.setVisible(true);
                bottomNav.setVisible(true);
                break;
            case "booking-form":
                bookingFormScreen.setVisible(true);
                bottomNav.setVisible(true);
                break;
            case "payment":
                paymentScreen.setVisible(true);
                bottomNav.setVisible(true);
                break;
            case "confirmation":
                confirmationScreen.setVisible(true);
                bottomNav.setVisible(true);
                break;
        }
    }
    
    private void updateTabStyles(String activeTab) {
        resetTabStyle(homeTab, "🏠", "Home");
        resetTabStyle(bookingsTab, "📋", "Bookings");
        resetTabStyle(timeTab, "🌍", "Time");
        resetTabStyle(messagesTab, "💬", "Messages");
        resetTabStyle(profileTab, "👤", "Profile");
        
        switch (activeTab) {
            case "home": setActiveTabStyle(homeTab, "🏠", "Home"); break;
            case "bookings": setActiveTabStyle(bookingsTab, "📋", "Bookings"); break;
            case "time": setActiveTabStyle(timeTab, "🌍", "Time"); break;
            case "messages": setActiveTabStyle(messagesTab, "💬", "Messages"); break;
            case "profile": setActiveTabStyle(profileTab, "👤", "Profile"); break;
        }
    }
    
    private void resetTabStyle(VBox tab, String icon, String text) {
        if (tab == null) return;
        
        // Clear existing children and add icon + text labels
        tab.getChildren().clear();
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c2c2c; -fx-padding: 2 0 0 0;");
        
        tab.getChildren().addAll(iconLabel, textLabel);
        tab.setStyle("-fx-background-color: linear-gradient(#f0f0f0, #d0d0d0); -fx-border-color: #808080; -fx-border-width: 1; -fx-padding: 15 0; -fx-cursor: hand; -fx-min-width: 100; -fx-pref-width: 100; -fx-alignment: center;");
    }
    
    private void setActiveTabStyle(VBox tab, String icon, String text) {
        if (tab == null) return;
        
        // Clear existing children and add icon + text labels
        tab.getChildren().clear();
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c2c2c; -fx-font-weight: bold; -fx-padding: 2 0 0 0;");
        
        tab.getChildren().addAll(iconLabel, textLabel);
        tab.setStyle("-fx-background-color: linear-gradient(#ffffff, #e0e0e0); -fx-border-color: #808080; -fx-border-width: 1; -fx-padding: 15 0; -fx-cursor: hand; -fx-min-width: 100; -fx-pref-width: 100; -fx-alignment: center;");
    }
    
    private void updateHeader(String tabName) {
        switch (tabName) {
            case "home": headerTitle.setText("✈️ JetSetGO"); break;
            case "bookings": headerTitle.setText("📋 My Bookings"); break;
            case "time": headerTitle.setText("🌍 World Times"); break;
            case "messages": headerTitle.setText("💬 Messages"); break;
            case "profile": headerTitle.setText("👤 Profile"); break;
            case "flight-details": headerTitle.setText("✈️ Flight Details"); break;
            case "booking-form": headerTitle.setText("📝 Passenger Info"); break;
            case "payment": headerTitle.setText("💳 Payment"); break;
            case "confirmation": headerTitle.setText("✅ Confirmed"); break;
        }
    }
    
    // DELEGATION - Flight operations
    public void showFlightDetails(Flight flight) {
        currentFlight = flight;
        flightDetailsContent.getChildren().clear();
        
        VBox detailsCard = flightDetailsController.createFlightDetailsView(flight);
        flightDetailsContent.getChildren().add(detailsCard);
        
        switchToTab("flight-details");
    }
    
    private void handleBookFlight() {
        bookingFormContent.getChildren().clear();
        VBox bookingForm = bookingFormController.createBookingForm(currentFlight);
        bookingFormContent.getChildren().add(bookingForm);
        switchToTab("booking-form");
    }
    
    
    private void showPaymentForm() {
        paymentContent.getChildren().clear();
        VBox paymentForm = paymentController.createPaymentForm(currentFlight, currentBookingData);
        paymentContent.getChildren().add(paymentForm);
        switchToTab("payment");
    }
    
    private void processBooking(PaymentData paymentData) {
        showStatus("Processing booking...", true);
        
        BookingService.BookingResult result = BookingService.completeBooking(
            currentFlight, 
            currentBookingData.getFirstName(), 
            currentBookingData.getLastName(),
            currentBookingData.getAge(), 
            currentBookingData.getAddress(),
            currentBookingData.getEmail(), 
            currentBookingData.getPhone(),
            paymentData.getTotalAmount(), 
            paymentData.getPaymentMethod(), 
            paymentData.getPaymentProvider()
        );
        
        hideStatus();
        
        if (result.isSuccess()) {
            showConfirmationScreen(result);
            showMobileAlert("Booking Confirmed", 
                "Your booking has been confirmed! Reference: " + result.getBookingReference());
        } else {
            showMobileAlert("Booking Failed", result.getMessage());
        }
    }
    
    private void showConfirmationScreen(BookingService.BookingResult result) {
        confirmationContent.getChildren().clear();
        VBox confirmationScreen = ConfirmationScreenBuilder.createConfirmationScreen(
            result, 
            currentFlight, 
            currentBookingData,
            new ConfirmationScreenBuilder.ConfirmationEventHandler() {
                @Override
                public void onNavigateToHome() {
                    switchToTab("home");
                }
            }
        );
        confirmationContent.getChildren().add(confirmationScreen);
        switchToTab("confirmation"); 
    }
    
    
    // DELEGATION - User operations
    private void handleLogout() {
        UserSession.getInstance().logout();
        showMobileAlert("Logged Out", "You have been successfully logged out.");
        navigateToLogin((Stage) profileContent.getScene().getWindow());
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
                        showNotifications();
                    }
                    
                    @Override
                    public void onShowNotificationDetails(NotificationService.Notification notification) {
                        notificationScreenBuilder.showNotificationDetails(notification);
                    }
                });
        }
        
        VBox notificationScreen = notificationScreenBuilder.createNotificationScreenContent();
        profileContent.getChildren().clear();
        profileContent.getChildren().add(notificationScreen);
    }
    
    private void showBookingDetails(BookingHistoryService.BookingHistory booking) {
        showMobileAlert("Booking Details",
            "Reference: " + booking.getBookingReference() + "\n" +
            "Flight: " + booking.getFlightNo() + "\n" +
            "Route: " + booking.getOrigin() + " → " + booking.getDestination() + "\n" +
            "Date: " + booking.getDeparture().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) + "\n" +
            "Seat: " + booking.getSeatNumber() + "\n" +
            "Amount: ₱" + String.format("%.2f", booking.getAmount()));
    }
   
    
    // UTILITY METHODS
    public void showStatus(String message, boolean showProgress) {
        statusLabel.setText(message);
        headerProgress.setVisible(showProgress);
        statusBar.setVisible(true);
        statusBar.setManaged(true);
    }
    
    public void hideStatus() {
        statusBar.setVisible(false);
        statusBar.setManaged(false);
    }
    
    private void loadAvailableFlights() {
        homeScreenBuilder.loadAvailableFlights(homeScreen, sectionLabel, loadingIndicator);
    }
    
    private void handleSearchFlights(String query) {
        showStatus("Searching for \"" + query + "\"...", true);

        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }

        flightService.searchFlights(query)
                .thenAccept(results -> {
                    Platform.runLater(() -> {
                        FlowPane flightGrid = (FlowPane) homeScreen.getProperties().get("flightGrid");
                        if (flightGrid != null) {
                            flightGrid.getChildren().clear();
                            
                            // Add search result cards to grid
                            for (Flight flight : results) {
                                VBox flightCard = homeScreenBuilder.createSimpleFlightCard(flight);
                                flightGrid.getChildren().add(flightCard);
                            }
                        }

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
    
    @FXML
private void goBackToFlightDetails() {
    switchToTab("flight-details");
}


@FXML
private void handleContinueToPayment() {
    if (bookingFormController != null) {
        bookingFormController.validateAndSubmitForm();
    }
}

@FXML
private void handleConfirmPayment() {
    if (paymentController != null && currentFlight != null && currentBookingData != null) {
        paymentController.processPayment(currentFlight, currentBookingData);
    }
}

@FXML
private void handleNewBooking() {
    switchToTab("home");
}

@FXML
private void handleGoHome() {
    switchToTab("home");
}

@FXML
private void handleGoToBookings() {
    switchToTab("bookings");
}

@FXML
private void handleGoToMessages() {
    switchToTab("messages");
}

@FXML
private void handleGoToProfile() {
    switchToTab("profile");
}

@FXML
private void handleGoToTime() {
    switchToTab("time");
}

}