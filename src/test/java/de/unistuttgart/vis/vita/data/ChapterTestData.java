package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.*;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;

/**
 * Holds test data for chapters and methods to create test chapters and check whether given data
 * matches the test data.
 */
public class ChapterTestData {
  public static final int TEST_CHAPTER_RANGE_START = 100000;
  public static final int TEST_CHAPTER_LENGTH = 200000;
  public static final int TEST_CHAPTER_RANGE_END = 300000;

  public static final int TEST_CHAPTER_NUMBER = 11;
  public static final String TEST_CHAPTER_TEXT = "This is a very short Chapter.";
  public static final String TEST_CHAPTER_TITLE = "A Knife in the Dark";

  /**
   * Creates a new Chapter belonging to the given Document and sets attributes to test values and
   * returns it.
   *
   * @return
   */
  public Chapter createTestChapter() {
    Chapter chapter = new Chapter();

    chapter.setLength(TEST_CHAPTER_LENGTH);
    chapter.setNumber(TEST_CHAPTER_NUMBER);
    chapter.setText(TEST_CHAPTER_TEXT);
    chapter.setTitle(TEST_CHAPTER_TITLE);
    chapter.setRange(new Range(
        TextPosition.fromGlobalOffset(chapter, 0),
        TextPosition.fromGlobalOffset(chapter, TEST_CHAPTER_LENGTH)));
    chapter.setDocumentLength(TEST_CHAPTER_RANGE_END);

    return chapter;
  }

  /**
   * Checks whether the given chapter is not <code>null</code> and includes the correct test data.
   *
   * @param chapterToCheck - the chapter which should be checked
   */
  public void checkData(Chapter chapterToCheck) {
    assertNotNull(chapterToCheck);
    assertEquals(TEST_CHAPTER_LENGTH, chapterToCheck.getLength());
    assertEquals(TEST_CHAPTER_NUMBER, chapterToCheck.getNumber());
    assertEquals(TEST_CHAPTER_TITLE, chapterToCheck.getTitle());
  }

}
