package slimeknights.mantle.client.model.util;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.client.model.BakedModelWrapper;
import slimeknights.mantle.client.model.data.ModelData;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Baked model that rebakes itself from the data it is handed, caching the result.
 *
 * <p>Subclasses implement the data-carrying {@code getQuads} overload and leave the rest to
 * {@link BakedModelWrapper}, which routes the vanilla three-argument call into it.
 *
 * <p>PORT: upstream Mantle made the vanilla overload throw, on the grounds that Forge's renderer
 * always supplied model data and reaching the plain call meant a bug. Vanilla 1.21.1 has only the
 * plain call, so throwing would take down every block render; the inherited delegation with
 * {@link ModelData#EMPTY} is what the wrapper does instead, and a data-driven subclass falls back
 * to its base variant until render attachments are wired up (see {@link ModelData}).
 *
 * @param <T> wrapped model type
 */
public abstract class DynamicBakedWrapper<T extends BakedModel> extends BakedModelWrapper<T> {
  protected DynamicBakedWrapper(T originalModel) {
    super(originalModel);
  }

  @Override
  public abstract List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random, ModelData data, @Nullable RenderType renderType);
}
