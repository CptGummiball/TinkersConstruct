package slimeknights.tconstruct.fabric.client;

import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.level.block.SkullBlock;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects the mod's skull types so they can join vanilla's skull model map.
 *
 * <p>Forge fired {@code EntityRenderersEvent.CreateSkullModels} for this. Vanilla builds the map in
 * {@code SkullBlockRenderer.createSkullRenderers} from a hardcoded list and Fabric adds no hook, so
 * {@code SkullModelRendererMixin} appends what is registered here. Registration and baking are
 * separate because the entity model set only exists once the renderers are being built, while the
 * types are known at client init.
 */
public final class SkullModelRegistry {
  private SkullModelRegistry() {}

  private record Entry(ModelLayerLocation layer, boolean piglin) {}

  /** Insertion ordered so the log and any iteration are stable */
  private static final Map<SkullBlock.Type,Entry> TYPES = new LinkedHashMap<>();

  /**
   * Registers a skull type's model.
   *
   * @param piglin  Whether the head uses the piglin model, which has ears
   */
  public static void register(SkullBlock.Type type, ModelLayerLocation layer, boolean piglin) {
    TYPES.put(type, new Entry(layer, piglin));
  }

  /** Bakes every registered type into the given map */
  public static void bakeInto(Map<SkullBlock.Type,SkullModelBase> models, EntityModelSet modelSet) {
    TYPES.forEach((type, entry) -> models.put(type, entry.piglin()
                                                    ? new PiglinHeadModel(modelSet.bakeLayer(entry.layer()))
                                                    : new SkullModel(modelSet.bakeLayer(entry.layer()))));
  }
}
