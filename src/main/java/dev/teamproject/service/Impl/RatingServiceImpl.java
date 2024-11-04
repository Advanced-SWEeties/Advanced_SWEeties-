package dev.teamproject.service.impl;

import dev.teamproject.model.Rating;
import dev.teamproject.repository.RatingRepository;
import dev.teamproject.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
