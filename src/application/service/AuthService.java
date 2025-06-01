package application.service;

import application.database.DatabaseConnection;
import application.model.User;
import java.sql.*;

public class AuthService {
    
    public static class LoginResult {
        private boolean success;
        private String message;
        private User user;
        
        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }
    
    public static class RegistrationResult {
        private boolean success;
        private String message;
        
        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    // Login method

    public static LoginResult login(String email, String password) {
        System.out.println("=== LOGIN DEBUG ===");
        System.out.println("Attempting login for email: " + email);
        System.out.println("Input password: " + password);

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT id, first_name, last_name, email, password, user_type FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✓ User found in database");
                System.out.println("User ID: " + rs.getInt("id"));
                System.out.println("User Type: " + rs.getString("user_type"));
                System.out.println("First Name: " + rs.getString("first_name"));

                String storedPassword = rs.getString("password");

                System.out.println("Stored password: " + storedPassword);
                System.out.println("Input password: " + password);
                System.out.println("Passwords match: " + storedPassword.equals(password));

                // Direct password comparison (no hashing)
                if (storedPassword.equals(password)) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("user_type"));

                    System.out.println("✓ Login successful for: " + user.getEmail());
                    System.out.println("✓ User type: " + user.getUserType());
                    return new LoginResult(true, "Login successful", user);
                } else {
                    System.out.println("✗ Password mismatch");
                    return new LoginResult(false, "Invalid email or password", null);
                }
            } else {
                System.out.println("✗ No user found with email: " + email);
                return new LoginResult(false, "Invalid email or password", null);
            }

        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            e.printStackTrace();
            return new LoginResult(false, "Database error occurred", null);
        }
    }

    public static RegistrationResult register(String firstName, String lastName, String email, String phoneNumber,
            String password) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // Check if email already exists
            String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next() && checkRs.getInt(1) > 0) {
                return new RegistrationResult(false, "Email already exists");
            }

            // Store user data WITH address field - SET ALL 8 PARAMETERS
            String insertSql = "INSERT INTO users (first_name, last_name, email, phone_number, password, age, address, user_type) VALUES (?, ?, ?, ?, ?, ?, ?, 'regular')";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, firstName); 
            insertStmt.setString(2, lastName); 
            insertStmt.setString(3, email); 
            insertStmt.setString(4, phoneNumber); 
            insertStmt.setString(5, password); 
            insertStmt.setInt(6, 0); 
            insertStmt.setString(7, ""); 

            int result = insertStmt.executeUpdate();

            if (result > 0) {
                return new RegistrationResult(true, "Registration successful");
            } else {
                return new RegistrationResult(false, "Registration failed");
            }

        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            e.printStackTrace();
            return new RegistrationResult(false, "Database error occurred");
        }
    }

    // Delete user account
    public static boolean deleteUser(int userId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Delete user's notifications first
            String deleteNotificationsSql = "DELETE FROM notifications WHERE user_id = ?";
            PreparedStatement notifStmt = conn.prepareStatement(deleteNotificationsSql);
            notifStmt.setInt(1, userId);
            notifStmt.executeUpdate();
            
            // Delete user's messages
            String deleteMessagesSql = "DELETE FROM messages WHERE user_id = ?";
            PreparedStatement msgStmt = conn.prepareStatement(deleteMessagesSql);
            msgStmt.setInt(1, userId);
            msgStmt.executeUpdate();
            
            // Check for active bookings
            String checkBookingsSql = "SELECT COUNT(*) FROM bookings WHERE user_id = ? AND status IN ('pending', 'confirmed')";
            PreparedStatement checkStmt = conn.prepareStatement(checkBookingsSql);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                conn.rollback();
                return false; // User has active bookings
            }
            
            // Delete user
            String deleteUserSql = "DELETE FROM users WHERE id = ?";
            PreparedStatement userStmt = conn.prepareStatement(deleteUserSql);
            userStmt.setInt(1, userId);
            
            int result = userStmt.executeUpdate();
            
            if (result > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}