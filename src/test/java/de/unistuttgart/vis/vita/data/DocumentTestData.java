package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentContent;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;

/**
 * Holds test data for documents and a method to create test documents.
 */
public class DocumentTestData {
  
  public static final String TEST_DOCUMENT_TITLE_1 =
      "The Lord of the Rings - The Fellowship of the Ring";
  public static final String TEST_DOCUMENT_TITLE_2 = 
      "The Lord of the Rings - The Two Towers";
  public static final String TEST_DOCUMENT_AUTHOR = "J. R. R. Tolkien";
  public static final int TEST_DOCUMENT_CHAPTER_COUNT = 22;
  public static final int TEST_DOCUMENT_CHARACTER_COUNT = 83920212;
  public static final String TEST_DOCUMENT_EDITION = "First edition";
  public static final String TEST_DOCUMENT_GENRE = "Fantasy";
  public static final int TEST_DOCUMENT_PERSON_COUNT = 42;
  public static final int TEST_DOCUMENT_PLACE_COUNT = 13;
  public static final int TEST_DOCUMENT_PUPLICATION_YEAR = 1959;
  public static final String TEST_DOCUMENT_PUBLISHER = "George Allen & Unwin";
  
  /**
   * Creates a test document using test data stored in constants.
   * 
   * @return a test document
   */
  public Document createTestDocument(int number) {
    if (number == 1) {
      return generateTestDocument(TEST_DOCUMENT_TITLE_1);
    } else if (number == 2) {
      return generateTestDocument(TEST_DOCUMENT_TITLE_2);
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  private Document generateTestDocument(String title) {
    Document testDoc = new Document();
    DocumentContent content = new DocumentContent();

    // create and set up metadata
    DocumentMetadata metaData = new DocumentMetadata(title, TEST_DOCUMENT_AUTHOR);
    metaData.setEdition(TEST_DOCUMENT_EDITION);
    metaData.setGenre(TEST_DOCUMENT_GENRE);
    metaData.setPublisher(TEST_DOCUMENT_PUBLISHER);
    metaData.setPublishYear(TEST_DOCUMENT_PUPLICATION_YEAR);

    // create and set up metrics
    DocumentMetrics metrics = new DocumentMetrics();
    metrics.setChapterCount(TEST_DOCUMENT_CHAPTER_COUNT);
    metrics.setPersonCount(TEST_DOCUMENT_PERSON_COUNT);
    metrics.setPlaceCount(TEST_DOCUMENT_PLACE_COUNT);
    metrics.setCharacterCount(TEST_DOCUMENT_CHARACTER_COUNT);

    // set up document
    testDoc.setContent(content);
    testDoc.setMetadata(metaData);
    testDoc.setMetrics(metrics);
    
    return testDoc;
  }
  
  public void checkData(Document documentToCheck, int number) {
    DocumentMetadata savedMetadata = documentToCheck.getMetadata();
    assertNotNull(savedMetadata);
    
    String savedTitle = savedMetadata.getTitle();
    
    if (number == 1) {
      assertEquals(DocumentTestData.TEST_DOCUMENT_TITLE_1, savedTitle);
    } else if(number == 2) {
      assertEquals(DocumentTestData.TEST_DOCUMENT_TITLE_2, savedTitle);
    }
    
    assertEquals(DocumentTestData.TEST_DOCUMENT_AUTHOR, savedMetadata.getAuthor());
    assertEquals(DocumentTestData.TEST_DOCUMENT_EDITION, savedMetadata.getEdition());
    assertEquals(DocumentTestData.TEST_DOCUMENT_GENRE, savedMetadata.getGenre());

    assertEquals(DocumentTestData.TEST_DOCUMENT_PUBLISHER, savedMetadata.getPublisher());
    assertEquals(DocumentTestData.TEST_DOCUMENT_PUPLICATION_YEAR, savedMetadata.getPublishYear().intValue());

    DocumentMetrics savedMetrics = documentToCheck.getMetrics();
    assertNotNull(savedMetrics);
    assertEquals(DocumentTestData.TEST_DOCUMENT_CHAPTER_COUNT, savedMetrics.getChapterCount());
    assertEquals(DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT, savedMetrics.getCharacterCount());
    assertEquals(DocumentTestData.TEST_DOCUMENT_PERSON_COUNT, savedMetrics.getPersonCount());
    assertEquals(DocumentTestData.TEST_DOCUMENT_PLACE_COUNT, savedMetrics.getPlaceCount());
  }
  
}
