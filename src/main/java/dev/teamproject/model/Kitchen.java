package dev.teamproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "kitchen")
public class Kitchen {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long kitchenId;
  @NotBlank
  private String name;
  @NotBlank
  @Size(max = 200)
  private String address;
  //  @Email
  //  @Size(max = 100)
  //  private String contactEmail;
  @Size(max = 50)
  private String contactPhone;
  //private double distance; location/coordinate is making more sense here? address solely is
  // enough.

  private Double latitude;
  private Double longitude;

  private Double rating;
  @OneToMany(mappedBy = "kitchen", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Rating> ratings = new ArrayList<>();

  @Size(max = 200)
  private String accessibilityFeatures;

  @Column(columnDefinition = "TEXT")
  private String operatingHours;
  private String operationalStatus;



  //  @OneToMany(mappedBy = "kitchen")
  //  private Set<Distributer> distributers;



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

  /**
   * This method update the rating of the kitchen.
   * rating is updated by calculating average rating of all associated ratings.
   */
  public void updateAverageRating() {
    this.rating = ratings.stream()
            .mapToDouble(Rating::getRating)
            .average()
            .orElse(0.0);
  }
}
