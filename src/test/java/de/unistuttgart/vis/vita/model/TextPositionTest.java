package de.unistuttgart.vis.vita.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.text.TextPosition;

/**
 * Performs some simple test on the TestPosition class.
 * 
 * @author Marc Weise
 * @version 0.2 01.08.2014
 */
public class TextPositionTest {
  
  // attributes
  private Chapter chapter;

  /**
   * Creates a test chapter for the TextPositions.
   */
  @Before
  public void setUp() {
    chapter = new Chapter();
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
  public void testZeroOffset() {
    TextPosition pos = new TextPosition(chapter, 0);
    assertEquals(0.0, pos.getProgress(), 0.001);
  }

}
