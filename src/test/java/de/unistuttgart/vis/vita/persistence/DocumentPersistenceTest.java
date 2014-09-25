package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentContent;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Checks whether a Document can be persisted.
 */
public class DocumentPersistenceTest extends AbstractPersistenceTest {
  private static final String TEST_DOCUMENT_AUTHOR = "J. R. R. Tolkien";
  private static final int TEST_DOCUMENT_CHAPTER_COUNT = 22;
  private static final int TEST_DOCUMENT_CHARACTER_COUNT = 83920212;
  private static final String TEST_DOCUMENT_EDITION = "First edition";
  private static final String TEST_DOCUMENT_GENRE = "Fantasy";
  private static final int TEST_DOCUMENT_PERSON_COUNT = 42;
  private static final int TEST_DOCUMENT_PLACE_COUNT = 13;
  private static final int TEST_DOCUMENT_PUPLICATION_YEAR = 1959;
  private static final String TEST_DOCUMENT_PUBLISHER = "George Allen & Unwin";
  private static final String TEST_DOCUMENT_TITLE_1 =
      "The Lord of the Rings - The Fellowship of the Ring";
  private static final String TEST_DOCUMENT_TITLE_2 = 
      "The Lord of the Rings - The Two Towers";
  private static final String TEST_PART_TITLE = "Part I";
  private static final int TEST_PART_NUMBER = 1;
  private static final String TEST_CHAPTER_TITLE = "A long-expected party";
  private static final int TEST_CHAPTER_NUMBER = 1;

  /**
   * Tests whether one document can be persisted.
   */
  @Test
  public void testPersistOneDocument() {
    // create and set up a new test document
    Document doc = createTestDocument(TEST_DOCUMENT_TITLE_1);

    // persist test document
    em.persist(doc);
    startNewTransaction();

    // read document from database
    List<Document> docs = readFromDb();

    // check if all data is correct
    assertEquals(1, docs.size());
    Document savedDocument = docs.get(0);
    assertNotNull(savedDocument);

    checkData(savedDocument);
  }

  /**
   * Tests whether one document and its content can be persisted.
   */
  @Test
  public void testPersistOneDocumentWithContent() {
    // create and set up a new test document
    Document doc = createTestDocument(TEST_DOCUMENT_TITLE_1);

    // add some parts and chapters
    DocumentPart part1 = new DocumentPart();
    part1.setTitle(TEST_PART_TITLE);
    part1.setNumber(TEST_PART_NUMBER);
    doc.getContent().getParts().add(part1);
    
    Chapter chapter1 = new Chapter();
    chapter1.setTitle(TEST_CHAPTER_TITLE);
    chapter1.setNumber(TEST_CHAPTER_NUMBER);
    part1.getChapters().add(chapter1);

    // persist test document and its content
    em.persist(doc);
    em.persist(part1);
    em.persist(chapter1);
    startNewTransaction();

    // read document from database
    List<Document> docs = readFromDb();

    // check if all data is correct
    assertEquals(1, docs.size());
    Document savedDocument = docs.get(0);
    assertNotNull(savedDocument);

    
    DocumentContent savedContent = savedDocument.getContent();
    assertNotNull(savedContent);
    assertEquals(1, savedContent.getParts().size());
    DocumentPart savedPart = savedContent.getParts().get(0);
    assertEquals(TEST_PART_NUMBER, savedPart.getNumber());
    assertEquals(TEST_PART_TITLE, savedPart.getTitle());
    assertEquals(1, savedPart.getChapters().size());
    
    Chapter savedChapter = savedPart.getChapters().get(0);
    assertEquals(TEST_CHAPTER_NUMBER, savedChapter.getNumber());
    assertEquals(TEST_CHAPTER_TITLE, savedChapter.getTitle());

    checkData(savedDocument);
  }

  private void checkData(Document documentToCheck) {
    DocumentMetadata savedMetadata = documentToCheck.getMetadata();
    assertNotNull(savedMetadata);
    assertEquals(TEST_DOCUMENT_AUTHOR, savedMetadata.getAuthor());
    assertEquals(TEST_DOCUMENT_EDITION, savedMetadata.getEdition());
    assertEquals(TEST_DOCUMENT_GENRE, savedMetadata.getGenre());
    assertEquals(TEST_DOCUMENT_TITLE_1, savedMetadata.getTitle());
    assertEquals(TEST_DOCUMENT_PUBLISHER, savedMetadata.getPublisher());
    assertEquals(TEST_DOCUMENT_PUPLICATION_YEAR, savedMetadata.getPublishYear());

    DocumentMetrics savedMetrics = documentToCheck.getMetrics();
    assertNotNull(savedMetrics);
    assertEquals(TEST_DOCUMENT_CHAPTER_COUNT, savedMetrics.getChapterCount());
    assertEquals(TEST_DOCUMENT_CHARACTER_COUNT, savedMetrics.getCharacterCount());
    assertEquals(TEST_DOCUMENT_PERSON_COUNT, savedMetrics.getPersonCount());
    assertEquals(TEST_DOCUMENT_PLACE_COUNT, savedMetrics.getPlaceCount());
  }

  /**
   * Creates a test document using test data stored in constants.
   * 
   * @return a test document
   */
  private Document createTestDocument(String title) {
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

  /**
   * Reads a List of Documents from the database.
   * 
   * @return list of Documents
   */
  private List<Document> readFromDb() {
    return em.createQuery("from Document", Document.class).getResultList();
  }
  
  /**
   * Checks whether multiple documents can be persisted.
   */
  @Test
  public void testPersistMultipleDocuments() {
    Document d1 = createTestDocument(TEST_DOCUMENT_TITLE_1);
    Document d2 = createTestDocument(TEST_DOCUMENT_TITLE_2);
    
    em.persist(d1);
    em.persist(d2);
    startNewTransaction();
    
    // read document from database
    List<Document> docs = readFromDb();

    // check if all data is correct
    assertEquals(2, docs.size());
  }

  /**
   * Checks the named queries for documents.
   */
  @Test
  public void testNamedQueries() {
    Document doc = createTestDocument(TEST_DOCUMENT_TITLE_1);

    em.persist(doc);
    startNewTransaction();

    TypedQuery<Document> allQ = em.createNamedQuery("Document.findAllDocuments", Document.class);
    List<Document> allResults = allQ.getResultList();

    // check whether data is correct
    assertTrue(allResults.size() > 0);
    Document readDocument = allResults.get(0);
    checkData(readDocument);

    String id = readDocument.getId();

    TypedQuery<Document> idQ = em.createNamedQuery("Document.findDocumentById", Document.class);
    idQ.setParameter("documentId", id);
    Document idResult = idQ.getSingleResult();

    checkData(idResult);
    TypedQuery<Document> titleQ = em.createNamedQuery("Document.findDocumentByTitle", Document.class);
    titleQ.setParameter("documentTitle", TEST_DOCUMENT_TITLE_1);
    Document titleResult = titleQ.getSingleResult();
    
    checkData(titleResult);
  }
}
