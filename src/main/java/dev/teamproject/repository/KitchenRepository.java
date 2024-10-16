package dev.teamproject.repository;

import dev.teamproject.model.Kitchen; // Importing specific class
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository; // Importing specific class
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Kitchen entities from the database.
 * It extends JpaRepository to provide CRUD operations.
 * 
 * <p>This repository provides methods to find kitchens by name and
 * by partial name matching.</p>
 */
@Repository
public interface KitchenRepository extends JpaRepository<Kitchen, Long> {
  
  Optional<Kitchen> findByName(String name);
  
  List<Kitchen> findByNameContaining(String namePart);

  Optional<Kitchen> findByKitchenId(long id);

  List<Kitchen> findTop20ByOrderByRatingDesc();
}
