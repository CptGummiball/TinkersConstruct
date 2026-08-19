package slimeknights.mantle.client.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

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
public record RenderItem(Vector3f center, float size, float x, float y, ItemDisplayContext transform, boolean hidden) {
  /** Registry of item placements per block state */
  public static final BlockStateDataMap<List<RenderItem>> STATE_REGISTRY = new BlockStateDataMap<>(
    ResourceLocation.fromNamespaceAndPath("mantle", "item_lists"), "mantle/model/item_lists", RenderItem::listFromJson);

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
      transformFromJson(json),
      GsonHelper.getAsBoolean(json, "hidden", false));
  }

  /** Resolves the display context named by the JSON */
  private static ItemDisplayContext transformFromJson(JsonObject json) {
    if (!json.has("transform")) {
      return ItemDisplayContext.NONE;
    }
    ResourceLocation id = ResourceLocation.parse(GsonHelper.getAsString(json, "transform"));
    ItemDisplayContext custom = CUSTOM_CONTEXTS.get(id);
    if (custom != null) {
      return custom;
    }
    if ("minecraft".equals(id.getNamespace())) {
      for (ItemDisplayContext context : ItemDisplayContext.values()) {
        if (context.getSerializedName().equals(id.getPath())) {
          return context;
        }
      }
    }
    return ItemDisplayContext.NONE;
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
    private ItemDisplayContext transform = ItemDisplayContext.NONE;
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

    public Builder transform(ItemDisplayContext transform) {
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
    return String.format(Locale.ROOT, "RenderItem[center=%s,size=%s,transform=%s]", center, size, transform);
  }
}
