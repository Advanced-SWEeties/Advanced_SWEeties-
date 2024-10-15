package dev.teamproject.repository;

import dev.teamproject.model;
import java.util;
import org.springframework.data.jpa.repository;
import org.springframework.stereotype;

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
}
