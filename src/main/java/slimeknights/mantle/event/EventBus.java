package slimeknights.mantle.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Minimal event bus with Forge's registration and posting semantics: listeners keyed by
 * exact event class, ordered by {@link EventPriority}, receiving-cancelled opt-in.
 *
 * <p>Deliberately simpler than Forge's: no inheritance-based dispatch (Tinkers listens on
 * concrete classes), no annotation scanning of {@code @SubscribeEvent} objects — the
 * `register(Object)` path used by Forge mod classes is replaced by explicit listeners
 * during the port.
 */
public class EventBus {

  private final Map<Class<?>, List<Listener<?>>> listeners = new ConcurrentHashMap<>();

  /** Registers a listener at {@link EventPriority#NORMAL} that skips cancelled events. */
  public <T extends Event> void addListener(Class<T> eventType, Consumer<T> listener) {
    addListener(EventPriority.NORMAL, false, eventType, listener);
  }

  /** Registers a listener with explicit priority and cancelled-event handling. */
  public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Class<T> eventType, Consumer<T> listener) {
    List<Listener<?>> list = listeners.computeIfAbsent(eventType, key -> new CopyOnWriteArrayList<>());
    list.add(new Listener<>(priority, receiveCanceled, listener));
    list.sort(null);
  }

  /**
   * Posts the event to listeners registered for its exact class.
   *
   * @return true when the event was cancelled
   */
  public boolean post(Event event) {
    List<Listener<?>> list = listeners.get(event.getClass());
    if (list != null) {
      for (Listener<?> listener : list) {
        listener.accept(event);
      }
    }
    return event.isCanceled();
  }

  private record Listener<T extends Event>(EventPriority priority, boolean receiveCanceled, Consumer<T> consumer)
    implements Comparable<Listener<?>> {

    @SuppressWarnings("unchecked")
    void accept(Event event) {
      if (receiveCanceled || !event.isCanceled()) {
        consumer.accept((T) event);
      }
    }

    @Override
    public int compareTo(Listener<?> other) {
      return priority.compareTo(other.priority);
    }
  }
}
