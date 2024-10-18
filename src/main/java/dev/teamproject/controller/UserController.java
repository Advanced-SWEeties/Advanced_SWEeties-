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
}