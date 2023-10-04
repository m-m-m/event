/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

/**
 * Implementation of {@link EventSource}.
 *
 * @param <E> the type of the events to {@link EventListener#onEvent(Object) send}.
 * @param <L> the type of the {@link EventListener listeners}.
 * @since 1.0.0
 */
public abstract class AbstractEventSource<E, L extends EventListener<?/* super E */> > implements EventSource<E, L> {

  /**
   * The constructor.
   */
  public AbstractEventSource() {

    super();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void addListener(L listener, boolean weak) {

    EventListener<E> l = (EventListener<E>) listener;
    if (weak) {
      l = l.weak(this);
    }
    doAddListener(l);
  }

  /**
   * @param listener the {@link EventListener} to add.
   */
  protected abstract void doAddListener(EventListener<E> listener);

  /**
   * @param event the event to {@link EventListener#onEvent(Object) send} to all {@link #addListener(EventListener)
   *        registered} {@link EventListener}s.
   * @return {@code true} if the event has actually been dispatched, {@code false} otherwise (no listener was
   *         {@link #addListener(EventListener) registered} for the event).
   */
  protected abstract boolean fireEvent(E event);

}
