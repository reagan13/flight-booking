package application.controller.admin;

import application.model.*;
import application.service.*;
import application.ui.admin.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController extends BaseController implements Initializable {
    
    // FXML Components - ENCAPSULATION
    @FXML private TextField flightSearchField;
    @FXML private TextField bookingSearchField;
    @FXML private TextField messageSearchField;
    @FXML private TextField transactionSearchField;
    
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
    @FXML private Button transactionsBtn;
    @FXML private Button addUserBtn;
    @FXML private Button addFlightBtn;
    
    // Content sections
    @FXML private VBox dashboardContent;
    @FXML private VBox usersContent;
    @FXML private VBox flightsContent;
    @FXML private VBox bookingsContent;
    @FXML private VBox messagesContent;
    @FXML private VBox transactionsContent;
    
    // Tables
    @FXML private TableView<User> usersTable;
    @FXML private TableView<Flight> flightsTable;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableView<Message> messagesTable;
    @FXML private TableView<Transaction> transactionsTable;
    
    // Message components
    @FXML private ListView<Message> conversationsList;
    @FXML private VBox chatArea;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextArea messageInputArea;
    @FXML private Button sendMessageBtn;
    @FXML private Label chatHeaderLabel;
    @FXML private ToggleButton automationToggle;
    @FXML private Label unreadCountLabel;
    
    // UI Builders - COMPOSITION pattern - FIXED TYPES
    private AdminUsersBuilder usersBuilder;
    private AdminFlightsBuilder flightsBuilder;
    private AdminBookingsBuilder bookingsBuilder;
    private AdminMessagesBuilder messagesBuilder;
    private AdminTransactionsBuilder transactionsBuilder; // FIXED: Use AdminTransactionsBuilder
    
    // Current state
    private int currentUserId = -1;
    private Button[] navButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AdminController initializing...");
        
        try {
            // Initialize navigation buttons array
            navButtons = new Button[]{dashboardBtn, usersBtn, flightsBtn, bookingsBtn, messagesBtn, transactionsBtn};
            
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
        boolean allPresent = true;
        
        if (adminNameLabel == null) {
            System.err.println("adminNameLabel is null");
            allPresent = false;
        }
        if (dashboardContent == null) {
            System.err.println("dashboardContent is null");
            allPresent = false;
        }
        if (usersTable == null) {
            System.err.println("usersTable is null");
            allPresent = false;
        }
        if (flightsTable == null) {
            System.err.println("flightsTable is null");
            allPresent = false;
        }
        if (bookingsTable == null) {
            System.err.println("bookingsTable is null");
            allPresent = false;
        }
        if (transactionsTable == null) {
            System.err.println("transactionsTable is null");
            allPresent = false;
        }
        if (transactionsContent == null) {
            System.err.println("transactionsContent is null");
            allPresent = false;
        }
        if (conversationsList == null) {
            System.err.println("conversationsList is null");
            allPresent = false;
        }
        
        return allPresent;
    }
    
    private void initializeBuilders() {
        try {
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
            
            // Initialize messages builder
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

            transactionsBuilder = new AdminTransactionsBuilder(new AdminTransactionsBuilder.TransactionsEventHandler() {
                @Override
                public void onViewTransactionDetails(Transaction transaction) {
                    AdminTransactionDialogs.showTransactionDetails(transaction, AdminController.this::showAlert);
                }
                
                @Override
                public void onTransactionsLoaded(int count) {
                    System.out.println("Transactions loaded: " + count);
                }
                
                @Override
                public void onTransactionsError(String error) {
                    showErrorAlert("Transactions Error", error);
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error initializing builders: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupAdminInfo() {
        try {
            // Check if UserSession exists
            try {
                User currentUser = UserSession.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String fullName = UserSession.getInstance().getCurrentUserFullName();
                    adminNameLabel.setText("Welcome, " + (fullName != null ? fullName : "Admin"));
                } else {
                    adminNameLabel.setText("Welcome, Admin");
                }
            } catch (Exception e) {
                // UserSession might not exist yet
                adminNameLabel.setText("Welcome, Admin");
            }
        } catch (Exception e) {
            System.err.println("Error setting up admin info: " + e.getMessage());
            adminNameLabel.setText("Welcome, Admin");
        }
    }
    
    private void loadDashboardStats() {
        try {
            // Check if AdminDashboardBuilder exists
            try {
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
            } catch (Exception e) {
                System.err.println("AdminDashboardBuilder not available: " + e.getMessage());
                // Set default values
                if (totalUsersLabel != null) totalUsersLabel.setText("0");
                if (totalFlightsLabel != null) totalFlightsLabel.setText("0");
                if (totalBookingsLabel != null) totalBookingsLabel.setText("0");
                if (totalRevenueLabel != null) totalRevenueLabel.setText("₱0.00");
            }
        } catch (Exception e) {
            System.err.println("Error loading dashboard stats: " + e.getMessage());
        }
    }
    
    // Navigation methods - POLYMORPHISM through method overriding
    @FXML
    private void showDashboard() {
        try {
            hideAllContent();
            if (dashboardContent != null) {
                dashboardContent.setVisible(true);
            }
            updateActiveButton(dashboardBtn);
            loadDashboardStats();
            System.out.println("Showing dashboard");
        } catch (Exception e) {
            System.err.println("Error showing dashboard: " + e.getMessage());
        }
    }
    
    @FXML
    private void showUsers() {
        try {
            hideAllContent();
            if (usersContent != null) {
                usersContent.setVisible(true);
            }
            updateActiveButton(usersBtn);
            refreshUsersData();
            System.out.println("Showing users");
        } catch (Exception e) {
            System.err.println("Error showing users: " + e.getMessage());
        }
    }
    
    @FXML
    private void showFlights() {
        try {
            hideAllContent();
            if (flightsContent != null) {
                flightsContent.setVisible(true);
            }
            updateActiveButton(flightsBtn);
            refreshFlightsData();
            System.out.println("Showing flights");
        } catch (Exception e) {
            System.err.println("Error showing flights: " + e.getMessage());
        }
    }
    
    @FXML
    private void showBookings() {
        try {
            hideAllContent();
            if (bookingsContent != null) {
                bookingsContent.setVisible(true);
            }
            updateActiveButton(bookingsBtn);
            refreshBookingsData();
            System.out.println("Showing bookings");
        } catch (Exception e) {
            System.err.println("Error showing bookings: " + e.getMessage());
        }
    }
    
    @FXML
    private void showMessages() {
        try {
            hideAllContent();
            if (messagesContent != null) {
                messagesContent.setVisible(true);
            }
            updateActiveButton(messagesBtn);
            refreshMessagesData();
            System.out.println("Showing messages");
        } catch (Exception e) {
            System.err.println("Error showing messages: " + e.getMessage());
        }
    }
    
    @FXML
    private void showTransactions() {
        try {
            hideAllContent();
            if (transactionsContent != null) {
                transactionsContent.setVisible(true);
            }
            updateActiveButton(transactionsBtn);
            refreshTransactionsData();
            System.out.println("Showing transactions");
        } catch (Exception e) {
            System.err.println("Error showing transactions: " + e.getMessage());
        }
    }

    // Data loading methods using builders
    private void refreshUsersData() {
        try {
            if (usersBuilder != null && usersTable != null) {
                usersBuilder.setupUsersTable(usersTable);
            }
        } catch (Exception e) {
            System.err.println("Error refreshing users data: " + e.getMessage());
        }
    }
    
    private void refreshFlightsData() {
        try {
            if (flightsBuilder != null && flightsTable != null) {
                flightsBuilder.setupFlightsTable(flightsTable);
                if (flightSearchField != null) {
                    flightsBuilder.setupFlightSearch(flightSearchField, flightsTable);
                }
            }
        } catch (Exception e) {
            System.err.println("Error refreshing flights data: " + e.getMessage());
        }
    }
    
    private void refreshBookingsData() {
        try {
            if (bookingsBuilder != null && bookingsTable != null) {
                bookingsBuilder.setupBookingsTable(bookingsTable);
                if (bookingSearchField != null) {
                    bookingsBuilder.setupBookingSearch(bookingSearchField, bookingsTable);
                }
            }
        } catch (Exception e) {
            System.err.println("Error refreshing bookings data: " + e.getMessage());
        }
    }
    
    private void refreshMessagesData() {
        try {
            if (messagesBuilder != null) {
                messagesBuilder.setupMessagesContent(conversationsList, chatArea, chatScrollPane,
                                               messageInputArea, sendMessageBtn, chatHeaderLabel,
                                               automationToggle, unreadCountLabel);
            }
        } catch (Exception e) {
            System.err.println("Error refreshing messages data: " + e.getMessage());
        }
    }
    
    private void refreshTransactionsData() {
        try {
            if (transactionsBuilder != null && transactionsTable != null) {
                transactionsBuilder.setupTransactionsTable(transactionsTable);
                if (transactionSearchField != null) {
                    transactionsBuilder.setupTransactionSearch(transactionSearchField, transactionsTable);
                }
            }
        } catch (Exception e) {
            System.err.println("Error refreshing transactions data: " + e.getMessage());
        }
    }
    
    // FXML Action handlers
    @FXML
    private void addNewUser() {
        try {
            AdminUserDialogs.addNewUser(this::showAlert, this::refreshUsersData);
        } catch (Exception e) {
            System.err.println("Error adding new user: " + e.getMessage());
        }
    }
    
    @FXML
    private void addNewFlight() {
        try {
            AdminFlightDialogs.addNewFlight(this::showAlert, this::refreshFlightsData);
        } catch (Exception e) {
            System.err.println("Error adding new flight: " + e.getMessage());
        }
    }
    
 
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Clear session if UserSession exists
            try {
                UserSession.getInstance().clearSession();
            } catch (Exception e) {
                System.err.println("UserSession not available: " + e.getMessage());
            }
            
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
        try {
            if (dashboardContent != null) dashboardContent.setVisible(false);
            if (usersContent != null) usersContent.setVisible(false);
            if (flightsContent != null) flightsContent.setVisible(false);
            if (bookingsContent != null) bookingsContent.setVisible(false);
            if (messagesContent != null) messagesContent.setVisible(false);
            if (transactionsContent != null) transactionsContent.setVisible(false);
        } catch (Exception e) {
            System.err.println("Error hiding content: " + e.getMessage());
        }
    }
    
    private void updateActiveButton(Button activeButton) {
        try {
            if (navButtons != null) {
                for (Button btn : navButtons) {
                    if (btn != null) {
                        if (btn == activeButton) {
                            btn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 12 15;");
                        } else {
                            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #495057; -fx-padding: 12 15;");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating active button: " + e.getMessage());
        }
    }
    
    // Message handling methods
    private void loadConversation(int userId) {
        try {
            currentUserId = userId;
            if (messagesBuilder != null) {
                messagesBuilder.loadConversation(userId, chatArea, chatScrollPane,
                        chatHeaderLabel, automationToggle);
            }
        } catch (Exception e) {
            System.err.println("Error loading conversation: " + e.getMessage());
        }
    }
    
    @FXML
    private void sendMessage() {
        try {
            if (currentUserId == -1) {
                showWarningAlert("Warning", "Please select a conversation first.");
                return;
            }
            
            if (messageInputArea == null || messageInputArea.getText().trim().isEmpty()) {
                showWarningAlert("Warning", "Please enter a message.");
                return;
            }
            
            if (messagesBuilder != null) {
                messagesBuilder.sendMessage(currentUserId, messageInputArea.getText().trim(),
                                          sendMessageBtn, this::showAlert, this::loadConversation);
                messageInputArea.clear();
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    @FXML
    private void toggleAutomation() {
        try {
            if (currentUserId == -1) {
                if (automationToggle != null) {
                    automationToggle.setSelected(!automationToggle.isSelected());
                }
                showWarningAlert("Warning", "Please select a conversation first.");
                return;
            }
            
            if (messagesBuilder != null) {
                messagesBuilder.toggleAutomation(currentUserId, automationToggle, this::showAlert);
            }
        } catch (Exception e) {
            System.err.println("Error toggling automation: " + e.getMessage());
        }
    }
    
    // Search and filter methods
    @FXML
    private void searchFlights() {
        try {
            if (flightsBuilder != null && flightSearchField != null && flightsTable != null) {
                flightsBuilder.setupFlightSearch(flightSearchField, flightsTable);
            }
        } catch (Exception e) {
            System.err.println("Error searching flights: " + e.getMessage());
        }
    }

    @FXML
    private void searchBookings() {
        try {
            if (bookingsBuilder != null && bookingSearchField != null && bookingsTable != null) {
                bookingsBuilder.setupBookingSearch(bookingSearchField, bookingsTable);
            }
        } catch (Exception e) {
            System.err.println("Error searching bookings: " + e.getMessage());
        }
    }

    @FXML
    private void searchTransactions() {
        try {
            if (transactionsBuilder != null && transactionSearchField != null && transactionsTable != null) {
                transactionsBuilder.setupTransactionSearch(transactionSearchField, transactionsTable);
            }
        } catch (Exception e) {
            System.err.println("Error searching transactions: " + e.getMessage());
        }
    }
    
    // Clear search methods
    @FXML
    private void clearFlightSearch() {
        try {
            if (flightSearchField != null) {
                flightSearchField.clear();
                refreshFlightsData();
            }
        } catch (Exception e) {
            System.err.println("Error clearing flight search: " + e.getMessage());
        }
    }

    @FXML
    private void clearBookingSearch() {
        try {
            if (bookingSearchField != null) {
                bookingSearchField.clear();
                refreshBookingsData();
            }
        } catch (Exception e) {
            System.err.println("Error clearing booking search: " + e.getMessage());
        }
    }

    @FXML
    private void clearTransactionSearch() {
        try {
            if (transactionSearchField != null) {
                transactionSearchField.clear();
                refreshTransactionsData();
            }
        } catch (Exception e) {
            System.err.println("Error clearing transaction search: " + e.getMessage());
        }
    }

    @FXML
    private void clearMessageSearch() {
        try {
            if (messageSearchField != null) {
                messageSearchField.clear();
                refreshMessagesData();
            }
        } catch (Exception e) {
            System.err.println("Error clearing message search: " + e.getMessage());
        }
    }

    @FXML
    private void clearUserSearch() {
        try {
            refreshUsersData();
        } catch (Exception e) {
            System.err.println("Error clearing user search: " + e.getMessage());
        }
    }

    // Refresh methods for buttons
    @FXML
    private void refreshUsers() {
        try {
            refreshUsersData();
        } catch (Exception e) {
            System.err.println("Error refreshing users: " + e.getMessage());
        }
    }

    @FXML
    private void refreshFlights() {
        try {
            refreshFlightsData();
        } catch (Exception e) {
            System.err.println("Error refreshing flights: " + e.getMessage());
        }
    }

    @FXML
    private void refreshBookings() {
        try {
            refreshBookingsData();
        } catch (Exception e) {
            System.err.println("Error refreshing bookings: " + e.getMessage());
        }
    }

    @FXML
    private void refreshMessages() {
        try {
            refreshMessagesData();
        } catch (Exception e) {
            System.err.println("Error refreshing messages: " + e.getMessage());
        }
    }

    @FXML
    private void refreshTransactions() {
        try {
            refreshTransactionsData();
        } catch (Exception e) {
            System.err.println("Error refreshing transactions: " + e.getMessage());
        }
    }

    @FXML
    private void refreshDashboard() {
        try {
            loadDashboardStats();
            System.out.println("Dashboard refreshed");
        } catch (Exception e) {
            System.err.println("Error refreshing dashboard: " + e.getMessage());
        }
    }

    // Utility methods for alerts
    protected void showAlert(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing alert: " + e.getMessage());
        }
    }

    protected void showErrorAlert(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing error alert: " + e.getMessage());
        }
    }

    protected void showWarningAlert(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing warning alert: " + e.getMessage());
        }
    }
}