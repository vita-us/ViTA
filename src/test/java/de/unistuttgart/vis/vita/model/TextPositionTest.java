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
 * Performs some simple tests on the TestPosition class.
 */
public class TextPositionTest {
  private static final int DOCUMENT_LENGTH = 1000000;
  private static final int OFFSET_TEST = 20000;

  // attributes
  private Chapter chapter;

  /**
   * Creates a test chapter for the TextPositions.
   */
  @Before
  public void setUp() {
    Document doc = new Document();
    DocumentMetrics metrics = doc.getMetrics();
    metrics.setCharacterCount(DOCUMENT_LENGTH);
    chapter = new Chapter();
  }

  @Test
  public void testFromGobalOffset() {
    TextPosition pos = TextPosition.fromGlobalOffset(7, DOCUMENT_LENGTH);
    assertEquals(7, pos.getOffset());
    // offset ist tested in depth below
  }

  @Test
  public void testFromLocalOffset() {
    chapter.setRange(new Range(
        TextPosition.fromGlobalOffset(20, DOCUMENT_LENGTH),
        TextPosition.fromGlobalOffset(40, DOCUMENT_LENGTH)));
    TextPosition pos = TextPosition.fromLocalOffset(chapter, 10, DOCUMENT_LENGTH);
    assertThat(pos.getOffset(), is(30));
  }

  /**
   * Checks whether a negative offset causes an IllegalArgumentException to be thrown.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNegativeOffset() {
    TextPosition.fromGlobalOffset(-42, DOCUMENT_LENGTH);
  }

  @Test
  public void testCompareTo() {
    TextPosition pos1 = TextPosition.fromGlobalOffset(OFFSET_TEST, DOCUMENT_LENGTH);
    TextPosition pos1Duplicate = TextPosition.fromGlobalOffset(OFFSET_TEST, DOCUMENT_LENGTH);
    TextPosition pos2 = TextPosition.fromGlobalOffset(OFFSET_TEST + 1, DOCUMENT_LENGTH);

    assertEquals(1, pos1.compareTo(null));
    assertEquals(-1, pos1.compareTo(pos2));
    assertEquals(1, pos2.compareTo(pos1));
    assertEquals(0, pos1.compareTo(pos1));
    assertEquals(0, pos1.compareTo(pos1Duplicate));
  }

  @Test
  public void testEquals() {
    TextPosition pos1 = TextPosition.fromGlobalOffset(OFFSET_TEST, DOCUMENT_LENGTH);
    TextPosition pos1Duplicate = TextPosition.fromGlobalOffset(OFFSET_TEST, DOCUMENT_LENGTH);
    TextPosition pos2 = TextPosition.fromGlobalOffset(OFFSET_TEST + 1, DOCUMENT_LENGTH);

    assertTrue(pos1.equals(pos1Duplicate));
    assertFalse(pos1.equals(pos2));
    assertFalse(pos2.equals(pos1));
    assertFalse(pos1.equals(null));
    assertFalse(pos1.equals("an object of a different class"));
  }

  @Test
  public void testHashCode() {
    TextPosition pos1 = TextPosition.fromGlobalOffset(OFFSET_TEST, DOCUMENT_LENGTH);
    TextPosition pos1Duplicate = TextPosition.fromGlobalOffset(OFFSET_TEST, DOCUMENT_LENGTH);
    TextPosition pos2 = TextPosition.fromGlobalOffset(OFFSET_TEST + 1, DOCUMENT_LENGTH);

    assertEquals(pos1.hashCode(), pos1Duplicate.hashCode());
    assertNotEquals(pos1.hashCode(), pos2.hashCode());
  }

  @Test
  public void testMin() {
    TextPosition pos1 = TextPosition.fromGlobalOffset(OFFSET_TEST, DOCUMENT_LENGTH);
    TextPosition pos2 = TextPosition.fromGlobalOffset(OFFSET_TEST + 100, DOCUMENT_LENGTH);
    assertThat(TextPosition.min(pos1, pos2), is(pos1));
    assertThat(TextPosition.min(pos2, pos1), is(pos1));
  }

  @Test
  public void testMax() {
    TextPosition pos1 = TextPosition.fromGlobalOffset(OFFSET_TEST, DOCUMENT_LENGTH);
    TextPosition pos2 = TextPosition.fromGlobalOffset(OFFSET_TEST + 100, DOCUMENT_LENGTH);
    assertThat(TextPosition.max(pos1, pos2), is(pos2));
    assertThat(TextPosition.max(pos2, pos1), is(pos2));
  }

}
