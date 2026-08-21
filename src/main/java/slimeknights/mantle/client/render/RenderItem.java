package slimeknights.mantle.client.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Placement of one item displayed on a block, read from {@code mantle/model/item_lists}.
 *
 * <p>Written for this tree; Mantle's client packages were never copied in. JSON shape, matching the
 * data {@code RenderItemProvider} already generated:
 * <pre>
 * {"center": [8, 15.5, 8], "size": 14, "x": 270, "y": 180, "transform": "tconstruct:casting_table"}
 * </pre>
 *
 * <p>Fabric port: {@code transform} names a display context. Forge let mods add their own values to
 * {@link ItemDisplayContext} and TConstruct added six; Fabric has no such hook, so custom ids are
 * resolved through {@link #registerContext} to the vanilla context they fall back to (see
 * {@code TinkerItemDisplays}), and unknown ids fall back to {@link ItemDisplayContext#NONE}.
 */
public record RenderItem(Vector3f center, float size, float x, float y, @Nullable ResourceLocation transformName, boolean hidden) {
  /** Registry of item placements per block state */
  public static final BlockStateDataMap<List<RenderItem>> STATE_REGISTRY = new BlockStateDataMap<>(
    ResourceLocation.fromNamespaceAndPath("mantle", "item_lists"), "mantle/model/item_lists", RenderItem::listFromJson, RenderItem::listToJson);

  /** Display contexts registered by mods to stand in for Forge's custom transform types */
  private static final Map<ResourceLocation,ItemDisplayContext> CUSTOM_CONTEXTS = new HashMap<>();

  /**
   * Maps a custom display context id onto the vanilla context it behaves as.
   * @param id        Id used in the {@code transform} key of the JSON
   * @param fallback  Vanilla context to render with
   */
  public static void registerContext(ResourceLocation id, ItemDisplayContext fallback) {
    CUSTOM_CONTEXTS.put(id, fallback);
  }

  /** If true, this item is not drawn */
  public boolean isHidden() {
    return hidden || size <= 0;
  }

  /** Resolves the display context named by {@link #transformName}, for the renderer */
  public ItemDisplayContext transform() {
    if (transformName == null) {
      return ItemDisplayContext.NONE;
    }
    ItemDisplayContext custom = CUSTOM_CONTEXTS.get(transformName);
    if (custom != null) {
      return custom;
    }
    if ("minecraft".equals(transformName.getNamespace())) {
      for (ItemDisplayContext context : ItemDisplayContext.values()) {
        if (context.getSerializedName().equals(transformName.getPath())) {
          return context;
        }
      }
    }
    return ItemDisplayContext.NONE;
  }


  /* JSON */

  /** Parses either a single item or a list of them */
  public static List<RenderItem> listFromJson(JsonElement element) {
    if (element.isJsonArray()) {
      List<RenderItem> list = new ArrayList<>();
      for (JsonElement entry : element.getAsJsonArray()) {
        list.add(fromJson(GsonHelper.convertToJsonObject(entry, "item")));
      }
      return List.copyOf(list);
    }
    return List.of(fromJson(GsonHelper.convertToJsonObject(element, "item")));
  }

  /** Parses a single item placement */
  public static RenderItem fromJson(JsonObject json) {
    Vector3f center = new Vector3f(8, 8, 8);
    if (json.has("center")) {
      var array = GsonHelper.getAsJsonArray(json, "center");
      if (array.size() != 3) {
        throw new com.google.gson.JsonSyntaxException("Expected 3 values for center, got " + array.size());
      }
      center.set(GsonHelper.convertToFloat(array.get(0), "center"),
                 GsonHelper.convertToFloat(array.get(1), "center"),
                 GsonHelper.convertToFloat(array.get(2), "center"));
    }
    return new RenderItem(
      center,
      GsonHelper.getAsFloat(json, "size", 16),
      GsonHelper.getAsFloat(json, "x", 0),
      GsonHelper.getAsFloat(json, "y", 0),
      json.has("transform") ? ResourceLocation.parse(GsonHelper.getAsString(json, "transform")) : null,
      GsonHelper.getAsBoolean(json, "hidden", false));
  }

  /** Serializes a list of items compactly: one item stays a single object */
  public static JsonElement listToJson(List<RenderItem> items) {
    if (items.size() == 1) {
      return items.get(0).toJson();
    }
    com.google.gson.JsonArray array = new com.google.gson.JsonArray();
    for (RenderItem item : items) {
      array.add(item.toJson());
    }
    return array;
  }

  /** Serializes this item to the shape {@link #fromJson} reads */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    com.google.gson.JsonArray centerArray = new com.google.gson.JsonArray();
    centerArray.add(center.x());
    centerArray.add(center.y());
    centerArray.add(center.z());
    json.add("center", centerArray);
    json.addProperty("size", size);
    if (x != 0) {
      json.addProperty("x", (int)x == x ? (Number)(int)x : (Number)x);
    }
    if (y != 0) {
      json.addProperty("y", (int)y == y ? (Number)(int)y : (Number)y);
    }
    if (transformName != null) {
      json.addProperty("transform", transformName.toString());
    }
    if (hidden) {
      json.addProperty("hidden", true);
    }
    return json;
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Reusable builder; {@link #build()} snapshots the current values */
  public static class Builder {
    private final Vector3f center = new Vector3f(8, 8, 8);
    private float size = 16;
    private float x = 0;
    private float y = 0;
    @Nullable
    private ResourceLocation transform = null;
    private boolean hidden = false;

    private Builder() {}

    public Builder center(float x, float y, float z) {
      center.set(x, y, z);
      return this;
    }

    public Builder size(float size) {
      this.size = size;
      return this;
    }

    public Builder x(float x) {
      this.x = x;
      return this;
    }

    public Builder y(float y) {
      this.y = y;
      return this;
    }

    public Builder transform(ResourceLocation transform) {
      this.transform = transform;
      return this;
    }

    public Builder hidden() {
      this.hidden = true;
      return this;
    }

    public RenderItem build() {
      return new RenderItem(new Vector3f(center), size, x, y, transform, hidden);
    }
  }

  @Override
  public String toString() {
    return String.format(Locale.ROOT, "RenderItem[center=%s,size=%s,transform=%s]", center, size, transformName);
  }
}
