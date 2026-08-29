package slimeknights.mantle.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Fabric stand-in for Forge's {@code net.minecraftforge.fluids.FluidType}.
 *
 * <p>Forge bundled a fluid's physical attributes — temperature, density, viscosity, light —
 * into one object attached to the fluid. Fabric splits these across
 * {@link FluidVariantAttributes} handlers instead. This class keeps Forge's shape, which 47
 * TConstruct files expect, and doubles as the Fabric handler so the values are visible to
 * every other mod in the pack rather than only to Tinkers.
 *
 * <p>Temperature matters most here: the smeltery reads it on 36 call sites to decide melting
 * behaviour, and other Fabric mods query it through the same attribute API.
 *
 * <p>Only the attributes Tinkers actually reads are modelled. Forge's texture, motion-scale
 * and client-extension fields have no consumer here; fluid rendering goes through Fabric's
 * {@code FluidRenderHandlerRegistry} in the client module instead.
 */
public class FluidType implements FluidVariantAttributeHandler {

  /** A bucket in millibuckets. The overwhelmingly most-used member of Forge's class. */
  public static final int BUCKET_VOLUME = FluidStack.BUCKET_VOLUME;

  /**
   * Fluid → type lookup. Forge hung the type off the fluid itself
   * ({@code Fluid.getFluidType()}, 23 TConstruct files); on Fabric that back-reference has
   * to live here, filled by {@link #register(Fluid...)}.
   */
  private static final java.util.Map<Fluid, FluidType> TYPES = new java.util.concurrent.ConcurrentHashMap<>();

  /** Water-like fallback so lookups of unregistered (e.g. other mods') fluids stay safe. */
  private static final FluidType DEFAULT = new FluidType(Properties.create());

  /** Cache of types derived from fabric attribute handlers for fluids we never registered. */
  private static final java.util.Map<Fluid, FluidType> DERIVED = new java.util.concurrent.ConcurrentHashMap<>();

  /**
   * Replacement for Forge's {@code Fluid.getFluidType()}. Fluids that never registered a
   * mantle type — vanilla's own and other mods' — derive one from their Fabric fluid
   * attributes, so e.g. lava reports Forge's 1300K instead of the water-like default; that
   * temperature is what melting-fuel datagen and heat displays run on. Fluids without an
   * attribute handler land on Fabric's defaults, which match the water-like fallback.
   */
  public static FluidType of(Fluid fluid) {
    FluidType type = TYPES.get(fluid);
    if (type != null) {
      return type;
    }
    return DERIVED.computeIfAbsent(fluid, f -> {
      net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant variant = net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant.of(f);
      return new FluidType(Properties.create()
        .temperature(FluidVariantAttributes.getTemperature(variant))
        .viscosity(FluidVariantAttributes.getViscosity(variant, null))
        .lightLevel(FluidVariantAttributes.getLuminance(variant)));
    });
  }

  private final Properties properties;

  public FluidType(Properties properties) {
    this.properties = properties;
  }

  /** Registers this as the Fabric attribute handler and the fluids' type lookup. */
  public void register(Fluid... fluids) {
    for (Fluid fluid : fluids) {
      TYPES.put(fluid, this);
      FluidVariantAttributes.register(fluid, this);
    }
  }

  /* Forge-shaped accessors */

  /** Kelvin. Water is 300; anything above 1000 is treated as hot by most consumers. */
  public int getTemperature() {
    return properties.temperature;
  }

  public int getDensity() {
    return properties.density;
  }

  public int getViscosity() {
    return properties.viscosity;
  }

  public int getLightLevel() {
    return properties.lightLevel;
  }

  public Rarity getRarity() {
    return properties.rarity;
  }

  /** If true, entities drown in this fluid; mirrors the Forge FluidType hook. Read by the entity-in-fluid handling once the event layer lands. */
  public boolean canDrownIn(net.minecraft.world.entity.LivingEntity entity) {
    return properties.canDrown;
  }

  /** Forge derived this from a negative density; kept so buoyancy checks read the same. */
  public boolean isLighterThanAir() {
    return properties.density <= 0;
  }

  public String getDescriptionId() {
    return properties.descriptionId;
  }

  /** Gets the description id for the given stack; NBT-sensitive types (potion) override */
  public String getDescriptionId(FluidStack stack) {
    return getDescriptionId();
  }

  public Component getDescription() {
    return Component.translatable(properties.descriptionId);
  }

  /** Gets the description for the given stack; NBT-sensitive types (potion) override */
  public Component getDescription(FluidStack stack) {
    return Component.translatable(getDescriptionId(stack));
  }

  /**
   * Gets the filled bucket for the given fluid stack, letting NBT-sensitive types (potion)
   * copy their data onto the bucket item; mirrors the Forge FluidType hook.
   */
  public net.minecraft.world.item.ItemStack getBucket(FluidStack stack) {
    return new net.minecraft.world.item.ItemStack(stack.getFluid().getBucket());
  }

  public SoundEvent getSound(SoundAction action) {
    return action == SoundAction.BUCKET_FILL ? properties.fillSound : properties.emptySound;
  }

  /** The two bucket sounds Tinkers distinguishes; Forge had an open registry of actions. */
  public enum SoundAction { BUCKET_FILL, BUCKET_EMPTY }

  /**
   * Whether placing this fluid at the position makes it evaporate instead.
   *
   * <p>Forge let each fluid type decide; vanilla hardcodes the one case that exists —
   * water in an ultrawarm dimension — inside {@code BucketItem}. Mirroring that keeps
   * placement behavior identical for vanilla and Fabric fluids alike.
   */
  public boolean isVaporizedOnPlacement(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, FluidStack stack) {
    return level.dimensionType().ultraWarm() && stack.getFluid().is(net.minecraft.tags.FluidTags.WATER);
  }

  /** Plays the evaporation effects; mirrors vanilla's bucket behavior */
  public void onVaporize(@javax.annotation.Nullable net.minecraft.world.entity.player.Player player, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, FluidStack stack) {
    level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
    for (int i = 0; i < 8; i++) {
      level.addParticle(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0, 0, 0);
    }
  }

  /* FluidVariantAttributeHandler — exposes the same values to the rest of the ecosystem */

  @Override
  public Component getName(FluidVariant variant) {
    return getDescription();
  }

  @Override
  public Optional<SoundEvent> getFillSound(FluidVariant variant) {
    return Optional.of(properties.fillSound);
  }

  @Override
  public Optional<SoundEvent> getEmptySound(FluidVariant variant) {
    return Optional.of(properties.emptySound);
  }

  @Override
  public int getLuminance(FluidVariant variant) {
    return properties.lightLevel;
  }

  @Override
  public int getTemperature(FluidVariant variant) {
    return properties.temperature;
  }

  @Override
  public int getViscosity(FluidVariant variant, @Nullable Level level) {
    return properties.viscosity;
  }

  @Override
  public boolean isLighterThanAir(FluidVariant variant) {
    return isLighterThanAir();
  }

  /** Builder mirroring Forge's {@code FluidType.Properties}. */
  public static class Properties {

    private int temperature = 300;
    private int density = 1000;
    private int viscosity = 1000;
    private int lightLevel = 0;
    private Rarity rarity = Rarity.COMMON;
    private String descriptionId = "block.minecraft.water";
    private SoundEvent fillSound = SoundEvents.BUCKET_FILL;
    private SoundEvent emptySound = SoundEvents.BUCKET_EMPTY;

    private Properties() {}

    public static Properties create() {
      return new Properties();
    }

    public Properties temperature(int temperature) {
      this.temperature = temperature;
      return this;
    }

    public Properties density(int density) {
      this.density = density;
      return this;
    }

    public Properties viscosity(int viscosity) {
      this.viscosity = viscosity;
      return this;
    }

    public Properties lightLevel(int lightLevel) {
      this.lightLevel = lightLevel;
      return this;
    }

    public Properties rarity(Rarity rarity) {
      this.rarity = rarity;
      return this;
    }

    public Properties descriptionId(String descriptionId) {
      this.descriptionId = descriptionId;
      return this;
    }

    public Properties sound(SoundAction action, SoundEvent sound) {
      if (action == SoundAction.BUCKET_FILL) {
        this.fillSound = sound;
      } else {
        this.emptySound = sound;
      }
      return this;
    }

    /* Behavior hooks mirrored from Forge's FluidType.Properties. The values are stored so
     * the entity-in-fluid handling can read them once the event layer lands; nothing on
     * Fabric consumes them yet. */

    /** How strongly this fluid pushes entities, as a per-tick motion scale */
    public Properties motionScale(double motionScale) {
      this.motionScale = motionScale;
      return this;
    }

    /** If true, this fluid extinguishes burning entities */
    public Properties canExtinguish(boolean canExtinguish) {
      this.canExtinguish = canExtinguish;
      return this;
    }

    /** If false, entities cannot swim upwards in this fluid */
    public Properties canSwim(boolean canSwim) {
      this.canSwim = canSwim;
      return this;
    }

    /** If false, entities do not drown in this fluid */
    public Properties canDrown(boolean canDrown) {
      this.canDrown = canDrown;
      return this;
    }

    /** Path node type mobs treat this fluid as (lava-like fluids use {@link PathType#LAVA}) */
    public Properties pathType(@Nullable PathType pathType) {
      this.pathType = pathType;
      return this;
    }

    /** Path node type for blocks adjacent to this fluid */
    public Properties adjacentPathType(@Nullable PathType adjacentPathType) {
      this.adjacentPathType = adjacentPathType;
      return this;
    }

    private double motionScale = 0.014;
    private boolean canExtinguish = false;
    private boolean canSwim = true;
    private boolean canDrown = true;
    @Nullable
    private PathType pathType = PathType.WATER;
    @Nullable
    private PathType adjacentPathType = PathType.WATER_BORDER;
  }
}
