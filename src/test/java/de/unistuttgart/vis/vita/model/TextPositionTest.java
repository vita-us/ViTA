package de.unistuttgart.vis.vita.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.document.TextPosition;

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
  public void testGetters() {
    TextPosition pos = new TextPosition(chapter, 7);
    assertEquals(chapter, pos.getChapter());
    assertEquals(7, pos.getOffset());
    // offset ist tested in depth below
  }

  /**
   * Checks whether a negative offset causes an IllegalArgumentException to be thrown.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNegativeOffset() {
    new TextPosition(chapter, -42);
  }
  
  @Test
  public void testCompareTo() {
    TextPosition pos1 = new TextPosition(chapter, OFFSET_TEST);
    TextPosition pos1Duplicate = new TextPosition(chapter, OFFSET_TEST);
    TextPosition pos2 = new TextPosition(chapter, OFFSET_TEST + 1);
    
    assertEquals(1, pos1.compareTo(null));
    assertEquals(-1, pos1.compareTo(pos2));
    assertEquals(1, pos2.compareTo(pos1));
    assertEquals(0, pos1.compareTo(pos1));
    assertEquals(0, pos1.compareTo(pos1Duplicate));
  }

  @Test
  public void testEquals() {
    TextPosition pos1 = new TextPosition(chapter, OFFSET_TEST);
    TextPosition pos1Duplicate = new TextPosition(chapter, OFFSET_TEST);
    TextPosition pos2 = new TextPosition(chapter, OFFSET_TEST + 1);
    
    assertTrue(pos1.equals(pos1Duplicate));
    assertFalse(pos1.equals(pos2));
    assertFalse(pos2.equals(pos1));
    assertFalse(pos1.equals(null));
    assertFalse(pos1.equals("an object of a different class"));
  }
  
  @Test
  public void testHashCode() {
    TextPosition pos1 = new TextPosition(chapter, OFFSET_TEST);
    TextPosition pos1Duplicate = new TextPosition(chapter, OFFSET_TEST);
    TextPosition pos2 = new TextPosition(chapter, OFFSET_TEST + 1);
    
    assertEquals(pos1.hashCode(), pos1Duplicate.hashCode());
    assertNotEquals(pos1.hashCode(), pos2.hashCode());
  }

}
