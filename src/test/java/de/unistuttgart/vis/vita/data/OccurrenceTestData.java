package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.*;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;

/**
 * Holds test data for Occurrences and methods to create test TextSpans and to check whether given 
 * data matches this test data.
 */
public class OccurrenceTestData {
  
  public static final int TEST_RANGE_START = 105200;
  public static final int TEST_RANGE_END = 105250;
  public static final int TEST_RANGE_LENGTH = 50;
  public static final int TEST_SENTENCE_START = 105175;
  public static final int TEST_SENTENCE_END = 105225;
  
  /**
   * Returns a test Occurrence in the given chapter.
   * 
   * @param chapter - the chapter in which the test Occurrence should lay in
   * @return a test Occurrence laying in the given chapter
   */
  public Occurrence createOccurrence(Chapter chapter) {
    TextPosition testSentenceStartPosition = TextPosition.fromGlobalOffset(TEST_SENTENCE_START, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition testSentenceEndPosition = TextPosition.fromGlobalOffset(TEST_SENTENCE_END, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range testSentenceRange = new Range(testSentenceStartPosition, testSentenceEndPosition);
    Sentence testSentence = new Sentence(testSentenceRange, chapter, 0);

    TextPosition testStartPosition = TextPosition.fromGlobalOffset(TEST_RANGE_START, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition testEndPosition = TextPosition.fromGlobalOffset(TEST_RANGE_END, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range testRange = new Range(testStartPosition, testEndPosition);
    Occurrence testOccurrence = new Occurrence(testSentence, testRange);
    return testOccurrence;
  }
  
  /**
   * Checks whether a given Occurrence matches the test Occurrence.
   * 
   * @param occurrenceToCheck - the occurrence to be checked
   * @param chapterId - the id of the chapter the given Range should lay in
   */
  public void checkData(Occurrence occurrenceToCheck, String chapterId) {
    assertNotNull(occurrenceToCheck);
    assertEquals(TEST_RANGE_LENGTH, occurrenceToCheck.getRange().getLength());
    
    TextPosition actualStart = occurrenceToCheck.getRange().getStart();
    assertEquals(TEST_RANGE_START, actualStart.getOffset());
    
    TextPosition actualEnd = occurrenceToCheck.getRange().getEnd();
    assertEquals(TEST_RANGE_END, actualEnd.getOffset());
        
    TextPosition actualSentenceStart = occurrenceToCheck.getSentence().getRange().getStart();
    assertEquals(TEST_RANGE_START, actualSentenceStart.getOffset());
    
    TextPosition actualSentenceEnd = occurrenceToCheck.getSentence().getRange().getEnd();
    assertEquals(TEST_RANGE_END, actualSentenceEnd.getOffset());
    
    assertEquals(chapterId, occurrenceToCheck.getSentence().getChapter().getId());
    
  }

}
