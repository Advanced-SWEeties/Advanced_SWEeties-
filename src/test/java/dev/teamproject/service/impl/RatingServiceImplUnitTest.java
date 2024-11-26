package dev.teamproject.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.User;
import dev.teamproject.repository.KitchenRepository;
import dev.teamproject.repository.RatingRepository;
import dev.teamproject.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit testing for Rating Service.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RatingServiceImplUnitTest {

  @Mock
  private RatingRepository ratingRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private KitchenRepository kitchenRepository;

  @InjectMocks
  private RatingServiceImpl ratingService;

  private Rating rating1;
  private Rating rating2;
  private Rating rating3;

  private Kitchen kitchen1;
  private Kitchen kitchen2;
  private Kitchen kitchen3;

  private User user1;
  private User user2;
  private User user3;

  /**
   * Setup method to initialize the test data.
   */
  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    kitchen1 = new Kitchen();
    kitchen1.setKitchenId(1L);
    kitchen1.setName("Test Kitchen 1");
    kitchenRepository.save(kitchen1);

    user1 = new User();
    user1.setUserId(1L);
    user1.setUsername("Test User 1");
    userRepository.save(user1);

    kitchen2 = new Kitchen();
    kitchen2.setKitchenId(2L);
    kitchen2.setName("Test Kitchen 2");
    kitchenRepository.save(kitchen2);

    user2 = new User();
    user2.setUserId(2L);
    user2.setUsername("Test User 2");
    userRepository.save(user2);

    kitchen3 = new Kitchen();
    kitchen3.setKitchenId(1L);
    kitchen3.setName("Test Kitchen 3");
    kitchenRepository.save(kitchen3);

    user3 = new User();
    user3.setUserId(3L);
    user3.setUsername("Test User 3");
    userRepository.save(user3);

    rating1 = Rating.builder()
        .ratingId(1L)
        .kitchen(kitchen1)
        .user(user1)
        .rating(5)
        .waitSec(120L)
        .comments("Great!")
        .build();

    rating2 = Rating.builder()
        .ratingId(2L)
        .kitchen(kitchen2)
        .user(user2)
        .rating(4)
        .waitSec(150L)
        .comments("Good.")
        .build();

    rating3 = Rating.builder()
        .kitchen(kitchen3)
        .kitchen(kitchen3)
        .user(user3)
        .rating(3)
        .waitSec(200L)
        .comments("Average.")
        .build();
  }

  @Test
  @Order(1)
  public void saveRatingTest() {
    given(ratingRepository.save(any(Rating.class))).willReturn(rating1);

    Rating savedRating = ratingService.saveRating(rating1);

    assertThat(savedRating).isNotNull();
    assertEquals("Test User 1", savedRating.getUserName());
    verify(ratingRepository, times(1)).save(rating1);
  }

  @Test
  @Order(2)
  public void getAllRatingsTest() {

    List<Rating> ratings = Arrays.asList(rating1, rating2);
    given(ratingRepository.findAll()).willReturn(ratings);

    List<Rating> allRatings = ratingService.getAllRatings();

    assertThat(allRatings).isNotNull();
    assertEquals(2, allRatings.size());
  }

  @Test
  @Order(3)
  public void getPredictedWaitingTimeTest() {
    given(ratingRepository.findByKitchen_KitchenId(1L)).willReturn(Arrays.asList(rating1, rating2));

    double averageWaitTime = ratingService.getPredictedWaitingTime(1L);

    assertEquals(135.0, averageWaitTime);
  }

  @Test
  @Order(4)
  public void getPredictedWaitingTimeTest_NoRatings() {

    given(ratingRepository.findByKitchen_KitchenId(1L)).willReturn(Collections.emptyList());

    double averageWaitTime = ratingService.getPredictedWaitingTime(1L);
    
    assertEquals(-1, averageWaitTime);
  }

  @Test
  @Order(5)
  public void getPredictedWaitingTimeTest_NullWaitSec() {
    User user4 = new User();
    user4.setUserId(4L);
    user4.setUsername("Test User 4");

    Rating ratingWithNullWaitSec = Rating.builder()
        .ratingId(4L)
        .kitchen(kitchen1)
        .user(user4)
        .rating(5)
        .waitSec(null)
        .build();

    given(ratingRepository.findByKitchen_KitchenId(1L))
        .willReturn(Arrays.asList(rating1, ratingWithNullWaitSec));

    double averageWaitTime = ratingService.getPredictedWaitingTime(1L);
    assertEquals(120.0, averageWaitTime); // Only considers ratings with non-null waitSec
  }

  // Test for invalid rating
  @Test
  @Order(6)
  public void saveRatingTest_InvalidRating() {
    Rating invalidRating = Rating.builder()
        .kitchen(kitchen2)
        .user(user2)
        .rating(6) // Invalid rating (greater than 5)
        .build();

    // Now we verify the exception
    assertThrows(Exception.class, () -> ratingService.saveRating(invalidRating));
    verify(ratingRepository, times(0)).save(any(Rating.class)); // Should not save
  }

  @Test
  @Order(7)
  public void getKitchenRatingsTest() {
    List<Rating> kitchenRatings = Arrays.asList(rating1, rating2);
    given(ratingRepository.findByKitchen_KitchenId(1L)).willReturn(kitchenRatings);

    List<Rating> ratings = ratingService.getKitchenRatings(1L);

    assertThat(ratings).isNotNull();
    assertEquals(2, ratings.size());
    assertEquals(1L, ratings.get(0).getKitchen().getKitchenId());
    assertEquals("Test User 1", ratings.get(0).getUserName());
    verify(ratingRepository, times(1)).findByKitchen_KitchenId(1L);
  }

  @Test
  @Order(8)
  public void updateRatingTest() {
    User user1Updated = new User();
    user1Updated.setUserId(3L);
    user1Updated.setUsername("Test User 1 updated");
    Rating updatedRating = Rating.builder()
        .ratingId(1L)
        .kitchen(kitchen1)
        .user(user1Updated)
        .rating(4)
        .waitSec(100L)
        .comments("Updated Comment")
        .commentUrl("http://hellohellotest.com")  // Adding new URL
        .publishTime("2024-11-24T12:00:00")  // Adding publish time
        .relativeTime("2 days ago")  // Adding relative time
        .build();

    given(ratingRepository.findById(1L)).willReturn(Optional.of(rating1));
    given(ratingRepository.save(any(Rating.class))).willReturn(updatedRating);

    Rating result = ratingService.updateRating(updatedRating, 1L);
    
    assertThat(result).isNotNull();
    assertEquals("Test User 1 updated", result.getUserName());
    assertEquals("Test User 1 updated", result.getUser().getUsername());
    assertEquals(4, result.getRating());
    assertEquals(100L, result.getWaitSec());
    assertEquals("Updated Comment", result.getComments());
    assertEquals("http://hellohellotest.com", result.getCommentUrl());  // Verifying the new URL
    assertEquals("2024-11-24T12:00:00", result.getPublishTime());  // Verifying the new publish time
    assertEquals("2 days ago", result.getRelativeTime());  // Verifying the new relative time
    verify(ratingRepository, times(1)).findById(1L);
    verify(ratingRepository, times(1)).save(any(Rating.class));
  }

  
  @Test
  @Order(9)
  public void updateRatingTest_RatingNotFound() {
    User user1Updated = new User();
    user1Updated.setUserId(3L);
    user1Updated.setUsername("Test User 1 updated");
    Rating updatedRating = Rating.builder()
        .ratingId(1L)
        .kitchen(kitchen1)
        .user(user1Updated)
        .rating(4)
        .waitSec(100L)
        .comments("Updated Comment")
        .build();

    given(ratingRepository.findById(1L)).willReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class, 
        () -> ratingService.updateRating(updatedRating, 1L));
    assertEquals("Rating does not exist with id: 1", exception.getMessage());
    verify(ratingRepository, times(1)).findById(1L);
    verify(ratingRepository, times(0)).save(any(Rating.class));
  }
 
  @Test
  @Order(10)
  public void updateRatingTest_InvalidRating() {
    Rating updatedRating = new Rating();
    updatedRating.setRating(6);  // Invalid rating

    given(ratingRepository.findById(1L)).willReturn(Optional.of(rating1));

    Exception exception = assertThrows(IllegalArgumentException.class, 
        () -> ratingService.updateRating(updatedRating, 1L));

    assertEquals("Rating must be between 1 and 5", exception.getMessage());
    verify(ratingRepository, never()).save(any(Rating.class));
  }
  
  @Test
  public void updateRating_MinRating() {
    Rating rating = new Rating();
    rating.setRating(1);

    given(ratingRepository.findById(1L)).willReturn(Optional.of(rating));
    given(ratingRepository.save(any(Rating.class))).willReturn(rating);

    Rating result = ratingService.updateRating(rating, 1L);
    
    assertEquals(1, result.getRating());
  }
  
  @Test
  public void updateRating_MaxRating() {
    Rating rating = new Rating();
    rating.setRating(5);

    given(ratingRepository.findById(1L)).willReturn(Optional.of(rating));
    given(ratingRepository.save(any(Rating.class))).willReturn(rating);

    Rating result = ratingService.updateRating(rating, 1L);
    
    assertEquals(5, result.getRating());
  }

  @Test
  @Order(10)
  public void deleteRatingTest() {
    given(ratingRepository.findById(1L)).willReturn(Optional.of(rating1));
    ratingService.deleteRating(1L);
    verify(ratingRepository, times(1)).findById(1L);
    verify(ratingRepository, times(1)).deleteById(1L);
  }

  @Test
  @Order(11)
  public void deleteRatingTest_RatingNotFound() {
    
    given(ratingRepository.findById(1L)).willReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, 
        () -> ratingService.deleteRating(1L));
    assertEquals("Rating does not exist with the given id: 1", exception.getMessage());
    verify(ratingRepository, times(1)).findById(1L);
    verify(ratingRepository, times(0)).deleteById(1L);
  }


}
