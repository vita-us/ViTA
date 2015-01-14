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
        TextPosition.fromGlobalOffset(chapter, 0, DOCUMENT_LENGTH),
        TextPosition.fromGlobalOffset(chapter, DOCUMENT_LENGTH, DOCUMENT_LENGTH)));

    pos1 = TextPosition.fromGlobalOffset(chapter, OFFSET_1, DOCUMENT_LENGTH);
    pos2 = TextPosition.fromGlobalOffset(chapter, OFFSET_2, DOCUMENT_LENGTH);
    pos3 = TextPosition.fromGlobalOffset(chapter, OFFSET_3, DOCUMENT_LENGTH);
    pos4 = TextPosition.fromGlobalOffset(chapter, OFFSET_4, DOCUMENT_LENGTH);
  }

  @Test
  public void testStartAndEnd() {
    Range span = new Range(pos1, pos2);
    assertEquals(pos1, span.getStart());
    assertEquals(pos2, span.getEnd());
  }

  @Test
  public void testUtilityConstructor() {
    chapter.setRange(new Range(pos1, pos4));

    Range span = new Range(chapter, OFFSET_2, OFFSET_3, DOCUMENT_LENGTH);
    assertThat(span.getStart().getOffset(), is(OFFSET_1 + OFFSET_2));
    assertThat(span.getEnd().getOffset(), is(OFFSET_1 + OFFSET_3));
  }

  /**
   * Checks whether an exception is thrown if start position > end position
   */
  @Test(expected = IllegalArgumentException.class)
  public void testIllegalTextSpan() {
    new Range(pos2, pos1);
  }

  /**
   * Checks whether an IllegalArgumentException is thrown if fist parameter is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testFirstNullTextSpan() {
    new Range(null, pos1);
  }

  /**
   * Checks whether an IllegalArgumentException is thrown if the second parameter is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSecondNullTextSpan() {
    new Range(pos1, null);
  }

  /**
   * Checks whether an IllegalArgumentException is thrown if both parameters are null
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBothNullTextSpan() {
    new Range(null, null);
  }

  /**
   * Checks whether TextSpan is empty if start and end position are the same.
   */
  @Test
  public void testEmptyTextSpan() {
    Range emptyTestSpan = new Range(pos2, pos2);
    assertEquals(0, emptyTestSpan.getLength());
  }

  /**
   * Checks whether TextSpan length is calculated correctly.
   */
  @Test
  public void testTextSpan() {
    Range testTextSpan = new Range(pos2, pos3);
    assertEquals(DIFF, testTextSpan.getLength());
  }

  @Test
  public void testCompareTo() {
    Range span1 = new Range(pos1, pos2);
    Range span2 = new Range(pos2, pos3);
    Range span1Duplicate = new Range(pos1, pos2);

    assertEquals(1, span1.compareTo(null));
    assertEquals(-1, span1.compareTo(span2));
    assertEquals(1, span2.compareTo(span1));
    assertEquals(0, span1.compareTo(span1Duplicate));
  }

  @Test
  public void testCompareToWithEqualStartPositions() {
    Range span1 = new Range(pos1, pos2);
    Range span2 = new Range(pos1, pos3);

    assertEquals(-1, span1.compareTo(span2));
    assertEquals(1, span2.compareTo(span1));
  }

  @Test
  public void testEquals() {
    Range span1 = new Range(pos1, pos2);
    Range span2 = new Range(pos2, pos3);
    Range span3 = new Range(pos1, pos3);
    Range span1Duplicate = new Range(pos1, pos2);

    assertTrue(span1.equals(span1Duplicate));
    assertFalse(span1.equals(pos2));
    assertFalse(span2.equals(span1));
    assertFalse(span2.equals(span3));
    assertFalse(span1.equals(null));
    assertFalse(span1.equals("an object of a different class"));
  }

  @Test
  public void testHashCode() {
    Range span1 = new Range(pos1, pos2);
    Range span2 = new Range(pos2, pos3);
    Range span3 = new Range(pos1, pos3);
    Range span1Duplicate = new Range(pos1, pos2);

    assertEquals(span1.hashCode(), span1Duplicate.hashCode());
    assertNotEquals(span1.hashCode(), pos2.hashCode());
    assertNotEquals(span2.hashCode(), span3.hashCode());
  }
}
