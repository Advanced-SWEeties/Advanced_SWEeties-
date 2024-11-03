package dev.teamproject.service;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.UserLocation;

import java.util.List;
import java.util.Map;

public interface OpenAIService {

  Map<String,String> getKitchenRecommendation(List<Kitchen> allKitchens, UserLocation location, String disabilityStatus, String mealHours);

}
