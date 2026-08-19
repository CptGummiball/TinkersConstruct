package slimeknights.mantle.client.model.geometry;

import com.google.common.io.CharStreams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.Mantle;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * The Fabric half of the geometry bridge: turns a {@code "loader": "namespace:id"} key in a model
 * JSON back into custom geometry.
 *
 * <p>Forge patched its model deserializer to dispatch on that key. Fabric's seam is
 * {@link ModelResolver}, which is consulted for every model id <em>before</em> vanilla reads the
 * file, and may return an {@link UnbakedModel} of our own. So for each JSON that declares a
 * registered loader we hand back a {@link GeometryUnbakedModel} carrying both the custom geometry
 * and the ordinary vanilla parse of the same text.
 *
 * <h2>Why the work happens in a preparation stage</h2>
 * A resolver runs for <em>every</em> model in the game, so it must be cheap, and it must not ask the
 * model loader for the id it is currently resolving — {@code getOrLoadModel} on our own id would
 * re-enter this resolver and loop forever (Fabric's loader detects it and throws
 * "Circular reference while loading model"). Both problems go away by never consulting the loader:
 * {@link PreparableModelLoadingPlugin} hands us the reload's own {@link ResourceManager} on a
 * background executor, we read and parse the JSONs there ourselves — vanilla parse via
 * {@link BlockModel#fromString}, custom half via the registered {@link IGeometryLoader} — and the
 * resolver itself degrades to a map lookup.
 *
 * <p>A model whose loader id is not registered is left alone: returning null falls through to the
 * vanilla parse, which ignores the unknown key exactly as it does today.
 */
public final class GeometryModelLoadingPlugin implements PreparableModelLoadingPlugin<Map<ResourceLocation,UnbakedModel>> {
  private GeometryModelLoadingPlugin() {}

  private static final GeometryModelLoadingPlugin INSTANCE = new GeometryModelLoadingPlugin();

  /** Cheap pre-filter: skips the JSON parse for the ~99% of models that declare no loader. */
  private static final String LOADER_KEY_HINT = "\"loader\"";

  /**
   * Deserialization context handed to {@link IGeometryLoader#read}.
   *
   * <p>Backed by vanilla's own model GSON so {@code context.deserialize(json, BlockModel.class)}
   * builds elements, faces and display transforms with exactly the adapters vanilla uses — which is
   * what the geometry classes relied on under Forge.
   */
  private static final JsonDeserializationContext DESERIALIZATION_CONTEXT = new JsonDeserializationContext() {
    @Override
    public <T> T deserialize(JsonElement json, Type type) throws JsonParseException {
      return BlockModel.GSON.fromJson(json, type);
    }
  };

  /** Registers the plugin. Call once from the client entrypoint, after the loaders are registered. */
  public static void init() {
    PreparableModelLoadingPlugin.register(GeometryModelLoadingPlugin::prepare, INSTANCE);
  }

  @Override
  public void onInitializeModelLoader(Map<ResourceLocation,UnbakedModel> models, ModelLoadingPlugin.Context context) {
    if (models.isEmpty()) {
      return;
    }
    context.resolveModel().register(ctx -> models.get(ctx.id()));
  }

  /**
   * Scans every model file once per reload, keeping only those a registered loader claims.
   *
   * <p>Vanilla reads the same files in the same reload, so this is a second pass over already-warm
   * data; the {@link #LOADER_KEY_HINT} substring test keeps it to a read and a scan for the models
   * that carry no loader, which is nearly all of them.
   */
  private static CompletableFuture<Map<ResourceLocation,UnbakedModel>> prepare(ResourceManager manager, Executor executor) {
    return CompletableFuture.supplyAsync(() -> {
      Map<ResourceLocation,UnbakedModel> models = new ConcurrentHashMap<>();
      if (GeometryLoaderRegistry.isEmpty()) {
        return models;
      }
      ModelBakery.MODEL_LISTER.listMatchingResources(manager).entrySet().parallelStream().forEach(entry -> {
        // listMatchingResources keys by file path (namespace:models/foo/bar.json); models are addressed by id
        ResourceLocation id = ModelBakery.MODEL_LISTER.fileToId(entry.getKey());
        UnbakedModel model = load(id, entry.getValue());
        if (model != null) {
          models.put(id, model);
        }
      });
      return models;
    }, executor);
  }

  /** Reads one model file, returning the wrapper if it declares a registered loader. */
  @Nullable
  private static UnbakedModel load(ResourceLocation id, Resource resource) {
    try {
      String contents;
      try (BufferedReader reader = resource.openAsReader()) {
        contents = CharStreams.toString(reader);
      } catch (IOException e) {
        // vanilla reads the same file straight after and reports the failure properly; stay quiet
        return null;
      }
      if (!contents.contains(LOADER_KEY_HINT)) {
        return null;
      }
      JsonObject json = GsonHelper.parse(contents);
      String loaderKey = GsonHelper.getAsString(json, "loader", "");
      if (loaderKey.isEmpty()) {
        return null;
      }
      ResourceLocation loaderId = ResourceLocation.tryParse(loaderKey);
      if (loaderId == null) {
        Mantle.logger.error("Model {} declares malformed loader id '{}'", id, loaderKey);
        return null;
      }
      IGeometryLoader<?> loader = GeometryLoaderRegistry.get(loaderId);
      if (loader == null) {
        // not ours (or not ported yet): vanilla parses the file and ignores the key, as before
        return null;
      }
      BlockModel base = BlockModel.fromString(contents);
      base.name = id.toString();
      IUnbakedGeometry<?> geometry = loader.read(json, DESERIALIZATION_CONTEXT);
      return new GeometryUnbakedModel(base, geometry, id);
    } catch (RuntimeException e) {
      // one broken model must not take down the whole model load; fall back to the vanilla parse
      Mantle.logger.error("Failed to load custom geometry for model {}", id, e);
      return null;
    }
  }
}
