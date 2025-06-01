package application.controller.admin;

import application.controller.admin.BaseController;
import application.ui.*;
import application.model.*;
import application.service.*;
import application.ui.admin.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController extends BaseController implements Initializable {
    
    // FXML Components - ENCAPSULATION
    @FXML private TextField flightSearchField;
    @FXML private TextField bookingSearchField;
    @FXML private TextField messageSearchField;
    
    // Labels
    @FXML private Label adminNameLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalFlightsLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label pendingBookingsLabel;
    @FXML private Label confirmedBookingsLabel;
    @FXML private Label cancelledBookingsLabel;
    @FXML private Label totalMessagesLabel;
    @FXML private Label todayBookingsLabel;
    @FXML private Label newMessagesLabel;
    @FXML private Label systemStatusLabel;
    
    // Buttons
    @FXML private Button logoutButton;
    @FXML private Button dashboardBtn;
    @FXML private Button usersBtn;
    @FXML private Button flightsBtn;
    @FXML private Button bookingsBtn;
    @FXML private Button messagesBtn;
    @FXML private Button addUserBtn;
    @FXML private Button addFlightBtn;
    
    // Content sections
    @FXML private VBox dashboardContent;
    @FXML private VBox usersContent;
    @FXML private VBox flightsContent;
    @FXML private VBox bookingsContent;
    @FXML private VBox messagesContent;
    
    // Tables
    @FXML private TableView<User> usersTable;
    @FXML private TableView<Flight> flightsTable;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableView<Message> messagesTable;
    
    // Message components
    @FXML private ListView<Message> conversationsList;
    @FXML private VBox chatArea;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextArea messageInputArea;
    @FXML private Button sendMessageBtn;
    @FXML private Label chatHeaderLabel;
    @FXML private ToggleButton automationToggle;
    @FXML private Label unreadCountLabel;
    
    // UI Builders - COMPOSITION pattern
    private AdminUsersBuilder usersBuilder;
    private AdminFlightsBuilder flightsBuilder;
    private AdminBookingsBuilder bookingsBuilder;
    private AdminMessagesBuilder messagesBuilder;
    
    // Current state
    private Message currentConversation;
    private int currentUserId = -1;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AdminController initializing...");
        
        try {
            // Check FXML injection
            if (!checkFXMLElements()) {
                System.err.println("FXML elements not properly injected!");
                return;
            }
            
            // Initialize UI builders - COMPOSITION
            initializeBuilders();
            
            // Set admin name
            setupAdminInfo();
            
            // Load dashboard stats
            loadDashboardStats();
            
            // Show dashboard by default
            showDashboard();
            
            System.out.println("AdminController initialized successfully");
            
        } catch (Exception e) {
            System.err.println("Error initializing AdminController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // INHERITANCE - implementing abstract method from BaseController
    @Override
    public void initialize() {
        // This method is called by BaseController if needed
    }
    
    @Override
    public void cleanup() {
        // Cleanup resources if needed
        System.out.println("AdminController cleanup completed");
    }
    
    // ENCAPSULATION - private initialization methods
    private boolean checkFXMLElements() {
        return adminNameLabel != null && dashboardContent != null && 
               usersTable != null && flightsTable != null && 
               bookingsTable != null && conversationsList != null;
    }
    
    private void initializeBuilders() {
        // Initialize users builder with event handling - POLYMORPHISM
        usersBuilder = new AdminUsersBuilder(new AdminUsersBuilder.UsersEventHandler() {
            @Override
            public void onUserEdit(User user) {
                AdminUserDialogs.editUser(user, AdminController.this::showAlert, 
                                        AdminController.this::refreshUsersData);
            }
            
            @Override
            public void onUserDelete(User user) {
                AdminUserDialogs.deleteUser(user, AdminController.this::showAlert, 
                                          AdminController.this::refreshUsersData);
            }
            
            @Override
            public void onUserAdd() {
                addNewUser();
            }
            
            @Override
            public void onUsersLoaded(int count) {
                System.out.println("Users loaded: " + count);
            }
            
            @Override
            public void onUsersError(String error) {
                showErrorAlert("Users Error", error);
            }
        });
        
        // Initialize flights builder
        flightsBuilder = new AdminFlightsBuilder(new AdminFlightsBuilder.FlightsEventHandler() {
            @Override
            public void onFlightEdit(Flight flight) {
                AdminFlightDialogs.editFlight(flight, AdminController.this::showAlert, 
                                            AdminController.this::refreshFlightsData);
            }
            
            @Override
            public void onFlightDelete(Flight flight) {
                AdminFlightDialogs.deleteFlight(flight, AdminController.this::showAlert, 
                                              AdminController.this::refreshFlightsData);
            }
            
            @Override
            public void onFlightAdd() {
                addNewFlight();
            }
            
            @Override
            public void onFlightDetails(Flight flight) {
                AdminFlightDialogs.showFlightDetails(flight, AdminController.this::showAlert);
            }
            
            @Override
            public void onFlightsLoaded(int count) {
                System.out.println("Flights loaded: " + count);
            }
            
            @Override
            public void onFlightsError(String error) {
                showErrorAlert("Flights Error", error);
            }
        });


        
        // Initialize bookings builder
        bookingsBuilder = new AdminBookingsBuilder(new AdminBookingsBuilder.BookingsEventHandler() {
            @Override
            public void onBookingView(Booking booking) {
                AdminBookingDialogs.showBookingDetails(booking, AdminController.this::showAlert);
            }
            
            @Override
            public void onBookingStatusChange(Booking booking) {
                AdminBookingDialogs.changeBookingStatus(booking, AdminController.this::showAlert,
                                                      AdminController.this::refreshBookingsData);
            }
            
            @Override
            public void onBookingDelete(Booking booking) {
                AdminBookingDialogs.deleteBooking(booking, AdminController.this::showAlert,
                                                AdminController.this::refreshBookingsData);
            }
            
            @Override
            public void onBookingsLoaded(int count) {
                System.out.println("Bookings loaded: " + count);
            }
            
            @Override
            public void onBookingsError(String error) {
                showErrorAlert("Bookings Error", error);
            }
        });
        
        // Initialize messages build`er
        messagesBuilder = new AdminMessagesBuilder(new AdminMessagesBuilder.MessagesEventHandler() {
            @Override
            public void onConversationSelect(int userId) {
                loadConversation(userId);
            }
            
            @Override
            public void onMessageSend(String message) {
                sendMessage();
            }
            
            @Override
            public void onAutomationToggle(boolean enabled) {
                toggleAutomation();
            }
            
            @Override
            public void onMessagesLoaded(int count) {
                System.out.println("Messages loaded: " + count);
            }
            
            @Override
            public void onMessagesError(String error) {
                showErrorAlert("Messages Error", error);
            }
        });
    }
    
    private void setupAdminInfo() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            String fullName = UserSession.getInstance().getCurrentUserFullName();
            adminNameLabel.setText("Welcome, " + (fullName != null ? fullName : "Admin"));
        } else {
            adminNameLabel.setText("Welcome, Admin");
        }
    }
    
    private void loadDashboardStats() {
        AdminDashboardBuilder.DashboardLabels labels = new AdminDashboardBuilder.DashboardLabels(
            totalUsersLabel, totalFlightsLabel, totalBookingsLabel, totalRevenueLabel,
            pendingBookingsLabel, confirmedBookingsLabel, cancelledBookingsLabel, 
            totalMessagesLabel, todayBookingsLabel, newMessagesLabel, systemStatusLabel
        );
        
        AdminDashboardBuilder.loadDashboardStats(labels, 
            new AdminDashboardBuilder.DashboardEventHandler() {
                @Override
                public void onStatsLoaded() {
                    System.out.println("Dashboard stats loaded successfully");
                }
                
                @Override
                public void onStatsError(String error) {
                    System.err.println("Dashboard stats error: " + error);
                }
            });
    }
    
    // Navigation methods - POLYMORPHISM through method overriding
    @FXML
    private void showDashboard() {
        hideAllContent();
        dashboardContent.setVisible(true);
        updateActiveButton(dashboardBtn);
        loadDashboardStats();
        System.out.println("Showing dashboard");
    }
    
    @FXML
    private void showUsers() {
        hideAllContent();
        usersContent.setVisible(true);
        updateActiveButton(usersBtn);
        refreshUsersData();
        System.out.println("Showing users");
    }
    
    @FXML
    private void showFlights() {
        hideAllContent();
        flightsContent.setVisible(true);
        updateActiveButton(flightsBtn);
        refreshFlightsData();
        System.out.println("Showing flights");
    }
    
    @FXML
    private void showBookings() {
        hideAllContent();
        bookingsContent.setVisible(true);
        updateActiveButton(bookingsBtn);
        refreshBookingsData();
        System.out.println("Showing bookings");
    }
    
    @FXML
    private void showMessages() {
        hideAllContent();
        messagesContent.setVisible(true);
        updateActiveButton(messagesBtn);
        refreshMessagesData();
        System.out.println("Showing messages");
    }
    
    // Data loading methods using builders
    private void refreshUsersData() {
        usersBuilder.setupUsersTable(usersTable);
    }
    
    private void refreshFlightsData() {
        flightsBuilder.setupFlightsTable(flightsTable);
        if (flightSearchField != null) {
            flightsBuilder.setupFlightSearch(flightSearchField, flightsTable);
        }
    }
    
    private void refreshBookingsData() {
        bookingsBuilder.setupBookingsTable(bookingsTable);
        if (bookingSearchField != null) {
            bookingsBuilder.setupBookingSearch(bookingSearchField, bookingsTable);
        }
    }
    
    private void refreshMessagesData() {
        messagesBuilder.setupMessagesContent(conversationsList, chatArea, chatScrollPane,
                                           messageInputArea, sendMessageBtn, chatHeaderLabel,
                                           automationToggle, unreadCountLabel);
    }
    
    // FXML Action handlers
    @FXML
    private void addNewUser() {
        AdminUserDialogs.addNewUser(this::showAlert, this::refreshUsersData);
    }
    
    @FXML
    private void addNewFlight() {
        AdminFlightDialogs.addNewFlight(this::showAlert, this::refreshFlightsData);
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            UserSession.getInstance().clearSession();
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Login.fxml"));
            Parent loginRoot = loader.load();
            
            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.setTitle("JetSetGO - Login");
            stage.centerOnScreen();
            
            System.out.println("Admin logged out successfully");
            
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ENCAPSULATION - private helper methods
    private void hideAllContent() {
        dashboardContent.setVisible(false);
        usersContent.setVisible(false);
        flightsContent.setVisible(false);
        bookingsContent.setVisible(false);
        messagesContent.setVisible(false);
    }
    
    private void updateActiveButton(Button activeButton) {
        Button[] navButtons = {dashboardBtn, usersBtn, flightsBtn, bookingsBtn, messagesBtn};
        
        for (Button btn : navButtons) {
            if (btn == activeButton) {
                btn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 12 15;");
            } else {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #495057; -fx-padding: 12 15;");
            }
        }
    }
    
    // Message handling methods
    private void loadConversation(int userId) {
        currentUserId = userId;
        messagesBuilder.loadConversation(userId, chatArea, chatScrollPane, 
                                       chatHeaderLabel, automationToggle);
    }
    
    @FXML
    private void sendMessage() {
        if (currentUserId == -1) {
            showWarningAlert("Warning", "Please select a conversation first.");
            return;
        }
        
        if (messageInputArea == null || messageInputArea.getText().trim().isEmpty()) {
            showWarningAlert("Warning", "Please enter a message.");
            return;
        }
        
        messagesBuilder.sendMessage(currentUserId, messageInputArea.getText().trim(),
                                  sendMessageBtn, this::showAlert, this::loadConversation);
        messageInputArea.clear();
    }
    
    @FXML
    private void toggleAutomation() {
        if (currentUserId == -1) {
            if (automationToggle != null) {
                automationToggle.setSelected(!automationToggle.isSelected());
            }
            showWarningAlert("Warning", "Please select a conversation first.");
            return;
        }
        
        messagesBuilder.toggleAutomation(currentUserId, automationToggle, this::showAlert);
    }
    
    public void refreshDashboard() {
        loadDashboardStats();
        System.out.println("Dashboard refreshed");
    }

    
// Search and filter methods
@FXML
private void searchFlights() {
    String searchTerm = flightSearchField.getText();
    if (flightsBuilder != null) {
        flightsBuilder.setupFlightSearch(flightSearchField, flightsTable);
    }
}

@FXML
private void searchBookings() {
    String searchTerm = bookingSearchField.getText();
    if (bookingsBuilder != null) {
        bookingsBuilder.setupBookingSearch(bookingSearchField, bookingsTable);
    }
}

@FXML
private void clearFlightSearch() {
    if (flightSearchField != null) {
        flightSearchField.clear();
        refreshFlightsData();
    }
}

@FXML
private void clearBookingSearch() {
    if (bookingSearchField != null) {
        bookingSearchField.clear();
        refreshBookingsData();
    }
}

@FXML
private void clearMessageSearch() {
    if (messageSearchField != null) {
        messageSearchField.clear();
        refreshMessagesData();
    }
}

@FXML
private void clearUserSearch() {
    // If you have a user search field, handle it here
    refreshUsersData();
}

// Refresh methods for buttons
@FXML
private void refreshUsers() {
    refreshUsersData();
}

@FXML
private void refreshFlights() {
    refreshFlightsData();
}

@FXML
private void refreshBookings() {
    refreshBookingsData();
}

@FXML
private void refreshMessages() {
    refreshMessagesData();
}

}