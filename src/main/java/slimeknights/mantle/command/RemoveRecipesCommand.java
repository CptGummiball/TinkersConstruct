package slimeknights.mantle.command;

import net.minecraft.resources.FileToIdConverter;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

/**
 * Datagen face of mantle's recipe remover: the constants the configuration data provider
 * writes {@code mantle/remove_recipes} presets with.
 *
 * <p>PORT NOTE: the runtime that applies these presets (predicate-driven recipe removal,
 * plus the {@code /mantle remove_recipes} dump command) is not ported yet; the shipped
 * presets are all gated behind config options that default off. Tracked in PORTING.md.
 */
public class RemoveRecipesCommand {

  private RemoveRecipesCommand() {}

  /** Maps preset ids to their data files */
  public static final FileToIdConverter PRESETS = FileToIdConverter.json("mantle/remove_recipes");

  /** Loadable for the recipe type list in a preset; a single type serializes compactly, the shape the shipped presets use */
  public static final Loadable<List<RecipeType<?>>> RECIPE_TYPES = new Loadable<>() {
    private final Loadable<List<RecipeType<?>>> list = Loadables.RECIPE_TYPE.list(1);

    @Override
    public List<RecipeType<?>> convert(com.google.gson.JsonElement element, String key, slimeknights.mantle.util.typed.TypedMap context) {
      if (element.isJsonPrimitive()) {
        return List.of(Loadables.RECIPE_TYPE.convert(element, key, context));
      }
      return list.convert(element, key, context);
    }

    @Override
    public com.google.gson.JsonElement serialize(List<RecipeType<?>> object) {
      if (object.size() == 1) {
        return Loadables.RECIPE_TYPE.serialize(object.get(0));
      }
      return list.serialize(object);
    }

    @Override
    public List<RecipeType<?>> decode(net.minecraft.network.RegistryFriendlyByteBuf buffer, slimeknights.mantle.util.typed.TypedMap context) {
      return list.decode(buffer, context);
    }

    @Override
    public void encode(net.minecraft.network.RegistryFriendlyByteBuf buffer, List<RecipeType<?>> object) {
      list.encode(buffer, object);
    }
  };
}
