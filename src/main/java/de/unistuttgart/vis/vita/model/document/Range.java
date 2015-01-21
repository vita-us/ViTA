package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

/**
 * Defines the bounds of a text block with a specific start and end. Is not aware of the actual text
 * within the bounds.
 */
@Entity
@Table(name = "Ranges", // RANGE is a reserved word in mysql
    indexes = {@Index(columnList = "start.offset"), @Index(columnList = "end.offset")})
@XmlRootElement
public class Range extends AbstractEntityBase implements Comparable<Range> {

  // constants
  private static final int MIN_LENGTH = 0;

  @Embedded
  @XmlElement(name = "start")
  private TextPosition start;

  @XmlElement(name = "end")
  @Embedded
  private TextPosition end;

  private int length;

  /**
   * Creates a new Range.
   */
  public Range() {
    // zero-parameter constructor needed for JPA
  }

  /**
   * Creates a new instance of Range with the given start and end position in the text.
   *
   * @param pStart - the TextPosition where the new Range should begin
   * @param pEnd - the TextPosiotion where the new Range should end
   */
  public Range(TextPosition pStart, TextPosition pEnd) {
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
   * Creates a Range from two offsets within a common chapter
   * 
   * @param chapter - the chapter this Range lies in
   * @param startOffset the start of the range, relative to the chapter beginning
   * @param endOffset the end of the range, relative to the chapter end
   * @param documentLength - the length of the whole document
   */
  public Range(Chapter chapter, int startOffset, int endOffset, int documentLength) {
    this(TextPosition.fromLocalOffset(chapter, startOffset, documentLength), TextPosition
        .fromLocalOffset(chapter, endOffset, documentLength));
  }

  /**
   * @return the start of the Range
   */
  public TextPosition getStart() {
    return start;
  }

  /**
   * @return the end of the Range
   */
  public TextPosition getEnd() {
    return end;
  }

  /**
   * @return the length of the Range in characters
   */
  public int getLength() {
    return length;
  }

  /**
   * Indicates whether this Range has an overlap with another Range.
   *
   * @param other - the other Range
   * @return true if there is an overlap, false otherwise
   */
  public boolean overlapsWith(Range other) {
    // this starts before the other one ends and this ends after the other one starts
    return getStart().compareTo(other.getEnd()) <= 0 && getEnd().compareTo(other.getStart()) >= 0;
  }

  public Range getOverlappingSpan(Range other) {
    Range result = null;

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

      result = new Range(latestStart, earliestEnd);
    }

    return result;
  }

  /**
   * Compares first the start positions, and if they are equal, the lengths. Shorter ranges are
   * considered smaller than longer ones. This will only produce usable results if both
   * TextPositions are in the same document.
   */
  @Override
  public int compareTo(Range o) {
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
   * Indicates if obj is a Range representing the same range as this range
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Range)) {
      return false;
    }

    Range other = (Range) obj;
    return start.equals(other.start) && end.equals(other.end);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(start).append(end).hashCode();
  }

  @Override
  public String toString() {
    return String.format("Range %s ... %s", start, end);
  }
}
