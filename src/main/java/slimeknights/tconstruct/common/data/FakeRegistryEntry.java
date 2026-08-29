package slimeknights.tconstruct.common.data;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Handles creating fake registry entries to datagen entries based on other mods.
 *
 * <p>1.21 rework: Forge exposed {@code unfreeze()} on its registry wrapper; vanilla registries
 * have no such API, so the datagen-only shim flips {@code MappedRegistry.frozen} through the
 * access widener around the registration. Never touched at runtime — the fake entry only
 * exists so loadables can serialize a holder whose ID belongs to a mod that is not installed,
 * for data guarded by mod-loaded conditions.
 */
public class FakeRegistryEntry {

  private FakeRegistryEntry() {}

  /** Creates a dummy registry entry */
  private static <T> T getOrCreate(Registry<T> registry, ResourceLocation id, Supplier<T> constructor) {
    if (!registry.containsKey(id)) {
      MappedRegistry<T> mapped = (MappedRegistry<T>)registry;
      mapped.frozen = false;
      // blocks, items and fluids grab intrusive holders in their constructors; freezing
      // nulled that map, so it comes back for the moment of creation. Registries whose
      // values never grab one (mob effects, entity types) must register without the map,
      // or vanilla asserts on the missing holder.
      mapped.unregisteredIntrusiveHolders = new java.util.IdentityHashMap<>();
      T value = constructor.get();
      if (mapped.unregisteredIntrusiveHolders.isEmpty()) {
        mapped.unregisteredIntrusiveHolders = null;
      }
      Registry.register(registry, id, value);
      mapped.unregisteredIntrusiveHolders = null;
      mapped.frozen = true;
      return value;
    }
    return Objects.requireNonNull(registry.get(id));
  }

  /** Gets or creates a fake block with the given ID */
  public static Block block(ResourceLocation id) {
    return getOrCreate(BuiltInRegistries.BLOCK, id, () -> new Block(BlockBehaviour.Properties.of()));
  }

  /** Gets or creates a fake item with the given ID */
  public static Item item(ResourceLocation id) {
    return getOrCreate(BuiltInRegistries.ITEM, id, () -> new Item(new Item.Properties()));
  }

  /** Gets or creates a fake entity type with the given ID */
  public static net.minecraft.world.entity.EntityType<?> entity(ResourceLocation id) {
    return getOrCreate(BuiltInRegistries.ENTITY_TYPE, id, () -> net.minecraft.world.entity.EntityType.Builder.createNothing(net.minecraft.world.entity.MobCategory.MISC).build(id.toString()));
  }

  /** Gets or creates a fake mob effect with the given ID */
  public static net.minecraft.world.effect.MobEffect effect(ResourceLocation id) {
    return getOrCreate(BuiltInRegistries.MOB_EFFECT, id, () -> new net.minecraft.world.effect.MobEffect(net.minecraft.world.effect.MobEffectCategory.NEUTRAL, 0xFFFFFF) {});
  }

  /** Gets or creates a fake fluid with the given ID */
  public static Fluid fluid(ResourceLocation id) {
    return getOrCreate(BuiltInRegistries.FLUID, id, FakeFluid::new);
  }

  /** Water-like fluid whose only job is existing under a foreign ID during datagen */
  private static class FakeFluid extends net.minecraft.world.level.material.WaterFluid.Source {
    @Override
    public boolean isSource(net.minecraft.world.level.material.FluidState state) {
      return true;
    }

    @Override
    public boolean isSame(Fluid fluid) {
      return fluid == this;
    }

    @Override
    public FlowingFluid getFlowing() {
      return this;
    }

    @Override
    public FlowingFluid getSource() {
      return this;
    }
  }
}
