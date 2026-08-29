package slimeknights.tconstruct.fluids;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.tconstruct.common.ClientEventBase;

/**
 * Client half of the fluids module: which fluids draw see-through, and the potion bucket's tint.
 *
 * <p>Fabric port: the model loader that lived here registers from the client entrypoint with the
 * others. Render layers move from {@code ItemBlockRenderTypes} — which is not safe to mutate on
 * Fabric — to {@code BlockRenderLayerMap}, and the item colour registers per item rather than
 * through a handed-out {@code ItemColors}.
 */
public class FluidClientEvents extends ClientEventBase {
  /** Registers the fluid client hooks */
  public static void init() {
    setTranslucent(TinkerFluids.honey);
    // slime
    setTranslucent(TinkerFluids.earthSlime);
    setTranslucent(TinkerFluids.skySlime);
    setTranslucent(TinkerFluids.enderSlime);
    // molten
    setTranslucent(TinkerFluids.moltenDiamond);
    setTranslucent(TinkerFluids.moltenEmerald);
    setTranslucent(TinkerFluids.moltenGlass);
    setTranslucent(TinkerFluids.liquidSoul);
    setTranslucent(TinkerFluids.moltenSoulsteel);
    setTranslucent(TinkerFluids.moltenAmethyst);

    // the potion bucket takes its colour from the potion inside it
    ColorProviderRegistry.ITEM.register(
      (stack, index) -> index > 0 ? -1 : stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor(),
      TinkerFluids.potion.asItem());
  }

  /** Draws a fluid with the translucent layer, so what is behind it shows through */
  private static void setTranslucent(FlowingFluidObject<?> fluid) {
    BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), fluid.getStill(), fluid.getFlowing());
  }
}
