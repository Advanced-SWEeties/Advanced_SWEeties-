package dev.teamproject.service.impl;

import dev.teamproject.model.Rating;
import dev.teamproject.repository.RatingRepository;
import dev.teamproject.service.RatingService;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Implementation of the RatingService interface.
 * This service contains the logic related to getting ratings info.
 */
@Primary
@Service
public class RatingServiceImpl implements RatingService {

  private final RatingRepository ratingRepository;

  @Autowired
  public RatingServiceImpl(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  /**
   * Adds a new rating for a given kitchen.
   *
   * @param rating the Rating object containing the user's feedback
   * @return the saved Rating object
   */
  public Rating saveRating(Rating rating) {
    // rating as an Integer in rating should be between 1 and 5
    if (rating.getRating() < 1 || rating.getRating() > 5) {
      throw new IllegalArgumentException("Rating must be between 1 and 5");
    }

    // Save the rating to the database
    Rating savedRating = ratingRepository.save(rating);
    return savedRating;
  }


  public List<Rating> getAllRatings() {
    return ratingRepository.findAll();
  }
  
  /**
   * Retrieves all ratings for a given kitchen.
   *
   * @param kitchenId the ID of the kitchen
   * @return a list of ratings for the specified kitchen
   */
  public List<Rating> getKitchenRatings(Long kitchenId) {
    return ratingRepository.findByKitchenId(kitchenId);
  }

  /**
   * Calculates the predicted waiting time for a given kitchen based on its ratings.
   *
   * @param kitchenId the ID of the kitchen
   * @return the average waiting time in seconds, or -1 if no ratings have waiting time data
   */
  public double getPredictedWaitingTime(Long kitchenId) {
    // Fetch all ratings for the given kitchen ID
    List<Rating> ratings = ratingRepository.findByKitchenId(kitchenId);

    // Calculate the average waitSec
    // Filter out ratings with null waitSec
    Stream<Rating> filteredRatings = ratings.stream()
        .filter(rating -> rating.getWaitSec() != null);

    // Extract waitSec values
    LongStream waitTimes = filteredRatings
        .mapToLong(Rating::getWaitSec);

    // Calculate the average
    OptionalDouble averageWaitTime = waitTimes.average();


    // Return the average wait time if present, otherwise return -1
    return averageWaitTime.orElse(-1);
  }

  @Override
  public Rating updateRating(Rating rating, long id) {
    Rating toUpdate = ratingRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Rating does not exist with id: " + id));
    
    if (rating.getKitchenId() != null && !rating.getKitchenId().equals(toUpdate.getKitchenId())) {
      toUpdate.setKitchenId(rating.getKitchenId());
    }
    if (rating.getUserId() != null && !rating.getUserId().equals(toUpdate.getUserId())) {
      toUpdate.setUserId(rating.getUserId());
    }
    if (rating.getWaitSec() != null && !rating.getWaitSec().equals(toUpdate.getWaitSec())) {
      toUpdate.setWaitSec(rating.getWaitSec());
    }
    if (rating.getUserName() != null && !rating.getUserName().equals(toUpdate.getUserName())) {
      toUpdate.setUserName(rating.getUserName());
    }
    if (rating.getRating() != null && !rating.getRating().equals(toUpdate.getRating())) {
      if (rating.getRating() < 1 || rating.getRating() > 5) {
        throw new IllegalArgumentException("Rating must be between 1 and 5");
      }
      toUpdate.setRating(rating.getRating());
    }
    if (rating.getComments() != null && !rating.getComments().equals(toUpdate.getComments())) {
      toUpdate.setComments(rating.getComments());
    }
    if (rating.getCommentUrl() != null 
        && !rating.getCommentUrl().equals(toUpdate.getCommentUrl())) {
      toUpdate.setCommentUrl(rating.getCommentUrl());
    }
    if (rating.getPublishTime() != null 
        && !rating.getPublishTime().equals(toUpdate.getPublishTime())) {
      toUpdate.setPublishTime(rating.getPublishTime());
    }
    if (rating.getRelativeTime() != null 
        && !rating.getRelativeTime().equals(toUpdate.getRelativeTime())) {
      toUpdate.setRelativeTime(rating.getRelativeTime());
    }

    ratingRepository.save(toUpdate);
    return toUpdate;
  }

  @Override
  public void deleteRating(long id) {
    Optional<Rating> toDelete = ratingRepository.findById(id);
    if (toDelete.isEmpty()) {
      throw new RuntimeException("Rating does not exist with the given id: " + id);
    }
    ratingRepository.deleteById(id);
  }


}
