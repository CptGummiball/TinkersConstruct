package slimeknights.tconstruct.library.client.model;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/** Properties for tinker tools */
public class TinkerItemProperties {
  /** ID for broken property */
  private static final ResourceLocation BROKEN_ID = TConstruct.getResource("broken");
  /** Property declaring broken */
  private static final ItemPropertyFunction BROKEN = (stack, level, entity, seed) -> {
    return ToolDamageUtil.isBroken(stack) ? 1 : 0;
  };

  /** ID for ammo property */
  private static final ResourceLocation AMMO_ID = TConstruct.getResource("ammo");
  /** Int declaring ammo type */
  private static final ItemPropertyFunction AMMO = (stack, level, entity, seed) -> {
    CompoundTag nbt = slimeknights.tconstruct.library.tools.nbt.TagCompat.getTag(stack);
    if (nbt != null) {
      CompoundTag persistentData = nbt.getCompound(ToolStack.TAG_PERSISTENT_MOD_DATA);
      if (!persistentData.isEmpty()) {
        CompoundTag ammo = persistentData.getCompound(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO.toString());
        if (!ammo.isEmpty()) {
          // no sense having two keys for ammo, just set 1 for arrow, 2 for fireworks
          return ammo.getString("id").equals(BuiltInRegistries.ITEM.getKey(Items.FIREWORK_ROCKET).toString()) ? 2 : 1;
        }
      }
    }
    return 0;
  };

  /** ID for the pulling property */
  private static final ResourceLocation CHARGING_ID = TConstruct.getResource("charging");
  /** Boolean indicating the bow is pulling */
  private static final ItemPropertyFunction CHARGING = (stack, level, holder, seed) -> {
    if (holder != null && holder.isUsingItem() && holder.getUseItem() == stack) {
      UseAnim anim = stack.getUseAnimation();
      if (anim == UseAnim.BLOCK) {
        return ModifierUtil.checkPersistentPresent(stack, ModifiableLauncherItem.KEY_DRAWBACK_AMMO) ? 2.5f : 2;
      }
      // TODO 1.21: space this out a bit more
      if (anim == UseAnim.SPEAR) {
        // shouldn't need to worry about arrows on spearing, everything supporting arrows uses just bow or block
        return 1.75f;
      }
      if (anim != UseAnim.EAT && anim != UseAnim.DRINK) {
        return ModifierUtil.checkPersistentPresent(stack, ModifiableLauncherItem.KEY_DRAWBACK_AMMO) ? 1.5f : 1;
      }
    }
    return 0;
  };
  /** ID for the pull property */
  private static final ResourceLocation CHARGE_ID = TConstruct.getResource("charge");
  /** Property for bow pull amount */
  private static final ItemPropertyFunction CHARGE = (stack, level, holder, seed) -> {
    if (holder == null || holder.getUseItem() != stack) {
      return 0.0F;
    }
    int drawtime = ModifierUtil.getPersistentInt(stack, GeneralInteractionModifierHook.KEY_DRAWTIME, -1);
    return drawtime == -1 ? 0 : (float)(stack.getUseDuration(holder) - holder.getUseItemRemainingTicks()) / drawtime;
  };
  /** ID for the cast fishing rods */
  private static final ResourceLocation CAST_ID = TConstruct.getResource("cast");
  /** Property for casting a fishing rod */
  private static final ItemPropertyFunction CAST = (stack, level, holder, seed) -> {
    // must be a fishing rod, and the player must be fishing
    // does player check first since its the fastest, avoids NBT parsing
    // Forge answered the action on the stack itself; ModifierUtil does that job here, and also
    // covers the vanilla rod the off-hand branch may be comparing against
    if (holder instanceof Player player && player.fishing != null && ModifierUtil.canCastFishingRod(stack)) {
      // must be in a hand, but if both hands have fishing rods, must be the one in the main hand
      ItemStack mainhand = holder.getMainHandItem();
      if (mainhand == stack || holder.getOffhandItem() == stack && !ModifierUtil.canCastFishingRod(mainhand)) {
        return 1;
      }
    }
    return 0;
  };

  /**
   * Keeps a property function's value as it is.
   *
   * <p>1.21 only accepts a {@link ClampedItemPropertyFunction}, whose {@code call} clamps to
   * {@code [0,1]} — and {@code ItemOverrides} goes through {@code call}. Several of the properties
   * above return more than one to name a variant rather than a fraction: {@code charging} reaches
   * 2.5 and {@code ammo} reaches 2, and clamping would collapse each of those onto the same model.
   * Forge registered them through an unclamped overload; overriding {@code call} is the same thing.
   */
  private record Unclamped(ItemPropertyFunction function) implements ClampedItemPropertyFunction {
    @Override
    public float call(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
      return function.call(stack, level, entity, seed);
    }

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
      return function.call(stack, level, entity, seed);
    }
  }

  /** Registers a property, keeping values outside {@code [0,1]} intact */
  private static void register(Item item, ResourceLocation id, ItemPropertyFunction function) {
    ItemProperties.register(item, id, new Unclamped(function));
  }

  /** Registers properties for a tool, including the option to have charge/block animations */
  public static void registerBrokenProperty(Item item) {
    register(item, BROKEN_ID, BROKEN);
  }

  /** Registers properties for a tool, including the option to have charge/block animations */
  public static void registerToolProperties(ItemLike itemlike) {
    Item item = itemlike.asItem();
    registerBrokenProperty(item);
    register(item, CHARGING_ID, CHARGING);
    register(item, CHARGE_ID, CHARGE);
    register(item, CAST_ID, CAST);
  }

  /** Registers properties for a bow */
  public static void registerCrossbowProperties(ItemLike item) {
    registerToolProperties(item);
    register(item.asItem(), AMMO_ID, AMMO);
  }
}
