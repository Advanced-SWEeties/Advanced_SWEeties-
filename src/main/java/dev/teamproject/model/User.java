package dev.teamproject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.persistence.CascadeType;

/**
 * Represents a User entity in the application.
 * This class contains details of a User, including their userId, username, 
 * password, apiKey, and type.
 */
@Data
@Entity
@SpringBootApplication
@Table(name = "\"user\"")
public class User{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String role;

  private LocalDateTime accountCreationTime;

  @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Rating> ratings = new ArrayList<>();
  /**
   * Creates a new user account with the specified username and password.
   * Sets the account creation time to the current time.
   *
   * @param username the username for the new account
   * @param password the password for the new account
   */
  public void createAccount(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.accountCreationTime = LocalDateTime.now();
    if (role == null || role.isEmpty())
      setUserAuthority();
    else
      this.role = role;
  }

  public void updateRole(){
    if (!this.role.equals("ROLE_ADMIN")) {
      setUserAuthority();
    }
  }
  
  /**
   * Sets the user authority based on the age of the account.
   * The authority is determined by the number of months since the account was created:
   * - ROLE_PLATINUM: 5 or more months
   * - ROLE_GOLD: 3 to 4 months
   * - ROLE_SILVER: 1 to 2 months
   * - ROLE_BRONZE: less than 1 month
   */
  public void setUserAuthority() {
    long accountAgeInMonths = ChronoUnit.MONTHS.between(accountCreationTime, LocalDateTime.now());

    if (accountAgeInMonths >= 5) {
      this.role = "PLATINUM_USER";
    } else if (accountAgeInMonths >= 3) {
      this.role = "GOLD_USER";
    } 
    else if (accountAgeInMonths >= 1) {
      this.role = "SILVER_USER";
    } 
    else {
      this.role = "BRONZE_USER";
    }
  }

  /**
   * Sets the user type based on the age of the account.
   * The user type is determined by the number of months since the account was created:
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * Sets the user type based on the age of the account.
   * The user type is determined by the number of months since the account was created:
   */  
  public String getPassword() {
    return this.password; // This should be the hashed password
  }

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

  @Override
  public String toString() {
    return this.getUserId() + " " + this.getUsername() + " " + this.getRole() + ".";
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