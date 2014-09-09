package de.unistuttgart.vis.vita.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * JUnit test on TextRepository
 * 
 *
 */
public class TextRepositoryTest {

  private static final String CHAPTER_ID = "chapterId";
  private static final String CHAPTER_TEXT = "chapterText";
  private static final String INDEX_PATH = "~/.vita/lucene/";

  private TextRepository textRepository = new TextRepository();
  private List<Chapter> chapters = new ArrayList<Chapter>();
  private List<String> storedChaptersIds = new ArrayList<String>();
  private List<String> storedChapterTexts = new ArrayList<String>();
  private Chapter chapter1;
  private IndexReader indexReader;
  private String changedChapter1Text;

  @Before
  public void setUp() throws IOException, ParseException {
    List<Chapter> chapterList1 = new ArrayList<Chapter>();
    List<Chapter> chapterList2 = new ArrayList<Chapter>();
    de.unistuttgart.vis.vita.model.document.Document document1 =
        new de.unistuttgart.vis.vita.model.document.Document();
    de.unistuttgart.vis.vita.model.document.Document document2 =
        new de.unistuttgart.vis.vita.model.document.Document();
    // add to the chapterList1 two chapters with setted text
    chapterList1 = new ArrayList<Chapter>();
    chapter1 = new Chapter(document1);
    chapter1.setText("This is the text of chapter one");
    chapters.add(chapter1);
    chapterList1.add(chapter1);
    Chapter chapter2 = new Chapter(document1);
    chapter2.setText("This is the text of chapter two");
    chapters.add(chapter2);
    chapterList1.add(chapter2);

    // add to the chapterList2 two chapters with setted text
    chapterList2 = new ArrayList<Chapter>();
    Chapter chapter3 = new Chapter(document2);
    chapter3.setText("This is the text of chapter three");
    chapters.add(chapter3);
    chapterList2.add(chapter3);
    Chapter chapter4 = new Chapter(document2);
    chapter4.setText("This is the text of chapter four");
    chapters.add(chapter4);
    chapterList2.add(chapter4);

    textRepository.storeChaptersTexts(chapterList1);
    textRepository.storeChaptersTexts(chapterList2);
    getStoredChaptersIdsAndTexts(chapters.get(0));
    getStoredChaptersIdsAndTexts(chapters.get(1));
    getStoredChaptersIdsAndTexts(chapters.get(2));
    getStoredChaptersIdsAndTexts(chapters.get(3));

    // change the text of chapter1 to "This is a false text"
    chapter1.setText("This is a false text");
    changedChapter1Text = chapter1.getText();

    // after populating the chapter text, the text of chapter1 is the stored text of chapter1
    // "This is the text of chapter one"
    textRepository.populateChapterText(chapter1);

  }

  /**
   * Sets the two lists(storedChaptersIds/storedChapterTexts) with the stored ids and chapters texts
   * with the aid of lucene queries
   * 
   * @param chapter
   * @throws IOException
   * @throws ParseException
   */
  private void getStoredChaptersIdsAndTexts(Chapter chapter) throws IOException, ParseException {
    Directory index = FSDirectory.open(new File(INDEX_PATH + chapter.getDocument().getId()));
    indexReader = DirectoryReader.open(index);
    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
    QueryParser queryParser = new QueryParser(CHAPTER_ID, new StandardAnalyzer());
    Query query = queryParser.parse(chapter.getId());
    ScoreDoc[] hits = indexSearcher.search(query, 1).scoreDocs;
    Document hitDoc = indexSearcher.doc(hits[0].doc);
    storedChaptersIds.add(hitDoc.getField(CHAPTER_ID).stringValue());
    storedChapterTexts.add(hitDoc.getField(CHAPTER_TEXT).stringValue());
  }


  /**
   * Tests the size of the stored indexes
   */
  @Test
  public void testIndexesSize() {
    assertEquals(2, textRepository.getIndexes().size());
  }

  /**
   * Tests the equality of the commited chapter ids and texts with the stored ones
   */
  @Test
  public void testStoreChapterTextsAndIds() {
    for (int i = 0; i < 4; i++) {
      assertEquals(chapters.get(i).getId(), storedChaptersIds.get(i));
      assertEquals(chapters.get(i).getText(), storedChapterTexts.get(i));
    }
  }

  /**
   * Tests the non equality of the changedChapter1Text and the chapter1 text after populating
   */
  @Test
  public void testPopulateChapterText() {
    assertFalse(chapter1.getText().equals(changedChapter1Text));
    assertEquals(chapter1.getText(), chapters.get(0).getText());

  }
}
