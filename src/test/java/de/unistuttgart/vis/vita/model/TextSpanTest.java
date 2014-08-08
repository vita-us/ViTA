package de.unistuttgart.vis.vita.model;

import static org.junit.Assert.assertEquals;

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
  private static final int DIFF = 25000;

  // attributes
  private Chapter chapter;

  private TextPosition pos1;
  private TextPosition pos2;
  private TextPosition pos3;

  /**
   * Create positions and chapter.
   */
  @Before
  public void setUp() {
    Document doc = new Document();
    DocumentMetrics metrics = doc.getMetrics();
    metrics.setCharacterCount(DOCUMENT_LENGTH);
    chapter = new Chapter(doc);

    pos1 = new TextPosition(chapter, OFFSET_1);
    pos2 = new TextPosition(chapter, OFFSET_2);
    pos3 = new TextPosition(chapter, OFFSET_3);
  }

  @Test
  public void testStartAndEnd() {
    TextSpan span = new TextSpan(pos1, pos2);
    assertEquals(pos1, span.getStart());
    assertEquals(pos2, span.getEnd());
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

}
