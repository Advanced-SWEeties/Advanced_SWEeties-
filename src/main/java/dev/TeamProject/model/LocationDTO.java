package dev.TeamProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // Lombok annotation to generate all basic code such as getters, setters, and toString
@NoArgsConstructor // NoArgsConstructor and AllArgsConstructor are used to automatically generate constructors
@AllArgsConstructor
public class LocationDTO {

  private double latitude;
  private double longitude;
  private String address;

}
