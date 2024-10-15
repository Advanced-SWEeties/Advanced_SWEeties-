package dev.teamproject.model;

/**
 * Represents a rating for a kitchen provided by a user.
 * This class contains the kitchen ID, user ID, rating score, and optional comments.
 */
public class Rating {
  private Long kitchenId;
  private Long userId;
  private int rating; // 1 to 5
  private String comments;
  // Getters and Setters
}
