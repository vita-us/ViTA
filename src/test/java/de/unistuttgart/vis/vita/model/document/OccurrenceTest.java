package de.unistuttgart.vis.vita.model.document;

import org.junit.Test;

/**
 * Checks whether exceptions are thrown if Occurrences are created with null values.
 */
public class OccurrenceTest {

  @Test(expected = IllegalArgumentException.class)
  public void testCreateOccurrenceNullSentence() {
    new Occurrence(null, new Range());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateOccurrenceWithNullRange() {
    new Occurrence(new Sentence(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetSentenceToNull() {
    new Occurrence().setSentence(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetRangeNull() {
    new Occurrence().setRange(null);
  }

}