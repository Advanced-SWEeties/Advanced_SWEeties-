package dev.teamproject.repository;

import dev.teamproject.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing User entities from the database.
 * It extends JpaRepository to provide CRUD operations.
 * This repository provides methods to find relevant User information.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
