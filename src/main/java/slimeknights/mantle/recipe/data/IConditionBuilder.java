package slimeknights.mantle.recipe.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import slimeknights.mantle.recipe.condition.ICondition;
import slimeknights.mantle.recipe.condition.TagEmptyCondition;
import slimeknights.mantle.recipe.condition.TagFilledCondition;

/**
 * Shim for Forge's {@code IConditionBuilder}: unqualified condition factories for recipe
 * providers to mix in. Names match Forge exactly so provider code ports without edits;
 * everything delegates to {@link ConditionHelper}'s factories, whose serialized form is the
 * Forge dialect the existing data files carry.
 */
@SuppressWarnings("unused")  // API
public interface IConditionBuilder {

  default ICondition and(ICondition... values) {
    return ConditionHelper.and(values);
  }

  default ICondition or(ICondition... values) {
    return ConditionHelper.or(values);
  }

  default ICondition not(ICondition value) {
    return ConditionHelper.not(value);
  }

  default ICondition TRUE() {
    return ConditionHelper.trueCondition();
  }

  default ICondition FALSE() {
    return ConditionHelper.falseCondition();
  }

  default ICondition modLoaded(String modid) {
    return ConditionHelper.modLoaded(modid);
  }

  default ICondition itemExists(String namespace, String path) {
    return ConditionHelper.itemExists(ResourceLocation.fromNamespaceAndPath(namespace, path));
  }

  default ICondition tagEmpty(TagKey<Item> tag) {
    return new TagEmptyCondition<>(tag);
  }

  default ICondition tagFilled(TagKey<Item> tag) {
    return new TagFilledCondition<>(tag);
  }
}
