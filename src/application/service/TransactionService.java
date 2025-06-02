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
    
    public static boolean updateTransactionStatus(int transactionId, String newPaymentStatus) {
        String updateTransactionSQL = "UPDATE transactions SET payment_status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        String updateBookingSQL = "UPDATE bookings SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Get booking ID for this transaction
                int bookingId = getBookingIdForTransaction(transactionId);
                
                // Update transaction status
                try (PreparedStatement pstmt1 = conn.prepareStatement(updateTransactionSQL)) {
                    pstmt1.setString(1, newPaymentStatus);
                    pstmt1.setInt(2, transactionId);
                    pstmt1.executeUpdate();
                }
                
                // Update corresponding booking status
                String bookingStatus = mapPaymentStatusToBookingStatus(newPaymentStatus);
                try (PreparedStatement pstmt2 = conn.prepareStatement(updateBookingSQL)) {
                    pstmt2.setString(1, bookingStatus);
                    pstmt2.setInt(2, bookingId);
                    pstmt2.executeUpdate();
                }
                
                // Commit both updates
                conn.commit();
                return true;
                
            } catch (SQLException e) {
                // Rollback if either update fails
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating transaction and booking status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static int getBookingIdForTransaction(int transactionId) throws SQLException {
        String query = "SELECT booking_id FROM transactions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, transactionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("booking_id");
                }
            }
        }
        
        throw new SQLException("Transaction not found: " + transactionId);
    }
    
    private static String mapPaymentStatusToBookingStatus(String paymentStatus) {
        switch (paymentStatus.toLowerCase()) {
            case "completed":
            case "success":
                return "confirmed";
            case "failed":
            case "cancelled":
                return "cancelled";
            case "pending":
                return "pending";
            case "refunded":
                return "cancelled"; // or "refunded" if you have that status
            default:
                return "pending";
        }
    }
   
    public static Map<String, Object> getTransactionStats() {
        Map<String, Object> stats = new HashMap<>();

        System.out.println("🔍 Getting transaction statistics...");

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Total transactions
            String totalQuery = "SELECT COUNT(*) as total FROM transactions";
            try (PreparedStatement stmt = conn.prepareStatement(totalQuery);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    stats.put("totalTransactions", total);
                    System.out.println("📊 Total Transactions: " + total);
                }
            }

            // Revenue from paid transactions
            String revenueQuery = "SELECT COALESCE(SUM(total_amount), 0.0) as revenue FROM transactions WHERE payment_status IN ('paid', 'completed')";
            try (PreparedStatement stmt = conn.prepareStatement(revenueQuery);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double revenue = rs.getDouble("revenue");
                    stats.put("totalRevenue", revenue);
                    System.out.println("💰 Total Revenue: ₱" + String.format("%.2f", revenue));
                }
            }

            // Processing fees
            String feesQuery = "SELECT COALESCE(SUM(processing_fee), 0.0) as fees FROM transactions WHERE payment_status IN ('paid', 'completed')";
            try (PreparedStatement stmt = conn.prepareStatement(feesQuery);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double fees = rs.getDouble("fees");
                    stats.put("totalFees", fees);
                    System.out.println("💳 Total Fees: ₱" + String.format("%.2f", fees));
                }
            }

            // Status counts
            String[] statuses = { "pending", "paid", "completed", "failed", "cancelled", "refunded" };
            for (String status : statuses) {
                String statusQuery = "SELECT COUNT(*) as count FROM transactions WHERE payment_status = ?";
                try (PreparedStatement stmt = conn.prepareStatement(statusQuery)) {
                    stmt.setString(1, status);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt("count");
                            stats.put(status + "Transactions", count);
                            System.out.println("📈 " + status + " Transactions: " + count);
                        }
                    }
                }
            }

            // Today's transactions
            String todayQuery = "SELECT COUNT(*) as count FROM transactions WHERE DATE(payment_date) = CURDATE()";
            try (PreparedStatement stmt = conn.prepareStatement(todayQuery);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    stats.put("todayTransactions", count);
                    System.out.println("📅 Today's Transactions: " + count);
                }
            }

            // Average transaction amount
            String avgQuery = "SELECT COALESCE(AVG(total_amount), 0.0) as avg FROM transactions WHERE payment_status IN ('paid', 'completed')";
            try (PreparedStatement stmt = conn.prepareStatement(avgQuery);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg");
                    stats.put("avgTransactionAmount", avg);
                    System.out.println("📊 Average Transaction: ₱" + String.format("%.2f", avg));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting transaction statistics: " + e.getMessage());
            e.printStackTrace();

            // Default values if database error
            stats.put("totalTransactions", 0);
            stats.put("totalRevenue", 0.0);
            stats.put("totalFees", 0.0);
            stats.put("pendingTransactions", 0);
            stats.put("paidTransactions", 0);
            stats.put("completedTransactions", 0);
            stats.put("failedTransactions", 0);
            stats.put("cancelledTransactions", 0);
            stats.put("refundedTransactions", 0);
            stats.put("todayTransactions", 0);
            stats.put("avgTransactionAmount", 0.0);
        }

        System.out.println("✅ Transaction statistics loaded: " + stats.size() + " metrics");
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

        String query = "SELECT t.id, t.booking_id, t.transaction_reference, t.payment_method, " +
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
                "OR u.first_name LIKE ? " +
                "OR u.last_name LIKE ? " +
                "OR b.booking_reference LIKE ? " +
                "ORDER BY t.payment_date DESC";

        String searchPattern = "%" + searchTerm + "%";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set search pattern for all search fields (REDUCED from 8 to 7 fields)
            for (int i = 1; i <= 7; i++) {
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