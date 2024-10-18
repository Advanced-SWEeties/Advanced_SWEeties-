package dev.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.teamproject.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserTests {

  @BeforeEach
  public void setUp() {
    user = new User(); // Initialize the user variable
  }

  @Test
  public void createAccountTest() {
    user.createAccount("testUser", "password123");
    assertNotNull(user.getUsername());
    assertEquals("testUser", user.getUsername());
  }

  @Test
  public void apiKeyTest() {
    user.setApiKey("ABCD1234");
    assertEquals(user.getApiKey(), "ABCD1234"); 
  }

  @Test
  public void loginTest() {
    user.createAccount("testUser", "password123");
    boolean loginResultSuccess = user.login("testUser", "password123");
    assertTrue(loginResultSuccess);

    boolean loginResultFail = user.login("testUser", "wrongPassword");
    assertFalse(loginResultFail);
  }

  @Test
  public void testUserTypeInitial() {
    user.createAccount("testUser", "password123");
    assertEquals("BronzeMember", user.getUserType());
  }

  public static User user;
}
