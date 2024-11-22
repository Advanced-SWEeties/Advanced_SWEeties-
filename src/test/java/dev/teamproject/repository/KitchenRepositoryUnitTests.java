package dev.teamproject.repository;

import dev.teamproject.model.Kitchen;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Unit testing for kitchen Repository.
 */
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class KitchenRepositoryUnitTests {

  @Autowired
  private KitchenRepository kitchenRepository;

  /**
   * Set up three kitchens before each test to ensure consistency.
   */
  @BeforeEach
  public void setup() {
    kitchenRepository.deleteAll();
    Kitchen kitchen1 = Kitchen.builder().name("Kitchen1").address("some place")
            .contactPhone("1234567890").build();
    Kitchen kitchen2 = Kitchen.builder().name("Kitchen2").address("some place")
            .contactPhone("1234567890").build();
    Kitchen kitchen3 = Kitchen.builder().name("Kitchen3").address("some place")
            .contactPhone("1234567890").build();
    kitchenRepository.save(kitchen1);
    kitchenRepository.save(kitchen2);
    kitchenRepository.save(kitchen3);
  }

  @AfterEach
  public void cleanup() {
    kitchenRepository.deleteAll();
  }

  @Test
  @DisplayName("Test 1: Save Kitchen Test")
  @Order(1)
  @Transactional
  public void testSaveKitchen() {
    Kitchen kitchen = Kitchen.builder().name("Kitchen4")
            .address("another place").contactPhone("0987654321").build();
    kitchenRepository.save(kitchen);
    System.out.println(kitchenRepository.findAll());
    System.out.println(kitchen);
    Assertions.assertThat(kitchen.getKitchenId()).isGreaterThan(0);
  }

  @Test
  @DisplayName("Test 2: Get Kitchen By Id Test")
  @Order(2)
  public void testGetKitchenById() {
    System.out.println(kitchenRepository.findAll());
    Long start = kitchenRepository.findFirstByOrderByKitchenIdAsc().getKitchenId();
    Optional<Kitchen> optionalKitchen = kitchenRepository.findByKitchenId(start + 1);
    Assertions.assertThat(optionalKitchen).isPresent();
    Kitchen kitchen = optionalKitchen.get();
    System.out.println(kitchen);
    Assertions.assertThat(kitchen.getKitchenId()).isEqualTo(start + 1);
  }

  @Test
  @DisplayName("Test 3: Get Kitchen By Name Test")
  @Order(3)
  public void getKitchenByNameTest() {
    Long start = kitchenRepository.findFirstByOrderByKitchenIdAsc().getKitchenId();
    Optional<Kitchen> optionalKitchen = kitchenRepository.findByName("Kitchen1");
    Assertions.assertThat(optionalKitchen).isPresent();
    Kitchen kitchen = optionalKitchen.get();
    System.out.println(kitchen);
    Assertions.assertThat(kitchen.getKitchenId()).isEqualTo(start);
  }

  @Test
  @DisplayName("Test 4: Get All Kitchens Test")
  @Order(4)
  public void getListOfKitchensTest() {
    List<Kitchen> kitchens = kitchenRepository.findAll();
    for (Kitchen kitchen : kitchens) {
      System.out.println(kitchen);
    }
    Assertions.assertThat(kitchens.size()).isEqualTo(3);
  }

  @Test
  @DisplayName("Test 5: Get Similar Kitchens By Name Test")
  @Order(5)
  public void getListOfSimilarKitchensTest() {
    List<Kitchen> kitchens = kitchenRepository.findByNameContaining("Kit");
    for (Kitchen kitchen : kitchens) {
      System.out.println(kitchen);
    }
    Assertions.assertThat(kitchens.size()).isEqualTo(3);
  }

  @Test
  @Order(6)
  @DisplayName("Test 6: Update Kitchen Test By ID")
  @Transactional
  public void updateKitchenTest() {
    System.out.println(kitchenRepository.findAll());
    Long start = kitchenRepository.findFirstByOrderByKitchenIdAsc().getKitchenId();
    Optional<Kitchen> optionalKitchen = kitchenRepository.findById(start);
    Assertions.assertThat(optionalKitchen).isPresent();
    Kitchen kitchen = optionalKitchen.get();
    kitchen.setContactPhone("2345678901");
    Kitchen kitchenUpdated = kitchenRepository.save(kitchen);

    System.out.println(kitchenUpdated);
    Assertions.assertThat(kitchenUpdated.getContactPhone()).isEqualTo("2345678901");
  }

  @Test
  @Order(7)
  @DisplayName("Test 7: Delete Kitchen Test By ID")
  @Transactional
  public void deleteKitchenTest() {
    System.out.println(kitchenRepository.findAll());
    Long start = kitchenRepository.findFirstByOrderByKitchenIdAsc().getKitchenId();
    kitchenRepository.deleteById(start);
    Optional<Kitchen> kitchenOptional = kitchenRepository.findById(start);
    Assertions.assertThat(kitchenOptional).isEmpty();
  }
}