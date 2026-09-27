package slimeknights.mantle.client.book.data.element;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * One or more item stacks named by a page, plus the action clicking them fires.
 *
 * <p>Book JSON writes these in whichever form was shortest at the time: a bare id string, an
 * ingredient-shaped object, a tag, an array, or a wrapper object carrying {@code item} alongside
 * {@code action} and {@code amount}. All five forms are accepted here so no book data has to
 * change.
 *
 * <p>Fabric port: 63 entries in the shipped books use Forge's {@code forge:nbt} ingredient to pin a
 * tool's materials or a creative slot's type. That serializer does not exist here, and 1.21 moved
 * stack NBT into components, so the {@code nbt} block is applied as {@code custom_data} — which is
 * exactly where the port keeps tool data — with {@code Damage} lifted out to its own component.
 */
public class ItemStackData {
  /** Placeholder for an entry that could not be resolved */
  public static final ItemStackData EMPTY = new ItemStackData(List.of());

  private final List<ItemStack> items;
  /** Action fired when the item is clicked, see {@link slimeknights.mantle.client.book.action.StringActionProcessor} */
  @Nullable
  public String action = null;

  public ItemStackData(List<ItemStack> items) {
    this.items = items;
  }

  public ItemStackData(ItemStack stack) {
    this(List.of(stack));
  }

  /** Every stack this entry cycles through */
  public List<ItemStack> getItems() {
    return this.items;
  }

  /** First stack, or empty if the entry resolved to nothing */
  public ItemStack getItem() {
    return this.items.isEmpty() ? ItemStack.EMPTY : this.items.get(0);
  }

  public boolean isEmpty() {
    return this.items.isEmpty();
  }

  /** Gson adapter accepting every shape the shipped book data uses */
  public static class Deserializer implements JsonDeserializer<ItemStackData> {
    @Override
    public ItemStackData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
      // wrapper object: {"item": <entry>, "action": "...", "amount": n}
      if (element.isJsonObject()) {
        JsonObject json = element.getAsJsonObject();
        if (json.has("item") && !json.has("nbt") && !json.has("type")) {
          ItemStackData data = new ItemStackData(parseEntries(json.get("item")));
          if (json.has("action")) {
            data.action = GsonHelper.getAsString(json, "action");
          }
          int amount = GsonHelper.getAsInt(json, "amount", 1);
          if (amount != 1) {
            data.getItems().forEach(stack -> stack.setCount(amount));
          }
          return data;
        }
      }
      return new ItemStackData(parseEntries(element));
    }
  }

  /** Parses any of the accepted entry shapes into a list of stacks */
  private static List<ItemStack> parseEntries(JsonElement element) {
    List<ItemStack> stacks = new ArrayList<>();
    addEntries(element, stacks);
    return stacks;
  }

  private static void addEntries(JsonElement element, List<ItemStack> stacks) {
    if (element == null || element.isJsonNull()) {
      return;
    }
    // "minecraft:stone"
    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
      Item item = resolveItem(element.getAsString());
      if (item != null) {
        stacks.add(new ItemStack(item));
      }
      return;
    }
    // a list of any of the other forms
    if (element.isJsonArray()) {
      JsonArray array = element.getAsJsonArray();
      for (JsonElement child : array) {
        addEntries(child, stacks);
      }
      return;
    }
    if (!element.isJsonObject()) {
      Mantle.logger.error("Unable to read book item entry {}", element);
      return;
    }
    JsonObject json = element.getAsJsonObject();
    // {"tag": "c:ingots/iron"} cycles through everything in the tag
    if (json.has("tag") && !json.has("item")) {
      TagKey<Item> tag = TagKey.create(Registries.ITEM, JsonHelper.getResourceLocation(json, "tag"));
      BuiltInRegistries.ITEM.getTag(tag).ifPresent(holders -> holders.forEach(holder -> stacks.add(new ItemStack(holder))));
      return;
    }
    // {"item": "...", "nbt": {...}} - the shape forge:nbt used
    if (json.has("item")) {
      JsonElement item = json.get("item");
      if (item.isJsonPrimitive()) {
        Item resolved = resolveItem(item.getAsString());
        if (resolved == null) {
          return;
        }
        ItemStack stack = new ItemStack(resolved);
        if (json.has("nbt")) {
          applyLegacyNbt(stack, json.get("nbt"));
        }
        stacks.add(stack);
      } else {
        addEntries(item, stacks);
      }
      return;
    }
    Mantle.logger.error("Unable to read book item entry {}", json);
  }

  /** Resolves an item id, logging rather than throwing so one bad page does not kill the book */
  @Nullable
  private static Item resolveItem(String id) {
    ResourceLocation location = ResourceLocation.tryParse(id);
    if (location == null) {
      Mantle.logger.error("Invalid item id {} in book data", id);
      return null;
    }
    Item item = BuiltInRegistries.ITEM.get(location);
    if (item == Items.AIR && !location.equals(BuiltInRegistries.ITEM.getDefaultKey())) {
      Mantle.logger.error("Unknown item {} in book data", id);
      return null;
    }
    return item;
  }

  /**
   * Applies a 1.20-shaped stack tag to a stack.
   *
   * <p>Everything lands in {@code custom_data}, which is where this port keeps tool NBT, so
   * {@code tic_materials} and friends read back exactly as they did. {@code Damage} is the one key
   * vanilla promoted to its own component, so it is moved across.
   */
  private static void applyLegacyNbt(ItemStack stack, JsonElement element) {
    CompoundTag tag;
    try {
      tag = net.minecraft.nbt.TagParser.parseTag(element.isJsonPrimitive() ? element.getAsString() : element.toString());
    } catch (Exception e) {
      Mantle.logger.error("Failed to parse book item NBT {}", element, e);
      return;
    }
    if (tag.contains("Damage")) {
      stack.set(DataComponents.DAMAGE, tag.getInt("Damage"));
      tag.remove("Damage");
    }
    if (!tag.isEmpty()) {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
  }

  /** Creates data from a plain item id, used by the index deserializer */
  public static ItemStackData of(JsonPrimitive primitive) {
    return new ItemStackData(parseEntries(primitive));
  }
}
