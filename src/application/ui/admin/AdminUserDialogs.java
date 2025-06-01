package application.ui.admin;

import application.model.User;
import application.service.UserService;
import application.service.UserSession;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.util.function.BiConsumer;

public class AdminUserDialogs {
    
    // Functional interface for alert callbacks
    public interface AlertCallback extends BiConsumer<String, String> {}
    
    public static void addNewUser(AlertCallback alertCallback, Runnable refreshCallback) {
        try {
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

            // Set placeholders
            setUserFieldPlaceholders(firstNameField, lastNameField, emailField, 
                                   ageField, addressField, passwordField);

            // Create layout
            VBox content = createUserFormLayout(firstNameField, lastNameField, emailField,
                                              passwordField, ageField, addressField, userTypeBox);

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Handle dialog result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return createUserFromFields(firstNameField, lastNameField, emailField,
                                               ageField, addressField, userTypeBox, 
                                               passwordField, alertCallback);
                }
                return null;
            });

            // Show dialog and process result
            dialog.showAndWait().ifPresent(newUser -> {
                try {
                    if (UserService.addUser(newUser, passwordField.getText())) {
                        alertCallback.accept("Success", "User added successfully!");
                        refreshCallback.run();
                    } else {
                        alertCallback.accept("Error", "Failed to add user. Please check the database connection and try again.");
                    }
                } catch (Exception e) {
                    alertCallback.accept("Database Error", "An error occurred while adding the user: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("Error in addNewUser dialog: " + e.getMessage());
            alertCallback.accept("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    public static void editUser(User user, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Edit User");
            dialog.setHeaderText("Edit User Information");
            
            // Create form fields with existing values
            TextField firstNameField = new TextField(user.getFirstName());
            TextField lastNameField = new TextField(user.getLastName());
            TextField emailField = new TextField(user.getEmail());
            TextField ageField = new TextField(String.valueOf(user.getAge()));
            TextField addressField = new TextField(user.getAddress());
            ComboBox<String> userTypeBox = new ComboBox<>();
            userTypeBox.getItems().addAll("regular", "admin");
            userTypeBox.setValue(user.getUserType());
            
            // Set placeholders
            setUserFieldPlaceholders(firstNameField, lastNameField, emailField, 
                                   ageField, addressField, null);
            
            // Create layout (without password field for editing)
            VBox content = createUserFormLayout(firstNameField, lastNameField, emailField,
                                              null, ageField, addressField, userTypeBox);
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            // Handle dialog result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return updateUserFromFields(user, firstNameField, lastNameField, emailField,
                                               ageField, addressField, userTypeBox, alertCallback);
                }
                return null;
            });
            
            // Show dialog and process result
            dialog.showAndWait().ifPresent(updatedUser -> {
                try {
                    if (UserService.updateUser(updatedUser)) {
                        alertCallback.accept("Success", "User updated successfully!");
                        refreshCallback.run();
                    } else {
                        alertCallback.accept("Error", "Failed to update user. Please check the database connection and try again.");
                    }
                } catch (Exception e) {
                    alertCallback.accept("Database Error", "An error occurred while updating the user: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error in editUser dialog: " + e.getMessage());
            alertCallback.accept("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    public static void deleteUser(User user, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            // Prevent deleting current admin user
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getId() == user.getId()) {
                alertCallback.accept("Error", "You cannot delete your own account while logged in.");
                return;
            }
            
            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete User");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Do you want to delete user: " + user.getFullName() + "?\n\nThis action cannot be undone.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        if (UserService.deleteUser(user.getId())) {
                            alertCallback.accept("Success", "User deleted successfully!");
                            refreshCallback.run();
                        } else {
                            alertCallback.accept("Error", "Failed to delete user. The user may have associated records that prevent deletion.");
                        }
                    } catch (Exception e) {
                        alertCallback.accept("Database Error", "An error occurred while deleting the user: " + e.getMessage());
                    }
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error in deleteUser dialog: " + e.getMessage());
            alertCallback.accept("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    // PRIVATE HELPER METHODS
    private static void setUserFieldPlaceholders(TextField firstNameField, TextField lastNameField, 
                                                TextField emailField, TextField ageField, 
                                                TextField addressField, PasswordField passwordField) {
        firstNameField.setPromptText("Enter first name");
        lastNameField.setPromptText("Enter last name");
        emailField.setPromptText("Enter email address");
        ageField.setPromptText("Enter age (1-120)");
        addressField.setPromptText("Enter address");
        if (passwordField != null) {
            passwordField.setPromptText("Enter password (min 6 characters)");
        }
    }
    
    private static VBox createUserFormLayout(TextField firstNameField, TextField lastNameField,
                                           TextField emailField, PasswordField passwordField,
                                           TextField ageField, TextField addressField,
                                           ComboBox<String> userTypeBox) {
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("First Name:"), firstNameField,
            new Label("Last Name:"), lastNameField,
            new Label("Email:"), emailField
        );
        
        if (passwordField != null) {
            content.getChildren().addAll(new Label("Password:"), passwordField);
        }
        
        content.getChildren().addAll(
            new Label("Age:"), ageField,
            new Label("Address:"), addressField,
            new Label("User Type:"), userTypeBox
        );
        
        return content;
    }
    
    private static User createUserFromFields(TextField firstNameField, TextField lastNameField,
                                           TextField emailField, TextField ageField,
                                           TextField addressField, ComboBox<String> userTypeBox,
                                           PasswordField passwordField, AlertCallback alertCallback) {
        try {
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
                alertCallback.accept("Validation Error", userValidationError);
                return null;
            }

            // Validate password
            String passwordValidationError = UserService.validatePassword(passwordField.getText());
            if (passwordValidationError != null) {
                alertCallback.accept("Validation Error", passwordValidationError);
                return null;
            }

            return newUser;

        } catch (NumberFormatException e) {
            alertCallback.accept("Validation Error", "Please enter a valid number for age.");
            return null;
        } catch (Exception e) {
            alertCallback.accept("Error", "An error occurred while validating user data: " + e.getMessage());
            return null;
        }
    }
    
    private static User updateUserFromFields(User user, TextField firstNameField, TextField lastNameField,
                                           TextField emailField, TextField ageField,
                                           TextField addressField, ComboBox<String> userTypeBox,
                                           AlertCallback alertCallback) {
        try {
            user.setFirstName(firstNameField.getText());
            user.setLastName(lastNameField.getText());
            user.setEmail(emailField.getText());
            user.setAge(Integer.parseInt(ageField.getText()));
            user.setAddress(addressField.getText());
            user.setUserType(userTypeBox.getValue());
            
            // Validate user data (for update)
            String validationError = UserService.validateUserData(user, true);
            if (validationError != null) {
                alertCallback.accept("Validation Error", validationError);
                return null;
            }
            
            return user;
            
        } catch (NumberFormatException e) {
            alertCallback.accept("Validation Error", "Please enter a valid number for age.");
            return null;
        } catch (Exception e) {
            alertCallback.accept("Error", "An error occurred while validating user data: " + e.getMessage());
            return null;
        }
    }
}