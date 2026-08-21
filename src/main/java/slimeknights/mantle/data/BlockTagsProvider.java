package slimeknights.mantle.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * Shim for Forge's {@code BlockTagsProvider}: vanilla only ships the hardcoded
 * {@code VanillaBlockTagsProvider}, so mods bring their own subclassable one. Inherits the
 * tag-remove datagen support from {@link BuiltinRegistryTagProvider}.
 */
public abstract class BlockTagsProvider extends BuiltinRegistryTagProvider<Block> {

  public BlockTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, BuiltInRegistries.BLOCK, lookupProvider, modId, existingFileHelper);
  }
}
