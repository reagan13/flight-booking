package application;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {
    
    @FXML
    private StackPane contentArea;
    
    @FXML
    private VBox homeTab;
    
    @FXML
    private VBox messagesTab;
    
    @FXML
    private VBox bookingsTab;
    
    @FXML
    private VBox accountTab;
    
    // Track the active tab
    private VBox activeTab;
    
    @FXML
    public void initialize() {
        // Make sure all UI elements are properly injected before using them
        if (homeTab != null) {
            // Set home as default active tab
            activeTab = homeTab;
            highlightTab(homeTab);
            
            // Load home view by default
            try {
                switchToHome();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Error: homeTab is null. FXML elements not properly injected.");
        }
    }
    
    @FXML
    private void switchToHome() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/resources/Home.fxml"));
            setContent(view);
            updateActiveTab(homeTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void switchToMessages() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/resources/Messages.fxml"));
            setContent(view);
            updateActiveTab(messagesTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void switchToBookings() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/resources/Bookings.fxml"));
            setContent(view);
            updateActiveTab(bookingsTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void switchToAccount() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/resources/Account.fxml"));
            setContent(view);
            updateActiveTab(accountTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void setContent(Parent content) {
        if (contentArea != null) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        }
    }
    
    private void updateActiveTab(VBox tab) {
        if (tab == null) return;
        
        // Reset previous active tab style
        if (activeTab != null) {
            resetTabStyle(activeTab);
        }
        
        // Set new active tab
        activeTab = tab;
        highlightTab(tab);
    }
    
    private void highlightTab(VBox tab) {
        if (tab == null) return;
        
        // Apply active style
        tab.setStyle("-fx-background-color: #e6f2ff; -fx-background-radius: 10;");
    }
    
    private void resetTabStyle(VBox tab) {
        if (tab == null) return;
        
        // Reset to default style
        tab.setStyle("");
    }
}