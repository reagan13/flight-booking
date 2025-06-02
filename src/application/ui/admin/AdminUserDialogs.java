package application.ui.admin;

import application.model.User;
import application.service.UserService;
import application.service.UserSession;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import java.util.function.BiConsumer;

public class AdminUserDialogs {
    
    // Functional interface for alert callbacks
    public interface AlertCallback extends BiConsumer<String, String> {}
    
    // Traditional Java styling constants
    private static final String DIALOG_STYLE = "-fx-background-color: #f0f0f0; -fx-border-color: #808080; -fx-border-width: 2;";
    private static final String FIELD_STYLE = "-fx-background-color: white; -fx-border-color: #808080; -fx-border-width: 1; -fx-padding: 8; -fx-font-size: 12px;";
    private static final String LABEL_STYLE = "-fx-text-fill: #2c2c2c; -fx-font-size: 12px; -fx-font-weight: bold;";
    private static final String BUTTON_STYLE = "-fx-background-color: linear-gradient(#f0f0f0, #d0d0d0); -fx-text-fill: #2c2c2c; -fx-padding: 8 16; -fx-border-color: #808080; -fx-border-width: 1; -fx-font-weight: bold; -fx-cursor: hand;";
    
    public static void addNewUser(AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Add New User");
            dialog.setHeaderText("Create New User Account");
            
            // Apply traditional styling to dialog
            styleDialog(dialog);

            // Create form fields
            TextField firstNameField = createStyledTextField("Enter first name");
            TextField lastNameField = createStyledTextField("Enter last name");
            TextField emailField = createStyledTextField("Enter email address");
            TextField ageField = createStyledTextField("Enter age (1-120)");
            TextField addressField = createStyledTextField("Enter address");
            PasswordField passwordField = createStyledPasswordField("Enter password (min 6 characters)");
            ComboBox<String> userTypeBox = createStyledComboBox();
            userTypeBox.getItems().addAll("regular", "admin");
            userTypeBox.setValue("regular");

            // Create traditional form layout
            VBox content = createTraditionalTwoColumnFormLayout(
                firstNameField, lastNameField, emailField,
                passwordField, ageField, addressField, userTypeBox,
                true // include password field
            );

            dialog.getDialogPane().setContent(content);
            
            // Style dialog buttons
            styleDialogButtons(dialog);

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
                        showTraditionalAlert("Success", "User added successfully!", Alert.AlertType.INFORMATION);
                        refreshCallback.run();
                    } else {
                        showTraditionalAlert("Error", "Failed to add user. Please check the database connection and try again.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showTraditionalAlert("Database Error", "An error occurred while adding the user: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

        } catch (Exception e) {
            System.err.println("Error in addNewUser dialog: " + e.getMessage());
            showTraditionalAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    public static void editUser(User user, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Edit User");
            dialog.setHeaderText("Edit User Information");
            
            // Apply traditional styling to dialog
            styleDialog(dialog);
            
            // Create form fields with existing values
            TextField firstNameField = createStyledTextField("Enter first name");
            firstNameField.setText(user.getFirstName());
            
            TextField lastNameField = createStyledTextField("Enter last name");
            lastNameField.setText(user.getLastName());
            
            TextField emailField = createStyledTextField("Enter email address");
            emailField.setText(user.getEmail());
            
            TextField ageField = createStyledTextField("Enter age (1-120)");
            ageField.setText(String.valueOf(user.getAge()));
            
            TextField addressField = createStyledTextField("Enter address");
            addressField.setText(user.getAddress());
            
            ComboBox<String> userTypeBox = createStyledComboBox();
            userTypeBox.getItems().addAll("regular", "admin");
            userTypeBox.setValue(user.getUserType());
            
            // Create traditional form layout (without password field for editing)
            VBox content = createTraditionalTwoColumnFormLayout(
                firstNameField, lastNameField, emailField,
                null, ageField, addressField, userTypeBox,
                false // no password field for editing
            );
            
            dialog.getDialogPane().setContent(content);
            
            // Style dialog buttons
            styleDialogButtons(dialog);
            
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
                        showTraditionalAlert("Success", "User updated successfully!", Alert.AlertType.INFORMATION);
                        refreshCallback.run();
                    } else {
                        showTraditionalAlert("Error", "Failed to update user. Please check the database connection and try again.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showTraditionalAlert("Database Error", "An error occurred while updating the user: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error in editUser dialog: " + e.getMessage());
            showTraditionalAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    public static void deleteUser(User user, AlertCallback alertCallback, Runnable refreshCallback) {
        try {
            // Prevent deleting current admin user
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getId() == user.getId()) {
                showTraditionalAlert("Error", "You cannot delete your own account while logged in.", Alert.AlertType.ERROR);
                return;
            }
            
            // Show traditional confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete User");
            alert.setHeaderText("Confirm User Deletion");
            alert.setContentText("Are you sure you want to delete user: " + user.getFullName() + "?\n\n" +
                                "This action cannot be undone and will permanently remove:\n" +
                                "• User account and profile\n" +
                                "• All associated data\n" +
                                "• Login credentials");
            
            // Style the confirmation dialog
            styleTraditionalAlert(alert);
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        if (UserService.deleteUser(user.getId())) {
                            showTraditionalAlert("Success", "User '" + user.getFullName() + "' has been deleted successfully!", Alert.AlertType.INFORMATION);
                            refreshCallback.run();
                        } else {
                            showTraditionalAlert("Error", "Failed to delete user. The user may have associated records that prevent deletion.", Alert.AlertType.ERROR);
                        }
                    } catch (Exception e) {
                        showTraditionalAlert("Database Error", "An error occurred while deleting the user: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error in deleteUser dialog: " + e.getMessage());
            showTraditionalAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // TRADITIONAL STYLING HELPER METHODS
    private static void styleDialog(Dialog<?> dialog) {
        // Style the dialog pane
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(DIALOG_STYLE + " -fx-padding: 15;");
        
        // Set preferred size for better 2-column layout
        dialogPane.setPrefWidth(600);
        dialogPane.setPrefHeight(350);
    }
    
    private static void styleDialogButtons(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Style the buttons
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        
        if (okButton != null) {
            okButton.setStyle(BUTTON_STYLE);
            okButton.setText("Save");
        }
        
        if (cancelButton != null) {
            cancelButton.setStyle(BUTTON_STYLE);
            cancelButton.setText("Cancel");
        }
    }
    
    private static TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle(FIELD_STYLE);
        field.setPrefWidth(200);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }
    
    private static PasswordField createStyledPasswordField(String promptText) {
        PasswordField field = new PasswordField();
        field.setPromptText(promptText);
        field.setStyle(FIELD_STYLE);
        field.setPrefWidth(200);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }
    
    private static ComboBox<String> createStyledComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setStyle(FIELD_STYLE);
        comboBox.setPrefWidth(200);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        return comboBox;
    }
    
    private static Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle(LABEL_STYLE);
        label.setPrefWidth(120);
        label.setMinWidth(120);
        return label;
    }
    
    private static VBox createTraditionalTwoColumnFormLayout(TextField firstNameField, TextField lastNameField,
                                                            TextField emailField, PasswordField passwordField,
                                                            TextField ageField, TextField addressField,
                                                            ComboBox<String> userTypeBox, boolean includePassword) {
        VBox mainContainer = new VBox(5);
        mainContainer.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #c0c0c0; -fx-border-width: 1;");
        
        // Form fields section with 2-column grid
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(10);
        formGrid.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #808080; -fx-border-width: 1;");
        
        // Setup column constraints for proper space utilization
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        col1.setPrefWidth(120);
        
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setMinWidth(200);
        
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setMinWidth(120);
        col3.setPrefWidth(120);
        
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setHgrow(Priority.ALWAYS);
        col4.setMinWidth(200);
        
        formGrid.getColumnConstraints().addAll(col1, col2, col3, col4);
        
        int row = 0;
        
        // Row 1: First Name and Last Name
        formGrid.add(createStyledLabel("First Name:"), 0, row);
        formGrid.add(firstNameField, 1, row);
        formGrid.add(createStyledLabel("Last Name:"), 2, row);
        formGrid.add(lastNameField, 3, row);
        row++;
        
        // Row 2: Email and Age
        formGrid.add(createStyledLabel("Email Address:"), 0, row);
        formGrid.add(emailField, 1, row);
        formGrid.add(createStyledLabel("Age:"), 2, row);
        formGrid.add(ageField, 3, row);
        row++;
        
        // Row 3: Password (if included) and User Type
        if (includePassword && passwordField != null) {
            formGrid.add(createStyledLabel("Password:"), 0, row);
            formGrid.add(passwordField, 1, row);
            formGrid.add(createStyledLabel("User Type:"), 2, row);
            formGrid.add(userTypeBox, 3, row);
            row++;
        } else {
            formGrid.add(createStyledLabel("User Type:"), 0, row);
            formGrid.add(userTypeBox, 1, row);
            row++;
        }
        
        // Row 4: Address (spans all columns)
        formGrid.add(createStyledLabel("Address:"), 0, row);
        GridPane.setColumnSpan(addressField, 3);
        formGrid.add(addressField, 1, row);
        row++;
        
        mainContainer.getChildren().add(formGrid);
        
        return mainContainer;
    }
    
    private static void showTraditionalAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        
        styleTraditionalAlert(alert);
        alert.showAndWait();
    }
    
    private static void styleTraditionalAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(DIALOG_STYLE + " -fx-padding: 20;");
        
        // Style alert buttons
        for (ButtonType buttonType : dialogPane.getButtonTypes()) {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            if (button != null) {
                button.setStyle(BUTTON_STYLE);
            }
        }
    }
    
    // VALIDATION AND DATA PROCESSING METHODS
    private static User createUserFromFields(TextField firstNameField, TextField lastNameField,
                                           TextField emailField, TextField ageField,
                                           TextField addressField, ComboBox<String> userTypeBox,
                                           PasswordField passwordField, AlertCallback alertCallback) {
        try {
            User newUser = new User();
            newUser.setFirstName(firstNameField.getText().trim());
            newUser.setLastName(lastNameField.getText().trim());
            newUser.setEmail(emailField.getText().trim());
            newUser.setAge(Integer.parseInt(ageField.getText().trim()));
            newUser.setAddress(addressField.getText().trim());
            newUser.setUserType(userTypeBox.getValue());

            // Validate user data
            String userValidationError = UserService.validateUserData(newUser, false);
            if (userValidationError != null) {
                showTraditionalAlert("Validation Error", userValidationError, Alert.AlertType.WARNING);
                return null;
            }

            // Validate password
            String passwordValidationError = UserService.validatePassword(passwordField.getText());
            if (passwordValidationError != null) {
                showTraditionalAlert("Validation Error", passwordValidationError, Alert.AlertType.WARNING);
                return null;
            }

            return newUser;

        } catch (NumberFormatException e) {
            showTraditionalAlert("Validation Error", "Please enter a valid number for age.", Alert.AlertType.WARNING);
            return null;
        } catch (Exception e) {
            showTraditionalAlert("Error", "An error occurred while validating user data: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }
    
    private static User updateUserFromFields(User user, TextField firstNameField, TextField lastNameField,
                                           TextField emailField, TextField ageField,
                                           TextField addressField, ComboBox<String> userTypeBox,
                                           AlertCallback alertCallback) {
        try {
            user.setFirstName(firstNameField.getText().trim());
            user.setLastName(lastNameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setAge(Integer.parseInt(ageField.getText().trim()));
            user.setAddress(addressField.getText().trim());
            user.setUserType(userTypeBox.getValue());
            
            // Validate user data (for update)
            String validationError = UserService.validateUserData(user, true);
            if (validationError != null) {
                showTraditionalAlert("Validation Error", validationError, Alert.AlertType.WARNING);
                return null;
            }
            
            return user;
            
        } catch (NumberFormatException e) {
            showTraditionalAlert("Validation Error", "Please enter a valid number for age.", Alert.AlertType.WARNING);
            return null;
        } catch (Exception e) {
            showTraditionalAlert("Error", "An error occurred while validating user data: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }
}