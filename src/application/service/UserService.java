package application.service;

import application.model.User;
import application.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class UserService {

    // Check if email already exists
    public static boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        
        return false;
    }
    
    // Check if email exists for different user (for updates)
    public static boolean emailExistsForOtherUser(String email, int userId) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ? AND id != ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email existence for other user: " + e.getMessage());
        }
        
        return false;
    }
    
    // Validate user data
    public static String validateUserData(User user, boolean isUpdate) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            return "First name is required.";
        }
        
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            return "Last name is required.";
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return "Email is required.";
        }
        
        // Email format validation
        if (!isValidEmail(user.getEmail())) {
            return "Please enter a valid email address.";
        }
        
        // Check email uniqueness
        if (isUpdate) {
            if (emailExistsForOtherUser(user.getEmail(), user.getId())) {
                return "Email address is already in use by another user.";
            }
        } else {
            if (emailExists(user.getEmail())) {
                return "Email address is already in use.";
            }
        }
        
        if (user.getAge() <= 0 || user.getAge() > 120) {
            return "Please enter a valid age (1-120).";
        }
        
        if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
            return "Address is required.";
        }
        
        if (user.getUserType() == null || user.getUserType().trim().isEmpty()) {
            return "User type is required.";
        }
        
        return null; // No validation errors
    }
    
    // Email format validation
    private static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    // Validate password
    public static String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password is required.";
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters long.";
        }

        return null; // No validation errors
    }
    

    
    public static ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String query = "SELECT id, first_name, last_name, email, age, address, user_type, created_at FROM users ORDER BY id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setAge(rs.getInt("age"));
                user.setAddress(rs.getString("address"));
                user.setUserType(rs.getString("user_type"));
                
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    user.setCreatedAt(timestamp.toLocalDateTime());
                }
                
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    public static boolean updateUser(User user) {
        String query = "UPDATE users SET first_name = ?, last_name = ?, email = ?, age = ?, address = ?, user_type = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getFirstName().trim());
            stmt.setString(2, user.getLastName().trim());
            stmt.setString(3, user.getEmail().trim().toLowerCase());
            stmt.setInt(4, user.getAge());
            stmt.setString(5, user.getAddress().trim());
            stmt.setString(6, user.getUserType());
            stmt.setInt(7, user.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean addUser(User user, String password) {
        String query = "INSERT INTO users (first_name, last_name, email, password, age, address, user_type) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getFirstName().trim());
            stmt.setString(2, user.getLastName().trim());
            stmt.setString(3, user.getEmail().trim().toLowerCase());
            stmt.setString(4, password); // In production, hash this password
            stmt.setInt(5, user.getAge());
            stmt.setString(6, user.getAddress().trim());
            stmt.setString(7, user.getUserType());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
}