package slimeknights.tconstruct.fabric;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.tconstruct.fluids.fluids.PotionFluidType;

import java.util.function.Supplier;

/**
 * Registrations exposing Tinkers' fluid-container items through Fabric's item fluid storage;
 * replaces the Forge {@code initCapabilities}/{@code AttachCapabilitiesEvent} wrappers.
 * The mantle transfer bridge ({@code TransferUtil.getFluidHandlerItem}) resolves against the
 * same lookup, so recipe code sees these containers exactly as it did on Forge.
 */
public class TinkerFluidStorage {
  private TinkerFluidStorage() {}

  /**
   * Registers a container holding a constant fluid that empties into the given item;
   * matches the semantics of {@code ConstantFluidContainerWrapper} (drain all or nothing).
   */
  public static void registerConstant(Supplier<FluidStack> fluid, Item emptyItem, ItemLike... items) {
    Item[] resolved = new Item[items.length];
    for (int i = 0; i < items.length; i++) {
      resolved[i] = items[i].asItem();
    }
    FluidStorage.ITEM.registerForItems((stack, context) -> {
      FluidStack contained = fluid.get();
      return new FullItemFluidStorage(context, emptyItem, contained.getVariant(), contained.getDroplets());
    }, resolved);
  }

  /**
   * Exposes every Tinkers block entity's fluid and item handlers through Fabric's storage
   * lookups. On Forge the capability system did this implicitly; on Fabric nothing had
   * registered the block side, so pipes and pumps from other mods (and Jade's tank display)
   * saw no storage at all on tanks, melters, smelteries or casting blocks.
   *
   * <p>Fallbacks fire only when no block-specific handler matched, and only mantle-derived
   * block entities answer, so other mods' blocks are never touched.
   */
  public static void registerBlockBridges() {
    FluidStorage.SIDED.registerFallback((level, pos, state, blockEntity, direction) -> {
      if (blockEntity instanceof slimeknights.mantle.transfer.cap.ICapabilityProvider provider) {
        return provider.getCapability(slimeknights.mantle.transfer.cap.ForgeCapabilities.FLUID_HANDLER, direction)
                       .map(handler -> (net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant>) new slimeknights.mantle.transfer.fluid.FluidStorageBridge(handler))
                       .orElse(null);
      }
      return null;
    });
    net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.registerFallback((level, pos, state, blockEntity, direction) -> {
      if (blockEntity instanceof slimeknights.mantle.transfer.cap.ICapabilityProvider provider) {
        return provider.getCapability(slimeknights.mantle.transfer.cap.ForgeCapabilities.ITEM_HANDLER, direction)
                       .map(handler -> (net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant>) new slimeknights.mantle.transfer.item.ItemStorageBridge(handler))
                       .orElse(null);
      }
      return null;
    });
  }

  /** Registers the potion bucket, whose fluid depends on the stack's potion component */
  public static void registerPotionBucket(ItemLike bucket) {
    FluidStorage.ITEM.registerForItems((stack, context) -> {
      PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      FluidStack fluid = PotionFluidType.potionFluid(contents, FluidStack.BUCKET_VOLUME);
      return new FullItemFluidStorage(context, Items.BUCKET, fluid.getVariant(), fluid.getDroplets());
    }, bucket.asItem());
  }
}
