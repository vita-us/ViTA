package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;

/**
 * Defines the bounds of a text block with a specific start and end. Is not aware of the actual text
 * within the bounds.
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "TextSpan.findAllTextSpans",
              query = "SELECT ts "
                    + "FROM TextSpan ts"),
      
  @NamedQuery(name = "TextSpan.findTextSpansForEntity",
              query = "SELECT ts "
                    + "FROM TextSpan ts, Entity e "
                    + "WHERE e.id = :entityId "
                    + "AND ts MEMBER OF e.occurrences"),
      
  @NamedQuery(name = "TextSpan.findTextSpanById",
              query = "SELECT ts "
                    + "FROM TextSpan ts "
                    + "WHERE ts.id = :textSpanId"),
})
public class TextSpan extends AbstractEntityBase implements Comparable<TextSpan> {

  // constants
  private static final int MIN_LENGTH = 0;

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
   * Converts this TextSpan into an Occurrence.
   * 
   * @param docLength - the length of the whole document in characters
   * @return an Occurrence equivalent to this TextSpan
   */
  public Occurrence toOccurrence(int docLength) {
    // create empty Occurrence
    Occurrence occ = new Occurrence();
    
    // convert start and end positions
    occ.setStart(getStart().toAbsoluteTextPosition(docLength));
    occ.setEnd(getEnd().toAbsoluteTextPosition(docLength));
    
    // length stays the same
    occ.setLength(getLength());
    return occ;
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

  /**
   * Indicates if obj is a TextSpan representing the same range as this text span
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof TextSpan)) {
      return false;
    }
    
    TextSpan other = (TextSpan)obj;
    return start.equals(other.start) && end.equals(other.end);
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(start).append(end).hashCode();
  }
  
  @Override
  public String toString() {
    return String.format("Span %s ... %s", start, end);
  }
}
