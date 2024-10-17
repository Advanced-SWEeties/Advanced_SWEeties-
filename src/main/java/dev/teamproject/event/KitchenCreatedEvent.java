package dev.teamproject.event;

import dev.teamproject.model.Kitchen;
import org.springframework.context.ApplicationEvent;

/**
 *  class for create Kitchen event.
 */
public class KitchenCreatedEvent extends ApplicationEvent {
  private Kitchen kitchen;

  public KitchenCreatedEvent(Object source, Kitchen kitchen) {
    super(source);
    this.kitchen = kitchen;
  }

  public Kitchen getKitchen() {
    return kitchen;
  }
}