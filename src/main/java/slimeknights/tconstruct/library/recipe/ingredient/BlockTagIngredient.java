package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableIngredientSerializer;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Item ingredient matching items with a block form in the given tag */
@RequiredArgsConstructor
public class BlockTagIngredient implements CustomIngredient {
  public static final LoadableIngredientSerializer<BlockTagIngredient> SERIALIZER = new LoadableIngredientSerializer<>(
    TConstruct.getResource("block_tag"),
    RecordLoadable.create(Loadables.BLOCK_TAG.requiredField("tag", i -> i.tag), BlockTagIngredient::new));

  private final TagKey<Block> tag;
  @Nullable
  private Set<Item> matchingItems;
  @Nullable
  private List<ItemStack> items;

  public static BlockTagIngredient of(TagKey<Block> tag) {
    return new BlockTagIngredient(tag);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && getMatchingItems().contains(stack.getItem());
  }

  @Override
  public boolean requiresTesting() {
    // matches purely on the item, but the item set is derived from a block tag, which the
    // vanilla stack list cannot express once tags reload; keep testing so reloads stay honest
    return true;
  }

  /** Gets the ordered matching items set */
  private Set<Item> getMatchingItems() {
    if (matchingItems == null) {
      matchingItems = RegistryHelper.getTagValueStream(BuiltInRegistries.BLOCK, tag)
                                    .map(Block::asItem)
                                    .filter(item -> item != Items.AIR)
                                    .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    return matchingItems;
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    if (items == null) {
      items = getMatchingItems().stream().map(ItemStack::new).toList();
    }
    return items;
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
