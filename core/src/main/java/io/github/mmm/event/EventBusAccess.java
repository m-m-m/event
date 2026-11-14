package io.github.mmm.event;

import java.util.ServiceLoader;

import io.github.mmm.base.service.ServiceHelper;

/// Class giving [global access][#get()] to the [EventBus].
public final class EventBusAccess {

  private static final EventBus EVENT_BUS = ServiceHelper.singleton(ServiceLoader.load(EventBus.class), false);

  private EventBusAccess() {

  }

  /// @return the [EventBus] instance.
  public static EventBus get() {

    return EVENT_BUS;
  }

}
