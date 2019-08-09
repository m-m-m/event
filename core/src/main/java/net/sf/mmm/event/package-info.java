/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
/**
 * Provides the API and base implementation for generic eventing. <a name="documentation"></a>
 * <h2>Event</h2><br>
 * This is a minimalistic but powerful API for the event pattern. To get started, here is a simple example how you can
 * define your custom event handler interface:
 *
 * <pre>
 * &#64;FunctionalInterface
 * public interface MyEventListener extends {@link net.sf.mmm.event.EventListener}<MyEvent> {
 * }
 * </pre>
 *
 * The event itself ({@code MyEvent}) can be any object so you can use an interface, class, or enum without any
 * restrictions.
 *
 * Your component sending events can be defined like this:
 *
 * <pre>
 * public interface MyComponent extends {@link net.sf.mmm.event.EventSource}<MyEvent, MyEventListener> {
 *   void doSomething();
 * }
 * </pre>
 *
 * The implementation simply extends from {@link net.sf.mmm.event.AbstractEventSource} inheriting the generic event
 * infrastructure (in case you already have to extend another class, use {@link net.sf.mmm.event.EventSourceAdapter}):
 *
 * <pre>
 * public class MyComponentImpl extends {@link net.sf.mmm.event.AbstractEventSource}<MyEvent, MyEventListener> implements MyComponent {
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
 * MyEventListener listener = (e) -> System.out.println("Received event: " + e);
 * component.{@link net.sf.mmm.event.EventSource#addListener(EventListener) addListener}(listener);
 * component.doSomething();
 * // when you are done, you can unsubscribe the listener
 * component.{@link net.sf.mmm.event.EventSource#removeListener(EventListener) removeListener}(listener);
 * </pre>
 *
 * @see net.sf.mmm.event.EventListener
 * @see net.sf.mmm.event.EventSource
 * @see net.sf.mmm.event.AbstractEventSource
 */
package net.sf.mmm.event;