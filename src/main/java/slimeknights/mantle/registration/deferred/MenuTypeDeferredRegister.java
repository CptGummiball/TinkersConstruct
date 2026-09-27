package slimeknights.mantle.registration.deferred;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import slimeknights.mantle.registration.RegistryObject;

/**
 * Deferred register for menu types.
 *
 * <p>Forge's {@code IForgeMenuType.create(IContainerFactory)} passed the raw opening buffer
 * to the client factory. On Fabric the equivalent is {@link ExtendedScreenHandlerType} with
 * typed opening data — and every Mantle/Tinkers menu sent exactly one thing in that buffer:
 * the block position (written by the old {@code NetworkHooks.openScreen(player, provider,
 * pos)} call). The factory therefore receives a {@link BlockPos} directly.
 */
@SuppressWarnings("unused")
public class MenuTypeDeferredRegister extends DeferredRegisterWrapper<MenuType<?>> {

  public MenuTypeDeferredRegister(String modID) {
    super(Registries.MENU, modID);
  }

  /** Factory for a menu opened with a block position, the data every Mantle menu used. */
  public interface PosFactory<C extends AbstractContainerMenu> {
    C create(int windowId, net.minecraft.world.entity.player.Inventory inventory, BlockPos pos);
  }

  /**
   * Registers a new menu type
   * @param name     Menu name
   * @param factory  Menu factory
   * @return  Registry object containing the menu type
   */
  public <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> register(String name, PosFactory<C> factory) {
    return register.register(name, () -> new ExtendedScreenHandlerType<>(
      (windowId, inventory, pos) -> factory.create(windowId, inventory, pos),
      StreamCodec.of((buffer, pos) -> buffer.writeBlockPos(pos), buffer -> buffer.readBlockPos())));
  }

  /** Factory for a menu opened with a custom typed payload */
  public interface DataFactory<C extends AbstractContainerMenu, D> {
    C create(int windowId, net.minecraft.world.entity.player.Inventory inventory, D data);
  }

  /**
   * Registers a menu whose opening data is not a block position. Used by the tool inventory menu,
   * the one menu opened from a held item rather than a block, which syncs the tool's inventory slot
   * plus the configured sync payload.
   * @param name     Menu name
   * @param codec    Codec for the opening data
   * @param factory  Menu factory
   * @return  Registry object containing the menu type
   */
  public <C extends AbstractContainerMenu, D> RegistryObject<MenuType<C>> register(String name, StreamCodec<? super RegistryFriendlyByteBuf,D> codec, DataFactory<C,D> factory) {
    return register.register(name, () -> new ExtendedScreenHandlerType<>(factory::create, codec));
  }
}
