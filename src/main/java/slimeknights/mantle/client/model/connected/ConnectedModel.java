package slimeknights.mantle.client.model.connected;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.joml.Matrix4f;
import slimeknights.mantle.block.IMultipartConnectedBlock;
import slimeknights.mantle.client.model.data.ModelData;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;
import slimeknights.mantle.client.model.geometry.IGeometryLoader;
import slimeknights.mantle.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.DynamicBakedWrapper;
import slimeknights.mantle.client.model.util.ExtraTextureContext;
import slimeknights.mantle.client.model.util.ModelTextureIteratable;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Model whose textures swap to a suffixed variant depending on which neighbours connect —
 * borderless glass being the visible case.
 *
 * <p>The base model bakes normally; the 63 other connection states bake lazily from the same
 * elements with the suffixed texture substituted. On Forge the connection bits travelled through
 * {@code getModelData}; Fabric hands the world straight to {@link Baked#emitBlockQuads}, so the
 * bits are computed right there. The blockstate-property fallback below it serves whatever still
 * asks through plain {@code getQuads}.
 */
public class ConnectedModel implements IUnbakedGeometry<ConnectedModel> {
  /** Loader for the {@code mantle:connected} id */
  public static final IGeometryLoader<ConnectedModel> LOADER = ConnectedModel::deserialize;

  /** Parent model, carries the elements and any per-element colours */
  private final ColoredBlockModel model;
  /** Map of texture name to the suffix set (indexed as 0bENWS by {@link ConnectedModelRegistry#registerType}) */
  private final Map<String,String[]> connectedTextures;
  /** Function to run to check if this block connects to another */
  private final BiPredicate<BlockState,BlockState> connectionPredicate;
  /** List of sides to check when getting block directions */
  private final Set<Direction> sides;

  /** Map of full texture name to the resulting material, filled during {@link #resolveParents} */
  private Map<String,Material> extraTextures = Map.of();

  protected ConnectedModel(ColoredBlockModel model, Map<String,String[]> connectedTextures, BiPredicate<BlockState,BlockState> connectionPredicate, Set<Direction> sides) {
    this.model = model;
    this.connectedTextures = connectedTextures;
    this.connectionPredicate = connectionPredicate;
    this.sides = sides;
  }

  @Override
  public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter, IGeometryBakingContext owner) {
    model.resolveParents(modelGetter, owner);
    // for all connected textures, add suffix textures
    Map<String,Material> extraTextures = new HashMap<>();
    for (Entry<String,String[]> entry : connectedTextures.entrySet()) {
      String name = entry.getKey();
      // skip if missing
      if (!owner.hasMaterial(name)) {
        continue;
      }
      Material base = owner.getMaterial(name);
      ResourceLocation atlas = base.atlasLocation();
      ResourceLocation texture = base.texture();
      String namespace = texture.getNamespace();
      String path = texture.getPath();

      // use base atlas and texture, but suffix the name
      for (String suffix : entry.getValue()) {
        if (suffix.isEmpty()) {
          continue;
        }
        String suffixedName = name + "_" + suffix;
        if (!extraTextures.containsKey(suffixedName)) {
          Material mat;
          // allow overriding a specific texture
          if (owner.hasMaterial(suffixedName)) {
            mat = owner.getMaterial(suffixedName);
          } else {
            mat = new Material(atlas, ResourceLocation.fromNamespaceAndPath(namespace, path + "/" + suffix));
          }
          extraTextures.put(suffixedName, mat);
        }
      }
    }
    this.extraTextures = Map.copyOf(extraTextures);
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    BakedModel baked = model.bake(owner, baker, spriteGetter, transform, overrides, location);
    return new Baked(this, new ExtraTextureContext(owner, extraTextures), transform, baked);
  }

  @SuppressWarnings("WeakerAccess")
  protected static class Baked extends DynamicBakedWrapper<BakedModel> {
    private final ConnectedModel parent;
    private final IGeometryBakingContext owner;
    private final ModelState transforms;
    private final Matrix4f rotationMatrix;
    /** Lazily baked variant per connection byte; entry 0 is the base bake */
    private final AtomicReferenceArray<BakedModel> cache = new AtomicReferenceArray<>(64);
    private final Map<String,String> nameMappingCache = new ConcurrentHashMap<>();
    private final ModelTextureIteratable modelTextures;

    public Baked(ConnectedModel parent, IGeometryBakingContext owner, ModelState transforms, BakedModel baked) {
      super(baked);
      this.parent = parent;
      this.owner = owner;
      this.transforms = transforms;
      this.rotationMatrix = transforms.getRotation().getMatrix();
      this.modelTextures = ModelTextureIteratable.of(owner, parent.model);
      // all directions false gives cache key of 0, that is ourself
      this.cache.set(0, baked);
    }

    /**
     * Gets the direction rotated
     * @param direction  Original direction to rotate
     * @param rotation   Rotation origin, aka the face of the block we are looking at. As a result, UP is identity
     * @return  Rotated direction
     */
    private static Direction rotateDirection(Direction direction, Direction rotation) {
      if (rotation == Direction.UP) {
        return direction;
      }
      if (rotation == Direction.DOWN) {
        // Z is backwards on the bottom
        if (direction.getAxis() == Axis.Z) {
          return direction.getOpposite();
        }
        // X is normal
        return direction;
      }
      // sides all just have the next side for left and right, and consistent up and down
      return switch (direction) {
        case NORTH -> Direction.UP;
        case SOUTH -> Direction.DOWN;
        case EAST -> rotation.getCounterClockWise();
        case WEST -> rotation.getClockWise();
        default -> throw new IllegalArgumentException("Direction must be horizontal axis");
      };
    }

    /**
     * Gets a transform function based on the block part UV and block face
     * @param face   Block face in question
     * @param uv     Block UV data
     * @return  Direction transform function
     */
    private static Function<Direction,Direction> getTransform(Direction face, BlockFaceUV uv) {
      // final transform switches from face (NSWE) to world direction, the rest are composed in to apply first
      Function<Direction,Direction> transform = d -> rotateDirection(d, face);

      // flipping
      boolean flipV = uv.uvs[1] > uv.uvs[3];
      if (uv.uvs[0] > uv.uvs[2]) {
        // flip both
        if (flipV) {
          transform = transform.compose(Direction::getOpposite);
        } else {
          // flip U
          transform = transform.compose(d -> d.getAxis() == Axis.X ? d.getOpposite() : d);
        }
      } else if (flipV) {
        transform = transform.compose(d -> d.getAxis() == Axis.Z ? d.getOpposite() : d);
      }

      // rotation
      return switch (uv.rotation) {
        case 90 -> transform.compose(Direction::getClockWise);
        case 180 -> transform.compose(Direction::getOpposite);
        case 270 -> transform.compose(Direction::getCounterClockWise);
        default -> transform;
      };
    }

    /** Uncached variant of {@link #getConnectedName(String)}, used internally */
    private String getConnectedNameUncached(String key) {
      // iterate into the parent models, trying to find a match
      String check = key;
      String found = "";
      for (Map<String,Either<Material,String>> textures : modelTextures) {
        Either<Material,String> either = textures.get(check);
        if (either != null) {
          // if no name, its not connected
          Optional<String> newName = either.right();
          if (newName.isEmpty()) {
            break;
          }
          // if the name is connected, we are done
          check = newName.get();
          if (parent.connectedTextures.containsKey(check)) {
            found = check;
            break;
          }
        }
      }
      return found;
    }

    /**
     * Gets the name of this texture that supports connected textures, or empty if never connected
     * @param key  Name of the part texture
     * @return  Name of the connected texture
     */
    private String getConnectedName(String key) {
      if (key.charAt(0) == '#') {
        key = key.substring(1);
      }
      // if the name is connected, we are done
      if (parent.connectedTextures.containsKey(key)) {
        return key;
      }
      return nameMappingCache.computeIfAbsent(key, this::getConnectedNameUncached);
    }

    /**
     * Gets the texture suffix
     * @param texture      Texture name, must be a connected texture
     * @param connections  Connections byte
     * @param transform    Rotations to apply to faces
     * @return  Suffix to use, with a leading underscore, or empty
     */
    private String getTextureSuffix(String texture, byte connections, Function<Direction,Direction> transform) {
      int key = 0;
      for (Direction dir : Plane.HORIZONTAL) {
        int flag = 1 << transform.apply(dir).get3DDataValue();
        if ((connections & flag) == flag) {
          key |= 1 << dir.get2DDataValue();
        }
      }
      // if empty, do not prefix
      String[] suffixes = parent.connectedTextures.get(texture);
      assert suffixes != null;
      String suffix = suffixes[key];
      if (suffix.isEmpty()) {
        return suffix;
      }
      return "_" + suffix;
    }

    /**
     * Bakes the variant of this model for the given connections
     * @param connections  Byte with each bit at a direction's 3D data value marking a connected side
     * @return  Model with connections applied
     */
    private BakedModel applyConnections(byte connections) {
      // copy each element with updated faces
      List<BlockElement> elements = new java.util.ArrayList<>();
      for (BlockElement part : parent.model.getElements()) {
        Map<Direction,BlockElementFace> partFaces = new EnumMap<>(Direction.class);
        for (Entry<Direction,BlockElementFace> entry : part.faces.entrySet()) {
          // first, determine which texture to use on this side
          Direction dir = entry.getKey();
          BlockElementFace original = entry.getValue();
          BlockElementFace face = original;

          // follow the texture name back to the original name
          // if it never reaches a connected texture, skip
          String connectedTexture = getConnectedName(original.texture());
          if (!connectedTexture.isEmpty()) {
            // if empty string, we can keep the old face
            String suffix = getTextureSuffix(connectedTexture, connections, getTransform(dir, original.uv()));
            if (!suffix.isEmpty()) {
              // suffix the texture
              face = new BlockElementFace(original.cullForDirection(), original.tintIndex(), "#" + connectedTexture + suffix, original.uv());
            }
          }
          partFaces.put(dir, face);
        }
        // add the updated parts into a new model part
        elements.add(new BlockElement(part.from, part.to, partFaces, part.rotation, part.shade));
      }

      // bake the model, keeping any per-element colours the parent had
      return parent.model.bakeWithElements(owner, elements, transforms);
    }

    /** Gets the model variant for the given connections, baking it on first request */
    private BakedModel getCachedModel(byte connections) {
      int key = connections & 0x3F;
      BakedModel model = cache.get(key);
      if (model == null) {
        // two threads may bake the same variant concurrently; both results are identical, first one wins
        model = applyConnections((byte) key);
        if (!cache.compareAndSet(key, null, model)) {
          model = cache.get(key);
        }
      }
      return model;
    }

    /**
     * Packs the connected sides into a byte, one bit per direction's 3D data value
     * @param predicate  Returns true if the block is connected on the given side
     */
    private static byte getConnections(Predicate<Direction> predicate) {
      byte connections = 0;
      for (Direction dir : Direction.values()) {
        if (predicate.test(dir)) {
          connections |= (byte) (1 << dir.get3DDataValue());
        }
      }
      return connections;
    }

    /** The world path: chunk rebuilds land here with the level in hand, so connections come straight from the neighbours. */
    @Override
    public void emitBlockQuads(BlockAndTintGetter view, BlockState state, BlockPos pos, Supplier<RandomSource> random, RenderContext context) {
      byte connections = getConnections(dir -> parent.sides.contains(dir)
        && parent.connectionPredicate.test(state, view.getBlockState(pos.relative(Direction.rotate(rotationMatrix, dir)))));
      context.bakedModelConsumer().accept(getCachedModel(connections), state);
    }

    /**
     * The stateless path: item rendering (null state, base model) and any caller without a world,
     * which reads the connections off {@link IMultipartConnectedBlock}'s blockstate properties when
     * the block carries them.
     */
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
      if (state == null) {
        return originalModel.getQuads(null, side, rand);
      }
      // this returns the original if the state is missing all properties
      byte connections = getConnections(dir -> {
        if (!parent.sides.contains(dir)) {
          return false;
        }
        BooleanProperty prop = IMultipartConnectedBlock.CONNECTED_DIRECTIONS.get(Direction.rotate(rotationMatrix, dir));
        return state.hasProperty(prop) && state.getValue(prop);
      });
      return getCachedModel(connections).getQuads(state, side, rand);
    }
  }

  /** Deserializes the geometry from the {@code connection} block of the model JSON */
  public static ConnectedModel deserialize(JsonObject json, JsonDeserializationContext context) {
    ColoredBlockModel model = ColoredBlockModel.deserialize(json, context);

    // root object for all model data
    JsonObject data = GsonHelper.getAsJsonObject(json, "connection");

    // need at least one connected texture
    JsonObject connected = GsonHelper.getAsJsonObject(data, "textures");
    if (connected.size() == 0) {
      throw new JsonSyntaxException("Must have at least one texture in connected");
    }

    // build texture list
    Map<String,String[]> connectedTextures = new HashMap<>(connected.size());
    for (Entry<String,JsonElement> entry : connected.entrySet()) {
      // don't validate texture as it may be contained in a child model that is not yet loaded
      String name = entry.getKey();
      connectedTextures.put(name, ConnectedModelRegistry.deserializeType(entry.getValue(), "textures[" + name + "]"));
    }

    // get a list of sides to pay attention to
    Set<Direction> sides;
    if (data.has("sides")) {
      JsonArray array = GsonHelper.getAsJsonArray(data, "sides");
      sides = EnumSet.noneOf(Direction.class);
      for (int i = 0; i < array.size(); i++) {
        String side = GsonHelper.convertToString(array.get(i), "sides[" + i + "]");
        Direction dir = Direction.byName(side);
        if (dir == null) {
          throw new JsonParseException("Invalid side " + side);
        }
        sides.add(dir);
      }
    } else {
      sides = EnumSet.allOf(Direction.class);
    }

    // other data
    BiPredicate<BlockState,BlockState> predicate = ConnectedModelRegistry.deserializePredicate(data, "predicate");

    // final model instance
    return new ConnectedModel(model, Map.copyOf(connectedTextures), predicate, sides);
  }
}
