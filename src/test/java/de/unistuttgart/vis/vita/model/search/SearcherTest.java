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
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;

/**
 * 
 * JUnit test on Searcher
 *
 */
public class SearcherTest {

  private Searcher searcher = new Searcher();
  private String documentId = "document1";
  private Document document;
  private int documentLength = 0;

  private final static String[] CHAPTERS_TEXTS =
      {
          "The two boys had started from their cabin home, just outside a small Virginia town, determined to secure fresh food for the family, at that time facing unusual privation. At home sitting. He is at school sitting.",
          "Bob at that time looks. Already smarting at that time of many who had called themselves friends in times past, at that time facing aroused all the Scotch combativeness in their natures.",
          "This is the text of the chapter three.",
          "This is the .text. of chapter four. But at that time more than once a ghost of a smile would chase across his jovial face. Evidently Pat O'Mara was thinking of the plans which he had been forming.",
          "\"Goodluck, Harry,\" he murmured. He turned on his heel and with a swish of his cloak, he was gone."};

  private UnitTestModel model;
  private List<Chapter> chapters = new ArrayList<Chapter>();
  private List<String> chapterIds = new ArrayList<String>();
  private String allChapters = "";

  @Before
  public void setUp() throws IOException, ParseException {

    for (int i = 0; i < 5; i++) {
      documentLength += CHAPTERS_TEXTS[i].length();
    }

    model = new UnitTestModel();
    UnitTestModel.startNewSession();
    storeChapterTexts();
    fillAllChaptersString();


  }

  /**
   * Creates chapters and stores their texts in lucene
   */
  private void storeChapterTexts() throws IOException, ParseException {

    document = new Document();
    document.setId(documentId);
    DocumentMetrics metrics = new DocumentMetrics();
    metrics.setCharacterCount(documentLength);
    document.setMetrics(metrics);
    int globalOffsetStart = 0;
    int globalOffsetEnd = CHAPTERS_TEXTS[0].length();

    for (int i = 0; i < CHAPTERS_TEXTS.length; i++) {
      Chapter chapter = new Chapter();
      chapter.setText(CHAPTERS_TEXTS[i]);
      chapter.setRange(new Range(TextPosition.fromGlobalOffset(globalOffsetStart, documentLength),
          TextPosition.fromGlobalOffset(globalOffsetEnd, documentLength)));
      chapterIds.add(chapter.getId());
      chapters.add(chapter);
      if (i != CHAPTERS_TEXTS.length - 1) {
        globalOffsetStart = globalOffsetEnd;
        int temp = CHAPTERS_TEXTS[i + 1].length();
        globalOffsetEnd = temp + globalOffsetEnd;
      }
    }
    model.getTextRepository().storeChaptersTexts(chapters, documentId);
  }

  /**
   * Stores all chapters texts in one string
   */
  private void fillAllChaptersString() {

    for (int i = 0; i < CHAPTERS_TEXTS.length; i++) {
      allChapters += CHAPTERS_TEXTS[i];
    }
  }

   /**
   * Tests the case-insensitivity of the word
   *
   * @throws IOException
   * @throws ParseException
   */
   @Test
   public void testCaseInsensitivityWord() throws IOException, ParseException {
   List<Range> rangesSmallCases = searcher.searchString(document, "virginia", chapters, model);
  
   assertEquals(1, rangesSmallCases.size());
   assertEquals(69, rangesSmallCases.get(0).getStart().getOffset());
   assertEquals(77, rangesSmallCases.get(0).getEnd().getOffset());
  
  
   List<Range> rangesMixedCases = searcher.searchString(document, "ViRgIniA", chapters, model);
   assertEquals(1, rangesMixedCases.size());
   assertEquals(69, rangesMixedCases.get(0).getStart().getOffset());
   assertEquals(77, rangesMixedCases.get(0).getEnd().getOffset());
  
   String virginia = "";
   for (int i = 69; i < 78; i++) {
   virginia += (CHAPTERS_TEXTS[0].charAt(i));
   }
   assertEquals("Virginia ", virginia);
   }
  
   /**
   * Tests the results regarding the searching for a phrase
   *
   * @throws IOException
   * @throws ParseException
   */
   @Test
   public void testPhrase1() throws IOException, ParseException {
   List<Range> rangesSmallCases =
   searcher.searchString(document, "at that time", chapters, model);
   assertEquals(5, rangesSmallCases.size());
  
   assertEquals(chapters.get(0).getRange().getStart().getOffset() + 132, rangesSmallCases.get(0)
   .getStart().getOffset());
   assertEquals(chapters.get(0).getRange().getStart().getOffset() + 144, rangesSmallCases.get(0)
   .getEnd().getOffset());
  
   String atThatTime1 = "";
   for (int i = chapters.get(0).getRange().getStart().getOffset() + 132; i < chapters.get(0)
   .getRange().getStart().getOffset() + 145; i++) {
   atThatTime1 += allChapters.charAt(i);
   }
   assertEquals("at that time ", atThatTime1);
  
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 4, rangesSmallCases.get(1)
   .getStart().getOffset());
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 16, rangesSmallCases.get(1)
   .getEnd().getOffset());
  
   String atThatTime2 = "";
   for (int i = chapters.get(1).getRange().getStart().getOffset() + 4; i < chapters.get(1)
   .getRange().getStart().getOffset() + 17; i++) {
   atThatTime2 += allChapters.charAt(i);
   }
   assertEquals("at that time ", atThatTime2);
  
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 41, rangesSmallCases.get(2)
   .getStart().getOffset());
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 53, rangesSmallCases.get(2)
   .getEnd().getOffset());
  
   String atThatTime3 = "";
   for (int i = chapters.get(1).getRange().getStart().getOffset() + 41; i < chapters.get(1)
   .getRange().getStart().getOffset() + 54; i++) {
   atThatTime3 += allChapters.charAt(i);
   }
   assertEquals("at that time ", atThatTime3);
  
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 111, rangesSmallCases.get(3)
   .getStart().getOffset());
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 123, rangesSmallCases.get(3)
   .getEnd().getOffset());
  
   String atThatTime4 = "";
   for (int i = chapters.get(1).getRange().getStart().getOffset() + 111; i < chapters.get(1)
   .getRange().getStart().getOffset() + 124; i++) {
   atThatTime4 += allChapters.charAt(i);
   }
  
   assertEquals("at that time ", atThatTime4);
  
   assertEquals(chapters.get(3).getRange().getStart().getOffset() + 40, rangesSmallCases.get(4)
   .getStart().getOffset());
   assertEquals(chapters.get(3).getRange().getStart().getOffset() + 52, rangesSmallCases.get(4)
   .getEnd().getOffset());
  
   String atThatTime5 = "";
   for (int i = chapters.get(3).getRange().getStart().getOffset() + 40; i < chapters.get(3)
   .getRange().getStart().getOffset() + 53; i++) {
   atThatTime5 += allChapters.charAt(i);
   }
   assertEquals("at that time ", atThatTime5);
  
   }
  
   /**
   * Tests the results and case-insensitivity regarding the same phrase in "testPhrase1"
   *
   * @throws IOException
   * @throws ParseException
   */
   @Test
   public void testPhrase2() throws IOException, ParseException {
   List<Range> rangesSmallCases =
   searcher.searchString(document, "At ThAt TiMe", chapters, model);
   assertEquals(5, rangesSmallCases.size());
  
   assertEquals(chapters.get(0).getRange().getStart().getOffset() + 132, rangesSmallCases.get(0)
   .getStart().getOffset());
   assertEquals(chapters.get(0).getRange().getStart().getOffset() + 144, rangesSmallCases.get(0)
   .getEnd().getOffset());
  
   String atThatTime1 = "";
   for (int i = chapters.get(0).getRange().getStart().getOffset() + 132; i < chapters.get(0)
   .getRange().getStart().getOffset() + 145; i++) {
   atThatTime1 += allChapters.charAt(i);
   }
   assertEquals("at that time ", atThatTime1);
  
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 4, rangesSmallCases.get(1)
   .getStart().getOffset());
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 16, rangesSmallCases.get(1)
   .getEnd().getOffset());
  
   String atThatTime2 = "";
   for (int i = chapters.get(1).getRange().getStart().getOffset() + 4; i < chapters.get(1)
   .getRange().getStart().getOffset() + 17; i++) {
   atThatTime2 += allChapters.charAt(i);
   }
   assertEquals("at that time ", atThatTime2);
  
  
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 41, rangesSmallCases.get(2)
   .getStart().getOffset());
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 53, rangesSmallCases.get(2)
   .getEnd().getOffset());
  
   String atThatTime3 = "";
   for (int i = chapters.get(1).getRange().getStart().getOffset() + 41; i < chapters.get(1)
   .getRange().getStart().getOffset() + 54; i++) {
   atThatTime3 += allChapters.charAt(i);
   }
   assertEquals("at that time ", atThatTime3);
  
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 111, rangesSmallCases.get(3)
   .getStart().getOffset());
   assertEquals(chapters.get(1).getRange().getStart().getOffset() + 123, rangesSmallCases.get(3)
   .getEnd().getOffset());
  
   String atThatTime4 = "";
   for (int i = chapters.get(1).getRange().getStart().getOffset() + 111; i < chapters.get(1)
   .getRange().getStart().getOffset() + 124; i++) {
   atThatTime4 += allChapters.charAt(i);
   }
  
   assertEquals("at that time ", atThatTime4);
  
   assertEquals(chapters.get(3).getRange().getStart().getOffset() + 40, rangesSmallCases.get(4)
   .getStart().getOffset());
   assertEquals(chapters.get(3).getRange().getStart().getOffset() + 52, rangesSmallCases.get(4)
   .getEnd().getOffset());
  
   String atThatTime5 = "";
   for (int i = chapters.get(3).getRange().getStart().getOffset() + 40; i < chapters.get(3)
   .getRange().getStart().getOffset() + 53; i++) {
   atThatTime5 += allChapters.charAt(i);
   }
   assertEquals("at that time ", atThatTime5);
   }

  /**
   * Tests the results regarding the searching for a phrase
   *
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public void testPhrase3() throws IOException, ParseException {
    List<Range> ranges = searcher.searchString(document, "he turned on his", chapters, model);

    assertEquals(1, ranges.size());
    assertEquals(chapters.get(4).getRange().getStart().getOffset() + 32, ranges.get(0).getStart()
        .getOffset());
    assertEquals(chapters.get(4).getRange().getStart().getOffset() + 48, ranges.get(0).getEnd()
        .getOffset());
  }

  /**
   * Tests failure of the results regarding the searching for a subphrase
   *
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public void testPhraseFailure() throws IOException, ParseException {
    List<Range> ranges = searcher.searchString(document, "at that tim", chapters, model);
    assertEquals(0, ranges.size());
  }

  /**
   * Tests the results regarding the searching for a stop word
   *
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public void testStopWords() throws IOException, ParseException {

    List<Range> rangesStopWords = searcher.searchString(document, "this", chapters, model);
    assertEquals(2, rangesStopWords.size());


    assertEquals(chapters.get(2).getRange().getStart().getOffset(), rangesStopWords.get(0)
        .getStart().getOffset());
    assertEquals(chapters.get(2).getRange().getStart().getOffset() + 4, rangesStopWords.get(0)
        .getEnd().getOffset());


    assertEquals(chapters.get(3).getRange().getStart().getOffset(), rangesStopWords.get(1)
        .getStart().getOffset());
    assertEquals(chapters.get(3).getRange().getStart().getOffset() + 4, rangesStopWords.get(1)
        .getEnd().getOffset());
  }
}
