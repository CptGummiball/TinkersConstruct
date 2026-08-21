package slimeknights.mantle.recipe.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * Datagen ingredient referencing items by name, for writing compat recipes whose items come
 * from mods not present at datagen time (guarded by mod-loaded conditions in the data).
 * Should never be used in an actual recipe.
 *
 * <p>1.21 rework: vanilla ingredients now hold registry holders, so an absent item cannot
 * become a real {@link Ingredient} — and it must not serialize through the vanilla codec
 * either, which would fail on the lookup. This is a Fabric {@link CustomIngredient} purely
 * so {@link #from} can return the {@link Ingredient} type builders expect;
 * {@code IngredientLoadable} recognizes the marker and writes the raw
 * {@code {"item": "mod:name"}} JSON the load-time parser expects, so the fabric
 * serialization path is never reached.
 */
public class ItemNameIngredient implements CustomIngredient {
  private final List<Entry> entries;

  private ItemNameIngredient(List<Entry> entries) {
    this.entries = entries;
  }

  /** Creates an ingredient from the given list of names */
  public static Ingredient from(List<ResourceLocation> names) {
    return new ItemNameIngredient(names.stream().<Entry>map(ItemEntry::new).toList()).toVanilla();
  }

  /** Creates an ingredient from the given list of names */
  public static Ingredient from(ResourceLocation... names) {
    return from(List.of(names));
  }

  /** Creates an ingredient from explicit entries, for mixing tags with item names */
  public static Ingredient ofEntries(Entry... entries) {
    return new ItemNameIngredient(List.of(entries)).toVanilla();
  }

  /** Serializes this to the JSON form the runtime parses after conditions pass */
  public JsonElement serialize() {
    if (entries.size() == 1) {
      return entries.get(0).serialize();
    }
    JsonArray array = new JsonArray();
    for (Entry entry : entries) {
      array.add(entry.serialize());
    }
    return array;
  }

  /** Single value of the written ingredient array */
  public sealed interface Entry {
    /** Entry naming an item that may not exist at datagen */
    static Entry item(ResourceLocation name) {
      return new ItemEntry(name);
    }

    /** Entry for a tag, allowed to mix with name entries in one plain array */
    static Entry tag(ResourceLocation tag) {
      return new TagEntry(tag);
    }

    JsonObject serialize();
  }

  private record ItemEntry(ResourceLocation name) implements Entry {
    @Override
    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      json.addProperty("item", name.toString());
      return json;
    }
  }

  private record TagEntry(ResourceLocation tag) implements Entry {
    @Override
    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      json.addProperty("tag", tag.toString());
      return json;
    }
  }

  @Override
  public boolean test(ItemStack stack) {
    return false;
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    return List.of();
  }

  @Override
  public boolean requiresTesting() {
    return true;
  }

  @Override
  public CustomIngredientSerializer<?> getSerializer() {
    throw new UnsupportedOperationException("ItemNameIngredient is a datagen marker, it cannot be serialized through the fabric ingredient API");
  }
}
