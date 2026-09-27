package slimeknights.mantle.client.model.generators;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.ExistingFileHelper;

/** Model provider writing into the {@code item} model folder, port of Forge's provider of the same name */
public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {
  public ItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
    super(output, modid, ITEM_FOLDER, ItemModelBuilder::new, existingFileHelper);
  }

  /** Creates a generated item model with a single layer texture, matching the item's registry name */
  public ItemModelBuilder basicItem(ItemLike item) {
    return basicItem(BuiltInRegistries.ITEM.getKey(item.asItem()));
  }

  /** Creates a generated item model with a single layer texture, matching the given name */
  public ItemModelBuilder basicItem(ResourceLocation item) {
    return getBuilder(item.toString())
      .parent(new ModelFile.UncheckedModelFile("item/generated"))
      .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
  }

  @Override
  public String getName() {
    return "Item Models: " + modid;
  }
}
