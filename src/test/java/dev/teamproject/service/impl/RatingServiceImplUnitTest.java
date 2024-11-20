package dev.teamproject.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dev.teamproject.model.Rating;
import dev.teamproject.repository.RatingRepository;
import dev.teamproject.service.impl.RatingServiceImpl;
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

  @InjectMocks
  private RatingServiceImpl ratingService;

  private Rating rating1;
  private Rating rating2;
  private Rating rating3;

  /**
   * Setup method to initialize the test data.
   */
  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    rating1 = Rating.builder()
        .ratingId(1L)
        .kitchenId(1L)
        .userId("user1")
        .userName("Customer One")
        .rating(5)
        .waitSec(120L)
        .comments("Great!")
        .build();

    rating2 = Rating.builder()
        .ratingId(2L)
        .kitchenId(1L)
        .userId("user2")
        .userName("Customer Two")
        .rating(4)
        .waitSec(150L)
        .comments("Good.")
        .build();

    rating3 = Rating.builder()
        .ratingId(3L)
        .kitchenId(2L)
        .userId("user3")
        .userName("Customer Three")
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
    assertEquals("Customer One", savedRating.getUserName());
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
    given(ratingRepository.findByKitchenId(1L)).willReturn(Arrays.asList(rating1, rating2));

    double averageWaitTime = ratingService.getPredictedWaitingTime(1L);

    assertEquals(135.0, averageWaitTime);
  }

  @Test
  @Order(4)
  public void getPredictedWaitingTimeTest_NoRatings() {

    given(ratingRepository.findByKitchenId(1L)).willReturn(Collections.emptyList());

    double averageWaitTime = ratingService.getPredictedWaitingTime(1L);
    
    assertEquals(-1, averageWaitTime);
  }

  @Test
  @Order(5)
  public void getPredictedWaitingTimeTest_NullWaitSec() {
    Rating ratingWithNullWaitSec = Rating.builder()
        .ratingId(4L)
        .kitchenId(1L)
        .userId("user4")
        .userName("User Four")
        .rating(5)
        .waitSec(null)
        .build();

    given(ratingRepository.findByKitchenId(1L))
        .willReturn(Arrays.asList(rating1, ratingWithNullWaitSec));

    double averageWaitTime = ratingService.getPredictedWaitingTime(1L);
    assertEquals(120.0, averageWaitTime); // Only considers ratings with non-null waitSec
  }

  // Test for invalid rating
  @Test
  @Order(6)
  public void saveRatingTest_InvalidRating() {
    Rating invalidRating = Rating.builder()
        .kitchenId(1L)
        .userName("")
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
    given(ratingRepository.findByKitchenId(1L)).willReturn(kitchenRatings);

    List<Rating> ratings = ratingService.getKitchenRatings(1L);

    assertThat(ratings).isNotNull();
    assertEquals(2, ratings.size());
    assertEquals(1L, ratings.get(0).getKitchenId());
    assertEquals("Customer One", ratings.get(0).getUserName());
    verify(ratingRepository, times(1)).findByKitchenId(1L);
  }

  @Test
  @Order(8)
  public void updateRatingTest() {
    Rating updatedRating = Rating.builder()
        .ratingId(1L)
        .kitchenId(1L)
        .userId("user1_updated")
        .userName("Customer One Updated")
        .rating(4)
        .waitSec(100L)
        .comments("Updated Comment")
        .build();

    given(ratingRepository.findById(1L)).willReturn(Optional.of(rating1));
    given(ratingRepository.save(any(Rating.class))).willReturn(updatedRating);

    Rating result = ratingService.updateRating(updatedRating, 1L);
    
    assertThat(result).isNotNull();
    assertEquals("Customer One Updated", result.getUserName());
    assertEquals("user1_updated", result.getUserId());
    assertEquals(4, result.getRating());
    assertEquals(100L, result.getWaitSec());
    assertEquals("Updated Comment", result.getComments());
    verify(ratingRepository, times(1)).findById(1L);
    verify(ratingRepository, times(1)).save(any(Rating.class));
  }
  
  @Test
  @Order(9)
  public void updateRatingTest_RatingNotFound() {
    Rating updatedRating = Rating.builder()
        .ratingId(1L)
        .kitchenId(1L)
        .userId("user1_updated")
        .userName("Customer One Updated")
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
