package dev.teamproject.service;

import dev.teamproject.model.Rating;
import java.util.List;

/**
 * Interface for Rating Service.
 * This interface defines the contract for rating operations.
 * this interface will provide specific logic regarding retrieving
 *  various info about ratings.
 */
public interface RatingService {
  Rating saveRating(Rating rating);

  List<Rating> getAllRatings();

  List<Rating> getKitchenRatings(Long kitchenId);

  double getPredictedWaitingTime(Long kitchenId);

  Rating updateRating(Rating rating, long id);

  void deleteRating(long id);
}
