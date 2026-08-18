package slimeknights.mantle.loot.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.HashMap;
import java.util.Map;

/**
 * A modifier applied to every loot table's output, Forge's "global loot modifier" concept.
 *
 * <p>Fabric has no equivalent, so the whole mechanism lives here: the entries are loaded
 * from {@code data/forge/loot_modifiers/global_loot_modifiers.json} by
 * {@link GlobalLootManager} and applied from a mixin on the one method every loot roll
 * funnels through. The JSON shape is unchanged from the Forge build, so the shipped data
 * (ore bonuses, mob drops) keeps working as written.
 */
public interface IGlobalLootModifier {
  /** Registry of modifier types, dispatched on the {@code type} key */
  Map<ResourceLocation, MapCodec<? extends IGlobalLootModifier>> TYPES = new HashMap<>();

  Codec<IGlobalLootModifier> CODEC = ResourceLocation.CODEC
    .<IGlobalLootModifier>dispatch(
      "type",
      IGlobalLootModifier::getTypeId,
      id -> {
        MapCodec<? extends IGlobalLootModifier> codec = TYPES.get(id);
        return codec != null ? codec : MapCodec.unit(() -> null);
      })
    .validate(modifier -> modifier == null
      ? DataResult.error(() -> "Unknown global loot modifier type")
      : DataResult.success(modifier));

  /** Registers a modifier type */
  static void register(ResourceLocation id, MapCodec<? extends IGlobalLootModifier> codec) {
    TYPES.put(id, codec);
  }

  /** Id of this modifier's type, for serialization */
  ResourceLocation getTypeId();

  /**
   * Applies this modifier to the loot a table generated.
   * @return the loot list to carry forward; may be the same instance
   */
  ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);
}
