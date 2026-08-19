/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Transformation;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import slimeknights.mantle.client.RenderTypeGroup;
import slimeknights.mantle.client.extensions.IClientFluidTypeExtensions;
import slimeknights.mantle.client.model.CompositeModel;
import slimeknights.mantle.client.model.DynamicFluidContainerModel;
import slimeknights.mantle.client.model.QuadTransformers;
import slimeknights.mantle.client.model.SimpleModelState;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;
import slimeknights.mantle.client.model.geometry.IGeometryLoader;
import slimeknights.mantle.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.geometry.StandaloneGeometryBakingContext;
import slimeknights.mantle.client.model.geometry.UnbakedGeometryHelper;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.transfer.TransferUtil;
import slimeknights.mantle.transfer.fluid.FluidStack;
import slimeknights.mantle.transfer.fluid.FluidType;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/*
 * PORT (phase 5, client models) — LIVE, loader id "tconstruct:fluid_container" (71 model files:
 * the copper can plus every filled bucket), registered in TinkerModelLoaders. All the Forge model
 * utilities it leaned on now have mantle shims.
 *
 * The element generation turned out not to be the substantial piece it looked like: both halves of
 * UnbakedGeometryHelper trace a sprite into item geometry, and vanilla's ItemModelGenerator already
 * does exactly that, so the mask elements are the same trace run over the container's mask sprite
 * while the fluid sprite textures them.
 *
 * Two things to know:
 *   - FluidUtil.getFluidContained became TransferUtil.getFluidHandlerItem, so the model reads the
 *     contents through Fabric's item storage lookup and therefore also renders other mods' fluid
 *     items correctly.
 *   - The 70 bucket models parent to forge:item/bucket_drip and the can to forge:item/default,
 *     neither of which exists in a Fabric tree. The parent resolves to the missing model, which
 *     costs those items their display transforms (they render unrotated in hand) but not their
 *     geometry, which comes from this loader. Supplying the two parent models is a resource-side
 *     fix and belongs with the asset pass, not here.
 */
/**
 * Rework of Forge's {@code DynamicFluidContainerModel} with two additional features: baked tints and fluid stack sensitive models.
 * Does not handle covers as I have never seen a need for them, and it means less code duplication (plus the forge model does the whole cover is mask thing wrong compared to 1.18).
 */
public record FluidContainerModel(FluidStack fluid, boolean flipGas) implements IUnbakedGeometry<FluidContainerModel> {
  public static final IGeometryLoader<FluidContainerModel> LOADER = FluidContainerModel::deserialize;

  /** Clone of the same named field from Forge's {@code DynamicFluidContainerModel}: nudges the fluid layer forward so it does not z-fight the container. */
  public static final Transformation FLUID_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.002f), new Quaternionf());

  /** Deserializes this model from JSON */
  public static FluidContainerModel deserialize(JsonObject json, JsonDeserializationContext context) {
    FluidStack fluidStack = FluidStack.EMPTY;
    // parse the fluid with an optional tag
    if (json.has("fluid")) {
      JsonElement fluidElement = json.get("fluid");
      Fluid fluid;
      CompoundTag tag = null;
      if (fluidElement.isJsonObject()) {
        JsonObject fluidObject = fluidElement.getAsJsonObject();
        fluid = Loadables.FLUID.getIfPresent(fluidObject, "name");
        if (fluidObject.has("nbt")) {
          tag = parseTag(fluidObject.get("nbt"));
        }
      } else {
        fluid = Loadables.FLUID.convert(fluidElement, "fluid");
      }
      fluidStack = new FluidStack(fluid, FluidType.BUCKET_VOLUME, tag);
    }
    boolean flipGas = GsonHelper.getAsBoolean(json, "flip_gas", true);
    return new FluidContainerModel(fluidStack, flipGas);
  }

  /**
   * Reads a compound tag written either as inline SNBT or as a JSON object.
   * Replaces Forge's {@code CraftingHelper.getNBT}, which accepted both spellings.
   */
  private static CompoundTag parseTag(JsonElement element) {
    if (element.isJsonObject()) {
      return (CompoundTag) JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, element);
    }
    try {
      return TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));
    } catch (CommandSyntaxException e) {
      throw new JsonSyntaxException("Malformed NBT in fluid container model", e);
    }
  }

  /** Gets the given sprite, or null if the texture is not present in the model */
  @Nullable
  private static TextureAtlasSprite getSprite(IGeometryBakingContext context, Function<Material,TextureAtlasSprite> spriteGetter, String key) {
    if (context.hasMaterial(key)) {
      return spriteGetter.apply(context.getMaterial(key));
    }
    return null;
  }

  private static BakedModel bakeInternal(IGeometryBakingContext context, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation, FluidStack fluid, boolean flipGas) {
    // get basic sprites
    IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid.getFluid());
    TextureAtlasSprite baseSprite = getSprite(context, spriteGetter, "base");
    TextureAtlasSprite fluidSprite = !fluid.isEmpty() ? spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, clientFluid.getStillTexture(fluid))) : null;

    // determine particle
    TextureAtlasSprite particleSprite = getSprite(context, spriteGetter, "particle");
    if (particleSprite == null) particleSprite = fluidSprite;
    if (particleSprite == null) particleSprite = baseSprite;
    if (particleSprite == null) {
      TConstruct.LOG.error("No valid particle sprite for fluid container model, you should supply either 'base' or 'particle'");
      particleSprite = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation()));
    }

    // if its a gas and we flipping, flip it
    if (flipGas && !fluid.isEmpty() && FluidType.of(fluid.getFluid()).isLighterThanAir()) {
      modelState = new SimpleModelState(modelState.getRotation().compose(new Transformation(null, new Quaternionf(0, 0, 1, 0), null, null)));
    }

    // start building the mode
    CompositeModel.Baked.Builder modelBuilder = CompositeModel.Baked.builder(context, particleSprite, overrides, context.getTransforms());
    RenderTypeGroup renderTypes = DynamicFluidContainerModel.getLayerRenderTypes(false);

    // add in the base
    if (baseSprite != null) {
      modelBuilder.addQuads(renderTypes, UnbakedGeometryHelper.bakeElements(
        UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite.contents()),
        $ -> baseSprite, modelState, modelLocation
      ));
    }

    // add in fluid, but only when the model supplies the mask that shapes it. Forge's models always
    // did; forge:item/bucket_drip here is a stand-in whose mask texture (forge:item/mask/
    // bucket_fluid_drip) is a Forge asset that was never vendored, and tracing the missing texture
    // instead would paint the whole 16x16 square with the fluid.
    if (fluidSprite != null && context.hasMaterial("fluid")) {
      List<BakedQuad> quads = UnbakedGeometryHelper.bakeElements(
        UnbakedGeometryHelper.createUnbakedItemMaskElements(1, spriteGetter.apply(context.getMaterial("fluid")).contents()),
        $ -> fluidSprite,
        new SimpleModelState(modelState.getRotation().compose(FLUID_TRANSFORM), modelState.isUvLocked()),
        modelLocation
      );

      // apply light
      RenderTypeGroup fluidRenderTypes = renderTypes;
      int light = FluidType.of(fluid.getFluid()).getLightLevel();
      if (light > 0) {
        fluidRenderTypes = DynamicFluidContainerModel.getLayerRenderTypes(true);
        QuadTransformers.settingEmissivity(light).processInPlace(quads);
      }
      // apply color
      int color = clientFluid.getTintColor(fluid);
      if (color != -1) {
        ColoredBlockModel.applyColorQuadTransformer(color).processInPlace(quads);
      }
      modelBuilder.addQuads(fluidRenderTypes, quads);
    }
    return modelBuilder.build();
  }

  @Override
  public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
    // We need to disable GUI 3D and block lighting for this to render properly
    context = StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false).build(modelLocation);
    // only do contained fluid if we did not set the fluid in the model properties
    if (fluid.isEmpty()) {
      overrides = new ContainedFluidOverrideHandler(context, overrides, modelState, flipGas);
    }
    return bakeInternal(context, spriteGetter, modelState, overrides, modelLocation, fluid, flipGas);
  }

  /** Handles swapping the model based on the contained fluid */
  @RequiredArgsConstructor
  private static final class ContainedFluidOverrideHandler extends ItemOverrides {
    private static final ResourceLocation BAKE_LOCATION = TConstruct.getResource("copper_can_dynamic");

    private final Map<FluidStack,BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change

    private final IGeometryBakingContext context;
    private final ItemOverrides nested;
    private final ModelState modelState;
    private final boolean flipGas;


    /** Gets the model directly, for creating the cached models */
    private BakedModel getUncahcedModel(FluidStack fluid) {
      return bakeInternal(context, Material::sprite, modelState, ItemOverrides.EMPTY, BAKE_LOCATION, fluid, flipGas);
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
      BakedModel overriden = nested.resolve(originalModel, stack, world, entity, seed);
      if (overriden != originalModel) return overriden;
      // Forge asked FluidUtil; on Fabric the contents come through the item storage lookup, which
      // means containers from other mods resolve here just as Tinkers' own do
      Optional<FluidStack> optional = TransferUtil.getFluidHandlerItem(stack)
        .map(handler -> handler.getTanks() > 0 ? handler.getFluidInTank(0) : FluidStack.EMPTY)
        .filter(contained -> !contained.isEmpty());
      if (optional.isPresent()) {
        FluidStack fluid = optional.get().copy();
        fluid.setAmount(FluidType.BUCKET_VOLUME); // cache considers amount, so ensure its consistent
        return cache.computeIfAbsent(fluid, this::getUncahcedModel);
      }
      return originalModel;
    }
  }
}
