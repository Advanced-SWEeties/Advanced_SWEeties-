package dev.TeamProject.model;

import java.util.ArrayList;
import java.util.List;

public class Distributor {
    private static Long distributorIdCounter = 0L; // Static variable to keep track of user IDs
    private Long distributorId;
    private String username;
    private String password;
    private String distributorType;
    private List<Kitchen> kitchens = new ArrayList<>();

    public void createAccount(String username, String password) {
        this.distributorId = generateDistributorId(); 
        this.username = username;
        this.password = password;
        this.setDistributorType();
    }

    public boolean login(String username, String password) {
        if (this.username.equals(username) && this.password.equals(password)){
            this.setDistributorType();
            return true;
        }
        return false;
    }

    private Long generateDistributorId() {
        return ++distributorIdCounter; 
    }

    public String getUsername() {
        return username;
    }

    public void resetPassword(String password) {
        this.password = password;
    }

    public String getDistributortype() {
        return distributorType;
    }

    public void addKitchen(Kitchen kitchen) {
        this.kitchens.add(kitchen);
    }

    public List<Kitchen> getKitchens() {
        return kitchens;
    }

    // Method to calculate and set distributor type based on total pickups
    private void setDistributorType() {
        int totalPickups = 0;

        // Calculate total pickups from all kitchens
        for (Kitchen kitchen : kitchens) {
            totalPickups += kitchen.getPickupCount(); // Get the number of pickups per kitchen
        }

        // Set distributor type based on total pickups
        if (totalPickups >= 100) {
            this.distributorType = "MasterChef";
        } else if (totalPickups >= 50) {
            this.distributorType = "CulinaryExpert";
        } else if (totalPickups >= 20) {
            this.distributorType = "Foodie";
        } else {
            this.distributorType = "Novice"; // Default user type if below 20 pickups
        }
    }
}
