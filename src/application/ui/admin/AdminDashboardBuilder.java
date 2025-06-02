package application.ui.admin;

import application.service.AdminService;
import application.service.TransactionService;
import javafx.scene.control.Label;
import java.util.Map;

public class AdminDashboardBuilder {
    // ENCAPSULATION - private constructor for utility class
    private AdminDashboardBuilder() {}
    
    // Interface for handling dashboard events - POLYMORPHISM
    public interface DashboardEventHandler {
        void onStatsLoaded();
        void onStatsError(String error);
    }
    
    
    public static void loadDashboardStats(DashboardLabels labels, DashboardEventHandler handler) {
        System.out.println("🔄 Loading dashboard statistics...");

        try {
            // Load basic statistics from database
            System.out.println("📊 Getting basic statistics...");
            int totalUsers = AdminService.getTotalUsersCount();
            int totalFlights = AdminService.getTotalFlightsCount();
            int totalBookings = AdminService.getTotalBookingsCount();
            int pendingBookings = AdminService.getBookingCountByStatus("pending");
            int confirmedBookings = AdminService.getBookingCountByStatus("confirmed");
            int cancelledBookings = AdminService.getBookingCountByStatus("cancelled");

            // Load transaction statistics
            System.out.println("💳 Getting transaction statistics...");
            Map<String, Object> transactionStats = TransactionService.getTransactionStats();

            // Extract transaction data with null checks
            int totalTransactions = transactionStats.get("totalTransactions") != null
                    ? (Integer) transactionStats.get("totalTransactions")
                    : 0;
            double totalRevenue = transactionStats.get("totalRevenue") != null
                    ? (Double) transactionStats.get("totalRevenue")
                    : 0.0;
            double totalFees = transactionStats.get("totalFees") != null ? (Double) transactionStats.get("totalFees")
                    : 0.0;
            int pendingTransactions = transactionStats.get("pendingTransactions") != null
                    ? (Integer) transactionStats.get("pendingTransactions")
                    : 0;
            int failedTransactions = transactionStats.get("failedTransactions") != null
                    ? (Integer) transactionStats.get("failedTransactions")
                    : 0;
            Object completedObj = transactionStats.get("completedTransactions");
            Object paidObj = transactionStats.get("paidTransactions");
            int completedTransactions = 0;
            if (completedObj != null)
                completedTransactions += (Integer) completedObj;
            if (paidObj != null)
                completedTransactions += (Integer) paidObj;

            int todayTransactions = transactionStats.get("todayTransactions") != null
                    ? (Integer) transactionStats.get("todayTransactions")
                    : 0;
            double avgTransactionAmount = transactionStats.get("avgTransactionAmount") != null
                    ? (Double) transactionStats.get("avgTransactionAmount")
                    : 0.0;

            // Load additional metrics
            System.out.println("📨 Getting message statistics...");
            int totalMessages = AdminService.getTotalMessagesCount();
            int newMessages = AdminService.getUnreadMessagesCount();
            int todayBookings = AdminService.getTodayBookingsCount();

            // Update ALL labels with actual data
            System.out.println("🖥️ Updating dashboard labels...");
            labels.totalUsersLabel.setText(String.valueOf(totalUsers));
            labels.totalFlightsLabel.setText(String.valueOf(totalFlights));
            labels.totalBookingsLabel.setText(String.valueOf(totalBookings));
            labels.totalRevenueLabel.setText(String.format("₱%.2f", totalRevenue));

            // Update booking status labels
            labels.pendingBookingsLabel.setText(String.valueOf(pendingBookings));
            labels.confirmedBookingsLabel.setText(String.valueOf(confirmedBookings));
            labels.cancelledBookingsLabel.setText(String.valueOf(cancelledBookings));

            // Update message and daily statistics
            labels.totalMessagesLabel.setText(String.valueOf(totalMessages));
            labels.todayBookingsLabel.setText(String.valueOf(todayBookings));
            labels.newMessagesLabel.setText(String.valueOf(newMessages));

            // Update system status
            String systemStatus = AdminService.getSystemStatus();
            labels.systemStatusLabel.setText(systemStatus);

            // Set status color based on system health
            if (systemStatus.contains("Online")) {
                labels.systemStatusLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            } else {
                labels.systemStatusLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            }

            System.out.println("✅ Dashboard statistics loaded successfully!");
            System.out.println("📊 Summary: " + totalUsers + " users, " + totalBookings + " bookings, ₱"
                    + String.format("%.2f", totalRevenue) + " revenue");

            handler.onStatsLoaded();

        } catch (Exception e) {
            System.err.println("❌ Error loading dashboard stats: " + e.getMessage());
            e.printStackTrace();

            // Enhanced fallback values
            labels.totalUsersLabel.setText("0");
            labels.totalFlightsLabel.setText("0");
            labels.totalBookingsLabel.setText("0");
            labels.totalRevenueLabel.setText("₱0.00");

            labels.pendingBookingsLabel.setText("0");
            labels.confirmedBookingsLabel.setText("0");
            labels.cancelledBookingsLabel.setText("0");

            labels.totalMessagesLabel.setText("0");
            labels.todayBookingsLabel.setText("0");
            labels.newMessagesLabel.setText("0");
            labels.systemStatusLabel.setText("🔴 Error Loading Data");
            labels.systemStatusLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");

            handler.onStatsError(e.getMessage());
        }
    }

    // ENCAPSULATION - inner class with only the labels that exist in AdminController
    public static class DashboardLabels {
        public final Label totalUsersLabel;
        public final Label totalFlightsLabel;
        public final Label totalBookingsLabel;
        public final Label totalRevenueLabel;
        public final Label pendingBookingsLabel;
        public final Label confirmedBookingsLabel;
        public final Label cancelledBookingsLabel;
        public final Label totalMessagesLabel;
        public final Label todayBookingsLabel;
        public final Label newMessagesLabel;
        public final Label systemStatusLabel;
        
        // Constructor that matches what AdminController is calling
        public DashboardLabels(Label totalUsersLabel, Label totalFlightsLabel, 
                              Label totalBookingsLabel, Label totalRevenueLabel,
                              Label pendingBookingsLabel, Label confirmedBookingsLabel, 
                              Label cancelledBookingsLabel, Label totalMessagesLabel,
                              Label todayBookingsLabel, Label newMessagesLabel, 
                              Label systemStatusLabel) {
            this.totalUsersLabel = totalUsersLabel;
            this.totalFlightsLabel = totalFlightsLabel;
            this.totalBookingsLabel = totalBookingsLabel;
            this.totalRevenueLabel = totalRevenueLabel;
            this.pendingBookingsLabel = pendingBookingsLabel;
            this.confirmedBookingsLabel = confirmedBookingsLabel;
            this.cancelledBookingsLabel = cancelledBookingsLabel;
            this.totalMessagesLabel = totalMessagesLabel;
            this.todayBookingsLabel = todayBookingsLabel;
            this.newMessagesLabel = newMessagesLabel;
            this.systemStatusLabel = systemStatusLabel;
        }
        
        // Getters for the existing labels
        public Label getTotalUsersLabel() { return totalUsersLabel; }
        public Label getTotalFlightsLabel() { return totalFlightsLabel; }
        public Label getTotalBookingsLabel() { return totalBookingsLabel; }
        public Label getTotalRevenueLabel() { return totalRevenueLabel; }
        public Label getPendingBookingsLabel() { return pendingBookingsLabel; }
        public Label getConfirmedBookingsLabel() { return confirmedBookingsLabel; }
        public Label getCancelledBookingsLabel() { return cancelledBookingsLabel; }
        public Label getTotalMessagesLabel() { return totalMessagesLabel; }
        public Label getTodayBookingsLabel() { return todayBookingsLabel; }
        public Label getNewMessagesLabel() { return newMessagesLabel; }
        public Label getSystemStatusLabel() { return systemStatusLabel; }
    }
    
    // Additional utility method for getting dashboard summary
    public static String getDashboardSummaryText() {
        try {
            Map<String, Object> transactionStats = TransactionService.getTransactionStats();
            double totalRevenue = (Double) transactionStats.get("totalRevenue");
            int totalBookings = AdminService.getTotalBookingsCount();
            int totalUsers = AdminService.getTotalUsersCount();
            
            return String.format(
                "📊 Quick Summary: %d Users | %d Bookings | ₱%.2f Revenue | %s",
                totalUsers, totalBookings, totalRevenue, AdminService.getSystemStatus()
            );
        } catch (Exception e) {
            return "📊 Dashboard data temporarily unavailable";
        }
    }
    
    // Method to check if any critical metrics need attention
    public static String getSystemAlerts() {
        try {
            Map<String, Object> transactionStats = TransactionService.getTransactionStats();
            int failedTransactions = (Integer) transactionStats.get("failedTransactions");
            int pendingBookings = AdminService.getBookingCountByStatus("pending");
            int newMessages = AdminService.getUnreadMessagesCount();
            
            StringBuilder alerts = new StringBuilder();
            
            if (failedTransactions > 0) {
                alerts.append("⚠️ ").append(failedTransactions).append(" failed transactions need review\n");
            }
            
            if (pendingBookings > 10) {
                alerts.append("📋 ").append(pendingBookings).append(" bookings awaiting confirmation\n");
            }
            
            if (newMessages > 5) {
                alerts.append("💬 ").append(newMessages).append(" unread messages require attention\n");
            }
            
            if (alerts.length() == 0) {
                return "✅ All systems operating normally";
            }
            
            return alerts.toString().trim();
            
        } catch (Exception e) {
            return "⚠️ Unable to check system alerts";
        }
    }
    
    // Get transaction metrics as formatted strings (for future use)
    public static Map<String, String> getTransactionMetrics() {
        try {
            Map<String, Object> stats = TransactionService.getTransactionStats();
            Map<String, String> formatted = new java.util.HashMap<>();
            
            formatted.put("totalTransactions", String.valueOf(stats.get("totalTransactions")));
            formatted.put("completedTransactions", String.valueOf(stats.get("completedTransactions")));
            formatted.put("pendingTransactions", String.valueOf(stats.get("pendingTransactions")));
            formatted.put("failedTransactions", String.valueOf(stats.get("failedTransactions")));
            formatted.put("todayTransactions", String.valueOf(stats.get("todayTransactions")));
            formatted.put("avgTransactionAmount", String.format("₱%.2f", stats.get("avgTransactionAmount")));
            formatted.put("totalFees", String.format("₱%.2f", stats.get("totalFees")));
            
            return formatted;
        } catch (Exception e) {
            System.err.println("Error getting transaction metrics: " + e.getMessage());
            return new java.util.HashMap<>();
        }
    }
    
    // Get booking metrics as formatted strings  
    public static Map<String, String> getBookingMetrics() {
        try {
            Map<String, String> metrics = new java.util.HashMap<>();
            
            metrics.put("totalBookings", String.valueOf(AdminService.getTotalBookingsCount()));
            metrics.put("pendingBookings", String.valueOf(AdminService.getBookingCountByStatus("pending")));
            metrics.put("confirmedBookings", String.valueOf(AdminService.getBookingCountByStatus("confirmed")));
            metrics.put("cancelledBookings", String.valueOf(AdminService.getBookingCountByStatus("cancelled")));
            metrics.put("todayBookings", String.valueOf(AdminService.getTodayBookingsCount()));
            
            return metrics;
        } catch (Exception e) {
            System.err.println("Error getting booking metrics: " + e.getMessage());
            return new java.util.HashMap<>();
        }
    }
    
    // Get user metrics as formatted strings
    public static Map<String, String> getUserMetrics() {
        try {
            Map<String, String> metrics = new java.util.HashMap<>();
            
            metrics.put("totalUsers", String.valueOf(AdminService.getTotalUsersCount()));
            metrics.put("newUsersThisMonth", String.valueOf(AdminService.getNewUsersThisMonth()));
            
            return metrics;
        } catch (Exception e) {
            System.err.println("Error getting user metrics: " + e.getMessage());
            return new java.util.HashMap<>();
        }
    }
}