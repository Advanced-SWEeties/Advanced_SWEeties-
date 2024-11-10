package dev.teamproject.controller;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.UserLocation;
import dev.teamproject.service.KitchenService;
import dev.teamproject.service.OpenAiService;
import dev.teamproject.service.RatingService;
import dev.teamproject.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Controller for managing kitchen-related endpoints.
 * This class handles HTTP requests related to kitchen operations.
 */
@RestController
@RequestMapping("/api")
public class KitchenController {
  private final KitchenService kitchenService;
  private final UserService userService;
  private final OpenAiService openAiService;
  private final RatingService ratingService;

  /**
   * Constructor for KitchenController.
   *
   * @param kitchenService The KitchenService object
   * @param userService The UserService object
   * @param openAiService The OpenAIService object
   * @param ratingService The RatingService object
   */
  @Autowired
  public KitchenController(
      KitchenService kitchenService,
      UserService userService,
      OpenAiService openAiService,
      RatingService ratingService) {
    this.kitchenService = kitchenService;
    this.userService = userService;
    this.openAiService = openAiService;
    this.ratingService = ratingService;
  }

  @GetMapping("/")
  public ResponseEntity<String> home() {
    return new ResponseEntity<>("Welcome to the Kitchen API!", HttpStatus.OK);
  }

  /**
   * Endpoint: /kitchens/recommendation
   * Method: GET
   * Query Parameters:
   * location - the User's current location.
   * disabilityStatus - User’s disability status as a String for accessible kitchen filtering.
   * mealHours - Desired meal time as a String to find kitchens open during specified hours.
   * Description: Provides personalized recommendations for top 3 kitchens based on the user's
   * location, accessibility needs,  meal time preference, and comments from previous customers.
   * Response:
   * 200 OK - Returns recommendation for top 3 kitchens that AI model found best meet user's need.
   * 400 Bad Request - If any of the required parameters are missing or invalid.
   * 404 Not Found - If the location is invalid or no kitchens are found in the database.
   * 500 Internal Server Error -  For unexpected backend errors.
   */
  @GetMapping("/kitchens/recommendation")
  public ResponseEntity<?> getKitchenRecommendation(
      @RequestParam String location,
      @RequestParam String disabilityStatus,
      @RequestParam String mealHours
  ) {

    if (location == null || location.isEmpty() || disabilityStatus == null
        || disabilityStatus.isEmpty() || mealHours == null || mealHours.isEmpty()) {
      return new ResponseEntity<>("Invalid input", HttpStatus.BAD_REQUEST);
    }

    UserLocation userLocation = userService.getUserLocation(location);
    if (userLocation == null) {
      return new ResponseEntity<>("Invalid location", HttpStatus.NOT_FOUND);
    }

    List<Kitchen> allKitchens = kitchenService.getAllKitchens();
    if (allKitchens == null || allKitchens.isEmpty()) {
      return new ResponseEntity<>("No kitchens found in the Mysql DB", HttpStatus.NOT_FOUND);
    }

    // no need to check if allRatings is null or empty (kitchens may not have reviews).
    List<Rating> allRatings = ratingService.getAllRatings();
    Map response = openAiService.getKitchenRecommendation(
        allKitchens, allRatings, userLocation, disabilityStatus, mealHours);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Endpoint: /kitchens/nearest
   * Method: GET
   * Query Parameters:
   * String - A string indicating the address
   * count - Number of charity kitchens to return, if exceed the current number of
   * kitchens, only return all kitchens available.
   * Description: Retrieves the specified number of nearest charity
   * kitchens based on the user’s geographical location.
   * Response:
   * 200 OK - Returns a list of kitchens with details such as name, address, distance,
   * rating, and accessibility features.
   * 400 Bad Request - If the parameters are invalid.
   * 500 Internal Server Error - For unexpected backend errors.
   */
  @GetMapping("/kitchens/nearest")
  public ResponseEntity<?> getNearestKitchens(
          @RequestParam String address,
          @RequestParam int count) {

    if (address == null || address.isEmpty() || count < 0) {
      return new ResponseEntity<>("Invalid parameters", HttpStatus.BAD_REQUEST);
    }

    // Logic to retrieve nearest kitchens
    List<Kitchen> allKitchens = kitchenService.getAllKitchens();
    if (allKitchens == null || allKitchens.isEmpty()) {
      return new ResponseEntity<>("No kitchens found in the Mysql DB", HttpStatus.NOT_FOUND);
    }

    List<Kitchen> nearestKitchens = userService.getNearestKitchens(address, allKitchens, count);
    if (nearestKitchens == null) {
      return new ResponseEntity<>("Invalid address", HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(nearestKitchens, HttpStatus.OK);
  }

  /**
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
  @GetMapping("/kitchens/top-rated")
  public ResponseEntity<?> getTopRatedKitchens(
          @RequestParam int count) {
    // Logic to retrieve top-rated kitchens
    if (count < 0) {
      return new ResponseEntity<>("invalid count: negative number", HttpStatus.BAD_REQUEST);
    }

    List<Kitchen> list =  kitchenService.fetchTopRatedKitchens(count);
    if (list == null) {
      return new ResponseEntity<>("No kitchens found in the Mysql DB", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(list, HttpStatus.OK);
  }

  /**
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
      return new ResponseEntity<>(kitchen, HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Endpoint: /kitchens/update
   * Method: PUT
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

  /**
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
    try {
      kitchenService.saveKitchen(newKitchenDetails);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("backend error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>("New kitchen added successfully.", HttpStatus.CREATED);
  }

  /**
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
          @RequestParam Long kitchenId) {
    // Check user role
    // Logic to delete a  kitchen
    try {
      kitchenService.deleteKitchen(kitchenId);
      return new ResponseEntity<>("Kitchen deleted successfully.", HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>("Kitchen to delete is not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("backend error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}