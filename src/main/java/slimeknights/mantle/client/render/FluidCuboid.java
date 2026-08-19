package slimeknights.mantle.client.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A single box of fluid inside a block, with a per face flowing flag and rotation.
 *
 * <p>Written for this tree; Mantle's client packages were never copied in. The JSON shape matches
 * the data the datagen providers already generated, see {@link BlockStateDataMap}:
 * <pre>
 * {"from": [1, 15, 1], "to": [15, 15.9, 15], "faces": {"up": {}, "north": {"flowing": true, "rotation": 180}}}
 * </pre>
 * Coordinates are in sixteenths of a block, as in a model JSON. Omitting {@code faces} draws all six.
 */
public class FluidCuboid {
  /** Registry of fluid cuboids per block state, filled from {@code mantle/model/block_fluids} */
  public static final BlockStateDataMap<List<FluidCuboid>> REGISTRY = new BlockStateDataMap<>(
    ResourceLocation.fromNamespaceAndPath("mantle", "block_fluids"), "mantle/model/block_fluids", FluidCuboid::listFromJson);

  /** Face shown with no rotation using the still texture */
  public static final FluidFace NORMAL = new FluidFace(false, 0);

  private final Vector3f from;
  private final Vector3f to;
  private final Map<Direction,FluidFace> faces;
  /** Cached block scale copies of {@link #from} and {@link #to} */
  private Vector3f fromScaled;
  private Vector3f toScaled;

  public FluidCuboid(Vector3f from, Vector3f to, Map<Direction,FluidFace> faces) {
    this.from = from;
    this.to = to;
    this.faces = faces;
  }

  /** Gets the minimum position in sixteenths */
  public Vector3f getFrom() {
    return from;
  }

  /** Gets the maximum position in sixteenths */
  public Vector3f getTo() {
    return to;
  }

  /** Gets the minimum position scaled to block coordinates */
  public Vector3f getFromScaled() {
    if (fromScaled == null) {
      fromScaled = new Vector3f(from).div(16);
    }
    return fromScaled;
  }

  /** Gets the maximum position scaled to block coordinates */
  public Vector3f getToScaled() {
    if (toScaled == null) {
      toScaled = new Vector3f(to).div(16);
    }
    return toScaled;
  }

  /** Gets the face data for the given side, or null if that side is not drawn */
  @Nullable
  public FluidFace getFace(Direction direction) {
    return faces.get(direction);
  }

  /** If true, this cuboid draws nothing */
  public boolean isEmpty() {
    return faces.isEmpty();
  }

  /** Data for a single face of the cuboid */
  public record FluidFace(boolean flowing, int rotation) {}


  /* JSON */

  /** Parses either a single cuboid or a list of them */
  public static List<FluidCuboid> listFromJson(JsonElement element) {
    if (element.isJsonArray()) {
      List<FluidCuboid> list = new java.util.ArrayList<>();
      for (JsonElement entry : element.getAsJsonArray()) {
        list.add(fromJson(GsonHelper.convertToJsonObject(entry, "fluid")));
      }
      return List.copyOf(list);
    }
    return List.of(fromJson(GsonHelper.convertToJsonObject(element, "fluid")));
  }

  /** Parses a single cuboid */
  public static FluidCuboid fromJson(JsonObject json) {
    Vector3f from = vectorFromJson(json, "from");
    Vector3f to = vectorFromJson(json, "to");
    return new FluidCuboid(from, to, facesFromJson(json));
  }

  /** Reads the faces object, defaulting to all six sides */
  private static Map<Direction,FluidFace> facesFromJson(JsonObject json) {
    Map<Direction,FluidFace> faces = new EnumMap<>(Direction.class);
    if (json.has("faces")) {
      for (Entry<String,JsonElement> entry : GsonHelper.getAsJsonObject(json, "faces").entrySet()) {
        Direction direction = Direction.byName(entry.getKey());
        if (direction == null) {
          throw new com.google.gson.JsonSyntaxException("Unknown face " + entry.getKey());
        }
        JsonObject face = GsonHelper.convertToJsonObject(entry.getValue(), entry.getKey());
        faces.put(direction, new FluidFace(GsonHelper.getAsBoolean(face, "flowing", false), GsonHelper.getAsInt(face, "rotation", 0)));
      }
    } else {
      for (Direction direction : Direction.values()) {
        faces.put(direction, NORMAL);
      }
    }
    return faces;
  }

  /** Reads a three float array */
  private static Vector3f vectorFromJson(JsonObject json, String key) {
    var array = GsonHelper.getAsJsonArray(json, key);
    if (array.size() != 3) {
      throw new com.google.gson.JsonSyntaxException("Expected 3 values for " + key + ", got " + array.size());
    }
    return new Vector3f(
      GsonHelper.convertToFloat(array.get(0), key),
      GsonHelper.convertToFloat(array.get(1), key),
      GsonHelper.convertToFloat(array.get(2), key));
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder for code defined cuboids, used by the datagen providers and the fluid projectile renderer */
  public static class Builder {
    private final Vector3f from = new Vector3f();
    private final Vector3f to = new Vector3f(16, 16, 16);
    private final Map<Direction,FluidFace> faces = new EnumMap<>(Direction.class);

    private Builder() {}

    public Builder from(float x, float y, float z) {
      from.set(x, y, z);
      return this;
    }

    public Builder to(float x, float y, float z) {
      to.set(x, y, z);
      return this;
    }

    /** Adds still faces on the given sides */
    public Builder face(Direction... directions) {
      return face(false, 0, directions);
    }

    /** Adds still faces on the given sides */
    public Builder face(Direction first, Direction... directions) {
      face(false, 0, first);
      return face(false, 0, directions);
    }

    /** Adds faces on the given sides with the given flowing state and rotation */
    public Builder face(boolean flowing, int rotation, Direction... directions) {
      FluidFace face = new FluidFace(flowing, rotation);
      for (Direction direction : directions) {
        faces.put(direction, face);
      }
      return this;
    }

    /** Builds the cuboid, defaulting to all six faces if none were set */
    public FluidCuboid build() {
      Map<Direction,FluidFace> faces = new EnumMap<>(Direction.class);
      if (this.faces.isEmpty()) {
        for (Direction direction : Direction.values()) {
          faces.put(direction, NORMAL);
        }
      } else {
        faces.putAll(this.faces);
      }
      return new FluidCuboid(new Vector3f(from), new Vector3f(to), faces);
    }
  }

  @Override
  public String toString() {
    return String.format(Locale.ROOT, "FluidCuboid[from=%s,to=%s,faces=%s]", from, to, faces.keySet());
  }
}
