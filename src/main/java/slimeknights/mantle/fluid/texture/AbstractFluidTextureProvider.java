package slimeknights.mantle.fluid.texture;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider for {@link FluidTexture}.
 *
 * <p>Forge keyed textures by its fluid-type registry; on Fabric the manager reads them by the
 * still fluid's id (see {@link FluidTextureManager}), so the builders are keyed the same way —
 * the shipped file names already match.
 */
@SuppressWarnings("unused")
public abstract class AbstractFluidTextureProvider extends GenericDataProvider {
  private final Map<ResourceLocation, FluidTexture.Builder> allTextures = new HashMap<>();
  private final Set<ResourceLocation> ignore = new HashSet<>();
  @Nullable
  private final String modId;

  public AbstractFluidTextureProvider(PackOutput packOutput, @Nullable String modId) {
    super(packOutput, Target.RESOURCE_PACK, FluidTextureManager.FOLDER, JsonHelper.DEFAULT_GSON);
    this.modId = modId;
  }

  @Override
  public final CompletableFuture<?> run(CachedOutput cache) {
    ensureTexturesAdded();
    // ensure we added textures for all our source fluids; flowing fluids share their source's entry
    if (modId != null) {
      List<String> missing = BuiltInRegistries.FLUID.entrySet().stream()
        .filter(entry -> entry.getKey().location().getNamespace().equals(modId))
        .filter(entry -> {
          Fluid fluid = entry.getValue();
          return !(fluid instanceof FlowingFluid flowing) || flowing.getSource() == fluid;
        })
        .filter(entry -> !allTextures.containsKey(entry.getKey().location()) && !ignore.contains(entry.getKey().location()))
        .map(entry -> entry.getKey().location().toString()).toList();
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing fluid textures for: " + String.join(", ", missing));
      }
    }
    // save files
    return allOf(allTextures.entrySet().stream().map(entry -> saveJson(cache, entry.getKey(), entry.getValue().build().serialize())));
  }

  /** Adds the textures if not already added */
  private void ensureTexturesAdded() {
    if (allTextures.isEmpty()) {
      addTextures();
    }
  }

  /** Gets the map of all textures. Should not be called in {@link #addTextures()}, meant for other data generators to use. */
  public Map<ResourceLocation, FluidTexture.Builder> getAllTextures() {
    ensureTexturesAdded();
    return allTextures;
  }

  /** Override to add your textures at the proper time */
  public abstract void addTextures();

  /** Create a new builder for the fluid with the given id */
  public FluidTexture.Builder texture(ResourceLocation id) {
    return allTextures.computeIfAbsent(id, FluidTexture.Builder::new);
  }

  /** Create a new builder for the give fluid object */
  public FluidTexture.Builder texture(FluidObject<?> fluid) {
    return texture(fluid.getId());
  }

  /** Marks the fluid with the given id to be ignored by this texture provider */
  public void skip(ResourceLocation id) {
    ignore.add(id);
  }

  /** Marks the given fluid to be ignored by this texture provider */
  public void skip(FluidObject<?> fluid) {
    skip(fluid.getId());
  }
}
