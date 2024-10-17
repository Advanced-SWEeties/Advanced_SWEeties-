package dev.teamproject.service;


import dev.teamproject.model.Kitchen;
import java.util.List;
import java.util.Optional;

/**
 * Interface for Kitchen Service.
 * This interface defines the contract for kitchen-related operations.
 * Implementations of this interface will provide specific business logic
 * related to kitchen management.
 */
public interface KitchenService {
  Kitchen saveKitchen(Kitchen kitchen);

  List<Kitchen> getAllKitchens();

  Optional<Kitchen> getKitchenById(long id);

  Optional<Kitchen> getKitchenByName(String kitchenName);

  Kitchen updateKitchen(Kitchen kitchen, long id);

  List<Kitchen> searchKitchen(String kitchenName);

  List<Kitchen> topRatedKitchens();

  void deleteKitchen(long id);
}
