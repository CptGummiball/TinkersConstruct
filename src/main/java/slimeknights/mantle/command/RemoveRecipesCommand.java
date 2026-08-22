package slimeknights.mantle.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;

import java.io.Reader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Command generating a datapack that removes all recipes matching a data-driven preset,
 * mantle's recipe remover. The presets live in {@code mantle/remove_recipes} and pair item
 * predicates for the result and inputs with a list of recipe types; matched recipes are
 * disabled by writing an empty {@code forge:conditional} wrapper over their id into the
 * generated pack, which the condition filter removes at load.
 *
 * <p>PORT: upstream hung this under Forge's command event with the other pack utilities;
 * here it registers as {@code /mantle remove_recipes preset <id>}. Removal semantics: a
 * recipe is removed when its type is listed, its result matches the result predicate, and
 * any of its inputs matches the input predicate (the shipped presets all use
 * {@code mantle:any} for the input).
 */
public class RemoveRecipesCommand {

  private RemoveRecipesCommand() {}

  /** Maps preset ids to their data files */
  public static final FileToIdConverter PRESETS = FileToIdConverter.json("mantle/remove_recipes");

  private static final DynamicCommandExceptionType PRESET_NOT_FOUND = new DynamicCommandExceptionType(id -> Component.translatable("command.mantle.remove_recipes.preset_not_found", String.valueOf(id)));
  private static final DynamicCommandExceptionType PRESET_INVALID = new DynamicCommandExceptionType(id -> Component.translatable("command.mantle.remove_recipes.preset_invalid", String.valueOf(id)));

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


  /* Command */

  /** Registers this sub command with the root command */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_GAME_COMMANDS))
              .then(Commands.literal("preset")
                            .then(Commands.argument("name", ResourceLocationArgument.id())
                                          .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(
                                            PRESETS.listMatchingResources(context.getSource().getServer().getResourceManager()).keySet().stream().map(PRESETS::fileToId), builder))
                                          .executes(RemoveRecipesCommand::runPreset)));
  }

  /** Runs the command for a preset */
  private static int runPreset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ResourceLocation presetId = ResourceLocationArgument.getId(context, "name");
    MinecraftServer server = context.getSource().getServer();

    // locate and parse the preset
    Optional<Resource> resource = server.getResourceManager().getResource(PRESETS.idToFile(presetId));
    if (resource.isEmpty()) {
      throw PRESET_NOT_FOUND.create(presetId);
    }
    IJsonPredicate<net.minecraft.world.item.Item> input;
    IJsonPredicate<net.minecraft.world.item.Item> result;
    List<RecipeType<?>> types;
    try (Reader reader = resource.get().openAsReader()) {
      JsonObject json = GsonHelper.parse(reader);
      input = ItemPredicate.LOADER.getIfPresent(json, "input");
      result = ItemPredicate.LOADER.getIfPresent(json, "result");
      types = RECIPE_TYPES.getIfPresent(json, "recipe_type");
    } catch (IOException | RuntimeException e) {
      slimeknights.mantle.Mantle.logger.error("Failed parsing remove recipes preset {}", presetId, e);
      throw PRESET_INVALID.create(presetId);
    }

    // scan the recipe manager for matches
    RegistryAccess access = server.registryAccess();
    Path packRoot = GeneratePackHelper.getDatapackPath(server);
    int removed = 0;
    for (RecipeHolder<?> holder : server.getRecipeManager().getRecipes()) {
      if (!types.contains(holder.value().getType())) {
        continue;
      }
      ItemStack resultStack = holder.value().getResultItem(access);
      if (resultStack.isEmpty() || !result.matches(resultStack.getItem())) {
        continue;
      }
      if (!matchesInput(holder.value().getIngredients(), input)) {
        continue;
      }
      // disable by shadowing the recipe with an empty conditional, dropped by the condition filter on load
      JsonObject disable = new JsonObject();
      disable.addProperty("type", "forge:conditional");
      disable.add("recipes", new com.google.gson.JsonArray());
      ResourceLocation id = holder.id();
      if (GeneratePackHelper.saveJson(disable, packRoot.resolve("data/" + id.getNamespace() + "/recipe/" + id.getPath() + ".json"))) {
        removed++;
      }
    }
    GeneratePackHelper.saveMcmeta(packRoot);

    int finalRemoved = removed;
    context.getSource().sendSuccess(() -> Component.translatable("command.mantle.remove_recipes.success", finalRemoved, GeneratePackHelper.getOutputComponent(packRoot)), true);
    return removed;
  }

  /** Checks whether any ingredient matches the input predicate; ingredient-less recipes only pass the any-predicate */
  private static boolean matchesInput(List<Ingredient> ingredients, IJsonPredicate<net.minecraft.world.item.Item> input) {
    if (input == ItemPredicate.ANY) {
      return true;
    }
    for (Ingredient ingredient : ingredients) {
      for (ItemStack stack : ingredient.getItems()) {
        if (input.matches(stack.getItem())) {
          return true;
        }
      }
    }
    return false;
  }
}
