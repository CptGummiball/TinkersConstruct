package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableIngredientSerializer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Ingredient that only matches tools with a specific hook */
@RequiredArgsConstructor
public class ToolHookIngredient implements CustomIngredient {
  public static final LoadableIngredientSerializer<ToolHookIngredient> SERIALIZER = new LoadableIngredientSerializer<>(
    TConstruct.getResource("tool_hook"),
    RecordLoadable.create(
      Loadables.ITEM_TAG.defaultField("tag", TinkerTags.Items.MODIFIABLE, i -> i.tag),
      ToolHooks.LOADER.requiredField("hook", i -> i.hook),
      ToolHookIngredient::new));

  private final TagKey<Item> tag;
  private final ModuleHook<?> hook;
  @Nullable
  private List<ItemStack> items;

  public static ToolHookIngredient of(TagKey<Item> tag, ModuleHook<?> hook) {
    return new ToolHookIngredient(tag, hook);
  }

  public static ToolHookIngredient of(ModuleHook<?> hook) {
    return of(TinkerTags.Items.MODIFIABLE, hook);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && stack.is(tag) && stack.getItem() instanceof IModifiable modifiable && modifiable.getToolDefinition().getData().getHooks().hasHook(hook);
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    if (items == null) {
      List<ItemStack> list = new ArrayList<>();
      // filtered version of tag values
      for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
        if (holder.value() instanceof IModifiable modifiable && modifiable.getToolDefinition().getData().getHooks().hasHook(hook)) {
          list.add(new ItemStack(modifiable));
        }
      }
      items = list;
    }
    return items;
  }

  @Override
  public boolean requiresTesting() {
    // tool definitions load from data, so the display list cannot be trusted across reloads
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
