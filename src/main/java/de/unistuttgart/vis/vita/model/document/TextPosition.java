package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Represents a position in the text of a Document. It is specified by the chapter in lays in and
 * the offset from the start of this Chapter.
 */
@Embeddable
public class TextPosition {
  @ManyToOne
  private Chapter chapter;
  
  private int offset;
  
  @Transient
  private Double progress;

  /**
   * Creates a new TextPosition which lays at a given offset in an also given Chapter.
   *
   * @param pChapter - the chapter this TextPosition lays in
   * @param pOffset  - the global offset of this TextPosition
   */
  public TextPosition(Chapter pChapter, int pOffset) {
    if (pOffset < 0) {
      throw new IllegalArgumentException("offset must not be negative!");
    }

    this.chapter = pChapter;
    this.offset = pOffset;
  }

  /**
   * Calculates the relative position of this TextPosition in the whole Document
   *
   * @return relative position in the Document, or NaN if length of Document is zero
   */
  private double calculateProgress() {
    if (chapter == null) {
      return Double.NaN;
    }
    
    double docLength = chapter.document.getMetrics().getCharacterCount();
    
    if (docLength == 0) {
      return Double.NaN;
    }
    
    return offset / docLength;
  }

  /**
   * @return the chapter this TextPosition lays in
   */
  public Chapter getChapter() {
    return chapter;
  }

  /**
   * @return the global offset of this TextPosition
   */
  public int getOffset() {
    return offset;
  }

  /**
   * @return the progress the relative position in the Document as a value between zero and one, or
   * NaN if Chapter is null or length of Document is 0
   */
  public double getProgress() {
    if (progress == null){
      progress = calculateProgress();
    }
    
    return progress;
  }

}
