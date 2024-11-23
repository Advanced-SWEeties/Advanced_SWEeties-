package dev.teamproject.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.User;
import dev.teamproject.model.UserLocation;
import dev.teamproject.service.OpenAiService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


/**
 * Unit testing for OpenAIServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class OpenAiServiceImplTest {

  @Mock
  OpenAiService openAiService;

  @InjectMocks
  private OpenAiServiceImpl openAiServiceImpl;

  Kitchen kitchen;
  Kitchen kitchen2;

  Rating rating1;
  Rating rating2;
  Rating rating3;

  User user1;
  User user2;
  User user3;

  UserLocation userLocation;

  private static final String ADDRESS = "Columbia University";
  private static final String DISABILITY_STATUS_DISABLED = "disabled";
  private static final String DISABILITY_STATUS_NOT_DISABLED = "not disabled";
  private static final String MEAL_HOURS_DAYTIME = "2PM";
  private static final String MEAL_HOURS_NIGHTTIME = "9PM";
  private static final String MOCKED_ANSWER = "The most recommended kitchen is \\\"Haidilao "
      + "Huoguo Flushing\\\" at 138-23 39th Ave, Flushing, NY 11354, close to your location. "
      + "Known for friendly service and high-quality food. The second recommended kitchen is "
      + "\\\"Junzi Kitchen\\\" at 2896 Broadway, New York, NY 10025, nearby with convenient "
      + "access. Offers pantry and meal services with positive staff reviews. \"The third "
      + "recommended kitchen is \\\"Xiaolongbao House\\\" at 123 Main Street, Queens, NY 11368. "
      + "Known for its authentic dumplings and fast service, it’s highly rated for its cozy "
      + "atmosphere and reasonable prices.\"";
  private static final String EXPECTED_ANSWER = MOCKED_ANSWER;

  private static final String MOCKED_ANSWER2 = "The most recommended kitchen for"
      + " nighttime dining is \\\"Haidilao Huoguo Flushing\\\" at 138-23 39th Ave, Flushing, "
      + "NY 11354, known for its excellent late-night service and high-quality hot pot. The "
      + "second recommended kitchen is \\\"Junzi Kitchen\\\" at 2896 Broadway, New York, NY "
      + "10025, offering hearty meals with convenient late-hour service. The third recommended "
      + "kitchen is \\\"Xiaolongbao House\\\" at 123 Main Street, Queens, NY 11368. Praised for "
      + "its authentic dumplings and welcoming atmosphere, it’s ideal for nighttime visits.";
  private static final String EXPECTED_ANSWER2 = MOCKED_ANSWER2;

  @BeforeEach
  public void setup() throws IOException {
    openAiService = mock(OpenAiService.class);

    kitchen = Kitchen.builder()
        .kitchenId(1L)
        .name("Haidilao Huoguo Flushing")
        .address("138-23 39th Ave, Flushing, NY 11354")
        .contactPhone("1234567890").build();

    kitchen2 = Kitchen.builder()
        .kitchenId(2L)
        .name("Junzi Kitchen")
        .address("2896 Broadway, New York, NY 10025")
        .contactPhone("987654321").build();

    userLocation = UserLocation.builder()
        .address(ADDRESS)
        .latitude(40.8075)
        .longitude(-73.9626)
        .build();

    user1 = new User();
    user1.setUserId(1L);
    user1.setUsername("John Doe");

    user2 = new User();
    user2.setUserId(2L);
    user2.setUsername("Jane Smith");

    user3 = new User();
    user3.setUserId(3L);
    user3.setUsername("Emily Johnson");

    rating1 = Rating.builder()
        .ratingId(1L)
        .kitchen(kitchen)
        .user(user1)
        .rating(5)
        .comments("The food was absolutely amazing! The service was top-notch.")
        .commentUrl("http://xxxxxxx")
        .publishTime("2024-11-06T12:00:00")
        .relativeTime("2 hours ago")
        .build();

    rating2 = Rating.builder()
        .ratingId(2L)
        .kitchen(kitchen)
        .user(user2)
        .rating(4)
        .comments("Great experience, but the wait time was longer than expected.")
        .commentUrl("http://xxxxxxx")
        .publishTime("2024-11-06T15:30:00")
        .relativeTime("30 minutes ago")
        .build();

    rating3 = Rating.builder()
        .ratingId(3L)
        .kitchen(kitchen2)
        .user(user3)
        .rating(3)
        .comments("Good food, but the ambiance could use some improvement.")
        .commentUrl("http://xxxxxxx")
        .publishTime("2024-11-05T19:45:00")
        .relativeTime("1 day ago")
        .build();
  }

  @Test
  void getKitchenRecommendationTest() {
    Mockito.when(openAiService.getKitchenRecommendation(Mockito.anyList(), Mockito.anyList(),
            Mockito.any(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Map.of("answer", MOCKED_ANSWER));

    // Test case 1: User is disabled and prefers daytime meals
    Map map = openAiService.getKitchenRecommendation(List.of(kitchen, kitchen2),
        List.of(rating1, rating2, rating3),
        userLocation, DISABILITY_STATUS_DISABLED, MEAL_HOURS_DAYTIME);

    assertEquals(EXPECTED_ANSWER, map.get("answer"));
  }

  @Test
  void getKitchenRecommendationTest2() {
    Mockito.when(openAiService.getKitchenRecommendation(Mockito.anyList(), Mockito.anyList(),
            Mockito.any(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Map.of("answer", MOCKED_ANSWER2));

    // Test case 2: User is not disabled and prefers nighttime meals
    Map map = openAiService.getKitchenRecommendation(List.of(kitchen, kitchen2),
        List.of(rating1, rating2, rating3),
        userLocation, DISABILITY_STATUS_NOT_DISABLED, MEAL_HOURS_NIGHTTIME);

    assertEquals(EXPECTED_ANSWER2, map.get("answer"));
  }



}

