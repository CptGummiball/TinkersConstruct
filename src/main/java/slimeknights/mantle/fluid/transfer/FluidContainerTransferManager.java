package slimeknights.mantle.fluid.transfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.condition.ConditionHelper;
import slimeknights.mantle.recipe.condition.ICondition.IContext;
import slimeknights.mantle.transfer.fluid.FluidStack;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.gson.GenericRegisteredSerializer;
import slimeknights.mantle.util.DataLoadedConditionContext;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/** Logic for filling and emptying fluid containers that are not fluid handlers */
@Log4j2
public class FluidContainerTransferManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
  /** Map of all modifier types that are expected to load in data packs */
  public static final GenericRegisteredSerializer<IFluidContainerTransfer> TRANSFER_LOADERS = new GenericRegisteredSerializer<>();
  /** Folder for saving the logic */
  public static final String FOLDER = "mantle/fluid_transfer";
  /** GSON instance */
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeHierarchyAdapter(IFluidContainerTransfer.class, TRANSFER_LOADERS)
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();
  /** Singleton instance of the manager */
  public static final FluidContainerTransferManager INSTANCE = new FluidContainerTransferManager();

  /** List of loaded transfer logic, only exists serverside */
  private List<IFluidContainerTransfer> transfers = Collections.emptyList();

  /** Set of all items that match a recipe, exists on both sides */
  @Setter @Nullable
  private Set<Item> containerItems = Collections.emptySet();

  /** Condition context for tags */
  private IContext context = IContext.EMPTY;

  private FluidContainerTransferManager() {
    super(GSON, FOLDER);
  }

  /** Lazily initializes the set of container items */
  protected Set<Item> getContainerItems() {
    if (this.containerItems == null) {
      List<Item> builder = new ArrayList<>();
      Consumer<Item> consumer = builder::add;
      for (IFluidContainerTransfer transfer : transfers) {
        transfer.addRepresentativeItems(consumer);
      }
      this.containerItems = Set.copyOf(builder);
    }
    return this.containerItems;
  }

  /**
   * For internal use only.
   *
   * <p>Forge wired this through AddReloadListenerEvent (which also supplied the condition
   * context) and OnDatapackSyncEvent for the client sync. On Fabric the reload listener
   * registers directly, conditions evaluate against loaded data, and the client sync of
   * {@link #getContainerItems()} is wired by the network module when a player joins.
   */
  public void init() {
    this.context = DataLoadedConditionContext.INSTANCE;
    ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(this);
  }

  @Override
  public net.minecraft.resources.ResourceLocation getFabricId() {
    return Mantle.getResource("fluid_container_transfer");
  }

  /** Loads transfer from JSON */
  @Nullable
  private IFluidContainerTransfer loadFluidTransfer(ResourceLocation key, JsonObject json) {
    try {
      if (!json.has("conditions") || ConditionHelper.processConditions(GsonHelper.getAsJsonArray(json, "conditions"), context)) {
        return GSON.fromJson(json, IFluidContainerTransfer.class);
      }
    } catch (JsonSyntaxException e) {
      log.error("Failed to load fluid container transfer info from {}", key, e);
    }
    return null;
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager manager, ProfilerFiller profiler) {
    long time = System.nanoTime();
    this.transfers = splashList.entrySet().stream()
                               .map(entry -> loadFluidTransfer(entry.getKey(), entry.getValue().getAsJsonObject()))
                               .filter(Objects::nonNull)
                               .toList();
    this.containerItems = null;
    log.info("Loaded {} dynamic modifiers in {} ms", transfers.size(), (System.nanoTime() - time) / 1000000f);
  }

  /**
   * Checks if the given stack could possibly match, used client side to determine if the fluid transfer falls back to opening the UI
   * @param item  Item to check
   * @return  True if a match is possible, basically just checks item ID
   */
  public boolean mayHaveTransfer(ItemLike item) {
    return getContainerItems().contains(item.asItem());
  }

  /**
   * Checks if the given stack could possibly match, used client side to determine if the fluid transfer falls back to opening the UI
   * @param stack  Stack to check
   * @return  True if a match is possible, basically just checks item ID
   */
  public boolean mayHaveTransfer(ItemStack stack) {
    return getContainerItems().contains(stack.getItem());
  }

  /** Gets the transfer for the given item and fluid, or null if its not a valid item and fluid */
  @Nullable
  public IFluidContainerTransfer getTransfer(ItemStack stack, FluidStack fluid) {
    for (IFluidContainerTransfer transfer : transfers) {
      if (transfer.matches(stack, fluid)) {
        return transfer;
      }
    }
    return null;
  }
}
