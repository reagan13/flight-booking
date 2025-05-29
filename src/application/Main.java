// filepath: d:\work\jetsetgo\src\application\Main.java
package application;

import application.service.UserSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Always start with login screen
            Parent root = FXMLLoader.load(getClass().getResource("/resources/Login.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/resources/auth.css").toExternalForm());
            
            primaryStage.setTitle("JetSetGO - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        // Clean up when application closes
        UserSession.getInstance().logout();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}