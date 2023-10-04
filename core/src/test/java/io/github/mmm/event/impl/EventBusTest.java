package io.github.mmm.event.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.mmm.event.EventBus;
import io.github.mmm.event.EventListener;

/**
 * This is the test-case for {@link EventBus} and its implementation.
 */
public abstract class EventBusTest extends Assertions {

  // private static final Logger LOG = LoggerFactory.getLogger(EventBusTest.class);

  /** The number of threads. */
  private static final int NUMBER_OF_THREADS = 10;

  /** The number of events an {@link EventThread} sends to the next thread. */
  private static final int EVENTS_PER_THREAD = 5;

  /**
   * @return the {@link EventBus} instance to test.
   */
  protected abstract EventBus getEventBus();

  /**
   * Tests the {@link #getEventBus() event bus} with a simple scenario.
   */
  @Test
  public void testSimple() {

    final EventBus eventBus = getEventBus();
    final List<Object> events = new LinkedList<>();
    final String eventLast = "terminate";
    EventListener<Object> listener = new EventListener<>() {

      @Override
      public void onEvent(Object event) {

        events.add(event);
        if (event == eventLast) {
          boolean removed = eventBus.removeListener(this);
          assertThat(removed).isTrue();
        }
      }
    };
    eventBus.addListener(Object.class, listener);
    Object event1 = new Object();
    assertThat(events).hasSize(0);
    eventBus.sendEvent(event1);
    assertThat(events).hasSize(1);
    String event2 = "foo";
    eventBus.sendEvent(event2);
    assertThat(events).hasSize(2);
    MyEvent event3 = new MyEvent(1, 1);
    eventBus.sendEvent(event3);
    assertThat(events).hasSize(3);
    eventBus.sendEvent(eventLast);
    assertThat(events).hasSize(4);
    eventBus.sendEvent("event after last");
    assertThat(events).hasSize(4);
    assertThat(events.get(0)).isSameAs(event1);
    assertThat(events.get(1)).isSameAs(event2);
    assertThat(events.get(2)).isSameAs(event3);
    assertThat(events.get(3)).isSameAs(eventLast);
    // listener auto-removed himself on last event
    boolean removed = eventBus.removeListener(listener);
    assertThat(removed).isFalse();
  }

  /**
   * Tests the event bus in a real concurrent usage scenario.
   *
   * @throws Exception if anything goes wrong.
   */
  @Test
  @Disabled("Only for local testing.")
  public void testConcurrent() throws Exception {

    // given
    final EventBus eventBus = getEventBus();

    EventThread[] threads = new EventThread[NUMBER_OF_THREADS];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new EventThread(eventBus, i);
    }

    // when
    for (int i = 0; i < threads.length; i++) {
      threads[i].start();
    }
    eventBus.sendEvent(new MyEvent(-1, threads[0].threadId));
    for (int i = 0; i < threads.length; i++) {
      threads[i].join();
    }

    // then

    //
    // + one for the thread itself (see EventThread.run()).
    int expectedEventProcessCount = 2;
    // total events: each thread receives (EVENTS_PER_THREAD + 1) initial events
    // it forwards its
    // 0 -> 0:0,0:1,...,0:10
    // 0 -> 0:1,0:2,...,0:10 times 10

    // initially 1 event send explicitly from call above, forwarded to all threads
    int expectedEventTotalCount = threads.length + 1; //
    for (int i = 0; i < threads.length; i++) {
      // each thread is sending an event for itself that is forwarded to all following threads
      // and it sends EVENTS_PER_THREAD events to its next thread forwarded to all following threads
      int eventCountForThread = (threads.length - i + 1) + (threads.length - i) * EVENTS_PER_THREAD;
      expectedEventTotalCount = expectedEventTotalCount + eventCountForThread;
    }
    // expectedEventTotalCount = 626; // hack
    for (int i = 0; i < threads.length; i++) {
      // all threads registered themselves equivalent on the even bus and must have received the same total
      // number of events.
      assertEquals(expectedEventTotalCount, threads[i].eventTotalCount);
      assertEquals(expectedEventProcessCount, threads[i].eventProcessCount);
      expectedEventProcessCount = expectedEventProcessCount + EVENTS_PER_THREAD + 1;
    }
  }

  /**
   * A {@link Thread} sending and receiving events for asynchronous tests.
   */
  protected static class EventThread extends Thread implements EventListener<MyEvent> {

    /** The {@link EventBus} to test. */
    private final EventBus eventBus;

    /** The sequential ID of this {@link EventThread}. */
    private final int threadId;

    /**
     * A counter for the number of events that have been processed (causing an event to the following
     * {@link EventThread}).
     */
    private int eventProcessCount;

    /**
     * The total number of events received.
     */
    private int eventTotalCount;

    /**
     * The constructor.
     *
     * @param eventBus is the {@link EventBus} instance.
     * @param id is the id (sequential number) of this thread.
     */
    public EventThread(EventBus eventBus, int id) {

      super("EventThread" + id);
      this.eventBus = eventBus;
      this.threadId = id;
      this.eventBus.addListener(MyEvent.class, this);
    }

    @Override
    public void onEvent(MyEvent event) {

      this.eventTotalCount++;
      // LOG.debug(this.threadId + ";" + event.sourceThreadId + ";" + event.targetThreadId + ";" + event.eventId);
      if (event.targetThreadId == this.threadId) {
        this.eventProcessCount++;
        this.eventBus.sendEvent(new MyEvent(event.sourceThreadId, this.threadId + 1, event.eventId));
      }
    }

    @Override
    public void run() {

      // send event to ourselves
      this.eventBus.sendEvent(new MyEvent(this.threadId, this.threadId));
      for (int i = 0; i < EVENTS_PER_THREAD; i++) {
        // send event to next thread
        this.eventBus.sendEvent(new MyEvent(this.threadId));
      }
      yield();
      // this.eventBus.removeListener(this);
    }
  }

  /**
   * A simple event for testing.
   */
  protected static class MyEvent {

    /** Counter for unique {@link #eventId}. */
    private static int idCounter = 0;

    /** The ID of this event (used for copies of the event for following {@link EventThread}s). */
    private int eventId;

    /**
     * The {@link EventThread#getId() id} of the thread who initially triggered the event (mainly for debugging and
     * tracing).
     */
    private int sourceThreadId;

    /** The {@link EventThread#getId() id} of the thread who should handle the event. */
    private int targetThreadId;

    /**
     * The constructor.
     *
     * @param sourceThreadId - see {@link #getSourceThreadId()}.
     */
    public MyEvent(int sourceThreadId) {

      this(sourceThreadId, sourceThreadId + 1);
    }

    /**
     * The constructor.
     *
     * @param sourceThreadId - see {@link #getSourceThreadId()}.
     * @param targetThreadId - see {@link #getTargetThreadId()}.
     */
    public MyEvent(int sourceThreadId, int targetThreadId) {

      this(sourceThreadId, targetThreadId, getNextId());
    }

    /**
     * @return the next unique ID.
     */
    private static synchronized int getNextId() {

      return idCounter++;
    }

    /**
     * The constructor.
     *
     * @param eventId - see {@link #getEventId()}
     * @param sourceThreadId - see {@link #getSourceThreadId()}.
     * @param targetThreadId - see {@link #getTargetThreadId()}.
     */
    public MyEvent(int sourceThreadId, int targetThreadId, int eventId) {

      super();
      this.eventId = eventId;
      this.sourceThreadId = sourceThreadId;
      this.targetThreadId = targetThreadId;
    }

    /**
     * @return the eventId
     */
    public int getEventId() {

      return this.eventId;
    }

    /**
     * @return the sourceThreadId
     */
    public int getSourceThreadId() {

      return this.sourceThreadId;
    }

    /**
     * @return the threadId
     */
    public int getTargetThreadId() {

      return this.targetThreadId;
    }

    @Override
    public String toString() {

      return "Event from " + this.sourceThreadId + " to " + this.targetThreadId + " with ID " + this.eventId;
    }
  }
}