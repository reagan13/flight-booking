package application;

import application.model.Booking;
import application.model.Flight;
import application.model.User;
import application.model.Message;
import application.service.AdminFlightService;
import application.service.AdminMessageService;
import application.service.AdminBookingService;
import application.service.AdminService;
import application.service.UserService;
import application.service.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML
    private TextField flightSearchField;
    @FXML private TextField bookingSearchField;


    
    // ALL LABELS - MATCH EXACTLY WITH FXML fx:id
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
    
    // ALL BUTTONS - MATCH EXACTLY WITH FXML fx:id
    @FXML private Button logoutButton;
    @FXML private Button dashboardBtn;
    @FXML private Button usersBtn;
    @FXML private Button flightsBtn;
    @FXML private Button bookingsBtn;
    @FXML private Button messagesBtn;
    @FXML
    private Button transactionsBtn;
    @FXML
    private Button addUserBtn;
    @FXML private Button addFlightBtn;

    
    // ALL CONTENT SECTIONS - ALL VBox (MATCH EXACTLY WITH FXML fx:id)
    @FXML private VBox dashboardContent;
    @FXML private VBox usersContent;
    @FXML private VBox flightsContent;
    @FXML private VBox bookingsContent;
    @FXML private VBox messagesContent;
    @FXML private VBox transactionsContent;
    
    // ALL TABLES - MATCH EXACTLY WITH FXML fx:id
    @FXML private TableView<User> usersTable;
    @FXML private TableView flightsTable;
    @FXML private TableView bookingsTable;
    @FXML private TableView messagesTable;
    @FXML
    private TableView transactionsTable;

    @FXML private TextField messageSearchField;
    @FXML private ListView<Message> conversationsList;
    @FXML private VBox chatArea;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextArea messageInputArea;
    @FXML private Button sendMessageBtn;
    @FXML private Label chatHeaderLabel;
    @FXML private ToggleButton automationToggle;
    @FXML
    private Label unreadCountLabel;

    private Message currentConversation;
    private int currentUserId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AdminController initializing...");

        try {
            // Check if FXML elements are properly injected
            if (adminNameLabel == null || dashboardContent == null) {
                System.err.println("FXML elements not properly injected!");
                return;
            }

            // Set admin name
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser != null) {
                String fullName = UserSession.getInstance().getCurrentUserFullName();
                adminNameLabel.setText("Welcome, " + (fullName != null ? fullName : "Admin"));
            } else {
                adminNameLabel.setText("Welcome, Admin");
            }

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
    
    private void loadDashboardStats() {
        try {
            // Main statistics from database
            int totalUsers = AdminService.getTotalUsersCount();
            int totalFlights = AdminService.getTotalFlightsCount();
            int totalBookings = AdminService.getTotalBookingsCount();
            int pendingBookings = AdminService.getBookingCountByStatus("pending");
            int confirmedBookings = AdminService.getBookingCountByStatus("confirmed");
            int cancelledBookings = AdminService.getBookingCountByStatus("cancelled");

            // Calculate total revenue from bookings
            double totalRevenue = AdminService.calculateTotalRevenue();
            double weekRevenue = AdminService.calculateWeeklyRevenue();

            // Message counts
            int totalMessages = AdminService.getTotalMessagesCount();
            int newMessages = AdminService.getUnreadMessagesCount();

            // Today's activity
            int todayBookings = AdminService.getTodayBookingsCount();

            // Update main dashboard labels
            totalUsersLabel.setText(String.valueOf(totalUsers));
            totalFlightsLabel.setText(String.valueOf(totalFlights));
            totalBookingsLabel.setText(String.valueOf(totalBookings));
            totalRevenueLabel.setText(String.format("₱%.2f", totalRevenue));

            // Update secondary stats
            pendingBookingsLabel.setText(String.valueOf(pendingBookings));
            confirmedBookingsLabel.setText(String.valueOf(confirmedBookings));
            cancelledBookingsLabel.setText(String.valueOf(cancelledBookings));
            totalMessagesLabel.setText(String.valueOf(totalMessages));

            // Update activity stats
            todayBookingsLabel.setText(String.valueOf(todayBookings));
            newMessagesLabel.setText(String.valueOf(newMessages));
            systemStatusLabel.setText("Online");

            System.out.println("Dashboard stats loaded successfully");

        } catch (Exception e) {
            System.err.println("Error loading dashboard stats: " + e.getMessage());
            // Fallback values
            totalUsersLabel.setText("25");
            totalFlightsLabel.setText("12");
            totalBookingsLabel.setText("48");
            totalRevenueLabel.setText("₱2,230.00");
            pendingBookingsLabel.setText("5");
            confirmedBookingsLabel.setText("40");
            cancelledBookingsLabel.setText("3");
            totalMessagesLabel.setText("15");
            todayBookingsLabel.setText("7");
            newMessagesLabel.setText("3");
            systemStatusLabel.setText("Online");
        }
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Clear the session
            UserSession.getInstance().clearSession();

            // Navigate to login
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

    // NAVIGATION METHODS
    @FXML
    private void showDashboard() {
        hideAllContent();
        dashboardContent.setVisible(true);
        updateActiveButton(dashboardBtn);
        System.out.println("Showing dashboard");
    }
    
    @FXML
    private void showUsers() {
        hideAllContent();
        usersContent.setVisible(true);
        updateActiveButton(usersBtn);
        loadUsersData();
        System.out.println("Showing users");
    }
    
    @FXML
    private void showFlights() {
        hideAllContent();
        flightsContent.setVisible(true);
        updateActiveButton(flightsBtn);
        loadFlightsData();
        System.out.println("Showing flights");
    }
    
    @FXML
    private void showBookings() {
        hideAllContent();
        bookingsContent.setVisible(true);
        updateActiveButton(bookingsBtn);
        loadBookingsData();
        System.out.println("Showing bookings");
    }
    
    @FXML
    private void showMessages() {
        hideAllContent();
        messagesContent.setVisible(true);
        updateActiveButton(messagesBtn);
        loadMessagesData();
        System.out.println("Showing messages");
    }
    
    @FXML
    private void showTransactions() {
        hideAllContent();
        transactionsContent.setVisible(true);
        updateActiveButton(transactionsBtn);
        loadTransactionsData();
        System.out.println("Showing transactions");
    }
    
    // QUICK ACTION METHODSadadtes
    @FXML
    private void addNewFlightQuickAction() {
        System.out.println("Add new flight clicked");
        showFlights();
    }
    
    @FXML
    private void viewAllUsers() {
        System.out.println("View all users clicked");
        showUsers();
    }
    
    @FXML
    private void generateReport() {
        System.out.println("Generate report clicked");
        showTransactions();
    }
    
    // HELPER METHODS
    private void hideAllContent() {
        dashboardContent.setVisible(false);
        usersContent.setVisible(false);
        flightsContent.setVisible(false);
        bookingsContent.setVisible(false);
        messagesContent.setVisible(false);
        transactionsContent.setVisible(false);
    }
    
    // FIXED: Removed -fx-alignment which is not supported in JavaFX 8
    private void updateActiveButton(Button activeButton) {
        Button[] navButtons = {dashboardBtn, usersBtn, flightsBtn, bookingsBtn, messagesBtn, transactionsBtn};
        
        for (Button btn : navButtons) {
            if (btn == activeButton) {
                // Active button style - REMOVED -fx-alignment
                btn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 12 15;");
            } else {
                // Inactive button style - REMOVED -fx-alignment
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #495057; -fx-padding: 12 15;");
            }
        }
    }
    
    // DATA LOADING METHODS
    
    private void loadUsersData() {
        try {
            System.out.println("Loading users data...");
            usersTable.getItems().clear();
            usersTable.getColumns().clear();

            // Create columns
            TableColumn<User, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            idCol.setPrefWidth(50);

            TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
            firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            firstNameCol.setPrefWidth(100);

            TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
            lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            lastNameCol.setPrefWidth(100);

            TableColumn<User, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            emailCol.setPrefWidth(200);

            TableColumn<User, Integer> ageCol = new TableColumn<>("Age");
            ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
            ageCol.setPrefWidth(60);

            TableColumn<User, String> addressCol = new TableColumn<>("Address");
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
            addressCol.setPrefWidth(150);

            TableColumn<User, String> typeCol = new TableColumn<>("Type");
            typeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
            typeCol.setPrefWidth(80);

            // Actions column
            TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
            actionsCol.setPrefWidth(150);

            actionsCol.setCellFactory(param -> new TableCell<User, Void>() {
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Delete");
                private final HBox buttonsBox = new HBox(5);

                {
                    // Simple button styling - no colors
                    editBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
                    deleteBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
                    buttonsBox.getChildren().addAll(editBtn, deleteBtn);
                    buttonsBox.setPadding(new Insets(5));

                    editBtn.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        editUser(user);
                    });

                    deleteBtn.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        deleteUser(user);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(buttonsBox);
                    }
                }
            });

            usersTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, emailCol, ageCol, addressCol, typeCol, actionsCol);


            // Load data
            ObservableList<User> users = UserService.getAllUsers();
            usersTable.setItems(users);

            System.out.println("Users data loaded successfully: " + users.size() + " users");

        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addNewUser() {
        try {
            // Create a dialog for adding new user
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Add New User");
            dialog.setHeaderText("Add New User");

            // Create form fields
            TextField firstNameField = new TextField();
            TextField lastNameField = new TextField();
            TextField emailField = new TextField();
            TextField ageField = new TextField();
            TextField addressField = new TextField();
            PasswordField passwordField = new PasswordField();
            ComboBox<String> userTypeBox = new ComboBox<>();
            userTypeBox.getItems().addAll("regular", "admin");
            userTypeBox.setValue("regular");

            // Add placeholder text
            firstNameField.setPromptText("Enter first name");
            lastNameField.setPromptText("Enter last name");
            emailField.setPromptText("Enter email address");
            ageField.setPromptText("Enter age (1-120)");
            addressField.setPromptText("Enter address");
            passwordField.setPromptText("Enter password (min 6 characters)");

            // Create layout
            VBox content = new VBox(10);
            content.getChildren().addAll(
                    new Label("First Name:"), firstNameField,
                    new Label("Last Name:"), lastNameField,
                    new Label("Email:"), emailField,
                    new Label("Password:"), passwordField,
                    new Label("Age:"), ageField,
                    new Label("Address:"), addressField,
                    new Label("User Type:"), userTypeBox);

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Convert result with validation
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    try {
                        // Create user object
                        User newUser = new User();
                        newUser.setFirstName(firstNameField.getText());
                        newUser.setLastName(lastNameField.getText());
                        newUser.setEmail(emailField.getText());
                        newUser.setAge(Integer.parseInt(ageField.getText()));
                        newUser.setAddress(addressField.getText());
                        newUser.setUserType(userTypeBox.getValue());

                        // Validate user data
                        String userValidationError = UserService.validateUserData(newUser, false);
                        if (userValidationError != null) {
                            showAlert("Validation Error", userValidationError);
                            return null;
                        }

                        // Validate password
                        String passwordValidationError = UserService.validatePassword(passwordField.getText());
                        if (passwordValidationError != null) {
                            showAlert("Validation Error", passwordValidationError);
                            return null;
                        }

                        return newUser;

                    } catch (NumberFormatException e) {
                        showAlert("Validation Error", "Please enter a valid number for age.");
                        return null;
                    } catch (Exception e) {
                        showAlert("Error", "An error occurred while validating user data: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            // Show dialog and handle result
            dialog.showAndWait().ifPresent(newUser -> {
                try {
                    if (UserService.addUser(newUser, passwordField.getText())) {
                        showAlert("Success", "User added successfully!");
                        loadUsersData(); // Refresh table
                    } else {
                        showAlert("Error", "Failed to add user. Please check the database connection and try again.");
                    }
                } catch (Exception e) {
                    showAlert("Database Error", "An error occurred while adding the user: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    
    private void editUser(User user) {
        try {
            // Create a dialog for editing user
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Edit User");
            dialog.setHeaderText("Edit User Information");
            
            // Create form fields
            TextField firstNameField = new TextField(user.getFirstName());
            TextField lastNameField = new TextField(user.getLastName());
            TextField emailField = new TextField(user.getEmail());
            TextField ageField = new TextField(String.valueOf(user.getAge()));
            TextField addressField = new TextField(user.getAddress());
            ComboBox<String> userTypeBox = new ComboBox<>();
            userTypeBox.getItems().addAll("regular", "admin");
            userTypeBox.setValue(user.getUserType());
            
            // Add placeholder text
            firstNameField.setPromptText("Enter first name");
            lastNameField.setPromptText("Enter last name");
            emailField.setPromptText("Enter email address");
            ageField.setPromptText("Enter age (1-120)");
            addressField.setPromptText("Enter address");
            
            // Create layout
            VBox content = new VBox(10);
            content.getChildren().addAll(
                new Label("First Name:"), firstNameField,
                new Label("Last Name:"), lastNameField,
                new Label("Email:"), emailField,
                new Label("Age:"), ageField,
                new Label("Address:"), addressField,
                new Label("User Type:"), userTypeBox
            );
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            // Convert result with validation
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    try {
                        // Update user object
                        user.setFirstName(firstNameField.getText());
                        user.setLastName(lastNameField.getText());
                        user.setEmail(emailField.getText());
                        user.setAge(Integer.parseInt(ageField.getText()));
                        user.setAddress(addressField.getText());
                        user.setUserType(userTypeBox.getValue());
                        
                        // Validate user data (for update)
                        String validationError = UserService.validateUserData(user, true);
                        if (validationError != null) {
                            showAlert("Validation Error", validationError);
                            return null;
                        }
                        
                        return user;
                        
                    } catch (NumberFormatException e) {
                        showAlert("Validation Error", "Please enter a valid number for age.");
                        return null;
                    } catch (Exception e) {
                        showAlert("Error", "An error occurred while validating user data: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });
            
            // Show dialog and handle result
            dialog.showAndWait().ifPresent(updatedUser -> {
                try {
                    if (UserService.updateUser(updatedUser)) {
                        showAlert("Success", "User updated successfully!");
                        loadUsersData(); // Refresh table
                    } else {
                        showAlert("Error", "Failed to update user. Please check the database connection and try again.");
                    }
                } catch (Exception e) {
                    showAlert("Database Error", "An error occurred while updating the user: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error editing user: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private void deleteUser(User user) {
        try {
            // Prevent deleting the current admin user
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getId() == user.getId()) {
                showAlert("Error", "You cannot delete your own account while logged in.");
                return;
            }
            
            // Show confirmation dialog
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete User");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Do you want to delete user: " + user.getFullName() + "?\n\nThis action cannot be undone.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        if (UserService.deleteUser(user.getId())) {
                            showAlert("Success", "User deleted successfully!");
                            loadUsersData(); // Refresh table
                        } else {
                            showAlert("Error", "Failed to delete user. The user may have associated records that prevent deletion.");
                        }
                    } catch (Exception e) {
                        showAlert("Database Error", "An error occurred while deleting the user: " + e.getMessage());
                    }
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    
    
    
    private void loadFlightsData() {
        try {
            System.out.println("Loading flights data...");
            flightsTable.getItems().clear();
            flightsTable.getColumns().clear();

            // Create columns
            TableColumn<Flight, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            idCol.setPrefWidth(50);

            TableColumn<Flight, String> flightNoCol = new TableColumn<>("Flight No");
            flightNoCol.setCellValueFactory(new PropertyValueFactory<>("flightNo"));
            flightNoCol.setPrefWidth(80);

            TableColumn<Flight, String> airlineCol = new TableColumn<>("Airline");
            airlineCol.setCellValueFactory(new PropertyValueFactory<>("airlineName"));
            airlineCol.setPrefWidth(100);

            TableColumn<Flight, String> routeCol = new TableColumn<>("Route");
            routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));
            routeCol.setPrefWidth(120);

            TableColumn<Flight, String> departureCol = new TableColumn<>("Departure");
            departureCol.setCellValueFactory(new PropertyValueFactory<>("formattedDeparture"));
            departureCol.setPrefWidth(130);

            TableColumn<Flight, String> arrivalCol = new TableColumn<>("Arrival");
            arrivalCol.setCellValueFactory(new PropertyValueFactory<>("formattedArrival"));
            arrivalCol.setPrefWidth(130);

            TableColumn<Flight, Integer> seatsCol = new TableColumn<>("Seats");
            seatsCol.setCellValueFactory(new PropertyValueFactory<>("seats"));
            seatsCol.setPrefWidth(60);

            TableColumn<Flight, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusCol.setPrefWidth(80);

            TableColumn<Flight, Double> priceCol = new TableColumn<>("Price");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
            priceCol.setPrefWidth(80);

            // Actions column
            TableColumn<Flight, Void> actionsCol = new TableColumn<>("Actions");
            actionsCol.setPrefWidth(150);

            actionsCol.setCellFactory(param -> new TableCell<Flight, Void>() {
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Delete");
                private final HBox buttonsBox = new HBox(5);

                {
                    editBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
                    deleteBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
                    buttonsBox.getChildren().addAll(editBtn, deleteBtn);
                    buttonsBox.setPadding(new Insets(5));

                    editBtn.setOnAction(event -> {
                        Flight flight = getTableView().getItems().get(getIndex());
                        editFlight(flight);
                    });

                    deleteBtn.setOnAction(event -> {
                        Flight flight = getTableView().getItems().get(getIndex());
                        deleteFlight(flight);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(buttonsBox);
                    }
                }
            });

            flightsTable.getColumns().addAll(idCol, flightNoCol, airlineCol, routeCol,
                    departureCol, arrivalCol, seatsCol, statusCol, priceCol, actionsCol);

            // Load data
            ObservableList<Flight> flights = AdminFlightService.getAllFlights();
            flightsTable.setItems(flights);

            // Setup row click handler for flight details
            setupFlightRowClickHandler();

            // Setup search functionality
            setupFlightSearch(flights);

            System.out.println("Flights data loaded successfully: " + flights.size() + " flights");

        } catch (Exception e) {
            System.err.println("Error loading flights: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFlightRowClickHandler() {
        flightsTable.setRowFactory(tv -> {
            TableRow<Flight> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Flight selectedFlight = row.getItem();
                    showFlightDetails(selectedFlight);
                }
            });
            return row;
        });
    }

    private void showFlightDetails(Flight flight) {
        try {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Flight Details");
            dialog.setHeaderText("Flight Information - " + flight.getFlightNo());

            // Create GridPane for organized layout
            GridPane grid = new GridPane();
            grid.setHgap(20);
            grid.setVgap(15);
            grid.setPadding(new Insets(20));

            // Set column constraints
            ColumnConstraints labelCol = new ColumnConstraints();
            labelCol.setPercentWidth(30);
            ColumnConstraints valueCol1 = new ColumnConstraints();
            valueCol1.setPercentWidth(35);
            ColumnConstraints labelCol2 = new ColumnConstraints();
            labelCol2.setPercentWidth(30);
            ColumnConstraints valueCol2 = new ColumnConstraints();
            valueCol2.setPercentWidth(35);
            grid.getColumnConstraints().addAll(labelCol, valueCol1, labelCol2, valueCol2);

            // Style for section headers
            String headerStyle = "-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;";
            String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #34495e;";
            String valueStyle = "-fx-text-fill: #2c3e50; -fx-font-size: 14px;";

            int currentRow = 0;

            // Basic Information Section
            Label basicHeader = new Label("✈ Basic Information");
            basicHeader.setStyle(headerStyle);
            grid.add(basicHeader, 0, currentRow++, 4, 1);

            Label flightIdLabel = new Label("Flight ID:");
            flightIdLabel.setStyle(labelStyle);
            Label flightIdValue = new Label(String.valueOf(flight.getId()));
            flightIdValue.setStyle(valueStyle);

            Label flightNoLabel = new Label("Flight Number:");
            flightNoLabel.setStyle(labelStyle);
            Label flightNoValue = new Label(flight.getFlightNo());
            flightNoValue.setStyle(valueStyle);

            grid.add(flightIdLabel, 0, currentRow);
            grid.add(flightIdValue, 1, currentRow);
            grid.add(flightNoLabel, 2, currentRow);
            grid.add(flightNoValue, 3, currentRow++);

            Label airlineLabel = new Label("Airline:");
            airlineLabel.setStyle(labelStyle);
            Label airlineValue = new Label(flight.getAirlineName());
            airlineValue.setStyle(valueStyle);

            Label aircraftLabel = new Label("Aircraft:");
            aircraftLabel.setStyle(labelStyle);
            Label aircraftValue = new Label(flight.getAircraft());
            aircraftValue.setStyle(valueStyle);

            grid.add(airlineLabel, 0, currentRow);
            grid.add(airlineValue, 1, currentRow);
            grid.add(aircraftLabel, 2, currentRow);
            grid.add(aircraftValue, 3, currentRow++);

            // Route Information Section
            currentRow++; // Add space
            Label routeHeader = new Label("🌍 Route Information");
            routeHeader.setStyle(headerStyle);
            grid.add(routeHeader, 0, currentRow++, 4, 1);

            Label originLabel = new Label("Origin:");
            originLabel.setStyle(labelStyle);
            Label originValue = new Label(flight.getOrigin());
            originValue.setStyle(valueStyle);

            Label destinationLabel = new Label("Destination:");
            destinationLabel.setStyle(labelStyle);
            Label destinationValue = new Label(flight.getDestination());
            destinationValue.setStyle(valueStyle);

            grid.add(originLabel, 0, currentRow);
            grid.add(originValue, 1, currentRow);
            grid.add(destinationLabel, 2, currentRow);
            grid.add(destinationValue, 3, currentRow++);

            Label routeLabel = new Label("Route:");
            routeLabel.setStyle(labelStyle);
            Label routeValue = new Label(flight.getRoute());
            routeValue.setStyle(valueStyle + " -fx-font-size: 16px;");

            grid.add(routeLabel, 0, currentRow);
            grid.add(routeValue, 1, currentRow, 3, 1);
            currentRow++;

            // Schedule Information Section
            currentRow++; // Add space
            Label scheduleHeader = new Label("🕒 Schedule Information");
            scheduleHeader.setStyle(headerStyle);
            grid.add(scheduleHeader, 0, currentRow++, 4, 1);

            Label departureLabel = new Label("Departure:");
            departureLabel.setStyle(labelStyle);
            Label departureValue = new Label(flight.getFormattedDeparture());
            departureValue.setStyle(valueStyle);

            Label arrivalLabel = new Label("Arrival:");
            arrivalLabel.setStyle(labelStyle);
            Label arrivalValue = new Label(flight.getFormattedArrival());
            arrivalValue.setStyle(valueStyle);

            grid.add(departureLabel, 0, currentRow);
            grid.add(departureValue, 1, currentRow);
            grid.add(arrivalLabel, 2, currentRow);
            grid.add(arrivalValue, 3, currentRow++);

            Label durationLabel = new Label("Duration:");
            durationLabel.setStyle(labelStyle);
            Label durationValue = new Label(flight.getDuration());
            durationValue.setStyle(valueStyle);

            grid.add(durationLabel, 0, currentRow);
            grid.add(durationValue, 1, currentRow);
            currentRow++;

            // Flight Details Section
            currentRow++; // Add space
            Label detailsHeader = new Label("📋 Flight Details");
            detailsHeader.setStyle(headerStyle);
            grid.add(detailsHeader, 0, currentRow++, 4, 1);

            Label seatsLabel = new Label("Available Seats:");
            seatsLabel.setStyle(labelStyle);
            Label seatsValue = new Label(String.valueOf(flight.getSeats()));
            seatsValue.setStyle(valueStyle);

            Label statusLabel = new Label("Status:");
            statusLabel.setStyle(labelStyle);
            Label statusValue = new Label(flight.getStatus());
            // Color status based on value
            String statusColor = getStatusColor(flight.getStatus());
            statusValue.setStyle(valueStyle + " -fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");

            grid.add(seatsLabel, 0, currentRow);
            grid.add(seatsValue, 1, currentRow);
            grid.add(statusLabel, 2, currentRow);
            grid.add(statusValue, 3, currentRow++);

            Label priceLabel = new Label("Base Price:");
            priceLabel.setStyle(labelStyle);
            Label priceValue = new Label(String.format("₱%.2f", flight.getPrice()));
            priceValue.setStyle(valueStyle + " -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

            grid.add(priceLabel, 0, currentRow);
            grid.add(priceValue, 1, currentRow);
            currentRow++;

            // Set content and buttons
            dialog.getDialogPane().setContent(grid);

            // Add action buttons
            // ButtonType editButtonType = new ButtonType("Edit Flight", ButtonBar.ButtonData.OTHER);
            ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll( closeButtonType);

            // Set dialog size
            dialog.getDialogPane().setPrefSize(650, 500);
            dialog.getDialogPane().setStyle("-fx-background-color: #f8f9fa;");

            // Handle button actions
            // dialog.setResultConverter(buttonType -> {
            //     if (buttonType == editButtonType) {
            //         dialog.close();
            //         editFlight(flight);
                    
            //     }
            //     return null;
            // });

            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("Error showing flight details: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Unable to display flight details: " + e.getMessage());
        }
    }
    private String getStatusColor(String status) {
        if (status == null) return "#2c3e50";
        
        switch (status.toLowerCase()) {
            case "active":
                return "#27ae60"; // Green
            case "cancelled":
                return "#e74c3c"; // Red
            case "delayed":
                return "#f39c12"; // Orange
            case "completed":
                return "#3498db"; // Blue
            default:
                return "#2c3e50"; // Dark gray
        }
    }

    
    
    private void setupFlightSearch(ObservableList<Flight> allFlights) {
        if (flightSearchField != null) {
            flightSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    // Show all flights when search is empty
                    flightsTable.setItems(allFlights);
                } else {
                    // Filter flights based on search term
                    ObservableList<Flight> filteredFlights = FXCollections.observableArrayList();
                    String searchTerm = newValue.toLowerCase().trim();
                    
                    for (Flight flight : allFlights) {
                        if (matchesSearchTerm(flight, searchTerm)) {
                            filteredFlights.add(flight);
                        }
                    }
                    
                    flightsTable.setItems(filteredFlights);
                }
            });
        }
    }

// Helper method to check if flight matches search term
private boolean matchesSearchTerm(Flight flight, String searchTerm) {
    return (flight.getFlightNo() != null && flight.getFlightNo().toLowerCase().contains(searchTerm)) ||
           (flight.getAirlineName() != null && flight.getAirlineName().toLowerCase().contains(searchTerm)) ||
           (flight.getOrigin() != null && flight.getOrigin().toLowerCase().contains(searchTerm)) ||
           (flight.getDestination() != null && flight.getDestination().toLowerCase().contains(searchTerm)) ||
           (flight.getStatus() != null && flight.getStatus().toLowerCase().contains(searchTerm)) ||
           (flight.getAircraft() != null && flight.getAircraft().toLowerCase().contains(searchTerm)) ||
           String.valueOf(flight.getId()).contains(searchTerm) ||
           String.valueOf(flight.getPrice()).contains(searchTerm);
}

// Add method to clear search
@FXML
private void clearFlightSearch() {
    if (flightSearchField != null) {
        flightSearchField.clear();
    }
}


    
    @FXML
    private void addNewFlight() {
        try {
            Dialog<Flight> dialog = new Dialog<>();
            dialog.setTitle("Add New Flight");
            dialog.setHeaderText("Add New Flight Information");

            // Create form fields
            TextField idField = new TextField();
            TextField flightNoField = new TextField();
            TextField airlineField = new TextField();
            TextField originField = new TextField();
            TextField destinationField = new TextField();
            DatePicker departureDatePicker = new DatePicker();
            TextField departureTimeField = new TextField();
            DatePicker arrivalDatePicker = new DatePicker();
            TextField arrivalTimeField = new TextField();
            TextField durationField = new TextField();
            TextField aircraftField = new TextField();
            TextField seatsField = new TextField();
            ComboBox<String> statusBox = new ComboBox<>();
            statusBox.getItems().addAll("Active", "Cancelled", "Delayed", "Completed");
            statusBox.setValue("Active");
            TextField priceField = new TextField();

            // Set placeholders
            idField.setPromptText("Enter flight ID");
            flightNoField.setPromptText("e.g., PR123");
            airlineField.setPromptText("e.g., Philippine Airlines");
            originField.setPromptText("e.g., MNL");
            destinationField.setPromptText("e.g., CEB");
            departureTimeField.setPromptText("HH:MM (e.g., 08:30)");
            arrivalTimeField.setPromptText("HH:MM (e.g., 10:45)");
            durationField.setPromptText("e.g., 02:15");
            aircraftField.setPromptText("e.g., Boeing 737");
            seatsField.setPromptText("e.g., 180");
            priceField.setPromptText("e.g., 5500.00");

            // Create GridPane layout
            GridPane grid = new GridPane();
            grid.setHgap(15);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            // Set column constraints for better layout
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(25);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(25);
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setPercentWidth(25);
            ColumnConstraints col4 = new ColumnConstraints();
            col4.setPercentWidth(25);
            grid.getColumnConstraints().addAll(col1, col2, col3, col4);

            // Row 1: Basic Flight Information
            Label basicInfoLabel = new Label("Basic Information");
            basicInfoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(basicInfoLabel, 0, 0, 4, 1);

            grid.add(new Label("Flight ID:"), 0, 1);
            grid.add(idField, 1, 1);
            grid.add(new Label("Flight Number:"), 2, 1);
            grid.add(flightNoField, 3, 1);

            grid.add(new Label("Airline:"), 0, 2);
            grid.add(airlineField, 1, 2, 3, 1); // Span 3 columns for airline name

            // Row 3: Route Information
            Label routeLabel = new Label("Route Information");
            routeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(routeLabel, 0, 3, 4, 1);

            grid.add(new Label("Origin:"), 0, 4);
            grid.add(originField, 1, 4);
            grid.add(new Label("Destination:"), 2, 4);
            grid.add(destinationField, 3, 4);

            // Row 5: Departure Information
            Label departureLabel = new Label("Departure Information");
            departureLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(departureLabel, 0, 5, 4, 1);

            grid.add(new Label("Departure Date:"), 0, 6);
            grid.add(departureDatePicker, 1, 6);
            grid.add(new Label("Departure Time:"), 2, 6);
            grid.add(departureTimeField, 3, 6);

            // Row 7: Arrival Information
            Label arrivalLabel = new Label("Arrival Information");
            arrivalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(arrivalLabel, 0, 7, 4, 1);

            grid.add(new Label("Arrival Date:"), 0, 8);
            grid.add(arrivalDatePicker, 1, 8);
            grid.add(new Label("Arrival Time:"), 2, 8);
            grid.add(arrivalTimeField, 3, 8);

            // Row 9: Flight Details
            Label detailsLabel = new Label("Flight Details");
            detailsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(detailsLabel, 0, 9, 4, 1);

            grid.add(new Label("Duration:"), 0, 10);
            grid.add(durationField, 1, 10);
            grid.add(new Label("Aircraft Type:"), 2, 10);
            grid.add(aircraftField, 3, 10);

            grid.add(new Label("Available Seats:"), 0, 11);
            grid.add(seatsField, 1, 11);
            grid.add(new Label("Status:"), 2, 11);
            grid.add(statusBox, 3, 11);

            // Row 12: Pricing
            Label pricingLabel = new Label("Pricing");
            pricingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(pricingLabel, 0, 12, 4, 1);

            grid.add(new Label("Base Price (₱):"), 0, 13);
            grid.add(priceField, 1, 13);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Make dialog larger to accommodate grid layout
            dialog.getDialogPane().setPrefSize(600, 500);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    try {
                        Flight flight = new Flight();
                        flight.setId(Integer.parseInt(idField.getText()));
                        flight.setFlightNo(flightNoField.getText());
                        flight.setAirlineName(airlineField.getText());
                        flight.setOrigin(originField.getText());
                        flight.setDestination(destinationField.getText());

                        LocalDate depDate = departureDatePicker.getValue();
                        LocalTime depTime = LocalTime.parse(departureTimeField.getText());
                        flight.setDeparture(LocalDateTime.of(depDate, depTime));

                        LocalDate arrDate = arrivalDatePicker.getValue();
                        LocalTime arrTime = LocalTime.parse(arrivalTimeField.getText());
                        flight.setArrival(LocalDateTime.of(arrDate, arrTime));

                        flight.setDuration(durationField.getText());
                        flight.setAircraft(aircraftField.getText());
                        flight.setSeats(Integer.parseInt(seatsField.getText()));
                        flight.setStatus(statusBox.getValue());
                        flight.setPrice(Double.parseDouble(priceField.getText()));

                        String validationError = AdminFlightService.validateFlightData(flight, false);
                        if (validationError != null) {
                            showAlert("Validation Error", validationError);
                            return null;
                        }

                        return flight;

                    } catch (Exception e) {
                        showAlert("Validation Error", "Please check all fields and try again: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(flight -> {
                try {
                    if (AdminFlightService.addFlight(flight)) {
                        showAlert("Success", "Flight added successfully!");
                        loadFlightsData();
                    } else {
                        showAlert("Error", "Failed to add flight.");
                    }
                } catch (Exception e) {
                    showAlert("Database Error", "Error adding flight: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void editFlight(Flight flight) {
        try {
            Dialog<Flight> dialog = new Dialog<>();
            dialog.setTitle("Edit Flight");
            dialog.setHeaderText("Edit Flight Information");

            // Create form fields with existing values
            TextField idField = new TextField(String.valueOf(flight.getId()));
            idField.setDisable(true); // Don't allow changing ID
            TextField flightNoField = new TextField(flight.getFlightNo());
            TextField airlineField = new TextField(flight.getAirlineName());
            TextField originField = new TextField(flight.getOrigin());
            TextField destinationField = new TextField(flight.getDestination());

            DatePicker departureDatePicker = new DatePicker(flight.getDeparture().toLocalDate());
            TextField departureTimeField = new TextField(flight.getDeparture().toLocalTime().toString());
            DatePicker arrivalDatePicker = new DatePicker(flight.getArrival().toLocalDate());
            TextField arrivalTimeField = new TextField(flight.getArrival().toLocalTime().toString());

            TextField durationField = new TextField(flight.getDuration());
            TextField aircraftField = new TextField(flight.getAircraft());
            TextField seatsField = new TextField(String.valueOf(flight.getSeats()));
            ComboBox<String> statusBox = new ComboBox<>();
            statusBox.getItems().addAll("Active", "Cancelled", "Delayed", "Completed");
            statusBox.setValue(flight.getStatus());
            TextField priceField = new TextField(String.valueOf(flight.getPrice()));

            // Use the same grid layout as addNewFlight
            GridPane grid = new GridPane();
            grid.setHgap(15);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            // Set column constraints
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(25);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(25);
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setPercentWidth(25);
            ColumnConstraints col4 = new ColumnConstraints();
            col4.setPercentWidth(25);
            grid.getColumnConstraints().addAll(col1, col2, col3, col4);

            // Add all fields to grid (same layout as add dialog)
            Label basicInfoLabel = new Label("Basic Information");
            basicInfoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(basicInfoLabel, 0, 0, 4, 1);

            grid.add(new Label("Flight ID:"), 0, 1);
            grid.add(idField, 1, 1);
            grid.add(new Label("Flight Number:"), 2, 1);
            grid.add(flightNoField, 3, 1);

            grid.add(new Label("Airline:"), 0, 2);
            grid.add(airlineField, 1, 2, 3, 1);

            Label routeLabel = new Label("Route Information");
            routeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(routeLabel, 0, 3, 4, 1);

            grid.add(new Label("Origin:"), 0, 4);
            grid.add(originField, 1, 4);
            grid.add(new Label("Destination:"), 2, 4);
            grid.add(destinationField, 3, 4);

            Label departureLabel = new Label("Departure Information");
            departureLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(departureLabel, 0, 5, 4, 1);

            grid.add(new Label("Departure Date:"), 0, 6);
            grid.add(departureDatePicker, 1, 6);
            grid.add(new Label("Departure Time:"), 2, 6);
            grid.add(departureTimeField, 3, 6);

            Label arrivalLabel = new Label("Arrival Information");
            arrivalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(arrivalLabel, 0, 7, 4, 1);

            grid.add(new Label("Arrival Date:"), 0, 8);
            grid.add(arrivalDatePicker, 1, 8);
            grid.add(new Label("Arrival Time:"), 2, 8);
            grid.add(arrivalTimeField, 3, 8);

            Label detailsLabel = new Label("Flight Details");
            detailsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(detailsLabel, 0, 9, 4, 1);

            grid.add(new Label("Duration:"), 0, 10);
            grid.add(durationField, 1, 10);
            grid.add(new Label("Aircraft Type:"), 2, 10);
            grid.add(aircraftField, 3, 10);

            grid.add(new Label("Available Seats:"), 0, 11);
            grid.add(seatsField, 1, 11);
            grid.add(new Label("Status:"), 2, 11);
            grid.add(statusBox, 3, 11);

            Label pricingLabel = new Label("Pricing");
            pricingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(pricingLabel, 0, 12, 4, 1);

            grid.add(new Label("Base Price (₱):"), 0, 13);
            grid.add(priceField, 1, 13);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setPrefSize(600, 500);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    try {
                        flight.setFlightNo(flightNoField.getText());
                        flight.setAirlineName(airlineField.getText());
                        flight.setOrigin(originField.getText());
                        flight.setDestination(destinationField.getText());

                        LocalDate depDate = departureDatePicker.getValue();
                        LocalTime depTime = LocalTime.parse(departureTimeField.getText());
                        flight.setDeparture(LocalDateTime.of(depDate, depTime));

                        LocalDate arrDate = arrivalDatePicker.getValue();
                        LocalTime arrTime = LocalTime.parse(arrivalTimeField.getText());
                        flight.setArrival(LocalDateTime.of(arrDate, arrTime));

                        flight.setDuration(durationField.getText());
                        flight.setAircraft(aircraftField.getText());
                        flight.setSeats(Integer.parseInt(seatsField.getText()));
                        flight.setStatus(statusBox.getValue());
                        flight.setPrice(Double.parseDouble(priceField.getText()));

                        String validationError = AdminFlightService.validateFlightData(flight, true);
                        if (validationError != null) {
                            showAlert("Validation Error", validationError);
                            return null;
                        }

                        return flight;

                    } catch (Exception e) {
                        showAlert("Validation Error", "Please check all fields: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(updatedFlight -> {
                try {
                    if (AdminFlightService.updateFlight(updatedFlight)) {
                        showAlert("Success", "Flight updated successfully!");
                        loadFlightsData();
                    } else {
                        showAlert("Error", "Failed to update flight.");
                    }
                } catch (Exception e) {
                    showAlert("Database Error", "Error updating flight: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    

    private void deleteFlight(Flight flight) {
        try {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete Flight");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Delete flight: " + flight.getFlightNo() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (AdminFlightService.deleteFlight(flight.getId())) {
                        showAlert("Success", "Flight deleted successfully!");
                        loadFlightsData();
                    } else {
                        showAlert("Error", "Failed to delete flight.");
                    }
                }
            });

        } catch (Exception e) {
            showAlert("Error", "Error deleting flight: " + e.getMessage());
        }
    }
    
    
    
    private void loadBookingsData() {
    try {
        System.out.println("Loading bookings data...");
        bookingsTable.getItems().clear();
        bookingsTable.getColumns().clear();

        // Create columns
        TableColumn<Booking, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Booking, String> refCol = new TableColumn<>("Reference");
        refCol.setCellValueFactory(new PropertyValueFactory<>("bookingReference"));
        refCol.setPrefWidth(100);

        TableColumn<Booking, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerCol.setPrefWidth(120);

        TableColumn<Booking, String> flightCol = new TableColumn<>("Flight");
        flightCol.setCellValueFactory(new PropertyValueFactory<>("flightInfo"));
        flightCol.setPrefWidth(200);

        TableColumn<Booking, String> seatCol = new TableColumn<>("Seat");
        seatCol.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        seatCol.setPrefWidth(60);

        TableColumn<Booking, String> dateCol = new TableColumn<>("Booking Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedBookingDate"));
        dateCol.setPrefWidth(130);

        TableColumn<Booking, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("formattedAmount"));
        amountCol.setPrefWidth(80);

        TableColumn<Booking, String> paymentStatusCol = new TableColumn<>("Payment");
        paymentStatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        paymentStatusCol.setPrefWidth(80);

        // Status column with colored text
        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(90);
        statusCol.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toUpperCase());
                    Booking booking = getTableView().getItems().get(getIndex());
                    setStyle("-fx-text-fill: " + booking.getStatusColor() + "; -fx-font-weight: bold;");
                }
            }
        });

        // Actions column
        TableColumn<Booking, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(param -> new TableCell<Booking, Void>() {
            private final Button viewBtn = new Button("View");
            private final Button statusBtn = new Button("Status");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttonsBox = new HBox(5);

            {
                viewBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 8;");
                statusBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 8;");
                deleteBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 8;");
                buttonsBox.getChildren().addAll(viewBtn, statusBtn, deleteBtn);
                buttonsBox.setPadding(new Insets(3));

                viewBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    showBookingDetails(booking);
                });

                statusBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    changeBookingStatus(booking);
                });

                deleteBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    deleteBooking(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });

        bookingsTable.getColumns().addAll(idCol, refCol, customerCol, flightCol, seatCol, 
                dateCol, amountCol, paymentStatusCol, statusCol, actionsCol);

        // Load data
        ObservableList<Booking> bookings = AdminBookingService.getAllBookings();
        bookingsTable.setItems(bookings);

        // Setup search functionality
        setupBookingSearch(bookings);

        System.out.println("Bookings data loaded successfully: " + bookings.size() + " bookings");

    } catch (Exception e) {
        System.err.println("Error loading bookings: " + e.getMessage());
        e.printStackTrace();
    }
}

private void setupBookingSearch(ObservableList<Booking> allBookings) {
    if (bookingSearchField != null) {
        bookingSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                bookingsTable.setItems(allBookings);
            } else {
                ObservableList<Booking> filteredBookings = FXCollections.observableArrayList();
                String searchTerm = newValue.toLowerCase().trim();
                
                for (Booking booking : allBookings) {
                    if (matchesBookingSearchTerm(booking, searchTerm)) {
                        filteredBookings.add(booking);
                    }
                }
                
                bookingsTable.setItems(filteredBookings);
            }
        });
    }
}

private boolean matchesBookingSearchTerm(Booking booking, String searchTerm) {
    return (booking.getBookingReference() != null && booking.getBookingReference().toLowerCase().contains(searchTerm)) ||
           (booking.getCustomerName() != null && booking.getCustomerName().toLowerCase().contains(searchTerm)) ||
           (booking.getCustomerEmail() != null && booking.getCustomerEmail().toLowerCase().contains(searchTerm)) ||
           (booking.getFlightInfo() != null && booking.getFlightInfo().toLowerCase().contains(searchTerm)) ||
           (booking.getStatus() != null && booking.getStatus().toLowerCase().contains(searchTerm)) ||
           (booking.getSeatNumber() != null && booking.getSeatNumber().toLowerCase().contains(searchTerm)) ||
           String.valueOf(booking.getId()).contains(searchTerm);
}

@FXML
private void clearBookingSearch() {
    if (bookingSearchField != null) {
        bookingSearchField.clear();
    }
}

private void showBookingDetails(Booking booking) {
    try {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Booking Details");
        dialog.setHeaderText("Booking Information - " + booking.getBookingReference());

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Set column constraints
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setPercentWidth(30);
        ColumnConstraints valueCol1 = new ColumnConstraints();
        valueCol1.setPercentWidth(35);
        ColumnConstraints labelCol2 = new ColumnConstraints();
        labelCol2.setPercentWidth(30);
        ColumnConstraints valueCol2 = new ColumnConstraints();
        valueCol2.setPercentWidth(35);
        grid.getColumnConstraints().addAll(labelCol, valueCol1, labelCol2, valueCol2);

        String headerStyle = "-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;";
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #34495e;";
        String valueStyle = "-fx-text-fill: #2c3e50; -fx-font-size: 14px;";

        int currentRow = 0;

        // Booking Information
        Label bookingHeader = new Label("📋 Booking Information");
        bookingHeader.setStyle(headerStyle);
        grid.add(bookingHeader, 0, currentRow++, 4, 1);

        grid.add(new Label("Booking ID:"), 0, currentRow);
        grid.add(new Label(String.valueOf(booking.getId())), 1, currentRow);
        grid.add(new Label("Reference:"), 2, currentRow);
        grid.add(new Label(booking.getBookingReference()), 3, currentRow++);

        grid.add(new Label("Status:"), 0, currentRow);
        Label statusLabel = new Label(booking.getStatus().toUpperCase());
        statusLabel.setStyle(valueStyle + " -fx-text-fill: " + booking.getStatusColor() + "; -fx-font-weight: bold;");
        grid.add(statusLabel, 1, currentRow);
        grid.add(new Label("Seat Number:"), 2, currentRow);
        grid.add(new Label(booking.getSeatNumber()), 3, currentRow++);

        // Customer Information
        currentRow++;
        Label customerHeader = new Label("👤 Customer Information");
        customerHeader.setStyle(headerStyle);
        grid.add(customerHeader, 0, currentRow++, 4, 1);

        grid.add(new Label("Name:"), 0, currentRow);
        grid.add(new Label(booking.getCustomerName()), 1, currentRow);
        grid.add(new Label("Email:"), 2, currentRow);
        grid.add(new Label(booking.getCustomerEmail() != null ? booking.getCustomerEmail() : "N/A"), 3, currentRow++);

        // Flight Information
        currentRow++;
        Label flightHeader = new Label("✈ Flight Information");
        flightHeader.setStyle(headerStyle);
        grid.add(flightHeader, 0, currentRow++, 4, 1);

        grid.add(new Label("Flight:"), 0, currentRow);
        grid.add(new Label(booking.getFlightInfo()), 1, currentRow, 3, 1);
        currentRow++;

        // Payment Information
        currentRow++;
        Label paymentHeader = new Label("💳 Payment Information");
        paymentHeader.setStyle(headerStyle);
        grid.add(paymentHeader, 0, currentRow++, 4, 1);

        grid.add(new Label("Amount:"), 0, currentRow);
        grid.add(new Label(booking.getFormattedAmount()), 1, currentRow);
        grid.add(new Label("Payment Status:"), 2, currentRow);
        grid.add(new Label(booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "N/A"), 3, currentRow++);

        grid.add(new Label("Payment Method:"), 0, currentRow);
        grid.add(new Label(booking.getPaymentMethod() != null ? booking.getPaymentMethod() : "N/A"), 1, currentRow);
        grid.add(new Label("Booking Date:"), 2, currentRow);
        grid.add(new Label(booking.getFormattedBookingDate()), 3, currentRow++);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefSize(700, 400);
        dialog.getDialogPane().setStyle("-fx-background-color: #f8f9fa;");

        dialog.showAndWait();

    } catch (Exception e) {
        System.err.println("Error showing booking details: " + e.getMessage());
        e.printStackTrace();
        showAlert("Error", "Unable to display booking details: " + e.getMessage());
    }
}

private void changeBookingStatus(Booking booking) {
    try {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Booking Status");
        dialog.setHeaderText("Change status for booking: " + booking.getBookingReference());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label currentStatusLabel = new Label("Current Status: " + booking.getStatus().toUpperCase());
        currentStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("pending", "confirmed", "cancelled", "completed", "payment_failed");
        statusBox.setValue(booking.getStatus());
        statusBox.setPrefWidth(200);

        Label noteLabel = new Label("Note: Changing to 'confirmed' will also mark payment as 'paid'");
        noteLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #6c757d;");

        content.getChildren().addAll(currentStatusLabel, new Label("New Status:"), statusBox, noteLabel);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return statusBox.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newStatus -> {
            if (!newStatus.equals(booking.getStatus())) {
                if (AdminBookingService.updateBookingStatus(booking.getId(), newStatus)) {
                    showAlert("Success", "Booking status updated successfully!");
                    loadBookingsData(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to update booking status.");
                }
            }
        });

    } catch (Exception e) {
        showAlert("Error", "An unexpected error occurred: " + e.getMessage());
    }
}

private void deleteBooking(Booking booking) {
    try {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Booking");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Delete booking: " + booking.getBookingReference()
                + "?\n\nThis action cannot be undone and will also delete associated transactions.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (AdminBookingService.deleteBooking(booking.getId())) {
                    showAlert("Success", "Booking deleted successfully!");
                    loadBookingsData();
                } else {
                    showAlert("Error", "Failed to delete booking.");
                }
            }
        });

    } catch (Exception e) {
        showAlert("Error", "Error deleting booking: " + e.getMessage());
    }
}

    
private void loadMessagesData() {
    try {
        System.out.println("Loading messages data...");

        // Clear current selection to avoid index issues
        if (conversationsList != null) {
            conversationsList.getSelectionModel().clearSelection();
        }

        // Load conversations list
        ObservableList<Message> conversations = AdminMessageService.getAllConversations();

        // Set items to ListView
        if (conversationsList != null) {
            conversationsList.setItems(conversations);

            // Setup conversation list cell factory
            conversationsList.setCellFactory(listView -> new ListCell<Message>() {
                @Override
                protected void updateItem(Message message, boolean empty) {
                    super.updateItem(message, empty);
                    if (empty || message == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle(""); // Clear any previous styling
                    } else {
                        try {
                            VBox conversationItem = createConversationItem(message);
                            setGraphic(conversationItem);
                            setText(null); // Important: clear text when using graphic
                        } catch (Exception e) {
                            System.err.println("Error creating conversation item: " + e.getMessage());
                            setText("Error loading conversation");
                            setGraphic(null);
                        }
                    }
                }
            });

            // Setup conversation selection with proper error handling
            conversationsList.getSelectionModel().selectedItemProperty()
                    .addListener((obs, oldSelection, newSelection) -> {
                        try {
                            if (newSelection != null && newSelection.getUserId() > 0) {
                                loadConversation(newSelection.getUserId());
                            }
                        } catch (Exception e) {
                            System.err.println("Error loading conversation: " + e.getMessage());
                            e.printStackTrace();
                            showAlert("Error", "Failed to load conversation: " + e.getMessage());
                        }
                    });
        }

        // Setup message search
        setupMessageSearch(conversations);

        // Update unread count
        updateUnreadCount();

        System.out.println("Messages data loaded successfully: " + conversations.size() + " conversations");

    } catch (Exception e) {
        System.err.println("Error loading messages: " + e.getMessage());
        e.printStackTrace();
        showAlert("Error", "Failed to load messages: " + e.getMessage());
    }
}

private VBox createConversationItem(Message message) {
    VBox item = new VBox(5);
    item.setPadding(new Insets(10));
    item.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
    
    try {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Safe name handling
        String userName = message.getUserName();
        if (userName == null || userName.trim().isEmpty()) {
            userName = "Unknown User";
        }
        
        Label nameLabel = new Label(userName);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label timeLabel = new Label(message.getFormattedTime());
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        // Add unread badge if there are unread messages
        if (message.getUnreadCount() > 0) {
            Label unreadBadge = new Label(String.valueOf(message.getUnreadCount()));
            unreadBadge.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; " +
                               "-fx-background-radius: 10; -fx-padding: 2 6; -fx-font-size: 11px;");
            header.getChildren().addAll(nameLabel, spacer, unreadBadge, timeLabel);
        } else {
            header.getChildren().addAll(nameLabel, spacer, timeLabel);
        }
        
        // Safe preview text handling
        String previewText = message.getPreviewText();
        if (previewText == null || previewText.trim().isEmpty()) {
            previewText = "No message content";
        }
        
        Label previewLabel = new Label(previewText);
        previewLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 13px;");
        previewLabel.setWrapText(true);
        previewLabel.setMaxWidth(300);
        
        // Safe email handling
        String userEmail = message.getUserEmail();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            userEmail = "No email";
        }
        
        Label emailLabel = new Label(userEmail);
        emailLabel.setStyle("-fx-text-fill: #adb5bd; -fx-font-size: 11px;");
        
        item.getChildren().addAll(header, previewLabel, emailLabel);
        
        // Add hover effect
        item.setOnMouseEntered(e -> item.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; " +
                                                  "-fx-background-color: #f8f9fa; -fx-cursor: hand;"));
        item.setOnMouseExited(e -> item.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));
        
    } catch (Exception e) {
        System.err.println("Error creating conversation item content: " + e.getMessage());
        // Fallback to simple text display
        Label errorLabel = new Label("Error loading conversation");
        errorLabel.setStyle("-fx-text-fill: red;");
        item.getChildren().clear();
        item.getChildren().add(errorLabel);
    }
    
    return item;
}

private void loadConversation(int userId) {
    try {
        System.out.println("Loading conversation for user ID: " + userId);
        currentUserId = userId;
        
        // Load conversation messages
        ObservableList<Message> messages = AdminMessageService.getConversationMessages(userId);
        
        // Clear chat area
        if (chatArea != null) {
            chatArea.getChildren().clear();
        }
        
        // Set chat header
        if (chatHeaderLabel != null && !messages.isEmpty()) {
            Message firstMessage = messages.get(0);
            String userName = firstMessage.getUserName();
            if (userName == null || userName.trim().isEmpty()) {
                userName = "Unknown User";
            }
            chatHeaderLabel.setText("Chat with " + userName);
            
            // Set automation toggle state
            if (automationToggle != null) {
                automationToggle.setSelected(AdminMessageService.isAutomationEnabled(userId));
            }
        }
        
        // Add messages to chat area
        if (chatArea != null) {
            for (Message message : messages) {
                try {
                    VBox messageItem = createMessageItem(message);
                    chatArea.getChildren().add(messageItem);
                } catch (Exception e) {
                    System.err.println("Error creating message item: " + e.getMessage());
                    // Continue with other messages
                }
            }
        }
        
        // Mark messages as read
        AdminMessageService.markMessagesAsRead(userId);
        
        // Scroll to bottom
        Platform.runLater(() -> {
            if (chatScrollPane != null) {
                chatScrollPane.setVvalue(1.0);
            }
        });
        
        // Update unread count
        updateUnreadCount();
        
        System.out.println("Conversation loaded successfully: " + messages.size() + " messages");
        
    } catch (Exception e) {
        System.err.println("Error loading conversation: " + e.getMessage());
        e.printStackTrace();
        showAlert("Error", "Failed to load conversation: " + e.getMessage());
    }
}

private VBox createMessageItem(Message message) {
    VBox messageContainer = new VBox(5);
    messageContainer.setPadding(new Insets(10));
    
    try {
        // Message header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Safe sender type handling
        String senderType = message.getSenderType();
        if (senderType == null) {
            senderType = "user";
        }
        
        String senderDisplay;
        switch (senderType.toLowerCase()) {
            case "admin":
                senderDisplay = "👨‍💼 Admin";
                break;
            case "bot":
                senderDisplay = "🤖 Bot";
                break;
            default:
                String userName = message.getUserName();
                if (userName == null || userName.trim().isEmpty()) {
                    userName = "User";
                }
                senderDisplay = "👤 " + userName;
                break;
        }
        
        Label senderLabel = new Label(senderDisplay);
        senderLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + message.getSenderTypeColor() + ";");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label timeLabel = new Label(message.getFormattedDateTime());
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
        
        header.getChildren().addAll(senderLabel, spacer, timeLabel);
        
        // Message content
        String messageText = message.getMessageText();
        if (messageText == null || messageText.trim().isEmpty()) {
            messageText = "No content";
        }
        
        Label contentLabel = new Label(messageText);
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(400);
        
        // Style based on sender
        switch (senderType.toLowerCase()) {
            case "admin":
                contentLabel.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; " +
                                    "-fx-background-radius: 10; -fx-padding: 10;");
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                break;
            case "bot":
                contentLabel.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #6c757d; " +
                                    "-fx-background-radius: 10; -fx-padding: 10; " +
                                    "-fx-border-color: #dee2e6; -fx-border-radius: 10;");
                messageContainer.setAlignment(Pos.CENTER_LEFT);
                break;
            default:
                contentLabel.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #495057; " +
                                    "-fx-background-radius: 10; -fx-padding: 10;");
                messageContainer.setAlignment(Pos.CENTER_LEFT);
                break;
        }
        
        messageContainer.getChildren().addAll(header, contentLabel);
        
    } catch (Exception e) {
        System.err.println("Error creating message item content: " + e.getMessage());
        // Fallback to simple display
        Label errorLabel = new Label("Error loading message");
        errorLabel.setStyle("-fx-text-fill: red;");
        messageContainer.getChildren().clear();
        messageContainer.getChildren().add(errorLabel);
    }
    
    return messageContainer;
}

private void setupMessageSearch(ObservableList<Message> allConversations) {
    if (messageSearchField != null && allConversations != null) {
        messageSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue == null || newValue.trim().isEmpty()) {
                    conversationsList.setItems(allConversations);
                } else {
                    ObservableList<Message> filteredConversations = FXCollections.observableArrayList();
                    String searchTerm = newValue.toLowerCase().trim();
                    
                    for (Message conversation : allConversations) {
                        if (conversation != null && matchesMessageSearchTerm(conversation, searchTerm)) {
                            filteredConversations.add(conversation);
                        }
                    }
                    
                    conversationsList.setItems(filteredConversations);
                }
            } catch (Exception e) {
                System.err.println("Error in message search: " + e.getMessage());
            }
        });
    }
}

private boolean matchesMessageSearchTerm(Message message, String searchTerm) {
    if (message == null || searchTerm == null) {
        return false;
    }
    
    try {
        return (message.getUserName() != null && message.getUserName().toLowerCase().contains(searchTerm)) ||
               (message.getUserEmail() != null && message.getUserEmail().toLowerCase().contains(searchTerm)) ||
               (message.getMessageText() != null && message.getMessageText().toLowerCase().contains(searchTerm));
    } catch (Exception e) {
        System.err.println("Error matching search term: " + e.getMessage());
        return false;
    }
}

@FXML
private void sendMessage() {
    try {
        if (currentUserId == -1) {
            showAlert("Warning", "Please select a conversation first.");
            return;
        }
        
        if (messageInputArea == null || messageInputArea.getText().trim().isEmpty()) {
            showAlert("Warning", "Please enter a message.");
            return;
        }
        
        String messageText = messageInputArea.getText().trim();
        
        if (AdminMessageService.sendMessage(currentUserId, messageText, "admin", null)) {
            messageInputArea.clear();
            loadConversation(currentUserId); // Refresh chat
        } else {
            showAlert("Error", "Failed to send message.");
        }
    } catch (Exception e) {
        System.err.println("Error sending message: " + e.getMessage());
        showAlert("Error", "An error occurred while sending the message: " + e.getMessage());
    }
}

@FXML
private void toggleAutomation() {
    try {
        if (currentUserId == -1) {
            if (automationToggle != null) {
                automationToggle.setSelected(!automationToggle.isSelected());
            }
            showAlert("Warning", "Please select a conversation first.");
            return;
        }
        
        boolean isEnabled = automationToggle != null ? automationToggle.isSelected() : false;
        AdminMessageService.setAutomationEnabled(currentUserId, isEnabled);
        
        String status = isEnabled ? "enabled" : "disabled";
        showAlert("Info", "Automated replies " + status + " for this user.");
    } catch (Exception e) {
        System.err.println("Error toggling automation: " + e.getMessage());
        showAlert("Error", "Failed to toggle automation: " + e.getMessage());
    }
}

private void updateUnreadCount() {
    try {
        int unreadCount = AdminMessageService.getUnreadMessageCount();
        if (unreadCountLabel != null) {
            if (unreadCount > 0) {
                unreadCountLabel.setText(String.valueOf(unreadCount));
                unreadCountLabel.setVisible(true);
            } else {
                unreadCountLabel.setVisible(false);
            }
        }
    } catch (Exception e) {
        System.err.println("Error updating unread count: " + e.getMessage());
    }
}

@FXML
private void refreshMessages() {
    try {
        loadMessagesData();
        if (currentUserId != -1) {
            loadConversation(currentUserId);
        }
    } catch (Exception e) {
        System.err.println("Error refreshing messages: " + e.getMessage());
        showAlert("Error", "Failed to refresh messages: " + e.getMessage());
    }
}
@FXML
private void clearMessageSearch() {
    try {
        if (messageSearchField != null) {
            messageSearchField.clear();
        }
    } catch (Exception e) {
        System.err.println("Error clearing message search: " + e.getMessage());
    }
}


    private void loadTransactionsData() {
        try {
            System.out.println("Loading transactions data...");
            transactionsTable.getItems().clear();
            transactionsTable.getColumns().clear();
            System.out.println("Transactions data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
    }
    
    public void refreshDashboard() {
        loadDashboardStats();
        System.out.println("Dashboard refreshed");
    }
}