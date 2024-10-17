package dev.teamproject.service;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.UserLocation;
import java.util.List;

/**
 * The interface for User Service.
 * This interface defines methods for kitchen-related operations.
 * Implementations of this interface will provide specific logic to retrieve
 * relevant information for the user.
 */
public interface UserService {
  UserLocation getUserLocation(String address);

  List<Kitchen> getNearestKitchens(String address, List<Kitchen> allKitchens, int count);
}
