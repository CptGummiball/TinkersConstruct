package slimeknights.tconstruct.fabric.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.recipe.helper.TagPreference;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dev-only harness for the pack compatibility check: with the test environment's mods loaded
 * (unify, oritech, energized power — {@code -PcompatMods} points gradle at their jars), it joins
 * a world and answers the question the pack maintainer cares about: does a steel ingot cast in
 * the smeltery come out as the same item every other mod's steel recipes produce once Unify has
 * rewritten them?
 *
 * <p>Tinkers' own outputs resolve through mantle's tag preference rather than Unify, so the two
 * unifiers can disagree; this prints both verdicts and fails loudly when they differ.
 */
public final class CompatDevHarness {
  private CompatDevHarness() {}

  private static int ticks = 0;

  public static void init() {
    if (!Boolean.getBoolean("tconstruct.compatHarness")) {
      return;
    }
    ClientTickEvents.END_CLIENT_TICK.register(CompatDevHarness::tick);
  }

  private static void tick(Minecraft minecraft) {
    if (minecraft.level == null || minecraft.player == null) {
      return;
    }
    ticks++;
    if (ticks == 1) {
      minecraft.options.pauseOnLostFocus = false;
      for (String mod : new String[] {"unify", "oritech", "energizedpower"}) {
        TConstruct.LOG.info("[compatharness] mod {}: {}", mod, FabricLoader.getInstance().isModLoaded(mod) ? "loaded" : "MISSING");
      }
      for (String id : new String[] {"oritech:steel_ingot", "energizedpower:steel_ingot", "tconstruct:steel_ingot"}) {
        TConstruct.LOG.info("[compatharness] item {}: {}", id, BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(id)) ? "present" : "absent");
      }
    }
    // recipes and tags are synced by now
    if (ticks == 60) {
      RecipeManager manager = minecraft.level.getRecipeManager();
      var access = minecraft.level.registryAccess();

      // what mantle's tag preference picks for steel, which is what tinkers' tag-based outputs use
      TagKey<Item> steelTag = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/steel"));
      String tagWinner = TagPreference.getPreference(steelTag).map(item -> BuiltInRegistries.ITEM.getKey(item).toString()).orElse("none");
      TConstruct.LOG.info("[compatharness] tag preference for #c:ingots/steel: {}", tagWinner);

      // what the tinkers casting recipe actually outputs
      String castOutput = "none";
      for (RecipeHolder<?> holder : manager.getAllRecipesFor(TinkerRecipeTypes.CASTING_TABLE.get())) {
        if (holder.id().getPath().contains("metal/steel/ingot")) {
          ItemStack result = holder.value().getResultItem(access);
          castOutput = BuiltInRegistries.ITEM.getKey(result.getItem()).toString();
          TConstruct.LOG.info("[compatharness] tinkers casting {} -> {}", holder.id(), castOutput);
        }
      }

      // what other mods' steel recipes output after unify's load-time rewrite
      Map<String,Integer> craftOutputs = new LinkedHashMap<>();
      for (RecipeHolder<?> holder : manager.getAllRecipesFor(RecipeType.CRAFTING)) {
        ItemStack result;
        try {
          result = holder.value().getResultItem(access);
        } catch (Exception e) {
          continue;
        }
        if (!result.isEmpty()) {
          ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.getItem());
          if (id.getPath().equals("steel_ingot")) {
            craftOutputs.merge(id.toString(), 1, Integer::sum);
          }
        }
      }
      TConstruct.LOG.info("[compatharness] steel_ingot outputs across crafting recipes: {}", craftOutputs);

      boolean consistent = craftOutputs.size() <= 1
        && (craftOutputs.isEmpty() || castOutput.equals("none") || craftOutputs.containsKey(castOutput));
      if (castOutput.equals("none")) {
        TConstruct.LOG.warn("[compatharness] no tinkers steel casting recipe loaded — check the compat recipe conditions");
      }
      TConstruct.LOG.info(consistent ? "[compatharness] PASS — one steel to rule them all: {}"
                                     : "[compatharness] FAIL — the unifiers disagree: cast={} vs crafting={}",
                          consistent ? (craftOutputs.isEmpty() ? castOutput : craftOutputs.keySet().iterator().next()) : castOutput,
                          craftOutputs);
    }
    if (ticks == 80) {
      minecraft.stop();
    }
  }
}
