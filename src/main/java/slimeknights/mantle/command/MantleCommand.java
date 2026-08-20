package slimeknights.mantle.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.GameRules;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.command.argument.TagSourceArgument;
import slimeknights.mantle.registration.deferred.ArgumentTypeDeferredRegister;

import java.util.function.Consumer;

/**
 * Root command for all commands in Mantle.
 *
 * <p>PORT: registration moved from Forge's {@code RegisterCommandsEvent} to Fabric's
 * {@link CommandRegistrationCallback}. Of the upstream subcommands, the tags family is here in
 * full. The rest stayed out with the systems they front: {@code dump_loot_modifiers} (global loot
 * modifiers are a Forge feature this port replaced with plain loot table injects),
 * {@code harvest_tiers} (Forge's tier sorting registry does not exist on Fabric),
 * {@code remove}/{@code hunger} (datapack editing utilities nothing in
 * Tinkers calls; add with their command classes if ever needed).
 */
public class MantleCommand {
  /** Permission level that allows a user to build in spawn protected areas */
  public static final int PERMISSION_EDIT_SPAWN = 1;
  /** Permission level that can run standard game commands, used by command blocks and functions */
  public static final int PERMISSION_GAME_COMMANDS = 2;
  /** Standard permission level for server operators */
  public static final int PERMISSION_PLAYER_COMMANDS = 3;
  /** Permission level for the server owner, server console, or the player in single player */
  public static final int PERMISSION_OWNER = 4;

  /** Argument types in Mantle's command trees; anything the server syncs to a client must be registered */
  private static final ArgumentTypeDeferredRegister ARGUMENT_TYPES = new ArgumentTypeDeferredRegister(Mantle.modId);

  /** Registers all Mantle command related content */
  public static void init() {
    ARGUMENT_TYPES.registerSingleton("mod_id", ModIdArgument.class, ModIdArgument::modIdArgument);
    RegistryArgument.registerSuggestions();
    TagSourceArgument.registerSuggestions();

    // register interesting sources; loot tables moved into the datapack registries in 1.21
    SourcesCommand.register("loot_tables", (context, builder)
      -> net.minecraft.commands.SharedSuggestionProvider.suggestResource(context.getSource().getServer().reloadableRegistries().getKeys(net.minecraft.core.registries.Registries.LOOT_TABLE), builder));
    SourcesCommand.register("recipes", (context, builder)
      -> net.minecraft.commands.SharedSuggestionProvider.suggestResource(context.getSource().getRecipeNames(), builder));

    CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> registerCommand(dispatcher, buildContext));
  }

  /** Registers a sub command for the root Mantle command */
  private static void register(LiteralArgumentBuilder<CommandSourceStack> root, String name, Consumer<LiteralArgumentBuilder<CommandSourceStack>> consumer) {
    LiteralArgumentBuilder<CommandSourceStack> subCommand = Commands.literal(name);
    consumer.accept(subCommand);
    root.then(subCommand);
  }

  /** Builds and registers the Mantle command */
  private static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
    LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("mantle");

    // sub commands
    register(builder, "tags", b -> {
      register(b, "view", ViewTagCommand::register);
      register(b, "entries", DumpTagCommand::register);
      register(b, "dump", DumpAllTagsCommand::register);
      register(b, "for", TagsForCommand::register);
      register(b, "preference", TagPreferenceCommand::register);
    });
    register(builder, "sources", b -> register(b, "data", SourcesCommand::register));

    // register final command
    dispatcher.register(builder);
  }

  /* Helpers */

  /**
   * Returns true if the source either does not have reduced debug info or they have the proper level.
   * Allows limiting a command that prints debug info to not work in reduced debug info
   * @param source             Command source
   * @param reducedDebugLevel  Level to use when reduced debug info is true
   * @return  True if the command can be run
   */
  public static boolean requiresDebugInfoOrOp(CommandSourceStack source, int reducedDebugLevel) {
    return !source.getLevel().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO) || source.hasPermission(reducedDebugLevel);
  }
}
