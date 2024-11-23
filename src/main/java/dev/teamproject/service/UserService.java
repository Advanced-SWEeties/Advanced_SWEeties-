package dev.teamproject.service;

import dev.teamproject.model.User;
import dev.teamproject.model.Kitchen;
import dev.teamproject.model.UserLocation;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * The interface for User Service.
 * This interface defines methods for kitchen-related operations.
 * Implementations of this interface will provide specific logic to retrieve
 * relevant information for the user.
 */
public interface UserService extends UserDetailsService  {
  UserLocation getUserLocation(String address);

  List<Kitchen> getNearestKitchens(String address, List<Kitchen> allKitchens, int count);

  Optional<User> getUserById(Long userId); // New method to get a user by ID

  void saveUser(User user); // New method to save a user

  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

  void deleteUser(Long userId); // New method to save a user
}