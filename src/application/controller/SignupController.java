package application.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.database.DatabaseConnection;
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

public class SignupController {
    
    @FXML
    private TextField firstNameField;
    
    @FXML
    private TextField lastNameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField ageField;
    
    @FXML
    private TextField addressField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button signupButton;
    
    @FXML
    private Hyperlink loginLink;
    
    @FXML
    private Label errorLabel;
    
    /**
     * Handle signup button click action
     */
    @FXML
    public void handleSignup(ActionEvent event) {
        // Get form data
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String ageText = ageField.getText().trim();
        String address = addressField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        
        // Form validation
        if (firstName.isEmpty()) {
            showError("Please enter your first name");
            return;
        }
        
        if (lastName.isEmpty()) {
            showError("Please enter your last name");
            return;
        }
        
        if (email.isEmpty()) {
            showError("Please enter your email");
            return;
        }
        
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }
        
        // Age validation
        int age;
        if (ageText.isEmpty()) {
            age = 0;  // Default age or you can show error
        } else {
            try {
                age = Integer.parseInt(ageText);
                if (age < 18) {
                    showError("You must be at least 18 years old to sign up");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid age");
                return;
            }
        }
        
        if (address.isEmpty()) {
            showError("Please enter your address");
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter a password");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }
        
        // Database operations
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Check if email already exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showError("Email is already registered");
                return;
            }
            
            // Insert new user - note the fields match our new table structure
            String insertQuery = "INSERT INTO users (first_name, last_name, email, password, age, address, user_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, firstName);
            insertStmt.setString(2, lastName);
            insertStmt.setString(3, email);
            insertStmt.setString(4, password); // In a real app, use password hashing
            insertStmt.setInt(5, age);
            insertStmt.setString(6, address);
            insertStmt.setString(7, "regular"); // Default user type for new signups
            
            int rowsAffected = insertStmt.executeUpdate();
            if (rowsAffected > 0) {
                // Registration successful, switch to login screen
                switchToLogin(event);
            } else {
                showError("Failed to create account. Please try again.");
            }
            
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Switch to login screen
     */
    @FXML
    public void switchToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/resources/Login.fxml"));
            Scene scene = new Scene(root);
            
            // Get current stage reference
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Apply existing stylesheets
            if (((Node) event.getSource()).getScene().getStylesheets() != null) {
                scene.getStylesheets().addAll(((Node) event.getSource()).getScene().getStylesheets());
            }
            
            // Set the new scene
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error loading login page");
            e.printStackTrace();
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}