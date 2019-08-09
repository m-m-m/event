/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.event;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * {@link EventListener} that wraps an original {@link EventListener} using a {@link WeakReference} so that the original
 * can be gargabe collected without being {@link net.sf.mmm.event.EventSource#removeListener(EventListener)
 * removed}.
 *
 * @param <E> the type of the {@link EventListener#onEvent(Object) handled} events.
 * @since 1.0.0
 */
class WeakEventListener<E> implements EventListener<E> {

  private final EventSourceAdapter<E, ?> source;

  final WeakReference<EventListener<E>> ref;

  /**
   * The constructor.
   *
   * @param source the {@link EventSourceAdapter}.
   * @param listener the original listener to wrap.
   */
  public WeakEventListener(EventSourceAdapter<E, ?> source, EventListener<E> listener) {

    super();
    Objects.requireNonNull(source, "source");
    Objects.requireNonNull(listener, "listener");
    this.source = source;
    this.ref = new WeakReference<>(listener);
  }

  @Override
  public boolean isWeak() {

    return true;
  }

  /**
   * @return {@code true} if the wrapped {@link EventListener} has been garbage collected, {@code false} otherwise.
   */
  public boolean wasGarbageCollected() {

    return (this.ref.get() == null);
  }

  @Override
  public void onEvent(E event) {

    EventListener<E> listener = this.ref.get();
    if (listener != null) {
      listener.onEvent(event);
    } else {
      this.source.removeListener(this);
    }
  }

  /**
   * @param count the current size of listeners. Has to be less or equal to the length of the given {@code listeners}
   *        array.
   * @param listeners the array of event listeners. May be modified by this method.
   * @return the new size of listeners.
   */
  static int trim(int count, EventListener<?>[] listeners) {

    for (int i = 0; i < count; i++) {
      if (listeners[i].isWeak() && (listeners[i] instanceof WeakEventListener)) {
        if (((WeakEventListener<?>) listeners[i]).wasGarbageCollected()) {
          int remaining = count - i - 1;
          if (remaining > 0) {
            System.arraycopy(listeners, i + 1, listeners, i, remaining);
          }
          count--;
          listeners[count] = null;
          i--;
        }
      }
    }
    return count;
  }
}
