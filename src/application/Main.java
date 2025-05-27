package application;

import application.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
   // Update the window size in the start method:

   @Override
   public void start(Stage primaryStage) {
       try {
           // Load the main scene
           Parent root = FXMLLoader.load(getClass().getResource("/resources/home.fxml"));
           Scene scene = new Scene(root, 350, 700); // Mobile portrait size

           try {
               // Load CSS files
               String mainCssPath = "/resources/application.css";
               if (getClass().getResource(mainCssPath) != null) {
                   scene.getStylesheets().add(getClass().getResource(mainCssPath).toExternalForm());
               }
               
               String authCssPath = "/resources/auth.css";
               if (getClass().getResource(authCssPath) != null) {
                   scene.getStylesheets().add(getClass().getResource(authCssPath).toExternalForm());
               }
           } catch (Exception e) {
               System.err.println("Warning: Could not load CSS file: " + e.getMessage());
           }

           // Test database connection
           try {
               DatabaseConnection.getConnection();
               System.out.println("Database connection successful");
           } catch (Exception e) {
               System.err.println("Database connection failed: " + e.getMessage());
           }

           primaryStage.setTitle("JetSetGO Mobile");
           primaryStage.setScene(scene);
           primaryStage.setResizable(false); // Fixed mobile size
           primaryStage.centerOnScreen();
           primaryStage.show();

           System.out.println("Mobile app started successfully!");

       } catch (Exception e) {
           System.err.println("Error loading FXML: " + e.getMessage());
           e.printStackTrace();
       }
   }
   

    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() {
        // Clean up resources on application exit
        DatabaseConnection.closeConnection();
    }
}