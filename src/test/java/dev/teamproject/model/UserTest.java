package dev.teamproject.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
  }

  @Test
  void testCreateAccount_SetsUsernameAndPassword() {
    String username = "testuser";
    String password = "password123";
    user.createAccount(username, password, "");

    assertEquals(username, user.getUsername());
    assertEquals(password, user.getPassword());
    assertNotNull(user.getAccountCreationTime());
  }

  @Test
  void testCreateAccount_SetsRoleWhenEmpty() {
    user.createAccount("testuser", "password123", "");
    assertEquals("BRONZE_USER", user.getRole()); // Assuming newly created users are BRONZE
  }

  @Test
  void testCreateAccount_SetsGivenRole() {
    user.createAccount("testuser", "password123", "ROLE_ADMIN");
    assertEquals("ROLE_ADMIN", user.getRole());
  }

  @Test
  void testUpdateRole_RoleIsNotAdmin_UpdatesRole() {
    // Create account and set the creation time to 6 months ago
    user.createAccount("testuser", "password123", ""); 
    user.setAccountCreationTime(LocalDateTime
        .now().minusMonths(6)); // Set the account creation time to 6 months ago

    user.updateRole();

    assertEquals("PLATINUM_USER", user.getRole());
  }

  @Test
  void testUpdateRole_RoleIsAdmin_NoUpdate() {
    user.createAccount("testuser", "password123", "ROLE_ADMIN");
    user.updateRole();

    assertEquals("ROLE_ADMIN", user.getRole());
  }

  @Test
  void testSetUserRole_AccountAgeLessThan1Month() {
    user.setAccountCreationTime(LocalDateTime.now().minusDays(15)); // Less than 1 month
    user.setUserRole();

    assertEquals("BRONZE_USER", user.getRole());
  }

  @Test
  void testSetUserRole_AccountAge1To2Months() {
    user.setAccountCreationTime(LocalDateTime.now()
        .minusMonths(1).minusDays(15)); // About 1.5 months
    user.setUserRole();

    assertEquals("SILVER_USER", user.getRole());
  }

  @Test
  void testSetUserRole_AccountAge3To4Months() {
    user.setAccountCreationTime(LocalDateTime.now()
        .minusMonths(3).minusDays(1)); // Just under 4 months
    user.setUserRole();

    assertEquals("GOLD_USER", user.getRole());
  }

  @Test
  void testSetUserRole_AccountAge5MonthsOrMore() {
    user.setAccountCreationTime(LocalDateTime.now().minusMonths(5)); // Exactly 5 months
    user.setUserRole();

    assertEquals("PLATINUM_USER", user.getRole());
  }

  @Test
  void testEquals_SameUserId() {
    User user1 = new User();
    user1.setUserId(1L);

    User user2 = new User();
    user2.setUserId(1L);

    assertEquals(user1, user2);
  }

  @Test
  void testEquals_DifferentUserId() {
    User user1 = new User();
    user1.setUserId(1L);

    User user2 = new User();
    user2.setUserId(2L);

    assertNotEquals(user1, user2);
  }

  @Test
  void testHashCode_SameUserId() {
    User user1 = new User();
    user1.setUserId(1L);

    User user2 = new User();
    user2.setUserId(1L);

    assertEquals(user1.hashCode(), user2.hashCode());
  }

  @Test
  void testToString() {
    user.setUserId(1L);
    user.setUsername("testuser");
    user.setRole("BRONZE_USER");

    assertEquals("1 testuser BRONZE_USER.", user.toString());
  }
}