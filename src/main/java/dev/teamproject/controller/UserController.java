package dev.teamproject.controller;

import dev.teamproject.model.AuthenticationResponse;
import dev.teamproject.model.User;
import dev.teamproject.service.UserService;
import dev.teamproject.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing user-related endpoints.
 * This class handles HTTP requests related to user operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private PasswordEncoder passwordEncoder; // Inject the password encoder

  @Autowired
  private JwtUtil jwtUtil;

  /**
   * Endpoint: /api/users/{userId}
   * Method: GET
   * Description: Retrieves user details by user ID.
   * Response:
   * 200 OK - User details.
   * 404 Not Found - If userId does not exist.
   */
  @GetMapping("/{userId}")
  public ResponseEntity<User> getUserById(@PathVariable Long userId) {
    Optional<User> optionalUser = userService.getUserById(userId);
    
    if (optionalUser.isPresent()) {
      User user = optionalUser.get(); 
      user.updateRole(); 
      return new ResponseEntity<>(user, HttpStatus.OK); 
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
    }
  }

  /**
   * Endpoint: /api/users/add
   * Method: POST
   * Description: Adds a new user.
   * Response:
   * 201 Created - New user added successfully.
   * 500 Internal Server Error - For unexpected backend errors.
   */
  @PostMapping("/add")
  public ResponseEntity<String> addUser(@RequestBody Map<String, String> userInput) {
    String username = userInput.get("username");
    String rawPassword = userInput.get("password");
    String role = userInput.get("role");

    try {
      User newUser = new User(); // Create a new User object
      String encodedPassword = passwordEncoder.encode(rawPassword); // Encode the password
      newUser.createAccount(username, encodedPassword, role); // Set the encoded password
      userService.saveUser(newUser); // Save the new user
      return new ResponseEntity<>("New user added successfully.", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Error adding user: " + e.getMessage(), 
        HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Endpoint: /api/users/login
   * Method: POST
   * Description: Authenticates user credentials and returns an API key for session management.
   * Response:
   * 200 OK - Returns API key and user details.
   * 401 Unauthorized - If credentials are incorrect.
   * 500 Internal Server Error - For unexpected backend errors.
   */
  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse>
      loginUser(@RequestBody Map<String, String> credentials) {
    String username = credentials.get("username");
    String password = credentials.get("password");

    try {
      // Use Spring Security's AuthenticationManager for authentication
      System.out.println("Username: " + username);
      System.out.println("Password: " + password);
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));
      final UserDetails userDetails = userService.loadUserByUsername(username);
      String jwt = jwtUtil.generateToken(userDetails.getUsername());
      return ResponseEntity.ok(new AuthenticationResponse(jwt, null));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new AuthenticationResponse(null, "Invalid username or password " + e.getMessage()));
    }
  }


  /**
   * Endpoint: /api/users/delete/{userId}
   * Method: DELETE
   * Description: Deletes a user by user ID. Accessible only to users with the "Manager" role.
   * Response:
   * 200 OK - User deleted successfully.
   * 404 Not Found - If the userId does not exist.
   * 403 Forbidden - If the user does not have permission.
   * 500 Internal Server Error - For unexpected backend errors.
   */
  @DeleteMapping("/delete/{userId}")
  public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
    try {
      Optional<User> user = userService.getUserById(userId);
      if (user.isPresent()) {
        userService.deleteUser(userId);
        return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>("Error deleting user: " + e.getMessage(), 
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}