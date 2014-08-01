package de.unistuttgart.vis.vita.model.text;

import de.unistuttgart.vis.vita.model.document.Chapter;

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
    this.chapter = pChapter;
    this.offset = pOffset;
    this.progress = calculateProgress();
  }

  private double calculateProgress() {
    // TODO Auto-generated method stub
    return 0;
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
   * @return the progress the relative position in the Document as a value between zero and one
   */
  public double getProgress() {
    return progress;
  }

}
