package slimeknights.mantle.registration;

import com.mojang.brigadier.arguments.ArgumentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.WoodType;
import slimeknights.mantle.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistrationHelper {
  /** Wood types to register with the texture atlas */
  private static final List<WoodType> WOOD_TYPES = new ArrayList<>();

  /** Properties for a standard bucket item */
  public static final Item.Properties BUCKET_PROPS = new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1);

  /**
   * Used to mark injected registry objects, as despite being set to null they will be nonnull at runtime.
   * @param <T>  Class type
   * @return  Null, its a lie
   */
  @SuppressWarnings("ConstantConditions")
  public static <T> T injected() {
    return null;
  }

  /**
   * Gets a holder for a registry object
   * @param registry  Registry instance
   * @param entry     Entry to fetch holder
   * @param <T>       Registry type
   * @param <R>       Return type, typically but not strictly registry type
   * @return  Supplier for the given registry casted to the requested type
   */
  @SuppressWarnings("unchecked")  // we know the entry is the given type
  public static <T, R extends T> Supplier<R> getCastedHolder(DefaultedRegistry<T> registry, T entry) {
    Supplier<T> holder = RegistryHelper.getHolder(registry, entry);
    return () -> (R) holder.get();
  }
  // handleMissingMappings is not ported. It rode on Forge's MissingMappingsEvent, which
  // remapped renamed registry entries when an existing world loaded. Fabric has no
  // equivalent hook, and this build targets a new pack with no Forge-era saves to migrate.

  /** Registers a wood type to be injected into the atlas, should be called before client setup */
  public static void registerWoodType(WoodType type) {
    synchronized (WOOD_TYPES) {
      WOOD_TYPES.add(type);
      WoodType.register(type);
    }
  }

  /** Runs the given consumer for each wood type registered */
  public static void forEachWoodType(Consumer<WoodType> consumer) {
    WOOD_TYPES.forEach(consumer);
  }

  /** Casts the class type to make it a valid argument type */
  @SuppressWarnings("unchecked")
  public static <T extends ArgumentType<?>> Class<T> genericArgumentType(Class<? super T> type) {
    return (Class<T>) type;
  }

  /**
   * Standard block properties for a fluid block. Lived on FluidDeferredRegister in Forge
   * Mantle; that class is not ported (Fabric registers eagerly), so the factory lives here.
   */
  public static net.minecraft.world.level.block.state.BlockBehaviour.Properties createFluidProperties(net.minecraft.world.level.material.MapColor color, int lightLevel) {
    return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
      .mapColor(color).replaceable().noCollission().randomTicks().strength(100.0F)
      .lightLevel(state -> lightLevel).pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)
      .noLootTable().liquid().sound(net.minecraft.world.level.block.SoundType.EMPTY);
  }
}
