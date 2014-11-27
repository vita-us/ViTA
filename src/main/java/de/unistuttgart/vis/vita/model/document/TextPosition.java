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

  /**
   * Creates a new instance of TextPosition.
   * <p>Use factory methods
   * {@link TextPosition#fromGlobalOffset(Chapter, int)} and
   * {@link TextPosition#fromLocalOffset(Chapter, int)} instead to avoid misunderstandings
   * concerning the offsets. </p>
   */
  protected TextPosition() {
    // no-argument constructor needed for JPA
  }

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
   * @param chapter - the chapter this TextPosition lies in
   * @param pOffset - the offset of this TextPosition within the chapter
   */
  public static TextPosition fromLocalOffset(Chapter chapter, int localOffset) {
    if (chapter == null)
      throw new NullPointerException("chapter is null");
    return new TextPosition(
        chapter,
        chapter.getRange().getStart().getOffset() + localOffset);
  }

  /**
   * Creates a new TextPosition by specifying the chapter and the document-wide character offset
   *
   * @param chapter - the chapter this TextPosition lies in
   * @param globalOffset - the global offset of this TextPosition within the document
   */
  public static TextPosition fromGlobalOffset(Chapter chapter, int globalOffset) {
    if (chapter == null)
      throw new NullPointerException("chapter is null");
    return new TextPosition(
        chapter,
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
   * Gets the offset relative to the enclosing chapter
   * @return the local offset
   */
  public int getLocalOffset() {
    return offset - chapter.getRange().getStart().getOffset();
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
    // do not compare chapters, because the position between to chapters may be attributed to two
    // different chapters, and they are still the same TextPosition
    return other.offset == this.offset;
  }

  @Override
  public int hashCode() {
    // do not compare chapters, because the position between to chapters may be attributed to two
    // different chapters, and they are still the same TextPosition
    return new HashCodeBuilder().append(offset).hashCode();
  }

  @Override
  public String toString() {
    return String.format("Pos %d %s", offset, chapter);
  }

  /**
   * Returns the text position further to the end of the document
   * @param a
   * @param b
   * @return either a or b
   */
  public static TextPosition max(TextPosition a, TextPosition b) {
    if (a.compareTo(b) > 0)
      return a;
    return b;
  }

  /**
   * Returns the text position further at the beginning of the document
   * @param a
   * @param b
   * @return either a or b
   */
  public static TextPosition min(TextPosition a, TextPosition b) {
    if (a.compareTo(b) < 0)
      return a;
    return b;
  }
}
