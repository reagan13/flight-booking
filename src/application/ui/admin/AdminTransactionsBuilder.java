package application.ui.admin;

import javafx.scene.control.*;
import javafx.collections.FXCollections;
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
        
        // Create columns
        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTransactionId()).asObject());
        idCol.setPrefWidth(60);
        
        TableColumn<Transaction, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserName()));
        userCol.setPrefWidth(120);
        
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTransactionType()));
        typeCol.setPrefWidth(100);
        
        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
        amountCol.setPrefWidth(100);
        
        TableColumn<Transaction, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        statusCol.setPrefWidth(80);
        
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedDateTime()));
        dateCol.setPrefWidth(120);
        
        TableColumn<Transaction, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPaymentMethod()));
        paymentCol.setPrefWidth(100);
        
        // Add columns to table
        transactionsTable.getColumns().addAll(idCol, userCol, typeCol, amountCol, statusCol, dateCol, paymentCol);
        
        // Load data
        loadTransactions(transactionsTable);
        
        // Add row click handler
        transactionsTable.setRowFactory(tv -> {
            TableRow<Transaction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Transaction transaction = row.getItem();
                    eventHandler.onViewTransactionDetails(transaction);
                }
            });
            return row;
        });
    }
    
    public void setupTransactionSearch(TextField searchField, TableView<Transaction> table) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTransactions(table, newValue);
        });
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
            ObservableList<Transaction> allTransactions = TransactionService.getAllTransactions();
            ObservableList<Transaction> filteredTransactions = FXCollections.observableArrayList();
            
            String lowerCaseFilter = searchText.toLowerCase();
            
            for (Transaction transaction : allTransactions) {
                if (transaction.getTransactionType().toLowerCase().contains(lowerCaseFilter) ||
                    transaction.getStatus().toLowerCase().contains(lowerCaseFilter) ||
                    transaction.getPaymentMethod().toLowerCase().contains(lowerCaseFilter) ||
                    transaction.getUserName().toLowerCase().contains(lowerCaseFilter) ||
                    transaction.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                    String.valueOf(transaction.getTransactionId()).contains(lowerCaseFilter)) {
                    filteredTransactions.add(transaction);
                }
            }
            
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