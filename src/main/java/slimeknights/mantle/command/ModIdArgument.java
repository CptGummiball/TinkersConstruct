package slimeknights.mantle.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Argument type accepting any loaded mod id, replacing Forge's
 * {@code net.minecraftforge.server.command.ModIdArgument}.
 *
 * <p>Registered in {@code MantleCommand.init}: the server syncs its command tree to the client,
 * and every argument type in the tree must be known to the argument type registry or the client
 * is disconnected while joining.
 */
public class ModIdArgument implements ArgumentType<String> {
  private static final List<String> EXAMPLES = List.of("mantle", "tconstruct");

  public static ModIdArgument modIdArgument() {
    return new ModIdArgument();
  }

  @Override
  public String parse(StringReader reader) {
    return reader.readUnquotedString();
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggest(FabricLoader.getInstance().getAllMods().stream()
      .map(mod -> mod.getMetadata().getId()), builder);
  }

  @Override
  public java.util.Collection<String> getExamples() {
    return EXAMPLES;
  }
}
