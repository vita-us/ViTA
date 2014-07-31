package de.unistuttgart.vis.vita.model.text;

/**
 * Represents a text block with a specific start and end.
 * 
 * @author Marc Weise
 * @version 0.1 31.07.2014
 */
public class TextSpan {
  
  // attributes
  private final TextPosition start;
  private final TextPosition end;
  private final int length;
  
  /**
   * Creates a new instance of TextSpan with the given start and end position in the text.
   * 
   * @param pStart - the TextPosition where the new TextSpan should begin
   * @param pEnd - the TextPosiotion where the new TextSpan should end
   */
  public TextSpan(TextPosition pStart, TextPosition pEnd) {
    this.start = pStart;
    this.end = pEnd;
    this.length = computeLength();
  }

  private int computeLength() {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * @return the start of the TextSpan
   */
  public TextPosition getStart() {
    return start;
  }

  /**
   * @return the end of the TextSpan
   */
  public TextPosition getEnd() {
    return end;
  }

  /**
   * @return the length of the TextSpan
   */
  public int getLength() {
    return length;
  }

}
