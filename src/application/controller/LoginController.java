package application.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.database.DatabaseConnection;
import application.model.User;
import application.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    
    @FXML
    private TextField emailPhoneField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Hyperlink signupLink;
    
    @FXML
    private Hyperlink forgotPasswordLink;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailPhoneField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Simple validation
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Modified query to only check email since there's no phone column
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password); // In a real app, use password hashing
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Login successful
                int userId = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String userEmail = rs.getString("email");
                String userType = rs.getString("user_type");
                String userAddress = rs.getString("address");
                int userAge = rs.getInt("age");
                
                // Create User object
                User currentUser = new User(userId, firstName, lastName, userEmail, userAddress, userAge, userType);
                
                // Store user info in session
                UserSession.getInstance().login(currentUser);
                
                // Navigate to appropriate screen based on user type
                if ("admin".equals(userType)) {
                    navigateToAdminDashboard(event);
                } else {
                    navigateToHome(event);
                }
            } else {
                showError("Invalid email or password");
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void handleForgotPassword(ActionEvent event) {
        try {
            Parent forgotPasswordRoot = FXMLLoader.load(getClass().getResource("/resources/ForgotPassword.fxml"));
            Scene forgotPasswordScene = new Scene(forgotPasswordRoot);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(forgotPasswordScene);
            stage.show();
        } catch (IOException e) {
            showError("Could not load forgot password page");
            e.printStackTrace();
        }
    }
    
    @FXML
    public void switchToSignup(ActionEvent event) {
        try {
            Parent signupRoot = FXMLLoader.load(getClass().getResource("/resources/Signup.fxml"));
            Scene signupScene = new Scene(signupRoot);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Apply existing stylesheets
            if (((Node) event.getSource()).getScene().getStylesheets() != null) {
                signupScene.getStylesheets().addAll(((Node) event.getSource()).getScene().getStylesheets());
            }
            
            stage.setScene(signupScene);
            stage.show();
        } catch (IOException e) {
            showError("Could not load signup page");
            e.printStackTrace();
        }
    }
    
    private void navigateToHome(ActionEvent event) {
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/resources/home.fxml"));
            Scene homeScene = new Scene(homeRoot);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(homeScene);
            stage.setTitle("JetSetGO - Home");
            stage.show();
        } catch (IOException e) {
            showError("Could not load home page");
            e.printStackTrace();
        }
    }
    
    private void navigateToAdminDashboard(ActionEvent event) {
        try {
            Parent adminRoot = FXMLLoader.load(getClass().getResource("/resources/AdminDashboard.fxml"));
            Scene adminScene = new Scene(adminRoot);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(adminScene);
            stage.show();
        } catch (IOException e) {
            showError("Could not load admin dashboard");
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}