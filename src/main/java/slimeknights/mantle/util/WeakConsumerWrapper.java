package slimeknights.mantle.util;

import slimeknights.mantle.transfer.cap.NonNullConsumer;

import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;

/**
 * Listener wrapper holding its owner weakly, so capability listeners do not keep unloaded
 * block entities alive. Mirrors the Forge Mantle utility, on the shimmed consumer type.
 */
public class WeakConsumerWrapper<TE, T> implements NonNullConsumer<T> {
  private final WeakReference<TE> te;
  private final BiConsumer<TE, T> consumer;

  public WeakConsumerWrapper(TE te, BiConsumer<TE, T> consumer) {
    this(new WeakReference<>(te), consumer);
  }

  public WeakConsumerWrapper(WeakReference<TE> te, BiConsumer<TE, T> consumer) {
    this.te = te;
    this.consumer = consumer;
  }

  @Override
  public void accept(T t) {
    TE owner = te.get();
    if (owner != null) {
      consumer.accept(owner, t);
    }
  }
}
