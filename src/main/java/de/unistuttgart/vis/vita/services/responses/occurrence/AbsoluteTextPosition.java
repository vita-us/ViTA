package de.unistuttgart.vis.vita.services.responses.occurrence;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds the data of an absolute text position in a Document.
 */
@XmlRootElement
public class AbsoluteTextPosition {

  private String chapter;
  private int offset;
  private Double progress;
  
  /**
   * Creates a new instance of AbsoluteTextPosition
   */
  public AbsoluteTextPosition() {
    // zero-argument constructor needed
  }
  
  /**
   * Creates a new AbsoluteTextPosition laying in the chapter with the given id at an also given 
   * offset and progress.
   * 
   * @param chapterId - the id of the chapter the new AbsoluteTextPosition should lay in
   * @param absoluteOffset - the global offset of the new AbsoluteTextPosition
   * @param docProgress - the position in the document as a number between 0 and 1
   */
  public AbsoluteTextPosition(String chapterId, int absoluteOffset, double docProgress) {
    this.chapter = chapterId;
    this.offset = absoluteOffset;
    this.progress = docProgress;
  }
  
  /**
   * @return the id of the chapter this AbsoluteTextPosition lays in
   */
  public String getChapter() {
    return chapter;
  }
  
  /**
   * Sets the id of the chapter this AbsoluteTextPosition lays in.
   * 
   * @param chapterId - the id of the chapter this AbsoluteTextPosition lays in
   */
  public void setChapter(String chapterId) {
    this.chapter = chapterId;
  }
  
  /**
   * @return the global offset of this AbsoluteTextPosition
   */
  public int getOffset() {
    return offset;
  }
  
  /**
   * Sets the offset of this AbsoluteTextPosition.
   * 
   * @param globalOffset - the global offset of this AbsoluteTextPosition
   */
  public void setOffset(int globalOffset) {
    this.offset = globalOffset;
  }
  
  /**
   * @return the progress of this AbsoluteTextPosition meaning the position in the document as a
   *         number between 0.0 and 1.0
   */
  public Double getProgress() {
    return progress;
  }
  
  /**
   * Sets the progress of this AbsoluteTextPosition.
   * 
   * @param docProgress - a number between 0.0 and 1.0 indicating the position in the document
   */
  public void setProgress(Double docProgress) {
    this.progress = docProgress;
  }

}
