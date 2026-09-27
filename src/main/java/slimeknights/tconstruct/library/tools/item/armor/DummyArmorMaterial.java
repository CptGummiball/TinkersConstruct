package slimeknights.tconstruct.library.tools.item.armor;

import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.registration.object.IdAwareObject;

import java.util.List;
import java.util.Map;

/**
 * Armor material with zeroed stats, since Tinkers bypasses all the usages — armor stats come
 * from tool definitions and materials instead.
 *
 * <p>Port note: on 1.20 {@code ArmorMaterial} was an interface this class implemented. 1.21
 * turned it into a registered record ({@code ArmorItem} takes a {@code Holder<ArmorMaterial>}),
 * so this is now a handle that registers the zeroed record eagerly (same semantics as the
 * deferred-register shims) and exposes the holder for armor item construction. Extending the
 * old shape crashed Lombok's handlers and was one of the two phantom-error sources.
 */
@Getter
public class DummyArmorMaterial implements IdAwareObject {
  private final ResourceLocation id;
  private final Holder<ArmorMaterial> holder;

  public DummyArmorMaterial(ResourceLocation id, Holder<SoundEvent> equipSound) {
    this.id = id;
    this.holder = Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, id, new ArmorMaterial(
      Map.of(),            // defense: zero for every slot
      0,                   // enchantment value
      equipSound,
      () -> Ingredient.EMPTY,
      List.of(),           // no vanilla texture layers; Tinkers renders armor itself
      0,                   // toughness
      0));                 // knockback resistance
  }

  /** Name for display and texture paths, matching the 1.20 {@code getName()} contract. */
  public String getName() {
    return id.toString();
  }
}
