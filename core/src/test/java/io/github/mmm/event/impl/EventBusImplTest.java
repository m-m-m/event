package io.github.mmm.event.impl;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.mmm.base.exception.GlobalExceptionHandler;
import io.github.mmm.event.EventBus;
import io.github.mmm.event.EventListener;

/**
 * Test of {@link EventBusImpl}.
 */
public class EventBusImplTest extends EventBusTest {

  /**
   * @return the {@link EventBus} instance to test.
   */
  @Override
  protected EventBus getEventBus() {

    return new EventBusImpl();
  }

  /**
   * Tests the error handling of {@link EventBusImpl}.
   */
  @Test
  public void testErrorHandling() {

    // given

    final List<Throwable> errorList = new LinkedList<>();
    final String errorEvent = "error";
    final RuntimeException error = new IllegalStateException(errorEvent);
    GlobalExceptionHandler errorHandler = new GlobalExceptionHandler() {

      @Override
      public void handleError(Object context, Throwable e) {

        assertThat(e).isSameAs(error);
        assertSame(errorEvent, context);
        errorList.add(e);
      }
    };
    final EventBus eventBus = new EventBusImpl(errorHandler);

    EventListener<String> listener = new EventListener<>() {

      @Override
      public void onEvent(String event) {

        if (event == errorEvent) {
          throw error;
        }
      }
    };
    eventBus.addListener(String.class, listener);

    // when + then
    eventBus.sendEvent("foo");
    assertThat(errorList).isEmpty();
    eventBus.sendEvent(errorEvent);
    assertThat(errorList).hasSize(1);
    eventBus.sendEvent("bar");
    assertThat(errorList).hasSize(1);
    eventBus.sendEvent(errorEvent);
    assertThat(errorList).hasSize(2);
  }

}
