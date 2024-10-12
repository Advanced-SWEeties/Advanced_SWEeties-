package dev.TeamProject.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import org.springframework.security.access.prepost.PreAuthorize;

import dev.TeamProject.Model.Kitchen;
import dev.TeamProject.Model.User;
import dev.TeamProject.Model.WaitTimePrediction;
import dev.TeamProject.Model.Rating;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SampleController {
    // home endpoint
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
     * Description: Retrieves the specified number of nearest charity kitchens based on the user’s geographical location.
     *
     * Response:
     * 200 OK - Returns a list of kitchens with details such as name, address, distance, rating, and accessibility features.
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
     * Description: Provides a list of top-rated kitchens sorted by a combination of ratings and distance, adjusted for service quality.
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
        return new ResponseEntity<>(/* List of Kitchens, */ HttpStatus.OK);
    }

    /*
     * Endpoint: /kitchens/details
     * Method: GET
     * Query Parameters:
     * kitchen_id - Unique identifier for the charity kitchen.
     * Description: Fetches detailed information about a specific charity kitchen, including operational status, operating hours, and services offered.
     * Response:
     * 200 OK - Detailed kitchen information.
     * 404 Not Found - If kitchen_id does not correspond to an existing kitchen.
     * 500 Internal Server Error - For unexpected backend errors.
     */
    // 3. Get Kitchen Details
    @GetMapping("/kitchens/details")
    public ResponseEntity<Kitchen> getKitchenDetails(
            @RequestParam Long kitchen_id) {
        // Logic to fetch kitchen details
        return new ResponseEntity<>(/* Kitchen Details, */ HttpStatus.OK);
    }

    /*
     * Endpoint: /kitchens/wait-times
     * Method: GET
     * Query Parameters:
     * kitchen_id - Unique identifier for the charity kitchen.
     * Description: Uses machine learning models to predict average waiting times based on historical data.
     * Response:
     * 200 OK - Predicted average waiting time.
     * 404 Not Found - If kitchen_id does not exist.
     * 500 Internal Server Error - For unexpected backend errors.

     */
    // 4. Predict Waiting Times
    @GetMapping("/kitchens/wait-times")
    public ResponseEntity<WaitTimePrediction> getWaitingTimePrediction(
            @RequestParam Long kitchen_id) {
        // Logic to predict waiting times
        return new ResponseEntity<>(/* Wait Time Prediction, */ HttpStatus.OK);
    }

    /*
     * Endpoint: /kitchens/rate
     * Method: POST
     * Request Body:
     * kitchen_id - Unique identifier for the charity kitchen.
     * rating - User rating from 1 to 5.
     * user_id - Unique identifier for the user submitting the rating.
     * Description: Allows users to rate a charity kitchen. Ratings are used to update the kitchen’s overall rating.
     * Response:
     * 200 OK - Rating successfully recorded.
     * 400 Bad Request - If the input parameters are invalid.
     * 404 Not Found - If kitchen_id or user_id does not exist.
     * 500 Internal Server Error - For unexpected backend errors.
     */

    // 5. Submit Rating (Only for authenticated users)
    // @PreAuthorize("isAuthenticated()") // The corresponding Spring Security configuration is not implemented (TODO)
    @PostMapping("/kitchens/rate")
    public ResponseEntity<String> submitRating(
            @RequestBody Rating rating) {
        // Logic to submit rating
        return new ResponseEntity<>("Rating successfully recorded.", HttpStatus.OK);
    }

    /*
     * Endpoint: /users/login
     * Method: POST
     * Request Body:
     * username - Username of the user.
     * password - Password of the user.
     * Description: Authenticates user credentials and returns an API key for session management.
     * Response:
     * 200 OK - Returns API key and user details.
     * 401 Unauthorized - If credentials are incorrect.
     * 500 Internal Server Error - For unexpected backend errors.
     */

    // 6. User Login (Public access)
    @PostMapping("/users/login")
    public ResponseEntity<User> userLogin(
            @RequestBody User loginRequest) {
        // Logic to authenticate user
        return new ResponseEntity<>(/* User Details with API Key, */ HttpStatus.OK);
    }


    /* 
     * Endpoint: /kitchens/update
     * Method: PUT
     *
     * Required Role: Super Golden Plus User or Manager
     * Request Body:
     * kitchen_id - Unique identifier for the charity kitchen.
     * new_details - Object containing updated details like address, operating hours, etc.
     * Description: Allows authorized users to update the details of a charity kitchen.
     * Response:
     * 200 OK - Kitchen information updated successfully.
     * 403 Forbidden - If the user does not have sufficient privileges.
     * 404 Not Found - If kitchen_id does not exist.
     * 500 Internal Server Error - For unexpected backend errors.
     */
    // 7. Update Kitchen Info (Only for 'SUPER_GOLDEN_PLUS' or 'MANAGER')
    // @PreAuthorize("hasAnyRole('SUPER_GOLDEN_PLUS', 'MANAGER')") // The corresponding Spring Security configuration is not implemented (TODO)
    @PutMapping("/kitchens/update")
    public ResponseEntity<String> updateKitchenInfo(
            @RequestBody Kitchen updatedKitchenDetails) {
        // Logic to update kitchen info
        return new ResponseEntity<>("Kitchen information updated successfully.", HttpStatus.OK);
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
    // @PreAuthorize("hasAnyRole('SUPER_GOLDEN_PLUS', 'MANAGER')") // The corresponding Spring Security configuration is not implemented (TODO)
    @PostMapping("/kitchens/add")
    public ResponseEntity<String> addKitchen(
            @RequestBody Kitchen newKitchenDetails) {
        // Logic to add a new kitchen
        return new ResponseEntity<>("New kitchen added successfully.", HttpStatus.CREATED);
    }

    /*
     * Endpoint: /users/delete
     * Method: DELETE
     * Required Role: Manager
     * Query Parameters:
     * user_id - Unique identifier for the user to be deleted.
     * Description: Allows authorized users to delete a user from the system.
     * Response:
     * 200 OK - User deleted successfully.
     * 403 Forbidden - If the user does not have sufficient privileges.
     * 500 Internal Server Error - For unexpected backend errors.
     */
    // 9. Delete User (Only for 'MANAGER')
    // @PreAuthorize("hasRole('MANAGER')") // The corresponding Spring Security configuration is not implemented (TODO)
    @DeleteMapping("/users/delete")
    public ResponseEntity<String> deleteUser(
            @RequestParam Long user_id) {
        // Logic to delete user
        return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
    }
}
