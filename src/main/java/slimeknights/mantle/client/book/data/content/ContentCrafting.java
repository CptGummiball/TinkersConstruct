package slimeknights.mantle.client.book.data.content;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.ArrowElement;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.SlotElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.client.screen.book.ArrowButton.ArrowType;
import slimeknights.mantle.util.html.HtmlElement;
import slimeknights.mantle.util.html.HtmlGroup;
import slimeknights.mantle.util.html.HtmlSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A crafting table recipe, either named by id or spelled out in the page.
 *
 * <p>Naming the recipe keeps the book honest: if the recipe changes, the page follows. The explicit
 * {@code grid}/{@code result} form exists for the handful of pages that illustrate something the
 * recipe system does not model, such as repairing a damaged tool.
 */
public class ContentCrafting extends PageContent {
  public static final transient ResourceLocation ID = Mantle.getResource("crafting");

  private static final transient int SLOT = 18;

  @Nullable
  public String title = null;
  /** Recipe to display; resolved against the client's recipe manager */
  @Nullable
  public ResourceLocation recipe = null;
  /** "small" for a two by two grid, anything else for three by three */
  @SerializedName("grid_size")
  public String gridSize = "large";
  /** Explicit grid, rows of columns, used when no recipe id is given */
  @Nullable
  public ItemStackData[][] grid = null;
  /** Explicit result, used alongside {@link #grid} */
  @Nullable
  public ItemStackData result = null;
  public TextData[] description = new TextData[0];

  /* Resolved on load */
  private transient List<List<ItemStack>> inputs = List.of();
  private transient List<ItemStack> output = List.of();
  private transient int width = 3;
  private transient int height = 3;

  @Nonnull
  @Override
  public String getTitle() {
    return this.title == null ? "" : this.title;
  }

  /** Grid edge implied by {@link #gridSize} */
  private int declaredSize() {
    return "small".equals(this.gridSize) ? 2 : 3;
  }

  @Override
  public void load() {
    int size = declaredSize();
    this.width = size;
    this.height = size;

    // explicit grid wins, it is only written where the recipe system has nothing to offer
    if (this.grid != null) {
      List<List<ItemStack>> inputs = new ArrayList<>();
      this.height = this.grid.length;
      this.width = 0;
      for (ItemStackData[] row : this.grid) {
        this.width = Math.max(this.width, row.length);
      }
      for (int y = 0; y < this.height; y++) {
        for (int x = 0; x < this.width; x++) {
          ItemStackData data = x < this.grid[y].length ? this.grid[y][x] : null;
          inputs.add(data == null ? List.of() : data.getItems());
        }
      }
      this.inputs = inputs;
      this.output = this.result == null ? List.of() : this.result.getItems();
      return;
    }

    if (this.recipe == null) {
      return;
    }
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      return;
    }
    RecipeHolder<?> holder = level.getRecipeManager().byKey(this.recipe).orElse(null);
    if (holder == null) {
      Mantle.logger.warn("Book page references unknown recipe {}", this.recipe);
      return;
    }
    Recipe<?> recipe = holder.value();
    HolderLookup.Provider registries = level.registryAccess();
    NonNullList<Ingredient> ingredients = recipe.getIngredients();
    if (recipe instanceof ShapedRecipe shaped) {
      this.width = shaped.getWidth();
      this.height = shaped.getHeight();
    } else {
      // shapeless recipes fill the declared grid left to right
      this.width = size;
      this.height = Math.max(1, (ingredients.size() + size - 1) / size);
    }
    List<List<ItemStack>> inputs = new ArrayList<>();
    for (int i = 0; i < this.width * this.height; i++) {
      inputs.add(i < ingredients.size() ? List.of(ingredients.get(i).getItems()) : List.of());
    }
    this.inputs = inputs;
    ItemStack output = recipe.getResultItem(registries == null ? RegistryAccess.EMPTY : registries);
    this.output = output.isEmpty() ? List.of() : List.of(output);
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int y = 0;
    if (this.title != null && !this.title.isEmpty()) {
      this.addTitle(list, this.title);
      y = getTitleHeight();
    }

    int gridWidth = this.width * SLOT;
    int totalWidth = gridWidth + 22 + SLOT + 4;
    int x = Math.max(0, (BookScreen.PAGE_WIDTH - totalWidth) / 2);
    int gridY = y + 8;

    list.add(new SlotElement(x, gridY, this.width, this.height, book.appearance.slotColor));
    for (int row = 0; row < this.height; row++) {
      for (int col = 0; col < this.width; col++) {
        int index = row * this.width + col;
        List<ItemStack> stacks = index < this.inputs.size() ? this.inputs.get(index) : List.of();
        if (!stacks.isEmpty()) {
          list.add(new ItemElement(x + col * SLOT + 1, gridY + row * SLOT + 1, 1F, stacks));
        }
      }
    }

    int arrowX = x + gridWidth + 2;
    int centerY = gridY + this.height * SLOT / 2;
    list.add(new ArrowElement(arrowX, centerY - 5, ArrowType.RIGHT, book.appearance.arrowColor, book.appearance.arrowColorHover, null));

    int resultX = arrowX + 22;
    list.add(new SlotElement(resultX, centerY - SLOT / 2, 1, 1, book.appearance.slotColor));
    if (!this.output.isEmpty()) {
      list.add(new ItemElement(resultX + 1, centerY - SLOT / 2 + 1, 1F, this.output));
    }

    int textY = gridY + this.height * SLOT + 10;
    list.add(new TextElement(0, textY, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - textY, this.description));
  }

  @Override
  public HtmlSerializable toHTML(BookData book) {
    HtmlGroup group = HtmlGroup.indent();
    if (this.title != null && !this.title.isEmpty()) {
      group.add(makeTitleHTML());
    }
    group.add(HtmlElement.div().add(TextData.toHtml(this.description, book)));
    return group;
  }
}
