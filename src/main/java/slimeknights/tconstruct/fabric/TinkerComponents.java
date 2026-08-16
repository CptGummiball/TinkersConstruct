package slimeknights.tconstruct.fabric;

import net.minecraft.world.entity.Entity;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability.PersistentDataComponent;

/**
 * Cardinal Components entrypoint.
 *
 * <p>Persistent data attaches to every entity — Forge attached to living entities plus
 * anything supporting entity modifiers, and a blanket registration is the safe superset
 * (the component is an empty tag until something writes it). ALWAYS_COPY matches the Forge
 * clone handler: the data survives death, not just end-portal returns.
 */
public class TinkerComponents implements EntityComponentInitializer {

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerFor(Entity.class, PersistentDataCapability.CAPABILITY, entity -> new PersistentDataComponent());
    registry.setRespawnCopyStrategy(PersistentDataCapability.CAPABILITY, RespawnCopyStrategy.ALWAYS_COPY);
  }
}
