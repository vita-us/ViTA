package de.unistuttgart.vis.vita.model;

import de.unistuttgart.vis.vita.model.document.Chapter;

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
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JUnit test on TextRepository
 */
public class TextRepositoryTest {

  private static final String CHAPTER_ID = "chapterId";
  private static final String CHAPTER_TEXT = "chapterText";
  private final static String[] CHAPTERS_TEXTS = {"This is the text of chapter one.",
                                                  "This is the text of chapter two.",
                                                  "This is the text of chapter three.",
                                                  "This is the text of chapter four.",};
  private final String document1Id = "document1";
  private final String document2Id = "document2";
  private final Directory directory1 = new RAMDirectory();
  private final Directory directory2 = new RAMDirectory();
  private DirectoryFactory directoryFactory;
  private TextRepository textRepository = new TextRepository();
  private List<Chapter> chapterList1;
  private List<Chapter> chapterList2;

  @Before
  public void setUp() throws IOException, ParseException {
    directoryFactory = mock(DirectoryFactory.class);
    when(directoryFactory.getDirectory(document1Id)).thenReturn(directory1);
    when(directoryFactory.getDirectory(document2Id)).thenReturn(directory2);
    textRepository = new TextRepository(directoryFactory);

    storeChapterTexts();

    chapterList1.get(0).setText("This is a false text");

    // after populating the chapter text, the text of chapter1 is the stored text of chapter1
    // "This is the text of chapter one"
    textRepository.populateChapterText(chapterList1.get(0), document1Id);
    // indexSearcherForDocument1 = textRepository.getIndexSearcherForDocument(document1);

  }

  /**
   * Returns the appropriate lucene document to this chapter
   */
  private Document getStoredDocument(Chapter chapter, String documentId) throws IOException,
                                                                                ParseException {

    Directory index = directoryFactory.getDirectory(documentId);
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
   */
  private Document getStoredDocument(Chapter chapter, IndexSearcher indexSearcherForDocument)
      throws IOException, ParseException {
    QueryParser queryParser = new QueryParser(CHAPTER_ID, new StandardAnalyzer());
    Query query = queryParser.parse(chapter.getId());
    ScoreDoc[] hits = indexSearcherForDocument.search(query, 1).scoreDocs;
    Document hitDoc = indexSearcherForDocument.doc(hits[0].doc);
    return hitDoc;
  }

  /**
   * Creates chapters and stores their texts in lucene
   */
  private void storeChapterTexts() throws IOException, ParseException {

    chapterList1 = new ArrayList<Chapter>();
    chapterList2 = new ArrayList<Chapter>();

    for (int i = 0; i < 2; i++) {
      Chapter chapter = new Chapter();
      chapter.setText(CHAPTERS_TEXTS[i]);
      chapterList1.add(chapter);
    }

    for (int i = 2; i < 4; i++) {
      Chapter chapter = new Chapter();
      chapter.setText(CHAPTERS_TEXTS[i]);
      chapterList2.add(chapter);
    }

    textRepository.storeChaptersTexts(chapterList1, document1Id);
    textRepository.storeChaptersTexts(chapterList2, document2Id);
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
  public void testStoreChapterTextsAndIds() throws IOException, ParseException {
    for (int i = 0; i < 2; i++) {
      assertEquals(chapterList1.get(i).getId(), getStoredDocument(chapterList1.get(i), document1Id)
          .getField(CHAPTER_ID).stringValue());
      assertEquals(chapterList1.get(i).getText(),
                   getStoredDocument(chapterList1.get(i), document1Id).getField(CHAPTER_TEXT)
                       .stringValue());
      assertEquals(chapterList2.get(i).getId(), getStoredDocument(chapterList2.get(i), document2Id)
          .getField(CHAPTER_ID).stringValue());
      assertEquals(chapterList2.get(i).getText(),
                   getStoredDocument(chapterList2.get(i), document2Id).getField(CHAPTER_TEXT)
                       .stringValue());
    }
  }

  /**
   * Tests the non equality of the changedChapter1Text and the chapter1 text after populating
   */
  @Test
  public void testPopulateChapterText() {
    assertFalse(chapterList1.get(0).getText().equals("This is a false text"));
    assertEquals(chapterList1.get(0).getText(), "This is the text of chapter one.");

  }

  /**
   * Tests the equality of the commited chapter ids and texts with the stored ones of document1 with
   * aid of indexsearcher for this document1
   */
  @Test
  public void testGetIndexSearcherForDocument() throws IOException, ParseException {
    for (int i = 0; i < 2; i++) {
      assertEquals(
          chapterList1.get(i).getId(),
          getStoredDocument(chapterList1.get(i),
                            textRepository.getIndexSearcherForDocument(document1Id))
              .getField(CHAPTER_ID)
              .stringValue());
      assertEquals(
          chapterList1.get(i).getText(),
          getStoredDocument(chapterList1.get(i),
                            textRepository.getIndexSearcherForDocument(document1Id))
              .getField(CHAPTER_TEXT)
              .stringValue());

    }
  }
}
