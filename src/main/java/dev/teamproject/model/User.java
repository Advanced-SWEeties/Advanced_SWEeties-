package dev.teamproject.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Represents a User entity in the application.
 * This class contains details of a User, including his userId, username, password, apiKey and type.
 */
@Data
@Entity
@SpringBootApplication
@Table(name = "\"user\"")
public class User {
  private static Long userIdCounter = 0L; // Static variable to keep track of user IDs
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;
  private String username;
  private String password;
  private String apiKey;
  private String userType; 
  private LocalDateTime accountCreationTime;
  @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Rating> ratings = new ArrayList<>();

  /**
   * Creates a new user account with the specified username and password.
   * Generates a unique user ID and sets the account creation time to the current time.
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

  //  private Long generateUserId() {
  //    return ++userIdCounter;
  //  }



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

  @Override
  public String toString() {
    return this.getUserId() + " " + this.getUsername() + " " + this.getUserType() + ".";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return Objects.equals(this.getUserId(), ((User) o).getUserId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}