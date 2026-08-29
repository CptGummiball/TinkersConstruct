package slimeknights.tconstruct.fluids.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

/** Liquid block applying an effect to entities inside it */
public class MobEffectLiquidBlock extends LiquidBlock {
  private final Supplier<MobEffectInstance> effect;
  public MobEffectLiquidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties, Supplier<MobEffectInstance> effect) {
    // 1.21 wants the fluid directly; the mantle builder registers blocks after fluids, so the supplier is filled
    super(supplier.get(), properties);
    this.effect = effect;
  }

  @Override
  public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    // Forge checked getFluidTypeHeight > 0; without that extension, check whether the
    // entity's feet are actually below the fluid surface at this position.
    // 1.21 note: the curative-items reset is gone with that API; milk can clear these
    // short-lived fluid effects until the milk hook lands with the event layer.
    if (entity instanceof LivingEntity living && entity.getY() < pos.getY() + state.getFluidState().getHeight(level, pos)) {
      living.addEffect(this.effect.get());
    }
  }

  /** Creates a new block supplier */
  public static Function<Supplier<? extends FlowingFluid>, LiquidBlock> createEffect(MapColor color, int lightLevel, Supplier<MobEffectInstance> effect) {
    return fluid -> new MobEffectLiquidBlock(fluid, FluidDeferredRegister.createProperties(color, lightLevel), effect);
  }
}
