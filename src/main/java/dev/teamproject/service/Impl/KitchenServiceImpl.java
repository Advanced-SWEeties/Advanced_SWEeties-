package dev.teamproject.service.Impl;

import dev.teamproject.model.*;
import dev.teamproject.repository.*;
import dev.teamproject.service.KitchenService; // Correct import
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of the KitchenService interface.
 * This service contains the business logic related to kitchen operations.
 */
@Service
public class KitchenServiceImpl implements KitchenService {
  private final KitchenRepository  kitchenRepository;

  @Autowired
  public KitchenServiceImpl(KitchenRepository kitchenRepository) {
    this.kitchenRepository = kitchenRepository;
  }
    // Implementation of KitchenService methods will go her
  @Override
  public Kitchen saveKitchen(Kitchen kitchen){
    Optional<Kitchen> toSave = kitchenRepository.findByName(kitchen.getName());
    if (toSave.isPresent()) {
      throw new RuntimeException("Kitchen already exists with given name: " + kitchen.getName());
    }
    Kitchen saved = kitchenRepository.save(kitchen);
    System.out.println("Kitchen named " + kitchen.getName() + " saved: " + saved);
    return saved;
  };

  @Override
  public List<Kitchen> getAllKitchens() {
    return kitchenRepository.findAll();
  }

  @Override
  public Optional<Kitchen> getKitchenById(long id) {
    Optional<Kitchen> toGet = kitchenRepository.findByKitchenId(id);
    if (toGet.isEmpty()) {
      throw new RuntimeException("Kitchen not exists with given id: " + id);
    }
    System.out.println("Kitchen with id " + id + " is got.");
    return toGet;
  }

  @Override
  public List<Kitchen> searchKitchen(String kitchenName) {
    List<Kitchen> kitchens = kitchenRepository.findByNameContaining(kitchenName);
    if (kitchens.isEmpty()) {
      throw new RuntimeException("All kitchen does not contain the given name: " + kitchenName);
    }
    System.out.println("List of Kitchens with name " + kitchenName + " are got.");
    return kitchens;
  }

  @Override
  public Optional<Kitchen> getKitchenByName(String kitchenName) {
    Optional<Kitchen> toGet = kitchenRepository.findByName(kitchenName);
    if (toGet.isEmpty()) {
      throw new RuntimeException("Kitchen not exists with given name: " + kitchenName);
    }
    System.out.println("Kitchen with name " + kitchenName + " is got.");
    return toGet;
  }

  @Override
  public Kitchen updateKitchen(Kitchen kitchen, long id) {
    Kitchen toUpdate = kitchenRepository.findByKitchenId(id)
            .orElseThrow(()-> new RuntimeException("Kitchen not exists with id: " + id));
    if (!kitchen.getName().isEmpty() && !Objects.equals(kitchen.getName(), toUpdate.getName())) {
      toUpdate.setName(kitchen.getName());
    }
    if (!kitchen.getAddress().isEmpty()
            && !Objects.equals(kitchen.getAddress(), toUpdate.getAddress())) {
      toUpdate.setAddress(kitchen.getAddress());
    }
    if (!kitchen.getContactEmail().isEmpty()
            && !Objects.equals(kitchen.getContactEmail(), toUpdate.getContactEmail())) {
      toUpdate.setContactEmail(kitchen.getContactEmail());
    }
    if (!kitchen.getContactPhone().isEmpty()
            && !Objects.equals(kitchen.getContactPhone(), toUpdate.getContactPhone())) {
      toUpdate.setContactPhone(kitchen.getContactPhone());
    }
    if (!kitchen.getAccessibilityFeatures().isEmpty()
            && !Objects.equals(kitchen.getAccessibilityFeatures(),
            toUpdate.getAccessibilityFeatures())) {
      toUpdate.setAccessibilityFeatures(kitchen.getAccessibilityFeatures());
    }
    if (!kitchen.getOperatingHours().isEmpty()
            && !Objects.equals(kitchen.getOperatingHours(), toUpdate.getOperatingHours())) {
      toUpdate.setOperatingHours(kitchen.getOperatingHours());
    }

    if (kitchen.getOperationalStatus() != null
            && kitchen.getOperationalStatus() != toUpdate.getOperationalStatus()) {
      toUpdate.setOperationalStatus(kitchen.getOperationalStatus());
    }

    kitchenRepository.save(toUpdate);
    return toUpdate;
  }

  @Override
  public void deleteKitchen(long id) {
    Optional<Kitchen> toDelete = kitchenRepository.findByKitchenId(id);
    if (toDelete.isEmpty()) {
      throw new RuntimeException("Kitchen not exists with given id: " + id);
    }
    kitchenRepository.deleteById(id);
    System.out.println("Kitchen with id " + id + " is deleted.");
  }

  @Override
  public List<Kitchen> topRatedKitchens() {
    return kitchenRepository.findTop20ByOrderByRatingDesc();
  }
}
