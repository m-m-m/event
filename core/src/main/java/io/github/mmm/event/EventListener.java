/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

/**
 * Interface for a generic event listener.
 *
 * @param <E> the type of the {@link #onEvent(Object) handled} events.
 * @since 1.0.0
 */
@FunctionalInterface
public interface EventListener<E> /* extends java.util.EventListener */ {

  /**
   * @return {@code true} if to match using {@link Object#equals(Object) equals}, {@code false} to match via object
   *         identity (compared using {@code ==}). For better performance the default is {@code false}. If you implement
   *         this interface as a class overriding {@link Object#equals(Object) equals} in order to being able to
   *         {@link EventSource#removeListener(EventListener) remove} the {@link EventListener} without keeping its
   *         reference by creating a new {@link Object#equals(Object) equal} {@link EventListener} instance, then you
   *         simply need to override this method returning {@code true}.
   */
  default boolean isMatchedUsingEquals() {

    return false;
  }

  /**
   * This method is called if an event occurred. <br>
   * <b>WARNING:</b><br>
   * Depending on the implementation of {@link EventSource} it may NOT be legal to
   * {@link EventSource#addListener(EventListener) add} or {@link EventSource#removeListener(EventListener) remove}
   * listeners during the call of this method as this may lead to a dead-lock.
   *
   * @param event is the event that notifies about something that happened.
   */
  void onEvent(E event);

}
