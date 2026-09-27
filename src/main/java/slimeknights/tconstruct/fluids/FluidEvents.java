package slimeknights.tconstruct.fluids;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.minecraft.world.item.Items;

/**
 * Fluid-related hooks outside registration.
 *
 * <p>Fabric port: the furnace-fuel event becomes a {@code FuelRegistry} entry, and the
 * powdered-snow-bucket capability attachment becomes a {@code FluidStorage.ITEM}
 * registration on the vanilla item.
 */
public class FluidEvents {
  private FluidEvents() {}

  public static void init() {
    // 150% efficiency compared to lava bucket, compare to casting blaze rods, which cast into 120%
    FuelRegistry.INSTANCE.add(TinkerFluids.blazingBlood.asItem(), 30000);

    // let the melter drain vanilla powder snow buckets into our powdered snow fluid
    FluidStorage.ITEM.registerForItems(
      (stack, context) -> new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(TinkerFluids.powderedSnow.get()), FluidConstants.BUCKET),
      Items.POWDER_SNOW_BUCKET);
  }
}
