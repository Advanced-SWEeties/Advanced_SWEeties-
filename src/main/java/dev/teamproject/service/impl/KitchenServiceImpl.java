package dev.teamproject.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.TempInfo;
import dev.teamproject.repository.KitchenRepository;
import dev.teamproject.repository.RatingRepository;
import dev.teamproject.service.KitchenService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the KitchenService interface.
 * This service contains the business logic related to kitchen operations.
 */
@Primary
@Service
public class KitchenServiceImpl implements KitchenService {
  @Value("${google.map.key}")
  String apiKey;

  private final KitchenRepository  kitchenRepository;
  private final RatingRepository ratingRepository;

  @Autowired
  public KitchenServiceImpl(KitchenRepository kitchenRepository,
                            RatingRepository ratingRepository) {
    this.kitchenRepository = kitchenRepository;
    this.ratingRepository = ratingRepository;
  }

  // Implementation of KitchenService methods will go her
  @Override
  public Kitchen saveKitchen(Kitchen kitchen) {
    Optional<Kitchen> toSave = kitchenRepository.findByName(kitchen.getName());
    if (toSave.isPresent()) {
      throw new RuntimeException("Kitchen already exists with given name: " + kitchen.getName());
    }
    Kitchen saved = kitchenRepository.save(kitchen);
    System.out.println("Kitchen named " + kitchen.getName() + " saved: " + saved);
    return saved;
  }

  @Override
  public List<Kitchen> getAllKitchens() {
    return kitchenRepository.findAll();
  }

  @Override
  public Optional<Kitchen> getKitchenById(long id) {
    Optional<Kitchen> toGet = kitchenRepository.findByKitchenId(id);
    if (toGet.isEmpty()) {
      throw new RuntimeException("Kitchen not exists with given id: " + id);
    }
    System.out.println("Kitchen with id " + id + " is got.");
    return toGet;
  }

  @Override
  public List<Kitchen> searchKitchen(String kitchenName) {
    List<Kitchen> kitchens = kitchenRepository.findByNameContaining(kitchenName);
    if (kitchens.isEmpty()) {
      throw new RuntimeException("All kitchen does not contain the given name: " + kitchenName);
    }
    System.out.println("List of Kitchens with name " + kitchenName + " are got.");
    return kitchens;
  }

  @Override
  public Optional<Kitchen> getKitchenByName(String kitchenName) {
    Optional<Kitchen> toGet = kitchenRepository.findByName(kitchenName);
    if (toGet.isEmpty()) {
      throw new RuntimeException("Kitchen not exists with given name: " + kitchenName);
    }
    System.out.println("Kitchen with name " + kitchenName + " is got.");
    return toGet;
  }

  @Override
  public Kitchen updateKitchen(Kitchen kitchen, long id) {
    Kitchen toUpdate = kitchenRepository.findByKitchenId(id)
            .orElseThrow(() -> new RuntimeException("Kitchen not exists with id: " + id));
    if (!kitchen.getName().isEmpty() && !Objects.equals(kitchen.getName(), toUpdate.getName())) {
      toUpdate.setName(kitchen.getName());
    }
    if (!kitchen.getAddress().isEmpty()
            && !Objects.equals(kitchen.getAddress(), toUpdate.getAddress())) {
      toUpdate.setAddress(kitchen.getAddress());
    }
    if (!kitchen.getContactPhone().isEmpty()
            && !Objects.equals(kitchen.getContactPhone(), toUpdate.getContactPhone())) {
      toUpdate.setContactPhone(kitchen.getContactPhone());
    }
    if (!kitchen.getAccessibilityFeatures().isEmpty()
            && !Objects.equals(kitchen.getAccessibilityFeatures(),
            toUpdate.getAccessibilityFeatures())) {
      toUpdate.setAccessibilityFeatures(kitchen.getAccessibilityFeatures());
    }
    if (!kitchen.getOperatingHours().isEmpty()
            && !Objects.equals(kitchen.getOperatingHours(), toUpdate.getOperatingHours())) {
      toUpdate.setOperatingHours(kitchen.getOperatingHours());
    }

    if (kitchen.getOperationalStatus() != null
            && kitchen.getOperationalStatus() != toUpdate.getOperationalStatus()) {
      toUpdate.setOperationalStatus(kitchen.getOperationalStatus());
    }

    kitchenRepository.save(toUpdate);
    return toUpdate;
  }

  @Override
  public void deleteKitchen(long id) {
    Optional<Kitchen> toDelete = kitchenRepository.findByKitchenId(id);
    if (toDelete.isEmpty()) {
      throw new RuntimeException("Kitchen not exists with given id: " + id);
    }
    kitchenRepository.deleteById(id);
    System.out.println("Kitchen with id " + id + " is deleted.");
  }

  @Override
  public List<Kitchen> topRatedKitchens() {
    return kitchenRepository.findTop20ByOrderByRatingDesc();
  }

  /**
   * Fetches soup kitchens in New York City from the Google Places API and saves
   * them to the database. This method sends a request to the Google Places API
   * to retrieve information about soup kitchens in New York City.
   * For each kitchen, it also saves user reviews as {@link Rating}
   * entities with randomly generated user IDs.</p>
   *
   * @return a list of {@link TempInfo} objects containing information about the retrieved kitchens.
   * @throws RuntimeException if there is an issue with fetching or processing the data.
   */
  @Override
  public List<TempInfo> fetchAllKitchens() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("X-Goog-Api-Key", apiKey);
    headers.add("X-Goog-FieldMask", "*");

    String requestUrl = "https://places.googleapis.com/v1/places:searchText";
    String body = "{ \"textQuery\": \"soup kitchen in New York City\", \"regionCode\": \"US\" }";
    HttpEntity<String> request = new HttpEntity<>(body, headers);
    RestTemplate template = new RestTemplate();
    List<TempInfo> tempInfos = new ArrayList<>();

    try {
      ResponseEntity<String> response = template.postForEntity(requestUrl, request, String.class);
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(response.getBody());
      if (root.size() == 0) {
        throw new RuntimeException("No data found");
      }

      JsonNode places = root.path("places");
      for (JsonNode place : places) {
        // kitchen entity mapping

        StringBuilder operatingHours = new StringBuilder();
        JsonNode weekdayDescriptions = place.path("regularOpeningHours")
                .path("weekdayDescriptions");
        if (weekdayDescriptions.isArray()) {
          for (JsonNode hour : weekdayDescriptions) {
            operatingHours.append(hour.asText()).append("\n");
          }
        }

        StringBuilder accessibilityOption = new StringBuilder();
        JsonNode accessibilityOptions = place.path("accessibilityOptions");
        if (accessibilityOptions.has("wheelchairAccessibleParking")) {
          boolean wheelchairAccessibleParking = accessibilityOptions
                  .path("wheelchairAccessibleParking").asBoolean();
          if (wheelchairAccessibleParking) {
            accessibilityOption.append("Wheelchair accessible parking\n");
          }
        }
        if (accessibilityOptions.has("wheelchairAccessibleRestroom")) {
          boolean wheelchairAccessibleRestroom = accessibilityOptions
                  .path("wheelchairAccessibleRestroom").asBoolean();
          if (wheelchairAccessibleRestroom) {
            accessibilityOption.append("Wheelchair accessible restroom\n");
          }
        }
        if (accessibilityOptions.has("wheelchairAccessibleSeating")) {
          boolean wheelchairAccessibleSeating = accessibilityOptions
                  .path("wheelchairAccessibleSeating").asBoolean();
          if (wheelchairAccessibleSeating) {
            accessibilityOption.append("Wheelchair accessible seating\n");
          }
        }
        if (accessibilityOptions.has("wheelchairAccessibleEntrance")) {
          boolean wheelchairAccessibleEntrance = accessibilityOptions
                  .path("wheelchairAccessibleEntrance").asBoolean();
          if (wheelchairAccessibleEntrance) {
            accessibilityOption.append("Wheelchair accessible entrance\n");
          }
        }

        JsonNode location = place.path("location");
        double lat = location.path("latitude").asDouble();
        double lng = location.path("longitude").asDouble();
        String formattedAddress = place.path("formattedAddress").asText();
        String displayName = place.path("displayName").path("text").asText();
        Long id = place.path("id").asLong();
        String number = place.path("nationalPhoneNumber").asText();
        double rating = place.path("rating").asDouble();
        String businessStatus = place.path("businessStatus").asText();
        Kitchen kitchen = Kitchen.builder()
                .name(displayName)
                .address(formattedAddress)
                .contactPhone(number)
                .latitude(lat)
                .longitude(lng)
                .rating(rating)
                .accessibilityFeatures(accessibilityOption.toString())
                .operatingHours(operatingHours.toString())
                .operationalStatus(businessStatus)
                .build();
        kitchenRepository.save(kitchen);

        Map<String, Object> reviewsInfo = new HashMap<>();
        JsonNode reviews = place.path("reviews");
        if (reviews.isArray()) {
          for (JsonNode review : reviews) {
            String randomUserId = UUID.randomUUID().toString();

            String authorName = review.path("authorAttribution")
                    .path("displayName").asText();
            String authorUri = review.path("authorAttribution")
                    .path("uri").asText();
            int reviewerRating = review.path("rating").asInt();
            String reviewText = review.path("text")
                    .path("text").asText();
            String publishTime = review.path("publishTime").asText();
            String relativeTime = review.path("relativePublishTimeDescription").asText();
            reviewsInfo.put("authorName", authorName);
            reviewsInfo.put("authorUri", authorUri);
            reviewsInfo.put("reviewerRating", reviewerRating);
            reviewsInfo.put("reviewText", reviewText);
            reviewsInfo.put("publishTime", publishTime);
            reviewsInfo.put("relativeTime", relativeTime);

            // rating entity mapping
            Rating ratingEntity = Rating.builder()
                    .kitchenId(kitchen.getKitchenId())
                    .userId(randomUserId)
                    .userName(authorName)
                    .rating(reviewerRating)
                    .comments(reviewText)
                    .commentUrl(authorUri)
                    .publishTime(publishTime)
                    .relativeTime(relativeTime)
                    .build();

            ratingRepository.save(ratingEntity);
          }
        }

        tempInfos.add(new TempInfo(displayName, formattedAddress, lat, lng, id, number, rating,
                operatingHours.toString(), businessStatus, reviewsInfo, accessibilityOption
                .toString()));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return tempInfos;
  }
}
