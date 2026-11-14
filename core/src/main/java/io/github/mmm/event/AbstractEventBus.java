/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.github.mmm.base.exception.GlobalExceptionHandler;

/// This is the default implementation of [EventBus].
public abstract class AbstractEventBus implements EventBus {

  // private static final Logger LOG = LoggerFactory.getLogger(AbstractEventBus.class);

  @SuppressWarnings("rawtypes")
  private final Map<Class<?>, EventDispatcher> eventType2dispatcherMap;

  private final Queue<Object> eventQueue;

  /// The [GlobalExceptionHandler].
  protected final GlobalExceptionHandler errorHandler;

  /// The constructor.
  public AbstractEventBus() {

    this(null);
  }

  /// The constructor.
  ///
  /// @param errorHandler the [GlobalExceptionHandler] instance.
  protected AbstractEventBus(GlobalExceptionHandler errorHandler) {

    super();
    this.eventType2dispatcherMap = new ConcurrentHashMap<>();
    this.eventQueue = new ConcurrentLinkedQueue<>();
    if (errorHandler == null) {
      this.errorHandler = io.github.mmm.base.exception.GlobalExceptionHandlerAccess.get();
    } else {
      this.errorHandler = errorHandler;
    }
  }

  @Override
  public void sendEvent(Object event) {

    Objects.requireNonNull(event);
    this.eventQueue.add(event);
    triggerDispatchEvents();
  }

  /// Called from [#sendEvent(Object)] to ensure [#dispatchEvents()] is triggered. This can be done
  /// synchronous or asynchronous.
  protected abstract void triggerDispatchEvents();

  /// Dispatches all events in the event queue.
  protected void dispatchEvents() {

    while (true) {
      Object event = this.eventQueue.poll();
      if (event == null) {
        return;
      }
      dispatchEvent(event);
    }
  }

  /// Dispatches the given event.
  ///
  /// @param <E> is the generic type of `event`.
  /// @param event is the event to dispatch.
  protected <E> void dispatchEvent(E event) {

    @SuppressWarnings("unchecked")
    Class<E> eventType = (Class<E>) event.getClass();
    EventDispatcher<E> eventDispatcher = getEventDispatcherOrNull(eventType);
    boolean dispatched = false;
    if (eventDispatcher != null) {
      dispatched = eventDispatcher.fireEvent(event);
    }
    if (!dispatched) {
      handleUndispatchedEvent(event);
    }
  }

  /// Called if an event was [send][#sendEvent(Object)] but not dispatched to any
  /// [registered listener][#addListener(Class, EventListener)].
  ///
  /// @param event is the un-dispatched event.
  protected void handleUndispatchedEvent(Object event) {

    // LOG.warn("Event send with no responsible listener registered: {}", event);
  }

  /// Gets or creates the [EventDispatcher] for the given `eventType`.
  ///
  /// @param <E> is the generic type of `eventType`.
  /// @param eventType is the [Class] reflecting the event.
  /// @return the [EventDispatcher] responsible for the given `eventType`.
  @SuppressWarnings("unchecked")
  protected <E> EventDispatcher<E> getEventDispatcherRequired(Class<E> eventType) {

    EventDispatcher<?> dispatcher = this.eventType2dispatcherMap.get(eventType);
    if (dispatcher == null) {
      Class<?> type = eventType.getSuperclass();
      EventDispatcher<?> parent;
      if (type != null) {
        parent = getEventDispatcherRequired(type);
      } else {
        parent = null;
      }
      dispatcher = this.eventType2dispatcherMap.computeIfAbsent(eventType, _ -> new EventDispatcher<>(parent));
    }
    return (EventDispatcher<E>) dispatcher;
  }

  /// Gets the most specific [EventDispatcher] responsible the given `eventType`.
  ///
  /// @param <E> is the generic type of `eventType`.
  /// @param eventType is the [Class] reflecting the event.
  /// @return the most specific [EventDispatcher] responsible for the given `eventType`. May be `null`
  ///         if no [EventListener] is [registered][#addListener(Class, EventListener)] for a compatible
  ///         `eventType`.
  @SuppressWarnings("unchecked")
  protected <E> EventDispatcher<E> getEventDispatcherOrNull(Class<E> eventType) {

    Class<?> type = eventType;
    EventDispatcher<?> dispatcher = this.eventType2dispatcherMap.get(eventType);
    while ((dispatcher == null) && (type != null)) {
      type = type.getSuperclass();
      dispatcher = this.eventType2dispatcherMap.get(type);
    }
    return (EventDispatcher<E>) dispatcher;
  }

  @Override
  public <E> void addListener(Class<E> eventType, EventListener<E> listener) {

    Objects.requireNonNull(eventType);
    Objects.requireNonNull(listener);
    if (eventType.isInterface()) {
      throw new UnsupportedOperationException(
          "This EventBus implementation does not support interfaces as event type: " + eventType.getName());
    }
    EventDispatcher<E> eventDispatcher = getEventDispatcherRequired(eventType);
    eventDispatcher.addListener(listener);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <E> boolean removeListener(Class<E> eventType, EventListener<E> listener) {

    boolean removed = false;
    if (eventType == null) {
      for (EventDispatcher<?> dispatcher : this.eventType2dispatcherMap.values()) {
        boolean currentRemoved = dispatcher.removeListener((EventListener) listener);
        if (currentRemoved) {
          removed = true;
        }
      }
    } else {
      EventDispatcher<E> dispatcher = this.eventType2dispatcherMap.get(eventType);
      if (dispatcher != null) {
        return dispatcher.removeListener(listener);
      }
    }
    return removed;
  }

  /// A dispatcher for all [EventListener]s of a particular [event
 type][EventBus#addListener(Class, EventListener)].
  ///
  /// @param <E> type of the [events][EventListener#onEvent(Object)].
  protected class EventDispatcher<E> extends AbstractEventSource<E, EventListener<E>> {

    /// @see #onEvent(Object, Collection)
    private final EventDispatcher<? super E> parentDispatcher;

    /// @see #fireEvent(Object, Collection)
    private final Collection<EventListener<E>> listeners;

    /// The constructor.
    ///
    /// @param parent is the [EventDispatcher] responsible for the super-class or `null` if this is the root
    ///        [EventDispatcher] responsible for [Object].
    public EventDispatcher(EventDispatcher<? super E> parent) {

      this(parent, new ConcurrentLinkedQueue<>());
    }

    /// The constructor.
    ///
    /// @param parent is the [EventDispatcher] responsible for the super-class or `null` if this is the root
    ///        [EventDispatcher] responsible for [Object].
    /// @param listeners the empty [Collection] implementation for the [EventListener]s.
    protected EventDispatcher(EventDispatcher<? super E> parent, Collection<EventListener<E>> listeners) {

      super();
      this.parentDispatcher = parent;
      this.listeners = listeners;
    }

    @Override
    protected void doAddListener(EventListener<E> listener) {

      this.listeners.add(listener);
    }

    @Override
    public boolean removeListener(EventListener<E> listener) {

      return this.listeners.remove(listener);
    }

    @Override
    protected boolean fireEvent(E event) {

      boolean dispatched = false;
      for (EventListener<E> listener : this.listeners) {
        try {
          listener.onEvent(event);
          dispatched = true;
        } catch (Throwable exception) {
          AbstractEventBus.this.errorHandler.handleError(event, exception);
        }
      }
      if (this.parentDispatcher != null) {
        boolean superDispatched = this.parentDispatcher.fireEvent(event);
        if (superDispatched) {
          dispatched = true;
        }
      }
      return dispatched;
    }

  }

}