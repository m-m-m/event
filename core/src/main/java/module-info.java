/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Provides generic, reusable infrastructure to define, send and receive events. <br>
 * <a name="documentation"></a>
 * <h2>Event</h2><br>
 * This module provides a minimalistic but powerful API for the event pattern. You may define any kind of custom event
 * like this:
 *
 * <pre>
 * // you may also use a record instead of a class...
 * public class MyEvent {
 * }
 * </pre>
 *
 * To get started, here is a simple example how you can define your custom event handler interface:
 *
 * <pre>
 * &#64;FunctionalInterface
 * public interface MyEventListener extends {@link io.github.mmm.event.EventListener}{@literal <}MyEvent{@literal >} {
 * }
 * </pre>
 *
 * Your component sending events can be defined like this:
 *
 * <pre>
 * public interface MyComponent extends {@link io.github.mmm.event.EventSource}{@literal <}MyEvent, MyEventListener{@literal >} {
 *   void doSomething();
 * }
 * </pre>
 *
 * The implementation simply extends from {@link io.github.mmm.event.AbstractEventSource} inheriting the generic event
 * infrastructure (in case you already have to extend another class, use
 * {@link io.github.mmm.event.EventSourceAdapter}):
 *
 * <pre>
 * public class MyComponentImpl extends {@link io.github.mmm.event.AbstractEventSource}{@literal <}MyEvent, MyEventListener{@literal >} implements MyComponent {
 *
 *   public void doSomething() {
 *     fireEvent(new MyEvent("Hello World!");
 *   }
 * }
 * </pre>
 *
 * Now you already have everything you need for eventing:
 *
 * <pre>
 * MyComponent component = new MyComponentImpl();
 * MyEventListener listener = (e) -{@literal >} System.out.println("Received event: " + e);
 * component.{@link io.github.mmm.event.EventSource#addListener(EventListener) addListener}(listener);
 * component.doSomething(); // this will echo "Received event: MyEvent@..."
 * // when you are done, you can unsubscribe the listener
 * component.{@link io.github.mmm.event.EventSource#removeListener(EventListener) removeListener}(listener);
 * </pre>
 *
 * @provides io.github.mmm.event.EventBus
 * @uses io.github.mmm.event.EventBus
 */
module io.github.mmm.event {

  requires io.github.mmm.base;

  // requires org.slf4j;

  uses io.github.mmm.event.EventBus;

  provides io.github.mmm.event.EventBus //
      with io.github.mmm.event.impl.EventBusImpl;

  exports io.github.mmm.event;
}
