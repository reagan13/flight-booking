package application.service;

import application.database.DatabaseConnection;
import application.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TransactionService {
    
    public static void initializeTransactionsTable() {
        // This method is now handled by DatabaseConnection.createTransactionsTable()
        System.out.println("TransactionService: Table initialization handled by DatabaseConnection");
    }
    
    public static ObservableList<Transaction> getAllTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        
        // Updated query to match your exact database schema
        String query =
            "SELECT t.id, t.booking_id, t.transaction_reference, t.payment_method, " +
            "t.payment_provider, t.amount, t.processing_fee, t.total_amount, " +
            "t.payment_status, t.payment_date, t.gateway_transaction_id, " +
            "t.gateway_response_code, t.created_at, t.updated_at, " +
            "COALESCE(u.first_name, 'Unknown') as user_first_name, " +
            "COALESCE(u.last_name, 'User') as user_last_name, " +
            "COALESCE(b.booking_reference, 'N/A') as booking_ref " +
            "FROM transactions t " +
            "LEFT JOIN bookings b ON t.booking_id = b.id " +
            "LEFT JOIN users u ON b.user_id = u.id " +
            "ORDER BY t.payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Transaction transaction = createTransactionFromResultSet(rs);
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    public static ObservableList<Transaction> getTransactionsByUser(int userId) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        
        String query =
            "SELECT t.id, t.booking_id, t.transaction_reference, t.payment_method, " +
            "t.payment_provider, t.amount, t.processing_fee, t.total_amount, " +
            "t.payment_status, t.payment_date, t.gateway_transaction_id, " +
            "t.gateway_response_code, t.created_at, t.updated_at, " +
            "COALESCE(u.first_name, 'Unknown') as user_first_name, " +
            "COALESCE(u.last_name, 'User') as user_last_name, " +
            "COALESCE(b.booking_reference, 'N/A') as booking_ref " +
            "FROM transactions t " +
            "LEFT JOIN bookings b ON t.booking_id = b.id " +
            "LEFT JOIN users u ON b.user_id = u.id " +
            "WHERE b.user_id = ? " +
            "ORDER BY t.payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = createTransactionFromResultSet(rs);
                    transactions.add(transaction);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving user transactions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    public static ObservableList<Transaction> getTransactionsByBooking(int bookingId) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        
        String query =
            "SELECT t.id, t.booking_id, t.transaction_reference, t.payment_method, " +
            "t.payment_provider, t.amount, t.processing_fee, t.total_amount, " +
            "t.payment_status, t.payment_date, t.gateway_transaction_id, " +
            "t.gateway_response_code, t.created_at, t.updated_at, " +
            "COALESCE(u.first_name, 'Unknown') as user_first_name, " +
            "COALESCE(u.last_name, 'User') as user_last_name, " +
            "COALESCE(b.booking_reference, 'N/A') as booking_ref " +
            "FROM transactions t " +
            "LEFT JOIN bookings b ON t.booking_id = b.id " +
            "LEFT JOIN users u ON b.user_id = u.id " +
            "WHERE t.booking_id = ? " +
            "ORDER BY t.payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, bookingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = createTransactionFromResultSet(rs);
                    transactions.add(transaction);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving booking transactions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    public static boolean updateTransactionStatus(int transactionId, String newStatus) {
        String updateSQL = "UPDATE transactions SET payment_status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, transactionId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating transaction status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static Map<String, Object> getTransactionStats() {
        Map<String, Object> stats = new HashMap<>();
        
        String query = 
            "SELECT " +
            "COUNT(*) as total_transactions, " +
            "COALESCE(SUM(CASE WHEN payment_status = 'completed' THEN total_amount ELSE 0 END), 0) as total_revenue, " +
            "COALESCE(SUM(CASE WHEN payment_status = 'completed' THEN processing_fee ELSE 0 END), 0) as total_fees, " +
            "COUNT(CASE WHEN payment_status = 'pending' THEN 1 END) as pending_transactions, " +
            "COUNT(CASE WHEN payment_status = 'failed' THEN 1 END) as failed_transactions, " +
            "COUNT(CASE WHEN payment_status = 'completed' THEN 1 END) as completed_transactions, " +
            "COUNT(CASE WHEN DATE(payment_date) = CURDATE() THEN 1 END) as today_transactions, " +
            "AVG(CASE WHEN payment_status = 'completed' THEN total_amount ELSE NULL END) as avg_transaction_amount " +
            "FROM transactions";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                stats.put("totalTransactions", rs.getInt("total_transactions"));
                stats.put("totalRevenue", rs.getDouble("total_revenue"));
                stats.put("totalFees", rs.getDouble("total_fees"));
                stats.put("pendingTransactions", rs.getInt("pending_transactions"));
                stats.put("failedTransactions", rs.getInt("failed_transactions"));
                stats.put("completedTransactions", rs.getInt("completed_transactions"));
                stats.put("todayTransactions", rs.getInt("today_transactions"));
                stats.put("avgTransactionAmount", rs.getDouble("avg_transaction_amount"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving transaction stats: " + e.getMessage());
            e.printStackTrace();
            
            // Return default stats
            stats.put("totalTransactions", 0);
            stats.put("totalRevenue", 0.0);
            stats.put("totalFees", 0.0);
            stats.put("pendingTransactions", 0);
            stats.put("failedTransactions", 0);
            stats.put("completedTransactions", 0);
            stats.put("todayTransactions", 0);
            stats.put("avgTransactionAmount", 0.0);
        }
        
        return stats;
    }
    
    public static Transaction getTransactionById(int transactionId) {
        String query =
            "SELECT t.id, t.booking_id, t.transaction_reference, t.payment_method, " +
            "t.payment_provider, t.amount, t.processing_fee, t.total_amount, " +
            "t.payment_status, t.payment_date, t.gateway_transaction_id, " +
            "t.gateway_response_code, t.created_at, t.updated_at, " +
            "COALESCE(u.first_name, 'Unknown') as user_first_name, " +
            "COALESCE(u.last_name, 'User') as user_last_name, " +
            "COALESCE(b.booking_reference, 'N/A') as booking_ref " +
            "FROM transactions t " +
            "LEFT JOIN bookings b ON t.booking_id = b.id " +
            "LEFT JOIN users u ON b.user_id = u.id " +
            "WHERE t.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, transactionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createTransactionFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving transaction: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static Transaction createTransactionFromResultSet(ResultSet rs) throws SQLException {
        // Map ALL fields from your exact MySQL schema to Transaction model
        int transactionId = rs.getInt("id");
        int bookingId = rs.getInt("booking_id");
        String transactionRef = rs.getString("transaction_reference");
        String paymentMethod = rs.getString("payment_method");
        String paymentProvider = rs.getString("payment_provider");
        double amount = rs.getDouble("amount");
        double processingFee = rs.getDouble("processing_fee");
        double totalAmount = rs.getDouble("total_amount");
        String paymentStatus = rs.getString("payment_status");
        Timestamp paymentDate = rs.getTimestamp("payment_date");
        String gatewayTransactionId = rs.getString("gateway_transaction_id");
        String gatewayResponseCode = rs.getString("gateway_response_code");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        // Create transaction with available data
        Transaction transaction = new Transaction(
                transactionId,
                0, // userId - not directly available in transactions table
                bookingId,
                "payment", // Default transaction type
                amount, // Base amount from database
                paymentStatus != null ? paymentStatus : "unknown",
                paymentMethod != null ? paymentMethod : "unknown",
                transactionRef != null ? transactionRef : "Transaction #" + transactionId,
                paymentDate != null ? paymentDate.toLocalDateTime() : LocalDateTime.now());

        // Set ALL additional fields from database
        transaction.setPaymentProvider(paymentProvider != null ? paymentProvider : "N/A");
        transaction.setProcessingFee(processingFee);
        transaction.setTotalAmount(totalAmount);
        transaction.setGatewayTransactionId(gatewayTransactionId != null ? gatewayTransactionId : "N/A");
        transaction.setGatewayResponseCode(gatewayResponseCode != null ? gatewayResponseCode : "N/A");

        if (createdAt != null)
            transaction.setCreatedAt(createdAt.toLocalDateTime());
        if (updatedAt != null)
            transaction.setUpdatedAt(updatedAt.toLocalDateTime());

        // Set user and booking info from JOINs
        String firstName = rs.getString("user_first_name");
        String lastName = rs.getString("user_last_name");
        if (firstName != null && lastName != null) {
            transaction.setUserName(firstName + " " + lastName);
        } else {
            transaction.setUserName("Unknown User");
        }

        String bookingRef = rs.getString("booking_ref");
        transaction.setBookingReference(bookingRef != null ? bookingRef : "N/A");

        return transaction;
    }

    
    // Search transactions by various criteria
    public static ObservableList<Transaction> searchTransactions(String searchTerm) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        
        String query =
            "SELECT t.id, t.booking_id, t.transaction_reference, t.payment_method, " +
            "t.payment_provider, t.amount, t.processing_fee, t.total_amount, " +
            "t.payment_status, t.payment_date, t.gateway_transaction_id, " +
            "t.gateway_response_code, t.created_at, t.updated_at, " +
            "COALESCE(u.first_name, 'Unknown') as user_first_name, " +
            "COALESCE(u.last_name, 'User') as user_last_name, " +
            "COALESCE(b.booking_reference, 'N/A') as booking_ref " +
            "FROM transactions t " +
            "LEFT JOIN bookings b ON t.booking_id = b.id " +
            "LEFT JOIN users u ON b.user_id = u.id " +
            "WHERE t.transaction_reference LIKE ? " +
            "OR t.payment_method LIKE ? " +
            "OR t.payment_status LIKE ? " +
            "OR t.payment_provider LIKE ? " +
            "OR t.gateway_transaction_id LIKE ? " +
            "OR u.first_name LIKE ? " +
            "OR u.last_name LIKE ? " +
            "OR b.booking_reference LIKE ? " +
            "ORDER BY t.payment_date DESC";
        
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Set search pattern for all search fields
            for (int i = 1; i <= 8; i++) {
                pstmt.setString(i, searchPattern);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = createTransactionFromResultSet(rs);
                    transactions.add(transaction);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching transactions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
}