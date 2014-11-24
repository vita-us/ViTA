package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;
import de.unistuttgart.vis.vita.services.responses.occurrence.AbsoluteTextPosition;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;

/**
 * Defines the bounds of a text block with a specific start and end. Is not
 * aware of the actual text within the bounds.
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "TextSpan.findAllTextSpans", 
      query = "SELECT ts " 
            + "FROM TextSpan ts"),
  
  // for returning the exact spans for an entity in a given range
  @NamedQuery(name = "TextSpan.findTextSpansForEntity", 
    query = "SELECT ts "
          + "FROM TextSpan ts, Entity e " 
          + "WHERE e.id = :entityId "
          + "AND ts MEMBER OF e.occurrences "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd "
          // right ordering
          + "ORDER BY ts.start.offset"),
          
  // for checking the amount of spans for an entity in a given range
  @NamedQuery(name = "TextSpan.getNumberOfTextSpansForEntity", 
  query = "SELECT COUNT(ts) "
        + "FROM TextSpan ts, Entity e " 
        + "WHERE e.id = :entityId "
        + "AND ts MEMBER OF e.occurrences "
        // range checks
        + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
        + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd"),
  
  // for returning the exact spans for an attribute in a given range
  @NamedQuery(name = "TextSpan.findTextSpansForAttribute", 
    query = "SELECT ts "
          + "FROM TextSpan ts, Entity e, Attribute a " 
          + "WHERE e.id = :entityId "
          + "AND a MEMBER OF e.attributes "
          + "AND a.id = :attributeId " 
          + "AND ts MEMBER OF a.occurrences "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd "
          // right ordering
          + "ORDER BY ts.start.offset"),
          
  // for checking the amount of spans for an attribute in a given range
  @NamedQuery(name = "TextSpan.getNumberOfTextSpansForAttribute", 
  query = "SELECT COUNT(ts) "
        + "FROM TextSpan ts, Entity e, Attribute a " 
        + "WHERE e.id = :entityId "
        + "AND a MEMBER OF e.attributes "
        + "AND a.id = :attributeId " 
        + "AND ts MEMBER OF a.occurrences "
        // range checks
        + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
        + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd"),
  
  // for returning the exact spans for an relation in a given range
  @NamedQuery(name = "TextSpan.findTextSpansForRelations", 
    query = "SELECT DISTINCT ts1 "
          + "FROM TextSpan ts1, Entity e " 
          + "INNER JOIN e.occurrences ts2 "
          + "WHERE e.id IN :entityIds "
          // range checks
          + "AND ts1.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts2.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts1.end.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts2.end.offset BETWEEN :rangeStart AND :rangeEnd "
          // intervals have an overlap
          + "AND ((ts2.start.offset > ts1.start.offset " + "AND ts2.start.offset < ts1.end.offset) "
            + "OR (ts1.start.offset > ts2.start.offset " + "AND ts1.start.offset < ts2.end.offset)) "
          // Null checks
          + "AND ts1.start.chapter IS NOT NULL " + "AND ts2.start.chapter IS NOT NULL " 
          + "AND ts1.end.chapter IS NOT NULL " + "AND ts2.end.chapter IS NOT NULL "
          // right ordering
          + "ORDER BY ts1.start.offset"),
  
  // for checking the amount of spans for a relation in a given range
  @NamedQuery(name = "TextSpan.getNumberOfTextSpansForRelations", 
    query = "SELECT DISTINCT COUNT(ts1) "
          + "FROM TextSpan ts1, Entity e " 
          + "INNER JOIN e.occurrences ts2 "
          + "WHERE e.id IN :entityIds "
          // range checks
          + "AND ts1.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts2.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts1.end.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts2.end.offset BETWEEN :rangeStart AND :rangeEnd "
          // intervals have an overlap
          + "AND ((ts2.start.offset > ts1.start.offset " + "AND ts2.start.offset < ts1.end.offset) "
            + "OR (ts1.start.offset > ts2.start.offset " + "AND ts1.start.offset < ts2.end.offset)) "
          // Null checks
          + "AND ts1.start.chapter IS NOT NULL " + "AND ts2.start.chapter IS NOT NULL " 
          + "AND ts1.end.chapter IS NOT NULL " + "AND ts2.end.chapter IS NOT NULL"),

  @NamedQuery(name = "TextSpan.findTextSpanById", query = "SELECT ts " 
      + "FROM TextSpan ts "
      + "WHERE ts.id = :textSpanId")
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
   * Creates a new instance of TextSpan with the given start and end position in
   * the text.
   *
   * @param pStart
   *          - the TextPosition where the new TextSpan should begin
   * @param pEnd
   *          - the TextPosiotion where the new TextSpan should end
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
   * Creates a text span from two offsets within a common chapter
   * @param chapter
   * @param startOffset the start of the span, relative to the chapter beginning
   * @param endOffset the end of the span, relative to the chapter end
   */
  public TextSpan(Chapter chapter, int startOffset, int endOffset) {
    this(TextPosition.fromLocalOffset(chapter, startOffset),
        TextPosition.fromLocalOffset(chapter, endOffset));
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
   * Indicates whether this TextSpan has an overlap with another TextSpan.
   * 
   * @param other - the other TextSpan
   * @return true if there is an overlap, false otherwise
   */
  public boolean overlapsWith(TextSpan other) {
    // this starts before the other one ends and this ends after the other one starts
    return getStart().compareTo(other.getEnd()) <= 0  && getEnd().compareTo(other.getStart()) >= 0;
  }
  
  public TextSpan getOverlappingSpan(TextSpan other) {
    TextSpan result = null;
    
    if (overlapsWith(other)) {
      TextPosition latestStart, earliestEnd;
      if (start.compareTo(other.getStart()) > 0) {
        latestStart = this.getStart();
      } else {
        latestStart = other.getStart();
      }
      
      if (getEnd().compareTo(other.getEnd()) < 0) {
        earliestEnd = this.getEnd();
      } else {
        earliestEnd = other.getEnd();
      }
      
      result = new TextSpan(latestStart, earliestEnd);
    }
    
    return result;
  }

  /**
   * Converts this TextSpan into an Occurrence.
   * 
   * @param docLength
   *          - the length of the whole document in characters
   * @return an Occurrence equivalent to this TextSpan
   */
  public Occurrence toOccurrence(int docLength) {
    // create empty Occurrence
    Occurrence occ = new Occurrence();

    // set absolute start position
    int startOffset = start.getOffset();
    String startChapterId = start.getChapter().getId();
    double startProgress = startOffset / (double) docLength;
    occ.setStart(new AbsoluteTextPosition(startChapterId, startOffset, startProgress));

    // set absolute end position
    int endOffset = end.getOffset();
    String endChapterId = end.getChapter().getId();
    double endProgress = endOffset / (double) docLength;
    occ.setEnd(new AbsoluteTextPosition(endChapterId, endOffset, endProgress));

    // set length
    occ.setLength(getLength());
    return occ;
  }

  /**
   * Compares first the start positions, and if they are equal, the lengths.
   * Shorter spans are considered smaller than longer ones. This will only
   * produce usable results if both TextPositions are in the same document.
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
   * Indicates if obj is a TextSpan representing the same range as this text
   * span
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof TextSpan)) {
      return false;
    }

    TextSpan other = (TextSpan) obj;
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
