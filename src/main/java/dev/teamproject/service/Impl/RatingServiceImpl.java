package dev.teamproject.service.impl;

import dev.teamproject.model.Rating;
import dev.teamproject.repository.RatingRepository;
import dev.teamproject.service.RatingService;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.LongStream;

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

  public List<Rating> getAllRatings() {
    return ratingRepository.findAll();
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
}
