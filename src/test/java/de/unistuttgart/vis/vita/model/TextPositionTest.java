package de.unistuttgart.vis.vita.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.document.TextPosition;

/**
 * Performs some simple test on the TestPosition class.
 * 
 * @author Marc Weise
 * @version 0.2 01.08.2014
 */
public class TextPositionTest {

  // constants
  private static final double DELTA = 0.001;

  // test data
  private static final int DOCUMENT_LENGTH = 1000000;

  private static final int OFFSET_START = 0;
  private static final int OFFSET_TEST = 20000;
  private static final int OFFSET_MIDDLE = 500000;

  private static final double PROGRESS_START = 0.0;
  private static final double PROGRESS_MIDDLE = 0.5;
  private static final double PROGRESS_END = 1.0;

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
    chapter = new Chapter(doc);
  }

  /**
   * Checks whether a negative offset causes an IllegalArgumentException to be thrown.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNegativeOffset() {
    new TextPosition(chapter, -42);
  }

  /**
   * Checks whether a TextPosition with zero offset can be created and has a progress of zero.
   */
  @Test
  public void testZeroOffsetProgress() {
    TextPosition pos = new TextPosition(chapter, OFFSET_START);
    assertEquals(PROGRESS_START, pos.getProgress(), DELTA);
  }

  /**
   * Checks whether a TextPosition at the middle of the Document can be created and has a progress
   * of a half.
   */
  @Test
  public void testMiddleOffsetProgress() {
    TextPosition pos = new TextPosition(chapter, OFFSET_MIDDLE);
    assertEquals(PROGRESS_MIDDLE, pos.getProgress(), DELTA);
  }

  /**
   * Checks whether a TextPosition at the end of the Document can be created and has a progress of
   * one.
   */
  @Test
  public void testMaximumOffsetProgress() {
    TextPosition pos = new TextPosition(chapter, DOCUMENT_LENGTH);
    assertEquals(PROGRESS_END, pos.getProgress(), DELTA);
  }

  /**
   * Checks whether a TextPosition with Chapter null can be created and has a progress of NaN.
   */
  @Test
  public void testNullChapterProgress() {
    TextPosition pos = new TextPosition(null, OFFSET_TEST);
    assertTrue(Double.isNaN(pos.getProgress()));
  }

  /**
   * Checks whether a TextPosition can be created with a Chapter of a empty Document (length == 0)
   * and has a progress of NaN.
   */
  @Test
  public void testZeroLengthDocumentProgress() {
    TextPosition pos = new TextPosition(new Chapter(new Document()), OFFSET_TEST);
    assertTrue(Double.isNaN(pos.getProgress()));
  }

}
