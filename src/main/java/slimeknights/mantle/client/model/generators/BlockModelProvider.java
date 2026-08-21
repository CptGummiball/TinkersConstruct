package slimeknights.mantle.client.model.generators;

import net.minecraft.data.PackOutput;
import slimeknights.mantle.data.ExistingFileHelper;

/** Model provider writing into the {@code block} model folder, port of Forge's provider of the same name */
public abstract class BlockModelProvider extends ModelProvider<BlockModelBuilder> {
  public BlockModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
    super(output, modid, BLOCK_FOLDER, BlockModelBuilder::new, existingFileHelper);
  }

  @Override
  public String getName() {
    return "Block Models: " + modid;
  }
}
