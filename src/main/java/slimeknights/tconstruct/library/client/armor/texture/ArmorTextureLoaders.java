package slimeknights.tconstruct.library.client.armor.texture;

import slimeknights.tconstruct.TConstruct;

/**
 * Registers the layer types an armor model can name.
 *
 * <p>These are the {@code "type"} values in {@code tinkering/armor_models}: without them every
 * layer fails to parse and the armor draws nothing.
 *
 * <p>Fabric port: Forge did this from {@code TinkerClient}, alongside the modifier model and sprite
 * transformer registrations. Those moved to their own slices, so this holds only the armor half.
 */
public final class ArmorTextureLoaders {
  private ArmorTextureLoaders() {}

  /** Registers the armor texture types; call once before the first resource reload */
  public static void init() {
    ArmorTextureSupplier.LOADER.register(TConstruct.getResource("fixed"), FixedArmorTextureSupplier.LOADER);
    ArmorTextureSupplier.LOADER.register(TConstruct.getResource("dyed"), DyedArmorTextureSupplier.LOADER);
    ArmorTextureSupplier.LOADER.register(TConstruct.getResource("first_present"), FirstArmorTextureSupplier.LOADER);
    ArmorTextureSupplier.LOADER.register(TConstruct.getResource("material"), MaterialArmorTextureSupplier.Material.LOADER);
    ArmorTextureSupplier.LOADER.register(TConstruct.getResource("persistent_data"), MaterialArmorTextureSupplier.PersistentData.LOADER);
    ArmorTextureSupplier.LOADER.register(TConstruct.getResource("trim"), TrimArmorTextureSupplier.LOADER);
    ArmorTextureSupplier.LOADER.register(TConstruct.getResource("material_has_fallback"), MaterialHasFallbackTextureSupplier.LOADER);
  }
}
