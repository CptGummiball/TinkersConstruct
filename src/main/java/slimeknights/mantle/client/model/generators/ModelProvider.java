package slimeknights.mantle.client.model.generators;

import com.google.common.base.Preconditions;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import slimeknights.mantle.data.ExistingFileHelper;
import slimeknights.mantle.data.ExistingFileHelper.ResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Base model provider, port of Forge's provider of the same name: a map of model builders
 * plus the vanilla template helpers. Paths without a folder separator are placed into this
 * provider's folder ({@code block} or {@code item}); paths containing one are used as-is.
 */
public abstract class ModelProvider<T extends ModelBuilder<T>> implements DataProvider {
  public static final String BLOCK_FOLDER = "block";
  public static final String ITEM_FOLDER = "item";

  protected static final ResourceType TEXTURE = new ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");
  protected static final ResourceType MODEL = new ResourceType(PackType.CLIENT_RESOURCES, ".json", "models");
  protected static final ResourceType MODEL_WITH_EXTENSION = new ResourceType(PackType.CLIENT_RESOURCES, "", "models");

  protected final PackOutput output;
  protected final String modid;
  protected final String folder;
  protected final Function<ResourceLocation, T> factory;
  protected final Map<ResourceLocation, T> generatedModels = new HashMap<>();
  protected final ExistingFileHelper existingFileHelper;

  public ModelProvider(PackOutput output, String modid, String folder, Function<ResourceLocation, T> factory, ExistingFileHelper existingFileHelper) {
    Preconditions.checkNotNull(output);
    this.output = output;
    Preconditions.checkNotNull(modid);
    this.modid = modid;
    Preconditions.checkNotNull(folder);
    this.folder = folder;
    Preconditions.checkNotNull(factory);
    this.factory = factory;
    Preconditions.checkNotNull(existingFileHelper);
    this.existingFileHelper = existingFileHelper;
  }

  public ModelProvider(PackOutput output, String modid, String folder, BiFunction<ResourceLocation, ExistingFileHelper, T> builderFromModId, ExistingFileHelper existingFileHelper) {
    this(output, modid, folder, loc -> builderFromModId.apply(loc, existingFileHelper), existingFileHelper);
  }

  /** Registers all models to the map, run before saving */
  protected abstract void registerModels();

  /** Gets a builder for the given path, creating if necessary. Model outputs go to {@code assets/<modid>/models/<path>} */
  public T getBuilder(String path) {
    Preconditions.checkNotNull(path, "Path must not be null");
    ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? ResourceLocation.parse(path) : ResourceLocation.fromNamespaceAndPath(modid, path));
    this.existingFileHelper.trackGenerated(outputLoc, MODEL);
    return generatedModels.computeIfAbsent(outputLoc, factory);
  }

  private ResourceLocation extendWithFolder(ResourceLocation rl) {
    if (rl.getPath().contains("/")) {
      return rl;
    }
    return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), folder + "/" + rl.getPath());
  }

  public ResourceLocation modLoc(String name) {
    return ResourceLocation.fromNamespaceAndPath(modid, name);
  }

  public ResourceLocation mcLoc(String name) {
    // parse: forge accepted embedded namespaces here, defaulting to minecraft
    return ResourceLocation.parse(name);
  }

  /** Gets an existing model file, validating it exists in the known packs */
  public ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
    ModelFile.ExistingModelFile ret = new ModelFile.ExistingModelFile(extendWithFolder(path), existingFileHelper);
    ret.assertExistence();
    return ret;
  }

  public T withExistingParent(String name, String parent) {
    return withExistingParent(name, mcLoc(parent));
  }

  public T withExistingParent(String name, ResourceLocation parent) {
    return getBuilder(name).parent(getExistingFile(parent));
  }

  public T cube(String name, ResourceLocation down, ResourceLocation up, ResourceLocation north, ResourceLocation south, ResourceLocation east, ResourceLocation west) {
    return withExistingParent(name, "cube")
      .texture("down", down)
      .texture("up", up)
      .texture("north", north)
      .texture("south", south)
      .texture("east", east)
      .texture("west", west);
  }

  private T singleTexture(String name, String parent, ResourceLocation texture) {
    return singleTexture(name, mcLoc(parent), texture);
  }

  public T singleTexture(String name, ResourceLocation parent, ResourceLocation texture) {
    return singleTexture(name, parent, "texture", texture);
  }

  private T singleTexture(String name, String parent, String textureKey, ResourceLocation texture) {
    return singleTexture(name, mcLoc(parent), textureKey, texture);
  }

  public T singleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture) {
    return withExistingParent(name, parent).texture(textureKey, texture);
  }

  public T cubeAll(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/cube_all", "all", texture);
  }

  public T cubeTop(String name, ResourceLocation side, ResourceLocation top) {
    return withExistingParent(name, BLOCK_FOLDER + "/cube_top")
      .texture("side", side)
      .texture("top", top);
  }

  private T sideBottomTop(String name, String parent, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    return withExistingParent(name, parent)
      .texture("side", side)
      .texture("bottom", bottom)
      .texture("top", top);
  }

  public T cubeBottomTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    return sideBottomTop(name, BLOCK_FOLDER + "/cube_bottom_top", side, bottom, top);
  }

  public T cubeColumn(String name, ResourceLocation side, ResourceLocation end) {
    return withExistingParent(name, BLOCK_FOLDER + "/cube_column")
      .texture("side", side)
      .texture("end", end);
  }

  public T cubeColumnHorizontal(String name, ResourceLocation side, ResourceLocation end) {
    return withExistingParent(name, BLOCK_FOLDER + "/cube_column_horizontal")
      .texture("side", side)
      .texture("end", end);
  }

  public T cross(String name, ResourceLocation cross) {
    return singleTexture(name, BLOCK_FOLDER + "/cross", "cross", cross);
  }

  public T stairs(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    return sideBottomTop(name, BLOCK_FOLDER + "/stairs", side, bottom, top);
  }

  public T stairsOuter(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    return sideBottomTop(name, BLOCK_FOLDER + "/outer_stairs", side, bottom, top);
  }

  public T stairsInner(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    return sideBottomTop(name, BLOCK_FOLDER + "/inner_stairs", side, bottom, top);
  }

  public T slab(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    return sideBottomTop(name, BLOCK_FOLDER + "/slab", side, bottom, top);
  }

  public T slabTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    return sideBottomTop(name, BLOCK_FOLDER + "/slab_top", side, bottom, top);
  }

  public T fencePost(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/fence_post", texture);
  }

  public T fenceSide(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/fence_side", texture);
  }

  public T fenceInventory(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/fence_inventory", texture);
  }

  public T fenceGate(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate", texture);
  }

  public T fenceGateOpen(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate_open", texture);
  }

  public T fenceGateWall(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate_wall", texture);
  }

  public T fenceGateWallOpen(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate_wall_open", texture);
  }

  public T button(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/button", texture);
  }

  public T buttonPressed(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/button_pressed", texture);
  }

  public T buttonInventory(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/button_inventory", texture);
  }

  public T pressurePlate(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/pressure_plate_up", texture);
  }

  public T pressurePlateDown(String name, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/pressure_plate_down", texture);
  }

  public T sign(String name, ResourceLocation texture) {
    return getBuilder(name).texture("particle", texture);
  }

  private T door(String name, String model, ResourceLocation bottom, ResourceLocation top) {
    return withExistingParent(name, BLOCK_FOLDER + "/" + model)
      .texture("bottom", bottom)
      .texture("top", top);
  }

  public T doorBottomLeft(String name, ResourceLocation bottom, ResourceLocation top) {
    return door(name, "door_bottom_left", bottom, top);
  }

  public T doorBottomLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
    return door(name, "door_bottom_left_open", bottom, top);
  }

  public T doorBottomRight(String name, ResourceLocation bottom, ResourceLocation top) {
    return door(name, "door_bottom_right", bottom, top);
  }

  public T doorBottomRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
    return door(name, "door_bottom_right_open", bottom, top);
  }

  public T doorTopLeft(String name, ResourceLocation bottom, ResourceLocation top) {
    return door(name, "door_top_left", bottom, top);
  }

  public T doorTopLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
    return door(name, "door_top_left_open", bottom, top);
  }

  public T doorTopRight(String name, ResourceLocation bottom, ResourceLocation top) {
    return door(name, "door_top_right", bottom, top);
  }

  public T doorTopRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
    return door(name, "door_top_right_open", bottom, top);
  }

  private T trapdoor(String name, String model, ResourceLocation texture) {
    return singleTexture(name, BLOCK_FOLDER + "/" + model, texture);
  }

  public T trapdoorBottom(String name, ResourceLocation texture) {
    return trapdoor(name, "template_trapdoor_bottom", texture);
  }

  public T trapdoorTop(String name, ResourceLocation texture) {
    return trapdoor(name, "template_trapdoor_top", texture);
  }

  public T trapdoorOpen(String name, ResourceLocation texture) {
    return trapdoor(name, "template_trapdoor_open", texture);
  }

  public T trapdoorOrientableBottom(String name, ResourceLocation texture) {
    return trapdoor(name, "template_orientable_trapdoor_bottom", texture);
  }

  public T trapdoorOrientableTop(String name, ResourceLocation texture) {
    return trapdoor(name, "template_orientable_trapdoor_top", texture);
  }

  public T trapdoorOrientableOpen(String name, ResourceLocation texture) {
    return trapdoor(name, "template_orientable_trapdoor_open", texture);
  }

  public T carpet(String name, ResourceLocation wool) {
    return singleTexture(name, BLOCK_FOLDER + "/carpet", "wool", wool);
  }

  /** {@return the model at the given location, generating if not yet present} */
  public ModelFile.ExistingModelFile getExistingFile(String path) {
    return getExistingFile(modLoc(path));
  }

  protected void clear() {
    generatedModels.clear();
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    clear();
    registerModels();
    return generateAll(cache);
  }

  protected CompletableFuture<?> generateAll(CachedOutput cache) {
    CompletableFuture<?>[] futures = new CompletableFuture<?>[this.generatedModels.size()];
    int i = 0;

    for (T model : this.generatedModels.values()) {
      futures[i++] = DataProvider.saveStable(cache, model.toJson(), getPath(model));
    }

    return CompletableFuture.allOf(futures);
  }

  private java.nio.file.Path getPath(T model) {
    ResourceLocation loc = model.getLocation();
    return this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                      .resolve(loc.getNamespace()).resolve("models").resolve(loc.getPath() + ".json");
  }

  @Override
  public String getName() {
    return folder + " models: " + modid;
  }
}
