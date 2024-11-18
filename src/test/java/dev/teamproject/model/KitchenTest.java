package dev.teamproject.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class KitchenTest {
  @Test
  public void equals_NullObjectTest() {
    Kitchen kitchen = Kitchen.builder()
        .kitchenId(1L)
        .name("Kitchen1")
        .address("somewhere")
        .build();

    assertFalse(kitchen.equals(null), "null case, return false");
  }

  @Test
  public void equals_differentClassTest() {
    Kitchen kitchen = Kitchen.builder()
        .kitchenId(1L)
        .name("Kitchen1")
        .address("somewhere")
        .build();

    String otherClassObject = "I am not a Kitchen object";
    assertFalse(kitchen.equals(otherClassObject), "different class case, return false");
  }

  @Test
  public void hashCode_ConsistentGeneration() {
    Kitchen kitchen = Kitchen.builder()
        .kitchenId(1L)
        .name("Kitchen1")
        .address("somewhere")
        .build();

    int expectedHashCode = kitchen.getClass().hashCode();
    assertEquals(expectedHashCode, kitchen.hashCode(), "the hascode should be the same");
  }
}