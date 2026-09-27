package slimeknights.tconstruct.library.tools.capability;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Capability to allow an entity to store modifiers, used on projectiles fired from modifiable items.
 *
 * <p>Port note: Forge attached a serializable provider to entities matching the registered
 * predicates. On Fabric this is a Cardinal Components entity component attached to every
 * entity (see {@code TinkerComponents}); it serializes nothing while empty, so the blanket
 * attach is free. The predicate list keeps its role of gating which entities Tinkers
 * <i>writes</i> modifiers to.
 */
public class EntityModifierCapability {
  /** Default instance to use with orElse */
  public static final EntityModifiers EMPTY = new EntityModifiers() {
    @Override
    public ModifierNBT getModifiers() {
      return ModifierNBT.EMPTY;
    }

    @Override
    public void setModifiers(ModifierNBT nbt) {}

    @Override
    public void addModifiers(ModifierNBT nbt) {}
  };

  private EntityModifierCapability() {}

  /* Static helpers */

  /** List of predicates to check if the entity supports this capability */
  private static final List<Predicate<Entity>> ENTITY_PREDICATES = new ArrayList<>();

  /** Capability ID */
  private static final ResourceLocation ID = TConstruct.getResource("modifiers");
  /** Component key; the CAPABILITY name is kept so call sites port with an import rewrite. */
  public static final ComponentKey<ModifiersComponent> CAPABILITY =
    ComponentRegistry.getOrCreate(ID, ModifiersComponent.class);

  /** Gets the capability for the entity or an empty instance if missing */
  public static EntityModifiers getCapability(Entity entity) {
    ModifiersComponent component = CAPABILITY.getNullable(entity);
    return component != null ? component : EMPTY;
  }

  /** Gets the data or an empty instance if missing */
  public static ModifierNBT getOrEmpty(Entity entity) {
    return getCapability(entity).getModifiers();
  }

  /** Checks if the given entity supports this capability */
  public static boolean supportCapability(Entity entity) {
    for (Predicate<Entity> entityPredicate : ENTITY_PREDICATES) {
      if (entityPredicate.test(entity)) {
        return true;
      }
    }
    return false;
  }

  /** Registers a predicate of entites that need this capability */
  public static void registerEntityPredicate(Predicate<Entity> predicate) {
    ENTITY_PREDICATES.add(predicate);
  }

  /** No event wiring needed on Fabric; the component attaches in {@code TinkerComponents}. */
  public static void register() {}

  /** Cardinal component holding the modifier list. */
  public static class ModifiersComponent implements Component, EntityModifiers {
    private static final String KEY_MODIFIERS = "modifiers";

    @Getter @Setter
    private ModifierNBT modifiers = ModifierNBT.EMPTY;

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
      this.modifiers = ModifierNBT.readFromNBT(tag.getList(KEY_MODIFIERS, Tag.TAG_COMPOUND));
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
      if (!modifiers.isEmpty()) {
        tag.put(KEY_MODIFIERS, modifiers.serializeToNBT());
      }
    }
  }

  /** Interface for callers to use */
  public interface EntityModifiers {
    /** Gets the stored modifiers */
    ModifierNBT getModifiers();

    /** Sets the stored modifiers */
    void setModifiers(ModifierNBT nbt);

    /** Adds additional modifiers to the stored modifiers */
    default void addModifiers(ModifierNBT nbt) {
      ModifierNBT existing = getModifiers();
      if (existing.isEmpty()) {
        setModifiers(nbt);
      } else {
        setModifiers(ModifierNBT.builder().add(existing).add(nbt).build());
      }
    }
  }
}
