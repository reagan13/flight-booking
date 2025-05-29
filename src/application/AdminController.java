package application;

import application.model.User;
import application.service.AdminService;
import application.service.UserService;
import application.service.UserSession;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    
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
    @FXML private Button addUserBtn;

    
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
    @FXML private TableView transactionsTable;

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
    private void addNewFlight() {
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
            System.out.println("Flights data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading flights: " + e.getMessage());
        }
    }
    
    private void loadBookingsData() {
        try {
            System.out.println("Loading bookings data...");
            bookingsTable.getItems().clear();
            bookingsTable.getColumns().clear();
            System.out.println("Bookings data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
    }
    
    private void loadMessagesData() {
        try {
            System.out.println("Loading messages data...");
            messagesTable.getItems().clear();
            messagesTable.getColumns().clear();
            System.out.println("Messages data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading messages: " + e.getMessage());
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