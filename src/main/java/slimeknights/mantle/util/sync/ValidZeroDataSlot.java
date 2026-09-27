package slimeknights.mantle.util.sync;

import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;

import java.util.function.Consumer;

/**
 * Data slot over one index of a {@link ContainerData}; shim of the Mantle original. The
 * "valid zero" concern (forcing an initial sync when the real value is 0) is covered by
 * vanilla's full-state broadcast on menu open, so this is a plain index view.
 */
public class ValidZeroDataSlot extends DataSlot {
  private final ContainerData data;
  private final int index;

  public ValidZeroDataSlot(ContainerData data, int index) {
    this.data = data;
    this.index = index;
  }

  @Override
  public int get() {
    return data.get(index);
  }

  @Override
  public void set(int value) {
    data.set(index, value);
  }

  /** Registers a slot for every index of the container data */
  public static void trackIntArray(Consumer<DataSlot> consumer, ContainerData array) {
    for (int i = 0; i < array.getCount(); i++) {
      consumer.accept(new ValidZeroDataSlot(array, i));
    }
  }
}
