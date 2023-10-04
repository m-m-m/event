/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event.impl;

import io.github.mmm.base.exception.GlobalExceptionHandler;
import io.github.mmm.event.AbstractEventBus;
import io.github.mmm.event.EventBus;

/**
 * This is the default implementation of {@link EventBus}.
 */
public class EventBusImpl extends AbstractEventBus {

  private volatile boolean dispatching;

  /**
   * The constructor.
   */
  public EventBusImpl() {

    this(null);
  }

  /**
   * The constructor.
   *
   * @param errorHandler the {@link GlobalExceptionHandler} instance.
   */
  protected EventBusImpl(GlobalExceptionHandler errorHandler) {

    super(errorHandler);
  }

  @Override
  protected void triggerDispatchEvents() {

    // not synchronized - may call dispatchEvents() parallel
    // since we are using a concurrent queue, events will get properly dispatched
    // in the worst case and event could overtake if all goes badly wrong...
    if (!this.dispatching) {
      this.dispatching = true;
      dispatchEvents();
      this.dispatching = false;
    }
  }

}
