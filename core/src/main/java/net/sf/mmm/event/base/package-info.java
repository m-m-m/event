/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
/**
 * Provides the base implementation for generic eventing. <a name="documentation"></a>
 * <h2>Event Base</h2><br>
 * This package provides the base implementation of the {@link net.sf.mmm.event.base Event API}. For a simple event
 * source you only need to extend {@link net.sf.mmm.event.base.AbstractEventSource}:
 *
 * <pre>
 * public class MyComponentImpl extends {@link net.sf.mmm.event.base.AbstractEventSource}<MyEvent, MyEventListener> {
 * }
 * </pre>
 *
 * The event itself ({@code MyEvent}) can be any object so you can use an interface, class, or enum without any
 * restrictions.
 *
 * Your component sending events can be defined like this:
 *
 * <pre>
 * public interface MyComponent extends EventSource<MyEvent, MyEventListener> {
 *   // my custom methods ...
 * }
 * </pre>
 *
 * To provide an implementation see {@link net.sf.mmm.event.base}.
 *
 * @see net.sf.mmm.event.base.AbstractEventSource
 * @see net.sf.mmm.event.base.EventSourceAdapter
 * @see net.sf.mmm.event.api
 */
package net.sf.mmm.event.base;