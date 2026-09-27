package slimeknights.mantle.client.model.generators;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.ExistingFileHelper;

/** Builder for block models, port of Forge's builder of the same name */
public class BlockModelBuilder extends ModelBuilder<BlockModelBuilder> {
  public BlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
    super(outputLocation, existingFileHelper);
  }
}
