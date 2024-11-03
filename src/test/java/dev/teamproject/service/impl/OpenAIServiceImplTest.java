package dev.teamproject.service.impl;

import dev.teamproject.service.OpenAIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenAIServiceImplTest {

  @Autowired
  OpenAIService openAIService;

  @Test
  void getKitchenRecommendation() {
    Map answer = openAIService.getKitchenRecommendation("England");
    System.out.println(answer);
  }

}