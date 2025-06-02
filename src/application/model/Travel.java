package application.model;


public class Travel {
    private String id;
    private String name;
    private String location;
    private double rating;
    private String description;
    private String imageUrl;
    private String price;
    private String type; // hotel, attraction, etc.
    private String websiteUrl;
    
    // Constructor
    public Travel(String id, String name, String location, double rating, 
                  String description, String imageUrl, String price, String type, 
                  String websiteUrl) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.type = type;
        this.websiteUrl = websiteUrl;
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public double getRating() {
        return rating;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public String getPrice() {
        return price;
    }
    
    public String getType() {
        return type;
    }
    
    public String getWebsiteUrl() {
        return websiteUrl;
    }
    
    public String getDisplayName() {
        return name;
    }
    
    public String getDisplayRating() {
        return String.format("%.1f★", rating);
    }
    
    @Override
    public String toString() {
        return name + " - " + location;
    }
}