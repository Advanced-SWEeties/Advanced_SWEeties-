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

  List<Rating> getAllRatings();
  
  public double getPredictedWaitingTime(Long kitchenId);
}
