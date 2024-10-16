package dev.teamproject.controller;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.User;

import java.util.*;

import dev.teamproject.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class KitchenController {
  private final KitchenService kitchenService;

  @Autowired
  public KitchenController(KitchenService kitchenService) {
    this.kitchenService = kitchenService;

  }
  @GetMapping("/")
  public ResponseEntity<String> home() {
    return new ResponseEntity<>("Welcome to the Kitchen API!", HttpStatus.OK);
  }

  /*
   * Endpoint: /kitchens/nearest
   * Method: GET
   *
   * Query Parameters:
   * String - A string indicating the address
   * count - Number of charity kitchens to return.
   * Description: Retrieves the specified number of nearest charity
   * kitchens based on the user’s geographical location.
   *
   * Response:
   * 200 OK - Returns a list of kitchens with details such as name, address, distance,
   * rating, and accessibility features.
   * 400 Bad Request - If the parameters are invalid.
   * 500 Internal Server Error - For unexpected backend errors.
   */
  // 1. Get Nearest Kitchens
  @GetMapping("/kitchens/nearest")
  public ResponseEntity<List<Kitchen>> getNearestKitchens(
          @RequestParam String address,
          @RequestParam int count) {
    // Logic to retrieve nearest kitchens
    return new ResponseEntity<>(/* List of Kitchens, */ HttpStatus.OK);
  }

  /*
   * Endpoint: /kitchens/top-rated
   * Method: GET
   * Query Parameters:
   * count - Number of charity kitchens to return.
   * Description: Provides a list of top-rated kitchens sorted by a combination of ratings
   * and distance, adjusted for service quality.
   * Response:
   * 200 OK - Successfully returns the list of top-rated charity kitchens.
   * 400 Bad Request - If the parameters are invalid.
   * 500 Internal Server Error - For unexpected backend errors.
   */
  // 2. Get Top Rated Kitchens
  @GetMapping("/kitchens/top-rated")
  public ResponseEntity<List<Kitchen>> getTopRatedKitchens(
          @RequestParam int count) {
    // Logic to retrieve top-rated kitchens
    // by Default 20 for now
    return new ResponseEntity<>(kitchenService.topRatedKitchens(), HttpStatus.OK);
  }

  /*
   * Endpoint: /kitchens/details
   * Method: GET
   * Query Parameters:
   * kitchenId - Unique identifier for the charity kitchen.
   * Description: Fetches detailed information about a specific charity kitchen, including
   * operational status, operating hours, and services offered.
   * Response:
   * 200 OK - Detailed kitchen information.
   * 404 Not Found - If kitchenId does not correspond to an existing kitchen.
   * 500 Internal Server Error - For unexpected backend errors.
   */
  @GetMapping("/kitchens/details")
  public ResponseEntity<Optional<Kitchen>> getKitchenDetails(
          @RequestParam Long kitchenId) {
    // Logic to fetch kitchen details
    try {
      Optional<Kitchen> kitchen = kitchenService.getKitchenById(kitchenId);
      return new ResponseEntity<>(kitchen, HttpStatus.NOT_FOUND);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /*
   * Endpoint: /kitchens/update
   * Method: PUT
   *
   * Required Role: Super Golden Plus User or Manager
   * Request Body:
   * kitchenId - Unique identifier for the charity kitchen.
   * new_details - Object containing updated details like address, operating hours, etc.
   * Description: Allows authorized users to update the details of a charity kitchen.
   * Response:
   * 200 OK - Kitchen information updated successfully.
   * 403 Forbidden - If the user does not have sufficient privileges.
   * 404 Not Found - If kitchenId does not exist.
   * 500 Internal Server Error - For unexpected backend errors.
   */
  // 7. Update Kitchen Info (Only for 'SUPER_GOLDEN_PLUS' or 'MANAGER')
  // @PreAuthorize("hasAnyRole('SUPER_GOLDEN_PLUS', 'MANAGER')") // The corresponding
  // Spring Security configuration is not implemented (TODO)
  @PutMapping("/kitchens/update")
  public ResponseEntity<String> updateKitchenInfo(
          @RequestBody Kitchen updatedKitchenDetails) {
    // Check user role
    // Logic to update kitchen info
    try {
      Kitchen kitchen = kitchenService.updateKitchen(updatedKitchenDetails,
              updatedKitchenDetails.getKitchenId());
      return  new ResponseEntity<>("Kitchen information updated successfully.", HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>("Kitchen to update is not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("backend error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /*
   * Endpoint: /kitchens/add
   * Method: POST
   * Required Role: Super Golden Plus User or Manager
   * Request Body:
   * new_kitchen_details - Object containing details of the new kitchen to be added.
   * Description: Allows authorized users to add a new charity kitchen to the system.
   * Response:
   * 201 Created - New kitchen added successfully.
   * 403 Forbidden - If the user does not have sufficient privileges.
   * 500 Internal Server Error - For unexpected backend errors.
   *
   */
  // 8. Add Kitchen (Only for 'SUPER_GOLDEN_PLUS' or 'MANAGER')
  // @PreAuthorize("hasAnyRole('SUPER_GOLDEN_PLUS', 'MANAGER')") // The corresponding Spring
  // Security configuration is not implemented (TODO)
  @PostMapping("/kitchens/add")
  public ResponseEntity<String> addKitchen(
          @RequestBody Kitchen newKitchenDetails) {
    // Check user role
    // Logic to add a new kitchen
    return new ResponseEntity<>("New kitchen added successfully.", HttpStatus.CREATED);
  }

  /*
   * Endpoint: /kitchens/id
   * Method: DELETE
   * Required Role: Super Golden Plus User or Manager
   * Request Body:
   * kitchenId - Unique identifier for the charity kitchen.
   * Description: Allows authorized users to delete a new charity kitchen from the system.
   * Response:
   * 201 Created - New kitchen added successfully.
   * 403 Forbidden - If the user does not have sufficient privileges.
   * 500 Internal Server Error - For unexpected backend errors.
   *
   */
  // 8. Add Kitchen (Only for 'SUPER_GOLDEN_PLUS' or 'MANAGER')
  // @PreAuthorize("hasAnyRole('SUPER_GOLDEN_PLUS', 'MANAGER')") // The corresponding Spring
  // Security configuration is not implemented (TODO)
  @DeleteMapping("/kitchens/delete")
  public ResponseEntity<String> deleteKitchen(
          @RequestBody Long id) {
    // Check user role
    // Logic to delete a  kitchen
    try {
      kitchenService.deleteKitchen(id);
      return new ResponseEntity<>("Kitchen deleted successfully.", HttpStatus.CREATED);
    } catch (RuntimeException e) {
      return new ResponseEntity<>("Kitchen to delete is not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("backend error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}