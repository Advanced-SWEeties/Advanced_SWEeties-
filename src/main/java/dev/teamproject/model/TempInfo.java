package dev.teamproject.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents temporary information about a kitchen or business location,
 * including its address, coordinates, status, and additional details like
 * reviews and accessibility options.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempInfo {
  private String formattedAddress;
  private String displayName;
  private double lat;
  private double lng;
  private Long id;
  private String number;
  private double rating;
  private String operatingHours;
  private String businessStatus;
  private Map<String, Object> reviewsInfo;
  private String accessibilityOption;
}