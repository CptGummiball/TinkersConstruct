package slimeknights.mantle.client.model.generators;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.ExistingFileHelper;

/**
 * Reference to a model file by location, checked or unchecked. Port of Forge's
 * {@code net.minecraftforge.client.model.generators.ModelFile} for the mantle datagen layer;
 * the JSON these builders write is validated byte-for-byte against the Forge-generated tree.
 */
public abstract class ModelFile {
  protected ResourceLocation location;

  protected ModelFile(ResourceLocation location) {
    this.location = location;
  }

  protected abstract boolean exists();

  public ResourceLocation getLocation() {
    assertExistence();
    return location;
  }

  /** Asserts that this model exists, throwing if it cannot be found */
  public void assertExistence() {
    Preconditions.checkState(exists(), "Model at %s does not exist", location);
  }

  public ResourceLocation getUncheckedLocation() {
    return location;
  }

  /** Model reference that is never validated */
  public static class UncheckedModelFile extends ModelFile {
    public UncheckedModelFile(String location) {
      this(ResourceLocation.parse(location));
    }

    public UncheckedModelFile(ResourceLocation location) {
      super(location);
    }

    @Override
    protected boolean exists() {
      return true;
    }
  }

  /** Model reference validated against the existing file helper */
  public static class ExistingModelFile extends ModelFile {
    private final ExistingFileHelper existingHelper;

    public ExistingModelFile(ResourceLocation location, ExistingFileHelper existingHelper) {
      super(location);
      this.existingHelper = existingHelper;
    }

    @Override
    protected boolean exists() {
      // a path that already carries an extension is checked as-is, otherwise .json is appended
      if (getUncheckedLocation().getPath().contains(".")) {
        return existingHelper.exists(getUncheckedLocation(), ModelProvider.MODEL_WITH_EXTENSION);
      }
      return existingHelper.exists(getUncheckedLocation(), ModelProvider.MODEL);
    }
  }
}
