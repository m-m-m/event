/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

import io.github.mmm.event.impl.WeakEventListener;

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

  /**
   * @param listener the {@link EventListener} to wrap (potentially).
   * @param weak - {@code true} to wrap as {@link WeakEventListener}, {@code false} otherwise.
   * @return the given {@link EventListener} or a {@link WeakEventListener} wrapping it in case {@code weak} is
   *         {@code true}.
   * @see EventSource#addListener(EventListener, boolean)
   */
  protected final EventListener<E> wrap(EventListener<E> listener, boolean weak) {

    if (weak) {
      return new WeakEventListener<>(this, listener);
    }
    return listener;
  }

  /**
   * @param <E> type of the event.
   * @param listener the {@link EventListener} to unwrap (potentially).
   * @return the given {@link EventListener} or the unwrapped {@link EventListener} if a {@link WeakEventListener} was
   *         given.
   */
  protected static <E> EventListener<E> unwrap(EventListener<E> listener) {

    if (listener == null) {
      return null;
    } else if (listener instanceof WeakEventListener) {
      return ((WeakEventListener<E>) listener).ref.get();
    }
    return listener;
  }

  /**
   * @param listener2remove the {@link EventListener} to {@link #removeListener(EventListener) remove}.
   * @param registeredListener the {@link #addListener(EventListener) registered} {@link EventListener} to match with.
   * @return {@code true} if the given {@link EventListener}s match, {@code false} otherwise.
   */
  protected static boolean matches(EventListener<?> listener2remove, EventListener<?> registeredListener) {

    registeredListener = AbstractEventSource.unwrap(registeredListener);
    if (listener2remove == registeredListener) {
      return true;
    } else if ((registeredListener.isMatchedUsingEquals()) && registeredListener.equals(listener2remove)) {
      return true;
    }
    return false;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void addListener(L listener, boolean weak) {

    doAddListener(wrap((EventListener) listener, weak));
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
