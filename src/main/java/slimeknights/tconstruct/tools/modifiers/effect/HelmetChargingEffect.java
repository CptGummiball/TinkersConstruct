package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * Effect for rendering the charge up when you start using a helmet.
 *
 * <p>phase 5: the Forge initializeClient charge-bar overlay (hidden inventory icon plus a
 * draw-progress bar over the effect icon) returns with the client HUD system.
 */
public class HelmetChargingEffect extends MobEffect {
  public HelmetChargingEffect() {
    super(MobEffectCategory.NEUTRAL, -1);
  }

  public List<ItemStack> getCurativeItems() {
    return new ArrayList<>();
  }

  public static int startUsingHelmet(IToolStackView tool, LivingEntity living, float speedFactor) {
    int time = GeneralInteractionModifierHook.startDrawing(tool, living, speedFactor);
    living.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(TinkerModifiers.helmetCharging.get()), time + 20, 0, true, false, true));
    return time;
  }
}
