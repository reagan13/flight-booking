package application.service;

import application.database.DatabaseConnection;
import application.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.regex.Pattern;

public class UserService {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    public static ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        
        // First, check what columns exist in the users table
        String checkColumnsQuery = "DESCRIBE users";
        boolean hasPhoneNumber = false;
        boolean hasCreatedAt = false;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkColumnsQuery);
             ResultSet checkRs = checkStmt.executeQuery()) {
            
            while (checkRs.next()) {
                String columnName = checkRs.getString("Field");
                if ("phone_number".equals(columnName)) {
                    hasPhoneNumber = true;
                }
                if ("created_at".equals(columnName)) {
                    hasCreatedAt = true;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking table structure: " + e.getMessage());
        }
        
        // Build the query based on available columns
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT id, first_name, last_name, email, age, address, user_type");
        
        if (hasPhoneNumber) {
            queryBuilder.append(", phone_number");
        }
        if (hasCreatedAt) {
            queryBuilder.append(", created_at");
        }
        
        queryBuilder.append(" FROM users ORDER BY id");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString());
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
                
                // Set phone number if column exists, otherwise set default
                if (hasPhoneNumber) {
                    user.setPhoneNumber(rs.getString("phone_number"));
                } else {
                    user.setPhoneNumber("N/A"); // Default value
                }
                
                users.add(user);
            }
            
            System.out.println("Successfully loaded " + users.size() + " users");
            
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    public static User getUserById(int userId) {
        // Similar approach - check for columns first
        String checkColumnsQuery = "DESCRIBE users";
        boolean hasPhoneNumber = false;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkColumnsQuery);
             ResultSet checkRs = checkStmt.executeQuery()) {
            
            while (checkRs.next()) {
                String columnName = checkRs.getString("Field");
                if ("phone_number".equals(columnName)) {
                    hasPhoneNumber = true;
                    break;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking table structure: " + e.getMessage());
        }
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT id, first_name, last_name, email, age, address, user_type");
        
        if (hasPhoneNumber) {
            queryBuilder.append(", phone_number");
        }
        
        queryBuilder.append(" FROM users WHERE id = ?");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setAge(rs.getInt("age"));
                user.setAddress(rs.getString("address"));
                user.setUserType(rs.getString("user_type"));
                
                if (hasPhoneNumber) {
                    user.setPhoneNumber(rs.getString("phone_number"));
                } else {
                    user.setPhoneNumber("N/A");
                }
                
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public static boolean addUser(User user, String password) {
        // Check if phone_number column exists
        boolean hasPhoneNumber = checkColumnExists("phone_number");
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO users (first_name, last_name, email, password, age, address, user_type");
        
        if (hasPhoneNumber) {
            queryBuilder.append(", phone_number");
        }
        
        queryBuilder.append(") VALUES (?, ?, ?, ?, ?, ?, ?");
        
        if (hasPhoneNumber) {
            queryBuilder.append(", ?");
        }
        
        queryBuilder.append(")");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, password); // In production, this should be hashed
            stmt.setInt(5, user.getAge());
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getUserType());
            
            if (hasPhoneNumber) {
                stmt.setString(8, user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            }
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean updateUser(User user) {
        boolean hasPhoneNumber = checkColumnExists("phone_number");
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE users SET first_name = ?, last_name = ?, email = ?, age = ?, address = ?, user_type = ?");
        
        if (hasPhoneNumber) {
            queryBuilder.append(", phone_number = ?");
        }
        
        queryBuilder.append(" WHERE id = ?");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getAge());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getUserType());
            
            if (hasPhoneNumber) {
                stmt.setString(7, user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
                stmt.setInt(8, user.getId());
            } else {
                stmt.setInt(7, user.getId());
            }
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
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
            return false;
        }
    }
    
    // Helper method to check if a column exists
    private static boolean checkColumnExists(String columnName) {
        String query = "DESCRIBE users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                if (columnName.equals(rs.getString("Field"))) {
                    return true;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking column existence: " + e.getMessage());
        }
        return false;
    }
    
    // Authentication methods
    public static User authenticateUser(String email, String password) {
        boolean hasPhoneNumber = checkColumnExists("phone_number");
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT id, first_name, last_name, email, age, address, user_type");
        
        if (hasPhoneNumber) {
            queryBuilder.append(", phone_number");
        }
        
        queryBuilder.append(" FROM users WHERE email = ? AND password = ?");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            
            stmt.setString(1, email);
            stmt.setString(2, password); // In production, compare with hashed password
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setAge(rs.getInt("age"));
                user.setAddress(rs.getString("address"));
                user.setUserType(rs.getString("user_type"));
                
                if (hasPhoneNumber) {
                    user.setPhoneNumber(rs.getString("phone_number"));
                } else {
                    user.setPhoneNumber("N/A");
                }
                
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        
        return null;
    }
    
    public static boolean isEmailExists(String email) {
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
    
    // Validation methods
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
        
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return "Please enter a valid email address.";
        }
        
        // Check if email already exists (skip for updates of the same user)
        if (!isUpdate || !getCurrentUserEmail(user.getId()).equals(user.getEmail())) {
            if (isEmailExists(user.getEmail())) {
                return "Email already exists. Please use a different email.";
            }
        }
        
        if (user.getAge() < 1 || user.getAge() > 120) {
            return "Age must be between 1 and 120.";
        }
        
        if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
            return "Address is required.";
        }
        
        if (user.getUserType() == null || (!user.getUserType().equals("regular") && !user.getUserType().equals("admin"))) {
            return "User type must be either 'regular' or 'admin'.";
        }
        
        return null; // No validation errors
    }
    
    public static String validatePassword(String password) {
        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters long.";
        }
        return null; // No validation errors
    }
    
    private static String getCurrentUserEmail(int userId) {
        String query = "SELECT email FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("email");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting current user email: " + e.getMessage());
        }
        
        return "";
    }
    
    // Get user statistics
    public static int getTotalUsersCount() {
        String query = "SELECT COUNT(*) FROM users WHERE user_type = 'regular'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total users count: " + e.getMessage());
        }
        return 0;
    }
    
    public static int getAdminUsersCount() {
        String query = "SELECT COUNT(*) FROM users WHERE user_type = 'admin'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting admin users count: " + e.getMessage());
        }
        return 0;
    }
}