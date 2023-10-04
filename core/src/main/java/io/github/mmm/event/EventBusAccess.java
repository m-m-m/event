package io.github.mmm.event;

import java.util.ServiceLoader;

import io.github.mmm.base.config.ServiceHelper;

/**
 * Class giving {@link #get() global access} to the {@link EventBus}.
 */
public final class EventBusAccess {

  private static final EventBus EVENT_BUS = ServiceHelper.singleton(ServiceLoader.load(EventBus.class), false);

  private EventBusAccess() {

  }

  /**
   * @return the {@link EventBus} instance.
   */
  public static EventBus get() {

    return EVENT_BUS;
  }

}
