package slimeknights.tconstruct.fabric.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.TConstruct;

import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * Honours Forge's {@code "remove"} tag-file extension, which the shipped data uses: Tinkers
 * strips pumpkins and melons from {@code minecraft:mineable/axe} (its own harvest logic
 * covers them) and from its scythe tag. Vanilla's codec silently drops unknown keys, so
 * without this the removals never applied on Fabric.
 *
 * <p>Runs once after vanilla collected all entries and re-reads only the tag files of this
 * loader's directory. Removals strip matching non-tag entries regardless of which pack added
 * them; Forge additionally tracks pack order, which only diverges when a lower pack removes
 * what a higher pack re-adds — no shipped or pack data does that.
 */
@Mixin(TagLoader.class)
public class TagLoaderRemoveMixin {

  @Shadow @Final private String directory;

  @Inject(method = "load", at = @At("RETURN"))
  private void tconstruct$applyRemovals(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
    Map<ResourceLocation, List<TagLoader.EntryWithSource>> tags = cir.getReturnValue();
    FileToIdConverter converter = FileToIdConverter.json(this.directory);
    for (Map.Entry<ResourceLocation, List<Resource>> resourceEntry : converter.listMatchingResourceStacks(resourceManager).entrySet()) {
      ResourceLocation tagId = converter.fileToId(resourceEntry.getKey());
      List<TagLoader.EntryWithSource> entries = tags.get(tagId);
      if (entries == null) {
        continue;
      }
      for (Resource resource : resourceEntry.getValue()) {
        try (Reader reader = resource.openAsReader()) {
          JsonElement json = JsonParser.parseReader(reader);
          if (json.isJsonObject() && json.getAsJsonObject().has("remove")) {
            for (JsonElement removed : GsonHelper.getAsJsonArray(json.getAsJsonObject(), "remove")) {
              // the forge datagen dialect writes plain element ids
              ResourceLocation removedId = ResourceLocation.parse(GsonHelper.convertToString(removed, "remove entry"));
              entries.removeIf(entry -> {
                TagEntry tagEntry = entry.entry();
                return !tagEntry.tag && tagEntry.id.equals(removedId);
              });
            }
          }
        } catch (Exception e) {
          TConstruct.LOG.error("Failed to process tag removals for {} from pack {}", tagId, resource.sourcePackId(), e);
        }
      }
    }
  }
}
