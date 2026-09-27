package slimeknights.mantle.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Bridges the legacy Forge fluid NBT tag onto 1.21 data components.
 *
 * <p>Forge 1.20.1 hung a nullable {@link CompoundTag} off every fluid stack. 1.21 replaced
 * loose NBT with typed components, and Fabric's {@code FluidVariant} carries a
 * {@link DataComponentPatch}. Rather than migrate 153 {@code getTag()} call sites — and the
 * potion-fluid logic that depends on the tag's shape — the whole tag travels inside one
 * component, so it survives every trip through a Fabric storage untouched.
 */
public final class TransferComponents {

  private TransferComponents() {}

  /** Carries the legacy Forge fluid NBT tag through Fabric's component-based variants. */
  public static final DataComponentType<CompoundTag> FLUID_TAG =
    DataComponentType.<CompoundTag>builder()
      .persistent(CompoundTag.CODEC)
      .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
      .build();

  /** Called once from the mod entrypoint; the type must exist before any variant is built. */
  public static void register() {
    Registry.register(
      BuiltInRegistries.DATA_COMPONENT_TYPE,
      ResourceLocation.fromNamespaceAndPath("tconstruct", "fluid_tag"),
      FLUID_TAG);
  }

  /** Builds a variant carrying the given legacy tag, if any. */
  public static FluidVariant variantOf(Fluid fluid, @Nullable CompoundTag tag) {
    if (tag == null || tag.isEmpty()) {
      return FluidVariant.of(fluid);
    }
    return FluidVariant.of(fluid, DataComponentPatch.builder().set(FLUID_TAG, tag.copy()).build());
  }

  /** Reads the legacy tag back out of a variant, or null when it carries none. */
  @Nullable
  public static CompoundTag tagOf(FluidVariant variant) {
    Optional<? extends CompoundTag> value = variant.getComponents().get(FLUID_TAG);
    return value == null ? null : value.orElse(null);
  }
}
