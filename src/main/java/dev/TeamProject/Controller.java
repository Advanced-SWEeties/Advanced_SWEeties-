package dev.TeamProject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.TeamProject.entities.*;
import dev.TeamProject.model.LocationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Controller {

    // get the Google API key
    @Value("${google.map.key}")
    String apiKey;

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
    public ResponseEntity<?> getNearestKitchens(
            @RequestParam String address
//            @RequestParam int count
    ) {

        // Logic to retrieve nearest kitchens
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
            address + "&key=" + apiKey;

        try{
            ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response.getBody());
            String s =  root.path("status").asText();
            JsonNode results = root.path("results");

            /* ZERO_RESULTS probably resulted from invalid address input, prompt the user to enter a valid address  */
            if (s.equals("ZERO_RESULTS")) {
                return new ResponseEntity<>("Invalid address, please try again with an accurate address", HttpStatus.BAD_REQUEST);
            } else {
                // get the latitude and longitude
                JsonNode location = results.get(0).path("geometry").path("location");
                double lat = location.path("lat").asDouble();
                double lng = location.path("lng").asDouble();

                // get the full name of the address
                String formattedAddress = results.get(0).path("formatted_address").asText();

                LocationDTO locationResponse = new LocationDTO(lat, lng, formattedAddress);
                return new ResponseEntity<>(locationResponse, HttpStatus.OK);
                //        return new ResponseEntity<>(/* List of Kitchens, */ HttpStatus.OK);
            }
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
    }

    @GetMapping("/allKitchens")
    public ResponseEntity<?> PopulateKitchenData(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-Goog-Api-Key", apiKey);
        headers.add("X-Goog-FieldMask", "*");
//       headers.add("X-Goog-FieldMask", "places.displayName,places.formattedAddress,places.location");

        String requestUrl = "https://places.googleapis.com/v1/places:searchText";
        String body = "{ \"textQuery\": \"soup kitchen in New York City\", \"regionCode\": \"US\" }";
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        RestTemplate template = new RestTemplate();
        List<TempInfo> tempInfos = new ArrayList<>();

        try{
            ResponseEntity<String> response = template.postForEntity(requestUrl,request,String.class);
            ObjectMapper mapper =  new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            if (root.size() == 0) {
                return new ResponseEntity<>("No data found, please enter a valid address", HttpStatus.NOT_FOUND);
            }

            JsonNode places = root.path("places");
            for (JsonNode place : places) {
                String formattedAddress = place.path("formattedAddress").asText();
                String displayName = place.path("displayName").path("text").asText();
                String id = place.path("id").asText();
                String number= place.path("nationalPhoneNumber").asText();
                double rating = place.path("rating").asDouble();
                String businessStatus = place.path("businessStatus").asText();

                JsonNode location = place.path("location");
                double lat = location.path("latitude").asDouble();
                double lng = location.path("longitude").asDouble();
                List<String> operatingHours = new ArrayList<>();
                JsonNode weekdayDescriptions = place.path("regularOpeningHours").path("weekdayDescriptions"); //
                if (weekdayDescriptions.isArray()) {
                    for (JsonNode hour : weekdayDescriptions) {
                        operatingHours.add(hour.asText());
                    }
                }

                Map<String, Object> reviewsInfo = new HashMap<>();
                JsonNode reviews = place.path("reviews");
                if (reviews.isArray()) {
                    for (JsonNode review : reviews) {
                        String authorName = review.path("authorAttribution").path("displayName").asText();
                        String authorUri = review.path("authorAttribution").path("uri").asText();
                        int reviewerRating = review.path("rating").asInt();
                        String reviewText = review.path("text").path("text").asText();
                        String publishTime = review.path("publishTime").asText();
                        String relativeTime = review.path("relativePublishTimeDescription").asText();
                        reviewsInfo.put("authorName", authorName);
                        reviewsInfo.put("authorUri", authorUri);
                        reviewsInfo.put("reviewerRating", reviewerRating);
                        reviewsInfo.put("reviewText", reviewText);
                        reviewsInfo.put("publishTime", publishTime);
                        reviewsInfo.put("relativeTime", relativeTime);
                    }
                }

                Map<String, Boolean> accessibilityOptionMap = new HashMap<>();
                JsonNode accessibilityOptions = place.path("accessibilityOptions");
                // warning: some of the fields are optional, so we need to check if they exist before we access them
                boolean wheelchairAccessibleParking;
                boolean wheelchairAccessibleRestroom;
                boolean wheelchairAccessibleSeating;
                boolean wheelchairAccessibleEntrance;

                if (accessibilityOptions.has("wheelchairAccessibleEntrance")) {
                    wheelchairAccessibleParking = accessibilityOptions.path("wheelchairAccessibleParking").asBoolean();
                    accessibilityOptionMap.put("wheelchairAccessibleParking", wheelchairAccessibleParking);
                }

                if (accessibilityOptions.has("wheelchairAccessibleRestroom")) {
                    wheelchairAccessibleRestroom = accessibilityOptions.path("wheelchairAccessibleRestroom").asBoolean();
                    accessibilityOptionMap.put("wheelchairAccessibleRestroom", wheelchairAccessibleRestroom);
                }

                if (accessibilityOptions.has("wheelchairAccessibleSeating")) {
                    wheelchairAccessibleSeating = accessibilityOptions.path("wheelchairAccessibleSeating").asBoolean();
                    accessibilityOptionMap.put("wheelchairAccessibleSeating", wheelchairAccessibleSeating);
                }

                if (accessibilityOptions.has("wheelchairAccessibleEntrance")) {
                    wheelchairAccessibleEntrance = accessibilityOptions.path("wheelchairAccessibleEntrance").asBoolean();
                    accessibilityOptionMap.put("wheelchairAccessibleEntrance", wheelchairAccessibleEntrance);
                }

                tempInfos.add(new TempInfo(displayName, formattedAddress,
                    lat, lng, id, number,rating, operatingHours, businessStatus,
                    reviewsInfo, accessibilityOptionMap));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(tempInfos, HttpStatus.OK);
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
