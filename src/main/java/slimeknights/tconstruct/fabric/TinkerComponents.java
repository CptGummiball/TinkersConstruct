package slimeknights.tconstruct.fabric;

import net.minecraft.world.entity.Entity;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability.ModifiersComponent;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability.PersistentDataComponent;

/**
 * Cardinal Components entrypoint.
 *
 * <p>Persistent data attaches to every entity — Forge attached to living entities plus
 * anything supporting entity modifiers, and a blanket registration is the safe superset
 * (the component is an empty tag until something writes it). ALWAYS_COPY matches the Forge
 * clone handler: the data survives death, not just end-portal returns.
 *
 * <p>Entity modifiers (projectiles fired from modifiable tools) attach the same way; the
 * component serializes nothing while empty, and write access is gated by
 * {@link EntityModifierCapability#supportCapability}.
 */
public class TinkerComponents implements EntityComponentInitializer {

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.beginRegistration(Entity.class, PersistentDataCapability.CAPABILITY)
      .respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY)
      .end(entity -> new PersistentDataComponent());
    registry.registerFor(Entity.class, EntityModifierCapability.CAPABILITY, entity -> new ModifiersComponent());
  }
}
