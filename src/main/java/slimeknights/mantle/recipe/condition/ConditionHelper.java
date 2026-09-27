package slimeknights.mantle.recipe.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Fabric replacement for the parts of Forge's {@code CraftingHelper} that deal with recipe
 * conditions.
 *
 * <p>Type names keep the {@code forge:} namespace because that is what the existing data
 * files contain; see {@link ICondition} for why they are not migrated. Unknown condition
 * types fail loudly rather than defaulting to "load it anyway" — silently loading a recipe
 * whose guard could not be evaluated is how packs end up with duplicate or impossible
 * recipes.
 */
public final class ConditionHelper {

  private ConditionHelper() {}

  private static final Map<ResourceLocation, Function<JsonObject, ICondition>> TYPES = new HashMap<>();
  /** Writers for conditions carrying data, keyed by condition ID; consulted by {@link #serialize(ICondition)} */
  private static final Map<ResourceLocation, IConditionSerializer<?>> WRITERS = new HashMap<>();

  private static ResourceLocation forge(String path) {
    return ResourceLocation.fromNamespaceAndPath("forge", path);
  }

  static {
    register(forge("true"), json -> new Constant(forge("true"), true));
    register(forge("false"), json -> new Constant(forge("false"), false));
    register(forge("not"), json -> new Not(deserialize(GsonHelper.getAsJsonObject(json, "value"))));
    register(forge("and"), json -> new Junction(forge("and"), children(json), true));
    register(forge("or"), json -> new Junction(forge("or"), children(json), false));
    register(forge("mod_loaded"), json -> new ModLoaded(GsonHelper.getAsString(json, "modid")));
    register(forge("item_exists"), json -> new ItemExists(
      ResourceLocation.parse(GsonHelper.getAsString(json, "item"))));
  }

  /** Adds a condition type; used by plugins that ship their own conditions. */
  public static void register(ResourceLocation id, Function<JsonObject, ICondition> factory) {
    TYPES.put(id, factory);
  }

  /** Adds a condition type with both directions, so datagen can write it back out. */
  public static void register(IConditionSerializer<?> serializer) {
    TYPES.put(serializer.getID(), serializer::read);
    WRITERS.put(serializer.getID(), serializer);
  }


  /* Datagen factories; the condition records themselves stay private */

  /** Condition that always passes */
  public static ICondition trueCondition() {
    return new Constant(forge("true"), true);
  }

  /** Condition that never passes */
  public static ICondition falseCondition() {
    return new Constant(forge("false"), false);
  }

  /** Inverts the given condition */
  public static ICondition not(ICondition value) {
    return new Not(value);
  }

  /** Passes when all children pass */
  public static ICondition and(ICondition... values) {
    return new Junction(forge("and"), List.of(values), true);
  }

  /** Passes when any child passes */
  public static ICondition or(ICondition... values) {
    return new Junction(forge("or"), List.of(values), false);
  }

  /** Passes when the given mod is loaded */
  public static ICondition modLoaded(String modid) {
    return new ModLoaded(modid);
  }

  /** Passes when the given item is registered */
  public static ICondition itemExists(ResourceLocation item) {
    return new ItemExists(item);
  }

  /** Passes when the given item is registered */
  public static ICondition itemExists(String namespace, String path) {
    return new ItemExists(ResourceLocation.fromNamespaceAndPath(namespace, path));
  }

  private static List<ICondition> children(JsonObject json) {
    List<ICondition> list = new ArrayList<>();
    for (JsonElement element : GsonHelper.getAsJsonArray(json, "values")) {
      list.add(deserialize(GsonHelper.convertToJsonObject(element, "value")));
    }
    return list;
  }

  /** Alias matching Forge's {@code ConditionHelper.getCondition} name. */
  public static ICondition getCondition(JsonObject json) {
    return deserialize(json);
  }

  public static ICondition deserialize(JsonObject json) {
    ResourceLocation type = ResourceLocation.parse(GsonHelper.getAsString(json, "type"));
    Function<JsonObject, ICondition> factory = TYPES.get(type);
    if (factory == null) {
      throw new JsonSyntaxException("Unknown condition type " + type);
    }
    return factory.apply(json);
  }

  /**
   * Evaluates the condition array under {@code key}, if present.
   *
   * @return true when the guarded object should load — including when it carries no conditions
   */
  public static boolean processConditions(JsonObject json, String key, ICondition.IContext context) {
    if (!json.has(key)) {
      return true;
    }
    return processConditions(GsonHelper.getAsJsonArray(json, key), context);
  }

  public static boolean processConditions(JsonArray array, ICondition.IContext context) {
    for (JsonElement element : array) {
      if (!deserialize(GsonHelper.convertToJsonObject(element, "condition")).test(context)) {
        return false;
      }
    }
    return true;
  }

  public static JsonArray serialize(ICondition[] conditions) {
    JsonArray array = new JsonArray();
    for (ICondition condition : conditions) {
      array.add(serialize(condition));
    }
    return array;
  }

  public static JsonObject serialize(ICondition condition) {
    JsonObject json = new JsonObject();
    json.addProperty("type", condition.getID().toString());
    IConditionSerializer<?> writer = WRITERS.get(condition.getID());
    if (writer != null) {
      writeUnchecked(writer, json, condition);
    } else if (condition instanceof Writable writable) {
      writable.write(json);
    }
    return json;
  }

  @SuppressWarnings("unchecked")
  private static <C extends ICondition> void writeUnchecked(IConditionSerializer<C> writer, JsonObject json, ICondition condition) {
    writer.write(json, (C)condition);
  }

  /** Conditions that carry data need to write it back out for datagen. */
  public interface Writable {
    void write(JsonObject json);
  }

  private record Constant(ResourceLocation getID, boolean value) implements ICondition {
    @Override
    public boolean test(IContext context) {
      return value;
    }
  }

  private record Not(ICondition child) implements ICondition, Writable {
    @Override
    public ResourceLocation getID() {
      return ResourceLocation.fromNamespaceAndPath("forge", "not");
    }

    @Override
    public boolean test(IContext context) {
      return !child.test(context);
    }

    @Override
    public void write(JsonObject json) {
      json.add("value", serialize(child));
    }
  }

  /** {@code and} when {@code requireAll}, otherwise {@code or}. */
  private record Junction(ResourceLocation getID, List<ICondition> children, boolean requireAll)
    implements ICondition, Writable {

    @Override
    public boolean test(IContext context) {
      for (ICondition child : children) {
        if (child.test(context) != requireAll) {
          return !requireAll;
        }
      }
      return requireAll;
    }

    @Override
    public void write(JsonObject json) {
      JsonArray values = new JsonArray();
      for (ICondition child : children) {
        values.add(serialize(child));
      }
      json.add("values", values);
    }
  }

  private record ModLoaded(String modid) implements ICondition, Writable {
    @Override
    public ResourceLocation getID() {
      return ResourceLocation.fromNamespaceAndPath("forge", "mod_loaded");
    }

    @Override
    public boolean test(IContext context) {
      return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public void write(JsonObject json) {
      json.addProperty("modid", modid);
    }
  }

  private record ItemExists(ResourceLocation item) implements ICondition, Writable {
    @Override
    public ResourceLocation getID() {
      return ResourceLocation.fromNamespaceAndPath("forge", "item_exists");
    }

    @Override
    public boolean test(IContext context) {
      return BuiltInRegistries.ITEM.containsKey(item);
    }

    @Override
    public void write(JsonObject json) {
      json.addProperty("item", item.toString());
    }
  }
}
