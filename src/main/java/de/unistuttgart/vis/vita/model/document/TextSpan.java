package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Defines the bounds of a text block with a specific start and end. Is not aware of the actual text
 * within the bounds.
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "TextSpan.findAllTextSpans",
      query = "SELECT ts "
      + "FROM TextSpan ts"),
      
  @NamedQuery(name = "TextSpan.findTextSpanById",
      query = "SELECT ts "
      + "FROM TextSpan ts "
      + "WHERE ts.id = :textSpanId"),
})
public class TextSpan implements Comparable<TextSpan> {

  // constants
  private static final int MIN_LENGTH = 0;

  @Id
  @GeneratedValue
  private int id;

  @Embedded
  private TextPosition start;

  @Embedded
  private TextPosition end;

  private int length;
  
  /**
   * Creates a new TextSpan.
   */
  public TextSpan() {
    // zero-parameter constructor needed for JPA
  }

  /**
   * Creates a new instance of TextSpan with the given start and end position in the text.
   *
   * @param pStart - the TextPosition where the new TextSpan should begin
   * @param pEnd - the TextPosiotion where the new TextSpan should end
   */
  public TextSpan(TextPosition pStart, TextPosition pEnd) {
    if (pStart == null || pEnd == null) {
      throw new IllegalArgumentException("positions must not be null!");
    }

    int diff = pEnd.getOffset() - pStart.getOffset();
    if (diff < MIN_LENGTH) {
      throw new IllegalArgumentException("end position must not be before start position!");
    }

    this.start = pStart;
    this.end = pEnd;
    this.length = diff;
  }
  
  /**
   * @return the id of the TextSpan
   */
  public int getId() {
    return id;
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
   * @return the length of the TextSpan in characters
   */
  public int getLength() {
    return length;
  }

  /**
   * Compares first the start positions, and if they are equal, the lengths. Shorter spans
   * are considered smaller than longer ones. This will only produce usable results
   * if both TextPositions are in the same document.
   */
  @Override
  public int compareTo(TextSpan o) {
    if (o == null) {
      return 1;
    }
    
    int cmp = getStart().compareTo(o.getStart());
    if (cmp != 0) {
      return cmp;
    }
    
    return Integer.compare(getLength(), o.getLength());
  }

}
