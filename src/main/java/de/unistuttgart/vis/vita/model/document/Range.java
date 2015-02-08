package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines the bounds of a text block with a specific start and end. Is not aware of the actual text
 * within the bounds.
 */
@Embeddable
@XmlRootElement
public class Range implements Comparable<Range> {

  // constants
  private static final int MIN_LENGTH = 0;

  @Embedded
  @XmlElement(name = "start")
  private TextPosition start;

  @XmlElement(name = "end")
  @Embedded
  private TextPosition end;

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
   * Merges ranges in a list so that there are no more overlaps
   * @param ranges a list of ranges
   * @return list of sorted, non-overlapping ranges
   */
  public static List<Range> mergeOverlappingRanges(List<Range> ranges) {
    Collections.sort(ranges);
    TextPosition lastEnd = null;
    TextPosition lastStart = null;
    List<Range> result = new ArrayList<>();
    for (Range range : ranges) {
      if (lastEnd != null && range.getStart().compareTo(lastEnd) <= 0) {
        lastEnd = TextPosition.max(lastEnd, range.getEnd());
      } else {
        if (lastEnd != null) {
          result.add(new Range(lastStart, lastEnd));
        }
        lastStart = range.getStart();
        lastEnd = range.getEnd();
      }
    }
    if (lastEnd != null) {
      result.add(new Range(lastStart, lastEnd));
    }
    return result;
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
    return end.getOffset() - start.getOffset();
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

  /**
   * Returns the intersection of this Range and a given one.
   *
   * @param otherRange - the other Range with which the intersection should be computed
   */
  public Range getOverlappingRange(Range otherRange) {
    Range result = null;

    if (overlapsWith(otherRange)) {
      TextPosition latestStart, earliestEnd;
      if (start.compareTo(otherRange.getStart()) > 0) {
        latestStart = this.getStart();
      } else {
        latestStart = otherRange.getStart();
      }

      if (getEnd().compareTo(otherRange.getEnd()) < 0) {
        earliestEnd = this.getEnd();
      } else {
        earliestEnd = otherRange.getEnd();
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
