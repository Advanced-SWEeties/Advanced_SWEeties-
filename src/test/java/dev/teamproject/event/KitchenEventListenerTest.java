package dev.teamproject.event;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.teamproject.model.Kitchen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@link KitchenEventListener} class.
 * 
 * <p>
 * This test suite verifies the behavior of the event listener when a 
 * {@link KitchenCreatedEvent} is fired. It checks that the listener 
 * correctly handles the event and interacts with the mocked event data.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class KitchenEventListenerTest {

  @InjectMocks
  private KitchenEventListener kitchenEventListener;

  @Mock
  private KitchenCreatedEvent kitchenCreatedEvent;

  @Mock
  private Kitchen kitchen; // Mock the Kitchen object

  /**
   * Sets up the necessary mocks before each test.
   * Initializes the mocks using Mockito's {@link MockitoAnnotations} utility.
   */
  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Tests the handling of a {@link KitchenCreatedEvent} by the 
   * {@link KitchenEventListener}. Verifies that the listener correctly
   * invokes the event's methods and interacts with the mocked objects.
   */
  @Test
  public void testHandleKitchenCreatedEvent() {
    // Arrange: Set up the mock event
    when(kitchenCreatedEvent.getKitchen()).thenReturn(kitchen);

    // Act: Trigger the event
    kitchenEventListener.handleEmployeeCreatedEvent(kitchenCreatedEvent);

    // Assert: Verify the output or behavior
    verify(kitchenCreatedEvent, times(1)).getKitchen();
  }
}
