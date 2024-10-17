package dev.teamproject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dev.teamproject.client.CallbackClientService;
import dev.teamproject.model.Kitchen;
import dev.teamproject.repository.KitchenRepository;
import dev.teamproject.service.impl.KitchenServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit testing for kitchen Service.
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KitchenServiceUnitTests {
  @Mock
  private KitchenRepository kitchenRepository;

  @Mock
  private CallbackClientService callbackClientService;

  @InjectMocks
  private KitchenServiceImpl kitchenService;

  private Kitchen kitchen;

  /**
   * set ip a kitchen object before each test.
   */
  @BeforeEach
  public void setup() {
    kitchen = Kitchen.builder()
            .kitchenId(1L)
            .name("Kitchen1")
            .address("some place")
            .contactPhone("1234567890").build();
  }

  @Test
  @Order(1)
  public void saveKitchenTest() {
    // precondition
    given(kitchenRepository.save(kitchen)).willReturn(kitchen);

    //action
    System.out.println("CallbackClientService mock instance: " + callbackClientService);
    Kitchen savedKitchen = kitchenService.saveKitchen(kitchen);

    verify(callbackClientService).notifyExternalService(savedKitchen);

    // verify
    System.out.println(savedKitchen);
    assertThat(savedKitchen).isNotNull();
  }

  @Test
  @Order(2)
  public void getKitchenByIdTest() {
    // precondition
    given(kitchenRepository.findByKitchenId(1L)).willReturn(Optional.of(kitchen));

    // action
    Kitchen existingKitchen = kitchenService.getKitchenById(1L).get();

    // verify
    System.out.println(existingKitchen);
    assertThat(existingKitchen).isNotNull();

  }

  @Test
  @Order(2)
  public void getKitchenByNameTest() {
    // precondition
    given(kitchenRepository.findByName("Kitchen1")).willReturn(Optional.of(kitchen));

    // action
    Kitchen existingKitchen = kitchenService.getKitchenByName("Kitchen1").get();

    // verify
    System.out.println(existingKitchen);
    assertThat(existingKitchen).isNotNull();
  }


  @Test
  @Order(3)
  public void getAllKitchenTest() {
    Kitchen kitchen1 = Kitchen.builder()
            .kitchenId(2L)
            .name("Kitchen2")
            .address("some place")
            .contactPhone("1234567890").build();

    // precondition
    given(kitchenRepository.findAll()).willReturn(List.of(kitchen, kitchen1));

    // action
    List<Kitchen> kitchenList = kitchenService.getAllKitchens();

    // verify
    System.out.println(kitchenList);
    assertThat(kitchenList).isNotNull();
    assertThat(kitchenList.size()).isGreaterThan(1);
  }

  @Test
  @Order(3)
  public void searchKitchenTest() {
    Kitchen kitchen1 = Kitchen.builder()
            .kitchenId(2L)
            .name("Kitchen2")
            .address("some place")
            .contactPhone("1234567890").build();

    // precondition
    given(kitchenRepository.findByNameContaining("Kit")).willReturn(List.of(kitchen, kitchen1));

    // action
    List<Kitchen> kitchenList = kitchenService.searchKitchen("Kit");

    // verify
    System.out.println(kitchenList);
    assertThat(kitchenList).isNotNull();
    assertThat(kitchenList.size()).isGreaterThan(1);
  }

  @Test
  @Order(4)
  public void updateKitchenTest() {

    // precondition
    given(kitchenRepository.findByKitchenId(kitchen.getKitchenId()))
            .willReturn(Optional.of(kitchen));
    kitchen.setContactPhone("34567898");
    kitchen.setAddress("nowhere");
    given(kitchenRepository.save(kitchen)).willReturn(kitchen);

    // action
    Kitchen updatedKitchen = kitchenService.updateKitchen(kitchen, kitchen.getKitchenId());

    // verify
    System.out.println(updatedKitchen);
    assertThat(updatedKitchen.getContactPhone()).isEqualTo("34567898");
    assertThat(updatedKitchen.getAddress()).isEqualTo("nowhere");
  }

  @Test
  @Order(5)
  public void deleteKitchenTest() {

    // precondition
    given(kitchenRepository.findByKitchenId(kitchen.getKitchenId()))
            .willReturn(Optional.of(kitchen));
    willDoNothing().given(kitchenRepository).deleteById(kitchen.getKitchenId());

    // action
    kitchenService.deleteKitchen(kitchen.getKitchenId());

    // verify
    verify(kitchenRepository, times(1)).deleteById(kitchen.getKitchenId());
  }

  //TODO top rated restaurant and fetchAllKitchens
}
