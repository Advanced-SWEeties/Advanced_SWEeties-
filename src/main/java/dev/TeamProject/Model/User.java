package dev.teamproject.model;

/**
 * Represents a user in the application.
 * This class contains information about the user, including their ID, username,
 * password, API key, and user type.
 */
public class User {
  private Long userId;
  private String username;
  private String password;
  private String apiKey;
  private String userType; // Standard, SuperGoldenPlus, Manager
  // Getters and Setters
}
