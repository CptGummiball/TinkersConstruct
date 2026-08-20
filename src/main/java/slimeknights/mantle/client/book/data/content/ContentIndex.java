package slimeknights.mantle.client.book.data.content;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.Mantle;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The listing page a section opens with.
 *
 * <p>Its JSON only carries presentation and the two knobs the grouping transformer reads:
 * {@link #hidden} drops pages from the listing, and {@link #operations} inserts group headings and
 * column breaks at named positions. The entries themselves are filled in by the transformer, since
 * only it knows which pages tag injection produced.
 */
public class ContentIndex extends ContentListing {
  public static final transient ResourceLocation ID = Mantle.getResource("index");

  /** Names of pages that should not appear in the listing */
  @Nullable
  public String[] hidden = null;

  /** Edits applied while the listing is built */
  @Nullable
  public List<Operation> operations = null;

  /** True if the named page should be left out of the listing */
  public boolean isHidden(String name) {
    if (this.hidden == null) {
      return false;
    }
    for (String entry : this.hidden) {
      if (entry.equals(name)) {
        return true;
      }
    }
    return false;
  }

  /** Operations understood in the {@code operations} list */
  public enum Action {
    /** Inserts a group heading before the named page */
    @SerializedName("add_group")
    ADD_GROUP,
    /** Starts a new column before the named page */
    @SerializedName("column_break")
    COLUMN_BREAK,
  }

  /**
   * One edit to the generated listing.
   * @param before  Name of the page this edit happens in front of
   * @param action  What to do
   * @param data    Group title, for {@link Action#ADD_GROUP}
   */
  public record Operation(String before, Action action, @Nullable String data) {}
}
