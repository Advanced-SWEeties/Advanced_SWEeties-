package dev.teamproject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
  @ManyToOne
  @JoinColumn(name = "kitchen_id", nullable = false)
  private Kitchen kitchen;

  @ManyToOne() // Optional relationship
  @JoinColumn(name = "user_id", nullable = true) // user_id can be null
  private User user;

  private Long waitSec;

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
  public void setUser(User user) {
    this.user = user;
    this.userName = user != null ? user.getUsername() : "";
  }

  public String getUserName() {
    return user != null ? user.getUsername() : "";
  }

}