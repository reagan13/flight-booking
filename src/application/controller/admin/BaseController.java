package application.controller.admin;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public abstract class BaseController {
    // ENCAPSULATION - protected methods for common functionality
    protected void showAlert(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }
    
    protected void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    protected void showErrorAlert(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }
    
    protected void showWarningAlert(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }
    
    protected void showConfirmationAlert(String title, String message) {
        showAlert(AlertType.CONFIRMATION, title, message);
    }
    
    // ABSTRACTION - abstract methods for initialization
    public abstract void initialize();
    public abstract void cleanup();
}