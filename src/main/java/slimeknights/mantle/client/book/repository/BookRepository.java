package slimeknights.mantle.client.book.repository;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.SectionData;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Where a book's files come from.
 *
 * <p>Split out from the book itself so a book can be assembled from several sources — the mod's
 * own assets plus anything an addon contributes — and so generated sections can claim
 * {@link #DUMMY} as a source without pretending to have files behind them.
 */
public abstract class BookRepository {
  /** Source for sections built in code rather than loaded from files */
  public static final BookRepository DUMMY = new DummyRepository();

  /** Sections this repository contributes, in book order */
  public abstract List<SectionData> getSections();

  /**
   * Resolves a path written in the book data to a resource location.
   * @param path  Path relative to the book root
   * @return  Location, or null if the path is unusable
   */
  @Nullable
  public ResourceLocation getResourceLocation(@Nullable String path) {
    return this.getResourceLocation(path, false);
  }

  /**
   * Resolves a path written in the book data to a resource location.
   * @param path  Path relative to the book root
   * @param safe  If true, no error is logged when the path is unusable
   */
  @Nullable
  public ResourceLocation getResourceLocation(@Nullable String path, boolean safe) {
    return null;
  }

  /** True if the given location can be read from this repository */
  public boolean resourceExists(@Nullable ResourceLocation location) {
    return false;
  }

  /** Opens the given resource, or null if it does not exist */
  @Nullable
  public Resource getResource(@Nullable ResourceLocation location) {
    return null;
  }

  /** Reads a resource to a string, dropping {@code //} comment lines */
  public String resourceToString(@Nullable Resource resource) {
    return this.resourceToString(resource, true);
  }

  /**
   * Reads a resource to a string.
   *
   * <p>Book JSON is hand-written and several files carry {@code //} comments, which Gson rejects
   * even in lenient mode, so they are stripped here rather than in every caller.
   */
  public String resourceToString(@Nullable Resource resource, boolean skipComments) {
    if (resource == null) {
      return "";
    }
    try (InputStream stream = resource.open();
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
      StringBuilder builder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        String trimmed = line.trim();
        if (skipComments && (trimmed.startsWith("//") || trimmed.startsWith("#"))) {
          continue;
        }
        builder.append(line).append('\n');
      }
      return builder.toString();
    } catch (IOException e) {
      Mantle.logger.error("Failed to read book resource", e);
      return "";
    }
  }

  /** Repository backing sections that were built in code */
  private static class DummyRepository extends BookRepository {
    @Override
    public List<SectionData> getSections() {
      return List.of();
    }
  }
}
