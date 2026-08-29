package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.transfer.cap.Capability;
import slimeknights.mantle.transfer.cap.LazyOptional;
import slimeknights.mantle.util.Lazy;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Resolves the capabilities a tool exposes through its modifiers, such as tanks and inventories.
 *
 * <p>Fabric has no per-stack capability attach, so there is no object to hang the providers off
 * and no lifetime for them to be cached against. {@link #getCapability(ItemStack, Capability)}
 * therefore builds the provider list for the stack on the spot; providers are cheap wrappers and
 * the tool cache they hold would have to be cleared on every lookup regardless, which is what
 * Forge's per-stack instances did on each {@code getCapability} call.
 *
 * <p>This is the internal face used by modifier hooks. Outward-facing lookups for other mods are
 * separate Fabric storage registrations, see {@code ToolFluidCapability#register()}.
 */
public class ToolCapabilityProvider {
  private static final List<BiFunction<ItemStack,Supplier<? extends IToolStackView>,IToolCapabilityProvider>> PROVIDER_CONSTRUCTORS = new ArrayList<>();

  private ToolCapabilityProvider() {}

  /** Gets the given capability from the tool, empty if no modifier provides it */
  @Nonnull
  public static <T> LazyOptional<T> getCapability(ItemStack stack, Capability<T> cap) {
    if (stack.isEmpty() || PROVIDER_CONSTRUCTORS.isEmpty()) {
      return LazyOptional.empty();
    }
    // NBT may not be initialized when a provider is created, so delay tool stack creation
    Lazy<ToolStack> tool = Lazy.of(() -> ToolStack.from(stack));
    List<IToolCapabilityProvider> providers = PROVIDER_CONSTRUCTORS.stream().map(con -> con.apply(stack, tool)).filter(Objects::nonNull).collect(Collectors.toList());
    if (providers.isEmpty()) {
      return LazyOptional.empty();
    }
    // clear the tool cache, as it may have changed since the last time a cap was fetched
    ToolStack toolStack = tool.get();
    toolStack.refreshTag(stack);
    // return the first successful provider
    for (IToolCapabilityProvider provider : providers) {
      provider.clearCache();
      LazyOptional<T> optional = provider.getCapability(toolStack, cap);
      if (optional.isPresent()) {
        return optional;
      }
    }
    return LazyOptional.empty();
  }

  /** Registers a tool capability provider constructor. Every capability lookup on a tool will call this constructor to create your provider.
   * Is it valid for this constructor to return null, just note that it will not be called a second time if the tools state changes. Thus you should avoid conditioning on anything other than item type */
  public static void register(BiFunction<ItemStack,Supplier<? extends IToolStackView>,IToolCapabilityProvider> constructor) {
    PROVIDER_CONSTRUCTORS.add(constructor);
  }

  /** Interface to get a capability on a tool */
  @FunctionalInterface
  public interface IToolCapabilityProvider {
    /** Gets a capability on the given tool */
    <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> cap);

    /** Called to clear the cache of the provider */
    default void clearCache() {}
  }
}
