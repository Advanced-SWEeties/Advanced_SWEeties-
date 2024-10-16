package dev.teamproject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a rating for a kitchen provided by a user.
 * This class contains the kitchen ID, user ID, rating score, and optional comments.
 */

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long ratingId;

  //  @OneToOne
  //  private Kitchen kitchen;

  @NotNull
  private Long kitchenId;

  private String userId;

  @NotBlank
  private String userName;
  @NotNull
  private Integer rating; // 1 to 5

  @Column(columnDefinition = "TEXT")
  private String comments;
  private String commentUrl;
  private String publishTime;
  private String relativeTime;

  // Getters and Setters
}
