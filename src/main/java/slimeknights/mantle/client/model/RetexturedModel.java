package slimeknights.mantle.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import slimeknights.mantle.client.model.geometry.IGeometryBakingContext;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.client.model.data.ModelData;
import slimeknights.mantle.client.model.geometry.IGeometryLoader;
import slimeknights.mantle.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.util.DynamicBakedWrapper;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.RetexturedHelper;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.HashSet;
import java.util.Set;

/**
 * Support for models whose textures are swapped at render time for a block chosen in NBT.
 *
 * <p>Holds three things: the texture-name expansion, the baking context that performs the swap, and
 * the {@code mantle:retextured} geometry itself. Tinkers' own geometry calls the first two directly
 * for the anvils and the seared components; the geometry below is what the tables and the smeltery
 * components name in their model JSON.
 */
public final class RetexturedModel {
  private RetexturedModel() {}

  /** Strips the {@code #} a face uses to reference a texture, as vanilla's lookup does. */
  private static String trim(String name) {
    return !name.isEmpty() && name.charAt(0) == '#' ? name.substring(1) : name;
  }

  /**
   * Expands a set of texture names to include every other name in the model that aliases them.
   *
   * <p>A model JSON routinely points several names at one texture — {@code "particle": "#top"} on
   * the anvils, for instance. Retexturing only the name the JSON listed would leave the aliases
   * showing the placeholder, so every face texture (and {@code particle}) that resolves to the same
   * {@link Material} as a requested name is pulled in too.
   *
   * @param owner      Context the names resolve against
   * @param model      Model whose elements are scanned for aliases
   * @param wanted     Texture names the model asked to have swapped
   * @return  {@code wanted} plus every alias of it present in the model
   */
  public static Set<String> getAllRetextured(IGeometryBakingContext owner, SimpleBlockModel model, Set<String> wanted) {
    // resolve what the requested names actually point at
    Set<Material> targets = new HashSet<>();
    for (String name : wanted) {
      if (owner.hasMaterial(name)) {
        targets.add(owner.getMaterial(name));
      }
    }
    Set<String> retextured = new HashSet<>(wanted);
    if (targets.isEmpty()) {
      return retextured;
    }
    // any face pointing at the same texture is the same texture by another name
    for (BlockElement element : model.getElements()) {
      for (BlockElementFace face : element.faces.values()) {
        String name = trim(face.texture());
        if (!retextured.contains(name) && owner.hasMaterial(name) && targets.contains(owner.getMaterial(name))) {
          retextured.add(name);
        }
      }
    }
    // particle is never named by a face, but it decides the break/step texture, so check it too
    if (!retextured.contains("particle") && owner.hasMaterial("particle") && targets.contains(owner.getMaterial("particle"))) {
      retextured.add("particle");
    }
    return retextured;
  }

  /**
   * Baking context that reports one chosen texture for a set of names and delegates the rest.
   *
   * <p>The texture is a bare {@link ResourceLocation} — the particle texture of the block being
   * copied — so it is resolved against the block atlas, which is where any block texture is
   * stitched.
   */
  public static class RetexturedContext implements IGeometryBakingContext {
    private final IGeometryBakingContext base;
    private final Set<String> retextured;
    private final Material texture;

    public RetexturedContext(IGeometryBakingContext base, Set<String> retextured, ResourceLocation texture) {
      this.base = base;
      this.retextured = retextured;
      this.texture = new Material(InventoryMenu.BLOCK_ATLAS, texture);
    }

    @Override
    public String getModelName() {
      return base.getModelName();
    }

    @Override
    public boolean hasMaterial(String name) {
      return retextured.contains(trim(name)) || base.hasMaterial(name);
    }

    @Override
    public Material getMaterial(String name) {
      if (retextured.contains(trim(name))) {
        return texture;
      }
      return base.getMaterial(name);
    }

    @Override
    public boolean useBlockLight() {
      return base.useBlockLight();
    }

    @Override
    public boolean useAmbientOcclusion() {
      return base.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
      return base.isGui3d();
    }

    @Override
    public ItemTransforms getTransforms() {
      return base.getTransforms();
    }

    @Override
    public Transformation getRootTransform() {
      return base.getRootTransform();
    }

    @Override
    public boolean isComponentVisible(String component, boolean fallback) {
      return base.isComponentVisible(component, fallback);
    }
  }

  /**
   * The {@code mantle:retextured} geometry: a plain block model plus the list of texture names that
   * follow the block the block entity was retextured with.
   */
  public static class Geometry implements IUnbakedGeometry<Geometry> {
    /** Shared loader instance */
    public static final IGeometryLoader<Geometry> LOADER = Geometry::deserialize;

    private final SimpleBlockModel model;
    private final Set<String> retextured;

    public Geometry(SimpleBlockModel model, Set<String> retextured) {
      this.model = model;
      this.retextured = retextured;
    }

    @Override
    public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter, IGeometryBakingContext context) {
      this.model.resolveParents(modelGetter, context);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
      BakedModel baked = this.model.bake(owner, baker, spriteGetter, transform, overrides, location);
      return new Baked(baked, owner, this.model, transform, getAllRetextured(owner, this.model, this.retextured));
    }

    /** Deserializes this model from JSON */
    public static Geometry deserialize(JsonObject json, JsonDeserializationContext context) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(json, context);
      Set<String> retextured = json.has("retextured")
                               ? ImmutableSet.copyOf(JsonHelper.parseList(json, "retextured", GsonHelper::convertToString))
                               : Set.of();
      return new Geometry(model, retextured);
    }
  }

  /**
   * Baked {@code mantle:retextured} model.
   *
   * <p>Rebakes itself once per distinct texture and keeps the result: a world full of retextured
   * seared components resolves to a handful of models rather than one per block.
   */
  public static class Baked extends DynamicBakedWrapper<BakedModel> {
    private final IGeometryBakingContext owner;
    private final SimpleBlockModel model;
    private final ModelState transform;
    private final Set<String> retextured;
    private final Map<ResourceLocation,BakedModel> cache = new ConcurrentHashMap<>();

    /** Item form: the stack names its texture in NBT, exactly as the block entity does */
    private final ItemOverrides overrides = new ItemOverrides() {
      @Nullable
      @Override
      public BakedModel resolve(BakedModel original, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        Block block = RetexturedHelper.getTexture(stack);
        return block == Blocks.AIR ? original : getCachedModel(block);
      }
    };

    public Baked(BakedModel baked, IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, Set<String> retextured) {
      super(baked);
      this.owner = owner;
      this.model = model;
      this.transform = transform;
      this.retextured = retextured;
    }

    /** Bakes a copy of this model with the given block's particle texture in place of the retextured names */
    private BakedModel bakeWith(ResourceLocation texture) {
      return this.model.bakeDynamic(new RetexturedContext(this.owner, this.retextured, texture), this.transform);
    }

    /** Model for the given block, baking it on first use */
    public BakedModel getCachedModel(Block block) {
      return this.cache.computeIfAbsent(ModelHelper.getParticleTexture(block), this::bakeWith);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random, ModelData data, @Nullable RenderType renderType) {
      Block block = data.get(RetexturedHelper.BLOCK_PROPERTY);
      if (block != null && block != Blocks.AIR) {
        return ModelHelper.getQuads(getCachedModel(block), state, side, random, data, renderType);
      }
      return ModelHelper.getQuads(this.originalModel, state, side, random, data, renderType);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
      Block block = data.get(RetexturedHelper.BLOCK_PROPERTY);
      if (block != null && block != Blocks.AIR) {
        return ModelHelper.getParticleIcon(getCachedModel(block), data);
      }
      return super.getParticleIcon(data);
    }

    @Override
    public ItemOverrides getOverrides() {
      return this.overrides;
    }
  }
}
