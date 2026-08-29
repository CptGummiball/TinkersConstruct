package slimeknights.tconstruct.tables.block.entity.table;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.block.entity.IRetexturedBlockEntity;
import slimeknights.mantle.client.model.data.ModelData;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.shared.block.entity.TableBlockEntity;

import javax.annotation.Nonnull;

public abstract class RetexturedTableBlockEntity extends TableBlockEntity implements IRetexturedBlockEntity {
  private static final String TAG_TEXTURE = "texture";

  @Nonnull @Getter
  protected Block texture = Blocks.AIR;
  public RetexturedTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Component name, int size) {
    super(type, pos, state, name, size);
  }
  // Forge's getRenderBoundingBox is not needed here. It widened the box a per-block-entity
  // frustum test used, and vanilla has no such test — every block entity in a visible section
  // renders, however far outside its own block it draws.


  /* Textures */

  /** Scratch tag satisfying the mantle interface; this implementation stores the texture in its own NBT key instead */
  private final CompoundTag persistentData = new CompoundTag();

  @Override
  public CompoundTag getPersistentData() {
    return persistentData;
  }



  @Override
  public String getTextureName() {
    return RetexturedHelper.getTextureName(texture);
  }

  @Override
  public ModelData getModelData() {
    return RetexturedHelper.getModelDataBuilder(texture).build();
  }

  private void textureUpdated() {
    // phase 5 note: Forge refreshed ModelData here; on Fabric the render layer reads the
    // texture from the synced NBT, so a block update is all that is needed
    if (level != null && level.isClientSide) {
      BlockState state = getBlockState();
      level.sendBlockUpdated(worldPosition, state, state, 0);
    }
  }

  @Override
  public void updateTexture(String name) {
    Block oldTexture = texture;
    texture = RetexturedHelper.getBlock(name);
    if (oldTexture != texture) {
      setChangedFast();
      textureUpdated();
    }
  }

  @Override
  public void saveSynced(CompoundTag tags, net.minecraft.core.HolderLookup.Provider registries) {
    super.saveSynced(tags, registries);
    if (texture != Blocks.AIR) {
      tags.putString(TAG_TEXTURE, getTextureName());
    }
  }

  @Override
  public void loadAdditional(CompoundTag tags, net.minecraft.core.HolderLookup.Provider registries) {
    super.loadAdditional(tags, registries);
    if (tags.contains(TAG_TEXTURE, Tag.TAG_STRING)) {
      texture = RetexturedHelper.getBlock(tags.getString(TAG_TEXTURE));
      textureUpdated();
    }
  }
}
