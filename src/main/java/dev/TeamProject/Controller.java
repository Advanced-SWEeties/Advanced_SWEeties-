package dev.TeamProject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.access.prepost.PreAuthorize;

import dev.TeamProject.model.Kitchen;
import dev.TeamProject.model.User;
import dev.TeamProject.model.WaitTimePrediction;
import dev.TeamProject.model.Rating;

import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {
    // home endpoint
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return new ResponseEntity<>("Welcome to the Kitchen API!", HttpStatus.OK);
    }
    
    // 1. Get Nearest Kitchens
    @GetMapping("/kitchens/nearest")
    public ResponseEntity<List<Kitchen>> getNearestKitchens(
            @RequestParam String address,
            @RequestParam int count) {
        // Logic to retrieve nearest kitchens
        return new ResponseEntity<>(/* List of Kitchens, */ HttpStatus.OK);
    }

    // 2. Get Top Rated Kitchens
    @GetMapping("/kitchens/top-rated")
    public ResponseEntity<List<Kitchen>> getTopRatedKitchens(
            @RequestParam int count) {
        // Logic to retrieve top-rated kitchens
        return new ResponseEntity<>(/* List of Kitchens, */ HttpStatus.OK);
    }

    // 3. Get Kitchen Details
    @GetMapping("/kitchens/details")
    public ResponseEntity<Kitchen> getKitchenDetails(
            @RequestParam Long kitchen_id) {
        // Logic to fetch kitchen details
        return new ResponseEntity<>(/* Kitchen Details, */ HttpStatus.OK);
    }

    // 4. Predict Waiting Times
    @GetMapping("/kitchens/wait-times")
    public ResponseEntity<WaitTimePrediction> getWaitingTimePrediction(
            @RequestParam Long kitchen_id) {
        // Logic to predict waiting times
        return new ResponseEntity<>(/* Wait Time Prediction, */ HttpStatus.OK);
    }

    // 5. Submit Rating (Only for authenticated users)
    // @PreAuthorize("isAuthenticated()") // The corresponding Spring Security configuration is not implemented (TODO)
    @PostMapping("/kitchens/rate")
    public ResponseEntity<String> submitRating(
            @RequestBody Rating rating) {
        // Logic to submit rating
        return new ResponseEntity<>("Rating successfully recorded.", HttpStatus.OK);
    }

    // 6. User Login (Public access)
    @PostMapping("/users/login")
    public ResponseEntity<User> userLogin(
            @RequestBody User loginRequest) {
        // Logic to authenticate user
        return new ResponseEntity<>(/* User Details with API Key, */ HttpStatus.OK);
    }

    // 7. Update Kitchen Info (Only for 'SUPER_GOLDEN_PLUS' or 'MANAGER')
    // @PreAuthorize("hasAnyRole('SUPER_GOLDEN_PLUS', 'MANAGER')") // The corresponding Spring Security configuration is not implemented (TODO)
    @PutMapping("/kitchens/update")
    public ResponseEntity<String> updateKitchenInfo(
            @RequestBody Kitchen updatedKitchenDetails) {
        // Logic to update kitchen info
        return new ResponseEntity<>("Kitchen information updated successfully.", HttpStatus.OK);
    }

    // 8. Add Kitchen (Only for 'SUPER_GOLDEN_PLUS' or 'MANAGER')
    // @PreAuthorize("hasAnyRole('SUPER_GOLDEN_PLUS', 'MANAGER')") // The corresponding Spring Security configuration is not implemented (TODO)
    @PostMapping("/kitchens/add")
    public ResponseEntity<String> addKitchen(
            @RequestBody Kitchen newKitchenDetails) {
        // Logic to add a new kitchen
        return new ResponseEntity<>("New kitchen added successfully.", HttpStatus.CREATED);
    }

    // 9. Delete User (Only for 'MANAGER')
    // @PreAuthorize("hasRole('MANAGER')") // The corresponding Spring Security configuration is not implemented (TODO)
    @DeleteMapping("/users/delete")
    public ResponseEntity<String> deleteUser(
            @RequestParam Long user_id) {
        // Logic to delete user
        return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
    }
}
