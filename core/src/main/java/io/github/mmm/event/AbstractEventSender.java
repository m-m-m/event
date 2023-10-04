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
public abstract class AbstractEventSender<E, L extends EventListener<?/* super E */> >
    extends AbstractEventSource<E, L> {

  private EventSourceAdapter<E, L> eventAdapter;

  /**
   * The constructor.
   */
  public AbstractEventSender() {

    super();
    this.eventAdapter = EventSourceAdapter.empty();
  }

  @Override
  protected void doAddListener(EventListener<E> listener) {

    this.eventAdapter = this.eventAdapter.addListener(listener);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public boolean removeListener(L listener) {

    EventSourceAdapter<E, L> adapter = this.eventAdapter.removeListener((EventListener) listener);
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
   * @return the {@link EventSourceAdapter}.
   */
  protected EventSourceAdapter<E, L> getEventAdapter() {

    return this.eventAdapter;
  }

  /**
   * @param event the event to {@link EventListener#onEvent(Object) send} to all {@link #addListener(EventListener)
   *        registered} {@link EventListener}s.
   * @return {@code true} if the event has actually been dispatched, {@code false} otherwise (no listener was
   *         {@link #addListener(EventListener) registered} for the event).
   */
  @Override
  protected boolean fireEvent(E event) {

    return this.eventAdapter.fireEvent(event);
  }

}
