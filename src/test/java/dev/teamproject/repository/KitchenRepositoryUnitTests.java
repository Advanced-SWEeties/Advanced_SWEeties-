package dev.teamproject.repository;

import dev.teamproject.model.Kitchen;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;


/**
 * Unit testing for kitchen Repository.
 */
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class KitchenRepositoryUnitTests {
  @Autowired
  private KitchenRepository kitchenRepository;

  @Test
  @DisplayName("Test 1:Save Kitchen Test")
  @Order(1)
  @Rollback(value = false)
  public void testSaveKitchen() {
    Kitchen kitchen =
            Kitchen.builder().name("Kitchen1").address("some place")
                            .contactPhone("1234567890").build();
    kitchenRepository.save(kitchen);
    System.out.println(kitchen);
    Assertions.assertThat(kitchen.getKitchenId()).isGreaterThan(0);
    Kitchen kitchen1 =
            Kitchen.builder().name("Kitchen2").address("some place")
                    .contactPhone("1234567890").build();
    kitchenRepository.save(kitchen1);
    System.out.println(kitchen1);
    Assertions.assertThat(kitchen1.getKitchenId()).isGreaterThan(1);
    Kitchen kitchen2 =
            Kitchen.builder().name("Kitchen3").address("some place")
                    .contactPhone("1234567890").build();
    kitchenRepository.save(kitchen2);
    System.out.println(kitchen2);
    Assertions.assertThat(kitchen2.getKitchenId()).isGreaterThan(2);
  }

  @Test
  @DisplayName("Test 2:get Kitchen Test By Id")
  @Order(2)
  public void testGetKitchenById() {
    Kitchen kitchen = kitchenRepository.findByKitchenId(1L).get();

    System.out.println(kitchen);
    Assertions.assertThat(kitchen.getKitchenId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Test 3:get Kitchen Test By Name")
  @Order(2)
  public void getKitchenByNameTest() {
    Kitchen kitchen = kitchenRepository.findByName("Kitchen1").get();

    System.out.println(kitchen);
    Assertions.assertThat(kitchen.getKitchenId()).isEqualTo(1L);
    Assertions.assertThat(kitchen.getName()).isEqualTo("Kitchen1");
  }

  @Test
  @DisplayName("Test 4:get all Kitchen Test By Id")
  @Order(3)
  public void getListOfKitchensTest() {

    List<Kitchen> kitchens = kitchenRepository.findAll();

    for (Kitchen kitchen : kitchens) {
      System.out.println(kitchen);
    }
    Assertions.assertThat(kitchens.size()).isEqualTo(3);

  }

  @Test
  @DisplayName("Test 5:get similar Kitchen Test By Name")
  @Order(4)
  public void getListOfSimilarKitchensTest() {

    List<Kitchen> kitchens = kitchenRepository.findByNameContaining("Kit");

    for (Kitchen kitchen : kitchens) {
      System.out.println(kitchen);
    }
    Assertions.assertThat(kitchens.size()).isEqualTo(3);

  }

  @Test
  @Order(5)
  @DisplayName("Test 6: update Kitchen Test By ID")
  @Rollback(value = false)
  public void updateKitchenTest() {


    Kitchen kitchen = kitchenRepository.findById(1L).get();
    kitchen.setContactPhone("2345678901");
    Kitchen kitchenUpdated =  kitchenRepository.save(kitchen);


    System.out.println(kitchenUpdated);
    Assertions.assertThat(kitchenUpdated.getContactPhone()).isEqualTo("2345678901");

  }

  @Test
  @Order(6)
  @DisplayName("Test 7: delete Kitchen Test By ID")
  @Rollback(value = false)
  public void deleteKitchenTest() {

    kitchenRepository.deleteById(1L);
    Optional<Kitchen> kitchenOptional = kitchenRepository.findById(1L);

    Assertions.assertThat(kitchenOptional).isEmpty();
  }

  //TODO top rated test
}
