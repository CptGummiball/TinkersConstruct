package slimeknights.tconstruct.fluids.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;
import java.util.function.Supplier;

/**
 * Bucket holding a potion, 2.5x the effect duration of a bottle.
 *
 * <p>1.21 notes: the potion lives in the vanilla {@code potion_contents} component (so all
 * vanilla naming/tooltip logic applies); the fluid form uses the legacy {@code Potion} NBT
 * key, converted by {@code PotionFluidType}. The Forge bucket capability is replaced by the
 * potion-aware storage registered in {@code TinkerFluidStorage}.
 */
public class PotionBucketItem extends PotionItem {
  private final Supplier<? extends Fluid> supplier;
  public PotionBucketItem(Supplier<? extends Fluid> supplier, Properties builder) {
    super(builder);
    this.supplier = supplier;
  }

  public Fluid getFluid() {
    return supplier.get();
  }

  /** Gets the potion contents of the stack */
  private static PotionContents contents(ItemStack stack) {
    return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
  }

  @Override
  public String getDescriptionId(ItemStack stack) {
    String bucketKey = Potion.getName(contents(stack).potion(), getDescriptionId() + ".effect.");
    if (Util.canTranslate(bucketKey)) {
      return bucketKey;
    }
    return super.getDescriptionId();
  }

  @Override
  public Component getName(ItemStack stack) {
    PotionContents contents = contents(stack);
    String bucketKey = Potion.getName(contents.potion(), getDescriptionId() + ".effect.");
    if (Util.canTranslate(bucketKey)) {
      return Component.translatable(bucketKey);
    }
    // default to filling with the contents
    return Component.translatable(getDescriptionId() + ".contents", Component.translatable(Potion.getName(contents.potion(), "item.minecraft.potion.effect.")));
  }

  @Override
  public ItemStack getDefaultInstance() {
    return PotionContents.createItemStack(this, Potions.AWKWARD);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
    Player player = living instanceof Player p ? p : null;
    if (player instanceof ServerPlayer serverPlayer) {
      CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
    }

    // effects are 2.5x duration
    if (!level.isClientSide) {
      for (MobEffectInstance effect : contents(stack).getAllEffects()) {
        if (effect.getEffect().value().isInstantenous()) {
          effect.getEffect().value().applyInstantenousEffect(player, player, living, effect.getAmplifier(), 2.5D);
        } else {
          living.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration() * 5 / 2, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
        }
      }
    }

    if (player != null) {
      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.getAbilities().instabuild) {
        stack.shrink(1);
      }
    }

    if (player == null || !player.getAbilities().instabuild) {
      if (stack.isEmpty()) {
        return new ItemStack(Items.BUCKET);
      }
      if (player != null) {
        player.getInventory().add(new ItemStack(Items.BUCKET));
      }
    }
    living.gameEvent(GameEvent.DRINK);
    return stack;
  }

  @Override
  public void appendHoverText(ItemStack pStack, TooltipContext context, List<Component> pTooltip, TooltipFlag pFlag) {
    contents(pStack).addPotionTooltip(pTooltip::add, 2.5f, context.tickRate());
  }

  @Override
  public int getUseDuration(ItemStack pStack, LivingEntity entity) {
    return 96; // 3x duration of potion bottles
  }
}
