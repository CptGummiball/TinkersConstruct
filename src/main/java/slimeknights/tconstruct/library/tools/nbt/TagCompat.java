package slimeknights.tconstruct.library.tools.nbt;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import slimeknights.mantle.client.SafeClientAccess;

import javax.annotation.Nullable;

/**
 * The load-bearing ToolStack decision: all tool data (materials, modifiers, stats, mod data)
 * lives as one CompoundTag inside {@code minecraft:custom_data}, exactly as it lived in the
 * stack tag on 1.20.
 *
 * <p>Why not typed components per field: ToolStack and the modifier NBT layer are ~2000
 * lines of logic written against one mutable tree, with third-party-facing NBT paths
 * (tic_materials, tic_modifiers, ...) that the pack's data files also reference. One
 * component keeps every one of those paths and code lines valid. Vanilla interop that needs
 * real components (damage bar, enchantment glint) goes through the item overrides, which
 * read ToolStack — so nothing observable is lost.
 *
 * <p>These helpers are the only place that knows where the tag lives.
 */
public final class TagCompat {

  private TagCompat() {}

  /** Reads the legacy stack tag, or null when absent — the 1.20 {@code getTag()} contract. */
  @Nullable
  public static CompoundTag getTag(ItemStack stack) {
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    return data == null ? null : data.copyTag();
  }

  /** True when the stack carries any legacy tag data. */
  public static boolean hasTag(ItemStack stack) {
    return stack.has(DataComponents.CUSTOM_DATA);
  }

  /**
   * Reads the legacy tag, creating and attaching an empty one when absent.
   *
   * <p>Mutation contract, matching 1.20's live tag: the returned instance is anchored as a
   * <i>fresh</i> component on the stack, so mutating it mutates the stack. Because the
   * component instance is new, snapshots vanilla took earlier (change detection, copies)
   * still hold the previous instance and correctly observe a change.
   */
  public static CompoundTag getOrCreateTag(ItemStack stack) {
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    CompoundTag tag = data == null ? new CompoundTag() : data.copyTag();
    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    return tag;
  }

  /**
   * Registry access for item stacks stored <i>inside</i> tool NBT (the tool inventory modules).
   *
   * <p>1.21 routes {@link ItemStack#save(net.minecraft.core.HolderLookup.Provider, net.minecraft.nbt.Tag)}
   * and {@link ItemStack#parseOptional} through a {@link HolderLookup.Provider} so components that
   * point at datapack registries (enchantments above all) resolve. The modifier hooks that read
   * those stacks take no world context — the 1.20 API needed none — and the hook signatures are
   * public API used by modules that port later, so the provider is resolved from the running game
   * instead: the server's registries when one is running, else the connected client's synced ones.
   */
  public static HolderLookup.Provider registries() {
    if (FabricLoader.getInstance().getGameInstance() instanceof MinecraftServer server) {
      return server.registryAccess();
    }
    RegistryAccess client = SafeClientAccess.getRegistryAccess();
    return client != null ? client : RegistryAccess.EMPTY;
  }

  /** Writes the tag back; null clears it. CustomData is immutable, so mutations must end here. */
  public static void setTag(ItemStack stack, @Nullable CompoundTag tag) {
    if (tag == null || tag.isEmpty()) {
      stack.remove(DataComponents.CUSTOM_DATA);
    } else {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
  }
}
