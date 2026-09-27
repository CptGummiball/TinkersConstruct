package slimeknights.mantle.command.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.book.BookLoader;

import java.util.function.Consumer;

/**
 * Root client command for Mantle: the book utilities and the asset source listing.
 *
 * <p>PORT: Fabric client commands dispatch on {@link FabricClientCommandSource} rather than the
 * vanilla stack, so every class under this package is typed to it. The suggestion providers are
 * plain constants instead of entries in {@code SuggestionProviders}: that registry exists to
 * serialize server-side providers across the wire, and a client-only command never crosses it.
 */
public class MantleClientCommand {
  /** Suggestion provider that lists registered book ids */
  public static final SuggestionProvider<FabricClientCommandSource> REGISTERED_BOOKS =
    (context, builder) -> SharedSuggestionProvider.suggestResource(BookLoader.getAllBooks(), builder);
  /** Suggestion provider that lists registered book domains */
  public static final SuggestionProvider<FabricClientCommandSource> REGISTERED_BOOK_DOMAINS =
    (context, builder) -> SharedSuggestionProvider.suggest(BookLoader.getAllBooks().stream().map(ResourceLocation::getNamespace).distinct(), builder);

  /** Registers all Mantle client command related content */
  public static void init() {
    // source command suggestions
    ClientSourcesCommand.register("item_models", "models/item", ".json", (context, builder)
      -> SharedSuggestionProvider.suggestResource(BuiltInRegistries.ITEM.keySet(), builder));

    ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> registerCommand(dispatcher));
  }

  /** Registers a sub command for the root Mantle client command */
  private static void register(LiteralArgumentBuilder<FabricClientCommandSource> root, String name, Consumer<LiteralArgumentBuilder<FabricClientCommandSource>> consumer) {
    LiteralArgumentBuilder<FabricClientCommandSource> subCommand = ClientCommandManager.literal(name);
    consumer.accept(subCommand);
    root.then(subCommand);
  }

  /** Builds and registers the Mantle client command */
  private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
    // PORT: upstream shared the /mantle root with the server command; Fabric consumes any
    // command whose root literal is registered client-side, so sharing would swallow the
    // server half (/mantle tags ...). Hence a root of its own.
    LiteralArgumentBuilder<FabricClientCommandSource> builder = ClientCommandManager.literal("mantle_client");

    // sub commands
    register(builder, "book", BookCommand::register);
    register(builder, "clear_book_cache", ClearBookCacheCommand::register);
    register(builder, "sources", b -> register(b, "assets", ClientSourcesCommand::register));

    // register final command
    dispatcher.register(builder);
  }
}
