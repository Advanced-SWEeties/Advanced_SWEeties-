
package dev.teamproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.teamproject.client.CallbackClientService;
import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.TempInfo;
import dev.teamproject.repository.KitchenRepository;
import dev.teamproject.repository.RatingRepository;
import dev.teamproject.service.impl.KitchenServiceImpl;
import java.util.List;
import java.util.Optional;
import org.hibernate.dialect.SybaseASEDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;


/**
 * Unit testing for kitchen Service.
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KitchenServiceUnitTests {
  @Mock
  private KitchenRepository kitchenRepository;

  @Mock
  private RatingRepository ratingRepository;

  @Mock
  private CallbackClientService callbackClientService;

  @Mock
  private RestTemplate restTemplate;
  
  @InjectMocks
  private KitchenServiceImpl kitchenService;

  private Kitchen kitchen;
  
  @Value("${google.map.key}")
  private String apiKey;

  /**
   * set ip a kitchen object before each test.
   */
  @BeforeEach
  public void setup() {
    kitchen = Kitchen.builder()
            .kitchenId(1L)
            .name("Kitchen1")
            .address("some place")
            .contactPhone("1234567890").build();
  }

  @Test
  @Order(1)
  public void saveKitchenTest() {
    // precondition
    given(kitchenRepository.save(kitchen)).willReturn(kitchen);

    //action
    System.out.println("CallbackClientService mock instance: " + callbackClientService);
    Kitchen savedKitchen = kitchenService.saveKitchen(kitchen);

    verify(callbackClientService).notifyExternalService(savedKitchen);

    // verify
    System.out.println(savedKitchen);
    assertThat(savedKitchen).isNotNull();
  }

  @Test
  @Order(2)
  public void saveKitchenTest_KitchenAlreadyExists() {
    // Given
    Kitchen kitchen = new Kitchen();
    kitchen.setName("Test Kitchen");
    
    given(kitchenRepository.findByName("Test Kitchen")).willReturn(Optional.of(kitchen));

    Exception exception = assertThrows(RuntimeException.class, () -> {
      kitchenService.saveKitchen(kitchen);
    }, "Expected saveKitchen to throw, but it did not");

    assertTrue(exception.getMessage().contains("Kitchen already exists with given name:" 
        + " Test Kitchen"));
  }

  @Test
  @Order(2)
  public void getKitchenByIdTest() {
    // precondition
    given(kitchenRepository.findByKitchenId(1L)).willReturn(Optional.of(kitchen));

    // action
    Kitchen existingKitchen = kitchenService.getKitchenById(1L).get();

    // verify
    System.out.println(existingKitchen);
    assertThat(existingKitchen).isNotNull();

  }

  @Test
  @Order(2)
  public void getKitchenByIdTest_NotFound() {
    // Given
    long kitchenId = 1L; 
    given(kitchenRepository.findByKitchenId(kitchenId)).willReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      kitchenService.getKitchenById(kitchenId);
    }, "Expected getKitchenById to throw, but it did not");

    assertTrue(exception.getMessage().contains("Kitchen not exists with given id: " + kitchenId));
  }
  
  @Test
  @Order(2)
  public void getKitchenByNameTest() {
    // precondition
    given(kitchenRepository.findByName("Kitchen1")).willReturn(Optional.of(kitchen));

    // action
    Kitchen existingKitchen = kitchenService.getKitchenByName("Kitchen1").get();

    // verify
    System.out.println(existingKitchen);
    assertThat(existingKitchen).isNotNull();
  }

  @Test
  @Order(2)
  public void getKitchenByNameTest_NotFound() {
    // Given
    String kitchenName = "sdklfjsi";
    given(kitchenRepository.findByName(kitchenName)).willReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      kitchenService.getKitchenByName(kitchenName);
    }, "Expected getKitchenByName to throw, but it did not");

    assertTrue(exception.getMessage().contains("Kitchen not exists with given name: " 
        + kitchenName));
  }

  @Test
  @Order(3)
  public void getAllKitchenTest() {
    Kitchen kitchen1 = Kitchen.builder()
            .kitchenId(2L)
            .name("Kitchen2")
            .address("some place")
            .contactPhone("1234567890").build();

    // precondition
    given(kitchenRepository.findAll()).willReturn(List.of(kitchen, kitchen1));

    // action
    List<Kitchen> kitchenList = kitchenService.getAllKitchens();

    // verify
    System.out.println(kitchenList);
    assertThat(kitchenList).isNotNull();
    assertThat(kitchenList.size()).isGreaterThan(1);
  }

  @Test
  @Order(3)
  public void searchKitchenTest() {
    Kitchen kitchen1 = Kitchen.builder()
            .kitchenId(2L)
            .name("Kitchen2")
            .address("some place")
            .contactPhone("1234567890").build();

    // precondition
    given(kitchenRepository.findByNameContaining("Kit")).willReturn(List.of(kitchen, kitchen1));

    // action
    List<Kitchen> kitchenList = kitchenService.searchKitchen("Kit");

    // verify
    System.out.println(kitchenList);
    assertThat(kitchenList).isNotNull();
    assertThat(kitchenList.size()).isGreaterThan(1);
  }

  @Test
  @Order(3)
  public void searchKitchenTest_NoKitchensFound() {
    // Given
    String searchTerm = "123"; 
    given(kitchenRepository.findByNameContaining(searchTerm)).willReturn(List.of());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      kitchenService.searchKitchen(searchTerm);
    }, "Expected searchKitchen to throw, but it did not");

    assertTrue(exception.getMessage().contains("All kitchen does not contain the given name: " 
        + searchTerm));
  }

  @Test
  @Order(4)
  public void updateKitchenTest() {

    // precondition
    given(kitchenRepository.findByKitchenId(kitchen.getKitchenId()))
            .willReturn(Optional.of(kitchen));
    kitchen.setContactPhone("34567898");
    kitchen.setAddress("nowhere");
    given(kitchenRepository.save(kitchen)).willReturn(kitchen);

    // action
    Kitchen updatedKitchen = kitchenService.updateKitchen(kitchen, kitchen.getKitchenId());

    // verify
    System.out.println(updatedKitchen);
    assertThat(updatedKitchen.getContactPhone()).isEqualTo("34567898");
    assertThat(updatedKitchen.getAddress()).isEqualTo("nowhere");
  }

  @Test
  @Order(5)
  public void updateKitchenTest_KitchenNotFound() {
    // Given
    long nonExistentKitchenId = 123; 
    given(kitchenRepository.findByKitchenId(nonExistentKitchenId)).willReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
      kitchenService.updateKitchen(new Kitchen(), nonExistentKitchenId);
    }, "Expected updateKitchen to throw, but it did not");

    assertTrue(exception.getMessage().contains("Kitchen not exists with id: " 
        + nonExistentKitchenId));
  }

  @Test
  @Order(6)
  public void updateKitchenTest_UpdateContactPhoneOnly() {
    // Given
    Kitchen existingKitchen = new Kitchen();
    existingKitchen.setKitchenId(1L);
    existingKitchen.setContactPhone("12345678");
    existingKitchen.setAddress("123 Street");
    existingKitchen.setName("Kitchen Original");

    Kitchen updateInfo = new Kitchen();
    updateInfo.setContactPhone("87654321"); // Change only the phone!

    given(kitchenRepository.findByKitchenId(1L)).willReturn(Optional.of(existingKitchen));
    given(
      kitchenRepository.save(any(Kitchen.class))
    ).willAnswer(invocation -> invocation.getArgument(0));

    // Action
    Kitchen updatedKitchen = kitchenService.updateKitchen(updateInfo, 1L);

    // Verify
    assertEquals("87654321", updatedKitchen.getContactPhone());
    assertEquals("123 Street", updatedKitchen.getAddress()); // Should remain unchanged
    assertEquals("Kitchen Original", updatedKitchen.getName()); // Should remain unchanged
  }

  @Test
  @Order(7)
  public void updateKitchenTest_UpdateAccessibilityFeatures() {
    // Given
    Kitchen existingKitchen = new Kitchen();
    existingKitchen.setKitchenId(1L);
    existingKitchen.setAccessibilityFeatures("Wheelchair ramp available");

    Kitchen updateInfo = new Kitchen();
    updateInfo.setAccessibilityFeatures("Wheelchair lift available");

    given(kitchenRepository.findByKitchenId(1L)).willReturn(Optional.of(existingKitchen));
    given(
      kitchenRepository.save(any(Kitchen.class))
    ).willAnswer(invocation -> invocation.getArgument(0));
    // Action
    Kitchen updatedKitchen = kitchenService.updateKitchen(updateInfo, 1L);

    // Verify
    assertEquals("Wheelchair lift available", updatedKitchen.getAccessibilityFeatures());
  }

  @Test
  @Order(8)
  public void updateKitchenTest_UpdateOperatingHours() {
    // Given
    Kitchen existingKitchen = new Kitchen();
    existingKitchen.setKitchenId(1L);
    existingKitchen.setOperatingHours("9 AM - 5 PM");

    Kitchen updateInfo = new Kitchen();
    updateInfo.setOperatingHours("10 AM - 6 PM");

    given(kitchenRepository.findByKitchenId(1L)).willReturn(Optional.of(existingKitchen));
    given(
      kitchenRepository.save(any(Kitchen.class))
    ).willAnswer(invocation -> invocation.getArgument(0));

    // Action
    Kitchen updatedKitchen = kitchenService.updateKitchen(updateInfo, 1L);

    // Verify
    assertEquals("10 AM - 6 PM", updatedKitchen.getOperatingHours());
  }

  @Test
  @Order(9)
  public void updateKitchenTest_UpdateOperationalStatus() {
    // Given
    Kitchen existingKitchen = new Kitchen();
    existingKitchen.setKitchenId(1L);
    existingKitchen.setOperationalStatus("Closed");

    Kitchen updateInfo = new Kitchen();
    updateInfo.setOperationalStatus("Open");

    given(kitchenRepository.findByKitchenId(1L)).willReturn(Optional.of(existingKitchen));
    given(
      kitchenRepository.save(any(Kitchen.class))
    ).willAnswer(invocation -> invocation.getArgument(0));

    // Action
    Kitchen updatedKitchen = kitchenService.updateKitchen(updateInfo, 1L);

    // Verify
    assertEquals("Open", updatedKitchen.getOperationalStatus());
  }

  @Test
  @Order(10)
  public void updateKitchenTest_NoOperationalStatusUpdateWhenNull() {
    // Given
    Kitchen existingKitchen = new Kitchen();
    existingKitchen.setKitchenId(1L);
    existingKitchen.setOperationalStatus("Closed");

    Kitchen updateInfo = new Kitchen(); // a null operational status (this is by default)

    given(kitchenRepository.findByKitchenId(1L)).willReturn(Optional.of(existingKitchen));
    given(
      kitchenRepository.save(any(Kitchen.class))
    ).willAnswer(invocation -> invocation.getArgument(0));

    // Action
    Kitchen updatedKitchen = kitchenService.updateKitchen(updateInfo, 1L);

    // Verify
    assertEquals("Closed", updatedKitchen.getOperationalStatus());
    verify(kitchenRepository).save(any(Kitchen.class));
  }


  @Test
  @Order(5)
  public void deleteKitchenTest() {

    // precondition
    given(kitchenRepository.findByKitchenId(kitchen.getKitchenId()))
            .willReturn(Optional.of(kitchen));
    willDoNothing().given(kitchenRepository).deleteById(kitchen.getKitchenId());

    // action
    kitchenService.deleteKitchen(kitchen.getKitchenId());

    // verify
    verify(kitchenRepository, times(1)).deleteById(kitchen.getKitchenId());
  }
  
  @Test
  @Order(6)
  public void deleteKitchenTest_KitchenNotFound() {
    // Given  a non-existent kitchen id 
    long nonExistentKitchenId = 1;
    given(kitchenRepository.findByKitchenId(nonExistentKitchenId)).willReturn(Optional.empty());
    Exception exception = assertThrows(RuntimeException.class, () -> {
      kitchenService.deleteKitchen(nonExistentKitchenId);
    }, "Expected deleteKitchen to throw, but it did not");

    assertTrue(exception.getMessage().contains("Kitchen not exists with given id: " 
        + nonExistentKitchenId));
  }

  @Test
  public void testFetchAllKitchens() {
    
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Goog-Api-Key", null);
    headers.add("X-Goog-FieldMask", "*");

    String requestUrl = "https://places.googleapis.com/v1/places:searchText";
    String requestBody = 
        "{ \"textQuery\": \"soup kitchen in New York City\", \"regionCode\": \"US\" }";
    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

    String jsonResponse = """
        {
          "places": [
            {
              "id": 10,
              "displayName": {
                "text": "Community Kitchen-West Harlem"
              },
              "formattedAddress": "252 W 116th St, New York, NY 10026",
              "nationalPhoneNumber": "(212) 665-9082",
              "rating": 4.5,
              "businessStatus": "Operational",
              "location": {
                "latitude": 40.8039429,
                "longitude": -73.954989
              },
              "regularOpeningHours": {
                "weekdayDescriptions": [
                  "Monday: Closed",
                  "Tuesday: 10:00 AM – 6:00 PM",
                  "Wednesday: 10:00 AM – 6:00 PM",
                  "Thursday: 10:00 AM – 6:00 PM",
                  "Friday: 10:00 AM – 6:00 PM",
                  "Saturday: 11:00 AM – 2:00 PM",
                  "Sunday: Closed"
                ]
              },
              "accessibilityOptions": {
                "wheelchairAccessibleParking": true,
                "wheelchairAccessibleRestroom": true,
                "wheelchairAccessibleSeating": true,
                "wheelchairAccessibleEntrance": true
              },
              "reviews": [
                {
                  "authorAttribution": {
                    "displayName": "None",
                    "uri": "None"
                  },
                  "rating": "None",
                  "text": {
                    "text": "None"
                  },
                  "publishTime": "None",
                  "relativePublishTimeDescription": "None"
                }
              ]
            }
          ]
        }
        """;
      


    ResponseEntity<String> mockResponse = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

    given(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .willReturn(mockResponse);


    // Execute the service method
    List<TempInfo> results = kitchenService.fetchAllKitchens();
    System.out.println("In testFetchAllKitchens: " + results);
    // Assertions
    assertNotNull(results);
    assertFalse(results.isEmpty());
      
    TempInfo firstResult = results.get(0);
    assertEquals("Community Kitchen-West Harlem", firstResult.getDisplayName());
  }
  //TODO top rated restaurant and fetchAllKitchens
}
