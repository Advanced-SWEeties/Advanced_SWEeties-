package dev.teamproject.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.teamproject.model.Kitchen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KitchenCreatedEventTest {

  Kitchen kitchen;
  Object source;

  @BeforeEach
  public void setup() {
    kitchen = Kitchen.builder()
        .kitchenId(1L)
        .name("Kitchen1")
        .address("some place")
        .contactPhone("1234567890").build();

    source = new Object();

  }


  @Test
  void kitchenCreatedEventSourceTest() {

    KitchenCreatedEvent event = new KitchenCreatedEvent(source, kitchen);

    assertEquals(source, event.getSource(), "Source should match the one provided");
  }

  @Test
  void testKitchenCreatedEvent() {

    KitchenCreatedEvent event = new KitchenCreatedEvent(source, kitchen);

    assertEquals(kitchen, event.getKitchen(), "Kitchen should match the one provided");
  }



}