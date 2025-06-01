package application.ui.admin;

import application.model.User;
import application.service.UserService;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class AdminUsersBuilder {
    // Interface for handling user events - POLYMORPHISM
    public interface UsersEventHandler {
        void onUserEdit(User user);
        void onUserDelete(User user);
        void onUserAdd();
        void onUsersLoaded(int count);
        void onUsersError(String error);
    }
    
    private final UsersEventHandler eventHandler;
    
    // ENCAPSULATION - private constructor requiring event handler
    public AdminUsersBuilder(UsersEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    public void setupUsersTable(TableView<User> usersTable) {
        try {
            usersTable.getItems().clear();
            usersTable.getColumns().clear();
            
            // Create columns using inheritance pattern
            createUserColumns(usersTable);
            
            // Load data
            ObservableList<User> users = UserService.getAllUsers();
            usersTable.setItems(users);
            
            eventHandler.onUsersLoaded(users.size());
            
        } catch (Exception e) {
            System.err.println("Error setting up users table: " + e.getMessage());
            eventHandler.onUsersError(e.getMessage());
        }
    }
    
    // ENCAPSULATION - private method for table setup
    private void createUserColumns(TableView<User> usersTable) {
        // Basic columns
        TableColumn<User, Integer> idCol = createTableColumn("ID", "id", 50);
        TableColumn<User, String> firstNameCol = createTableColumn("First Name", "firstName", 100);
        TableColumn<User, String> lastNameCol = createTableColumn("Last Name", "lastName", 100);
        TableColumn<User, String> emailCol = createTableColumn("Email", "email", 200);
        TableColumn<User, Integer> ageCol = createTableColumn("Age", "age", 60);
        TableColumn<User, String> addressCol = createTableColumn("Address", "address", 150);
        TableColumn<User, String> typeCol = createTableColumn("Type", "userType", 80);
        
        // Actions column
        TableColumn<User, Void> actionsCol = createActionsColumn();
        
        usersTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, emailCol, 
                                      ageCol, addressCol, typeCol, actionsCol);
    }
    
    // POLYMORPHISM - generic method for creating columns
    private <T> TableColumn<User, T> createTableColumn(String title, String property, double width) {
        TableColumn<User, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }
    
    private TableColumn<User, Void> createActionsColumn() {
        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        
        actionsCol.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editBtn = createActionButton("Edit");
            private final Button deleteBtn = createActionButton("Delete");
            private final HBox buttonsBox = new HBox(5);
            
            {
                buttonsBox.getChildren().addAll(editBtn, deleteBtn);
                buttonsBox.setPadding(new Insets(5));
                
                editBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    eventHandler.onUserEdit(user);
                });
                
                deleteBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    eventHandler.onUserDelete(user);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });
        
        return actionsCol;
    }
    
    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 12px; -fx-padding: 5 10;");
        return button;
    }
}