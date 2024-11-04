package dev.teamproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Represents the geographic location of a user, including latitude, longitude, and address.
 * This class stores the user's location details, such as coordinates and the formatted address.
 */
@Data
@AllArgsConstructor
@Builder
public class UserLocation {
  private double latitude;
  private double longitude;
  private String address;
}
