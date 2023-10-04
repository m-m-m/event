/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

/**
 * This is the interface for an event bus. An event bus is a central place for {@link #sendEvent(Object) sending},
 * {@link #addListener(Class, EventListener) listening} to and {@link EventListener#onEvent(Object) receiving} events.
 * The {@link EventBus} allows to communicate between loosely coupled components in a smart and efficient way:
 * <ul>
 * <li>A component sending events only needs to know the {@link EventBus} but not the receivers of the events.</li>
 * <li>A component receiving events only needs to know the {@link EventBus} but not the sender of the events.</li>
 * </ul>
 * This way components can communicate via events without compile time dependency between them. All they need to see is
 * the {@link EventBus} and the event itself. <br>
 * <b>ATTENTION:</b><br>
 * This interface is designed as a stable API and standard for an event bus. However, there can be various
 * implementations of this interface with different aspects regarding concurrency, polymorphism, performance, etc. While
 * this interface will remain stable, we might change internals of the {@link EventBus} implementation. Further, you may
 * want to choose a different implementation of {@link EventBus} when you are inside a front-end application (UI) or a
 * back-end application (server). Therefore, it is possible to provide your own implementation of {@link EventBus} as a
 * Java module via {@link java.util.ServiceLoader}.<br>
 * <b>NOTE:</b><br>
 * The loose coupling makes flows less easy to see, understand and debug. You should only consider this approach for
 * components that should be decoupled by design. Do not get confused by the beauty of the event-bus pattern and avoid
 * using it where straight method calls should be preferred. <br>
 * E.g. if you have a user-interface with a navigation sub-dialog and various other dialogs they should communicate via
 * {@link EventBus} to update their views accordingly. However, a business component responsible to read and write
 * addresses may get the requirement that in case of a change of an address some logic from the domain of another
 * component should be invoked and that might even reject the change. In the latter case {@link EventBus} is the wrong
 * choice.
 *
 * @see EventBusAccess#get()
 */
public interface EventBus {

  /**
   * This method sends an event to all {@link #addListener(Class, EventListener) suitable registered listeners}.
   *
   * @param event is the event to send. Technically such event may be any Object such as a {@link String}. However, it
   *        is strongly recommended to create explicit value classes named with the suffix "Event". The easiest way to
   *        create your own event type is to use a Java {@link Record}. Please note that it may seem more easy to use
   *        data-objects directly as event, e.g. for the selection of a user, you may send the user object itself as
   *        event. However, later you may notice that you also need to send an event if a user is deleted or created and
   *        your design and semantic of events will be flawed. Therefore it is strongly to create a
   *        {@code UserSelectionEvent} {@link Class} or {@link Record} containing the selected user or its unique
   *        identifier in that example from the beginning to ensure a design for extension and flexibility.
   */
  void sendEvent(Object event);

  /**
   * This method registers a listener that is interested in events.
   *
   * @param <E> is the type of the events to listen to.
   * @param eventType is the {@link Class} reflecting the events to listen to. Typically this should be the exact type
   *        of some event sent via {@link #sendEvent(Object)}. However, polymorphic implementations of {@link EventBus}
   *        will also support event inheritance and allow you to register an {@link EventListener} for a
   *        {@link Class#getSuperclass() super-class} of an event type.
   * @param listener is the {@link EventListener} that shall be {@link EventListener#onEvent(Object) notified} if an
   *        event of the given {@link Class} is {@link #sendEvent(Object) send}.
   */
  <E> void addListener(Class<E> eventType, EventListener<E> listener);

  /**
   * This method removes a listener. If the listener was not {@link #addListener(Class, EventListener) registered}
   * before this method will have no effect.
   *
   * @param <E> is the type of the events to listen to.
   * @param eventType is the {@link Class} reflecting the events to listen to.
   * @param listener is the {@link EventListener} to remove.
   * @return {@code true} if the given {@code listener} has successfully been removed, {@code false} if the
   *         {@code listener} was NOT {@link #addListener(Class, EventListener) registered}.
   */
  <E> boolean removeListener(Class<E> eventType, EventListener<E> listener);

  /**
   * This method removes a listener. If the listener was not registered before this method will have no effect.
   *
   * @param listener is the {@link EventListener} to remove.
   * @return {@code true} if the given {@code listener} has successfully been removed, {@code false} if the
   *         {@code listener} was NOT {@link #addListener(Class, EventListener) registered}.
   */
  default boolean removeListener(EventListener<?> listener) {

    return removeListener(null, listener);
  }

}