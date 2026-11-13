package com.contact.management.common.event;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

/**
 * Publishes application events to listeners running in the same process.
 *
 * <p>This component is intentionally independent from external messaging
 * systems. Use a dedicated integration when events must cross process
 * boundaries.</p>
 */
@Component
public class EventPublisher {

    private final Map<Class<?>, List<Object>> listeners = new ConcurrentHashMap<>();

    public <T> void add(final Class<T> eventType, final Consumer<T> listener) {
        addListener(eventType, listener);
    }

    public void add(final Class<?> eventType, final Runnable handler) {
        addListener(eventType, handler);
    }

    private void addListener(final Class<?> eventType, final Object listener) {
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(listener, "listener must not be null");

        listeners.computeIfAbsent(eventType, key -> new CopyOnWriteArrayList<>())
                .add(listener);
    }

    public void publish(final Object event) {
        Objects.requireNonNull(event, "event must not be null");

        final List<Object> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null || eventListeners.isEmpty()) {
            return;
        }

        eventListeners.forEach(listener -> invoke(listener, event));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void invoke(final Object listener, final Object event) {
        if (listener instanceof Runnable) {
            ((Runnable) listener).run();
        } else if (listener instanceof Consumer) {
            ((Consumer) listener).accept(event);
        }
    }
}
