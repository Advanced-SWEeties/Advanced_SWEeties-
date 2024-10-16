package dev.teamproject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.teamproject.model.Kitchen;
import dev.teamproject.model.Rating;
import dev.teamproject.model.TempInfo;
import dev.teamproject.repository.KitchenRepository;
import dev.teamproject.repository.RatingRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
//@Primary
@RequiredArgsConstructor
@Service
@Primary
public class KitchenServiceImpl implements KitchenService {
  // get the Google API key
  @Value("${google.map.key}")
  String apiKey;

  private final KitchenRepository kitchenRepository;
  private final RatingRepository ratingRepository;

  @Override
  public List<Kitchen> listAllKitchens() {
    //  return kitchenRepository.findAll();
    return null;
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

