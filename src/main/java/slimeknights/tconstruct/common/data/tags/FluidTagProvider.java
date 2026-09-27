package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import slimeknights.mantle.data.BuiltinRegistryTagProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import slimeknights.mantle.datagen.Tags;
import slimeknights.mantle.data.ExistingFileHelper;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class FluidTagProvider extends BuiltinRegistryTagProvider<Fluid> {

  public FluidTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider, ExistingFileHelper helper) {
    super(packOutput, net.minecraft.core.registries.BuiltInRegistries.FLUID, lookupProvider, TConstruct.MOD_ID, helper);
  }

  @Override
  protected void addTags(Provider pProvider) {
    // first, register common tags
    // slime
    fluidTag(TinkerFluids.earthSlime);
    fluidTag(TinkerFluids.skySlime);
    fluidTag(TinkerFluids.ichor);
    fluidTag(TinkerFluids.enderSlime);
    fluidTag(TinkerFluids.magma);
    fluidTag(TinkerFluids.venom);
    // basic molten
    fluidTag(TinkerFluids.searedStone);
    fluidTag(TinkerFluids.scorchedStone);
    fluidTag(TinkerFluids.moltenClay);
    fluidTag(TinkerFluids.moltenGlass);
    fluidTag(TinkerFluids.liquidSoul);
    fluidTag(TinkerFluids.moltenPorcelain);
    // fancy molten
    fluidTag(TinkerFluids.moltenObsidian);
    fluidTag(TinkerFluids.moltenEmerald);
    fluidTag(TinkerFluids.moltenQuartz);
    fluidTag(TinkerFluids.moltenDiamond);
    fluidTag(TinkerFluids.moltenAmethyst);
    fluidTag(TinkerFluids.moltenEnder);
    fluidTag(TinkerFluids.blazingBlood);
    // ores
    fluidTag(TinkerFluids.moltenIron);
    fluidTag(TinkerFluids.moltenGold);
    fluidTag(TinkerFluids.moltenCopper);
    fluidTag(TinkerFluids.moltenCobalt);
    fluidTag(TinkerFluids.moltenSteel);
    fluidTag(TinkerFluids.moltenDebris);
    // alloys
    fluidTag(TinkerFluids.moltenSlimesteel);
    fluidTag(TinkerFluids.moltenAmethystBronze);
    fluidTag(TinkerFluids.moltenRoseGold);
    fluidTag(TinkerFluids.moltenPigIron);
    // nether alloys
    fluidTag(TinkerFluids.moltenManyullyn);
    fluidTag(TinkerFluids.moltenHepatizon);
    fluidTag(TinkerFluids.moltenQueensSlime);
    fluidTag(TinkerFluids.moltenCinderslime);
    fluidTag(TinkerFluids.moltenSoulsteel);
    fluidTag(TinkerFluids.moltenNetherite);
    // end alloys
    fluidTag(TinkerFluids.moltenKnightmetal);
    fluidTag(TinkerFluids.moltenKnightslime);
    // compat ores
    fluidTag(TinkerFluids.moltenTin);
    fluidTag(TinkerFluids.moltenAluminum);
    fluidTag(TinkerFluids.moltenLead);
    fluidTag(TinkerFluids.moltenSilver);
    fluidTag(TinkerFluids.moltenNickel);
    fluidTag(TinkerFluids.moltenZinc);
    fluidTag(TinkerFluids.moltenPlatinum);
    fluidTag(TinkerFluids.moltenTungsten);
    fluidTag(TinkerFluids.moltenOsmium);
    fluidTag(TinkerFluids.moltenUranium);
    fluidTag(TinkerFluids.moltenChromium);
    fluidTag(TinkerFluids.moltenCadmium);
    // compat alloys
    fluidTag(TinkerFluids.moltenBronze);
    fluidTag(TinkerFluids.moltenBrass);
    fluidTag(TinkerFluids.moltenElectrum);
    fluidTag(TinkerFluids.moltenInvar);
    fluidTag(TinkerFluids.moltenConstantan);
    fluidTag(TinkerFluids.moltenPewter);
    // thermal compat alloys
    fluidTag(TinkerFluids.moltenEnderium);
    fluidTag(TinkerFluids.moltenLumium);
    fluidTag(TinkerFluids.moltenSignalum);
    // mekanism compat alloys
    fluidTag(TinkerFluids.moltenRefinedGlowstone);
    fluidTag(TinkerFluids.moltenRefinedObsidian);
    // cosmere compat alloys
    fluidTag(TinkerFluids.moltenNicrosil);
    fluidTag(TinkerFluids.moltenDuralumin);
    fluidTag(TinkerFluids.moltenBendalloy);
    // twilight compat fluids
    fluidTag(TinkerFluids.moltenSteeleaf);
    fluidTag(TinkerFluids.fieryLiquid);
    // unplacable fluids
    fluidTag(TinkerFluids.honey);
    fluidTag(TinkerFluids.beetrootSoup);
    fluidTag(TinkerFluids.mushroomStew);
    fluidTag(TinkerFluids.rabbitStew);
    fluidTag(TinkerFluids.meatSoup);

    /* Normal tags */
    this.tag(TinkerTags.Fluids.SLIME)
        .addTag(TinkerFluids.earthSlime.getTag())
        .addTag(TinkerFluids.skySlime.getTag())
        .addTag(TinkerFluids.ichor.getTag())
        .addTag(TinkerFluids.enderSlime.getTag());

    fluidTag(TinkerFluids.potion);
    fluidTag(TinkerFluids.powderedSnow);

    // drowned want fluids that work nice in water, while wither skeletons want to complement the withering
    // both need to act as a swasher tutorial though
    tag(TinkerTags.Fluids.DROWNED_SWASHER).add(Fluids.LAVA, TinkerFluids.powderedSnow.get(), TinkerFluids.moltenGlass.get(), TinkerFluids.moltenObsidian.get());
    tag(TinkerTags.Fluids.WITHER_SKELETON_SWASHER).add(Fluids.LAVA, TinkerFluids.blazingBlood.get(), TinkerFluids.liquidSoul.get(), TinkerFluids.magma.get());

    // tag local tags with the chemthrower, do not include forge tags as its on other mods to choose how they want to support IE
    // block effects - mostly mining
    this.tag(TinkerTags.Fluids.CHEMTHROWER_BLOCK_EFFECTS)
      .addTag(// small gem
        TinkerFluids.moltenAmethyst.getLocalTag()).addTag(TinkerFluids.moltenQuartz.getLocalTag()).addTag(// large gem
        TinkerFluids.moltenEmerald.getLocalTag()).addTag(TinkerFluids.moltenDiamond.getLocalTag()).addTag(TinkerFluids.moltenDebris.getLocalTag()).addTag(// twilight forest
        TinkerFluids.fieryLiquid.getLocalTag());
    // entity effects - most of these have block effects, but we don't want the clouds triggering mostly
    this.tag(TinkerTags.Fluids.CHEMTHROWER_ENTITY_EFFECTS)
      .add(TinkerFluids.powderedSnow.get())
      .addTag(// common
        Tags.Fluids.MILK).addTag(TinkerFluids.blazingBlood.getLocalTag()).addTag(// slime
        TinkerFluids.venom.getLocalTag()).addTag(// glass
        TinkerFluids.moltenGlass.getLocalTag()).addTag(TinkerFluids.liquidSoul.getLocalTag()).addTag(TinkerFluids.moltenObsidian.getLocalTag()).addTag(// clay
        TinkerFluids.moltenClay.getLocalTag()).addTag(TinkerFluids.searedStone.getLocalTag()).addTag(TinkerFluids.scorchedStone.getLocalTag()).addTag(// food
        TinkerFluids.honey.getLocalTag()).addTag(TinkerFluids.mushroomStew.getLocalTag()).addTag(TinkerFluids.rabbitStew.getLocalTag()).addTag(TinkerFluids.meatSoup.getLocalTag()).addTag(// tier 2
        TinkerFluids.moltenCopper.getLocalTag()).addTag(TinkerFluids.moltenIron.getLocalTag()).addTag(TinkerFluids.moltenGold.getLocalTag()).addTag(// tier 2 compat
        TinkerFluids.moltenZinc.getLocalTag()).addTag(TinkerFluids.moltenTin.getLocalTag()).addTag(TinkerFluids.moltenAluminum.getLocalTag()).addTag(TinkerFluids.moltenSilver.getLocalTag()).addTag(TinkerFluids.moltenLead.getLocalTag()).addTag(TinkerFluids.moltenNickel.getLocalTag()).addTag(TinkerFluids.moltenPlatinum.getLocalTag()).addTag(TinkerFluids.moltenTungsten.getLocalTag()).addTag(TinkerFluids.moltenOsmium.getLocalTag()).addTag(TinkerFluids.moltenUranium.getLocalTag()).addTag(TinkerFluids.moltenChromium.getLocalTag()).addTag(TinkerFluids.moltenCadmium.getLocalTag()).addTag(// tier 3
        TinkerFluids.moltenAmethystBronze.getLocalTag()).addTag(TinkerFluids.moltenPigIron.getLocalTag()).addTag(TinkerFluids.moltenRoseGold.getLocalTag()).addTag(TinkerFluids.moltenCobalt.getLocalTag()).addTag(TinkerFluids.moltenSteel.getLocalTag()).addTag(// tier 3 compat
        TinkerFluids.moltenBronze.getLocalTag()).addTag(TinkerFluids.moltenBrass.getLocalTag()).addTag(TinkerFluids.moltenPewter.getLocalTag()).addTag(TinkerFluids.moltenInvar.getLocalTag()).addTag(TinkerFluids.moltenConstantan.getLocalTag()).addTag(// tier 4
        TinkerFluids.moltenManyullyn.getLocalTag()).addTag(TinkerFluids.moltenHepatizon.getLocalTag()).addTag(TinkerFluids.moltenNetherite.getLocalTag()).addTag(TinkerFluids.moltenKnightmetal.getLocalTag()).addTag(// thermal alloys
        TinkerFluids.moltenLumium.getLocalTag()).addTag(TinkerFluids.moltenEnderium.getLocalTag()).addTag(// mekanism alloys
        TinkerFluids.moltenRefinedGlowstone.getLocalTag()).addTag(TinkerFluids.moltenRefinedObsidian.getLocalTag()).addTag(// cosmere alloys
        TinkerFluids.moltenNicrosil.getLocalTag()).addTag(TinkerFluids.moltenDuralumin.getLocalTag()).addTag(TinkerFluids.moltenBendalloy.getLocalTag()).addTag(// twilight forest
        TinkerFluids.moltenSteeleaf.getLocalTag());
    // both effects - all the neat slimes
    this.tag(TinkerTags.Fluids.CHEMTHROWER_BOTH_EFFECTS)
      // slime
      .addTag(// slime
        TinkerFluids.earthSlime.getLocalTag()).addTag(TinkerFluids.skySlime.getLocalTag()).addTag(TinkerFluids.ichor.getTag()).addTag(TinkerFluids.enderSlime.getTag()).addTag(TinkerFluids.magma.getLocalTag()).addTag(TinkerFluids.moltenEnder.getLocalTag()).addTag(// slime metal
        TinkerFluids.moltenSlimesteel.getLocalTag()).addTag(TinkerFluids.moltenQueensSlime.getLocalTag()).addTag(TinkerFluids.moltenCinderslime.getLocalTag()).addTag(TinkerFluids.moltenKnightslime.getLocalTag()).addTag(// thermal alloys
        TinkerFluids.moltenSignalum.getLocalTag());

    // tooltips //
    this.tag(TinkerTags.Fluids.GLASS_TOOLTIPS).addTag(TinkerFluids.moltenGlass.getTag()).addTag(TinkerFluids.liquidSoul.getTag()).addTag(TinkerFluids.moltenObsidian.getTag());
    this.tag(TinkerTags.Fluids.SLIME_TOOLTIPS).addTag(TinkerFluids.magma.getTag()).addTag(TinkerFluids.moltenEnder.getTag()).addTag(TinkerTags.Fluids.SLIME);
    this.tag(TinkerTags.Fluids.BOTTLE_TOOLTIPS).addTag(TinkerFluids.venom.getTag()).addTag(TinkerFluids.fieryLiquid.getTag());
    this.tag(TinkerTags.Fluids.CLAY_TOOLTIPS).addTag(TinkerFluids.moltenClay.getTag()).addTag(TinkerFluids.moltenPorcelain.getTag()).addTag(TinkerFluids.searedStone.getTag()).addTag(TinkerFluids.scorchedStone.getTag());
    this.tag(TinkerTags.Fluids.METAL_TOOLTIPS).addTag(// vanilla ores
        TinkerFluids.moltenIron.getTag()).addTag(TinkerFluids.moltenGold.getTag()).addTag(TinkerFluids.moltenCopper.getTag()).addTag(TinkerFluids.moltenCobalt.getTag()).addTag(TinkerFluids.moltenSteel.getTag()).addTag(TinkerFluids.moltenDebris.getTag()).addTag(// base alloys
        TinkerFluids.moltenSlimesteel.getTag()).addTag(TinkerFluids.moltenAmethystBronze.getTag()).addTag(TinkerFluids.moltenRoseGold.getTag()).addTag(TinkerFluids.moltenPigIron.getTag()).addTag(TinkerFluids.moltenManyullyn.getTag()).addTag(TinkerFluids.moltenHepatizon.getTag()).addTag(TinkerFluids.moltenQueensSlime.getTag()).addTag(TinkerFluids.moltenCinderslime.getTag()).addTag(TinkerFluids.moltenNetherite.getTag()).addTag(TinkerFluids.moltenSoulsteel.getTag()).addTag(TinkerFluids.moltenKnightmetal.getTag()).addTag(TinkerFluids.moltenKnightslime.getTag()).addTag(// compat ores
        TinkerFluids.moltenTin.getTag()).addTag(TinkerFluids.moltenAluminum.getTag()).addTag(TinkerFluids.moltenLead.getTag()).addTag(TinkerFluids.moltenSilver.getTag()).addTag(TinkerFluids.moltenNickel.getTag()).addTag(TinkerFluids.moltenZinc.getTag()).addTag(TinkerFluids.moltenPlatinum.getTag()).addTag(TinkerFluids.moltenTungsten.getTag()).addTag(TinkerFluids.moltenOsmium.getTag()).addTag(TinkerFluids.moltenUranium.getTag()).addTag(TinkerFluids.moltenChromium.getTag()).addTag(TinkerFluids.moltenCadmium.getTag()).addTag(// compat alloys
        TinkerFluids.moltenBronze.getTag()).addTag(TinkerFluids.moltenBrass.getTag()).addTag(TinkerFluids.moltenElectrum.getTag()).addTag(TinkerFluids.moltenInvar.getTag()).addTag(TinkerFluids.moltenConstantan.getTag()).addTag(TinkerFluids.moltenPewter.getTag()).addTag(// thermal alloys
        TinkerFluids.moltenEnderium.getTag()).addTag(TinkerFluids.moltenLumium.getTag()).addTag(TinkerFluids.moltenSignalum.getTag()).addTag(// mekanism alloys
        TinkerFluids.moltenRefinedGlowstone.getTag()).addTag(TinkerFluids.moltenRefinedObsidian.getTag()).addTag(// cosmere alloys
        TinkerFluids.moltenNicrosil.getTag()).addTag(TinkerFluids.moltenDuralumin.getTag()).addTag(TinkerFluids.moltenBendalloy.getTag()).addTag(// Twilight alloys
        TinkerFluids.moltenSteeleaf.getTag());

    this.tag(TinkerTags.Fluids.LARGE_GEM_TOOLTIPS).addTag(TinkerFluids.moltenEmerald.getTag()).addTag(TinkerFluids.moltenDiamond.getTag());
    this.tag(TinkerTags.Fluids.SMALL_GEM_TOOLTIPS).addTag(TinkerFluids.moltenQuartz.getTag()).addTag(TinkerFluids.moltenAmethyst.getTag());
    this.tag(MantleTags.Fluids.SOUP).addTag(TinkerFluids.meatSoup.getTag()).addOptionalTag(TinkerTags.Fluids.SOUP_TOOLTIPS.location());

    // hide upcoming fluids
    tag(TinkerTags.Fluids.HIDDEN_IN_RECIPE_VIEWERS).add(TinkerFluids.moltenSoulsteel.get());
    // hide upcoming fluids that require NBT. Can expand this list if other mods report problems
    tag(TinkerTags.Fluids.HIDE_IN_CREATIVE_TANKS).add(TinkerFluids.potion.get()).addTag(TinkerTags.Fluids.HIDDEN_IN_RECIPE_VIEWERS);
  }

  @Override
  public String getName() {
    return "Tinkers Construct Fluid TinkerTags";
  }

  /** Adds tags for an unplacable fluid */
  private void fluidTag(FluidObject<?> fluid) {
    tag(Objects.requireNonNull(fluid.getCommonTag())).add(fluid.get());
  }

  /** Adds tags for a placable fluid */
  private void fluidTag(FlowingFluidObject<?> fluid) {
    tag(fluid.getLocalTag()).add(fluid.getStill(), fluid.getFlowing());
    TagKey<Fluid> tag = fluid.getCommonTag();
    if (tag != null) {
      tag(tag).addTag(fluid.getLocalTag());
    }
  }
}
