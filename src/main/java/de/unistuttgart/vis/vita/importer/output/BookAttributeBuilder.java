package de.unistuttgart.vis.vita.importer.output;

import java.util.List;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;

/**
 * Computes the remaining attributes of the book, which are not text or metadata, for example range
 * (@link Chapter#getRange()) and length.
 */
public class BookAttributeBuilder extends AbstractBuilder {
  private final List<DocumentPart> parts;

  /**
   * This Attribute Builder will compute attributes for the given parts.
   * 
   * @param parts All parts of the book.
   */
  public BookAttributeBuilder(List<DocumentPart> parts) {
    this.parts = parts;
  }

  /**
   * Sets/Updates the attributes.
   */
  public void buildAttributes() {
    setChapterLength();
    setChapterRange();
  }

  /**
   * Set the length of all chapters.
   */
  private void setChapterLength() {
    for (DocumentPart part : this.parts) {
      for (Chapter chapter : part.getChapters()) {
        int length = chapter.getText().length();
        chapter.setLength(length);
      }
    }
  }

  /**
   * Set the range of all chapters.
   */
  private void setChapterRange() {
    int currentPosition = 0;
    for (DocumentPart part : this.parts) {
      for (Chapter chapter : part.getChapters()) {
        TextPosition startPosition = TextPosition.fromGlobalOffset(chapter, currentPosition);
        currentPosition += chapter.getLength();
        TextPosition endPosition = TextPosition.fromGlobalOffset(chapter, currentPosition);
        chapter.setRange(new TextSpan(startPosition, endPosition));
      }
    }
  }
}
