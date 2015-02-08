package de.unistuttgart.vis.vita.model.document;

import org.junit.Test;

/**
 * Performs tests on Chapter model class.
 */
public class ChapterTest {

  @Test(expected = IllegalArgumentException.class)
  public void testSetSentences() {
    new Chapter().setSentences(null);
  }

}