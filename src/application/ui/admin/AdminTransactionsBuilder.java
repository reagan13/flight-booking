package application.ui.admin;

import javafx.scene.control.*;
import javafx.collections.ObservableList;
import application.model.Transaction;
import application.service.TransactionService;

public class AdminTransactionsBuilder {
    
    private final TransactionsEventHandler eventHandler;
    
    public AdminTransactionsBuilder(TransactionsEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    public void setupTransactionsTable(TableView<Transaction> transactionsTable) {
        // Clear existing columns
        transactionsTable.getColumns().clear();
        
        
        // Transaction ID Column
        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTransactionId()).asObject());
        idCol.setPrefWidth(60);
        idCol.setMinWidth(50);
        
        // Transaction Reference Column  
        TableColumn<Transaction, String> refCol = new TableColumn<>("Reference");
        refCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        refCol.setPrefWidth(140);
        refCol.setMinWidth(120);
        
        // Booking ID Column
        TableColumn<Transaction, Integer> bookingIdCol = new TableColumn<>("Booking ID");
        bookingIdCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getBookingId()).asObject());
        bookingIdCol.setPrefWidth(80);
        bookingIdCol.setMinWidth(70);
        
        // Booking Reference Column
        TableColumn<Transaction, String> bookingRefCol = new TableColumn<>("Booking Ref");
        bookingRefCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBookingReference()));
        bookingRefCol.setPrefWidth(120);
        bookingRefCol.setMinWidth(100);
        
        // Customer Name Column
        TableColumn<Transaction, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserName()));
        customerCol.setPrefWidth(140);
        customerCol.setMinWidth(120);
        
        // Payment Method Column
        TableColumn<Transaction, String> methodCol = new TableColumn<>("Payment Method");
        methodCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPaymentMethod()));
        methodCol.setPrefWidth(120);
        methodCol.setMinWidth(100);
        
        // Payment Provider Column
        TableColumn<Transaction, String> providerCol = new TableColumn<>("Provider");
        providerCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPaymentProvider()));
        providerCol.setPrefWidth(100);
        providerCol.setMinWidth(80);
        
        // Amount Column (base amount)
        TableColumn<Transaction, String> baseAmountCol = new TableColumn<>("Base Amount");
        baseAmountCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedAmount()));
        baseAmountCol.setPrefWidth(110);
        baseAmountCol.setMinWidth(100);
        
        // Processing Fee Column
        TableColumn<Transaction, String> feeCol = new TableColumn<>("Processing Fee");
        feeCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedProcessingFee()));
        feeCol.setPrefWidth(110);
        feeCol.setMinWidth(100);
        
        // Total Amount Column
        TableColumn<Transaction, String> totalCol = new TableColumn<>("Total Amount");
        totalCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedTotalAmount()));
        totalCol.setPrefWidth(110);
        totalCol.setMinWidth(100);
        
        // Payment Status Column with color coding
        TableColumn<Transaction, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        statusCol.setPrefWidth(100);
        statusCol.setMinWidth(80);
        
        // Add custom cell factory for status colors
        statusCol.setCellFactory(column -> {
            return new TableCell<Transaction, String>() {
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(status.toUpperCase());
                        // Color code based on status
                        switch (status.toLowerCase()) {
                            case "completed":
                            case "paid":
                            case "success":
                                setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                                break;
                            case "pending":
                                setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");
                                break;
                            case "failed":
                            case "cancelled":
                                setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                                break;
                            default:
                                setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #495057;");
                        }
                    }
                }
            };
        });
        
        // Payment Date Column
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Payment Date");
        dateCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedDateTime()));
        dateCol.setPrefWidth(140);
        dateCol.setMinWidth(120);
        
        // Created At Column
        TableColumn<Transaction, String> createdCol = new TableColumn<>("Created");
        createdCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedCreatedAt()));
        createdCol.setPrefWidth(130);
        createdCol.setMinWidth(110);
        
        // Updated At Column
        TableColumn<Transaction, String> updatedCol = new TableColumn<>("Last Updated");
        updatedCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedUpdatedAt()));
        updatedCol.setPrefWidth(130);
        updatedCol.setMinWidth(110);
        
        // Add columns to table 
        transactionsTable.getColumns().addAll(
            idCol, refCol, bookingIdCol, bookingRefCol, customerCol,
            methodCol, providerCol, baseAmountCol, feeCol, totalCol,
            statusCol, dateCol, createdCol, updatedCol
        );
        
        // Set table properties
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transactionsTable.setRowFactory(tv -> {
            TableRow<Transaction> row = new TableRow<>();
            
            // Add hover effect
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: #f8f9fa;");
                }
            });
            
            row.setOnMouseExited(e -> {
                row.setStyle("");
            });
            
            // Add double-click handler for transaction details
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Transaction transaction = row.getItem();
                    eventHandler.onViewTransactionDetails(transaction);
                }
            });
            
            // Add context menu for actions
            ContextMenu contextMenu = new ContextMenu();
            
            MenuItem viewDetails = new MenuItem("View Full Details");
            viewDetails.setOnAction(e -> {
                if (!row.isEmpty()) {
                    eventHandler.onViewTransactionDetails(row.getItem());
                }
            });
            
            MenuItem changeStatus = new MenuItem("Change Status");
            changeStatus.setOnAction(e -> {
                if (!row.isEmpty()) {
                    Transaction transaction = row.getItem();
                    AdminTransactionDialogs.changeTransactionStatus(transaction, 
                        (title, message) -> eventHandler.onTransactionsError(message),
                        () -> loadTransactions(transactionsTable));
                }
            });
            
            contextMenu.getItems().addAll(viewDetails, changeStatus);
            row.setContextMenu(contextMenu);
            
            return row;
        });
        
        // Load data
        loadTransactions(transactionsTable);
    }
    
    public void setupTransactionSearch(TextField searchField, TableView<Transaction> table) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTransactions(table, newValue);
        });
        
        // Updated placeholder text 
        searchField.setPromptText("Search by reference, customer, status, method, provider...");
    }
    
    private void loadTransactions(TableView<Transaction> table) {
        try {
            ObservableList<Transaction> transactions = TransactionService.getAllTransactions();
            table.setItems(transactions);
            eventHandler.onTransactionsLoaded(transactions.size());
        } catch (Exception e) {
            eventHandler.onTransactionsError("Failed to load transactions: " + e.getMessage());
        }
    }
    
    private void filterTransactions(TableView<Transaction> table, String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            loadTransactions(table);
            return;
        }
        
        try {
            // Use the database search which searches across all fields
            ObservableList<Transaction> filteredTransactions = TransactionService.searchTransactions(searchText);
            table.setItems(filteredTransactions);
        } catch (Exception e) {
            eventHandler.onTransactionsError("Search failed: " + e.getMessage());
        }
    }
    
    public interface TransactionsEventHandler {
        void onViewTransactionDetails(Transaction transaction);
        void onTransactionsLoaded(int count);
        void onTransactionsError(String error);
    }
}