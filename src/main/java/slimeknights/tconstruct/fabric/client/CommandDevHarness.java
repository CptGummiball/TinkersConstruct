package slimeknights.tconstruct.fabric.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import slimeknights.tconstruct.TConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Dev-only harness driving the command layer end to end: gives a tool, modifies it through
 * {@code /tconstruct modifiers}, dumps a tag view, generates the part textures (server command to
 * client packet to PNG files) and exports a guide book to images (client command). Asserts the
 * two file outputs exist, screenshots the chat as the visual record, then quits.
 *
 * <p>Enable with {@code -Dtconstruct.commandHarness=true}; the {@code runClientCommands} config
 * does. Exit code 1 when an assertion fails, so the caller can tell without reading the log.
 */
public final class CommandDevHarness {
  private CommandDevHarness() {}

  private static int ticks = 0;
  private static boolean failed = false;

  public static void init() {
    if (!Boolean.getBoolean("tconstruct.commandHarness")) {
      return;
    }
    ClientTickEvents.END_CLIENT_TICK.register(CommandDevHarness::tick);
  }

  private static void tick(Minecraft minecraft) {
    if (minecraft.level == null || minecraft.player == null) {
      return;
    }
    ticks++;
    switch (ticks) {
      case 1 -> {
        minecraft.options.pauseOnLostFocus = false;
        minecraft.options.menuBackgroundBlurriness().set(0);
        // sanity: both command roots registered on the integrated server
        var server = minecraft.getSingleplayerServer();
        if (server == null || server.getCommands().getDispatcher().getRoot().getChild("tconstruct") == null
            || server.getCommands().getDispatcher().getRoot().getChild("mantle") == null) {
          fail(minecraft, "command roots missing on the integrated server");
        } else {
          TConstruct.LOG.info("[commandharness] /tconstruct and /mantle registered");
        }
      }
      case 10 -> minecraft.player.connection.sendCommand("clear @s");
      // the give below lands in the first free slot; make sure that is the held one even
      // when the world save carries an older hotbar selection
      case 15 -> minecraft.player.getInventory().selected = 0;
      case 20 -> minecraft.player.connection.sendCommand("give @s tconstruct:pickaxe");
      case 40 -> minecraft.player.connection.sendCommand("tconstruct modifiers @s add tconstruct:haste 1");
      case 55 -> {
        // the modifier command works on the held tool, so the result is readable right off the stack
        var stack = minecraft.player.getMainHandItem();
        var tag = slimeknights.tconstruct.library.tools.nbt.TagCompat.getTag(stack);
        if (tag == null || !tag.toString().contains("tconstruct:haste")) {
          fail(minecraft, "held tool has no haste modifier after /tconstruct modifiers add; held: " + stack + " tag: " + tag);
        } else {
          TConstruct.LOG.info("[commandharness] modifier applied to held tool: {}", stack.getHoverName().getString());
        }
      }
      case 60 -> minecraft.player.connection.sendCommand("mantle tags view minecraft:item minecraft:planks");
      case 80 -> minecraft.player.connection.sendCommand("tconstruct generate part_textures all");
      case 240 -> minecraft.player.connection.sendCommand("mantle_client book export_images tconstruct:puny_smelting");
      case 260 -> minecraft.player.connection.sendCommand("mantle remove_recipes preset tconstruct:vanilla_tools");
      case 700 -> {
        File gameDir = minecraft.gameDirectory;
        checkPngs(minecraft, new File(gameDir, "resourcepacks/TinkersConstructGeneratedPartTextures"), "part texture generator");
        checkPngs(minecraft, new File(gameDir, "screenshots/mantle_book/tconstruct/puny_smelting"), "book image export");
        // recipe remover: the preset disables vanilla tool recipes into the generated pack
        var server = minecraft.getSingleplayerServer();
        if (server != null) {
          File removed = slimeknights.mantle.command.GeneratePackHelper.getDatapackPath(server).resolve("data/minecraft/recipe/iron_pickaxe.json").toFile();
          if (removed.isFile()) {
            TConstruct.LOG.info("[commandharness] recipe remover wrote {}", removed);
          } else {
            fail(minecraft, "recipe remover did not write " + removed);
          }
        }
      }
      case 720 -> minecraft.setScreen(new net.minecraft.client.gui.screens.ChatScreen(""));
      case 725 -> Screenshot.grab(minecraft.gameDirectory, "commands_chat.png", minecraft.getMainRenderTarget(),
                                  component -> TConstruct.LOG.info("[commandharness] {}", component.getString()));
      case 745 -> {
        TConstruct.LOG.info(failed ? "[commandharness] FAIL" : "[commandharness] PASS");
        minecraft.stop();
      }
    }
  }

  /** Asserts the directory exists and holds at least one png below it */
  private static void checkPngs(Minecraft minecraft, File dir, String what) {
    long count = 0;
    if (dir.isDirectory()) {
      try (Stream<Path> files = Files.walk(dir.toPath())) {
        count = files.filter(f -> f.toString().endsWith(".png")).count();
      } catch (IOException e) {
        TConstruct.LOG.error("[commandharness] failed walking {}", dir, e);
      }
    }
    if (count == 0) {
      fail(minecraft, what + " wrote no files to " + dir);
    } else {
      TConstruct.LOG.info("[commandharness] {} wrote {} png files", what, count);
    }
  }

  private static void fail(Minecraft minecraft, String message) {
    failed = true;
    TConstruct.LOG.error("[commandharness] {}", message);
  }
}
