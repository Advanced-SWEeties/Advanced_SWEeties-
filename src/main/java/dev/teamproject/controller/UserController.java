package dev.teamproject.controller;

import dev.teamproject.model.User;
import dev.teamproject.service.UserService;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    Optional<User> user = userService.getUserById(userId);
    return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
    String password = userInput.get("password");

    try {
      User newUser = new User(); // Create a new User object
      newUser.createAccount(username, password); // Set username and password
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
  public ResponseEntity<Map<String, Object>> 
      loginUser(@RequestBody Map<String, String> credentials) {
    String username = credentials.get("username");
    String password = credentials.get("password");

    try {
      Optional<User> userOpt = userService.authenticate(username, password);
      if (userOpt.isPresent()) {
        User user = userOpt.get();
        //String apiKey = userService.generateApiKey(user); 
        String apiKey = "random12345";
        Map<String, Object> response = Map.of(
            "apiKey", apiKey,
            "user", user
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(Map.of("error", "Unexpected error: " + e.getMessage()), 
        HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}