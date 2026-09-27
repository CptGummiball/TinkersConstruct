package slimeknights.mantle.transfer.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

/** Shim of Forge's INBTSerializable with the 1.21 registry-aware signatures */
public interface INBTSerializable<T extends Tag> {
  T serializeNBT(HolderLookup.Provider registries);

  void deserializeNBT(HolderLookup.Provider registries, T nbt);
}
