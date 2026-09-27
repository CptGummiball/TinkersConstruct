package slimeknights.tconstruct.plugin.emi;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolTraitHook;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe.X_OFFSET;
import static slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe.Y_OFFSET;

/**
 * Tool building: the station layout's part slots over a giant translucent render of the tool,
 * with every material variant cycling in sync between parts and result.
 */
public class ToolBuildingEmiRecipe extends TinkerEmiRecipe {
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final int WIDTH = 134;
  private static final int HEIGHT = 66;

  private final ToolBuildingRecipe recipe;
  private final List<List<ItemStack>> partsAndExtras;

  public ToolBuildingEmiRecipe(dev.emi.emi.api.recipe.EmiRecipeCategory category, ToolBuildingRecipe recipe) {
    super(category, null, WIDTH, HEIGHT);
    this.recipe = recipe;
    List<List<ItemStack>> parts = Stream.concat(recipe.getAllToolParts().stream(),
      recipe.getExtraRequirements().stream().map(ingredient -> Arrays.asList(ingredient.getItems()))).toList();
    // pad to the layout so every slot has something to show
    int missing = recipe.getLayoutSlots().size() - parts.size();
    if (missing > 0) {
      List<List<ItemStack>> padded = new ArrayList<>(parts);
      for (int i = 0; i < missing; i++) {
        padded.add(List.of(ItemStack.EMPTY));
      }
      parts = padded;
    }
    this.partsAndExtras = parts;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    List<EmiIngredient> inputs = new ArrayList<>();
    for (List<ItemStack> part : partsAndExtras) {
      inputs.add(items(part));
    }
    for (ItemStack hidden : recipe.getHiddenInputs()) {
      inputs.add(EmiStack.of(hidden));
    }
    return inputs;
  }

  @Override
  public List<EmiStack> getOutputs() {
    return recipe.getDisplayOutput().stream().map(EmiStack::of).toList();
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND, 0, 0, WIDTH, HEIGHT, 122, 77);

    // giant ghost of the tool being built, dimmed by the cover texture over it
    ItemStack outputStack = recipe.getOutput() instanceof IModifiableDisplay modifiable ? modifiable.getRenderTool() : recipe.getOutput().asItem().getDefaultInstance();
    List<LayoutSlot> layoutSlots = recipe.getLayoutSlots();
    widgets.addDrawable(0, 0, WIDTH, HEIGHT, (graphics, mouseX, mouseY, delta) -> {
      var pose = graphics.pose();
      pose.pushPose();
      pose.translate(5, 6.5, 0);
      pose.scale(3.7f, 3.7f, 1.0f);
      graphics.renderItem(outputStack, 0, 0);
      pose.popPose();

      RenderSystem.enableBlend();
      RenderSystem.disableDepthTest();
      RenderSystem.setShaderColor(1, 1, 1, 0.82f);
      graphics.blit(BACKGROUND, 5, 6, 122, 77, 70, 60);
      RenderSystem.setShaderColor(1, 1, 1, 0.28f);
      for (LayoutSlot slot : layoutSlots) {
        graphics.blit(BACKGROUND, slot.getX() + X_OFFSET - 1, slot.getY() + Y_OFFSET - 1, 144, 59, 18, 18);
      }
      RenderSystem.setShaderColor(1, 1, 1, 1);
      for (LayoutSlot slot : layoutSlots) {
        graphics.blit(BACKGROUND, slot.getX() + X_OFFSET - 1, slot.getY() + Y_OFFSET - 1, 162, 59, 18, 18);
      }
    });

    // part inputs on the station layout; same-size lists cycle in sync with the output
    for (int i = 0; i < layoutSlots.size() && i < partsAndExtras.size(); i++) {
      widgets.addSlot(items(partsAndExtras.get(i)), layoutSlots.get(i).getX() + X_OFFSET - 1, layoutSlots.get(i).getY() + Y_OFFSET - 1).drawBack(false);
    }

    widgets.addSlot(items(recipe.getDisplayOutput()), WIDTH - 27, 22).drawBack(false).recipeContext(this);
  }
}
