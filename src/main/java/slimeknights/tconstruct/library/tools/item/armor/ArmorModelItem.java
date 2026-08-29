package slimeknights.tconstruct.library.tools.item.armor;

import net.minecraft.resources.ResourceLocation;

/**
 * Armor item that names an entry in {@code tinkering/armor_models}.
 *
 * <p>Fabric port: Forge let the item answer {@code initializeClient} with a client extension that
 * knew its own model, keeping the name on the client side of the item. Fabric registers renderers
 * against items from outside, so the item has to be able to say which model it wants; this is the
 * one method that asks.
 */
public interface ArmorModelItem {
  /** Name of the armor model this item renders with */
  ResourceLocation getArmorModelName();
}
