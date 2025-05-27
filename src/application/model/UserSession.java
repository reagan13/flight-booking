package application.model;

/**
 * Singleton class to store user session information
 */
public class UserSession {
    private static UserSession instance;
    
    private int userId;
    private String name;
    private String email;
    private String userType;
    private boolean loggedIn;
    
    private UserSession() {
        // Private constructor to force singleton pattern
        this.loggedIn = false;
    }
    
    /**
     * Get the singleton instance
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
            System.out.println("=== DEBUG: Created new UserSession instance ===");
        }
        return instance;
    }
    
    /**
     * Set user details after successful login
     */
    public void setUser(int userId, String name, String email, String userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.loggedIn = true;
        System.out.println("User session set: " + userId + ", " + name);
    }
    
    /**
     * Get current user as User object
     */
    public User getCurrentUser() {
        if (!loggedIn) {
            return null;
        }
        
        // Create a User object with the current session data
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        
        // Split name into first and last name (assuming format is "First Last")
        String[] nameParts = name.split(" ", 2);
        if (nameParts.length > 0) {
            user.setFirstName(nameParts[0]);
            if (nameParts.length > 1) {
                user.setLastName(nameParts[1]);
            } else {
                user.setLastName("");
            }
        }
        
        return user;
    }
    
    /**
     * Clear user session on logout
     */
    public void clearSession() {
        this.userId = 0;
        this.name = null;
        this.email = null;
        this.userType = null;
        this.loggedIn = false;
    }
    
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public boolean isLoggedIn() {
        System.out.println("=== DEBUG: Checking if user is logged in: " + loggedIn + " ===");
        return loggedIn;
    }
    
    public int getUserId() {
        System.out.println("=== DEBUG: Getting user ID: " + userId + " ===");
        return userId;
    }
    
    public boolean isAdmin() {
        return "admin".equals(userType);
    }
}