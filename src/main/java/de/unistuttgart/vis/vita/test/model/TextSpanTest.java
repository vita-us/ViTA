package de.unistuttgart.vis.vita.test.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.text.TextPosition;
import de.unistuttgart.vis.vita.model.text.TextSpan;

/**
 * Tests the creation of TextSpans and the computation of its lengths.
 * 
 * @author Marc Weise
 * @version 0.1 31.07.2014
 */
public class TextSpanTest {

  private static final int CHAPTER_3_2_TO_4_DIFF = 40000;
  private static final int CHAPTER_3_2_TO_5_DIFF = 50000;
  private static final int CHAPTER_1_LENGTH = 50000;
  private static final int CHAPTER_1_OFFSET_1 = 3000;

  private static final int CHAPTER_2_LENGTH = 80000;
  private static final int CHAPTER_2_OFFSET_1 = 43000;

  private static final int CHAPTER_3_LENGTH = 10000;
  private static final int CHAPTER_3_OFFSET_1 = 1000;
  private static final int CHAPTER_3_OFFSET_2 = 5000;
  private static final int CHAPTER_3_OFFSET_DIFF = 4000;

  private static final int CHAPTER_4_LENGTH = 100000;
  private static final int CHAPTER_4_OFFSET_1 = 35000;

  private static final int CHAPTER_5_LENGTH = 70000;
  private static final int CHAPTER_5_OFFSET_1 = 10000;

  private Chapter chapter1;
  private TextPosition chapter1Pos1;

  private Chapter chapter2;
  private TextPosition chapter2Pos1;

  private Chapter chapter3;
  private TextPosition chapter3Pos1;
  private TextPosition chapter3Pos2;
  private TextPosition chapter3PosStart;
  private TextPosition chapter3PosEnd;

  private Chapter chapter4;
  private TextPosition chapter4Pos1;

  private TextPosition chapter4PosEnd;
  private Chapter chapter5;
  private TextPosition chapter5Pos1;

  @Before
  public void setUp() {

    // chapter 1 and position
    chapter1 = new Chapter();
    chapter1.setNumber(1);
    chapter1.setLength(CHAPTER_1_LENGTH);
    chapter1Pos1 = new TextPosition(chapter1, CHAPTER_1_OFFSET_1);

    // chapter 2 and position
    chapter2 = new Chapter();
    chapter2.setNumber(2);
    chapter2.setLength(CHAPTER_2_LENGTH);
    chapter2Pos1 = new TextPosition(chapter2, CHAPTER_2_OFFSET_1);

    // chapter 3 and positions
    chapter3 = new Chapter();
    chapter3.setNumber(3);
    chapter3.setLength(CHAPTER_3_LENGTH);
    chapter3Pos1 = new TextPosition(chapter3, CHAPTER_3_OFFSET_1);
    chapter3Pos2 = new TextPosition(chapter3, CHAPTER_3_OFFSET_2);
    chapter3PosStart = new TextPosition(chapter3, 1);
    chapter3PosEnd = new TextPosition(chapter3, CHAPTER_3_LENGTH);

    // chapter 4 and position
    chapter4 = new Chapter();
    chapter4.setNumber(4);
    chapter4.setLength(CHAPTER_4_LENGTH);
    chapter4Pos1 = new TextPosition(chapter4, CHAPTER_4_OFFSET_1);
    chapter4PosEnd = new TextPosition(chapter4, CHAPTER_4_LENGTH);

    // chapter 5 and position
    chapter5 = new Chapter();
    chapter5.setNumber(5);
    chapter5.setLength(CHAPTER_5_LENGTH);
    chapter5Pos1 = new TextPosition(chapter5, CHAPTER_5_OFFSET_1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEndMultipleChaptersBeforeStart() {
    new TextSpan(chapter3Pos1, chapter1Pos1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEndOneChapterBeforeStart() {
    new TextSpan(chapter3Pos1, chapter2Pos1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEndSameChapterBeforeStart() {
    new TextSpan(chapter3Pos2, chapter3Pos1);
  }

  @Test
  public void testStartEqualsEnd() {
    TextSpan emptyTestSpan = new TextSpan(chapter3Pos1, chapter3Pos1);
    assertEquals(0, emptyTestSpan.getLength());
  }

  @Test
  public void testEndSameChapterAfterStart() {
    TextSpan testSpan = new TextSpan(chapter3Pos1, chapter3Pos2);
    assertEquals(CHAPTER_3_OFFSET_DIFF, testSpan.getLength());
  }

  @Test
  public void testWholeChapter() {
    TextSpan wholeChapterSpan = new TextSpan(chapter3PosStart, chapter3PosEnd);
    assertEquals(CHAPTER_3_LENGTH, wholeChapterSpan.getLength());
  }

  @Test
  public void testEndNextChapterAfterStart() {
    TextSpan testSpan = new TextSpan(chapter3Pos2, chapter4Pos1);
    assertEquals(CHAPTER_3_2_TO_4_DIFF, testSpan.getLength());
  }

  @Test
  public void testEndMultipleChaptersAfterStart() {
    TextSpan testSpan = new TextSpan(chapter3Pos2, chapter5Pos1);
    assertEquals(CHAPTER_3_2_TO_5_DIFF, testSpan.getLength());
  }

  @Test
  public void testMultipleWholeChapters() {
    TextSpan testSpan = new TextSpan(chapter3PosStart, chapter4PosEnd);
    assertEquals(CHAPTER_3_LENGTH + CHAPTER_4_LENGTH, testSpan.getLength());
  }

}
