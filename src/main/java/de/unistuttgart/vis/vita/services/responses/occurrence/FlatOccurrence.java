package de.unistuttgart.vis.vita.services.responses.occurrence;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds data about an Occurrence, including an absolute start and end position and a length.
 */
@XmlRootElement
public class FlatOccurrence {
  
  private AbsoluteTextPosition start;
  private AbsoluteTextPosition end;
  private int length;
  
  /**
   * Creates a new instance of Occurrence, setting all attributes to default values.
   */
  public FlatOccurrence() {
    // zero-argument constructor needed 
  }
  
  /**
   * @return the absolute start position of this occurrence
   */
  public AbsoluteTextPosition getStart() {
    return start;
  }
  
  /**
   * Sets the AbsoluteTextPosition for the start of this occurrence.
   * 
   * @param occStart - the absolute position in the document where this occurrence starts
   */
  public void setStart(AbsoluteTextPosition occStart) {
    this.start = occStart;
  }
  
  /**
   * @return the absolute end position of this occurrence
   */
  public AbsoluteTextPosition getEnd() {
    return end;
  }
  
  /**
   * Sets the AbsoluteTextPosition for the end of this occurrence.
   * 
   * @param occEnd - the absolute position in the document where this occurrence ends
   */
  public void setEnd(AbsoluteTextPosition occEnd) {
    this.end = occEnd;
  }
  
  /**
   * @return the length of this occurrence
   */
  public int getLength() {
    return length;
  }
  
  /**
   * Sets the length of this occurrence.
   * 
   * @param occLength - the length of this occurrence
   */
  public void setLength(int occLength) {
    this.length = occLength;
  }
  
  @Override
  public String toString() {
    return "FlatOccurrence: " + start.getOffset() + " - " + end.getOffset(); 
  }

}
