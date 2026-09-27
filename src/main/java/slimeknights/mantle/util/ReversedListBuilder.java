package slimeknights.mantle.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * List builder that emits its contents in reverse insertion order.
 *
 * <p>Exists for models that must be assembled front-to-back but drawn back-to-front. The tool
 * model is the case: it walks the layers top down so each layer knows what the layers above it
 * already cover ({@link ItemLayerPixels}), then hands the quads over bottom up, so the topmost
 * layer is drawn last and wins where two layers overlap.
 */
public class ReversedListBuilder<T> {
  private final List<T> list = new ArrayList<>();

  /** Adds an element, which will be emitted before everything added before it */
  public void add(T element) {
    list.add(element);
  }

  /** Passes every element to the consumer in reverse insertion order */
  public void build(Consumer<T> consumer) {
    for (int i = list.size() - 1; i >= 0; i--) {
      consumer.accept(list.get(i));
    }
  }

  /** Returns the elements in reverse insertion order */
  public List<T> build() {
    List<T> copy = new ArrayList<>(list);
    Collections.reverse(copy);
    return copy;
  }
}
