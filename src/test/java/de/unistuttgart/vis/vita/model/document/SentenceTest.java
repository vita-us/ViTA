package de.unistuttgart.vis.vita.model.document;

import de.unistuttgart.vis.vita.data.ChapterTestData;

import org.junit.Test;

public class SentenceTest {

  private Chapter testChapter;

  protected void setUp() throws Exception {
    this.testChapter = new ChapterTestData().createTestChapter();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSentenceRangeNull() {
    new Sentence(null, testChapter, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSentenceChapterNull() {
    new Sentence(new Range(), null, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateSentenceWithIllegalIndex() {
    new Sentence(new Range(), testChapter, -1);
  }

  @Test(expected =  IllegalArgumentException.class)
  public void testSetRangeNull() {
    new Sentence().setRange(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetChapterNull() {
    new Sentence().setChapter(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetIllegalIndex() {
    new Sentence().setIndex(-1);
  }

}