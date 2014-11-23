package de.unistuttgart.vis.vita.model.search;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import de.unistuttgart.vis.vita.model.UnitTestModel;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.TextSpan;

/**
 * 
 * JUnit test on Searcher
 *
 */
public class SearcherTest {

  private Searcher searcher = new Searcher();
  private String documentId = "document1";

  private final static String[] CHAPTERS_TEXTS =
      {
          "The two boys had started from their cabin home, just outside a small Virginia town, determined to secure fresh food for the family, at that time facing unusual privation. At home sitting. He is at school sitting.",
          "Bob at that time looks. Already smarting at that time of many who had called themselves friends in times past, at that time facing aroused all the Scotch combativeness in their natures.",
          "This is the text of the chapter three.",
          "This is the .text. of chapter four. But at that time more than once a ghost of a smile would chase across his jovial face. Evidently Pat O'Mara was thinking of the plans which he had been forming.",};

  private UnitTestModel model;
  private List<Chapter> chapters = new ArrayList<Chapter>();
  private List<String> chapterIds = new ArrayList<String>();

  @Before
  public void setUp() throws IOException, ParseException {

    model = new UnitTestModel();
    UnitTestModel.startNewSession();
    storeChapterTexts();
  }

  /**
   * Creates chapters and stores their texts in lucene
   */
  private void storeChapterTexts() throws IOException, ParseException {

    for (int i = 0; i < 4; i++) {
      Chapter chapter = new Chapter();
      chapter.setText(CHAPTERS_TEXTS[i]);
      chapterIds.add(chapter.getId());
      chapters.add(chapter);
    }
    model.getTextRepository().storeChaptersTexts(chapters, documentId);
  }

   /**
   * Tests the case-insensitivity of the word
   *
   * @throws IOException
   * @throws ParseException
   */
   @Test
   public void testCaseInsensitivityWord() throws IOException, ParseException {
   List<TextSpan> spansSmallCases = searcher.searchString(documentId, "virginia", chapters,
   model);
  
   assertEquals(1, spansSmallCases.size());
   assertEquals(chapterIds.get(0), spansSmallCases.get(0).getStart().getChapter().getId());
   assertEquals(chapterIds.get(0), spansSmallCases.get(0).getEnd().getChapter().getId());
   assertEquals(69, spansSmallCases.get(0).getStart().getOffset());
   assertEquals(76, spansSmallCases.get(0).getEnd().getOffset());
  
  
   List<TextSpan> spansMixedCases = searcher.searchString(documentId, "ViRgIniA", chapters,
   model);
   assertEquals(1, spansMixedCases.size());
   assertEquals(chapterIds.get(0), spansMixedCases.get(0).getStart().getChapter().getId());
   assertEquals(chapterIds.get(0), spansMixedCases.get(0).getEnd().getChapter().getId());
   assertEquals(69, spansMixedCases.get(0).getStart().getOffset());
   assertEquals(76, spansMixedCases.get(0).getEnd().getOffset());
  
   String virginia = "";
   for (int i = 69; i < 77; i++) {
   virginia += (CHAPTERS_TEXTS[0].charAt(i));
   }
   assertEquals("Virginia", virginia);
   }
  
   /**
   * Tests the results regarding the searching for a phrase
   *
   * @throws IOException
   * @throws ParseException
   */
   @Test
   public void testPhrase1() throws IOException, ParseException {
   List<TextSpan> spansSmallCases = searcher.searchString(documentId, "at that time", chapters, model);
   assertEquals(5, spansSmallCases.size());
  
   assertEquals(chapterIds.get(1), spansSmallCases.get(0).getStart().getChapter().getId());
   assertEquals(chapterIds.get(1), spansSmallCases.get(0).getEnd().getChapter().getId());
   assertEquals(4, spansSmallCases.get(0).getStart().getOffset());
   assertEquals(15, spansSmallCases.get(0).getEnd().getOffset());
  
   String atThatTime1 = "";
   for (int i = 4; i < 16; i++) {
   atThatTime1 += (CHAPTERS_TEXTS[1].charAt(i));
   }
   assertEquals("at that time", atThatTime1);
  
   assertEquals(chapterIds.get(1), spansSmallCases.get(1).getStart().getChapter().getId());
   assertEquals(chapterIds.get(1), spansSmallCases.get(1).getEnd().getChapter().getId());
   assertEquals(41, spansSmallCases.get(1).getStart().getOffset());
   assertEquals(52, spansSmallCases.get(1).getEnd().getOffset());
  
   String atThatTime2 = "";
   for (int i = 41; i < 53; i++) {
   atThatTime2 += (CHAPTERS_TEXTS[1].charAt(i));
   }
   assertEquals("at that time", atThatTime2);
  
   assertEquals(chapterIds.get(1), spansSmallCases.get(2).getStart().getChapter().getId());
   assertEquals(chapterIds.get(1), spansSmallCases.get(2).getEnd().getChapter().getId());
   assertEquals(111, spansSmallCases.get(2).getStart().getOffset());
   assertEquals(122, spansSmallCases.get(2).getEnd().getOffset());
  
   String atThatTime3 = "";
   for (int i = 111; i < 123; i++) {
   atThatTime3 += (CHAPTERS_TEXTS[1].charAt(i));
   }
   assertEquals("at that time", atThatTime3);
   assertEquals(chapterIds.get(0), spansSmallCases.get(3).getStart().getChapter().getId());
   assertEquals(chapterIds.get(0), spansSmallCases.get(3).getEnd().getChapter().getId());
   assertEquals(132, spansSmallCases.get(3).getStart().getOffset());
   assertEquals(143, spansSmallCases.get(3).getEnd().getOffset());
  
   String atThatTime4 = "";
   for (int i = 132; i < 144; i++) {
   atThatTime4 += (CHAPTERS_TEXTS[0].charAt(i));
   }
   assertEquals("at that time", atThatTime4);
   assertEquals(chapterIds.get(3), spansSmallCases.get(4).getStart().getChapter().getId());
   assertEquals(chapterIds.get(3), spansSmallCases.get(4).getEnd().getChapter().getId());
   assertEquals(40, spansSmallCases.get(4).getStart().getOffset());
   assertEquals(51, spansSmallCases.get(4).getEnd().getOffset());
  
   String atThatTime5 = "";
   for (int i = 40; i < 52; i++) {
   atThatTime5 += (CHAPTERS_TEXTS[3].charAt(i));
   }
   assertEquals("at that time", atThatTime5);
  
   }
  
   /**
   * Tests the results and case-insensitivity regarding the same phrase in "testPhrase1"
   *
   * @throws IOException
   * @throws ParseException
   */
   @Test
   public void testPhrase2() throws IOException, ParseException {
   List<TextSpan> spansMixedCases =
   searcher.searchString(documentId, "At ThAt tiMe", chapters, model);
  
   assertEquals(5, spansMixedCases.size());
   assertEquals(chapterIds.get(1), spansMixedCases.get(0).getStart().getChapter().getId());
   assertEquals(chapterIds.get(1), spansMixedCases.get(0).getEnd().getChapter().getId());
   assertEquals(4, spansMixedCases.get(0).getStart().getOffset());
   assertEquals(15, spansMixedCases.get(0).getEnd().getOffset());
  
   String atThatTime1 = "";
   for (int i = 4; i < 16; i++) {
   atThatTime1 += (CHAPTERS_TEXTS[1].charAt(i));
   }
   assertEquals("at that time", atThatTime1);
  
   assertEquals(chapterIds.get(1), spansMixedCases.get(1).getStart().getChapter().getId());
   assertEquals(chapterIds.get(1), spansMixedCases.get(1).getEnd().getChapter().getId());
   assertEquals(41, spansMixedCases.get(1).getStart().getOffset());
   assertEquals(52, spansMixedCases.get(1).getEnd().getOffset());
  
   String atThatTime2 = "";
   for (int i = 41; i < 53; i++) {
   atThatTime2 += (CHAPTERS_TEXTS[1].charAt(i));
   }
   assertEquals("at that time", atThatTime2);
  
   assertEquals(chapterIds.get(1), spansMixedCases.get(2).getStart().getChapter().getId());
   assertEquals(chapterIds.get(1), spansMixedCases.get(2).getEnd().getChapter().getId());
   assertEquals(111, spansMixedCases.get(2).getStart().getOffset());
   assertEquals(122, spansMixedCases.get(2).getEnd().getOffset());
  
   String atThatTime3 = "";
   for (int i = 111; i < 123; i++) {
   atThatTime3 += (CHAPTERS_TEXTS[1].charAt(i));
   }
   assertEquals("at that time", atThatTime3);
   assertEquals(chapterIds.get(0), spansMixedCases.get(3).getStart().getChapter().getId());
   assertEquals(chapterIds.get(0), spansMixedCases.get(3).getEnd().getChapter().getId());
   assertEquals(132, spansMixedCases.get(3).getStart().getOffset());
   assertEquals(143, spansMixedCases.get(3).getEnd().getOffset());
  
   String atThatTime4 = "";
   for (int i = 132; i < 144; i++) {
   atThatTime4 += (CHAPTERS_TEXTS[0].charAt(i));
   }
   assertEquals("at that time", atThatTime4);
   assertEquals(chapterIds.get(3), spansMixedCases.get(4).getStart().getChapter().getId());
   assertEquals(chapterIds.get(3), spansMixedCases.get(4).getEnd().getChapter().getId());
   assertEquals(40, spansMixedCases.get(4).getStart().getOffset());
   assertEquals(51, spansMixedCases.get(4).getEnd().getOffset());
  
   String atThatTime5 = "";
   for (int i = 40; i < 52; i++) {
   atThatTime5 += (CHAPTERS_TEXTS[3].charAt(i));
   }
   assertEquals("at that time", atThatTime5);
  
   }
  
   /**
   * Tests failure of the results regarding the searching for a subphrase
   *
   * @throws IOException
   * @throws ParseException
   */
   @Test
   public void testPhraseFailure() throws IOException, ParseException {
   List<TextSpan> spans = searcher.searchString(documentId, "at that tim", chapters, model);
   assertEquals(0, spans.size());
   }

  /**
   * Tests the results regarding the searching for a stop word
   * 
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public void testStopWords() throws IOException, ParseException {

    List<TextSpan> spansStopWords = searcher.searchString(documentId, "this", chapters, model);
    assertEquals(2, spansStopWords.size());

    assertEquals(chapterIds.get(2), spansStopWords.get(0).getStart().getChapter().getId());
    assertEquals(chapterIds.get(2), spansStopWords.get(0).getEnd().getChapter().getId());
    assertEquals(0, spansStopWords.get(0).getStart().getOffset());
    assertEquals(3, spansStopWords.get(0).getEnd().getOffset());

    assertEquals(chapterIds.get(3), spansStopWords.get(1).getStart().getChapter().getId());
    assertEquals(chapterIds.get(3), spansStopWords.get(1).getEnd().getChapter().getId());
    assertEquals(0, spansStopWords.get(1).getStart().getOffset());
    assertEquals(3, spansStopWords.get(1).getEnd().getOffset());
  }
}
