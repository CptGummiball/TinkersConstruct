package slimeknights.mantle.command.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists which resource packs provide a client asset, mirroring {@code /mantle sources data} for
 * the client's resource manager.
 *
 * <p>PORT: upstream shared its row type and printer with the server command; Fabric client
 * commands dispatch on their own source type, so the printer lives here standalone.
 */
public class ClientSourcesCommand {
  /** Named asset folders registered for direct lookup */
  private static final List<SourceFolder> FOLDERS = new ArrayList<>();

  /** Registers a named folder shortcut, such as {@code item_models} for {@code models/item} */
  public static void register(String argument, String folder, String extension, SuggestionProvider<FabricClientCommandSource> suggestionProvider) {
    FOLDERS.add(new SourceFolder(argument, folder, extension, suggestionProvider));
  }

  /** Registers this command with the builder */
  public static void register(LiteralArgumentBuilder<FabricClientCommandSource> subCommand) {
    subCommand.then(ClientCommandManager.literal("path")
      .then(ClientCommandManager.argument("path", ResourceLocationArgument.id())
        .executes(context -> run(context, context.getArgument("path", ResourceLocation.class)))));
    for (SourceFolder source : FOLDERS) {
      subCommand.then(ClientCommandManager.literal(source.argument())
        .then(ClientCommandManager.argument("id", ResourceLocationArgument.id()).suggests(source.suggestionProvider())
          .executes(context -> {
            ResourceLocation id = context.getArgument("id", ResourceLocation.class);
            return run(context, id.withPath(source.folder() + '/' + id.getPath() + source.extension()));
          })));
    }
  }

  /** Lists every pack providing the given path */
  private static int run(CommandContext<FabricClientCommandSource> context, ResourceLocation path) {
    ResourceManager manager = Minecraft.getInstance().getResourceManager();
    List<Resource> resources = manager.getResourceStack(path);
    if (resources.isEmpty()) {
      context.getSource().sendError(Component.translatable("command.mantle.sources.not_found", path));
      return 0;
    }
    context.getSource().sendFeedback(Component.translatable("command.mantle.sources.success", path, resources.size()));
    for (Resource resource : resources) {
      context.getSource().sendFeedback(Component.literal(" * " + resource.sourcePackId()));
    }
    return resources.size();
  }

  /** Row for a registered folder shortcut */
  private record SourceFolder(String argument, String folder, String extension, SuggestionProvider<FabricClientCommandSource> suggestionProvider) {}
}
