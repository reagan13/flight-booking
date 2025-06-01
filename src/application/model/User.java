package application.model;

import java.time.LocalDateTime;

public class User extends BaseEntity { // INHERITANCE
    // ENCAPSULATION - private fields
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private int age;
    private String userType;

    // Default constructor
    public User() {
        super();
    }
    
    // Constructor with essential fields
    public User(int id, String firstName, String lastName, String email, String userType) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userType = userType;
    }
    
    // Full constructor
    public User(int id, String firstName, String lastName, String email, 
                String address, int age, String userType) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.age = age;
        this.userType = userType;
    }
    
    // Constructor with timestamps
    public User(int id, String firstName, String lastName, String email, 
                String address, int age, String userType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, firstName, lastName, email, address, age, userType);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ENCAPSULATION - controlled access through getters/setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
        updateTimestamp(); // inherited method
    }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
        updateTimestamp();
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email; 
        updateTimestamp();
    }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { 
        this.address = address; 
        updateTimestamp();
    }
    
    public int getAge() { return age; }
    public void setAge(int age) { 
        this.age = age; 
        updateTimestamp();
    }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { 
        this.userType = userType; 
        updateTimestamp();
    }
    
    // Business methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isAdmin() {
        return "admin".equals(userType);
    }
    
    public boolean isRegularUser() {
        return "regular".equals(userType) || "user".equals(userType);
    }
    
    // POLYMORPHISM - implementing abstract method
    @Override
    public String getDisplayName() {
        return getFullName();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}