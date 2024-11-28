package dev.teamproject.repository;

import dev.teamproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity.
 * This interface provides methods to perform CRUD operations on User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
  // Optional method to find a user by username
  User findByUsername(String username);
}