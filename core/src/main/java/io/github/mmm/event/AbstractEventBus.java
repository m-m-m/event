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

/**
 * This is the default implementation of {@link EventBus}.
 */
public abstract class AbstractEventBus implements EventBus {

  // private static final Logger LOG = LoggerFactory.getLogger(AbstractEventBus.class);

  @SuppressWarnings("rawtypes")
  private final Map<Class<?>, EventDispatcher> eventType2dispatcherMap;

  private final Queue<Object> eventQueue;

  /** The {@link GlobalExceptionHandler}. */
  protected final GlobalExceptionHandler errorHandler;

  /**
   * The constructor.
   */
  public AbstractEventBus() {

    this(null);
  }

  /**
   * The constructor.
   *
   * @param errorHandler the {@link GlobalExceptionHandler} instance.
   */
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

  /**
   * Called from {@link #sendEvent(Object)} to ensure {@link #dispatchEvents()} is triggered. This can be done
   * synchronous or asynchronous.
   */
  protected abstract void triggerDispatchEvents();

  /**
   * Dispatches all events in the event queue.
   */
  protected void dispatchEvents() {

    while (true) {
      Object event = this.eventQueue.poll();
      if (event == null) {
        return;
      }
      dispatchEvent(event);
    }
  }

  /**
   * Dispatches the given event.
   *
   * @param <E> is the generic type of {@code event}.
   * @param event is the event to dispatch.
   */
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

  /**
   * Called if an event was {@link #sendEvent(Object) send} but not dispatched to any
   * {@link #addListener(Class, EventListener) registered listener}.
   *
   * @param event is the un-dispatched event.
   */
  protected void handleUndispatchedEvent(Object event) {

    // LOG.warn("Event send with no responsible listener registered: {}", event);
  }

  /**
   * Gets or creates the {@link EventDispatcher} for the given {@code eventType}.
   *
   * @param <E> is the generic type of {@code eventType}.
   * @param eventType is the {@link Class} reflecting the event.
   * @return the {@link EventDispatcher} responsible for the given {@code eventType}.
   */
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
      dispatcher = this.eventType2dispatcherMap.computeIfAbsent(eventType, t -> new EventDispatcher<>(parent));
    }
    return (EventDispatcher<E>) dispatcher;
  }

  /**
   * Gets the most specific {@link EventDispatcher} responsible the given {@code eventType}.
   *
   * @param <E> is the generic type of {@code eventType}.
   * @param eventType is the {@link Class} reflecting the event.
   * @return the most specific {@link EventDispatcher} responsible for the given {@code eventType}. May be {@code null}
   *         if no {@link EventListener} is {@link #addListener(Class, EventListener) registered} for a compatible
   *         {@code eventType}.
   */
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

  /**
   * A dispatcher for all {@link EventListener}s of a particular {@link EventBus#addListener(Class, EventListener) event
   * type}.
   *
   * @param <E> type of the {@link EventListener#onEvent(Object) events}.
   */
  protected class EventDispatcher<E> extends AbstractEventSource<E, EventListener<E>> {

    /** @see #onEvent(Object, Collection) */
    private final EventDispatcher<? super E> parentDispatcher;

    /** @see #fireEvent(Object, Collection) */
    private final Collection<EventListener<E>> listeners;

    /**
     * The constructor.
     *
     * @param parent is the {@link EventDispatcher} responsible for the super-class or {@code null} if this is the root
     *        {@link EventDispatcher} responsible for {@link Object}.
     */
    public EventDispatcher(EventDispatcher<? super E> parent) {

      this(parent, new ConcurrentLinkedQueue<>());
    }

    /**
     * The constructor.
     *
     * @param parent is the {@link EventDispatcher} responsible for the super-class or {@code null} if this is the root
     *        {@link EventDispatcher} responsible for {@link Object}.
     * @param listeners the empty {@link Collection} implementation for the {@link EventListener}s.
     */
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