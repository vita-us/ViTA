package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;

/**
 * Holds test data for chapters and methods to create test chapters and check whether given data
 * matches the test data.
 */
public class ChapterTestData {
  
  public static final int TEST_CHAPTER_LENGTH = 2531244;
  public static final int TEST_CHAPTER_NUMBER = 11;
  public static final String TEST_CHAPTER_TEXT = "This is a very short Chapter.";
  public static final String TEST_CHAPTER_TITLE = "A Knife in the Dark";
  
  /**
   * Creates a new Chapter, sets attributes to test values and returns it.
   * 
   * @return test chapter
   */
  public  Chapter createTestChapter() {
    return createTestChapter(null);
  }
  
  /**
   * Creates a new Chapter belonging to the given Document and sets attributes to test values and 
   * returns it.
   * 
   * @param pDocument - the document the created chapter should belong to
   * @return
   */
  public Chapter createTestChapter(Document pDocument) {
    Chapter chapter = new Chapter(pDocument);

    chapter.setLength(TEST_CHAPTER_LENGTH);
    chapter.setNumber(TEST_CHAPTER_NUMBER);
    chapter.setText(TEST_CHAPTER_TEXT);
    chapter.setTitle(TEST_CHAPTER_TITLE);

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
    assertEquals(TEST_CHAPTER_TEXT, chapterToCheck.getText());
    assertEquals(TEST_CHAPTER_TITLE, chapterToCheck.getTitle());
  }

}
