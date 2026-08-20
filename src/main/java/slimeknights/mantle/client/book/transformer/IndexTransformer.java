package slimeknights.mantle.client.book.transformer;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.ContentPageIconList;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.client.screen.book.element.SizedBookElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Puts a grid of section icons at the front of the book.
 *
 * <p>Links point at the section rather than at a concrete page: this pass runs before the material
 * and tool sections have any pages, since those are generated from datapack contents by later
 * transformers. Resolving the link when it is clicked lands on whatever the section's first page
 * turned out to be.
 */
public class IndexTransformer extends BookTransformer {
  public static final IndexTransformer INSTANCE = new IndexTransformer();

  /** Name of the generated section */
  public static final String INDEX_SECTION = "index";

  private IndexTransformer() {}

  @Override
  public void transform(BookData book) {
    if (book.sections.isEmpty() || book.findSection(INDEX_SECTION) != null) {
      return;
    }

    // remember the sections to link before the index joins them
    List<SectionData> linked = new ArrayList<>(book.sections);

    SectionData index = new SectionData(INDEX_SECTION);
    index.parent = book;
    index.source = BookRepository.DUMMY;
    book.sections.add(0, index);

    String title = book.strings.getOrDefault(INDEX_SECTION, book.getTitleComponent().getString());
    List<ContentPageIconList> pages = ContentPageIconList.getPagesNeededForItemCount(
      linked.size(), index, title, book.strings.get("index.subtext"));
    if (pages.isEmpty()) {
      return;
    }

    java.util.ListIterator<ContentPageIconList> iterator = pages.listIterator();
    ContentPageIconList current = iterator.next();
    for (SectionData section : linked) {
      SizedBookElement icon = new ItemElement(0, 0, 1F, section.icon == null ? List.<net.minecraft.world.item.ItemStack>of() : section.icon.getItems());
      Component name = Component.literal(section.getTitle());
      while (!current.addLink(icon, name, sectionLink(section)) && iterator.hasNext()) {
        current = iterator.next();
      }
    }
  }

  /**
   * Builds a page reference that names the section but no page inside it.
   *
   * <p>The page lookup falls back to the section's first page for an unknown page name, which is
   * exactly the behaviour wanted here and needs no special case in the link element.
   */
  private static PageData sectionLink(SectionData section) {
    PageData link = new PageData(true);
    link.parent = section;
    link.source = section.source;
    link.name = "";
    return link;
  }
}
