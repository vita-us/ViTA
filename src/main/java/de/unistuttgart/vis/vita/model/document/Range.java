package de.unistuttgart.vis.vita.model.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;
import de.unistuttgart.vis.vita.services.responses.occurrence.AbsoluteTextPosition;
import de.unistuttgart.vis.vita.services.responses.occurrence.FlatOccurrence;

/**
 * Defines the bounds of a text block with a specific start and end. Is not aware of the actual text
 * within the bounds.
 */
@Entity
@Table(indexes = {@Index(columnList = "start.offset"), @Index(columnList = "end.offset")})

public class Range extends AbstractEntityBase implements Comparable<Range> {

  // constants
  private static final int MIN_LENGTH = 0;

  @Embedded
  private TextPosition start;

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
   * Converts this Range into an Occurrence.
   *
   * @param docLength - the length of the whole document in characters
   * @return an Occurrence equivalent to this Range
   */
  public FlatOccurrence toOccurrence(int docLength) {
    // create empty Occurrence
    FlatOccurrence occ = new FlatOccurrence();

    // set absolute start position
    int startOffset = start.getOffset();
    double startProgress = startOffset / (double) docLength;
    occ.setStart(new AbsoluteTextPosition(startOffset, startProgress));

    // set absolute end position
    int endOffset = end.getOffset();
    double endProgress = endOffset / (double) docLength;
    occ.setEnd(new AbsoluteTextPosition(endOffset, endProgress));

    // set length
    occ.setLength(getLength());
    return occ;
  }

  /**
   * Compares first the start positions, and if they are equal, the lengths. Shorter spans are
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
   * Indicates if obj is a Range representing the same range as this text span
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
    return String.format("Span %s ... %s", start, end);
  }

  /**
   * Extends the start and the end of this text span by {@code radius}, but not across chapter
   * boundaries
   * 
   * @param radius - the length in characters to be added left and right.
   * @param allChapters - all chapters of the document or at least the chapters containing this
   *        range; must be ordered by position in the document.
   * @param documentLength - the length of the document of this range.
   * @return the widened span
   */
  public Range widen(int radius, List<Chapter> allChapters, int documentLength) {
    int chapterOfRangeStart = Range.findChapterOfRange(0, allChapters, this, true);
    int chapterStart = allChapters.get(chapterOfRangeStart).getRange().getStart().getOffset();
    int start = getStart().getOffset() - radius;

    if (start < chapterStart) {
      start = chapterStart;
    }

    int chapterOfRangeEnd = Range.findChapterOfRange(0, allChapters, this, false);
    int chapterEnd = allChapters.get(chapterOfRangeEnd).getRange().getEnd().getOffset();
    int end = getEnd().getOffset() + radius;

    if (end > chapterEnd) {
      end = chapterEnd;
    }

    return new Range(TextPosition.fromGlobalOffset(allChapters.get(chapterOfRangeStart), start,
        documentLength), TextPosition.fromGlobalOffset(allChapters.get(chapterOfRangeEnd), end,
        documentLength));
  }

  /**
   * Merges overlapping or touching text spans, but not across chapter boundaries
   * 
   * @param ranges the input spans
   * @param allChapters all chapters of the document or at least the chapters containing all of the
   *        given ranges; must be ordered by position in the document.
   * @return the normalized spans
   */
  public static List<Range> normalizeOverlaps(List<Range> ranges, List<Chapter> allChapters) {
    // to assure chapter boundaries
    int currentSpanChapterIndex = 0;
    int spanChapterIndex = 0;
    ranges = new ArrayList<>(ranges); // clone
    Collections.sort(ranges);
    TextPosition currentSpanStart = null;
    TextPosition currentSpanEnd = null;
    List<Range> newSpans = new ArrayList<>();
    for (Range span : ranges) {
      spanChapterIndex = findChapterOfRange(spanChapterIndex, allChapters, span, true);
      if (currentSpanStart == null) {
        // this is the first span (after a break or the start)
        currentSpanStart = span.getStart();
        currentSpanEnd = span.getEnd();
        currentSpanChapterIndex = findChapterOfRange(0, allChapters, span, false);
      } else if (allChapters.get(currentSpanChapterIndex).equals(allChapters.get(spanChapterIndex))
          && currentSpanEnd.compareTo(span.getStart()) >= 0) {
        // the last one overlaps this one, so just adjust the end
        currentSpanEnd = TextPosition.max(currentSpanEnd, span.getEnd());
      } else {
        // new span, save old first
        newSpans.add(new Range(currentSpanStart, currentSpanEnd));
        currentSpanStart = span.getStart();
        currentSpanEnd = span.getEnd();
        currentSpanChapterIndex =
            findChapterOfRange(currentSpanChapterIndex, allChapters, span, false);
      }
    }

    if (currentSpanStart != null) {
      // There is a step at the end
      newSpans.add(new Range(currentSpanStart, currentSpanEnd));
    }

    return newSpans;
  }

  /**
   * Intersects multiple text span lists
   *
   * @param list of span lists. The spans in each list must not be overlapping
   * @return the text spans that are included in all given lists of text spans
   */
  public static List<Range> intersect(List<List<Range>> lists) {
    return TextSpanIntersector.intersect(lists);
  }

  /**
   * Find the Chapter which contains a range. The given range MUST be inside of one of the chapters.
   * This means both have to be part of the same document and chapters should be all chapters of the
   * document.
   * 
   * @param startIndex - the position in the Chapter List where the search should start.
   * @param chapters - the chapters to search in.
   * @param range - the range to search.
   * @param useStartPosition - the start position (true) or the end position (false) of the range
   *        decides in which chapter the range is located.
   * @return the index position of the chapter in the list. list size if not found.
   */
  public static int findChapterOfRange(int startIndex, List<Chapter> chapters, Range range,
      boolean useStartPosition) {
    if (startIndex < 0) {
      throw new IllegalArgumentException("Index must not be negative");
    }
    if (startIndex >= chapters.size()) {
      throw new IllegalArgumentException("Index is bigger than chapters size");
    }

    int index = startIndex;
    while (index < chapters.size()) {
      if (chapters.get(index).containsRange(range, useStartPosition)) {
        break;
      }
      index++;
    }
    if (index >= chapters.size()) {
      throw new IllegalArgumentException("Chapters MUST contain the given range.");
    }
    return index;
  }
}
