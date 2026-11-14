/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

/// Implementation of [EventSource].
///
/// @param <E> the type of the events to [send][EventListener#onEvent(Object)].
/// @param <L> the type of the [listeners][EventListener].
/// @since 1.0.0
public abstract class AbstractEventSender<E, L extends EventListener<?/* super E */> >
    extends AbstractEventSource<E, L> {

  private EventSourceAdapter<E, L> eventAdapter;

  /// The constructor.
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

  /// @return `true` if at least one [EventListener] is [registered][#addListener(EventListener)],
  ///         `false` otherwise.
  protected boolean hasListeners() {

    return this.eventAdapter.hasListeners();
  }

  /// @return the [EventSourceAdapter].
  protected EventSourceAdapter<E, L> getEventAdapter() {

    return this.eventAdapter;
  }

  @Override
  protected boolean fireEvent(E event) {

    return this.eventAdapter.fireEvent(event);
  }

  /// Makes this event sender read-only so it discards all [added][#addListener(EventListener)]
  /// [EventListener]s and prevents future [adding][#addListener(EventListener)].
  /// **ATTENTION**: This is an internal method that shall only be used by code of this library and not by third-party
  /// users.
  ///
  /// @see EventSourceAdapter#readOnly()
  protected void makeReadOnly() {

    this.eventAdapter = EventSourceAdapter.readOnly();
  }

}
