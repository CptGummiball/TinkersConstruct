package slimeknights.tconstruct.library.tools.capability;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.SyncPersistentDataPacket;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

/**
 * Persistent NBT data on an entity. For players, this is automatically synced to the client
 * on login, but not during gameplay. Persists after death.
 *
 * <p>Forge attached this through {@code AttachCapabilitiesEvent} with hand-written
 * clone/respawn plumbing. On Fabric it is a Cardinal Components entity component: CCA owns
 * NBT persistence and the death copy (see {@code TinkerComponents} for the registration with
 * {@code RespawnCopyStrategy.ALWAYS_COPY}); the login sync stays explicit here.
 */
public class PersistentDataCapability {

  private PersistentDataCapability() {}

  /** Component key; the CAPABILITY name is kept so call sites port with an import rewrite. */
  public static final ComponentKey<PersistentDataComponent> CAPABILITY =
    ComponentRegistry.getOrCreate(TConstruct.getResource("persistent_data"), PersistentDataComponent.class);

  /** Gets the data or warns if its missing */
  public static ModDataNBT getOrWarn(Entity entity) {
    PersistentDataComponent component = CAPABILITY.getNullable(entity);
    if (component == null) {
      TConstruct.LOG.warn("Missing Tinkers NBT on entity {}, this should not happen", entity.getType());
      return new ModDataNBT();
    }
    return component.getData();
  }

  /** Registers the login sync; component attachment lives in TinkerComponents. */
  public static void register() {
    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sync(handler.getPlayer()));
  }

  /** Syncs the data to the given player */
  public static void sync(Player player) {
    PersistentDataComponent component = CAPABILITY.getNullable(player);
    if (component != null && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
      TinkerNetwork.getInstance().sendTo(new SyncPersistentDataPacket(component.getData().getCopy()), serverPlayer);
    }
  }

  /** Cardinal component wrapping the mod data. */
  public static class PersistentDataComponent implements Component {

    private ModDataNBT data = new ModDataNBT();

    public ModDataNBT getData() {
      return data;
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
      this.data = ModDataNBT.readFromNBT(tag.getCompound("data"));
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
      CompoundTag copy = data.getCopy();
      if (!copy.isEmpty()) {
        tag.put("data", copy);
      }
    }
  }
}
