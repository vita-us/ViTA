package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Represents the position of a single character in the text of a Document. It is aware of the
 * chapter it occurs in as well as the relative position in the whole document.
 */
@Embeddable
public class TextPosition implements Comparable<TextPosition> {
  
  @ManyToOne
  private Chapter chapter;
  private int offset;
  
  private TextPosition() { }

  /**
   * Creates a new TextPosition by specifying the chapter and the document-wide character offset
   *
   * @param pChapter - the chapter this TextPosition lies in
   * @param pOffset - the global offset of this TextPosition within the document
   */
  private TextPosition(Chapter pChapter, int pOffset) {
    // This constructor is private to prevent confusion about global/local offsets
    // The factory methods should be used instead.
    
    if (pOffset < 0) {
      throw new IllegalArgumentException("offset must not be negative!");
    }

    this.chapter = pChapter;
    this.offset = pOffset;
  }

  /**
   * Creates a new TextPosition by specifying the chapter and the chapter-local character offset
   *
   * @param pChapter - the chapter this TextPosition lies in
   * @param pOffset - the offset of this TextPosition within the chapter
   */
  public static TextPosition fromLocalOffset(Chapter pChapter, int localOffset) {
    return new TextPosition(
        pChapter,
        pChapter.getRange().getStart().getOffset() + localOffset);
  }

  /**
   * Creates a new TextPosition by specifying the chapter and the document-wide character offset
   *
   * @param pChapter - the chapter this TextPosition lies in
   * @param globalOffset - the global offset of this TextPosition within the document
   */
  public static TextPosition fromGlobalOffset(Chapter pChapter, int globalOffset) {
    return new TextPosition(
        pChapter,
        globalOffset);
  }

  /**
   * @return the chapter this TextPosition lies in
   */
  public Chapter getChapter() {
    return chapter;
  }

  /**
   * @return the global offset of this TextPosition within the document
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Compares this position to the given other one. This will only produce usable results
   * if both TextPositions are in the same document.
   */
  @Override
  public int compareTo(TextPosition o) {
    if (o == null) {
      return 1;
    }
    
    return Integer.compare(offset, o.offset);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof TextPosition)) {
      return false;
    }
    
    TextPosition other = (TextPosition)obj;
    // TODO include chapter/document as soon as they have hashCode/equals implemented
    return /*other.chapter.equals(chapter) && */other.offset == this.offset;
  }
  
  @Override
  public int hashCode() {
    // TODO include chapter/document as soon as they have hashCode/equals implemented
    return new HashCodeBuilder().append(offset).append(chapter).hashCode();
  }
  
  @Override
  public String toString() {
    return String.format("Pos %d %s", offset, chapter);
  }
}
