package dev.teamproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents a User entity in the application.
 * This class contains details of a User, including their userId, username, 
 * password, apiKey, and type.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @NotBlank
  private String username;

  @NotBlank
  private String password;

  private String apiKey;

  private String userType;

  private LocalDateTime accountCreationTime;

  /**
   * Creates a new user account with the specified username and password.
   * Sets the account creation time to the current time.
   *
   * @param username the username for the new account
   * @param password the password for the new account
   */
  public void createAccount(String username, String password) {
    this.username = username;
    this.password = password;
    this.accountCreationTime = LocalDateTime.now();
    this.setUserType();
  }

  /**
   * Function to log in a user with the provided username and password.
   *
   * @param username the username of the user attempting to log in
   * @param password the password of the user attempting to log in
   * @return true if the login is successful; false otherwise
   */
  public boolean login(String username, String password) {
    return this.username.equals(username) && this.password.equals(password);
  }

  /**
   * Sets the user type based on the age of the account.
   * The user type is determined by the number of months since the account was created:
   * - PlatinumMember: 5 or more months
   * - GoldMember: 3 to 4 months
   * - SilverMember: 1 to 2 months
   * - BronzeMember: less than 1 month
   */
  public void setUserType() {
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
}