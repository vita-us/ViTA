package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.*;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;

/**
 * Holds test data for TextSpans and methods to create test TextSpans and to check whether given 
 * data matches this test data.
 */
public class TextSpanTestData {
  
  public static final int TEST_TEXT_SPAN_START = 105200;
  public static final int TEST_TEXT_SPAN_END = 105250;
  public static final int TEST_TEXT_SPAN_LENGTH = 50;
  
  /**
   * Returns a test TextSpan in the given chapter.
   * 
   * @param chapter - the chapter in which the test TextSpan should lay in
   * @return a test TextSpan laying in the given chapter
   */
  public Range createTestTextSpan(Chapter chapter) {
    TextPosition testStartPosition = TextPosition.fromGlobalOffset(chapter, TEST_TEXT_SPAN_START);
    TextPosition testEndPosition = TextPosition.fromGlobalOffset(chapter, TEST_TEXT_SPAN_END);
    Range testTextSpan = new Range(testStartPosition, testEndPosition);
    return testTextSpan;
  }
  
  /**
   * Checks whether a given TextSpan matches the test TextSpan.
   * 
   * @param textSpanToCheck - the TextSpan to be checked
   * @param chapterId - the id of the chapter the given TextSpan should lay in
   */
  public void checkData(Range textSpanToCheck, String chapterId) {
    assertNotNull(textSpanToCheck);
    assertEquals(TEST_TEXT_SPAN_LENGTH, textSpanToCheck.getLength());
    
    TextPosition actualStart = textSpanToCheck.getStart();
    assertEquals(TEST_TEXT_SPAN_START, actualStart.getOffset());
    assertEquals(chapterId, actualStart.getChapter().getId());
    
    TextPosition actualEnd = textSpanToCheck.getEnd();
    assertEquals(TEST_TEXT_SPAN_END, actualEnd.getOffset());
    assertEquals(chapterId, actualStart.getChapter().getId());
  }

}
