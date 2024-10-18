package dev.teamproject.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.UserLocation;
import dev.teamproject.repository.KitchenRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Unit testing for User Service.
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceImplTest {

  @InjectMocks
  private UserServiceImpl userServiceImpl;

  @Mock
  private KitchenRepository kitchenRepository;

  @Mock
  private RestTemplate restTemplate;

  private static final String APIKEY = "AIzaSyBx5BYUFIISVIvM5-OHE1LcJ3FR13H2Y58";

  private List<Kitchen> allKitchens;

  private static final String ADDRESS = "500 W 120th St, New York, NY 10027";

  private static final String REQUEST_URL =
      "https://maps.googleapis.com/maps/api/geocode/json?address=" + ADDRESS + "&key=" + APIKEY;

  private static final String  ZERO_RESULT_RESPONSE  = """
        {
        "status": "ZERO_RESULTS",
        "results": []
        }
        """;

  private static final String SINGLE_RESPONSE = """
        {
        "results": [
            {
            "formatted_address": "500 W 120th St, New York, NY 10027, USA",
            "geometry": {
                "location": {
                "lat": 40.810000,
                "lng": -73.960000
                }
            }
            }
        ],
        "status": "OK"
        }
        """;


  @BeforeEach
  void setUp() {
    allKitchens = kitchenRepository.findAll();
    userServiceImpl.apiKey = APIKEY;
    userServiceImpl.restTemplate = restTemplate;
  }

  @Test
  public void getUserLocationApiKeyIsNullTest() {
    String temp = userServiceImpl.apiKey;
    userServiceImpl.apiKey = null;

    assertThrows(IllegalArgumentException.class, () -> userServiceImpl.getUserLocation(ADDRESS));
    userServiceImpl.apiKey = temp;
  }

  @Test
  public void getUserLocationZero_ResultsTest() {
    // mock restTemplate
    given(restTemplate.getForEntity(REQUEST_URL, String.class))
        .willReturn(new ResponseEntity<>(ZERO_RESULT_RESPONSE, HttpStatus.OK));

    assertNull(userServiceImpl.getUserLocation(ADDRESS),
        "Expected null when status is ZERO_RESULTS");
  }

  @Test
  void getUserLocationTest() {

    // mock restTemplate
    given(restTemplate.getForEntity(REQUEST_URL, String.class))
        .willReturn(new ResponseEntity<>(SINGLE_RESPONSE, HttpStatus.OK));

    UserLocation userLocation = userServiceImpl.getUserLocation(ADDRESS);
    UserLocation expected = new UserLocation(
        40.810000,
        -73.960000,
        "500 W 120th St, New York, NY 10027, USA");
    assertEquals(expected, userLocation);
  }


  @Test
  public void getNearestKitchensInvalidAddressTest() {

    // Mock restTemplate to return ZERO_RESULT_RESPONSE when getForEntity is called, it would cause
    // getUserLocation to return null
    given(restTemplate.getForEntity(anyString(), eq(String.class)))
        .willReturn(new ResponseEntity<>(ZERO_RESULT_RESPONSE, HttpStatus.OK));

    List<Kitchen> result = userServiceImpl.getNearestKitchens(ADDRESS, allKitchens, 5);

    // verify
    assertNull(result, "Expected null when getUserLocation returns null.");
  }


  @Test
  public void getNearestKitchensTest() {

    given(restTemplate.getForEntity(anyString(), eq(String.class)))
        .willReturn(new ResponseEntity<>(SINGLE_RESPONSE, HttpStatus.OK));

    Kitchen kitchen1 = Kitchen.builder()
        .kitchenId(1L)
        .name("Kitchen1")
        .address("some place 1")
        .contactPhone("1234567890")
        .latitude(40.820000)
        .longitude(-73.950000)
        .build();

    Kitchen kitchen2 = Kitchen.builder()
        .kitchenId(2L)
        .name("Kitchen2")
        .address("some place 2")
        .contactPhone("0987654321")
        .latitude(40.830000)
        .longitude(-73.940000)
        .build();

    Kitchen kitchen3 = Kitchen.builder()
        .kitchenId(3L)
        .name("Kitchen3")
        .address("some place 3")
        .contactPhone("1122334455")
        .latitude(40.800000)
        .longitude(-73.970000)
        .build();

    List<Kitchen> allKitchens = Arrays.asList(kitchen1, kitchen2, kitchen3);
    List<Kitchen> result = userServiceImpl.getNearestKitchens(ADDRESS, allKitchens, 2);
    List<Kitchen> expected = Arrays.asList(kitchen1, kitchen3);

    // Verify
    assertEquals(expected, result, "returned nearest kitchens!!");
  }

  @Test
  void haversineTest() {

    // set up the testing latitude and longitude
    double lat1 = 40.810000;
    double lon1 = -73.960000;

    Kitchen kitchen = Kitchen.builder()
        .kitchenId(1L)
        .name("Kitchen1")
        .address("some place 1")
        .contactPhone("1234567890")
        .latitude(40.820000)
        .longitude(-73.950000)
        .build();

    double result = userServiceImpl.haversine(lat1, lon1, kitchen);
    double expected = 1.3945025343830006;
    // verify
    assertEquals(expected, result, 0.1);
  }
}