package slimeknights.tconstruct.library.client.modifiers.model;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.DyedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.MaterialModifierModel;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.client.modifiers.PotionModifierModel;

/**
 * Registers the modifier sprite types a modifier model map can name.
 *
 * <p>These are the {@code "type"} values in {@code tinkering/modifiers/sprites}: without them every
 * entry that is more than a bare texture path fails to parse, and the modifier draws nothing.
 *
 * <p>Fabric port: Forge did this from {@code TinkerClient}, alongside the sprite transformer and
 * armor texture registrations that belong to the datagen and armor slices. Only the modifier models
 * moved here, so the rest can join their own slices without dragging this along.
 */
public final class ModifierModelLoaders {
  private ModifierModelLoaders() {}

  /** Registers the modifier model types; call once before the first resource reload */
  public static void init() {
    ModifierModel.LOADER.register(TConstruct.getResource("empty"), ModifierModel.EMPTY.getLoader());
    ModifierModel.LOADER.register(TConstruct.getResource("compound"), CompoundModifierModel.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("conditional"), ConditionalModifierModel.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("trait"), TraitModel.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("basic"), NormalModifierModel.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("dyed"), DyedModifierModel.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("material"), MaterialModifierModel.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("potion"), PotionModifierModel.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("armor_trim"), TrimModifierModel.Armor.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("custom_trim"), TrimModifierModel.Custom.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("banner"), BannerModifierModel.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("fluid"), FluidModifierModel.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("tank"), TankModifierModel.LOADER);
    ModifierModel.LOADER.register(TConstruct.getResource("material_has_fallback"), MaterialHasFallbackModifierModel.LOADER);
  }
}
