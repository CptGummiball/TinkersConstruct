package slimeknights.tconstruct.common;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
import slimeknights.mantle.registration.object.EnumObject;

import java.util.function.Supplier;

/**
 * Contains helpers to use for registering client events.
 *
 * <p>Fabric port: Forge handed the {@code BlockColors} and {@code ItemColors} instances to the
 * registration event, so every helper took both. Fabric registers through a static registry and
 * reads the block colours off the running client, which is the same instance either way — so the
 * two parameters are gone from all three signatures.
 */
public abstract class ClientEventBase {
  /**
   * Registers a block colors alias for the given block
   * @param block  Block to register
   */
  protected static void registerBlockItemColorAlias(Block block) {
    ColorProviderRegistry.ITEM.register(
      (stack, index) -> Minecraft.getInstance().getBlockColors().getColor(block.defaultBlockState(), null, null, index),
      block);
  }

  /**
   * Registers a block colors alias for the given block supplier
   * @param block  Block to register
   */
  protected static void registerBlockItemColorAlias(Supplier<? extends Block> block) {
    registerBlockItemColorAlias(block.get());
  }

  /**
   * Registers a block colors alias for all blocks in the given instance
   * @param blocks  EnumBlock instance
   */
  protected static <B extends Block> void registerBlockItemColorAlias(EnumObject<?,B> blocks) {
    for (B block : blocks.values()) {
      registerBlockItemColorAlias(block);
    }
  }
}
