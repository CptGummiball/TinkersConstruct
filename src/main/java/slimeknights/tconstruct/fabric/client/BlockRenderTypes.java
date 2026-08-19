package slimeknights.tconstruct.fabric.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Applies the render type each block model declares.
 *
 * <p>Forge let a model JSON carry {@code "render_type"} and honored it directly; vanilla and
 * Fabric only know a per-block mapping, and neither reads that key. Rather than duplicating
 * the information in a hand-kept list that drifts from the models, this walks each block's
 * blockstate to the models it references and applies whatever they declare — so the models
 * stay the single source of truth, exactly as they were on Forge.
 *
 * <p>Runs once against the client resource manager; the mapping is global and not reloadable,
 * which matches the Fabric API's own contract.
 */
public class BlockRenderTypes {
  private BlockRenderTypes() {}

  private static boolean applied = false;

  /**
   * Hooks the first client resource load; the mapping is global and one-shot, so later
   * reloads are ignored rather than re-registering.
   */
  public static void init() {
    net.fabricmc.fabric.api.resource.ResourceManagerHelper.get(net.minecraft.server.packs.PackType.CLIENT_RESOURCES)
      .registerReloadListener(new net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener() {
        @Override
        public ResourceLocation getFabricId() {
          return TConstruct.getResource("block_render_types");
        }

        @Override
        public void onResourceManagerReload(ResourceManager manager) {
          apply(manager);
        }
      });
  }

  /** Reads every Tinkers block's models and applies their declared render type */
  public static void apply(ResourceManager manager) {
    if (applied) {
      return;
    }
    applied = true;
    int count = 0;
    for (Map.Entry<ResourceLocation,Block> entry : BuiltInRegistries.BLOCK.entrySet().stream()
                                                                          .filter(e -> e.getKey().location().getNamespace().equals(TConstruct.MOD_ID))
                                                                          .map(e -> Map.entry(e.getKey().location(), e.getValue()))
                                                                          .toList()) {
      RenderType type = resolve(manager, entry.getKey());
      if (type != null) {
        BlockRenderLayerMap.INSTANCE.putBlock(entry.getValue(), type);
        count++;
      }
    }
    TConstruct.LOG.info("Applied model-declared render types to {} blocks", count);
  }

  /** Finds the render type declared by any model the block's blockstate references */
  @Nullable
  private static RenderType resolve(ResourceManager manager, ResourceLocation block) {
    JsonObject blockstate = readJson(manager, ResourceLocation.fromNamespaceAndPath(block.getNamespace(), "blockstates/" + block.getPath() + ".json"));
    if (blockstate == null) {
      return null;
    }
    RenderType found = null;
    for (ResourceLocation model : collectModels(blockstate)) {
      RenderType type = readRenderType(manager, model);
      // a block renders on one layer; if its models disagree, the most permissive wins so
      // nothing is silently culled (translucent > cutout > solid)
      if (type != null && (found == null || priority(type) > priority(found))) {
        found = type;
      }
    }
    return found;
  }

  /** Collects every model id a blockstate references, across both variant and multipart forms */
  private static Set<ResourceLocation> collectModels(JsonObject blockstate) {
    Set<ResourceLocation> models = new HashSet<>();
    if (blockstate.has("variants")) {
      for (Map.Entry<String,JsonElement> variant : GsonHelper.getAsJsonObject(blockstate, "variants").entrySet()) {
        addModels(models, variant.getValue());
      }
    }
    if (blockstate.has("multipart")) {
      for (JsonElement part : GsonHelper.getAsJsonArray(blockstate, "multipart")) {
        if (part.isJsonObject() && part.getAsJsonObject().has("apply")) {
          addModels(models, part.getAsJsonObject().get("apply"));
        }
      }
    }
    return models;
  }

  /** A variant entry is either a single model object or a weighted array of them */
  private static void addModels(Set<ResourceLocation> models, JsonElement element) {
    if (element.isJsonArray()) {
      for (JsonElement child : element.getAsJsonArray()) {
        addModels(models, child);
      }
    } else if (element.isJsonObject()) {
      JsonObject object = element.getAsJsonObject();
      if (object.has("model")) {
        models.add(ResourceLocation.parse(GsonHelper.getAsString(object, "model")));
      }
    }
  }

  /** Reads the render type a model declares, following parents is unnecessary as the generator writes it on the leaf */
  @Nullable
  private static RenderType readRenderType(ResourceManager manager, ResourceLocation model) {
    JsonObject json = readJson(manager, ResourceLocation.fromNamespaceAndPath(model.getNamespace(), "models/" + model.getPath() + ".json"));
    if (json == null || !json.has("render_type")) {
      return null;
    }
    return switch (GsonHelper.getAsString(json, "render_type")) {
      case "minecraft:translucent", "translucent" -> RenderType.translucent();
      case "minecraft:cutout", "cutout" -> RenderType.cutout();
      case "minecraft:cutout_mipped", "cutout_mipped" -> RenderType.cutoutMipped();
      case "minecraft:solid", "solid" -> RenderType.solid();
      default -> null;
    };
  }

  /** Ranks layers so a block whose models disagree renders on the most permissive one */
  private static int priority(RenderType type) {
    if (type == RenderType.translucent()) {
      return 3;
    }
    if (type == RenderType.cutout()) {
      return 2;
    }
    if (type == RenderType.cutoutMipped()) {
      return 1;
    }
    return 0;
  }

  @Nullable
  private static JsonObject readJson(ResourceManager manager, ResourceLocation path) {
    Optional<Resource> resource = manager.getResource(path);
    if (resource.isEmpty()) {
      return null;
    }
    try (BufferedReader reader = resource.get().openAsReader()) {
      return GsonHelper.parse(reader);
    } catch (Exception e) {
      TConstruct.LOG.error("Failed reading {} while resolving render types", path, e);
      return null;
    }
  }
}
