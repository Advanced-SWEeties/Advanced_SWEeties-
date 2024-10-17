package dev.TeamProject.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class User {
    private static Long userIdCounter = 0L; // Static variable to keep track of user IDs
    private Long userId;
    private String username;
    private String password;
    private String apiKey;
    private String userType; 
    private LocalDateTime accountCreationTime; 

    // Getters and Setters
    public void createAccount(String username, String password) {
        this.userId = generateUserId(); 
        this.username = username;
        this.password = password;
        this.accountCreationTime = LocalDateTime.now(); 
        this.setUserType();
    }

    private Long generateUserId() {
        return ++userIdCounter;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public boolean login(String username, String password) {
        if (this.username.equals(username) && this.password.equals(password)){
            this.setUserType();
            return true;
        }
        return false;
    }

    // Function to set user type based on account age in months
    private void setUserType() {
        long accountAgeInMonths = ChronoUnit.MONTHS.between(accountCreationTime, LocalDateTime.now());
    
        if (accountAgeInMonths >= 5) {
            this.userType = "PlatinumMember";
        } else if (accountAgeInMonths >= 3) {
            this.userType = "GoldMember";
        } else if (accountAgeInMonths >= 1) {
            this.userType = "SilverMember";
        } else {
            this.userType = "BronzeMember"; 
        }
    }
    
    public String getUserType() {
        return userType;
    }
}