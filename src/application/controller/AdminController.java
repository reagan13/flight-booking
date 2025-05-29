package application.controller;

import application.service.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    
    @FXML private Label adminNameLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalFlightsLabel;
    @FXML private Label totalBookingsLabel;
    
    @FXML private Button logoutButton;
    @FXML private Button dashboardBtn;
    @FXML private Button usersBtn;
    @FXML private Button flightsBtn;
    @FXML private Button bookingsBtn;
    @FXML private Button reportsBtn;
    
    @FXML private VBox dashboardContent;
    @FXML private VBox usersContent;
    @FXML private VBox flightsContent;
    @FXML private VBox bookingsContent;
    @FXML private VBox reportsContent;
    
    @FXML private TableView usersTable;
    @FXML private TableView flightsTable;
    @FXML private TableView bookingsTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set admin name from session
        if (UserSession.getInstance().getCurrentUser() != null) {
            adminNameLabel.setText("Welcome, " + UserSession.getInstance().getCurrentUserFullName());
        }
        
        // Load dashboard statistics
        loadDashboardStats();
        
        // Set up button hover effects
        setupButtonHoverEffects();
        
        // Show dashboard by default
        showDashboard();
    }
    
    private void setupButtonHoverEffects() {
        Button[] navButtons = {dashboardBtn, usersBtn, flightsBtn, bookingsBtn, reportsBtn};
        
        for (Button btn : navButtons) {
            btn.setOnMouseEntered(e -> {
                btn.setStyle(btn.getStyle() + "-fx-background-color: #4a5f7a;");
            });
            
            btn.setOnMouseExited(e -> {
                if (!btn.getStyle().contains("selected")) {
                    btn.setStyle(btn.getStyle().replace("-fx-background-color: #4a5f7a;", "-fx-background-color: transparent;"));
                }
            });
        }
    }
    
    private void loadDashboardStats() {
        // TODO: Load real statistics from database
        totalUsersLabel.setText("150");
        totalFlightsLabel.setText("25");
        totalBookingsLabel.setText("89");
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Clear user session
            UserSession.getInstance().logout();
            
            // Navigate back to login
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Login.fxml"));
            Parent loginRoot = loader.load();
            
            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.setTitle("JetSetGO - Login");
            stage.centerOnScreen();
            
            System.out.println("Admin logged out successfully");
            
        } catch (IOException e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void showDashboard() {
        hideAllContent();
        dashboardContent.setVisible(true);
        updateActiveButton(dashboardBtn);
    }
    
    @FXML
    private void showUsers() {
        hideAllContent();
        usersContent.setVisible(true);
        updateActiveButton(usersBtn);
        // TODO: Load users data
    }
    
    @FXML
    private void showFlights() {
        hideAllContent();
        flightsContent.setVisible(true);
        updateActiveButton(flightsBtn);
        // TODO: Load flights data
    }
    
    @FXML
    private void showBookings() {
        hideAllContent();
        bookingsContent.setVisible(true);
        updateActiveButton(bookingsBtn);
        // TODO: Load bookings data
    }
    
    @FXML
    private void showReports() {
        hideAllContent();
        reportsContent.setVisible(true);
        updateActiveButton(reportsBtn);
        // TODO: Load reports data
    }
    
    @FXML
    private void addNewFlight() {
        System.out.println("Add new flight clicked");
        // TODO: Open add flight dialog
    }
    
    @FXML
    private void viewAllUsers() {
        showUsers();
    }
    
    @FXML
    private void generateReport() {
        System.out.println("Generate report clicked");
        // TODO: Generate report functionality
    }
    
    private void hideAllContent() {
        dashboardContent.setVisible(false);
        usersContent.setVisible(false);
        flightsContent.setVisible(false);
        bookingsContent.setVisible(false);
        reportsContent.setVisible(false);
    }
    
    private void updateActiveButton(Button activeButton) {
        Button[] navButtons = {dashboardBtn, usersBtn, flightsBtn, bookingsBtn, reportsBtn};
        
        for (Button btn : navButtons) {
            if (btn == activeButton) {
                btn.setStyle(btn.getStyle() + "-fx-background-color: #4a5f7a; selected: true;");
            } else {
                btn.setStyle(btn.getStyle().replace("-fx-background-color: #4a5f7a;", "-fx-background-color: transparent;").replace("selected: true;", ""));
            }
        }
    }
}