package slimeknights.tconstruct.fabric.client;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.plugin.emi.TConstructEmiPlugin;

import java.util.List;

/**
 * Dev-only harness for the EMI integration: joins a world, opens every Tinkers recipe category in
 * EMI's recipe screen and photographs each. Only referenced when {@code -Dtconstruct.emiHarness}
 * is set — the bootstrap checks the flag before touching this class, since it links against EMI.
 */
public final class EmiDevHarness {
  private EmiDevHarness() {}

  private static final List<EmiRecipeCategory> CATEGORIES = List.of(
    TConstructEmiPlugin.CASTING_TABLE, TConstructEmiPlugin.CASTING_BASIN, TConstructEmiPlugin.MELTING,
    TConstructEmiPlugin.FOUNDRY, TConstructEmiPlugin.ALLOY, TConstructEmiPlugin.ENTITY_MELTING,
    TConstructEmiPlugin.MOLDING, TConstructEmiPlugin.MODIFIERS, TConstructEmiPlugin.MODIFIER_WORKTABLE,
    TConstructEmiPlugin.TOOL_BUILDING, TConstructEmiPlugin.PART_BUILDER, TConstructEmiPlugin.SEVERING);

  private static int ticks = 0;
  private static int category = 0;

  public static void init() {
    ClientTickEvents.END_CLIENT_TICK.register(EmiDevHarness::tick);
  }

  private static void tick(Minecraft minecraft) {
    if (minecraft.level == null || minecraft.player == null) {
      return;
    }
    ticks++;
    if (ticks == 1) {
      minecraft.options.pauseOnLostFocus = false;
      minecraft.options.menuBackgroundBlurriness().set(0);
      minecraft.getToasts().clear();
    }
    // give EMI time to reload its index after join, then page through the categories
    if (ticks == 200) {
      minecraft.setScreen(new InventoryScreen(minecraft.player));
    }
    if (ticks >= 240 && category < CATEGORIES.size()) {
      int step = (ticks - 240) % 30;
      if (step == 0) {
        EmiApi.displayRecipeCategory(CATEGORIES.get(category));
      } else if (step == 20) {
        Screenshot.grab(minecraft.gameDirectory, "emi_" + CATEGORIES.get(category).getId().getPath() + ".png",
                        minecraft.getMainRenderTarget(), component -> TConstruct.LOG.info("[emiharness] {}", component.getString()));
        category++;
      }
    }
    if (category >= CATEGORIES.size() && ticks % 30 == 25) {
      TConstruct.LOG.info("[emiharness] PASS, photographed {} categories", category);
      minecraft.stop();
    }
  }
}
