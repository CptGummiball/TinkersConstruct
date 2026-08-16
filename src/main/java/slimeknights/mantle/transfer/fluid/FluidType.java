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

  /**
   * Replacement for Forge's {@code Fluid.getFluidType()}. Falls back to water-like defaults
   * for fluids that never registered a type, matching Forge's behaviour for plain fluids.
   */
  public static FluidType of(Fluid fluid) {
    return TYPES.getOrDefault(fluid, DEFAULT);
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

  /** Forge derived this from a negative density; kept so buoyancy checks read the same. */
  public boolean isLighterThanAir() {
    return properties.density <= 0;
  }

  public String getDescriptionId() {
    return properties.descriptionId;
  }

  public Component getDescription() {
    return Component.translatable(properties.descriptionId);
  }

  public SoundEvent getSound(SoundAction action) {
    return action == SoundAction.BUCKET_FILL ? properties.fillSound : properties.emptySound;
  }

  /** The two bucket sounds Tinkers distinguishes; Forge had an open registry of actions. */
  public enum SoundAction { BUCKET_FILL, BUCKET_EMPTY }

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
  }
}
