package slimeknights.tconstruct.tools.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import slimeknights.mantle.transfer.fluid.FluidStack;

/** Serializer for fluid stack data in entities; 1.21 entity data serializers are stream-codec based */
public class FluidDataSerializer implements EntityDataSerializer<FluidStack> {
  @Override
  public StreamCodec<? super RegistryFriendlyByteBuf,FluidStack> codec() {
    return FluidStack.STREAM_CODEC;
  }

  @Override
  public FluidStack copy(FluidStack stack) {
    return stack.copy();
  }
}
