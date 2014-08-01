package de.unistuttgart.vis.vita.model.document;

/**
 * Represents a position in the text of a Document. It is specified by the chapter in lays in and
 * the offset from the start of this Chapter.
 * 
 * @author Marc Weise
 * @version 0.2 01.08.2014
 */
public class TextPosition {

  // attributes
  private Chapter chapter;
  private int offset;
  private double progress;

  /**
   * Creates a new TextPosition which lays at a given offset in an also given Chapter.
   * 
   * @param pChapter - the chapter this TextPosition lays in
   * @param pOffset - the global offset of this TextPosition
   */
  public TextPosition(Chapter pChapter, int pOffset) {
    if (pOffset < 0) {
      throw new IllegalArgumentException("offset must not be negative!");
    }

    this.chapter = pChapter;
    this.offset = pOffset;

    double progress = 0.0;
    if (pChapter == null) {
      progress = Double.NaN;
    } else {
      progress = calculateProgress();
    }
    
    this.progress = progress;

  }

  /**
   * Calculates the relative position of this TextPosition in the whole Document
   * 
   * @return relative position in the Document, or NaN if length of Document is zero
   */
  private double calculateProgress() {
    double docLength = chapter.document.getMetrics().getCharacterCount();

    double progress = 0.0;
    if (docLength > 0) {
      progress = (offset / docLength);
    } else {
      progress = Double.NaN;
    }

    return progress;
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
   *         NaN if Chapter is null or length of Document is 0
   */
  public double getProgress() {
    return progress;
  }

}
