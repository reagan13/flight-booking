package application.controller.client;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

public abstract class BaseController {
    
    public abstract void initialize();
    
    public abstract void cleanup();
    
    protected void showMobileAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setPrefSize(300, 180);
        dialogPane.setStyle("-fx-font-size: 13px; -fx-background-radius: 15;");
        
        alert.showAndWait();
    }
    
    protected void navigateToLogin(Stage currentStage) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/resources/Login.fxml"));
            javafx.scene.Parent loginRoot = loader.load();
            javafx.scene.Scene loginScene = new javafx.scene.Scene(loginRoot);
            currentStage.setScene(loginScene);
            currentStage.setTitle("JetSetGO - Login");
            currentStage.centerOnScreen();
            System.out.println("Successfully navigated to login screen");
        } catch (Exception e) {
            System.err.println("Error navigating to login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}