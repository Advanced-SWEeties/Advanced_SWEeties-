package dev.teamproject.repository;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.User;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Unit testing for Rating Repository.
 */
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class RatingRepositoryUnitTests {

  @Autowired
  private RatingRepository ratingRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private KitchenRepository kitchenRepository;

  private Kitchen kitchen;
  private User user1;

  /**
   * Set up kitchen and user before each test to ensure consistency.
   */
  @BeforeEach
  @Transactional
  @Rollback(value = false)
  public void setupKitchenAndUser() {
    kitchen = Kitchen.builder()
            .name("Kitchen1")
            .address("some place")
            .contactPhone("1234567890")
            .build();
    kitchen = kitchenRepository.save(kitchen);

    user1 = new User();
    user1.setUsername("Test User 1");
    user1 = userRepository.save(user1);
  }

  @Test
  @DisplayName("Test 1: Save Rating Test")
  @Order(1)
  public void testSaveRating() {
    Rating rating = Rating.builder()
            .kitchen(kitchen)
            .user(user1)
            .userName("John Doe")
            .rating(5)
            .comments("Excellent service!")
            .build();

    Rating savedRating = ratingRepository.save(rating);
    Assertions.assertThat(savedRating.getRatingId()).isGreaterThan(0);
  }

  @Test
  @DisplayName("Test 2: Get Rating by ID Test")
  @Order(2)
  public void testGetRatingById() {
    Rating rating = Rating.builder()
            .kitchen(kitchen)
            .user(user1)
            .userName("John Doe")
            .rating(5)
            .comments("Excellent service!")
            .build();

    Rating savedRating = ratingRepository.save(rating);

    Optional<Rating> optionalRating = ratingRepository.findById(savedRating.getRatingId());
    Assertions.assertThat(optionalRating).isPresent();
    Assertions.assertThat(optionalRating.get().getRatingId()).isEqualTo(savedRating.getRatingId());
  }

  @Test
  @DisplayName("Test 3: Get Ratings by Kitchen ID Test")
  @Order(3)
  public void testGetRatingsByKitchenId() {
    Rating rating = Rating.builder()
            .kitchen(kitchen)
            .user(user1)
            .userName("John Doe")
            .rating(5)
            .comments("Excellent service!")
            .build();

    ratingRepository.save(rating);

    List<Rating> ratings = ratingRepository.findByKitchen_KitchenId(kitchen.getKitchenId());
    Assertions.assertThat(ratings).isNotEmpty();
    Assertions.assertThat(ratings.get(0).getKitchen().getKitchenId())
            .isEqualTo(kitchen.getKitchenId());
  }

  @Test
  @DisplayName("Test 4: Get All Ratings Test")
  @Order(4)
  public void testGetAllRatings() {
    Rating rating = Rating.builder()
            .kitchen(kitchen)
            .user(user1)
            .userName("John Doe")
            .rating(5)
            .comments("Excellent service!")
            .build();

    ratingRepository.save(rating);

    List<Rating> ratings = ratingRepository.findAll();
    Assertions.assertThat(ratings.size()).isGreaterThan(0);
  }

  @Test
  @DisplayName("Test 5: Update Rating Test")
  @Order(5)
  @Rollback(value = false)
  public void testUpdateRating() {
    Rating rating = Rating.builder()
            .kitchen(kitchen)
            .user(user1)
            .userName("John Doe")
            .rating(5)
            .comments("Excellent service!")
            .build();

    Rating savedRating = ratingRepository.save(rating);

    savedRating.setComments("Updated comment!");
    Rating updatedRating = ratingRepository.save(savedRating);

    Assertions.assertThat(updatedRating.getComments()).isEqualTo("Updated comment!");
  }

  @Test
  @DisplayName("Test 6: Delete Rating Test")
  @Order(6)
  @Rollback(value = false)
  public void testDeleteRating() {
    Rating rating = Rating.builder()
            .kitchen(kitchen)
            .user(user1)
            .userName("John Doe")
            .rating(5)
            .comments("Excellent service!")
            .build();

    Rating savedRating = ratingRepository.save(rating);

    ratingRepository.deleteById(savedRating.getRatingId());
    Optional<Rating> ratingOptional = ratingRepository.findById(savedRating.getRatingId());
    Assertions.assertThat(ratingOptional).isEmpty();
  }
}
