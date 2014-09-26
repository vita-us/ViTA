package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.unistuttgart.vis.vita.services.responses.occurrence.AbsoluteTextPosition;

/**
 * Represents the position of a single character in the text of a Document. It is aware of the
 * chapter it occurs in as well as the relative position in the whole document.
 */
@Embeddable
public class TextPosition implements Comparable<TextPosition> {
  @ManyToOne
  private Chapter chapter;

  private int offset;

  @Transient
  private Double progress;
  
  /**
   * Creates a new TextPosition setting all fields to default values.
   */
  public TextPosition() {
    this.chapter = new Chapter();
  }

  /**
   * Creates a new TextPosition by specifying the chapter and the document-wide character offset
   *
   * @param pChapter - the chapter this TextPosition lies in
   * @param pOffset - the global offset of this TextPosition within the document
   */
  public TextPosition(Chapter pChapter, int pOffset) {
    if (pOffset < 0) {
      throw new IllegalArgumentException("offset must not be negative!");
    }

    this.chapter = pChapter;
    this.offset = pOffset;
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
   * Converts this (relative) TextPosition to an AbsoluteTextPosition in a document with the given 
   * length.
   * 
   * @param docLength - the length of the complete Document in characters
   * @return the AbsoluteTextPosition
   */
  public AbsoluteTextPosition toAbsoluteTextPosition(int docLength) {
    Chapter chapter = getChapter();
    
    int chapterOffset = chapter.getRange().getStart().getOffset();
    int relativeOffset = getOffset();
    int absoluteOffset = chapterOffset + relativeOffset;
    
    double docProgress = absoluteOffset / docLength;
    
    return new AbsoluteTextPosition(chapter.getId(), absoluteOffset, docProgress);
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
    return String.format("Pos %d", offset, chapter);
  }
}
