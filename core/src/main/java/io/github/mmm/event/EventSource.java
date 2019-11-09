/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

/**
 * Interface for a source of events that allows to {@link #addListener(EventListener) add} and
 * {@link #removeListener(EventListener) remove} {@link EventListener}s.<br>
 *
 * @param <E> the type of the events to {@link EventListener#onEvent(Object) send}.
 * @param <L> the type of the {@link EventListener listeners}.
 * @since 1.0.0
 */
// Java generics are kind of buggy, can not use "L extends EventListener<? super E>".
public interface EventSource<E, L extends EventListener<?/* super E */> > {

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

    addListener(listener, false);
  }

  /**
   * Same as {@link #addListener(EventListener, boolean)} with {@code weak}-flag set to {@code true}.
   *
   * @param listener the {@link EventListener} to register.
   * @see #addListener(EventListener, boolean)
   */
  default void addWeakListener(L listener) {

    addListener(listener, true);
  }

  /**
   * Adds an {@link EventListener} which will be notified whenever the an event occurs (something changes). If the same
   * listener is added more than once, it will be notified more than once. The same {@link EventListener} instance may
   * be registered for different {@code EventSource}s.
   *
   * @param listener the {@link EventListener} to register.
   * @param weak - {@code true} if the {@link EventListener} may be garbage collected without being
   *        {@link EventSource#removeListener(EventListener) removed} via a {@link java.lang.ref.WeakReference},
   *        {@code false} otherwise (if the listener will be associated using a strong reference). When providing
   *        {@code true} here (use {@link java.lang.ref.WeakReference}), you need to store a reference to your
   *        registered {@link EventListener} yourself in the owning parent object so it is not garbage-collected too
   *        early.
   * @see #addWeakListener(EventListener)
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
