package dev.teamproject.service.impl;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.User;
import dev.teamproject.repository.KitchenRepository;
import dev.teamproject.repository.RatingRepository;
import dev.teamproject.repository.UserRepository;
import dev.teamproject.service.RatingService;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the RatingService interface.
 * This service contains the logic related to getting ratings info.
 */
@Primary
@Service
@Transactional
public class RatingServiceImpl implements RatingService {

  private final RatingRepository ratingRepository;
  private final KitchenRepository kitchenRepository;
  private final UserRepository userRepository;

  /**
   * Constructor to autowire dependency of this bean.
   *
   * @param kitchenRepository kitchen Repository Bean
   * @param ratingRepository rating Repository Bean
   * @param userRepository user Repository Bean
   */
  @Autowired
  public RatingServiceImpl(RatingRepository ratingRepository, KitchenRepository kitchenRepository,
                           UserRepository userRepository) {
    this.ratingRepository = ratingRepository;
    this.kitchenRepository = kitchenRepository;
    this.userRepository = userRepository;
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
    Kitchen kitchen = rating.getKitchen();
    if (kitchen != null) {
      //rating.setKitchen(kitchen);
      kitchen.getRatings().add(rating);
      kitchen.updateAverageRating();
    }
    User user = rating.getUser();
    if (user != null) {
      user.getRatings().add(rating);
    }
    // Save the rating to the database
    return ratingRepository.save(rating);
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
    return ratingRepository.findByKitchen_KitchenId(kitchenId);
  }

  /**
   * Calculates the predicted waiting time for a given kitchen based on its ratings.
   *
   * @param kitchenId the ID of the kitchen
   * @return the average waiting time in seconds, or -1 if no ratings have waiting time data
   */
  public double getPredictedWaitingTime(Long kitchenId) {
    // Fetch all ratings for the given kitchen ID
    List<Rating> ratings = ratingRepository.findByKitchen_KitchenId(kitchenId);

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
    
    if (rating.getKitchen() != null && !rating.getKitchen().equals(toUpdate.getKitchen())) {
      toUpdate.setKitchen(rating.getKitchen());
    }
    if (rating.getUser() != null && !rating.getUser().equals(toUpdate.getUser())) {
      toUpdate.setUser(rating.getUser());
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
    Kitchen kitchen = rating.getKitchen();
    if (kitchen != null) {
      kitchen.getRatings().add(rating);
      kitchen.updateAverageRating();
      kitchenRepository.save(kitchen);
    }
    User user = rating.getUser();
    if (user != null) {
      user.getRatings().add(rating);
      userRepository.save(user);
    }
    // Save the rating to the database

    ratingRepository.save(toUpdate);
    return toUpdate;
  }

  @Override
  public void deleteRating(long id) {
    Optional<Rating> toDelete = ratingRepository.findById(id);
    if (toDelete.isEmpty()) {
      throw new RuntimeException("Rating does not exist with the given id: " + id);
    } else {
      Rating toDeleteRating = toDelete.get();
      Kitchen kitchen = toDeleteRating.getKitchen();
      if (kitchen != null) {
        kitchen.getRatings().remove(toDeleteRating);
        kitchen.updateAverageRating();
        kitchenRepository.save(kitchen);
      }
      User user = toDeleteRating.getUser();
      if (user != null) {
        user.getRatings().remove(toDeleteRating);
        userRepository.save(user);
      }
      // Save the rating to the database
      ratingRepository.deleteById(id);
    }
  }


}
