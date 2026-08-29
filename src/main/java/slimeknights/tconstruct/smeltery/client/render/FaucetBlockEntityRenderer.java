package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.MantleRenderTypes;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;
import slimeknights.tconstruct.smeltery.block.entity.FaucetBlockEntity;

import java.util.List;

public class FaucetBlockEntityRenderer implements BlockEntityRenderer<FaucetBlockEntity> {
  public FaucetBlockEntityRenderer(Context context) {}

  @Override
  public void render(FaucetBlockEntity tileEntity, float partialTicks, PoseStack matrices, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
    FluidStack renderFluid = tileEntity.getRenderFluid();
    if (!tileEntity.isPouring() || renderFluid.isEmpty()) {
      return;
    }

    // safety
    Level world = tileEntity.getLevel();
    if (world == null) {
      return;
    }

    // fetch faucet model to determine where to render fluids
    BlockState state = tileEntity.getBlockState();
    List<FluidCuboid> fluids = FluidCuboid.REGISTRY.get(state, List.of());
    if (!fluids.isEmpty()) {
      // if side, rotate fluid model
      Direction direction = state.getValue(FaucetBlock.FACING);
      boolean isRotated = RenderingHelper.applyRotation(matrices, direction);

      // fluid props; Forge read these off IClientFluidTypeExtensions, Fabric off the fluid variant
      TextureAtlasSprite still = FluidRenderer.getStillSprite(renderFluid);
      if (still == null) {
        if (isRotated) {
          matrices.popPose();
        }
        return;
      }
      TextureAtlasSprite flowing = FluidRenderer.getFlowingSprite(renderFluid);
      int color = FluidRenderer.getColor(renderFluid);
      combinedLightIn = FluidRenderer.withBlockLight(combinedLightIn, FluidRenderer.getLuminosity(renderFluid));

      // render all cubes in the model
      VertexConsumer buffer = bufferIn.getBuffer(MantleRenderTypes.FLUID);
      for (FluidCuboid cube : fluids) {
        FluidRenderer.renderCuboid(matrices, buffer, cube, 0, still, flowing, color, combinedLightIn, false);
      }

      // render into the block(s) below
      RenderingHelper.renderFaucetFluids(world, tileEntity.getBlockPos(), direction, matrices, buffer, still, flowing, color, combinedLightIn);

      // if rotated, pop back rotation
      if(isRotated) {
        matrices.popPose();
      }
    }
  }
}
