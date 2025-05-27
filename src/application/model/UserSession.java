package application.model;

public class UserSession {
    private static UserSession instance;
    
    private User currentUser;
    private boolean loggedIn = false;
    
    private UserSession() {
        // private constructor
    }
    
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    public void login(User user) {
        this.currentUser = user;
        this.loggedIn = true;
    }
    
    public void logout() {
        this.currentUser = null;
        this.loggedIn = false;
    }
    
    public boolean isLoggedIn() {
        return loggedIn;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public int getUserId() {
        return currentUser != null ? currentUser.getId() : 0;
    }
    
    public String getUserName() {
        return currentUser != null ? currentUser.getFirstName() + " " + currentUser.getLastName() : "";
    }
    
    public String getUserEmail() {
        return currentUser != null ? currentUser.getEmail() : "";
    }
    
    public String getUserType() {
        return currentUser != null ? currentUser.getUserType() : "";
    }
}