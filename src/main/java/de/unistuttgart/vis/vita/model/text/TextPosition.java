package de.unistuttgart.vis.vita.model.text;

import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Represents a position in the text of a Document. It is specified by the chapter in lays in and
 * the offset from the start of this Chapter.
 * 
 * @author Marc Weise
 * @version 0.1 31.07.2014
 */
public class TextPosition {

  // attributes
  private Chapter chapter;
  private int offset;
  private double progress;
  
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
   * @return the chapter
   */
  public Chapter getChapter() {
    return chapter;
  }

  /**
   * @return the offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * @return the progress
   */
  public double getProgress() {
    return progress;
  }

}
