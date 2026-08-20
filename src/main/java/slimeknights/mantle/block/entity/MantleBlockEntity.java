package slimeknights.mantle.block.entity;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.client.model.data.ModelData;

import javax.annotation.Nullable;

public class MantleBlockEntity extends BlockEntity
    implements slimeknights.mantle.transfer.cap.ICapabilityProvider, RenderDataBlockEntity {

  public MantleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  /* Model data: what a block entity tells its model about itself, for models that vary per block */

  /**
   * Extra state this block entity's model needs, such as which block it is retextured with.
   *
   * <p>Forge's {@code BlockEntity#getModelData}, kept under the same name so the overrides port
   * unchanged. Fabric calls the same idea a render attachment and reads it through the block view
   * during a chunk rebuild, which {@link #getRenderData()} below bridges.
   */
  public ModelData getModelData() {
    return ModelData.EMPTY;
  }

  @Nullable
  @Override
  public Object getRenderData() {
    ModelData data = getModelData();
    // null means "nothing attached", which is cheaper for the renderer than an empty bag
    return data == ModelData.EMPTY ? null : data;
  }

  /**
   * Asks for this block's model to be rebuilt because {@link #getModelData()} changed.
   *
   * <p>Forge tracked model data separately from the chunk mesh and could refresh just that; Fabric
   * reads the attachment while the mesh is built, so the only way to pick up a change is to rebuild
   * the section. Client side only — on the server the block update that follows does the same job
   * for everyone.
   */
  public void requestModelDataUpdate() {
    Level level = getLevel();
    if (level != null && level.isClientSide) {
      BlockState state = getBlockState();
      level.setBlocksDirty(getBlockPos(), state, state);
    }
  }

  /* Capabilities: the shimmed Forge surface used by the smeltery's internal wiring.
   * Outward-facing (hoppers, pipes) access is a Fabric storage registration instead. */

  @Override
  public <T> slimeknights.mantle.transfer.cap.LazyOptional<T> getCapability(slimeknights.mantle.transfer.cap.Capability<T> capability, @Nullable net.minecraft.core.Direction side) {
    return slimeknights.mantle.transfer.cap.LazyOptional.empty();
  }

  /** Invalidates any handlers this block entity gave out; Forge fired this on removal */
  public void invalidateCaps() {}

  @Override
  public void setRemoved() {
    super.setRemoved();
    invalidateCaps();
  }

  public boolean isClient() {
    return this.getLevel() != null && this.getLevel().isClientSide;
  }

  /**
   * Marks the chunk dirty without performing comparator updates (twice!!) or block state checks
   * Used since most of our markDirty calls only adjust TE data
   */
  @SuppressWarnings("deprecation")
  public void setChangedFast() {
    if (level != null) {
      if (level.hasChunkAt(worldPosition)) {
        level.getChunkAt(worldPosition).setUnsaved(true);
      }
    }
  }
  
  
  /* Syncing */

  /**
   * If true, this TE syncs when {@link net.minecraft.world.level.Level#blockUpdated(BlockPos, Block) is called
   * Syncs data from {@link #saveSynced(CompoundTag)}
   */
  protected boolean shouldSyncOnUpdate() {
    return false;
  }

  @Override
  @Nullable
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    // number is just used for vanilla, -1 ensures it skips all instanceof checks as its not a vanilla TE
    return shouldSyncOnUpdate() ? ClientboundBlockEntityDataPacket.create(this) : null;
  }

  /**
   * Write to NBT that is synced to the client in the update tag and in saveAdditional
   * @param nbt  NBT
   * @param registries  Registry access for component-aware serialisation (new in 1.20.5)
   */
  protected void saveSynced(CompoundTag nbt, HolderLookup.Provider registries) {}

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag nbt = new CompoundTag();
    saveSynced(nbt, registries);
    return nbt;
  }

  @Override
  public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
    super.saveAdditional(nbt, registries);
    saveSynced(nbt, registries);
  }
}
