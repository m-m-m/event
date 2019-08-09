/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.event.base;

import net.sf.mmm.event.api.EventListener;
import net.sf.mmm.event.api.EventSource;

/**
 * Implementation of {@link EventSource}.
 *
 * @param <E> the type of the events to {@link EventListener#onEvent(Object) send}.
 * @param <L> the type of the {@link EventListener listeners}.
 * @since 1.0.0
 */
public abstract class AbstractEventSource<E, L extends EventListener<? super E>> implements EventSource<E, L> {

  private EventSourceAdapter<E, L> eventAdapter;

  /**
   * The constructor.
   */
  public AbstractEventSource() {

    super();
    this.eventAdapter = EventSourceAdapter.empty();
  }

  @Override
  public void addListener(L listener, boolean weak) {

    this.eventAdapter = this.eventAdapter.addListener(listener, weak);
  }

  @Override
  public boolean removeListener(L listener) {

    EventSourceAdapter<E, L> adapter = this.eventAdapter.removeListener(listener);
    if (adapter == null) {
      return false;
    }
    this.eventAdapter = adapter;
    return true;
  }

  /**
   * @return {@code true} if at least one {@link EventListener} is {@link #addListener(EventListener, boolean)
   *         registered}, {@code false} otherwise.
   */
  protected boolean hasListeners() {

    return this.eventAdapter.hasListeners();
  }

  /**
   * @param event the event to {@link EventListener#onEvent(Object) send} to all {@link #addListener(EventListener)
   *        registered} {@link EventListener}s.
   */
  protected void fireEvent(E event) {

    this.eventAdapter.fireEvent(event);
  }

}
