package application.service;

import application.database.DatabaseConnection;
import application.model.User;
import application.model.Flight;

import java.sql.*;
import java.util.Random;

public class BookingService {
    
    public static class BookingResult {
        private boolean success;
        private String bookingReference;
        private String message;
        private int bookingId;
        
        public BookingResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public BookingResult(boolean success, String bookingReference, int bookingId, String message) {
            this.success = success;
            this.bookingReference = bookingReference;
            this.bookingId = bookingId;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getBookingReference() { return bookingReference; }
        public String getMessage() { return message; }
        public int getBookingId() { return bookingId; }
    }
    
    public static class TransactionResult {
        private boolean success;
        private String transactionReference;
        private String message;
        
        public TransactionResult(boolean success, String transactionReference, String message) {
            this.success = success;
            this.transactionReference = transactionReference;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getTransactionReference() { return transactionReference; }
        public String getMessage() { return message; }
    }
    
    /**
     * Update user information in database
     */
    public static boolean updateUserInformation(String firstName, String lastName, int age, String address,
            String email, String phone) {
        if (!UserSession.getInstance().isLoggedIn()) {
            return true; // Skip update for guest users
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            // Updated SQL to include phone_number
            String sql = "UPDATE users SET first_name = ?, last_name = ?, age = ?, address = ?, phone_number = ? WHERE email = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setInt(3, age);
            stmt.setString(4, address);
            stmt.setString(5, phone); 
            stmt.setString(6, email);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Update the session user object with ALL new data
                User currentUser = UserSession.getInstance().getCurrentUser();
                currentUser.setFirstName(firstName);
                currentUser.setLastName(lastName);
                currentUser.setAge(age);
                currentUser.setAddress(address);
                currentUser.setPhoneNumber(phone);

                System.out.println("✅ User information updated successfully!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating user information: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
    
    /**
     * Create a new booking
     */
    public static BookingResult createBooking(Flight flight, String passengerFirstName, String passengerLastName, 
                                            String email, String phone) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Generate unique booking reference
            String bookingReference = generateBookingReference();
            
            // Get user ID if logged in, otherwise null for guest booking
            Integer userId = UserSession.getInstance().isLoggedIn() ? 
                           UserSession.getInstance().getCurrentUser().getId() : null;
            
            // Generate a random seat number (you can improve this with actual seat selection)
            String seatNumber = generateSeatNumber();
            
            String sql = "INSERT INTO bookings (user_id, flight_id, booking_reference, seat_number, status) VALUES (?, ?, ?, ?, 'confirmed')";
            
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (userId != null) {
                stmt.setInt(1, userId);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, flight.getId());
            stmt.setString(3, bookingReference);
            stmt.setString(4, seatNumber);
            
            int rowsInserted = stmt.executeUpdate();
            
            if (rowsInserted > 0) {
                // Get the generated booking ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int bookingId = generatedKeys.getInt(1);
                    
                    // Update flight available seats
                    updateFlightSeats(flight.getId(), flight.getSeats() - 1);
                    
                    System.out.println("Booking created successfully! Reference: " + bookingReference);
                    return new BookingResult(true, bookingReference, bookingId, "Booking created successfully!");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
            return new BookingResult(false, "Failed to create booking: " + e.getMessage());
        }
        
        return new BookingResult(false, "Failed to create booking");
    }
    
    /**
     * Process payment and create transaction record
     */
    public static TransactionResult processPayment(int bookingId, double amount, String paymentMethod, 
                                                 String paymentProvider) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Generate transaction reference
            String transactionReference = generateTransactionReference();
            
            // Calculate processing fee 
            double processingFee = calculateProcessingFee(amount, paymentMethod);
            double totalAmount = amount + processingFee;
            
            // Simulate payment gateway processing
            boolean paymentSuccess = simulatePaymentGateway(paymentMethod, totalAmount);
            
            String paymentStatus = paymentSuccess ? "paid" : "failed";
            String gatewayTransactionId = paymentSuccess ? generateGatewayTransactionId() : null;
            String gatewayResponseCode = paymentSuccess ? "00" : "99"; // 00 = success, 99 = failed
            
            String sql = "INSERT INTO transactions (booking_id, transaction_reference, payment_method, " +
                        "payment_provider, amount, processing_fee, total_amount, payment_status, " +
                        "gateway_transaction_id, gateway_response_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookingId);
            stmt.setString(2, transactionReference);
            stmt.setString(3, paymentMethod);
            stmt.setString(4, paymentProvider);
            stmt.setDouble(5, amount);
            stmt.setDouble(6, processingFee);
            stmt.setDouble(7, totalAmount);
            stmt.setString(8, paymentStatus);
            stmt.setString(9, gatewayTransactionId);
            stmt.setString(10, gatewayResponseCode);
            
            int rowsInserted = stmt.executeUpdate();
            
            if (rowsInserted > 0) {
                if (paymentSuccess) {
                    System.out.println("Payment processed successfully! Transaction: " + transactionReference);
                    return new TransactionResult(true, transactionReference, "Payment processed successfully!");
                } else {
                    // Update booking status to failed
                    updateBookingStatus(bookingId, "payment_failed");
                    return new TransactionResult(false, transactionReference, "Payment failed. Please try again.");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error processing payment: " + e.getMessage());
            e.printStackTrace();
            return new TransactionResult(false, null, "Payment processing error: " + e.getMessage());
        }
        
        return new TransactionResult(false, null, "Payment processing failed");
    }
    
    /**
     * Complete booking process: update user info, create booking, process payment
     */
    public static BookingResult completeBooking(Flight flight, String firstName, String lastName, 
    int age, String address, String email, String phone,
            double amount, String paymentMethod, String paymentProvider) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // Generate booking reference
            String bookingReference = generateBookingReference();

            // Insert booking with "pending" status instead of "confirmed"
            String bookingSql = "INSERT INTO bookings (user_id, flight_id, booking_reference, seat_number, status) VALUES (?, ?, ?, ?, 'pending')";
            PreparedStatement bookingStmt = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);

            if (UserSession.getInstance().isLoggedIn()) {
                bookingStmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            } else {
                bookingStmt.setNull(1, Types.INTEGER);
            }
            bookingStmt.setInt(2, flight.getId());
            bookingStmt.setString(3, bookingReference);
            bookingStmt.setString(4, generateSeatNumber()); 

            int bookingResult = bookingStmt.executeUpdate();

            if (bookingResult > 0) {
                // Get booking ID
                ResultSet rs = bookingStmt.getGeneratedKeys();
                int bookingId = 0;
                if (rs.next()) {
                    bookingId = rs.getInt(1);
                }

                // Calculate fees
                double processingFee = calculateProcessingFee(amount, paymentMethod);
                double totalAmount = amount + processingFee;

                // Insert transaction with "pending" payment status
                String transactionSql = "INSERT INTO transactions (booking_id, transaction_reference, payment_method, payment_provider, amount, processing_fee, total_amount, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, 'pending')";
                PreparedStatement transactionStmt = conn.prepareStatement(transactionSql);

                transactionStmt.setInt(1, bookingId);
                transactionStmt.setString(2, generateTransactionReference(paymentMethod));
                transactionStmt.setString(3, paymentMethod);
                transactionStmt.setString(4, paymentProvider);
                transactionStmt.setDouble(5, amount);
                transactionStmt.setDouble(6, processingFee);
                transactionStmt.setDouble(7, totalAmount);

                int transactionResult = transactionStmt.executeUpdate();

                if (transactionResult > 0) {
                    // Create notification for pending booking
                    if (UserSession.getInstance().isLoggedIn()) {
                        NotificationService.createPendingBookingNotification(
                                UserSession.getInstance().getCurrentUser().getId(),
                                bookingReference,
                                bookingId);
                    }

                    // Update available seats
                    updateAvailableSeats(flight.getId(), -1);

                    return new BookingResult(true, bookingReference, bookingId, "Booking submitted successfully! Awaiting admin approval.");
                }
            }

            return new BookingResult(false, "Failed to process booking");

        } catch (SQLException e) {
            System.err.println("Error completing booking: " + e.getMessage());
            e.printStackTrace();
            return new BookingResult(false, "Database error: " + e.getMessage());
        }
    }

    private static double calculateProcessingFee(double amount, String paymentMethod) {
        switch (paymentMethod) {
            case "credit_card":
                return amount * 0.025; // 2.5%
            case "gcash":
            case "maya":
                return amount * 0.01; // 1%
            case "paypal":
                return amount * 0.034; // 3.4%
            case "bank_transfer":
                return 0.0; // No fee
            default:
                return amount * 0.02; // 2%
        }
    }
    
    private static String generateSeatNumber() {
        // Simple seat generation
        String[] seatLetters = {"A", "B", "C", "D", "E", "F"};
        int seatRow = (int) (Math.random() * 30) + 1; // Rows 1-30
        String seatLetter = seatLetters[(int) (Math.random() * seatLetters.length)];
        return seatRow + seatLetter;
    }
    
    private static String generateTransactionReference(String paymentMethod) {
        String prefix;
        switch (paymentMethod) {
            case "gcash": prefix = "GC"; break;
            case "maya": prefix = "MY"; break;
            case "paypal": prefix = "PP"; break;
            case "bank_transfer": prefix = "BT"; break;
            default: prefix = "CC"; break;
        }
        return prefix + "-" + System.currentTimeMillis() % 1000000;
    }
    
    private static void updateAvailableSeats(int flightId, int change) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE flights SET available_seats = available_seats + ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, change);
            stmt.setInt(2, flightId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating available seats: " + e.getMessage());
        }
    }

    // Helper methods
    private static String generateBookingReference() {
        return "JG" + System.currentTimeMillis() % 100000000L;
    }
    
    private static String generateTransactionReference() {
        return "TXN" + System.currentTimeMillis() % 1000000000L;
    }
    
   
    
    private static String generateGatewayTransactionId() {
        return "GTW" + System.currentTimeMillis() + new Random().nextInt(1000);
    }
    
    private static boolean simulatePaymentGateway(String paymentMethod, double amount) {
        Random random = new Random();
        return random.nextDouble() < 0.9;
    }
    
    private static void updateFlightSeats(int flightId, int newSeatCount) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE flights SET available_seats = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, newSeatCount);
        stmt.setInt(2, flightId);
        stmt.executeUpdate();
    }
    
    private static void updateBookingStatus(int bookingId, String status) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, status);
        stmt.setInt(2, bookingId);
        stmt.executeUpdate();
    }
}