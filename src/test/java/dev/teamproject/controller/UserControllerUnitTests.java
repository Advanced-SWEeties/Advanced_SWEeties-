package dev.teamproject.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.teamproject.model.AuthenticationResponse;
import dev.teamproject.model.User;
import dev.teamproject.service.UserService;
import dev.teamproject.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserControllerUnitTests {

  @InjectMocks
  private UserController userController;

  @Mock
  private UserService userService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = new User();
    user.setUserId(1L);
    user.setUsername("testuser");
    user.setPassword(passwordEncoder.encode("password")); // Encoding the password for testing
    user.setRole("ROLE_MANAGER");
  }

  @Test
  void getUserById_UserExists_ReturnsUser() {
    when(userService.getUserById(1L)).thenReturn(Optional.of(user));

    ResponseEntity<User> response = userController.getUserById(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(user, response.getBody());
  }

  @Test
  void testGetUserById_NonExistingUser() {
    given(userService.getUserById(1L)).willReturn(Optional.empty());

    ResponseEntity<User> response = userController.getUserById(1L);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void testAddUser_InternalServerError() {
    Map<String, String> userInput = new HashMap<>();
    userInput.put("username", "newuser");
    userInput.put("password", "newpassword");
    userInput.put("role", "USER");

    // Simulate an error when saving the user
    doThrow(new RuntimeException("Database error")).when(userService).saveUser(any(User.class));

    ResponseEntity<String> response = userController.addUser(userInput);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().startsWith("Error adding user:"));
  }

  @Test
  void testLoginUser_InvalidCredentials() {
    Map<String, String> credentials = new HashMap<>();
    credentials.put("username", "testuser");
    credentials.put("password", "wrongpassword");

    // Mock authentication to throw exception
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid username or password"));

    ResponseEntity<AuthenticationResponse> response = userController.loginUser(credentials);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getError().contains("Invalid username or password"));
  }

  @Test
  void testDeleteUser_ExistingUser() {
    given(userService.getUserById(1L)).willReturn(Optional.of(user));

    ResponseEntity<String> response = userController.deleteUser(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User deleted successfully.", response.getBody());
    verify(userService).deleteUser(1L);
  }

  @Test
  void testDeleteUser_NonExistingUser() {
    given(userService.getUserById(1L)).willReturn(Optional.empty());

    ResponseEntity<String> response = userController.deleteUser(1L);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("User not found.", response.getBody());
  }

  @Test
  void testDeleteUser_InternalServerError() {
    given(userService.getUserById(1L)).willThrow(new RuntimeException("Database error"));

    ResponseEntity<String> response = userController.deleteUser(1L);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().startsWith("Error deleting user:"));
  }
}