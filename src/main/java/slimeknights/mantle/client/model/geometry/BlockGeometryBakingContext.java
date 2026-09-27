package slimeknights.mantle.client.model.geometry;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

/**
 * {@link IGeometryBakingContext} backed by the vanilla parse of the model JSON that carried the
 * {@code "loader"} key.
 *
 * <p>A model JSON with a custom loader is still a perfectly ordinary model file as far as
 * {@code parent}, {@code textures}, {@code display}, {@code ambientocclusion} and {@code gui_light}
 * are concerned — vanilla simply ignores the extra keys. So the whole context can be read straight
 * off a {@link BlockModel} deserialized from the same text, which is exactly the data Forge handed
 * to the geometry through its patched {@code BlockGeometryBakingContext}.
 */
public class BlockGeometryBakingContext implements IGeometryBakingContext {
  private final BlockModel model;
  private final String name;

  public BlockGeometryBakingContext(BlockModel model, ResourceLocation location) {
    this.model = model;
    this.name = location.toString();
  }

  /** The vanilla-side model this context reads from; also the texture owner for nested baking. */
  public BlockModel getBlockModel() {
    return model;
  }

  @Override
  public String getModelName() {
    return name;
  }

  @Override
  public boolean hasMaterial(String name) {
    return model.hasTexture(name);
  }

  @Override
  public Material getMaterial(String name) {
    return model.getMaterial(name);
  }

  @Override
  public boolean useBlockLight() {
    return model.getGuiLight().lightLikeBlock();
  }

  @Override
  public boolean useAmbientOcclusion() {
    return model.hasAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return model.getGuiLight().lightLikeBlock();
  }

  @Override
  public ItemTransforms getTransforms() {
    return model.getTransforms();
  }
}
