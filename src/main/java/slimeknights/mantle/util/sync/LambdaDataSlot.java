package slimeknights.mantle.util.sync;

import net.minecraft.world.inventory.DataSlot;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/** Data slot syncing an int through lambdas; shim of the Mantle original */
public class LambdaDataSlot extends DataSlot {
  private final IntSupplier getter;
  private final IntConsumer setter;

  public LambdaDataSlot(IntSupplier getter, IntConsumer setter) {
    this.getter = getter;
    this.setter = setter;
  }

  /**
   * Compat with the Forge Mantle signature; the first parameter primed the initial sync,
   * which vanilla now covers via {@code sendAllDataToRemote} on menu open.
   */
  public LambdaDataSlot(int uncached, IntSupplier getter, IntConsumer setter) {
    this(getter, setter);
  }

  @Override
  public int get() {
    return getter.getAsInt();
  }

  @Override
  public void set(int value) {
    setter.accept(value);
  }
}
