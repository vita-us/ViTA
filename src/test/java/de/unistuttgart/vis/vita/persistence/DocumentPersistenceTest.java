package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentContent;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Checks whether a Document can be persisted.
 */
public class DocumentPersistenceTest extends AbstractPersistenceTest {
  
  private static final String TEST_PART_TITLE = "Part I";
  private static final int TEST_PART_NUMBER = 1;
  private static final String TEST_CHAPTER_TITLE = "A long-expected party";
  private static final int TEST_CHAPTER_NUMBER = 1;
  
  private DocumentTestData testData;
  
  @Override
  @Before
  public void setUp() {
    super.setUp();
    testData = new DocumentTestData();
  }

  /**
   * Tests whether one document can be persisted.
   */
  @Test
  public void testPersistOneDocument() {
    // create and set up a new test document
    Document doc = testData.createTestDocument(1);

    // persist test document
    em.persist(doc);
    startNewTransaction();

    // read document from database
    List<Document> docs = readFromDb();

    // check if all data is correct
    assertEquals(1, docs.size());
    Document savedDocument = docs.get(0);
    assertNotNull(savedDocument);

    testData.checkData(savedDocument, 1);
  }

  /**
   * Tests whether one document and its content can be persisted.
   */
  @Test
  public void testPersistOneDocumentWithContent() {
    // create and set up a new test document
    Document doc = testData.createTestDocument(1);

    // add some parts and chapters
    DocumentPart part1 = new DocumentPart();
    part1.setTitle(TEST_PART_TITLE);
    part1.setNumber(TEST_PART_NUMBER);
    doc.getContent().getParts().add(part1);
    
    Chapter chapter1 = new Chapter(doc);
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

    testData.checkData(savedDocument, 1);
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
    Document d1 = testData.createTestDocument(1);
    Document d2 = testData.createTestDocument(2);
    
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
    Document doc = testData.createTestDocument(1);

    em.persist(doc);
    startNewTransaction();

    TypedQuery<Document> allQ = em.createNamedQuery("Document.findAllDocuments", Document.class);
    List<Document> allResults = allQ.getResultList();

    // check whether data is correct
    assertTrue(allResults.size() > 0);
    Document readDocument = allResults.get(0);
    testData.checkData(readDocument, 1);

    String id = readDocument.getId();

    TypedQuery<Document> idQ = em.createNamedQuery("Document.findDocumentById", Document.class);
    idQ.setParameter("documentId", id);
    Document idResult = idQ.getSingleResult();

    testData.checkData(idResult, 1);
    TypedQuery<Document> titleQ = em.createNamedQuery("Document.findDocumentByTitle", 
                                                      Document.class);
    titleQ.setParameter("documentTitle", DocumentTestData.TEST_DOCUMENT_TITLE_1);
    Document titleResult = titleQ.getSingleResult();
    
    testData.checkData(titleResult, 1);
  }
  
}
