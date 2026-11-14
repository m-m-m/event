/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

/// Implementation of [EventSource].
///
/// @param <E> the type of the events to [send][EventListener#onEvent(Object)].
/// @param <L> the type of the [listeners][EventListener].
/// @since 1.0.0
public abstract class AbstractEventSource<E, L extends EventListener<?/* super E */> > implements EventSource<E, L> {

  /// The constructor.
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

  /// @param listener the [EventListener] to add.
  protected abstract void doAddListener(EventListener<E> listener);

  /// @param event the event to [send][EventListener#onEvent(Object)] to all [registered][#addListener(EventListener)] [EventListener]s.
  /// @return `true` if the event has actually been dispatched, `false` otherwise (no listener was
  ///         [registered][#addListener(EventListener)] for the event).
  protected abstract boolean fireEvent(E event);

}
