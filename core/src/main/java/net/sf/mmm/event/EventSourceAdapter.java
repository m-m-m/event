/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.event;

import java.util.Arrays;

/**
 * Adapter for {@link EventSource}.
 *
 * @param <E> the type of the events to send.
 * @param <L> the type of the {@link EventListener listeners}.
 * @since 1.0.0
 * @see EventSource
 * @see AbstractEventSource
 */
public abstract class EventSourceAdapter<E, L extends EventListener<? super E>> {

  private static final Empty EMPTY = new Empty();

  EventSourceAdapter() {

    super();
  }

  boolean matches(EventListener<? super E> remove, EventListener<? super E> registered) {

    if (remove == registered) {
      return true;
    } else if ((registered.isMatchedUsingEquals()) && registered.equals(remove)) {
      return true;
    } else if (registered.isWeak() && (registered instanceof WeakEventListener)) {
      WeakEventListener<? super E> weakListener = (WeakEventListener<? super E>) registered;
      EventListener<? super E> reference = weakListener.ref.get();
      if (reference != null) {
        return matches(remove, reference);
      }
    }
    return false;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  EventListener<? super E> wrap(EventListener<? super E> listener, boolean weak) {

    if (weak) {
      return new WeakEventListener(this, listener);
    }
    return listener;
  }

  @SuppressWarnings("unchecked")
  <T extends EventListener<? super E>> T unwrap(EventListener<? super E> listener) {

    if (listener == null) {
      return null;
    } else if (listener.isWeak() && (listener instanceof WeakEventListener)) {
      return (T) ((WeakEventListener<? super E>) listener).ref.get();
    }
    return (T) listener;
  }

  /**
   * @param listener - see {@link EventSource#addListener(EventListener)}.
   * @param weak - see {@link EventSource#addListener(EventListener, boolean)}.
   * @return this adapter itself or a new instance capable to handle more listeners.
   */
  public abstract EventSourceAdapter<E, L> addListener(EventListener<? super E> listener, boolean weak);

  /**
   * @param listener - see {@link EventSource#removeListener(EventListener)}.
   * @return {@code null} if the given {@link EventListener} was not registered and nothing changed, otherwise this
   *         adapter itself or a new instance capable to handle less listeners.
   */
  public abstract EventSourceAdapter<E, L> removeListener(EventListener<? super E> listener);

  /**
   * @param event the event to {@link EventListener#onEvent(Object) send} to all
   *        {@link #addListener(EventListener, boolean) registered} {@link EventListener}s.
   */
  public abstract void fireEvent(E event);

  void fireEvent(E event, EventListener<? super E> listener) {

    try {
      listener.onEvent(event);
    } catch (Exception e) {
      Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
    }
  }

  /**
   * @return {@code true} if at least one {@link EventListener} is {@link #addListener(EventListener, boolean)
   *         registered}, {@code false} otherwise.
   */
  public boolean hasListeners() {

    return (getListenerCount() > 0);
  }

  /**
   * @return the number of {@link #addListener(EventListener, boolean) registered} {@link EventListener}s.
   */
  public abstract int getListenerCount();

  /**
   * @param index the index of the requested {@link EventListener} in the range from {@code 0} to
   *        <code>{@link #getListenerCount()} - 1</code>.
   * @return the requested {@link EventListener} or {@code null} if index is out of bounds.
   */
  public abstract L getListener(int index);

  /**
   * @param index the index of the requested {@link EventListener} in the range from {@code 0} to
   *        <code>{@link #getListenerCount()} - 1</code>.
   * @return the requested {@link EventListener} or {@code null} if index is out of bounds.
   */
  public abstract EventListener<? super E> getRawListener(int index);

  /**
   * @param <E> the type of the events to send.
   * @param <L> the type of the {@link EventListener listeners}.
   * @return the empty {@link EventSourceAdapter}.
   */
  @SuppressWarnings("unchecked")
  public static <E, L extends EventListener<? super E>> EventSourceAdapter<E, L> empty() {

    return EMPTY;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static class Empty extends EventSourceAdapter {

    private Empty() {

      super();
    }

    @Override
    public EventSourceAdapter addListener(EventListener listener, boolean weak) {

      return new Single<>(wrap(listener, weak));
    }

    @Override
    public EventSourceAdapter removeListener(EventListener listener) {

      return null;
    }

    @Override
    public void fireEvent(Object event) {

    }

    @Override
    public boolean hasListeners() {

      return false;
    }

    @Override
    public int getListenerCount() {

      return 0;
    }

    @Override
    public EventListener getListener(int index) {

      return null;
    }

    @Override
    public EventListener getRawListener(int index) {

      return null;
    }

  }

  private static class Single<E, L extends EventListener<? super E>> extends EventSourceAdapter<E, L> {

    private final EventListener<? super E> listener;

    private Single(EventListener<? super E> listener) {

      super();
      this.listener = listener;
    }

    @Override
    public EventSourceAdapter<E, L> addListener(EventListener<? super E> eventListener, boolean weak) {

      return new Multi<>(this.listener, wrap(eventListener, weak));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventSourceAdapter<E, L> removeListener(EventListener<? super E> eventListener) {

      if (matches(eventListener, this.listener)) {
        return EMPTY;
      }
      return null;
    }

    @Override
    public void fireEvent(E event) {

      fireEvent(event, this.listener);
    }

    @Override
    public boolean hasListeners() {

      return true;
    }

    @Override
    public int getListenerCount() {

      return 1;
    }

    @Override
    public L getListener(int index) {

      if (index == 0) {
        return unwrap(this.listener);
      }
      return null;
    }

    @Override
    public EventListener<? super E> getRawListener(int index) {

      if (index == 0) {
        return this.listener;
      }
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static class Multi<E, L extends EventListener<? super E>> extends EventSourceAdapter<E, L> {

    private EventListener<? super E>[] listeners;

    private int listenerCount;

    private boolean locked;

    private Multi(EventListener<? super E> first, EventListener<? super E> second) {

      super();
      this.listeners = new EventListener[] { first, second };
      this.listenerCount = 2;
    }

    @Override
    public EventSourceAdapter<E, L> addListener(EventListener<? super E> listener, boolean weak) {

      int oldCapacity = this.listeners.length;
      if (this.locked) {
        int newCapacity = (this.listenerCount < oldCapacity) ? oldCapacity : (oldCapacity * 3) / 2 + 1;
        this.listeners = Arrays.copyOf(this.listeners, newCapacity);
      } else if (this.listenerCount == oldCapacity) {
        this.listenerCount = WeakEventListener.trim(this.listenerCount, this.listeners);
        if (this.listenerCount == oldCapacity) {
          int newCapacity = (oldCapacity * 3) / 2 + 1;
          this.listeners = Arrays.copyOf(this.listeners, newCapacity);
        }
      }
      this.listeners[this.listenerCount++] = listener;
      return this;
    }

    @Override
    public EventSourceAdapter<E, L> removeListener(EventListener<? super E> listener) {

      for (int i = 0; i < this.listenerCount; i++) {
        if (matches(listener, this.listeners[i])) {
          if (this.listenerCount == 2) {
            return new Single<>(this.listeners[1 - i]);
          } else {
            EventListener<? super E>[] oldListeners = this.listeners;
            if (this.locked) {
              this.listeners = new EventListener[this.listeners.length];
              System.arraycopy(oldListeners, 0, this.listeners, 0, i);
            }
            int remaining = this.listenerCount - i - 1;
            if (remaining > 0) {
              System.arraycopy(oldListeners, i + 1, this.listeners, i, remaining);
            }
            this.listenerCount--;
            if (!this.locked) {
              this.listeners[this.listenerCount] = null;
            }
          }
          return this;
        }
      }
      return null;
    }

    @Override
    public void fireEvent(E event) {

      try {
        this.locked = true;
        for (int i = 0; i < this.listenerCount; i++) {
          fireEvent(event, this.listeners[i]);
        }
      } finally {
        this.locked = false;
      }
    }

    @Override
    public boolean hasListeners() {

      return true;
    }

    @Override
    public int getListenerCount() {

      return this.listenerCount;
    }

    @Override
    public L getListener(int index) {

      if ((index >= 0) && (index < this.listenerCount)) {
        return unwrap(this.listeners[index]);
      }
      return null;
    }

    @Override
    public EventListener<? super E> getRawListener(int index) {

      if ((index >= 0) && (index < this.listenerCount)) {
        return this.listeners[index];
      }
      return null;
    }
  }

}
