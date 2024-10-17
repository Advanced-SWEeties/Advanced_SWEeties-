package dev.teamproject.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener for kitchenEvent.
 */
@Component
public class KitchenEventListener {
  @EventListener
  public void handleEmployeeCreatedEvent(KitchenCreatedEvent event) {
    // Callback logic here
    System.out.println("Kitchen created: " + event.getKitchen());
  }
}