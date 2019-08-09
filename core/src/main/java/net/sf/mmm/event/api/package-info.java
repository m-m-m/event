/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
/**
 * Provides the API for generic eventing. <a name="documentation"></a>
 * <h2>Event API</h2><br>
 * This is a minimalistic but powerful API for the event pattern. To get started, here is a simple example how you can
 * define your custom event handler interface:
 *
 * <pre>
 * &#64;FunctionalInterface
 * public interface MyEventListener extends {@link net.sf.mmm.event.api.EventListener}<MyEvent> {
 * }
 * </pre>
 *
 * The event itself ({@code MyEvent}) can be any object so you can use an interface, class, or enum without any
 * restrictions.
 *
 * Your component sending events can be defined like this:
 *
 * <pre>
 * public interface MyComponent extends {@link net.sf.mmm.event.api.EventSource}<MyEvent, MyEventListener> {
 *   // my custom methods ...
 * }
 * </pre>
 *
 * Now you already have your API to listen to events:
 *
 * <pre>
 * MyComponent component = getComponent();
 * MyEventListener listener = (e) -> System.out.println("Received event: " + e);
 * component.{@link net.sf.mmm.event.api.EventSource#addListener(EventListener) addListener}(listener);
 * // ... trigger some events ...
 * // when you are done, you can unsubscribe the listener
 * component.{@link net.sf.mmm.event.api.EventSource#removeListener(EventListener) removeListener}(listener);
 * </pre>
 *
 * To provide an implementation see {@link net.sf.mmm.event.base}.
 *
 * @see net.sf.mmm.event.api.EventListener
 * @see net.sf.mmm.event.api.EventSource
 * @see net.sf.mmm.event.base
 */
package net.sf.mmm.event.api;