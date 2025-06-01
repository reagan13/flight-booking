package application.controller.auth;

import java.io.IOException;

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

public class SignupController {
    
    @FXML
    private TextField firstNameField;    

    @FXML
    private TextField lastNameField;     

    @FXML
    private TextField phoneNumberField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private TextField ageField;
    
    @FXML
    private TextField addressField;
    
    @FXML
    private Button signupButton;
    
    @FXML
    private Hyperlink loginLink;
    
    @FXML
    private Label errorLabel;
    
    /**
     * Handle signup 
     */     
    @FXML
    public void handleSignup(ActionEvent event) {

        // Get data from separate fields
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() ||
                email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return;
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            showError("Please enter a valid phone number");
            return;
        }

       
            AuthService.RegistrationResult result = AuthService.register(firstName, lastName, email, phoneNumber, password);
            if (result.isSuccess()) {
                System.out.println("Registration successful!");
                switchToLogin(event);
            } else {
                showError(result.getMessage());
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
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
    
    /**
     * Validate phone number format
     * Basic validation ]
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Allow digits, possibly with hyphens, parentheses, spaces
        String phoneRegex = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\./0-9]*$";
        return phoneNumber.matches(phoneRegex) && phoneNumber.replaceAll("[^0-9]", "").length() >= 7;
    }
}