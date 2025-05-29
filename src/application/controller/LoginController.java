package application.controller;

import java.io.IOException;

import application.model.User;
import application.service.UserSession;
import application.service.AuthService;
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
    private void handleLogin(ActionEvent event) {
        // Clear any previous error
        clearError();
        
        String email = emailPhoneField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        try {
            AuthService.LoginResult result = AuthService.login(email, password);

            if (result.isSuccess()) {
                User user = result.getUser();
                
                // This should now work!
                UserSession.getInstance().setCurrentUser(user);

                // Check user type and redirect accordingly
                if ("admin".equals(user.getUserType())) {
                    System.out.println("Redirecting to admin panel...");
                    navigateToAdminPanel();
                } else {
                    System.out.println("Redirecting to user home...");
                    navigateToHome();
                }
            } else {
                showError(result.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();  // This will help us see the full error
            showError("An error occurred during login. Please try again.");
        }
    }
    private void navigateToAdminPanel() {
        try {
            Stage stage = (Stage) emailPhoneField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/AdminPanel.fxml"));
            Parent adminRoot = loader.load();

            Scene adminScene = new Scene(adminRoot);
            stage.setScene(adminScene);
            stage.setTitle("JetSetGO - Admin Panel");
            // stage.setMaximized(true);
            stage.centerOnScreen();

            System.out.println("Successfully navigated to admin panel");

        } catch (Exception e) {
            System.err.println("Error navigating to admin panel: " + e.getMessage());
            e.printStackTrace();
            showError("Could not load admin panel");
        }
    }

    private void navigateToHome() {
        try {
            Stage stage = (Stage) emailPhoneField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Home.fxml"));
            Parent homeRoot = loader.load();

            Scene homeScene = new Scene(homeRoot);
            stage.setScene(homeScene);
            stage.setTitle("JetSetGO - Book Your Flight");
            stage.centerOnScreen();

            System.out.println("Successfully navigated to home");

        } catch (Exception e) {
            System.err.println("Error navigating to home: " + e.getMessage());
            e.printStackTrace();
            showError("Could not load home screen");
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
    
    // Helper methods for error handling
    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
    
    private void clearError() {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }
}