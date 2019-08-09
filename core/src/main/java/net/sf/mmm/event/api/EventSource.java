/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.event.api;

/**
 * Interface for a source of events that allows to {@link #addListener(EventListener) add} and
 * {@link #removeListener(EventListener) remove} {@link EventListener}s.<br>
 *
 * @param <E> the type of the events to {@link EventListener#onEvent(Object) send}.
 * @param <L> the type of the {@link EventListener listeners}.
 * @since 1.0.0
 */
public interface EventSource<E, L extends EventListener<? super E>> {

  /**
   * Adds an {@link EventListener} which will be notified whenever the an event occurs (something changes). If the same
   * listener is added more than once, it will be notified more than once. The same {@link EventListener} instance may
   * be registered for different {@code EventSource}s.
   * <p>
   * By default the {@code EventSource} stores a strong reference to the {@link EventListener} which will prevent the
   * listener from being garbage collected. You then need to {@link #removeListener(EventListener) remove} that
   * {@link EventListener} after use to avoid memory leaks. For convenience your may also use
   * {@link #addWeakListener(EventListener)} instead to enforce that a {@link java.lang.ref.WeakReference} is used
   * internally.
   *
   * @param listener the {@link EventListener} to register.
   *
   * @see #addListener(EventListener, boolean)
   * @see #removeListener(EventListener)
   * @see EventListener#isMatchedUsingEquals()
   */
  default void addListener(L listener) {

    addListener(listener, listener.isWeak());
  }

  /**
   * Same as {@link #addListener(EventListener, boolean)} with {@link EventListener#isWeak() weak}-flag set to
   * {@code true}.
   *
   * @param listener the {@link EventListener} to register.
   * @see EventListener#isWeak()
   */
  default void addWeakListener(L listener) {

    addListener(listener, true);
  }

  /**
   * Same as {@link #addListener(EventListener)} but with ability to override the {@link EventListener#isWeak()
   * weak}-flag. This is for convenience allowing to register a {@link EventListener#isWeak() weak}
   * {@link EventListener} provided as lambda expression. However, you still need to store the reference to that
   * {@link EventListener} in the owning object.
   *
   * @param listener the {@link EventListener} to register.
   * @param weak the {@link EventListener#isWeak() weak}-flag.
   */
  void addListener(L listener, boolean weak);

  /**
   * This method removes an {@link EventListener}. If the {@link EventListener} was not registered before this method
   * does not do any change. Otherwise the first matching {@link EventListener} will be removed. So if you
   * {@link #addListener(EventListener) added} an {@link EventListener} multiple times, only the first occurrence will
   * be removed.<br>
   * For performance reasons {@link EventListener#isMatchedUsingEquals()} returns {@code false} by default. To force the
   * usage of {@link Object#equals(Object) equals} instead, ensure your {@link EventListener} implementation overrides
   * {@link Object#equals(Object)} and {@link EventListener#isMatchedUsingEquals()} returning {@code true}.
   *
   * @param listener is the {@link EventListener} to unregister.
   * @return {@code true} if the given {@code listener} has successfully been removed, {@code false} if the
   *         {@code listener} was NOT {@link #addListener(EventListener) registered}.
   */
  boolean removeListener(L listener);

}
