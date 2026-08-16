package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableIngredientSerializer;

import java.util.Arrays;
import java.util.List;

/** Simple ingredient checking for an item with a specific potion */
public class PotionIngredient extends ItemIngredient {
  /** Ingredient serializer instance */
  public static final LoadableIngredientSerializer<PotionIngredient> SERIALIZER = new LoadableIngredientSerializer<>(Mantle.getResource("potion"), RecordLoadable.create(
    ItemsField.INSTANCE, TAG_FIELD,
    Loadables.POTION.nullableField("potion", i -> i.potion),
    PotionIngredient::new
  ));

  /** Potion to require; null matches stacks without a potion (1.21 removed the empty potion sentinel) */
  @Nullable
  private final Potion potion;
  protected PotionIngredient(List<Item> items, @Nullable TagKey<Item> itemTag, @Nullable Potion potion) {
    super(items, itemTag);
    this.potion = potion;
  }

  /** Creates a potion ingredient matching a list of items */
  public static PotionIngredient of(Potion potion, List<ItemLike> items) {
    return new PotionIngredient(toItem(items), null, potion);
  }

  /** Creates a potion ingredient matching a list of items */
  public static PotionIngredient of(Potion potion, ItemLike... items) {
    return of(potion, Arrays.asList(items));
  }

  /** Creates a potion ingredient matching a tag */
  public static PotionIngredient of(Potion potion, TagKey<Item> tag) {
    return new PotionIngredient(List.of(), tag, potion);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    // stack must match, any item must match, and potion must match; 1.21 stores the potion in a component
    return stack != null && super.test(stack)
      && stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion()
              .map(holder -> holder.value() == potion)
              .orElse(potion == null);
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    if (potion == null) {
      return super.getMatchingStacks();
    }
    var holder = BuiltInRegistries.POTION.wrapAsHolder(potion);
    return allItems().map(item -> PotionContents.createItemStack(item, holder)).toList();
  }

  @Override
  public boolean requiresTesting() {
    return true;
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
