package slimeknights.mantle.client.book.transformer;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.SectionData;

/** A transformer that only touches sections with a given name */
public abstract class SectionTransformer extends BookTransformer {
  protected final String sectionName;

  public SectionTransformer(String sectionName) {
    this.sectionName = sectionName;
  }

  /** Name of the section this transformer applies to */
  public String getSectionName() {
    return this.sectionName;
  }

  /** Runs over one matching section */
  public abstract void transform(BookData book, SectionData sectionData);

  @Override
  public void transform(BookData book) {
    for (SectionData section : book.sections) {
      if (section.name.equalsIgnoreCase(this.sectionName)) {
        this.transform(book, section);
      }
    }
  }
}
