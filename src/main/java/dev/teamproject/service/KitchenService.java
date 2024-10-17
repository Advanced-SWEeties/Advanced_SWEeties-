package dev.teamproject.service;

import dev.teamproject.model.Kitchen;
import dev.teamproject.model.TempInfo;
import java.util.List;

/**
 * Interface for Kitchen Service.
 * This interface defines the contract for kitchen-related operations.
 * Implementations of this interface will provide specific business logic
 * related to kitchen management.
 */
public interface KitchenService {
  List<Kitchen> listAllKitchens();

  List<TempInfo> fetchAllKitchens();
}
