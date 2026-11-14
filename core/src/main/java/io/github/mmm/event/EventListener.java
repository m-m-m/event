/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

import io.github.mmm.event.impl.WeakEventListener;

/// Interface for a generic event listener.
///
/// @param <E> the type of the [handled][#onEvent(Object)] events.
/// @since 1.0.0
@FunctionalInterface
public interface EventListener<E> /* extends java.util.EventListener */ {

  /// @return `true` if to match using [equals][Object#equals(Object)], `false` to match via object
  ///         identity (compared using `==`). For better performance the default is `false`. If you implement
  ///         this interface as a class overriding [equals][Object#equals(Object)] in order to being able to
  ///         [remove][EventSource#removeListener(EventListener)] the [EventListener] without keeping its
  ///         reference by creating a new [equal][Object#equals(Object)] [EventListener] instance, then you
  ///         simply need to override this method returning `true`.
  default boolean isMatchedUsingEquals() {

    return false;
  }

  /// This method is called if an event occurred.
  /// **WARNING:**
  /// Depending on the implementation of [EventSource] it may NOT be legal to
  /// [add][EventSource#addListener(EventListener)] or [remove][EventSource#removeListener(EventListener)]
  /// listeners during the call of this method as this may lead to a dead-lock.
  ///
  /// @param event is the event that notifies about something that happened.
  void onEvent(E event);

  /// @param source the [EventSource].
  /// @return an instance of [EventListener] using a [java.lang.ref.WeakReference] so that this original
  ///         [EventListener] can be gargabe collected without being
  ///         [removed][EventSource#removeListener(EventListener)] manually.
  default EventListener<E> weak(EventSource<E, ?> source) {

    return new WeakEventListener<>(source, this);
  }

  /// @return the raw [EventListener] that may be wrapped (e.g. via [#weak(EventSource)]).
  default EventListener<E> unwrap() {

    return this;
  }

  /// @param listener the registered [EventListener] to match with.
  /// @return `true` if this [EventListener] matches the given [EventListener], `false`
  ///         otherwise.
  default boolean matches(EventListener<?> listener) {

    listener = listener.unwrap();
    if (this == listener) {
      return true;
    } else if ((listener.isMatchedUsingEquals()) && listener.equals(this)) {
      return true;
    }
    return false;
  }
}
