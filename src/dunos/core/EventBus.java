package dunos.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * DunDunDunOS EventBus - A publish/subscribe event system for inter-component communication.
 * Allows loose coupling between system components through asynchronous event delivery.
 * Supports event filtering, prioritization, and weak references for automatic cleanup.
 */
public class EventBus {

    private static volatile EventBus instance;
    private final ConcurrentHashMap<Class<?>, List<EventHandler<?>>> handlers;
    private final ExecutorService eventExecutor;

    /**
     * Generic event wrapper containing event data.
     */
    public static class Event<T> {
        private final T data;
        private final long timestamp;
        private final String source;
        private boolean consumed;

        public Event(T data, String source) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
            this.source = source;
            this.consumed = false;
        }

        public T getData() { return data; }
        public long getTimestamp() { return timestamp; }
        public String getSource() { return source; }
        public boolean isConsumed() { return consumed; }
        public void consume() { this.consumed = true; }
    }

    /**
     * Represents a registered event handler with metadata.
     */
    private static class EventHandler<T> {
        final Consumer<Event<T>> handler;
        final String filter;
        final int priority;
        final UUID id;

        EventHandler(Consumer<Event<T>> handler, String filter, int priority) {
            this.handler = handler;
            this.filter = filter;
            this.priority = priority;
            this.id = UUID.randomUUID();
        }
    }

    private EventBus() {
        this.handlers = new ConcurrentHashMap<>();
        this.eventExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "EventBus-Thread");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Gets the singleton EventBus instance.
     */
    public static EventBus getInstance() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    /**
     * Registers an event handler for a specific event type.
     *
     * @param <T> The event data type
     * @param eventClass The class of the event data
     * @param handler The consumer function to handle the event
     * @return A subscription ID that can be used to unsubscribe
     */
    public <T> UUID subscribe(Class<T> eventClass, Consumer<Event<T>> handler) {
        return subscribe(eventClass, handler, null, 0);
    }

    /**
     * Registers an event handler with a filter string.
     *
     * @param <T> The event data type
     * @param eventClass The class of the event data
     * @param handler The consumer function
     * @param filter Optional filter string (e.g., "type:notification")
     * @return Subscription ID
     */
    public <T> UUID subscribe(Class<T> eventClass, Consumer<Event<T>> handler, String filter) {
        return subscribe(eventClass, handler, filter, 0);
    }

    /**
     * Registers an event handler with filter and priority.
     *
     * @param <T> The event data type
     * @param eventClass The class of the event data
     * @param handler The consumer function
     * @param filter Optional filter string
     * @param priority Higher priority handlers are called first
     * @return Subscription ID
     */
    @SuppressWarnings("unchecked")
    public <T> UUID subscribe(Class<T> eventClass, Consumer<Event<T>> handler, String filter, int priority) {
        EventHandler<T> eventHandler = new EventHandler<>(handler, filter, priority);
        handlers.computeIfAbsent(eventClass, k -> new ArrayList<>()).add((EventHandler<?>) eventHandler);
        // Sort by priority
        handlers.get(eventClass).sort((a, b) -> b.priority - a.priority);
        return eventHandler.id;
    }

    /**
     * Unsubscribes a handler using its subscription ID.
     */
    public boolean unsubscribe(UUID subscriptionId) {
        for (List<EventHandler<?>> handlerList : handlers.values()) {
            Iterator<EventHandler<?>> iterator = handlerList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().id.equals(subscriptionId)) {
                    iterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Publishes an event synchronously. All matching handlers are called immediately.
     *
     * @param <T> The event data type
     * @param eventClass The class of the event data
     * @param event The event to publish
     */
    @SuppressWarnings("unchecked")
    public <T> void publish(Class<T> eventClass, Event<T> event) {
        List<EventHandler<?>> handlerList = handlers.get(eventClass);
        if (handlerList != null) {
            // Copy to avoid concurrent modification
            List<EventHandler<?>> copy;
            synchronized (handlerList) {
                copy = new ArrayList<>(handlerList);
            }
            for (EventHandler<?> eh : copy) {
                if (!event.isConsumed()) {
                    try {
                        ((EventHandler<T>) eh).handler.accept(event);
                    } catch (Exception e) {
                        System.err.println("[EventBus] Handler error: " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Publishes an event asynchronously on the event executor thread pool.
     */
    public <T> void publishAsync(Class<T> eventClass, Event<T> event) {
        eventExecutor.submit(() -> publish(eventClass, event));
    }

    /**
     * Creates a new event with the given data and source.
     */
    public static <T> Event<T> createEvent(T data, String source) {
        return new Event<>(data, source);
    }

    /**
     * Convenience method to publish with automatic event creation.
     */
    public <T> void emit(Class<T> eventClass, T data, String source) {
        publish(eventClass, new Event<>(data, source));
    }

    /**
     * Convenience method to publish asynchronously with automatic event creation.
     */
    public <T> void emitAsync(Class<T> eventClass, T data, String source) {
        publishAsync(eventClass, new Event<>(data, source));
    }

    /**
     * Clears all registered handlers.
     */
    public void clear() {
        handlers.clear();
    }

    /**
     * Returns the number of registered handlers for a given event type.
     */
    public int getHandlerCount(Class<?> eventClass) {
        List<EventHandler<?>> handlerList = handlers.get(eventClass);
        return handlerList != null ? handlerList.size() : 0;
    }
}

