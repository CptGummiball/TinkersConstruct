package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.BannerPatternTextures;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map.Entry;

/** Sprite source creating modifier textures for banners using shield banner textures */
public record ShieldBannerModifierSpriteSource(int cropX, int cropY, int cropWidth, int cropHeight, ResourceLocation destinationPrefix, int offsetX, int offsetY, int outSize) implements SpriteSource {
  private static final Codec<Integer> NON_NEGATIVE = ExtraCodecs.intRange(0, Integer.MAX_VALUE);
  private static final Codec<Integer> SHIELD_SIZE = ExtraCodecs.intRange(0, 64);
  public static final MapCodec<ShieldBannerModifierSpriteSource> CODEC = RecordCodecBuilder.<ShieldBannerModifierSpriteSource>mapCodec(inst -> inst.group(
    SHIELD_SIZE.fieldOf("crop_x").forGetter(ShieldBannerModifierSpriteSource::cropX),
    SHIELD_SIZE.fieldOf("crop_y").forGetter(ShieldBannerModifierSpriteSource::cropY),
    SHIELD_SIZE.fieldOf("crop_width").forGetter(ShieldBannerModifierSpriteSource::cropWidth),
    SHIELD_SIZE.fieldOf("crop_height").forGetter(ShieldBannerModifierSpriteSource::cropHeight),
    ResourceLocation.CODEC.fieldOf("destination_prefix").forGetter(ShieldBannerModifierSpriteSource::destinationPrefix),
    NON_NEGATIVE.fieldOf("offset_x").forGetter(ShieldBannerModifierSpriteSource::offsetX),
    NON_NEGATIVE.fieldOf("offset_y").forGetter(ShieldBannerModifierSpriteSource::offsetY),
    NON_NEGATIVE.fieldOf("output_size").forGetter(ShieldBannerModifierSpriteSource::outSize)
  ).apply(inst, ShieldBannerModifierSpriteSource::new)).validate(source -> {
    if (source.cropX + source.cropWidth >= 64 || source.cropY + source.cropHeight >= 64) {
      return DataResult.error(() -> "Invalid banner shield modifier sprite source: crop region must be within 64 by 64");
    } else if (source.offsetX + source.cropWidth >= source.outSize || source.offsetY + source.cropHeight >= source.outSize) {
      return DataResult.error(() -> "Invalid banner shield modifier sprite source: crop result must be placed within output size " + source.outSize);
    }
    return DataResult.success(source);
  });
  /** Folder the shield pattern textures live in */
  private static final FileToIdConverter SHIELD_TEXTURES = BannerPatternTextures.SHIELD_TEXTURES;
  /** Registered type set on init */
  private static SpriteSourceType TYPE = null;

  /** Registers this sprite source */
  @Internal
  public static SpriteSourceType register() {
    if (TYPE == null) {
      // vanilla's own register() forces the minecraft namespace onto the name, so the type
      // goes into the map directly to keep the id the shipped atlas definition names
      TYPE = new SpriteSourceType(CODEC);
      SpriteSources.TYPES.put(TConstruct.getResource("shield_banner_to_modifier"), TYPE);
    }
    return TYPE;
  }

  @Override
  public void run(ResourceManager manager, Output output) {
    // the pattern list moved to BannerPatternTextures, which the banner modifier model reads too;
    // its header records why the registry cannot be asked for it during resource loading
    int count = 0;
    for (Entry<ResourceLocation,Resource> entry : BannerPatternTextures.listResources(manager)) {
      ResourceLocation input = entry.getKey();
      ResourceLocation assetId = SHIELD_TEXTURES.fileToId(input);
      LazyLoadedImage image = new LazyLoadedImage(input, entry.getValue(), 1);
      ResourceLocation destination = destinationPrefix.withSuffix(MaterialRenderInfo.getSuffix(assetId));
      output.add(destination, new BannerModifierSpriteSupplier(image, input, destination));
      count++;
    }
    TConstruct.LOG.info("Generated {} shield banner modifier sprites under {}", count, destinationPrefix);
  }

  @Override
  public SpriteSourceType type() {
    return register();
  }

  /** Generates a cropped sprite lazily */
  @RequiredArgsConstructor
  private class BannerModifierSpriteSupplier implements SpriteSupplier {
    private final LazyLoadedImage original;
    private final ResourceLocation input, output;

    @Nullable
    @Override
    public SpriteContents apply(SpriteResourceLoader loader) {
      try {
        // its possible the original is bigger than we expect due to HD pack, if so scale it accordingly
        // we only support scaling if it is a multiple of width
        NativeImage original = this.original.get();
        int scale = original.getWidth() / 64;
        if (scale == 0) {
          TConstruct.LOG.warn("Unable to crop {} to produce {} as texture size is less than 64", input, output);
        } else {
          NativeImage generated = new NativeImage(outSize * scale, outSize * scale, true);
          original.copyRect(generated, cropX * scale, cropY * scale, offsetX * scale, offsetY * scale, cropWidth * scale, cropHeight * scale, false, false);
          return new SpriteContents(this.output, new FrameSize(generated.getWidth(), generated.getHeight()), generated, ResourceMetadata.EMPTY);
        }
      } catch (IllegalArgumentException | IOException ex) {
        TConstruct.LOG.warn("Unable to crop {} to produce {}", this.input, this.output, ex);
      } finally {
        this.original.release();
      }
      return null;
    }
  }
}
