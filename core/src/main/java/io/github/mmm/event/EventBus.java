/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

/// This is the interface for an event bus. An event bus is a central place for [sending][#sendEvent(Object)],
/// [listening][#addListener(Class, EventListener)] to and [receiving][EventListener#onEvent(Object)] events.
/// The [EventBus] allows to communicate between loosely coupled components in a smart and efficient way:
///
/// - A component sending events only needs to know the [EventBus] but not the receivers of the events.
/// - A component receiving events only needs to know the [EventBus] but not the sender of the events.
///
/// This way components can communicate via events without compile time dependency between them. All they need to see is
/// the [EventBus] and the event itself.
/// **ATTENTION:**
/// This interface is designed as a stable API and standard for an event bus. However, there can be various
/// implementations of this interface with different aspects regarding concurrency, polymorphism, performance, etc. While
/// this interface will remain stable, we might change internals of the [EventBus] implementation. Further, you may
/// want to choose a different implementation of [EventBus] when you are inside a front-end application (UI) or a
/// back-end application (server). Therefore, it is possible to provide your own implementation of [EventBus] as a
/// Java module via [java.util.ServiceLoader].
/// **NOTE:**
/// The loose coupling makes flows less easy to see, understand and debug. You should only consider this approach for
/// components that should be decoupled by design. Do not get confused by the beauty of the event-bus pattern and avoid
/// using it where straight method calls should be preferred.
/// E.g. if you have a user-interface with a navigation sub-dialog and various other dialogs they should communicate via
/// [EventBus] to update their views accordingly. However, a business component responsible to read and write
/// addresses may get the requirement that in case of a change of an address some logic from the domain of another
/// component should be invoked and that might even reject the change. In the latter case [EventBus] is the wrong
/// choice.
///
/// @see EventBusAccess#get()
public interface EventBus {

  /// This method sends an event to all [suitable registered listeners][#addListener(Class, EventListener)].
  ///
  /// @param event is the event to send. Technically such event may be any Object such as a [String]. However, it
  ///        is strongly recommended to create explicit value classes named with the suffix "Event". The easiest way to
  ///        create your own event type is to use a Java [Record]. Please note that it may seem more easy to use
  ///        data-objects directly as event, e.g. for the selection of a user, you may send the user object itself as
  ///        event. However, later you may notice that you also need to send an event if a user is deleted or created and
  ///        your design and semantic of events will be flawed. Therefore it is strongly to create a
  ///        `UserSelectionEvent` [Class] or [Record] containing the selected user or its unique
  ///        identifier in that example from the beginning to ensure a design for extension and flexibility.
  void sendEvent(Object event);

  /// This method registers a listener that is interested in events.
  ///
  /// @param <E> is the type of the events to listen to.
  /// @param eventType is the [Class] reflecting the events to listen to. Typically this should be the exact type
  ///        of some event sent via [#sendEvent(Object)]. However, polymorphic implementations of [EventBus]
  ///        will also support event inheritance and allow you to register an [EventListener] for a
  ///        [super-class][Class#getSuperclass()] of an event type.
  /// @param listener is the [EventListener] that shall be [notified][EventListener#onEvent(Object)] if an
  ///        event of the given [Class] is [send][#sendEvent(Object)].
  <E> void addListener(Class<E> eventType, EventListener<E> listener);

  /// This method removes a listener. If the listener was not [registered][#addListener(Class, EventListener)]
  /// before this method will have no effect.
  ///
  /// @param <E> is the type of the events to listen to.
  /// @param eventType is the [Class] reflecting the events to listen to.
  /// @param listener is the [EventListener] to remove.
  /// @return `true` if the given `listener` has successfully been removed, `false` if the
  ///         `listener` was NOT [registered][#addListener(Class, EventListener)].
  <E> boolean removeListener(Class<E> eventType, EventListener<E> listener);

  /// This method removes a listener. If the listener was not registered before this method will have no effect.
  ///
  /// @param listener is the [EventListener] to remove.
  /// @return `true` if the given `listener` has successfully been removed, `false` if the
  ///         `listener` was NOT [registered][#addListener(Class, EventListener)].
  default boolean removeListener(EventListener<?> listener) {

    return removeListener(null, listener);
  }

}