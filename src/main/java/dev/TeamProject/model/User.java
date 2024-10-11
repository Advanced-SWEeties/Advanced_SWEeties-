package dev.TeamProject.model;

public class User {
    private static Long userIdCounter = 0L; // Static variable to keep track of user IDs
    private Long userId;
    private String username;
    private String password;
    private String apiKey;
    private String userType; // Standard, SuperGoldenPlus, Manager
    private boolean pickUp;
    private int pickUpCount;
    
    // Getters and Setters}

    // Getters and Setters
    public void createAccount(String username, String password) {
        this.userId = generateUserId(); 
        this.username = username;
        this.password = password;
        this.setUserType();
        // Additional logic for account creation (e.g., storing in database)
    }

    // Function to generate a new user ID
    private Long generateUserId() {
        return ++userIdCounter; // Increment the counter and return the new user ID
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    // Function to set the API key
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        // Additional logic if needed, e.g., validation or saving to a database
    }

    public String getApiKey() {
        return apiKey;
    }

    // Function to log in
    public boolean login(String username, String password) {
        // Logic to validate username and password against stored user data
        return this.username.equals(username) && this.password.equals(password);
    }

    // Function to set user type based on pickup count
    private void setUserType() {
        if (this.pickUpCount >= 30) {
            this.userType = "SuperGoldenPlus";
        } else if (this.pickUpCount >= 20) {
            this.userType = "GoldenPlus";
        } else if (this.pickUpCount >= 10) {
            this.userType = "Gold";
        } else {
            this.userType = "Standard"; // Default user type if below 10
        }
    }

    // Function to set availability for pickup
    public void setAvailableForPickup() {
        // Logic to mark the user as available for pickup (e.g., update a database field)
        // Also to store the number of pickups for a user
        this.pickUp = true;
        this.pickUpCount += 1;
        setUserType();
    }

    // Function to get user location (external API call)
    public void getUserLocation() {
        // Logic to call an external API to get the user's location
        // For example, using an HTTP client to make a request to the location API
    }
}