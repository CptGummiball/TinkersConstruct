package slimeknights.mantle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Draws the items sitting on an inventory block, such as the crafting station and the tinker station.
 *
 * <p>Written for this tree; Mantle's client packages were never copied in. Upstream Mantle calls this
 * {@code InventoryTileEntityRenderer}; renamed to match the naming the rest of this tree uses.
 */
public class InventoryBlockEntityRenderer<T extends BlockEntity & Container> implements BlockEntityRenderer<T> {
  @SuppressWarnings("unused")  // nicer lambda
  public InventoryBlockEntityRenderer(Context context) {}

  @Override
  public void render(T inventory, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int light, int overlay) {
    BlockState state = inventory.getBlockState();
    List<RenderItem> items = RenderItem.STATE_REGISTRY.get(state, List.of());
    if (items.isEmpty()) {
      return;
    }
    boolean isRotated = RenderingHelper.applyRotation(matrices, state);
    int max = Math.min(items.size(), inventory.getContainerSize());
    for (int i = 0; i < max; i++) {
      RenderingHelper.renderItem(matrices, buffer, inventory.getItem(i), items.get(i), light);
    }
    if (isRotated) {
      matrices.popPose();
    }
  }
}
