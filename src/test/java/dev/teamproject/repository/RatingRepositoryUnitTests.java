package dev.teamproject.repository;

import dev.teamproject.model.Rating;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit testing for Rating Repository.
 */
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RatingRepositoryUnitTests {

  @Autowired
  private RatingRepository ratingRepository;

  @Test
  @DisplayName("Test 1: Save Rating Test")
  @Order(1)
  @Rollback(value = false)
  public void testSaveRating() {
    Rating rating = Rating.builder()
        .kitchenId(1L)
        .userId("user1")
        .userName("John Doe")
        .rating(5)
        .comments("Excellent service!")
        .build();

    Rating savedRating = ratingRepository.save(rating);

    System.out.println(savedRating);
    Assertions.assertThat(savedRating.getRatingId()).isGreaterThan(0);
  }

  @Test
  @DisplayName("Test 2: Get Rating by ID Test")
  @Order(2)
  public void testGetRatingById() {
    Rating rating = ratingRepository.findById(1L).get();

    System.out.println(rating);
    Assertions.assertThat(rating.getRatingId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Test 3: Get Ratings by Kitchen ID Test")
  @Order(3)
  public void testGetRatingsByKitchenId() {
    List<Rating> ratings = ratingRepository.findByKitchenId(1L);

    for (Rating rating : ratings) {
      System.out.println(rating);
    }

    Assertions.assertThat(ratings).isNotEmpty();
    Assertions.assertThat(ratings.get(0).getKitchenId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Test 4: Get All Ratings Test")
  @Order(4)
  public void testGetAllRatings() {
    List<Rating> ratings = ratingRepository.findAll();

    Assertions.assertThat(ratings.size()).isGreaterThan(0);
  }

  @Test
  @DisplayName("Test 5: Update Rating Test")
  @Order(5)
  @Rollback(value = false)
  public void testUpdateRating() {
    Rating rating = ratingRepository.findById(1L).get();

    rating.setComments("Updated comment!");
    Rating updatedRating = ratingRepository.save(rating);

    System.out.println(updatedRating);
    Assertions.assertThat(updatedRating.getComments()).isEqualTo("Updated comment!");
  }

  @Test
  @DisplayName("Test 6: Delete Rating Test")
  @Order(6)
  @Rollback(value = false)
  public void testDeleteRating() {
    ratingRepository.deleteById(1L);
    Optional<Rating> ratingOptional = ratingRepository.findById(1L);

    Assertions.assertThat(ratingOptional).isEmpty();
  }
}
