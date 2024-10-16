package dev.teamproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a kitchen entity in the application.
 * This class contains details about a kitchen, including its name, address,
 * contact information, rating, and operational status.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Kitchen {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long kitchenId;
  @NotBlank
  private String name;
  @NotBlank
  @Size(max = 200)
  private String address;
  @Email
  @Size(max = 100)
  private String contactEmail;
  @Size(max = 50)
  private String contactPhone;
  //private double distance; location/coordinate is making more sense here? address solely is
  // enough.
  private Double rating;
  @Size(max = 100)
  private String accessibilityFeatures;
  private String operatingHours;
  private Boolean operationalStatus;



//  @OneToMany(mappedBy = "kitchen")
//  private Set<Distributer> distributers;

  //  @OneToMany(mappedBy = "kitchen")
//  private Set<Rating> Ratings;

  // getters and setters are generated through @data annotation from lombok
  @Override
  public String toString() {
    return this.getKitchenId() + " " + this.getName() + " " + this.getAddress() + ".";
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return Objects.equals(this.getKitchenId(), ((Kitchen) o).getKitchenId());
  }
  
  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
