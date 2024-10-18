package dev.teamproject.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.teamproject.model.Kitchen;
import dev.teamproject.model.User;
import dev.teamproject.model.UserLocation;
import dev.teamproject.repository.UserRepository;
import dev.teamproject.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * Implementation of UserService interface that provides methods
 * to handle user-related operations like fetching user location and getting nearest kitchens.
 */
@Service
@Primary
public class UserServiceImpl implements UserService {

  // get the Google API key
  @Value("${google.map.key}")
  private String apiKey;
  private static final double EARTH_RADIUS_KM = 6371;

  /**
   * Fetches the geographic coordinates (latitude and longitude) and the formatted address
   * for a given address string using the Google Geocoding API.
   *
   * @param address The input address as a string. This address will be sent to the Google Maps
   *                Geocoding API to retrieve the corresponding geographic coordinates.
   * @return UserLocation containing the latitude, longitude, and
   *         formatted address of the given input address. Returns null if no results are
   *         found (e.g., invalid address).
   * @throws RuntimeException if there is an error in the API request or response parsing.
   */
  @Override
  public UserLocation getUserLocation(String address) {
    RestTemplate restTemplate = new RestTemplate();
    String requestUrl = "https://maps.googleapis.com/maps/api/geocode/json?address="
        + address + "&key=" + apiKey;

    try {
      ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
      ObjectMapper mapper = new ObjectMapper();

      JsonNode root = mapper.readTree(response.getBody());
      String s =  root.path("status").asText();
      JsonNode results = root.path("results");

      /* ZERO_RESULTS probably resulted from invalid address input, prompt the user
      to enter a valid address during the api call */
      if (s.equals("ZERO_RESULTS")) {
        return null;
      } else {
        // get the latitude and longitude
        JsonNode location = results.get(0).path("geometry").path("location");
        double lat = location.path("lat").asDouble();
        double lng = location.path("lng").asDouble();

        // get the full name of the address
        String formattedAddress = results.get(0).path("formatted_address").asText();

        return new UserLocation(lat, lng, formattedAddress);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns a list of the nearest Kitchen objects to the given address.
   * It calculates the distance between the user's location (derived from the
   * address) and each kitchen,
   * and returns the closest kitchens up to the specified count.
   *
   * @param address      The user's address used to find their location.
   * @param allKitchens  A list of available kitchens.
   * @param count        The number of nearest kitchens to return. If fewer kitchens are
   *                     available, it returns as many as possible.
   * @return A list of the nearest kitchens, sorted by distance to the user's location,
   *         or null if the address is invalid.
   * @throws RuntimeException if there is an error retrieving the user's location.
   */
  @Override
  public List<Kitchen> getNearestKitchens(String  address, List<Kitchen> allKitchens, int count) {
    UserLocation userLocation = getUserLocation(address);
    if (userLocation == null) {
      return null;
    }
    double userLat = userLocation.getLatitude();
    double userLng = userLocation.getLongitude();

    // use a priority queue to store count number of nearest kitchens
    PriorityQueue<Kitchen> pq = new PriorityQueue<>((k1, k2) -> {
      double distance1 = haversine(userLat, userLng, k1);
      double distance2 = haversine(userLat, userLng, k2);
      return Double.compare(distance1, distance2);
    });

    for (Kitchen kitchen : allKitchens) {
      pq.offer(kitchen);
    }

    List<Kitchen> nearestKitchens = new ArrayList<Kitchen>();

    // avoid large count causing out of bound exception
    count = Math.min(count, allKitchens.size());
    for (int i = 0; i < count; i++) {
      if (pq.isEmpty()) {
        break;
      }
      nearestKitchens.add(pq.poll());
    }

    return nearestKitchens;
  }

  /**
   * Calculates the distance between the user's location and a kitchen using the
   * Haversine formula.This formula provides the shortest distance between two
   * points on a sphere based on their latitudes and longitudes.
   *
   * @param userLat    The latitude of the user's location.
   * @param userLng    The longitude of the user's location.
   * @param kitchen2   The  Kitchen object containing the kitchen's location
   *                   (latitude and longitude).
   * @return The distance in kilometers between the user's location and the kitchen.
   */
  public static double haversine(double userLat, double userLng, Kitchen kitchen2) {
    // first need radians from longitude and latitude
    double user1lat = Math.toRadians(userLat);
    double user1lon = Math.toRadians(userLng);
    double kitchen2lat = Math.toRadians(kitchen2.getLatitude());
    double kitchen2lon = Math.toRadians(kitchen2.getLongitude());

    // Apply Haversine formula
    double deltaLat = kitchen2lat - user1lat;
    double deltaLon = kitchen2lon - user1lon;
    double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
        + Math.cos(user1lat) * Math.cos(kitchen2lat)
          *  Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    // Distance in km
    return EARTH_RADIUS_KM * c;
  }


  private final UserRepository userRepository; // Assuming you have a UserRepository
  
  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<User> getUserById(Long userId) {
    return userRepository.findById(userId); // Assuming you have a method in UserRepository
  }

  @Override
  public void saveUser(User user) {
    userRepository.save(user); // Assuming you have a method in UserRepository
  }

}
