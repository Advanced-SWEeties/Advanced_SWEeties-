package dev.teamproject.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dev.teamproject.event.KitchenCreatedEvent;
import dev.teamproject.model.Kitchen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;



class CallbackClientServiceTest {

  private ApplicationEventPublisher mockPublisher;
  private CallbackClientService callbackClientService;
  private Kitchen kitchen;

  @BeforeEach
  public void setup() {
    mockPublisher = mock(ApplicationEventPublisher.class);
    callbackClientService = new CallbackClientService(mockPublisher);

    kitchen = Kitchen.builder()
        .kitchenId(1L)
        .name("Kitchen1")
        .address("Some Place")
        .contactPhone("1234567890")
        .build();
  }

  @Test
  void testEventPublishingCalledOnce() {
    callbackClientService.notifyExternalService(kitchen);
    verify(mockPublisher, times(1)).publishEvent(any(KitchenCreatedEvent.class));
  }

  @Test
  void testPublishedEventIsNotNull() {
    callbackClientService.notifyExternalService(kitchen);
    ArgumentCaptor<KitchenCreatedEvent> eventCaptor =
        ArgumentCaptor.forClass(KitchenCreatedEvent.class);
    verify(mockPublisher).publishEvent(eventCaptor.capture());

    KitchenCreatedEvent publishedEvent = eventCaptor.getValue();
    assertNotNull(publishedEvent, "The published event should not be null");
  }

  @Test
  void testPublishedEventHasCorrectSource() {
    callbackClientService.notifyExternalService(kitchen);
    ArgumentCaptor<KitchenCreatedEvent> eventCaptor =
        ArgumentCaptor.forClass(KitchenCreatedEvent.class);
    verify(mockPublisher).publishEvent(eventCaptor.capture());

    KitchenCreatedEvent publishedEvent = eventCaptor.getValue();
    assertEquals(callbackClientService, publishedEvent.getSource(),
        "The event source should match the service instance");
  }

  @Test
  void testPublishedEventContainsCorrectKitchen() {
    callbackClientService.notifyExternalService(kitchen);

    ArgumentCaptor<KitchenCreatedEvent> eventCaptor =
        ArgumentCaptor.forClass(KitchenCreatedEvent.class);
    verify(mockPublisher).publishEvent(eventCaptor.capture());

    // Verify
    KitchenCreatedEvent publishedEvent = eventCaptor.getValue();
    assertNotNull(publishedEvent, "The published event should not be null");
    assertEquals(callbackClientService, publishedEvent.getSource(),
        "The event source should match the service instance");
    assertEquals(kitchen, publishedEvent.getKitchen(),
        "The event should contain the correct kitchen");
  }
}
