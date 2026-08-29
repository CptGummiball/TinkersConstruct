package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableIngredientSerializer;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.transfer.TransferUtil;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.mantle.transfer.fluid.IFluidHandler.FluidAction;
import slimeknights.mantle.transfer.fluid.IFluidHandlerItem;
import slimeknights.mantle.transfer.item.ItemHandlerHelper;
import slimeknights.mantle.util.typed.TypedMap;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Ingredient that matches a container of fluid */
@SuppressWarnings("unused")  // API
public class FluidContainerIngredient implements CustomIngredient {
  public static final ResourceLocation ID = Mantle.getResource("fluid_container");
  public static final LoadableIngredientSerializer<FluidContainerIngredient> SERIALIZER = new LoadableIngredientSerializer<>(ID, RecordLoadable.create(
    FluidField.INSTANCE,
    IngredientLoadable.ALLOW_EMPTY.nullableField("display", i -> i.display),
    FluidContainerIngredient::new));

  /** Ingredient to use for matching */
  private final FluidIngredient fluidIngredient;
  /** Internal ingredient to display the ingredient recipe viewers */
  @Nullable
  private final Ingredient display;
  private List<ItemStack> displayStacks;
  protected FluidContainerIngredient(FluidIngredient fluidIngredient, @Nullable Ingredient display) {
    this.fluidIngredient = fluidIngredient;
    this.display = display;
  }

  /** Creates an instance from a fluid ingredient with a display container, as a vanilla-usable ingredient */
  public static Ingredient fromIngredient(FluidIngredient ingredient, Ingredient display) {
    return new FluidContainerIngredient(ingredient, display).toVanilla();
  }

  /** Creates an instance from a fluid ingredient with no display, not recommended */
  public static Ingredient fromIngredient(FluidIngredient ingredient) {
    return new FluidContainerIngredient(ingredient, null).toVanilla();
  }

  /** Creates an instance from a fluid object with its bucket as the display container */
  public static Ingredient fromFluid(FluidObject<?> fluid) {
    return fromIngredient(fluid.ingredient(FluidStack.BUCKET_VOLUME), Ingredient.of(fluid));
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    // first, must have a fluid capability
    return stack != null && !stack.isEmpty() && TransferUtil.getFluidHandlerItem(stack).flatMap(cap -> {
      // second, must contain enough fluid
      if (cap.getTanks() == 1) {
        FluidStack contained = cap.getFluidInTank(0);
        if (!contained.isEmpty() && fluidIngredient.getAmount(contained.getFluid()) == contained.getAmount() && fluidIngredient.test(contained.getFluid())) {
          // so far so good, from this point on we are forced to make copies as we need to try draining, so copy and fetch the copy's cap
          ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
          return TransferUtil.getFluidHandlerItem(copy);
        }
      }
      return Optional.empty();
    }).filter(cap -> {
      // alright, we know it has the fluid, the question is just whether draining the fluid will give us the desired result
      Fluid fluid = cap.getFluidInTank(0).getFluid();
      int amount = fluidIngredient.getAmount(fluid);
      FluidStack drained = cap.drain(amount, FluidAction.EXECUTE);
      // we need an exact match, and we need the resulting container item to be the same as the item stack's remainder
      ItemStack container = cap instanceof IFluidHandlerItem item ? item.getContainer() : ItemStack.EMPTY;
      return drained.getFluid() == fluid && drained.getAmount() == amount && ItemStack.matches(stack.getRecipeRemainder(), container);
    }).isPresent();
  }

  @Override
  public List<ItemStack> getMatchingStacks() {
    if (displayStacks == null) {
      // no container? unfortunately hard to display this recipe so show nothing
      if (display == null) {
        displayStacks = List.of();
      } else {
        displayStacks = List.of(display.getItems());
      }
    }
    return displayStacks;
  }

  @Override
  public boolean requiresTesting() {
    return true;
  }

  @Override
  public CustomIngredientSerializer<?> getSerializer() {
    return SERIALIZER;
  }

  /** Serializes to JSON for datagen */
  public JsonElement toJson() {
    return SERIALIZER.serialize(this);
  }

  /**
   * Field reading the fluid ingredient in the 1.20 format: the fluid ingredient's keys sit
   * flat on the ingredient object (the shape the shipped data files use), with a nested
   * {@code fluid} object as the alternative for ingredient forms that don't serialize to an
   * object.
   */
  private enum FluidField implements RecordField<FluidIngredient,FluidContainerIngredient> {
    INSTANCE;

    @Override
    public FluidIngredient get(JsonObject json, TypedMap context) {
      // if we have fluid and its not a primitive, then its nested
      if (json.has("fluid") && !json.get("fluid").isJsonPrimitive()) {
        return FluidIngredient.LOADABLE.getIfPresent(json, "fluid");
      }
      return FluidIngredient.LOADABLE.convert(json, "fluid");
    }

    @Override
    public void serialize(FluidContainerIngredient parent, JsonObject json) {
      JsonElement element = FluidIngredient.LOADABLE.serialize(parent.fluidIngredient);
      if (element.isJsonObject()) {
        for (Map.Entry<String,JsonElement> entry : element.getAsJsonObject().entrySet()) {
          json.add(entry.getKey(), entry.getValue());
        }
      } else {
        json.add("fluid", element);
      }
    }

    @Override
    public FluidIngredient decode(RegistryFriendlyByteBuf buffer, TypedMap context) {
      return FluidIngredient.LOADABLE.decode(buffer);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, FluidContainerIngredient parent) {
      FluidIngredient.LOADABLE.encode(buffer, parent.fluidIngredient);
    }
  }
}
