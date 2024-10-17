package dev.teamproject.client;

import dev.teamproject.event.KitchenCreatedEvent;
import dev.teamproject.model.Kitchen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 *  callback service for mocking in kitchen service.
 */
@Service
public class CallbackClientService {

  private final ApplicationEventPublisher eventPublisher;

  @Autowired
  public CallbackClientService(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  /**
   * publish the event to notify the kitchen service.
   *
   * @param kitchen the kitchen to saved
   */
  public void notifyExternalService(Kitchen kitchen) {
    // Publish the KitchenEvent
    KitchenCreatedEvent event = new KitchenCreatedEvent(this, kitchen);
    eventPublisher.publishEvent(event);
  }
}