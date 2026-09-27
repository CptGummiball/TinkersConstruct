package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.mantle.transfer.fluid.FluidType;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.tools.nbt.TagCompat;

import javax.annotation.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Fluid type for the potion fluid: name and bucket contents derive from the potion stored in
 * the fluid's legacy NBT tag ({@code Potion} string key, the shape all recipes/data use).
 *
 * <p>1.21 notes: {@code PotionUtils} is gone; the potion resolves against the registry from
 * the tag. The tint-color client extension moves to the fluid render handler in phase 5.
 */
public class PotionFluidType extends FluidType {
  public PotionFluidType(Properties properties) {
    super(properties);
  }

  /** Reads the potion from a fluid tag, empty optional if unset or unknown */
  public static Optional<Holder.Reference<Potion>> getPotion(@Nullable CompoundTag tag) {
    if (tag != null && tag.contains("Potion", Tag.TAG_STRING)) {
      ResourceLocation id = ResourceLocation.tryParse(tag.getString("Potion"));
      if (id != null) {
        return BuiltInRegistries.POTION.getHolder(id);
      }
    }
    return Optional.empty();
  }

  @Override
  public String getDescriptionId(FluidStack stack) {
    return Potion.getName(getPotion(stack.getTag()).map(holder -> (Holder<Potion>) holder), "item.minecraft.potion.effect.");
  }

  @Override
  public ItemStack getBucket(FluidStack fluidStack) {
    ItemStack itemStack = new ItemStack(fluidStack.getFluid().getBucket());
    CompoundTag tag = fluidStack.getTag();
    if (tag != null) {
      TagCompat.setTag(itemStack, tag.copy());
    }
    return itemStack;
  }

  /** Creates the potion tag */
  private static CompoundTag potionTag(ResourceLocation location) {
    CompoundTag tag = new CompoundTag();
    tag.putString("Potion", location.toString());
    return tag;
  }

  /** Creates a fluid stack for the given potion */
  public static FluidStack potionFluid(ResourceKey<Potion> potion, int size) {
    return new FluidStack(TinkerFluids.potion.get(), size, potionTag(potion.location()));
  }

  /** Creates a fluid stack for the given potion */
  public static FluidStack potionFluid(Holder<Potion> potion, int size) {
    return new FluidStack(TinkerFluids.potion.get(), size, potionTag(potion.unwrapKey().orElseThrow().location()));
  }

  /** Creates a fluid stack for the given potion contents, water if no potion is set */
  public static FluidStack potionFluid(PotionContents contents, int size) {
    return contents.potion()
      .map(potion -> potionFluid(potion, size))
      .orElseGet(() -> new FluidStack(TinkerFluids.potion.get(), size, potionTag(BuiltInRegistries.POTION.getKey(Potions.WATER.value()))));
  }

  /** Creates a fluid output for the given potion */
  public static FluidOutput potionResult(Holder<Potion> potion, int size) {
    return FluidOutput.fromTag(Objects.requireNonNull(TinkerFluids.potion.getCommonTag()), size, potionTag(potion.unwrapKey().orElseThrow().location()));
  }

  /** Creates a potion bucket for the given potion */
  public static ItemStack potionBucket(ResourceKey<Potion> potion) {
    ItemStack stack = new ItemStack(TinkerFluids.potion);
    TagCompat.setTag(stack, potionTag(potion.location()));
    return stack;
  }

  /** Creates a potion bucket for the given potion */
  public static ItemStack potionBucket(Holder<Potion> potion) {
    return potionBucket(potion.unwrapKey().orElseThrow());
  }
}
