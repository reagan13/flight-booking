package application;

import application.service.AdminService;
import application.service.UserSession;
import application.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.text.NumberFormat;
import java.util.Locale;

public class AdminController {
    
    // FXML Components
    @FXML private Label adminNameLabel;
    @FXML private VBox navigationMenu;
    
    // Navigation Buttons
    @FXML private Button dashboardBtn;
    @FXML private Button flightsBtn;
    @FXML private Button bookingsBtn;
    @FXML private Button usersBtn;
    @FXML private Button messagesBtn;
    @FXML private Button reportsBtn;
    @FXML private Button logoutBtn;
    
    // Content Areas
    @FXML private VBox dashboardContent;
    @FXML private VBox flightsContent;
    @FXML private VBox bookingsContent;
    @FXML private VBox usersContent;
    @FXML private VBox messagesContent;
    @FXML private VBox reportsContent;
    
    // Dashboard Statistics
    @FXML private Label totalUsersCount;
    @FXML private Label totalFlightsCount;
    @FXML private Label pendingBookingsCount;
    @FXML private Label totalRevenueAmount;
    @FXML private VBox recentActivityList;
    
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));
    private String currentActiveSection = "dashboard";
    
    @FXML
    public void initialize() {
        System.out.println("Admin Controller initializing...");
        
        // Set admin name
        if (UserSession.getInstance().isLoggedIn()) {
            User admin = UserSession.getInstance().getCurrentUser();
            adminNameLabel.setText(admin.getFirstName() + " " + admin.getLastName());
        }
        
        // Load dashboard data
        loadDashboardData();
        
        // Set initial active button
        updateActiveButton(dashboardBtn);
    }
    
    // Navigation Methods
    @FXML
    private void showDashboard() {
        switchContent("dashboard");
        updateActiveButton(dashboardBtn);
        loadDashboardData();
    }
    
    @FXML
    private void showFlights() {
        switchContent("flights");
        updateActiveButton(flightsBtn);
        // Will implement flight management in next step
    }
    
    @FXML
    private void showBookings() {
        switchContent("bookings");
        updateActiveButton(bookingsBtn);
        // Will implement booking management in next step
    }
    
    @FXML
    private void showUsers() {
        switchContent("users");
        updateActiveButton(usersBtn);
        // Will implement user management in next step
    }
    
    @FXML
    private void showMessages() {
        switchContent("messages");
        updateActiveButton(messagesBtn);
        // Will implement message management in next step
    }
    
    @FXML
    private void showReports() {
        switchContent("reports");
        updateActiveButton(reportsBtn);
        // Will implement reports in next step
    }
    
    @FXML
    private void handleLogout() {
        UserSession.getInstance().logout();
        
        try {
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
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
    
    // Helper Methods
    private void switchContent(String section) {
        // Hide all content areas
        dashboardContent.setVisible(false);
        flightsContent.setVisible(false);
        bookingsContent.setVisible(false);
        usersContent.setVisible(false);
        messagesContent.setVisible(false);
        reportsContent.setVisible(false);
        
        // Show selected content
        switch (section) {
            case "dashboard":
                dashboardContent.setVisible(true);
                break;
            case "flights":
                flightsContent.setVisible(true);
                break;
            case "bookings":
                bookingsContent.setVisible(true);
                break;
            case "users":
                usersContent.setVisible(true);
                break;
            case "messages":
                messagesContent.setVisible(true);
                break;
            case "reports":
                reportsContent.setVisible(true);
                break;
        }
        
        currentActiveSection = section;
    }
    
    private void updateActiveButton(Button activeButton) {
        // Reset all button styles
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: 500;";
        String activeStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: 500;";
        
        dashboardBtn.setStyle(inactiveStyle);
        flightsBtn.setStyle(inactiveStyle);
        bookingsBtn.setStyle(inactiveStyle);
        usersBtn.setStyle(inactiveStyle);
        messagesBtn.setStyle(inactiveStyle);
        reportsBtn.setStyle(inactiveStyle);
        
        // Set active button style
        activeButton.setStyle(activeStyle);
    }
    
    private void loadDashboardData() {
        // Load statistics in background thread
        Platform.runLater(() -> {
            try {
                // Get statistics from AdminService (we'll create this)
                int totalUsers = AdminService.getTotalUsersCount();
                int totalFlights = AdminService.getTotalFlightsCount();
                int pendingBookings = AdminService.getBookingCountByStatus("pending");
                double totalRevenue = AdminService.getTotalRevenue();
                
                // Update UI
                totalUsersCount.setText(String.valueOf(totalUsers));
                totalFlightsCount.setText(String.valueOf(totalFlights));
                pendingBookingsCount.setText(String.valueOf(pendingBookings));
                totalRevenueAmount.setText(currencyFormat.format(totalRevenue));
                
                // Load recent activity
                loadRecentActivity();
                
            } catch (Exception e) {
                System.err.println("Error loading dashboard data: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void loadRecentActivity() {
        // Clear existing activity
        recentActivityList.getChildren().clear();
        
        // Add sample recent activity - we'll implement this properly later
        VBox activityItem = new VBox(5);
        activityItem.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 5;");
        
        Label activityText = new Label("📋 New booking pending approval");
        activityText.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        
        Label activityTime = new Label("5 minutes ago");
        activityTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        activityItem.getChildren().addAll(activityText, activityTime);
        recentActivityList.getChildren().add(activityItem);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}