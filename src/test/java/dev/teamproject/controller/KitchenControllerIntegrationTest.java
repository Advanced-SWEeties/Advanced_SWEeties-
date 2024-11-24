package dev.teamproject.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.User;
import dev.teamproject.model.UserLocation;
import dev.teamproject.repository.KitchenRepository;
import dev.teamproject.repository.RatingRepository;
import dev.teamproject.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration Tests for KitchenController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class KitchenControllerIntegrationTest {

  @Autowired
  WebApplicationContext wac;

  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private KitchenRepository kitchenRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RatingRepository ratingRepository;

  private Kitchen kitchen1;
  private Kitchen kitchen2;

  private User user1;
  private User user2;

  private Rating rating1;
  private Rating rating2;

  private UserLocation userLocation;

  /**
   * Set up test data before each test.
   */
  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac)
        .build();

    user1 = new User();
    user1.createAccount("user1", "password1");
    user1 = userRepository.save(user1);

    user2 = new User();
    user2.createAccount("user2", "password2");
    user2 = userRepository.save(user2);

    kitchen1 = Kitchen.builder()
        .name("Kitchen1")
        .address("116 St NY")
        .contactPhone("1234567890")
        .latitude(40.8075)
        .longitude(-73.9626)
        .rating(3.5)
        .build();
    kitchen1 = kitchenRepository.save(kitchen1);

    kitchen2 = Kitchen.builder()
        .name("Kitchen2")
        .address("College point NY")
        .contactPhone("1111112222")
        .latitude(40.7128)
        .longitude(-74.0060)
        .rating(4.5)
        .build();
    kitchen2 = kitchenRepository.save(kitchen2);

    userLocation = UserLocation.builder()
        .address("Columbia University")
        .latitude(40.8075)
        .longitude(-73.9626)
        .build();

    rating1 = Rating.builder()
        .kitchen(kitchen1)
        .user(user1)
        .rating(5)
        .waitSec(120L)
        .comments("good service!")
        .publishTime("2024-11-24T12:51:04")
        .relativeTime("21 hours ago")
        .build();
    rating1 = ratingRepository.save(rating1);

    rating2 = Rating.builder()
        .kitchen(kitchen2)
        .user(user2)
        .rating(4)
        .waitSec(100L)
        .comments("hmmmmmmmmm, not bad")
        .publishTime("2024-11-24T12:51:05")
        .relativeTime("3 hours ago")
        .build();
    rating2 = ratingRepository.save(rating2);
  }

  @Test
  public void testHomeEndpoint() throws Exception {
    mockMvc.perform(get("/api/"))
        .andExpect(status().isOk())
        .andExpect(content().string("Welcome to the Kitchen API!"));
  }

  @Test
  public void testAddKitchen() throws Exception {
    Kitchen newKitchen = Kitchen.builder()
        .name("Kitchen3")
        .address("new kitchen address")
        .contactPhone("1234567890")
        .build();

    mockMvc.perform(post("/api/kitchens/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newKitchen)))
        .andExpect(status().isCreated())
        .andExpect(content().string("New kitchen added successfully."));

    // Verify
    List<Kitchen> kitchens = kitchenRepository.findAll();
    assert (kitchens.size() == 3);
    assert (kitchens.stream().anyMatch(k -> k.getName().equals("Kitchen3")));
  }

  @Test
  public void testGetKitchenDetails() throws Exception {
    mockMvc.perform(get("/api/kitchens/details")
            .param("kitchenId", kitchen1.getKitchenId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(kitchen1.getName())))
        .andExpect(jsonPath("$.address", is(kitchen1.getAddress())))
        .andExpect(jsonPath("$.contactPhone", is(kitchen1.getContactPhone())));
  }

  @Test
  public void testGetKitchenDetailsInvalidId() throws Exception {
    mockMvc.perform(get("/api/kitchens/details")
            .param("kitchenId", "-11111"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testUpdateKitchen() throws Exception {
    kitchen1.setAddress("kitchen1 New Address");
    kitchen1.setContactPhone("1111111");

    mockMvc.perform(put("/api/kitchens/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(kitchen1)))
        .andExpect(status().isOk())
        .andExpect(content().string("Kitchen information updated successfully."));

    // Verify
    Optional<Kitchen> updatedKitchen = kitchenRepository.findById(kitchen1.getKitchenId());
    assert (updatedKitchen.isPresent());
    assert (updatedKitchen.get().getAddress().equals("kitchen1 New Address"));
    assert (updatedKitchen.get().getContactPhone().equals("1111111"));
  }


  @Test
  public void testUpdateKitchenNotFound() throws Exception {
    Kitchen nonExistentKitchen = Kitchen.builder()
        .kitchenId(999999L)
        .name("No kitchen")
        .address("No address")
        .contactPhone("0000000000")
        .build();

    mockMvc.perform(put("/api/kitchens/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nonExistentKitchen)))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Kitchen to update is not found"));
  }


  @Test
  public void testDeleteKitchen() throws Exception {
    mockMvc.perform(delete("/api/kitchens/delete")
            .param("kitchenId", kitchen1.getKitchenId().toString()))
        .andExpect(status().isOk())
        .andExpect(content().string("Kitchen deleted successfully."));

    // Verify
    Optional<Kitchen> deletedKitchen = kitchenRepository.findById(kitchen1.getKitchenId());
    assert (deletedKitchen.isEmpty());
  }


  @Test
  public void testDeleteKitchenNotFound() throws Exception {
    mockMvc.perform(delete("/api/kitchens/delete")
            .param("kitchenId", "999999999"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Kitchen to delete is not found"));
  }


  @Test
  public void testGetNearestKitchens() throws Exception {
    mockMvc.perform(get("/api/kitchens/nearest")
            .param("address", "Columbia University")
            .param("count", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$[0].name", is("Kitchen1")))
        .andExpect(jsonPath("$[1].name", is("Kitchen2")));
  }


  @Test
  public void testGetNearestKitchensInvalidAddress() throws Exception {
    mockMvc.perform(get("/api/kitchens/nearest")
            .param("address", "")
            .param("count", "1"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid parameters"));

  }

  @Test
  public void testGetNearestKitchensNegativeCount() throws Exception {
    mockMvc.perform(get("/api/kitchens/nearest")
            .param("address", "Columbia University")
            .param("count", "-1"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid parameters"));
  }


  @Test
  public void testGetNearestKitchensNoKitchensFound() throws Exception {

    ratingRepository.deleteAll();
    kitchenRepository.deleteAll();

    mockMvc.perform(get("/api/kitchens/nearest")
            .param("address", "Columbia University")
            .param("count", "1"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No kitchens found in the Mysql DB"));
  }


  @Test
  public void testGetTopRatedKitchens() throws Exception {
    mockMvc.perform(get("/api/kitchens/top-rated")
            .param("count", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$[0].name", is("Kitchen2")))
        .andExpect(jsonPath("$[1].name", is("Kitchen1")));
  }

  @Test
  public void testGetTopRatedKitchensInvalidCount() throws Exception {
    mockMvc.perform(get("/api/kitchens/top-rated")
            .param("count", "-1"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("invalid count: negative number"));
  }


  @Test
  public void testGetTopRatedKitchensNoKitchensFound() throws Exception {
    ratingRepository.deleteAll();
    kitchenRepository.deleteAll();

    mockMvc.perform(get("/api/kitchens/top-rated")
            .param("count", "5"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No kitchens found in the Mysql DB"));
  }


  @Test
  public void testGetKitchenRecommendation() throws Exception {
    mockMvc.perform(get("/api/kitchens/recommendation")
            .param("location", "Columbia University")
            .param("disabilityStatus", "disabled")
            .param("mealHours", "2PM"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("The most recommended kitchen is")))
        .andExpect(content().string(containsString("Kitchen1")));
  }


  @Test
  public void testGetKitchenRecommendationInvalidInputs() throws Exception {
    mockMvc.perform(get("/api/kitchens/recommendation")
            .param("location", "")
            .param("disabilityStatus", "disabled")
            .param("mealHours", "2PM"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid input"));

    mockMvc.perform(get("/api/kitchens/recommendation")
            .param("disabilityStatus", "disabled")
            .param("mealHours", "2PM"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/api/kitchens/recommendation")
            .param("location", "Columbia University")
            .param("disabilityStatus", "")
            .param("mealHours", "2PM"))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/api/kitchens/recommendation")
            .param("location", "Columbia University")
            .param("disabilityStatus", "disabled")
            .param("mealHours", ""))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid input"));
  }

  @Test
  public void testGetKitchenRecommendationNoKitchensFound() throws Exception {
    ratingRepository.deleteAll();
    kitchenRepository.deleteAll();

    mockMvc.perform(get("/api/kitchens/recommendation")
            .param("location", "Columbia University")
            .param("disabilityStatus", "disabled")
            .param("mealHours", "2PM"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No kitchens found in the Mysql DB"));
  }


  @Test
  public void testAddRating() throws Exception {
    Rating newRating = Rating.builder()
        .kitchen(kitchen1)
        .user(user1)
        .rating(5)
        .waitSec(90L)
        .comments("good!")
        .publishTime("2024-11-24T13:15:38")
        .relativeTime("1 hour ago")
        .build();

    mockMvc.perform(post("/api/ratings/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newRating)))
        .andExpect(status().isCreated())
        .andExpect(content().string("Rating added successfully."));

    // Verify
    List<Rating> ratings = ratingRepository.findAll();
    assert (ratings.size() == 3);
    assert (ratings.stream().anyMatch(r -> r.getComments().equals("good!")));
  }


  @Test
  public void testAddRatingInvalidData() throws Exception {
    Rating invalidRating = Rating.builder()
        .kitchen(null)
        .user(user1)
        .rating(null)
        .waitSec(null)
        .comments("hmmm")
        .build();

    mockMvc.perform(post("/api/ratings/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRating)))
        .andExpect(status().isInternalServerError());
  }


  @Test
  public void testUpdateRating() throws Exception {
    rating1.setComments("Updated rating1");

    mockMvc.perform(put("/api/ratings/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rating1)))
        .andExpect(status().isOk())
        .andExpect(content().string("Rating updated successfully."));

    // Verify
    Optional<Rating> updatedRating = ratingRepository.findById(rating1.getRatingId());
    assert (updatedRating.isPresent());
    assert (updatedRating.get().getComments().equals("Updated rating1"));
  }


  @Test
  public void testUpdateRatingNotFound() throws Exception {
    Rating testRating = Rating.builder()
        .ratingId(999L)
        .kitchen(kitchen1)
        .user(user1)
        .rating(3)
        .waitSec(6L)
        .comments("hmmm.")
        .publishTime("2024-11-24T13:15:38")
        .relativeTime("1 hour ago")
        .build();

    mockMvc.perform(put("/api/ratings/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRating)))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Rating to update is not found"));
  }


  @Test
  public void testDeleteRatingNotFound() throws Exception {
    mockMvc.perform(delete("/api/ratings/delete")
            .param("ratingId", "999"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Rating to delete is not found"));
  }

  @Test
  public void testRetrieveKitchenRatings() throws Exception {
    mockMvc.perform(get("/api/ratings/retrieveKitchenRatings")
            .param("kitchenId", kitchen1.getKitchenId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].userName", is(user1.getUsername())))
        .andExpect(jsonPath("$[0].comments", is(rating1.getComments())));
  }


  @Test
  public void testRetrieveKitchenRatingsNoRatings() throws Exception {
    Kitchen kitchenNoRatings = Kitchen.builder()
        .name("KitchenNoRatings")
        .address("789 Pine St")
        .contactPhone("2223334444")
        .build();
    kitchenNoRatings = kitchenRepository.save(kitchenNoRatings);

    mockMvc.perform(get("/api/ratings/retrieveKitchenRatings")
            .param("kitchenId", kitchenNoRatings.getKitchenId().toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No ratings found for the specified kitchen."));
  }


  @Test
  public void testRetrieveKitchenRatingsInvalidKitchen() throws Exception {
    mockMvc.perform(get("/api/ratings/retrieveKitchenRatings")
            .param("kitchenId", "-11111"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No ratings found for the specified kitchen."));
  }


  @Test
  public void testGetPredictedWaitTime() throws Exception {
    List<Rating> ratings = ratingRepository.findByKitchen_KitchenId(kitchen1.getKitchenId());

    OptionalDouble expectedWaitTime = ratings.stream()
        .filter(rating -> rating.getWaitSec() != null)
        .mapToLong(Rating::getWaitSec)
        .average();

    double finalExpectedWaitTime = expectedWaitTime.orElse(-1);

    mockMvc.perform(get("/api/kitchens/wait_time")
            .param("kitchenId", kitchen1.getKitchenId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.predicted_wait_time", is(finalExpectedWaitTime)));
  }




  @Test
  public void testGetPredictedWaitTimeNoRatings() throws Exception {
    Kitchen kitchenNoRatings = Kitchen.builder()
        .name("KitchenNoRatings")
        .address("789 Pine St")
        .contactPhone("2223334444")
        .build();
    kitchenNoRatings = kitchenRepository.save(kitchenNoRatings);

    mockMvc.perform(get("/api/kitchens/wait_time")
            .param("kitchenId", kitchenNoRatings.getKitchenId().toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No ratings with wait time found for this kitchen."));
  }


  @Test
  public void testGetPredictedWaitTimeInvalidKitchen() throws Exception {
    mockMvc.perform(get("/api/kitchens/wait_time")
            .param("kitchenId", "999"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No ratings with wait time found for this kitchen."));
  }
}
