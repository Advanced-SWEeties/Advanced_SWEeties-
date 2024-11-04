package dev.teamproject.service.impl;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.UserLocation;
import dev.teamproject.service.OpenAIService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the OpenAIService interface.
 * This service contains the various logic related to operations which leverage AI capabilities.
 */
@Primary
@Service
public class OpenAIServiceImpl implements OpenAIService {

  private final ChatModel chatModel;

  public OpenAIServiceImpl(ChatModel chatModel) {
    this.chatModel = chatModel;
  }

  @Value("classpath:templates/get-kitchen-recommendation.st")
  private Resource getKitchenRecommendationTemplate;

  /**
   * Generates kitchen recommendations based on available kitchens, user ratings,
   * user location, disability accessibility needs, and meal time preferences.
   * Utilizes AI(ChatGpt model) to provide personalized suggestions.
   *
   * @param allKitchens A list of all available Kitchen objects. This list is used to find
   *                    the most suitable kitchens based on the user's requirements.
   * @param allRatings A list of Rating objects for all kitchens
   * @param location The UserLocation object representing the user's geographical location
   * @param disabilityStatus A string indicating the user's disability status
   * @param mealHours A string specifying the desired meal time
   * @return A map containing recommendation results, with keys as the recommendation type
   *         and values as corresponding kitchen details. The map includes the generated response
   *         from the AI model.
   * @throws RuntimeException if there is an error in the AI model call or response processing.
   */
  @Override
  public Map<String,String> getKitchenRecommendation(
      List<Kitchen> allKitchens,
      List<Rating> allRatings,
      UserLocation location,
      String disabilityStatus,
      String mealHours) {

    PromptTemplate promptTemplate = new PromptTemplate(getKitchenRecommendationTemplate);
    Prompt prompt = promptTemplate.create(Map.of(
        "allKitchens", allKitchens,
        "allRatings", allRatings,
        "location", location,
        "disabilityStatus", disabilityStatus,
        "mealHours", mealHours));
    ChatResponse answer = chatModel.call(prompt);

    Map<String, String> response = new HashMap<>();
    response.put("answer", answer.getResult().getOutput().getContent());
    return response;
  }
}
