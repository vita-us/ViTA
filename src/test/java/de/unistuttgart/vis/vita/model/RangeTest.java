package de.unistuttgart.vis.vita.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;

import java.util.Arrays;
import java.util.List;

/**
 * Tests the creation of Range and the computation of its lengths.
 */
public class RangeTest {

  // test data
  private static final int DOCUMENT_LENGTH = 1000000;
  private static final int OFFSET_1 = 10000;
  private static final int OFFSET_2 = 70000;
  private static final int OFFSET_3 = 95000;
  private static final int OFFSET_4 = 100000;
  private static final int DIFF = 25000;

  // attributes
  private Chapter chapter;

  private TextPosition pos1;
  private TextPosition pos2;
  private TextPosition pos3;
  private TextPosition pos4;

  /**
   * Create positions and chapter.
   */
  @Before
  public void setUp() {
    Document doc = new Document();
    DocumentMetrics metrics = doc.getMetrics();
    metrics.setCharacterCount(DOCUMENT_LENGTH);
    chapter = new Chapter();
    chapter.setRange(new Range(
        TextPosition.fromGlobalOffset(0, DOCUMENT_LENGTH),
        TextPosition.fromGlobalOffset(DOCUMENT_LENGTH, DOCUMENT_LENGTH)));

    pos1 = TextPosition.fromGlobalOffset(OFFSET_1, DOCUMENT_LENGTH);
    pos2 = TextPosition.fromGlobalOffset(OFFSET_2, DOCUMENT_LENGTH);
    pos3 = TextPosition.fromGlobalOffset(OFFSET_3, DOCUMENT_LENGTH);
    pos4 = TextPosition.fromGlobalOffset(OFFSET_4, DOCUMENT_LENGTH);
  }

  @Test
  public void testStartAndEnd() {
    Range range = new Range(pos1, pos2);
    assertEquals(pos1, range.getStart());
    assertEquals(pos2, range.getEnd());
  }

  @Test
  public void testUtilityConstructor() {
    chapter.setRange(new Range(pos1, pos4));

    Range range = new Range(chapter, OFFSET_2, OFFSET_3, DOCUMENT_LENGTH);
    assertThat(range.getStart().getOffset(), is(OFFSET_1 + OFFSET_2));
    assertThat(range.getEnd().getOffset(), is(OFFSET_1 + OFFSET_3));
  }

  /**
   * Checks whether an exception is thrown if start position > end position
   */
  @Test(expected = IllegalArgumentException.class)
  public void testIllegalRanges() {
    new Range(pos2, pos1);
  }

  /**
   * Checks whether an IllegalArgumentException is thrown if fist parameter is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testFirstNullRange() {
    new Range(null, pos1);
  }

  /**
   * Checks whether an IllegalArgumentException is thrown if the second parameter is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSecondNullRange() {
    new Range(pos1, null);
  }

  /**
   * Checks whether an IllegalArgumentException is thrown if both parameters are null
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBothNullRange() {
    new Range(null, null);
  }

  /**
   * Checks whether Range is empty if start and end position are the same.
   */
  @Test
  public void testEmptyRange() {
    Range emptyRange = new Range(pos2, pos2);
    assertEquals(0, emptyRange.getLength());
  }

  /**
   * Checks whether Range length is calculated correctly.
   */
  @Test
  public void testRange() {
    Range testRange = new Range(pos2, pos3);
    assertEquals(DIFF, testRange.getLength());
  }

  @Test
  public void testCompareTo() {
    Range range1 = new Range(pos1, pos2);
    Range range2 = new Range(pos2, pos3);
    Range range1Duplicate = new Range(pos1, pos2);

    assertEquals(1, range1.compareTo(null));
    assertEquals(-1, range1.compareTo(range2));
    assertEquals(1, range2.compareTo(range1));
    assertEquals(0, range1.compareTo(range1Duplicate));
  }

  @Test
  public void testCompareToWithEqualStartPositions() {
    Range range1 = new Range(pos1, pos2);
    Range range2 = new Range(pos1, pos3);

    assertEquals(-1, range1.compareTo(range2));
    assertEquals(1, range2.compareTo(range1));
  }

  @Test
  public void testEquals() {
    Range range1 = new Range(pos1, pos2);
    Range range2 = new Range(pos2, pos3);
    Range range3 = new Range(pos1, pos3);
    Range range1Duplicate = new Range(pos1, pos2);

    assertTrue(range1.equals(range1Duplicate));
    assertFalse(range1.equals(pos2));
    assertFalse(range2.equals(range1));
    assertFalse(range2.equals(range3));
    assertFalse(range1.equals(null));
    assertFalse(range1.equals("an object of a different class"));
  }

  @Test
  public void testHashCode() {
    Range range1 = new Range(pos1, pos2);
    Range range2 = new Range(pos2, pos3);
    Range range3 = new Range(pos1, pos3);
    Range range1Duplicate = new Range(pos1, pos2);

    assertEquals(range1.hashCode(), range1Duplicate.hashCode());
    assertNotEquals(range1.hashCode(), pos2.hashCode());
    assertNotEquals(range2.hashCode(), range3.hashCode());
  }

  @Test
  public void testMergeOverlappingRanges() {
    Range single = makeRange(20, 100);
    Range overlapBegin = makeRange(200, 250);
    Range overlapEnd = makeRange(230, 280);
    List<Range> result = Range.mergeOverlappingRanges(Arrays.asList(single, overlapBegin, overlapEnd));
    assertThat(result, contains(single, makeRange(200, 280)));
  }

  @Test
  public void testMergeOverlappingRangesSwallowing() {
    Range overlapSwallower = makeRange(200, 280);
    Range overlapSwallowed = makeRange(230, 250);
    Range single = makeRange(1020, 1100);
    List<Range> result = Range.mergeOverlappingRanges(Arrays.asList(
        single, overlapSwallower, overlapSwallowed));
    assertThat(result, contains(makeRange(200, 280), single));
  }

  @Test
  public void testMergeOverlappingRangesEdgeCase1() {
    Range first = makeRange(200, 300);
    Range second = makeRange(301, 400);
    List<Range> result = Range.mergeOverlappingRanges(Arrays.asList(first, second));
    assertThat(result, contains(first, second));
  }

  @Test
  public void testMergeOverlappingRangesEdgeCase2() {
    Range first = makeRange(200, 300);
    Range second = makeRange(300, 400);
    List<Range> result = Range.mergeOverlappingRanges(Arrays.asList(first, second));
    assertThat(result, contains(makeRange(200, 400)));
  }

  private Range makeRange(int start, int end) {
    return new Range(TextPosition.fromGlobalOffset(start, DOCUMENT_LENGTH),
        TextPosition.fromGlobalOffset(end, DOCUMENT_LENGTH));
  }
}
