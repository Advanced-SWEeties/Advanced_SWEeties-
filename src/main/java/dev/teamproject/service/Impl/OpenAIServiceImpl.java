package dev.teamproject.service.impl;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.UserLocation;
import dev.teamproject.service.OpenAIService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIServiceImpl implements OpenAIService {

  private final ChatModel chatModel;

  public OpenAIServiceImpl(ChatModel chatModel) {
    this.chatModel = chatModel;
  }

  @Value("classpath:templates/get-kitchen-recommendation.st")
  private Resource getKitchenRecommendationTemplate;

  @Override
  public Map<String,String> getKitchenRecommendation(
      List<Kitchen> allKitchens,
      UserLocation location,
      String disabilityStatus,
      String mealHours) {
    PromptTemplate promptTemplate = new PromptTemplate(getKitchenRecommendationTemplate);
    Prompt prompt = promptTemplate.create(Map.of(
        "allKitchens", allKitchens,
        "location", location,
        "disabilityStatus", disabilityStatus,
        "mealHours", mealHours));
    ChatResponse answer = chatModel.call(prompt);

    Map<String, String> response = new HashMap<>();
    response.put("answer", answer.getResult().getOutput().getContent());
    return response;
  }
}
