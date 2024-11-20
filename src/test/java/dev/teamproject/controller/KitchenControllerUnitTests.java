package dev.teamproject.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.UserLocation;
import dev.teamproject.service.KitchenService;
import dev.teamproject.service.OpenAiService;
import dev.teamproject.service.RatingService;
import dev.teamproject.service.UserService;
import dev.teamproject.service.impl.RatingServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


/**
 * Unit testing for kitchen Controller.
 */
@WebMvcTest(KitchenController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KitchenControllerUnitTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private KitchenService kitchenService;

  @MockBean
  private UserService userService;

  @MockBean
  private OpenAiService openAiService;

  @MockBean
  private RatingService ratingService;

  @Autowired
  private ObjectMapper objectMapper;

  Kitchen kitchen;
  Kitchen kitchen2;

  Rating rating1;
  Rating rating2;

  UserLocation userLocation;

  private static final String ADDRESS = "Columbia University";
  private static final String DISABILITY_STATUS_DISABLED = "disabled";
  private static final String DISABILITY_STATUS_NOT_DISABLED = "not disabled";
  private static final String MEAL_HOURS_DAYTIME = "2PM";
  private static final String MEAL_HOURS_NIGHTTIME = "9PM";


  /**
   *  set up a kitchen object before each test.
   */
  @BeforeEach
  public void setup() {
    kitchen = Kitchen.builder()
            .kitchenId(1L)
            .name("Kitchen1")
            .address("some place")
            .contactPhone("1234567890").build();

    kitchen2 = Kitchen.builder()
            .kitchenId(2L)
            .name("Kitchen2")
            .address("116 street")
            .contactPhone("987654321").build();

    userLocation = UserLocation.builder()
        .address(ADDRESS)
        .latitude(40.8075)
        .longitude(-73.9626)
        .build();

    
    rating1 = Rating.builder()
            .ratingId(1L)
            .kitchenId(1L)
            .userId("user1")
            .userName("Customer One")
            .rating(5)
            .waitSec(120L)
            .comments("Great service!")
            .build();

    rating2 = Rating.builder()
            .ratingId(2L)
            .kitchenId(1L)
            .userId("user2")
            .userName("Customer Two")
            .rating(4)
            .waitSec(100L)
            .comments("Good service.")
            .build();
  }

  //Post Controller
  @Test
  @Order(1)
  public void saveKitchenTest() throws Exception {
    // precondition
    given(kitchenService.saveKitchen(any(Kitchen.class))).willReturn(kitchen);

    // action
    ResultActions response = mockMvc.perform(post("/api/kitchens/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(kitchen)));

    // verify
    response.andExpect(status().isCreated())
            .andExpect(content().string("New kitchen added successfully."));
  }

  //get by Id controller
  @Test
  @Order(2)
  public void getByIdKitchenTest() throws Exception {
    // precondition
    given(kitchenService.getKitchenById(kitchen.getKitchenId())).willReturn(Optional.of(kitchen));

    // action
    ResultActions response = mockMvc.perform(get("/api/kitchens/details?kitchenId={id}",
            kitchen.getKitchenId()));

    // verify
    response.andExpect(status().isOk())
            .andExpect(jsonPath("$.name",
                    is(kitchen.getName())))
            .andExpect(jsonPath("$.address",
                    is(kitchen.getAddress())))
            .andExpect(jsonPath("$.contactPhone",
                    is(kitchen.getContactPhone())));

  }


  //Update kitchen
  @Test
  @Order(3)
  public void updateKitchenTest() throws Exception {
    // precondition
    given(kitchenService.getKitchenById(kitchen.getKitchenId())).willReturn(Optional.of(kitchen));
    kitchen.setContactPhone("34567898");
    kitchen.setAddress("nowhere");
    given(kitchenService.updateKitchen(kitchen, kitchen.getKitchenId())).willReturn(kitchen);

    // action
    ResultActions response = mockMvc.perform(put("/api/kitchens/update",
            kitchen.getKitchenId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(kitchen)));

    // verify
    response.andExpect(status().isOk())
            .andExpect(content().string("Kitchen information updated successfully."));
  }


  // delete kitchen
  @Test
  @Order(4)
  public void deleteKitchenTest() throws Exception {
    // precondition
    willDoNothing().given(kitchenService).deleteKitchen(kitchen.getKitchenId());

    // action
    ResultActions response = mockMvc.perform(delete("/api/kitchens/delete?kitchenId={id}",
            kitchen.getKitchenId()));

    // then - verify the output
    response.andExpect(status().isOk())
            .andExpect(content().string("Kitchen deleted successfully."));;
  }

  @Test
  @Order(5)
  public void getNearestKitchensInvalidAddressTest() throws Exception {
    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/nearest?address={address}&count={count}",
        "", 1));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid parameters"));
  }

  @Test
  @Order(6)
  public void getNearestKitchensNegativeCountTest() throws Exception {
    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/nearest?address={address}&count={count}",
            ADDRESS, -1));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid parameters"));
  }

  @Test
  @Order(7)
  public void getNearKitchensNoKitchensFoundTest() throws Exception {
    // precondition
    given(kitchenService.getAllKitchens()).willReturn(null);

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/nearest?address={address}&count={count}",
            ADDRESS, 1));

    // verify
    response.andExpect(status().isNotFound())
        .andExpect(content().string("No kitchens found in the Mysql DB"));
  }

  @Test
  @Order(8)
  public void getNearKitchensNoKitchensFoundTest2() throws Exception {
    // precondition
    given(userService.getNearestKitchens(ADDRESS, kitchenService.getAllKitchens(), 1))
        .willReturn(null);

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/nearest?address={address}&count={count}",
            ADDRESS, 1));

    // verify
    response.andExpect(status().isNotFound())
        .andExpect(content().string("No kitchens found in the Mysql DB"));
  }

  @Test
  @Order(9)
  public void getNearKitchensTest() throws Exception {
    // precondition
    given(kitchenService.getAllKitchens())
        .willReturn(List.of(kitchen, kitchen2));
    given(userService.getNearestKitchens(ADDRESS, List.of(kitchen, kitchen2), 2))
        .willReturn(List.of(kitchen, kitchen2));

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/nearest?address={address}&count={count}",
            ADDRESS, 2));

    // verify
    response.andExpect(status().isOk())
        .andExpect(content().string(containsString("Kitchen1")))
        .andExpect(content().string(containsString("some place")))
        .andExpect(content().string(containsString("Kitchen2")))
        .andExpect(content().string(containsString("116 street")));
  }

  @Test
  @Order(10)
  public void getTopRatedKitchensInvalidCountTest() throws Exception {
    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/top-rated?count={count}",
            -1));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("invalid count: negative number"));
  }

  @Test
  @Order(11)
  public void getTopRatedKitchensNoKitchensFoundTest() throws Exception {
    // precondition
    given(kitchenService.fetchTopRatedKitchens(10)).willReturn(null);

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/top-rated?count={count}",
            10));

    // verify
    response.andExpect(status().isNotFound())
        .andExpect(content().string("No kitchens found in the Mysql DB"));
  }

  @Test
  @Order(12)
  public void getTopRatedKitchensTest() throws Exception {
    // precondition
    given(kitchenService.fetchTopRatedKitchens(2))
        .willReturn(List.of(kitchen, kitchen2));

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/top-rated?count={count}",
            2));

    // verify
    response.andExpect(status().isOk())
        .andExpect(content().string(containsString("Kitchen1")))
        .andExpect(content().string(containsString("some place")))
        .andExpect(content().string(containsString("Kitchen2")))
        .andExpect(content().string(containsString("116 street")));
  }

  @Test
  @Order(13)
  public void getKitchenRecommendationInvalidLocationTest() throws Exception {
    // action
    String location  = "";
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/recommendation?location={location}"
                + "&disabilityStatus={disabilityStatus}&mealHours={mealHours}",
            location, DISABILITY_STATUS_DISABLED, MEAL_HOURS_DAYTIME));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid input"));

    location  = null;
    response = mockMvc.perform(
        get("/api/kitchens/recommendation?location={location}"
                + "&disabilityStatus={disabilityStatus}&mealHours={mealHours}",
            location, DISABILITY_STATUS_DISABLED, MEAL_HOURS_DAYTIME));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid input"));
  }

  @Test
  @Order(14)
  public void getKitchenRecommendationInvalidDisabilityStatusTest() throws Exception {
    // action
    String disabilityStatus  = "";
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/recommendation?location={location}"
                + "&disabilityStatus={disabilityStatus}&mealHours={mealHours}",
            ADDRESS, disabilityStatus, MEAL_HOURS_DAYTIME));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid input"));

    disabilityStatus  = null;
    response = mockMvc.perform(
        get("/api/kitchens/recommendation?location={location}"
                + "&disabilityStatus={disabilityStatus}&mealHours={mealHours}",
            ADDRESS, disabilityStatus, MEAL_HOURS_DAYTIME));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid input"));
  }

  @Test
  @Order(15)
  public void getKitchenRecommendationInvalidMealHoursTest() throws Exception {
    // action
    String mealHours  = "";
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/recommendation?location={location}"
                + "&disabilityStatus={disabilityStatus}&mealHours={mealHours}",
            ADDRESS, DISABILITY_STATUS_DISABLED, mealHours));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid input"));

    mealHours  = null;
    response = mockMvc.perform(
        get("/api/kitchens/recommendation?location={location}"
                + "&disabilityStatus={disabilityStatus}&mealHours={mealHours}",
            ADDRESS, DISABILITY_STATUS_DISABLED, mealHours));

    // verify
    response.andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid input"));
  }

  @Test
  @Order(16)
  public void getKitchenRecommendationNoKitchensFoundTest() throws Exception {
    // precondition
    given(userService.getUserLocation(ADDRESS)).willReturn(userLocation);

    given(kitchenService.getAllKitchens()).willReturn(null);

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/recommendation?location={address}"
                + "&disabilityStatus={disabilityStatus}&mealHours={mealHours}",
            ADDRESS, DISABILITY_STATUS_DISABLED, MEAL_HOURS_DAYTIME));

    // verify
    response.andExpect(status().isNotFound())
        .andExpect(content().string("No kitchens found in the Mysql DB"));

    given(kitchenService.getAllKitchens()).willReturn(List.of());

    // action
    response = mockMvc.perform(
        get("/api/kitchens/recommendation?location={address}"
                + "&disabilityStatus={disabilityStatus}&mealHours={mealHours}",
            ADDRESS, DISABILITY_STATUS_DISABLED, MEAL_HOURS_DAYTIME));

    // verify
    response.andExpect(status().isNotFound())
        .andExpect(content().string("No kitchens found in the Mysql DB"));

  }

  @Test
  @Order(17)
  public void getKitchenRecommendationGetUserLocationNullTest() throws Exception {
    // precondition
    given(userService.getUserLocation(ADDRESS)).willReturn(null);

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/recommendation?location={address}"
                + "&disabilityStatus={disabilityStatus}&mealHours={mealHours}",
            ADDRESS, DISABILITY_STATUS_DISABLED, MEAL_HOURS_DAYTIME));

    // verify
    response.andExpect(status().isNotFound())
        .andExpect(content().string("Invalid location"));
  }

  @Test
  @Order(13)
  public void getKitchenRecommendationTest() throws Exception {

    String mockedResponse = "The most recommended kitchen is \\\"Kitchen1\\\" at some place,"
        + " close to your location. Known for friendly service and high-quality food. The second "
        + "recommended kitchen is \\\"Kitchen2\\\" at 116 street, nearby with convenient access. "
        + "Offers pantry and meal services with positive staff reviews.";
    Map<String, String> answerMap = new HashMap<>();
    answerMap.put("answer", mockedResponse);

    // precondition
    given(userService.getUserLocation(ADDRESS)).willReturn(userLocation);

    given(kitchenService.getAllKitchens()).willReturn(List.of(kitchen, kitchen2));

    given(openAiService.getKitchenRecommendation(any(), any(), any(), anyString(), anyString()))
        .willReturn(answerMap);

    // action
    ResultActions response = mockMvc.perform(
        get("/api/kitchens/recommendation?location={address}"
                + "&disabilityStatus={disabilityStatus}&mealHours={mealHours}",
            ADDRESS, DISABILITY_STATUS_DISABLED, MEAL_HOURS_DAYTIME));

    // verify
    response.andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().string(containsString("Known for friendly service "
            + "and high-quality food")))
        .andExpect(content().string(containsString("at 116 street")));
  }
  
  @Test
  @Order(18)
  public void addRatingTest() throws Exception {
    // Precondition
    given(ratingService.saveRating(any(Rating.class))).willReturn(rating1);

    // Action
    ResultActions response = mockMvc.perform(post("/api/ratings/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rating1)));

    // Verify
    response.andExpect(status().isCreated())
            .andExpect(content().string("Rating added successfully."));
  }

  @Test
  @Order(19)
  public void updateRatingTest() throws Exception {
    // Precondition
    rating1.setComments("Updated comment.");
    given(ratingService.updateRating(any(Rating.class), any(Long.class))).willReturn(rating1);

    // Action
    ResultActions response = mockMvc.perform(put("/api/ratings/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rating1)));

    // Verify
    response.andExpect(status().isOk())
            .andExpect(content().string("Rating updated successfully."));
  }
  
  @Test
  @Order(20)
  public void updateRatingNotFoundTest() throws Exception {
    // Precondition
    given(ratingService.updateRating(any(Rating.class), any(Long.class)))
            .willThrow(new RuntimeException("Rating to update is not found"));

    // Action
    ResultActions response = mockMvc.perform(put("/api/ratings/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rating1)));

    // Verify
    response.andExpect(status().isNotFound())
            .andExpect(content().string("Rating to update is not found"));
  }

  @Test
  @Order(21)
  public void deleteRatingTest() throws Exception {
    // Precondition
    willDoNothing().given(ratingService).deleteRating(any(Long.class));

    // Action
    ResultActions response = mockMvc.perform(delete("/api/ratings/delete?ratingId={id}",
            rating1.getRatingId()));

    // Verify
    response.andExpect(status().isOk())
            .andExpect(content().string("Rating deleted successfully."));
  }

  @Test
  @Order(22)
  public void deleteRatingNotFoundTest() throws Exception {
    // Precondition
    willDoNothing().given(ratingService).deleteRating(any(Long.class));
    doThrow(new RuntimeException("Rating to delete is not found"))
            .when(ratingService).deleteRating(any(Long.class));

    // Action
    ResultActions response = mockMvc.perform(delete("/api/ratings/delete?ratingId={id}",
            rating1.getRatingId()));

    // Verify
    response.andExpect(status().isNotFound())
            .andExpect(content().string("Rating to delete is not found"));
  }

  @Test
  @Order(23)
  public void retrieveKitchenRatingsTest() throws Exception {
    // Precondition
    given(ratingService.getKitchenRatings(any(Long.class)))
            .willReturn(List.of(rating1, rating2));

    // Action
    ResultActions response = mockMvc.perform(
            get("/api/ratings/retrieveKitchenRatings?kitchenId={id}",
            rating1.getKitchenId()));

    // Verify
    response.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId", is(rating1.getUserId())))
            .andExpect(jsonPath("$[0].comments", is(rating1.getComments())))
            .andExpect(jsonPath("$[1].userId", is(rating2.getUserId())))
            .andExpect(jsonPath("$[1].comments", is(rating2.getComments())));
  }

  @Test
  @Order(24)
  public void retrieveKitchenRatingsNotFoundTest() throws Exception {
    // Precondition
    given(ratingService.getKitchenRatings(any(Long.class)))
            .willReturn(List.of());

    // Action
    ResultActions response = mockMvc.perform(
            get("/api/ratings/retrieveKitchenRatings?kitchenId={id}",
            rating1.getKitchenId()));

    // Verify
    response.andExpect(status().isNotFound())
            .andExpect(content().string("No ratings found for the specified kitchen."));
  }


}
