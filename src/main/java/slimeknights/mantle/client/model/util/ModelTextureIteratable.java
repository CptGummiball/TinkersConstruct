package slimeknights.mantle.client.model.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.Material;
import slimeknights.mantle.client.model.geometry.BlockGeometryBakingContext;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Iterates a model's texture map, then each parent's, in resolution order.
 *
 * <p>Vanilla's {@code BlockModel.getMaterial} walks the same chain but collapses it to the final
 * {@link Material}, losing the intermediate {@code #name} hops. The connected and NBT-key models
 * need the hops themselves — one to learn which declared name a face's texture reference lands on,
 * the other to enumerate every name the model tree declares.
 */
public class ModelTextureIteratable implements Iterable<Map<String,Either<Material,String>>> {
  /** Map iterated before the model chain, or null for none */
  @Nullable
  private final Map<String,Either<Material,String>> startMap;
  /** First model whose map is iterated, or null to end after {@link #startMap} */
  @Nullable
  private final BlockModel startModel;

  public ModelTextureIteratable(@Nullable Map<String,Either<Material,String>> startMap, @Nullable BlockModel startModel) {
    this.startMap = startMap;
    this.startModel = startModel;
  }

  public ModelTextureIteratable(BlockModel model) {
    this(null, model);
  }

  /**
   * Iterates the model behind the given context, or the fallback when the context is not backed by
   * one — which is what a retexturing wrapper like {@code ExtraTextureContext} looks like.
   */
  public static ModelTextureIteratable of(IGeometryBakingContext owner, SimpleBlockModel fallback) {
    if (owner instanceof BlockGeometryBakingContext blockOwner) {
      return new ModelTextureIteratable(null, blockOwner.getBlockModel());
    }
    return new ModelTextureIteratable(fallback.getTextures(), fallback.getParent());
  }

  @Override
  public MapIterator iterator() {
    return new MapIterator(startMap, startModel);
  }

  private static class MapIterator implements Iterator<Map<String,Either<Material,String>>> {
    @Nullable
    private Map<String,Either<Material,String>> initial;
    @Nullable
    private BlockModel model;

    MapIterator(@Nullable Map<String,Either<Material,String>> initial, @Nullable BlockModel model) {
      this.initial = initial;
      this.model = model;
    }

    @Override
    public boolean hasNext() {
      return initial != null || model != null;
    }

    @Override
    public Map<String,Either<Material,String>> next() {
      Map<String,Either<Material,String>> map;
      if (initial != null) {
        map = initial;
        initial = null;
      } else if (model != null) {
        map = model.textureMap;
        model = model.parent;
      } else {
        throw new NoSuchElementException();
      }
      return map;
    }
  }
}
