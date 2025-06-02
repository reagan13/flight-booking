package application.service;

import application.model.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;
    
    // Private constructor for singleton pattern
    private UserSession() {
        System.out.println("UserSession created");
    }
    
    // Get singleton instance
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
            System.out.println("New UserSession instance created");
        }
        return instance;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("✓ Current user set: " + (user != null ? user.getEmail() + " (" + user.getUserType() + ")" : "null"));
    }
    
    // Get current user
    public User getCurrentUser() {
        return currentUser;
    }
    public void logout() {
        clearSession();
    }
    
    // Check if user is logged in
    public boolean isLoggedIn() {
        boolean loggedIn = currentUser != null;
        return loggedIn;
    }
    
    // Check if current user is admin
    public boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getUserType());
    }
    
    // Check if current user is regular user
    public boolean isRegularUser() {
        return currentUser != null && "regular".equals(currentUser.getUserType());
    }
    
    // Get current user's ID
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    
    // Get current user's email
    public String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }
    
    // Get current user's full name
    public String getCurrentUserFullName() {
        if (currentUser != null) {
            return currentUser.getFirstName() + " " + currentUser.getLastName();
        }
        return null;
    }
    
    // Get user type
    public String getCurrentUserType() {
        return currentUser != null ? currentUser.getUserType() : null;
    }
    
    // Clear session (logout)
    public void clearSession() {
        System.out.println("Logging out user: " + (currentUser != null ? currentUser.getEmail() : "none"));
        this.currentUser = null;
    }
    
    // Update user information in session
    public void updateCurrentUser(User updatedUser) {
        if (this.currentUser != null && updatedUser.getId() == this.currentUser.getId()) {
            this.currentUser = updatedUser;
            System.out.println("Current user updated: " + updatedUser.getEmail());
        }
    }
    
    // Check specific permissions
    public boolean hasPermission(String permission) {
        if (currentUser == null) return false;
        
        // Admin has all permissions
        if ("admin".equals(currentUser.getUserType())) {
            return true;
        }
        
        switch (permission) {
            case "book_flight":
            case "view_bookings":
            case "cancel_booking":
                return "regular".equals(currentUser.getUserType());
            case "manage_flights":
            case "manage_users":
            case "view_reports":
                return "admin".equals(currentUser.getUserType());
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return "UserSession{" +
                "currentUser=" + (currentUser != null ? currentUser.getEmail() : "null") +
                ", isLoggedIn=" + isLoggedIn() +
                ", userType=" + getCurrentUserType() +
                '}';
    }
}