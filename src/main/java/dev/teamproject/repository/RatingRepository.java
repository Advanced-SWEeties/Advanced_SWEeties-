package dev.teamproject.repository;

import dev.teamproject.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Rating entities from the database.
 * It extends JpaRepository to provide CRUD operations.
 * This repository provides methods to find relevant rating information.
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
}
