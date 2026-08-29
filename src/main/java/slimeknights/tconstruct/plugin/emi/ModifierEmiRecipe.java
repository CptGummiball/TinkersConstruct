package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adding a modifier at the tinker station or anvil: the five station inputs around the tool, the
 * modifier named at the top, level bounds, slot cost, and the requirement and incremental flags.
 */
public class ModifierEmiRecipe extends TinkerEmiRecipe {
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final String KEY_MIN = TConstruct.makeTranslationKey("jei", "modifiers.level.min");
  private static final String KEY_MAX = TConstruct.makeTranslationKey("jei", "modifiers.level.max");
  private static final String KEY_RANGE = TConstruct.makeTranslationKey("jei", "modifiers.level.range");
  private static final String KEY_EXACT = TConstruct.makeTranslationKey("jei", "modifiers.level.exact");
  private static final String KEY_SLOT = TConstruct.makeTranslationKey("jei", "modifiers.slot");
  private static final String KEY_SLOTS = TConstruct.makeTranslationKey("jei", "modifiers.slots");
  private static final Component TEXT_FREE = TConstruct.makeTranslation("jei", "modifiers.free");

  /** Positions of the five station input slots, outer 18x18 coordinates */
  private static final int[][] INPUTS = {{2, 32}, {24, 14}, {46, 32}, {42, 57}, {6, 57}};

  /** Cache of sprite for each slot type, resolved through the creative slot item's data-keyed model */
  private static final Map<String,TextureAtlasSprite> SLOT_SPRITES = new HashMap<>();

  private final IDisplayModifierRecipe recipe;

  public ModifierEmiRecipe(dev.emi.emi.api.recipe.EmiRecipeCategory category, IDisplayModifierRecipe recipe) {
    super(category, recipe.getRecipeId(), 128, 77);
    this.recipe = recipe;
  }

  @Override
  public List<EmiIngredient> getInputs() {
    List<EmiIngredient> inputs = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      inputs.add(items(recipe.getDisplayItems(i)));
    }
    return inputs;
  }

  @Override
  public List<EmiIngredient> getCatalysts() {
    return List.of(items(recipe.getToolWithoutModifier()));
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(new ModifierEmiStack(recipe.getDisplayResult()));
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    widgets.addTexture(BACKGROUND, 0, 0, 128, 77, 0, 0);

    // the five station inputs; an empty one shows its purpose icon instead
    for (int i = 0; i < 5; i++) {
      List<net.minecraft.world.item.ItemStack> stacks = recipe.getDisplayItems(i);
      if (stacks.isEmpty()) {
        widgets.addTexture(BACKGROUND, INPUTS[i][0] + 1, INPUTS[i][1] + 1, 16, 16, 128 + i * 16, 0);
      } else {
        widgets.addSlot(items(stacks), INPUTS[i][0], INPUTS[i][1]).drawBack(false);
      }
    }

    // modifier banner: icon at the left, level-aware name centered over the top bar
    ModifierEntry result = recipe.getDisplayResult();
    widgets.addSlot(new ModifierEmiStack(result), 2, 2).drawBack(false).recipeContext(this);
    addCenteredText(widgets, result.getDisplayName(), 72, 6);

    // info icons: unmet requirements and incremental
    if (result.getHook(ModifierHooks.REQUIREMENTS).requirementsError(result) != null) {
      widgets.addTexture(BACKGROUND, 66, 58, 16, 16, 128, 17);
      Component error = result.getHook(ModifierHooks.REQUIREMENTS).requirementsError(result);
      if (error != null) {
        widgets.addTooltipText(List.of(error), 66, 58, 16, 16);
      }
    }
    if (recipe.isIncremental()) {
      widgets.addTexture(BACKGROUND, 83, 59, 16, 16, 128, 33);
    }

    // level bounds
    Component levelText = null;
    Component variant = recipe.getVariant();
    if (variant != null) {
      levelText = variant;
    } else {
      IntRange level = recipe.getLevel();
      int min = level.min();
      int max = level.max();
      if (min == 1) {
        if (max < ModifierEntry.VALID_LEVEL.max()) {
          levelText = Component.translatable(KEY_MAX, max);
        }
      } else if (min == max) {
        levelText = Component.translatable(KEY_EXACT, min);
      } else if (max == ModifierEntry.VALID_LEVEL.max()) {
        levelText = Component.translatable(KEY_MIN, min);
      } else {
        levelText = Component.translatable(KEY_RANGE, min, max);
      }
    }
    if (levelText != null) {
      addCenteredText(widgets, levelText, 86, 16);
    }

    // slot cost, or the slotless icon
    var slots = recipe.getSlots();
    widgets.addDrawable(102, 58, 24, 16, (graphics, mouseX, mouseY, delta) -> drawSlotCost(graphics, slots));
    List<Component> slotTooltip;
    if (slots == null) {
      slotTooltip = List.of(TEXT_FREE);
    } else if (slots.count() == 1) {
      slotTooltip = List.of(Component.translatable(KEY_SLOT, slots.type().getDisplayName()));
    } else {
      slotTooltip = List.of(Component.translatable(KEY_SLOTS, slots.count(), slots.type().getDisplayName()));
    }
    widgets.addTooltipText(slotTooltip, 102, 58, 24, 16);

    // the tool, before in the middle and after on the right
    widgets.addSlot(items(recipe.getToolWithoutModifier()), 24, 37).drawBack(false).catalyst(true);
    widgets.addSlot(items(recipe.getToolWithModifier()), 104, 33).drawBack(false).recipeContext(this);
  }

  /** Draws the slot count and its slot type sprite, mirroring the JEI slot ingredient renderer */
  private static void drawSlotCost(GuiGraphics graphics, @Nullable slimeknights.tconstruct.library.tools.SlotType.SlotCount slots) {
    var font = Minecraft.getInstance().font;
    if (slots != null) {
      String text = Integer.toString(slots.count());
      graphics.drawString(font, text, 9 - font.width(text), 5, 0xFF808080, false);
    }
    SlotType type = slots == null ? null : slots.type();
    TextureAtlasSprite sprite = SLOT_SPRITES.computeIfAbsent(type == null ? "slotless" : type.getName(), ModifierEmiRecipe::lookupSprite);
    graphics.blit(8, 0, 0, 16, 16, sprite);
  }

  /** Resolves the sprite for a slot type through the creative slot item's data-keyed model */
  private static TextureAtlasSprite lookupSprite(String name) {
    Minecraft minecraft = Minecraft.getInstance();
    ModelManager modelManager = minecraft.getModelManager();
    BakedModel model = minecraft.getItemRenderer().getItemModelShaper().getItemModel(TinkerModifiers.creativeSlotItem.get());
    if (model != null && model.getOverrides() instanceof NBTKeyModel.Overrides overrides) {
      Material material = overrides.getTexture(name);
      return modelManager.getAtlas(material.atlasLocation()).getSprite(material.texture());
    }
    return modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(MissingTextureAtlasSprite.getLocation());
  }
}
