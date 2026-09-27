package slimeknights.mantle.client.model.generators;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.ExistingFileHelper;

/** Builder for item models, port of Forge's builder of the same name */
public class ItemModelBuilder extends ModelBuilder<ItemModelBuilder> {
  public ItemModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
    super(outputLocation, existingFileHelper);
  }
}
