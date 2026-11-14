/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

/// Interface for a source of events that allows to [add][#addListener(EventListener)] and
/// [remove][#removeListener(EventListener)] [EventListener]s.
///
/// @param <E> the type of the events to [send][EventListener#onEvent(Object)].
/// @param <L> the type of the [listeners][EventListener].
/// @since 1.0.0
// Java generics are kind of buggy, can not use "L extends EventListener<? super E>".
public interface EventSource<E, L extends EventListener<?/* super E */> > {

  /// Adds an [EventListener] which will be notified whenever the an event occurs (something changes). If the same
  /// listener is added more than once, it will be notified more than once. The same [EventListener] instance may
  /// be registered for different `EventSource`s.
  ///
  /// By default the `EventSource` stores a strong reference to the [EventListener] which will prevent the
  /// listener from being garbage collected. You then need to [remove][#removeListener(EventListener)] that
  /// [EventListener] after use to avoid memory leaks. For convenience your may also use
  /// [#addWeakListener(EventListener)] instead to enforce that a [java.lang.ref.WeakReference] is used
  /// internally.
  ///
  /// @param listener the [EventListener] to register.
  ///
  /// @see #addListener(EventListener, boolean)
  /// @see #removeListener(EventListener)
  /// @see EventListener#isMatchedUsingEquals()
  default void addListener(L listener) {

    addListener(listener, false);
  }

  /// Same as [#addListener(EventListener, boolean)] with `weak`-flag set to `true`.
  ///
  /// @param listener the [EventListener] to register.
  /// @see #addListener(EventListener, boolean)
  default void addWeakListener(L listener) {

    addListener(listener, true);
  }

  /// Adds an [EventListener] which will be notified whenever the an event occurs (something changes). If the same
  /// listener is added more than once, it will be notified more than once. The same [EventListener] instance may
  /// be registered for different `EventSource`s.
  ///
  /// @param listener the [EventListener] to register.
  /// @param weak - `true` if the [EventListener] may be garbage collected without being
  ///        [removed][EventSource#removeListener(EventListener)] via a [java.lang.ref.WeakReference],
  ///        `false` otherwise (if the listener will be associated using a strong reference). When providing
  ///        `true` here (use [java.lang.ref.WeakReference]), you need to store a reference to your
  ///        registered [EventListener] yourself in the owning parent object so it is not garbage-collected too
  ///        early.
  /// @see #addWeakListener(EventListener)
  void addListener(L listener, boolean weak);

  /// This method removes an [EventListener]. If the [EventListener] was not registered before this method
  /// does not do any change. Otherwise the first matching [EventListener] will be removed. So if you
  /// [added][#addListener(EventListener)] an [EventListener] multiple times, only the first occurrence will
  /// be removed.
  /// For performance reasons [EventListener#isMatchedUsingEquals()] returns `false` by default. To force the
  /// usage of [equals][Object#equals(Object)] instead, ensure your [EventListener] implementation overrides
  /// [Object#equals(Object)] and [EventListener#isMatchedUsingEquals()] returning `true`.
  ///
  /// @param listener is the [EventListener] to unregister.
  /// @return `true` if the given `listener` has successfully been removed, `false` if the
  ///         `listener` was NOT [registered][#addListener(EventListener)].
  boolean removeListener(L listener);

}
