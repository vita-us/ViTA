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
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;

/**
 * Defines the bounds of a text block with a specific start and end. Is not
 * aware of the actual text within the bounds.
 */
@Entity
@Table(indexes={
  @Index(columnList="start.offset"),
  @Index(columnList="end.offset")
})
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

  // gets the occurrences of all entities
  @NamedQuery(name = "TextSpan.findTextSpansForEntities",
    query = "SELECT ts "
          + "FROM TextSpan ts, Entity e "
          + "WHERE e.id IN :entityIds "
          + "AND ts MEMBER OF e.occurrences "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd "
          // Null checks
          + "AND ts.start.chapter IS NOT NULL "
          // right ordering
          + "ORDER BY ts.start.offset"),

  // checks whether a set of entities occur in a range (for relation occurrences)
  @NamedQuery(name = "TextSpan.getNumberOfOccurringEntities",
    query = "SELECT COUNT(DISTINCT e.id) "
          + "FROM Entity e "
          + "INNER JOIN e.occurrences ts "
          + "WHERE e.id IN :entityIds "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          // Null checks
          + "AND ts.start.chapter IS NOT NULL " + "AND ts.start.chapter IS NOT NULL"),

  // gets the entities that occur within a given range
  @NamedQuery(name = "TextSpan.getOccurringEntities",
    query = "SELECT DISTINCT e "
          + "FROM Entity e "
          + "INNER JOIN e.occurrences ts "
          + "WHERE e IN :entities "
          // range checks
          + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
          + "GROUP BY e "
          + "HAVING COUNT(ts) > 0.25 * ("
            + "SELECT COUNT(ts2) "
            + "FROM Person p "
            + "INNER JOIN p.occurrences ts2 "
            + "WHERE ts2.start.offset BETWEEN :rangeStart AND :rangeEnd"
          + ") / ("
            + "SELECT COUNT(DISTINCT p) "
            + "FROM Person p "
            + "INNER JOIN p.occurrences ts2 "
            + "WHERE ts2.start.offset BETWEEN :rangeStart AND :rangeEnd"
          + ")"),

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

  /**
   * Extends the start and the end of this text span by {@code radius},
   * but not across chapter boundaries
   * @param radius the length in characters to be added left and right
   * @return the widened span
   */
  public TextSpan widen(int radius) {
    int chapterStart = getStart().getChapter().getRange().getStart().getOffset();
    int start = getStart().getOffset() - radius;

    if (start < chapterStart) {
      start = chapterStart;
    }

    int chapterEnd = getEnd().getChapter().getRange().getEnd().getOffset();
    int end = getEnd().getOffset() + radius;

    if (end > chapterEnd) {
      end = chapterEnd;
    }

    return new TextSpan(TextPosition.fromGlobalOffset(getStart().getChapter(), start),
        TextPosition.fromGlobalOffset(getEnd().getChapter(), end));
  }

  /**
   * Merges overlapping or touching text spans, but not across chapter boundaries
   * @param spans the input spans
   * @return the normalized spans
   */
  public static List<TextSpan> normalizeOverlaps(List<TextSpan> spans) {
    spans = new ArrayList<>(spans); // clone
    Collections.sort(spans);
    TextPosition currentSpanStart = null;
    TextPosition currentSpanEnd = null;
    List<TextSpan> newSpans = new ArrayList<>();
    for (TextSpan span : spans) {
      if (currentSpanStart == null) {
        // this is the first span (after a break or the start)
        currentSpanStart = span.getStart();
        currentSpanEnd = span.getEnd();
      } else if (currentSpanEnd.getChapter().equals(span.getStart().getChapter())
          && currentSpanEnd.compareTo(span.getStart()) >= 0) {
        // the last one overlaps this one, so just adjust the end
        currentSpanEnd = TextPosition.max(currentSpanEnd, span.getEnd());
      } else {
        // new span, save old first
        newSpans.add(new TextSpan(currentSpanStart, currentSpanEnd));
        currentSpanStart = span.getStart();
        currentSpanEnd = span.getEnd();
      }
    }

    if (currentSpanStart != null) {
      // There is a step at the end
      newSpans.add(new TextSpan(currentSpanStart, currentSpanEnd));
    }

    return newSpans;
  }

  /**
   * Intersects multiple text span lists
   *
   * @param list of span lists. The spans in each list must not be overlapping
   * @return the text spans that are included in all given lists of text spans
   */
  public static List<TextSpan> intersect(List<List<TextSpan>> lists) {
    return TextSpanIntersector.intersect(lists);
  }
}
