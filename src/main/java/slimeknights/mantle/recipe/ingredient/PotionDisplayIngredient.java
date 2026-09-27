package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableIngredientSerializer;

import javax.annotation.Nullable;
import java.util.List;

/** Ingredient that shows all potion variants on the displayed item list */
public class PotionDisplayIngredient extends ItemIngredient {
  /** Ingredient serializer instance */
  public static final LoadableIngredientSerializer<PotionDisplayIngredient> SERIALIZER = new LoadableIngredientSerializer<>(Mantle.getResource("potion_display"), RecordLoadable.create(ItemsField.INSTANCE, TAG_FIELD, PotionDisplayIngredient::new));

  /** cache for {@link #getMatchingStacks()} */
  private List<ItemStack> displayStacks = null;

  protected PotionDisplayIngredient(List<Item> items, @Nullable TagKey<Item> tag) {
    super(items, tag);
  }

  /** Creates a ingredient matching a list of items */
  public static PotionDisplayIngredient of(List<ItemLike> items) {
    return new PotionDisplayIngredient(toItem(items), null);
  }

  /** Creates a ingredient matching a list of items */
  public static PotionDisplayIngredient of(ItemLike... items) {
    return of(List.of(items));
  }

  /** Creates a ingredient matching a tag */
  public static PotionDisplayIngredient of(TagKey<Item> tag) {
    return new PotionDisplayIngredient(List.of(), tag);
  }

  @Override
  public boolean requiresTesting() {
    // matches by item alone, the potion list is display only
    return false;
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    // if empty, means we want wildcard, show all potions on the stack
    if (displayStacks == null) {
      // 1.21 has no empty potion entry to filter out
      displayStacks = BuiltInRegistries.POTION.holders()
        .flatMap(potion -> allItems().map(item -> PotionContents.createItemStack(item, potion)))
        .toList();
    }
    return displayStacks;
  }

  @Override
  public CustomIngredientSerializer<?> getSerializer() {
    return SERIALIZER;
  }

  /** Serializes to JSON for datagen */
  public JsonElement toJson() {
    return SERIALIZER.serialize(this);
  }
}
