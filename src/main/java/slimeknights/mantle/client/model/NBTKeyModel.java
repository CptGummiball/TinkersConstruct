package slimeknights.mantle.client.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import slimeknights.mantle.client.model.geometry.BlockGeometryBakingContext;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;
import slimeknights.mantle.client.model.geometry.IGeometryLoader;
import slimeknights.mantle.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.client.model.util.ModelTextureIteratable;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * Model which uses a key in the stack's data to select which texture variant to draw.
 *
 * <p>The "NBT" in the name and the JSON's {@code nbt_key} survive from 1.20; the data itself now
 * lives in the {@code minecraft:custom_data} component, which is where the data-fixer moved every
 * stack tag and where this port's items keep writing it.
 */
public class NBTKeyModel implements IUnbakedGeometry<NBTKeyModel> {
  /** Loader for the {@code mantle:nbt_key} id */
  public static final IGeometryLoader<NBTKeyModel> LOADER = NBTKeyModel::deserialize;

  /** Map of statically registered extra textures, used for addon mods */
  private static final Multimap<ResourceLocation,Pair<String,ResourceLocation>> EXTRA_TEXTURES = HashMultimap.create();

  /**
   * Registers an extra variant texture for the model with the given key. Note that resource packs can override the extra texture
   * @param key          Model key, should be defined in the model JSON if supported
   * @param textureName  Name of the texture defined, corresponds to a possible value of the data key
   * @param texture      Texture to use, same format as in resource packs
   */
  @SuppressWarnings("unused")  // API
  public static void registerExtraTexture(ResourceLocation key, String textureName, ResourceLocation texture) {
    EXTRA_TEXTURES.put(key, Pair.of(textureName, texture));
  }

  /** Key to check in the stack's custom data */
  private final String nbtKey;
  /** Key denoting which extra textures to fetch from the map */
  @Nullable
  private final ResourceLocation extraTexturesKey;

  /** Map of textures for the model */
  private Map<String,Material> textures = Collections.emptyMap();

  protected NBTKeyModel(String nbtKey, @Nullable ResourceLocation extraTexturesKey) {
    this.nbtKey = nbtKey;
    this.extraTexturesKey = extraTexturesKey;
  }

  @Override
  public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter, IGeometryBakingContext owner) {
    textures = new HashMap<>();
    // must have a default
    textures.put("default", owner.getMaterial("default"));
    // fetch all textures the model tree declares; the texture map is the only place the variant names appear
    if (owner instanceof BlockGeometryBakingContext blockContext) {
      for (Map<String,Either<Material,String>> map : new ModelTextureIteratable(blockContext.getBlockModel())) {
        for (String key : map.keySet()) {
          if (!textures.containsKey(key) && owner.hasMaterial(key)) {
            textures.put(key, owner.getMaterial(key));
          }
        }
      }
    }
    // fetch extra textures
    if (extraTexturesKey != null) {
      for (Pair<String,ResourceLocation> extra : EXTRA_TEXTURES.get(extraTexturesKey)) {
        String key = extra.getFirst();
        if (!textures.containsKey(key)) {
          textures.put(key, new Material(InventoryMenu.BLOCK_ATLAS, extra.getSecond()));
        }
      }
    }
  }

  /** Bakes a model for the given texture */
  private static BakedModel bakeModel(IGeometryBakingContext owner, Material texture, Function<Material,TextureAtlasSprite> spriteGetter, Transformation rotation, ItemOverrides overrides) {
    TextureAtlasSprite sprite = spriteGetter.apply(texture);
    CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(owner, sprite, overrides, owner.getTransforms());
    builder.addQuads(MantleItemLayerModel.getDefaultRenderType(owner), MantleItemLayerModel.getQuadsForSprite(-1, -1, sprite, rotation, 0));
    return builder.build();
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    // the root transform is documented identity in this port, so the state rotation is the whole transform
    Transformation transform = modelTransform.getRotation();
    // build variants map
    Map<String,BakedModel> variants = new HashMap<>(textures.size());
    for (Entry<String,Material> entry : textures.entrySet()) {
      String key = entry.getKey();
      if (!key.equals("default")) {
        variants.put(key, bakeModel(owner, entry.getValue(), spriteGetter, transform, ItemOverrides.EMPTY));
      }
    }
    return bakeModel(owner, textures.get("default"), spriteGetter, transform, new Overrides(nbtKey, textures, Map.copyOf(variants)));
  }

  /** Overrides list swapping in the variant the stack's data names */
  public static class Overrides extends ItemOverrides {
    private final String nbtKey;
    private final Map<String,Material> textures;
    private final Map<String,BakedModel> variants;

    protected Overrides(String nbtKey, Map<String,Material> textures, Map<String,BakedModel> variants) {
      this.nbtKey = nbtKey;
      this.textures = textures;
      this.variants = variants;
    }

    @Override
    public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int seed) {
      CustomData data = stack.get(DataComponents.CUSTOM_DATA);
      if (data != null && data.contains(nbtKey)) {
        // getUnsafe avoids copying the tag; this runs every frame for a rendered stack, and the string is only read
        return variants.getOrDefault(data.getUnsafe().getString(nbtKey), model);
      }
      return model;
    }

    /** Gets the given texture from the model */
    @SuppressWarnings("unused")  // API
    public Material getTexture(String name) {
      Material texture = textures.get(name);
      return texture != null ? texture : textures.get("default");
    }
  }

  /** Deserializes this model from JSON */
  public static NBTKeyModel deserialize(JsonObject json, JsonDeserializationContext context) {
    String key = GsonHelper.getAsString(json, "nbt_key");
    ResourceLocation extraTexturesKey = null;
    if (json.has("extra_textures_key")) {
      extraTexturesKey = ResourceLocation.parse(GsonHelper.getAsString(json, "extra_textures_key"));
    }
    return new NBTKeyModel(key, extraTexturesKey);
  }
}
