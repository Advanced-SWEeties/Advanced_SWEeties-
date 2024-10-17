package dev.teamproject.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a User entity in the application.
 * This class contains details of a User, including his userId, username, password, apiKey and type.
 */
public class User {
  private static Long userIdCounter = 0L; // Static variable to keep track of user IDs
  private Long userId;
  private String username;
  private String password;
  private String apiKey;
  private String userType; 
  private LocalDateTime accountCreationTime; 

  /**
   * Creates a new user account with the specified username and password.
   * Generates a unique user ID and sets the account creation time to the current time.
   *
   * @param username the username for the new account
   * @param password the password for the new account
   */
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

  /**
   * Function to log in a user with the provided username and password.
   *
   * @param username the username of the user attempting to log in
   * @param password the password of the user attempting to log in
   * @return true if the login is successful; false otherwise
   */
  public boolean login(String username, String password) {
    if (this.username.equals(username) && this.password.equals(password)) {
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