package dev.teamproject.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.User;
import dev.teamproject.repository.KitchenRepository;
import dev.teamproject.repository.RatingRepository;
import dev.teamproject.repository.UserRepository;
import dev.teamproject.service.impl.KitchenServiceImpl;
import dev.teamproject.service.impl.RatingServiceImpl;
import dev.teamproject.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration testing for kitchen, user and rating.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserKitchenRatingIntegrationTest {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private KitchenRepository kitchenRepository;

  @Autowired
  private RatingRepository ratingRepository;

  @Autowired
  private RatingServiceImpl ratingService;

  @Autowired
  private KitchenServiceImpl kitchenService;

  @Autowired
  private UserServiceImpl userService;

  @Test
  public void testCreateEntities() {
    // Create a Kitchen
    Kitchen kitchen = new Kitchen();
    kitchen.setName("Test Kitchen");
    kitchenService.saveKitchen(kitchen);

    // Create a User
    User user = new User();
    user.setUsername("Test User");
    userRepository.save(user);

    // Create a Rating
    Rating rating = new Rating();
    rating.setRating(5);
    rating.setKitchen(kitchen);
    rating.setUser(user);
    ratingService.saveRating(rating);

    // Verify Entities are Saved
    assertNotNull(kitchen.getKitchenId());
    assertNotNull(user.getUserId());
    assertNotNull(rating.getRatingId());
  }

  @Test
  public void testAssociation() {
    // Create and Save Entities
    Kitchen kitchen = new Kitchen();
    kitchen.setName("Test Kitchen");
    kitchenService.saveKitchen(kitchen);

    User user = new User();
    user.setUsername("Test User");
    userRepository.save(user);

    Rating rating = new Rating();
    rating.setRating(4);
    rating.setKitchen(kitchen);
    rating.setUser(user);
    ratingService.saveRating(rating);

    // Fetch Kitchen and Check Ratings
    Kitchen fetchedKitchen = kitchenRepository
            .findByKitchenId(kitchen.getKitchenId()).orElseThrow();
    assertEquals(1, fetchedKitchen.getRatings().size());

    // Fetch User and Check Ratings
    User fetchedUser = userRepository.findById(user.getUserId()).orElseThrow();
    assertEquals(1, fetchedUser.getRatings().size());
  }

  @Test
  public void testUpdateRating() {
    // Create and Save Entities
    Kitchen kitchen = new Kitchen();
    kitchen.setName("Test Kitchen");
    kitchenService.saveKitchen(kitchen);

    User user = new User();
    user.setUsername("Test User");
    userRepository.save(user);

    Rating rating = new Rating();
    rating.setRating(4);
    rating.setKitchen(kitchen);
    rating.setUser(user);
    ratingService.saveRating(rating);

    // Update Rating
    rating.setRating(5);
    ratingService.updateRating(rating, rating.getRatingId());

    // Fetch and Verify Update
    Rating updatedRating = ratingRepository.findById(rating.getRatingId()).orElseThrow();
    assertEquals(5, updatedRating.getRating());
  }

  @Test
  public void testDeleteUser() {
    // Create and Save Entities
    Kitchen kitchen = new Kitchen();
    kitchen.setName("Test Kitchen");
    kitchenService.saveKitchen(kitchen);

    User user = new User();
    user.setUsername("Test User");
    userRepository.save(user);

    Rating rating = new Rating();
    rating.setRating(4);
    rating.setKitchen(kitchen);
    rating.setUser(user);
    ratingService.saveRating(rating);
    // Delete User
    userRepository.deleteById(user.getUserId());

    assertFalse(ratingRepository.findById(rating.getRatingId()).isPresent());

    Kitchen fetchedKitchen = kitchenRepository.findById(kitchen.getKitchenId()).orElseThrow();
    assertNotNull(fetchedKitchen);
  }

  @Test
  public void testDeleteKitchen() {
    // Create and Save Entities
    Kitchen kitchen = new Kitchen();
    kitchen.setName("Test Kitchen");
    kitchenService.saveKitchen(kitchen);

    User user = new User();
    user.setUsername("Test User");
    userRepository.save(user);

    Rating rating = new Rating();
    rating.setRating(4);
    rating.setKitchen(kitchen);
    rating.setUser(user);
    ratingService.saveRating(rating);
    // Delete Kitchen
    kitchenService.deleteKitchen(kitchen.getKitchenId());
    // Verify Rating is Removed (if cascade delete is enabled)
    assertFalse(ratingRepository.findById(rating.getRatingId()).isPresent());
  }

}
