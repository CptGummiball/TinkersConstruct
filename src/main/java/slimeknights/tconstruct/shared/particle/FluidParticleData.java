package slimeknights.tconstruct.shared.particle;

import com.mojang.serialization.MapCodec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import slimeknights.mantle.transfer.fluid.FluidStack;

/**
 * Particle data for a fluid particle.
 *
 * <p>1.21: particle types define a {@link MapCodec} and a stream codec; the command-string
 * deserializer is gone (vanilla parses particle arguments through the codec now).
 */
@RequiredArgsConstructor
public class FluidParticleData implements ParticleOptions {

  @Getter
  private final ParticleType<FluidParticleData> type;
  @Getter
  private final FluidStack fluid;

  /** Legacy-style string form, used only for debug logging */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(BuiltInRegistries.PARTICLE_TYPE.getKey(getType()));
    builder.append(" ");
    builder.append(BuiltInRegistries.FLUID.getKey(fluid.getFluid()));
    CompoundTag nbt = fluid.getTag();
    if (nbt != null) {
      builder.append(nbt);
    }
    return builder.toString();
  }

  /** Particle type for a fluid particle */
  public static class Type extends ParticleType<FluidParticleData> {
    private final MapCodec<FluidParticleData> codec = FluidStack.CODEC.xmap(fluid -> new FluidParticleData(this, fluid), data -> data.fluid).fieldOf("fluid");
    private final StreamCodec<RegistryFriendlyByteBuf,FluidParticleData> streamCodec = FluidStack.STREAM_CODEC.map(fluid -> new FluidParticleData(this, fluid), data -> data.fluid);

    public Type() {
      super(false);
    }

    @Override
    public MapCodec<FluidParticleData> codec() {
      return codec;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf,FluidParticleData> streamCodec() {
      return streamCodec;
    }
  }
}
