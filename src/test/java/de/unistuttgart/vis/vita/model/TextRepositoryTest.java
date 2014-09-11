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
import org.hibernate.type.descriptor.sql.NCharTypeDescriptor;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

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
  private Chapter chapter1;
  private List<Chapter> chapterList1;
  private List<Chapter> chapterList2;
  private IndexSearcher indexSearcherForDocument1;
  private de.unistuttgart.vis.vita.model.document.Document document1;
  private final static String[] CHAPTERS_TEXTS = {"This is the text of chapter one.",
      "This is the text of chapter two.", "This is the text of chapter three.",
      "This is the text of chapter four.",};

  @Before
  public void setUp() throws IOException, ParseException {

    storeChapterTexts();

    // change the text of chapter1 to "This is a false text"
    chapter1.setText("This is a false text");

    // after populating the chapter text, the text of chapter1 is the stored text of chapter1
    // "This is the text of chapter one"
    textRepository.populateChapterText(chapter1);
    indexSearcherForDocument1 = textRepository.getIndexSearcherForDocument(document1);

  }

  /**
   * Returns the appropriate lucene document to this chapter
   * 
   * @param chapter
   * @throws IOException
   * @throws ParseException
   */
  private Document getStoredDocument(Chapter chapter) throws IOException, ParseException {
    Directory index = FSDirectory.open(new File(INDEX_PATH + chapter.getDocument().getId()));
    IndexReader indexReader = DirectoryReader.open(index);
    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
    QueryParser queryParser = new QueryParser(CHAPTER_ID, new StandardAnalyzer());
    Query query = queryParser.parse(chapter.getId());
    ScoreDoc[] hits = indexSearcher.search(query, 1).scoreDocs;
    Document hitDoc = indexSearcher.doc(hits[0].doc);
    return hitDoc;
  }

  /**
   * Returns the appropriate lucene document to the chapter with indexSearcherForDocument1
   * 
   * @param chapter
   * @throws IOException
   * @throws ParseException
   */
  private Document getStoredDocument1(Chapter chapter, IndexSearcher indexSearcherForDocument1)
      throws IOException, ParseException {
    QueryParser queryParser = new QueryParser(CHAPTER_ID, new StandardAnalyzer());
    Query query = queryParser.parse(chapter.getId());
    ScoreDoc[] hits = indexSearcherForDocument1.search(query, 1).scoreDocs;
    Document hitDoc = indexSearcherForDocument1.doc(hits[0].doc);
    return hitDoc;
  }

  /**
   * Creates chapters and stores their texts in lucene
   * 
   * @throws IOException
   */
  private void storeChapterTexts() throws IOException {
    document1 = new de.unistuttgart.vis.vita.model.document.Document();
    de.unistuttgart.vis.vita.model.document.Document document2 =
        new de.unistuttgart.vis.vita.model.document.Document();

    chapterList1 = new ArrayList<Chapter>();
    chapterList2 = new ArrayList<Chapter>();

    for (int i = 0; i < 2; i++) {
      Chapter chapter = new Chapter(document1);
      chapter.setText(CHAPTERS_TEXTS[i]);
      chapterList1.add(chapter);
    }

    // chapter1 is the first chapter of chapterList1
    chapter1 = chapterList1.get(0);

    for (int i = 2; i < 4; i++) {
      Chapter chapter = new Chapter(document2);
      chapter.setText(CHAPTERS_TEXTS[i]);
      chapterList2.add(chapter);
    }

    textRepository.storeChaptersTexts(chapterList1);
    textRepository.storeChaptersTexts(chapterList2);
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
   * 
   * @throws ParseException
   * @throws IOException
   */
  @Test
  public void testStoreChapterTextsAndIds() throws IOException, ParseException {
    for (int i = 0; i < 2; i++) {
      assertEquals(chapterList1.get(i).getId(),
          getStoredDocument(chapterList1.get(i)).getField(CHAPTER_ID).stringValue());
      assertEquals(chapterList1.get(i).getText(),
          getStoredDocument(chapterList1.get(i)).getField(CHAPTER_TEXT).stringValue());
      assertEquals(chapterList2.get(i).getId(),
          getStoredDocument(chapterList2.get(i)).getField(CHAPTER_ID).stringValue());
      assertEquals(chapterList2.get(i).getText(),
          getStoredDocument(chapterList2.get(i)).getField(CHAPTER_TEXT).stringValue());
    }
  }

  /**
   * Tests the non equality of the changedChapter1Text and the chapter1 text after populating
   */
  @Test
  public void testPopulateChapterText() {
    assertFalse(chapter1.getText().equals("This is a false text"));
    assertEquals(chapter1.getText(), chapterList1.get(0).getText());

  }

  /**
   * Tests the equality of the commited chapter ids and texts with the stored ones of document1 with
   * aid of indexsearcher for this document1
   */
  @Test
  public void testGetIndexSearcherForDocument() throws IOException, ParseException {
    for (int i = 0; i < 2; i++) {
      assertEquals(chapterList1.get(i).getId(),
          getStoredDocument1(chapterList1.get(i), indexSearcherForDocument1).getField(CHAPTER_ID)
              .stringValue());
      assertEquals(chapterList1.get(i).getText(),
          getStoredDocument1(chapterList1.get(i), indexSearcherForDocument1).getField(CHAPTER_TEXT)
              .stringValue());

    }
  }
}
