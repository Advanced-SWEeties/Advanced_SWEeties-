package dev.teamproject.service;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.UserLocation;

import java.util.List;
import java.util.Map;

/**
 * Interface for OpenAI Service.
 * This interface defines the contract for AI-related operations.
 * Implementations of this interface will provide specific logic regarding the
 * user's request which we will response via leveraging AI capabilities in the application.
 */
public interface OpenAIService {

  Map<String,String> getKitchenRecommendation(List<Kitchen> allKitchens, List<Rating> allRatings, UserLocation location, String disabilityStatus, String mealHours);

}
