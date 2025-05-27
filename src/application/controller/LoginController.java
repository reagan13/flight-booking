// filepath: d:\work\jetsetgo\src\application\controller\LoginController.java
package application.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.database.DatabaseConnection;
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
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Hyperlink signupLink;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Simple validation
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        
        try {
            Connection conn = DatabaseConnection.getConnection();
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
                String userType = rs.getString("user_type");
                
                // Store user info in session
                UserSession.getInstance().setUser(userId, firstName + " " + lastName, email, userType);
                
                // Navigate to appropriate screen based on user type
                if ("admin".equals(userType)) {
                    navigateToAdminDashboard(event);
                } else {
                    navigateToMain(event);
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
    
    private void navigateToMain(ActionEvent event) {
        try {
            Parent mainRoot = FXMLLoader.load(getClass().getResource("/resources/Main.fxml"));
            Scene mainScene = new Scene(mainRoot);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.show();
        } catch (IOException e) {
            showError("Could not load main page");
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