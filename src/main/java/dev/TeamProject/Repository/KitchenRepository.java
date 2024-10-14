package dev.TeamProject.Repository;

import dev.TeamProject.Model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface KitchenRepository extends JpaRepository<Kitchen, Long> {
  Optional<Kitchen> findByName(String name);
  List<Kitchen> findByNameContaining(String namePart);
}
