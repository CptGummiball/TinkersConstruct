package slimeknights.tconstruct.smeltery.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import slimeknights.mantle.transfer.cap.ICapabilityProvider;
import slimeknights.mantle.transfer.fluid.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluid container holding 1 ingot of fluid
 */
public class CopperCanItem extends Item {
  private static final String TAG_FLUID = "fluid";
  private static final String TAG_FLUID_TAG = "fluid_tag";

  public CopperCanItem(Properties properties) {
    super(properties);
  }

  // Forge initCapabilities replaced by the FluidStorage.ITEM registration in
  // TinkerSmeltery.init(), which bridges CopperCanFluidHandler

  @Override
  public ItemStack getRecipeRemainder(ItemStack stack) {
    // Fabric's stack-aware remainder replaces Forge's has/getCraftingRemainingItem pair
    return getFluid(stack) != Fluids.EMPTY ? new ItemStack(this) : ItemStack.EMPTY;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    Fluid fluid = getFluid(stack);
    if (fluid != Fluids.EMPTY) {
      CompoundTag fluidTag = getFluidTag(stack);
      // the display name resolves through FluidVariantAttributes either way
      MutableComponent text = new FluidStack(fluid, FluidValues.INGOT, fluidTag).getDisplayName().plainCopy();
      tooltip.add(Component.translatable(this.getDescriptionId() + ".contents", text).withStyle(ChatFormatting.GRAY));
      if (flag.isAdvanced()) {
        tooltip.add(Component.translatable(TankItem.FLUID_ID, Loadables.FLUID.getKey(fluid)).withStyle(ChatFormatting.DARK_GRAY));
      }
    } else {
      tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }
  }

  /** Removes the fluid from the given stack */
  public static void removeFluid(ItemStack stack) {
    CompoundTag nbt = slimeknights.tconstruct.library.tools.nbt.TagCompat.getTag(stack);
    if (nbt != null) {
      nbt.remove(TAG_FLUID);
      nbt.remove(TAG_FLUID_TAG);
      slimeknights.tconstruct.library.tools.nbt.TagCompat.setTag(stack, nbt.isEmpty() ? null : nbt);
    }
  }

  /** Sets the fluid on the given stack whether or not its valiid */
  private static void setFluidInternal(ItemStack stack, ResourceLocation fluid, @Nullable CompoundTag fluidTag) {
    CompoundTag nbt = slimeknights.tconstruct.library.tools.nbt.TagCompat.getOrCreateTag(stack);
    nbt.putString(TAG_FLUID, fluid.toString());
    if (fluidTag != null) {
      nbt.put(TAG_FLUID_TAG, fluidTag.copy());
    } else {
      nbt.remove(TAG_FLUID_TAG);
    }
  }


  /** Sets the fluid on the given stack */
  @SuppressWarnings("deprecation")
  public static ItemStack setFluid(ItemStack stack, ResourceLocation fluid, @Nullable CompoundTag fluidTag) {
    // if empty, try to remove the NBT, helps with recipes
    if (fluid.equals(BuiltInRegistries.FLUID.getDefaultKey())) {
      removeFluid(stack);
    } else {
      setFluidInternal(stack, fluid, fluidTag);
    }
    return stack;
  }
  /** Sets the fluid on the given stack */
  @SuppressWarnings("deprecation")
  public static ItemStack setFluid(ItemStack stack, Fluid fluid, @Nullable CompoundTag fluidTag) {
    // if empty, try to remove the NBT, helps with recipes
    if (fluid == Fluids.EMPTY) {
      removeFluid(stack);
    } else {
      setFluidInternal(stack, BuiltInRegistries.FLUID.getKey(fluid), fluidTag);
    }
    return stack;
  }

  /** Sets the fluid on the given stack */
  public static ItemStack setFluid(ItemStack stack, FluidStack fluid) {
    return setFluid(stack, fluid.getFluid(), fluid.getTag());
  }

  /** Gets the fluid from the given stack */
  public static Fluid getFluid(ItemStack stack) {
    CompoundTag nbt = slimeknights.tconstruct.library.tools.nbt.TagCompat.getTag(stack);
    if (nbt != null && nbt.contains(TAG_FLUID, Tag.TAG_STRING)) {
      ResourceLocation location = ResourceLocation.tryParse(nbt.getString(TAG_FLUID));
      if (location != null && BuiltInRegistries.FLUID.containsKey(location)) {
        Fluid fluid = BuiltInRegistries.FLUID.get(location);
        if (fluid != null) {
          return fluid;
        }
      }
    }
    return Fluids.EMPTY;
  }

  /** Adds filled variants of the copper can to the given consumer */
  @SuppressWarnings("deprecation")
  public static void addFilledVariants(Consumer<ItemStack> output) {
    BuiltInRegistries.FLUID.holders().filter(holder -> {
      Fluid fluid = holder.value();
      return fluid.isSource(fluid.defaultFluidState()) && !holder.is(TinkerTags.Fluids.HIDE_IN_CREATIVE_TANKS);
    }).forEachOrdered(holder -> {
      output.accept(CopperCanItem.setFluid(new ItemStack(TinkerSmeltery.copperCan), holder.key().location(), null));
    });
  }

  /** Gets the fluid NBT from the given stack */
  @Nullable
  public static CompoundTag getFluidTag(ItemStack stack) {
    CompoundTag nbt = slimeknights.tconstruct.library.tools.nbt.TagCompat.getTag(stack);
    if (nbt != null && nbt.contains(TAG_FLUID_TAG, Tag.TAG_COMPOUND)) {
      return nbt.getCompound(TAG_FLUID_TAG);
    }
    return null;
  }

  /**
   * Gets a string variant name for the given stack
   * @param stack  Stack instance to check
   * @return  String variant name
   */
  public static String getSubtype(ItemStack stack) {
    CompoundTag nbt = slimeknights.tconstruct.library.tools.nbt.TagCompat.getTag(stack);
    if (nbt != null) {
      return nbt.getString(TAG_FLUID);
    }
    return "";
  }
}
