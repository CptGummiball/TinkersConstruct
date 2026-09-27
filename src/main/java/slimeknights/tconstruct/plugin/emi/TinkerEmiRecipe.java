package slimeknights.tconstruct.plugin.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.transfer.fluid.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base for Tinkers' EMI recipes: carries the category, id and dimensions, and converts between
 * Tinkers' millibucket fluid amounts and EMI's droplets (81 droplets to the millibucket, the
 * Fabric transfer convention EMI displays in).
 */
public abstract class TinkerEmiRecipe implements EmiRecipe {
  private final EmiRecipeCategory category;
  @Nullable
  private final ResourceLocation id;
  private final int width;
  private final int height;

  protected TinkerEmiRecipe(EmiRecipeCategory category, @Nullable ResourceLocation id, int width, int height) {
    this.category = category;
    this.id = id;
    this.width = width;
    this.height = height;
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return category;
  }

  @Nullable
  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public int getDisplayWidth() {
    return width;
  }

  @Override
  public int getDisplayHeight() {
    return height;
  }

  /** Converts a Tinkers fluid stack to an EMI stack, millibuckets to droplets */
  public static EmiStack fluid(FluidStack stack) {
    return EmiStack.of(stack.getFluid(), stack.getAmount() * 81L);
  }

  /** Converts a list of Tinkers fluid stacks to one cycling EMI ingredient */
  public static EmiIngredient fluids(List<FluidStack> stacks) {
    return EmiIngredient.of(stacks.stream().map(s -> (EmiIngredient) fluid(s)).toList());
  }

  /** Converts a list of item stacks to one cycling EMI ingredient */
  public static EmiIngredient items(List<net.minecraft.world.item.ItemStack> stacks) {
    return EmiIngredient.of(stacks.stream().map(s -> (EmiIngredient) EmiStack.of(s)).toList());
  }

  /** Converts a millibucket capacity to droplets for a tank widget */
  public static int capacity(int mb) {
    return mb * 81;
  }

  /** Centered grey text, the way the JEI categories drew their headers */
  static void addCenteredText(dev.emi.emi.api.widget.WidgetHolder widgets, net.minecraft.network.chat.Component text, int centerX, int y) {
    widgets.addText(text, centerX - net.minecraft.client.Minecraft.getInstance().font.width(text) / 2, y, 0xFF808080, false);
  }

  /** Entity instances created for the spinning display, cached per type across all recipes */
  private static final java.util.Map<net.minecraft.world.entity.EntityType<?>,net.minecraft.world.entity.Entity> RENDER_ENTITIES = new java.util.HashMap<>();

  /**
   * Adds the spinning-entity box the JEI categories drew, cycling through the given types.
   * Non-living entities stay invisible; the egg slot underneath still identifies them.
   */
  static void addEntityRenderer(dev.emi.emi.api.widget.WidgetHolder widgets, List<net.minecraft.world.entity.EntityType<?>> types, int x, int y) {
    if (types.isEmpty()) {
      return;
    }
    widgets.addDrawable(x, y, 32, 32, (graphics, mouseX, mouseY, delta) -> {
      net.minecraft.world.entity.EntityType<?> type = types.get((int) (System.currentTimeMillis() / 2000 % types.size()));
      net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
      net.minecraft.world.entity.Entity entity = minecraft.level == null ? null
        : RENDER_ENTITIES.computeIfAbsent(type, t -> t.create(minecraft.level));
      if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
        float scale = 32f / Math.max(1f, Math.max(living.getBbWidth(), living.getBbHeight()));
        net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory(graphics, 16, 30, (int) scale,
          new org.joml.Vector3f(), new org.joml.Quaternionf().rotationXYZ(0.43633232F, (float) Math.toRadians(180 - (System.currentTimeMillis() / 20 % 360)), (float) Math.PI), null, living);
      }
    });
  }
}
