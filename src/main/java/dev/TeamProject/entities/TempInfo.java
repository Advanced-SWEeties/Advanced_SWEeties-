package dev.TeamProject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempInfo {
  private String formattedAddress;
  private String displayName;
  private double lat;
  private double lng;
  private String id;
  private String number;
  private double rating;
  private List<String> operatingHours;
  private String businessStatus;
  private Map<String, Object> reviewsInfo;
  Map<String, Boolean> accessibilityOptionMap;
}
