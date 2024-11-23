package dev.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.teamproject.model.User;
import dev.teamproject.service.UserService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/* 
@ExtendWith(MockitoExtension.class)
class UserTests {

  @Mock
  private UserService userService;

  @InjectMocks
  private User user;

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
  public void loginUserNameFailTest() {
    user.createAccount("testUser", "password123");
    boolean loginResultFail = user.login("differentUser", "password123");
    assertFalse(loginResultFail);
  }

  @Test
  public void loginPasswordFailTest() {
    user.createAccount("testUser", "password123");
    boolean loginResultFail = user.login("testUser", "wrongPassword");
    assertFalse(loginResultFail);
  }

  @Test
  public void testUserTypeInitial() {
    user.createAccount("testUser", "password123");
    assertEquals("BronzeMember", user.getUserType());
  }

  @Test
  void testSetUserTypePlatinumMember() {
    user.setAccountCreationTime(LocalDateTime.now().minus(6, ChronoUnit.MONTHS));
    user.setUserType();
    assertEquals("PlatinumMember", user.getUserType(),
        "User type should be PlatinumMember for 5+ months");
  }

  @Test
  void testSetUserTypeGoldMember() {
    user.setAccountCreationTime(LocalDateTime.now().minus(4, ChronoUnit.MONTHS));
    user.setUserType();
    assertEquals("GoldMember", user.getUserType(),
        "User type should be GoldMember for 3–4 months");
  }

  @Test
  void testSetUserTypeSilverMember() {
    user.setAccountCreationTime(LocalDateTime.now().minus(2, ChronoUnit.MONTHS));
    user.setUserType();
    assertEquals("SilverMember", user.getUserType(),
        "User type should be SilverMember for 1–2 months");
  }

  @Test
  void testSetUserTypeBronzeMember() {
    user.setAccountCreationTime(LocalDateTime.now().minus(15, ChronoUnit.DAYS));
    user.setUserType();
    assertEquals("BronzeMember", user.getUserType(),
        "User type should be BronzeMember for less than 1 month");
  }

  @Test
  public void testUserSetPassword() {
    user.createAccount("testUser", "password123");
    user.setPassword("newPassword");
    assertEquals("newPassword", user.getPassword());
  }


}
  */
