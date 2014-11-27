package de.unistuttgart.vis.vita.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;

/**
 * Tests the creation of TextSpans and the computation of its lengths.
 */
public class TextSpanTest {

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
    chapter.setRange(new TextSpan(
        TextPosition.fromGlobalOffset(chapter, 0),
        TextPosition.fromGlobalOffset(chapter, DOCUMENT_LENGTH)));

    pos1 = TextPosition.fromGlobalOffset(chapter, OFFSET_1);
    pos2 = TextPosition.fromGlobalOffset(chapter, OFFSET_2);
    pos3 = TextPosition.fromGlobalOffset(chapter, OFFSET_3);
    pos4 = TextPosition.fromGlobalOffset(chapter, OFFSET_4);
  }

  @Test
  public void testStartAndEnd() {
    TextSpan span = new TextSpan(pos1, pos2);
    assertEquals(pos1, span.getStart());
    assertEquals(pos2, span.getEnd());
  }

  @Test
  public void testUtilityConstructor() {
    chapter.setRange(new TextSpan(pos1, pos4));

    TextSpan span = new TextSpan(chapter, OFFSET_2, OFFSET_3);
    assertThat(span.getStart().getOffset(), is(OFFSET_1 + OFFSET_2));
    assertThat(span.getEnd().getOffset(), is(OFFSET_1 + OFFSET_3));
    assertThat(span.getStart().getChapter(), is(chapter));
    assertThat(span.getEnd().getChapter(), is(chapter));
  }

  /**
   * Checks whether an exception is thrown if start position > end position
   */
  @Test(expected = IllegalArgumentException.class)
  public void testIllegalTextSpan() {
    new TextSpan(pos2, pos1);
  }

  /**
   * Checks whether an IllegalArgumentException is thrown if fist parameter is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testFirstNullTextSpan() {
    new TextSpan(null, pos1);
  }

  /**
   * Checks whether an IllegalArgumentException is thrown if the second parameter is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSecondNullTextSpan() {
    new TextSpan(pos1, null);
  }

  /**
   * Checks whether an IllegalArgumentException is thrown if both parameters are null
   */
  @Test(expected = IllegalArgumentException.class)
  public void testBothNullTextSpan() {
    new TextSpan(null, null);
  }

  /**
   * Checks whether TextSpan is empty if start and end position are the same.
   */
  @Test
  public void testEmptyTextSpan() {
    TextSpan emptyTestSpan = new TextSpan(pos2, pos2);
    assertEquals(0, emptyTestSpan.getLength());
  }

  /**
   * Checks whether TextSpan length is calculated correctly.
   */
  @Test
  public void testTextSpan() {
    TextSpan testTextSpan = new TextSpan(pos2, pos3);
    assertEquals(DIFF, testTextSpan.getLength());
  }

  @Test
  public void testCompareTo() {
    TextSpan span1 = new TextSpan(pos1, pos2);
    TextSpan span2 = new TextSpan(pos2, pos3);
    TextSpan span1Duplicate = new TextSpan(pos1, pos2);

    assertEquals(1, span1.compareTo(null));
    assertEquals(-1, span1.compareTo(span2));
    assertEquals(1, span2.compareTo(span1));
    assertEquals(0, span1.compareTo(span1Duplicate));
  }

  @Test
  public void testCompareToWithEqualStartPositions() {
    TextSpan span1 = new TextSpan(pos1, pos2);
    TextSpan span2 = new TextSpan(pos1, pos3);

    assertEquals(-1, span1.compareTo(span2));
    assertEquals(1, span2.compareTo(span1));
  }

  @Test
  public void testEquals() {
    TextSpan span1 = new TextSpan(pos1, pos2);
    TextSpan span2 = new TextSpan(pos2, pos3);
    TextSpan span3 = new TextSpan(pos1, pos3);
    TextSpan span1Duplicate = new TextSpan(pos1, pos2);

    assertTrue(span1.equals(span1Duplicate));
    assertFalse(span1.equals(pos2));
    assertFalse(span2.equals(span1));
    assertFalse(span2.equals(span3));
    assertFalse(span1.equals(null));
    assertFalse(span1.equals("an object of a different class"));
  }

  @Test
  public void testHashCode() {
    TextSpan span1 = new TextSpan(pos1, pos2);
    TextSpan span2 = new TextSpan(pos2, pos3);
    TextSpan span3 = new TextSpan(pos1, pos3);
    TextSpan span1Duplicate = new TextSpan(pos1, pos2);

    assertEquals(span1.hashCode(), span1Duplicate.hashCode());
    assertNotEquals(span1.hashCode(), pos2.hashCode());
    assertNotEquals(span2.hashCode(), span3.hashCode());
  }

  @Test
  public void testWiden() {
    TextSpan span = new TextSpan(chapter, 50, 60);
    TextSpan widened = span.widen(10);
    assertThat(widened.getStart().getOffset(), is(40));
    assertThat(widened.getStart().getChapter(), is(chapter));
    assertThat(widened.getEnd().getOffset(), is(70));
    assertThat(widened.getEnd().getChapter(), is(chapter));
  }

  @Test
  public void testWidenLimits() {
    TextSpan span = new TextSpan(chapter, 50, 60);
    TextSpan widened = span.widen(70);
    assertThat(widened.getStart().getOffset(), is(0));
    assertThat(widened.getEnd().getOffset(), is(130));
  }

  @Test
  public void testNormalizeOverlaps() {
    TextSpan span1 = new TextSpan(chapter, 50, 70);
    TextSpan span2 = new TextSpan(chapter, 60, 80);
    List<TextSpan> normalized = TextSpan.normalizeOverlaps(Arrays.asList(span1, span2));
    assertThat(normalized, contains(new TextSpan(chapter, 50,80)));
  }

  @Test
  public void testNormalizeOverlapsWithTouching() {
    TextSpan span1 = new TextSpan(chapter, 50, 70);
    TextSpan span2 = new TextSpan(chapter, 70, 80);
    List<TextSpan> normalized = TextSpan.normalizeOverlaps(Arrays.asList(span1, span2));
    assertThat(normalized, contains(new TextSpan(chapter, 50,80)));
  }

  @Test
  public void testNormalizeOverlapsWithoutOverlap() {
    TextSpan span1 = new TextSpan(chapter, 50, 70);
    TextSpan span2 = new TextSpan(chapter, 80, 90);
    List<TextSpan> normalized = TextSpan.normalizeOverlaps(Arrays.asList(span1, span2));
    assertThat(normalized, contains(
        new TextSpan(chapter, 50,70),
        new TextSpan(chapter, 80,90)));
  }

  @Test
  public void testNormalizeOverlapsAcrossChapters() {
    TextSpan span1 = new TextSpan(chapter, 50, 70);
    Chapter chapter2 = new Chapter();
    TextSpan span2 = new TextSpan(chapter2, 60, 80);
    List<TextSpan> normalized = TextSpan.normalizeOverlaps(Arrays.asList(span1, span2));
    // should not do any merging because these are two different chapters
    assertThat(normalized, contains(
        new TextSpan(chapter, 50,70),
        new TextSpan(chapter2, 60,80)));
  }
}
