package slimeknights.mantle.data;

import net.minecraft.core.HolderLookup;

import javax.annotation.Nullable;

/**
 * Registry access for the datapack load currently in progress.
 *
 * <p>1.21 moved enchantments (and other content) into datapack registries, which are built
 * per-world rather than existing statically. Loadables that resolve such entries therefore
 * need a {@link HolderLookup.Provider} handed to them at parse time — see
 * {@link slimeknights.mantle.data.loadable.field.ContextKey#REGISTRY_ACCESS}. Forge passed
 * one down through its reload-listener patch; here a mixin on {@code ReloadableServerResources}
 * publishes it and the reload listeners read it when building their parse contexts.
 */
public final class DatapackRegistries {
  private static volatile HolderLookup.Provider current = null;

  private DatapackRegistries() {}

  /** Called by the ReloadableServerResources mixin when a datapack load begins */
  public static void setup(@Nullable HolderLookup.Provider registries) {
    current = registries;
  }

  /** Registry access for the load in progress, null outside a datapack load */
  @Nullable
  public static HolderLookup.Provider current() {
    return current;
  }
}
