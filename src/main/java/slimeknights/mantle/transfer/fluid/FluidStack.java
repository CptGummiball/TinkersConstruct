package slimeknights.mantle.transfer.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.fluids.FluidStack}.
 *
 * <p>The API surface is kept identical to the Forge 1.20.1 class on purpose: 173 files and
 * ~880 call sites use it, so matching the original signatures turns the port of those files
 * into an import rewrite instead of a rewrite of the logic.
 *
 * <h2>Units</h2>
 * Amounts are <b>millibuckets</b> (1000/bucket), as in Forge and as in every fluid amount
 * baked into the existing recipe JSONs. Fabric's Transfer API counts droplets
 * (81000/bucket); conversion happens only at the {@code Storage} boundary via
 * {@link #toDroplets(int)}. 81000/1000 divides exactly, so no rounding error is possible.
 *
 * <h2>Tags</h2>
 * Forge 1.20.1 gave each stack a nullable {@link CompoundTag}. 1.21 replaced that with
 * data components, but {@code FluidVariant} can carry the legacy tag as a single component
 * (see {@link TransferComponents#FLUID_TAG}), which keeps the 153 {@code getTag()} call
 * sites working and round-trips losslessly through Fabric storages.
 */
public class FluidStack {

  /** Droplets per millibucket. Fabric uses 81000 droplets/bucket, Forge 1000 mB/bucket. */
  public static final int DROPLETS_PER_MB = 81;

  public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0, null);

  public static final Codec<FluidStack> CODEC = RecordCodecBuilder.create(inst -> inst.group(
    BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
    Codec.INT.fieldOf("amount").forGetter(FluidStack::getAmount),
    CompoundTag.CODEC.optionalFieldOf("tag").forGetter(s -> java.util.Optional.ofNullable(s.getTag()))
  ).apply(inst, (fluid, amount, tag) -> new FluidStack(fluid, amount, tag.orElse(null))));

  public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> STREAM_CODEC =
    StreamCodec.of(FluidStack::encode, FluidStack::decode);

  private Fluid fluid;
  private int amount;
  @Nullable
  private CompoundTag tag;

  public FluidStack(Fluid fluid, int amount) {
    this(fluid, amount, null);
  }

  public FluidStack(Fluid fluid, int amount, @Nullable CompoundTag tag) {
    this.fluid = fluid;
    this.amount = amount;
    this.tag = tag;
  }

  public FluidStack(FluidStack copy, int amount) {
    this(copy.getFluid(), amount, copy.tag == null ? null : copy.tag.copy());
  }

  /* Fabric interop */

  /** Wraps a Fabric variant plus a millibucket amount. */
  public static FluidStack of(FluidVariant variant, int amountMb) {
    if (variant.isBlank() || amountMb <= 0) {
      return EMPTY;
    }
    return new FluidStack(variant.getFluid(), amountMb, TransferComponents.tagOf(variant));
  }

  /** Wraps a Fabric variant plus a droplet amount, rounding down to whole millibuckets. */
  public static FluidStack ofDroplets(FluidVariant variant, long droplets) {
    return of(variant, (int) (droplets / DROPLETS_PER_MB));
  }

  /** Converts to a Fabric variant, carrying the legacy tag as a component. */
  public FluidVariant getVariant() {
    return isEmpty() ? FluidVariant.blank() : TransferComponents.variantOf(fluid, tag);
  }

  /** This stack's amount expressed in Fabric droplets. */
  public long getDroplets() {
    return toDroplets(amount);
  }

  public static long toDroplets(int millibuckets) {
    return (long) millibuckets * DROPLETS_PER_MB;
  }

  public static int toMillibuckets(long droplets) {
    return (int) (droplets / DROPLETS_PER_MB);
  }

  /* Forge-compatible accessors */

  public Fluid getFluid() {
    return isEmpty() ? Fluids.EMPTY : fluid;
  }

  /** The fluid ignoring the empty check, matching Forge's {@code getRawFluid}. */
  public Fluid getRawFluid() {
    return fluid;
  }

  public boolean isEmpty() {
    return fluid == Fluids.EMPTY || amount <= 0;
  }

  public int getAmount() {
    return isEmpty() ? 0 : amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public void grow(int amount) {
    setAmount(this.amount + amount);
  }

  public void shrink(int amount) {
    setAmount(this.amount - amount);
  }

  @Nullable
  public CompoundTag getTag() {
    return tag;
  }

  public boolean hasTag() {
    return tag != null;
  }

  public void setTag(@Nullable CompoundTag tag) {
    this.tag = tag;
  }

  public CompoundTag getOrCreateTag() {
    if (tag == null) {
      tag = new CompoundTag();
    }
    return tag;
  }

  public Component getDisplayName() {
    return isEmpty() ? Component.empty() : FluidVariantAttributes.getName(getVariant());
  }

  /** Matches the {@code fluid.<namespace>.<path>} convention the existing lang files use. */
  public String getTranslationKey() {
    ResourceLocation id = BuiltInRegistries.FLUID.getKey(getFluid());
    return Util.makeDescriptionId("fluid", id);
  }

  public FluidStack copy() {
    return new FluidStack(fluid, amount, tag == null ? null : tag.copy());
  }

  /** True when fluid and tag match, ignoring amount. Forge's {@code isFluidEqual}. */
  public boolean isFluidEqual(FluidStack other) {
    return getFluid() == other.getFluid() && java.util.Objects.equals(tag, other.tag);
  }

  public boolean containsFluid(FluidStack other) {
    return isFluidEqual(other) && amount >= other.amount;
  }

  /* Serialisation — the on-disk shape matches Forge's so existing saves load unchanged */

  public CompoundTag writeToNBT(CompoundTag nbt) {
    ResourceLocation id = BuiltInRegistries.FLUID.getKey(getFluid());
    nbt.putString("FluidName", id.toString());
    nbt.putInt("Amount", amount);
    if (tag != null) {
      nbt.put("Tag", tag.copy());
    }
    return nbt;
  }

  public static FluidStack loadFluidStackFromNBT(@Nullable CompoundTag nbt) {
    if (nbt == null || !nbt.contains("FluidName")) {
      return EMPTY;
    }
    ResourceLocation id = ResourceLocation.tryParse(nbt.getString("FluidName"));
    if (id == null) {
      return EMPTY;
    }
    Fluid fluid = BuiltInRegistries.FLUID.get(id);
    if (fluid == Fluids.EMPTY) {
      return EMPTY;
    }
    CompoundTag tag = nbt.contains("Tag") ? nbt.getCompound("Tag") : null;
    return new FluidStack(fluid, nbt.getInt("Amount"), tag);
  }

  private static void encode(RegistryFriendlyByteBuf buf, FluidStack stack) {
    if (stack.isEmpty()) {
      buf.writeVarInt(0);
      return;
    }
    buf.writeVarInt(stack.amount);
    buf.writeById(BuiltInRegistries.FLUID::getId, stack.getFluid());
    ByteBufCodecs.OPTIONAL_COMPOUND_TAG.encode(buf, java.util.Optional.ofNullable(stack.tag));
  }

  private static FluidStack decode(RegistryFriendlyByteBuf buf) {
    int amount = buf.readVarInt();
    if (amount <= 0) {
      return EMPTY;
    }
    Fluid fluid = buf.readById(BuiltInRegistries.FLUID::byId);
    CompoundTag tag = ByteBufCodecs.OPTIONAL_COMPOUND_TAG.decode(buf).orElse(null);
    return new FluidStack(fluid == null ? Fluids.EMPTY : fluid, amount, tag);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FluidStack other)) {
      return false;
    }
    return amount == other.amount && isFluidEqual(other);
  }

  @Override
  public int hashCode() {
    int result = getFluid().hashCode();
    result = 31 * result + amount;
    result = 31 * result + (tag == null ? 0 : tag.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return isEmpty() ? "FluidStack.EMPTY" : amount + "mB " + BuiltInRegistries.FLUID.getKey(getFluid());
  }
}
