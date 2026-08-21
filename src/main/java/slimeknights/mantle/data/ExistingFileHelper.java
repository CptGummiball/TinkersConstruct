package slimeknights.mantle.data;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Stand-in for Forge's {@code net.minecraftforge.common.data.ExistingFileHelper}, which datagen
 * providers take to check for and read files from existing packs.
 *
 * <p>At runtime every caller passes null and reads through a {@link ResourceManager} instead;
 * for datagen, {@link #forDatagen} builds managers over the vanilla jar plus the project's
 * resource directories, mirroring Forge's {@code --existing} arguments. Files written during
 * the run are registered through {@link #trackGenerated} so later existence checks pass even
 * though the file is not part of any input pack.
 */
public class ExistingFileHelper {
  @Nullable
  private final ResourceManager clientResources;
  @Nullable
  private final ResourceManager serverData;
  private final Set<ResourceLocation> generatedClient = new HashSet<>();
  private final Set<ResourceLocation> generatedServer = new HashSet<>();

  public ExistingFileHelper(@Nullable ResourceManager clientResources, @Nullable ResourceManager serverData) {
    this.clientResources = clientResources;
    this.serverData = serverData;
  }

  /**
   * Builds a helper for datagen: the vanilla jar's assets and data plus the given existing
   * resource roots (each containing {@code assets/} and/or {@code data/}).
   */
  public static ExistingFileHelper forDatagen(List<Path> existingRoots) {
    return new ExistingFileHelper(buildManager(PackType.CLIENT_RESOURCES, existingRoots), buildManager(PackType.SERVER_DATA, existingRoots));
  }

  private static ResourceManager buildManager(PackType type, List<Path> existingRoots) {
    List<PackResources> packs = new ArrayList<>();
    packs.add(new VanillaPackResourcesBuilder()
                .setMetadata(BuiltInMetadata.of())
                .exposeNamespace("minecraft")
                .applyDevelopmentConfig()
                .pushJarResources()
                .build(new PackLocationInfo("vanilla", Component.literal("vanilla"), PackSource.BUILT_IN, Optional.<KnownPack>empty())));
    for (Path root : existingRoots) {
      if (Files.isDirectory(root)) {
        packs.add(new PathPackResources(new PackLocationInfo(root.toString(), Component.literal(root.toString()), PackSource.BUILT_IN, Optional.<KnownPack>empty()), root));
      }
    }
    return new MultiPackResourceManager(type, packs);
  }

  @Nullable
  private ResourceManager manager(PackType packType) {
    return packType == PackType.CLIENT_RESOURCES ? clientResources : serverData;
  }

  private Set<ResourceLocation> generated(PackType packType) {
    return packType == PackType.CLIENT_RESOURCES ? generatedClient : generatedServer;
  }

  /** Checks whether the given resource exists in the known packs */
  public boolean exists(ResourceLocation loc, PackType packType) {
    if (generated(packType).contains(loc)) {
      return true;
    }
    ResourceManager manager = manager(packType);
    return manager != null && manager.getResource(loc).isPresent();
  }

  /** Checks whether the given resource exists, building the path from prefix and suffix */
  public boolean exists(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) {
    return exists(ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), pathPrefix + "/" + loc.getPath() + pathSuffix), packType);
  }

  /** Gets the given resource, throwing if missing */
  public Resource getResource(ResourceLocation loc, PackType packType) throws IOException {
    ResourceManager manager = manager(packType);
    if (manager == null) {
      throw new FileNotFoundException("No resource manager for " + packType + "; cannot read " + loc);
    }
    return manager.getResource(loc).orElseThrow(() -> new FileNotFoundException(loc.toString()));
  }

  /** Gets the given resource, building the path from prefix and suffix */
  public Resource getResource(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) throws IOException {
    return getResource(ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), pathPrefix + "/" + loc.getPath() + pathSuffix), packType);
  }

  /** Marks a resource as generated by this run, so later existence checks pass */
  public void trackGenerated(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) {
    generated(packType).add(ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), pathPrefix + "/" + loc.getPath() + pathSuffix));
  }

  /** Checks whether the given resource exists, expanding the path through the type */
  public boolean exists(ResourceLocation loc, IResourceType type) {
    return exists(loc, type.packType(), type.suffix(), type.prefix());
  }

  /** Gets the given resource, expanding the path through the type */
  public Resource getResource(ResourceLocation loc, IResourceType type) throws IOException {
    return getResource(loc, type.packType(), type.suffix(), type.prefix());
  }

  /** Marks a resource as generated, expanding the path through the type */
  public void trackGenerated(ResourceLocation loc, IResourceType type) {
    trackGenerated(loc, type.packType(), type.suffix(), type.prefix());
  }

  /** Forge's interface name for the record below, kept for source parity */
  public interface IResourceType {
    PackType packType();
    String suffix();
    String prefix();
  }

  /** Pack type plus the path pieces around an id, mirroring Forge's nested type of the same name */
  public record ResourceType(PackType packType, String suffix, String prefix) implements IResourceType {}
}
