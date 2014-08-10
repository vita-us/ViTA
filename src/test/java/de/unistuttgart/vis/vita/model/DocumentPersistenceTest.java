package de.unistuttgart.vis.vita.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentContent;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;

/**
 * Checks whether a Document can be persisted.
 */
public class DocumentPersistenceTest {

  // test data
  private static final String TEST_DOCUMENT_AUTHOR = "J. R. R. Tolkien";
  private static final int TEST_DOCUMENT_CHAPTER_COUNT = 22;
  private static final int TEST_DOCUMENT_CHARACTER_COUNT = 83920212;
  private static final String TEST_DOCUMENT_EDITION = "First edition";
  private static final String TEST_DOCUMENT_GENRE = "Fantasy";
  private static final int TEST_DOCUMENT_PERSON_COUNT = 42;
  private static final int TEST_DOCUMENT_PLACE_COUNT = 13;
  private static final int TEST_DOCUMENT_PUPLICATION_YEAR = 1959;
  private static final String TEST_DOCUMENT_PUBLISHER = "George Allen & Unwin";
  private static final String TEST_DOCUMENT_TITLE =
      "The Lord of the Rings - The Fellowship of the Ring";
  
  // attributes
  private EntityManager em;
  private Model model;

  /**
   * Creates model and EntityManager and starts first transaction.
   * 
   * @throws Exception
   */
  @Before
  public void setUp() {
    model = new Model();
    em = model.getEntityManager();
    em.getTransaction().begin();
  }

  /**
   * Tests whether one document can be persisted.
   */
  @Test
  public void testPersistOneDocument() {
    // create and set up a new test document
    Document doc = createTestDocument();

    // persist test document
    em.persist(doc);
    em.getTransaction().commit();
    em.close();

    // read document from database
    List<Document> docs = readFromDb();
    
    // check if all data is correct
    assertEquals(1, docs.size());
    Document savedDocument = docs.get(0);
    assertNotNull(savedDocument);
    
    DocumentMetadata savedMetadata = savedDocument.getMetadata();
    assertNotNull(savedMetadata);
    assertEquals(TEST_DOCUMENT_AUTHOR, savedMetadata.getAuthor());
    assertEquals(TEST_DOCUMENT_EDITION, savedMetadata.getEdition());
    assertEquals(TEST_DOCUMENT_GENRE, savedMetadata.getGenre());
    assertEquals(TEST_DOCUMENT_TITLE, savedMetadata.getTitle());
    assertEquals(TEST_DOCUMENT_PUBLISHER, savedMetadata.getPublisher());
    Calendar savedPublishYear = Calendar.getInstance();
    savedPublishYear.setTime(savedMetadata.getPublishYear());
    assertEquals(TEST_DOCUMENT_PUPLICATION_YEAR, savedPublishYear.get(Calendar.YEAR));
    
    DocumentMetrics savedMetrics = savedDocument.getMetrics();
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
  private Document createTestDocument() {
    Document testDoc = new Document();
    DocumentContent content = new DocumentContent();
    
    // create and set up metadata
    DocumentMetadata metaData = new DocumentMetadata(TEST_DOCUMENT_TITLE, TEST_DOCUMENT_AUTHOR);
    metaData.setEdition(TEST_DOCUMENT_EDITION);
    metaData.setGenre(TEST_DOCUMENT_GENRE);
    metaData.setPublisher(TEST_DOCUMENT_PUBLISHER);
    Calendar publishYear = Calendar.getInstance();
    publishYear.clear();
    publishYear.set(TEST_DOCUMENT_PUPLICATION_YEAR, Calendar.JANUARY, 1);
    metaData.setPublishYear(publishYear.getTime());
    
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
    em = model.getEntityManager();
    em.getTransaction().begin();
    List<Document> docs = em.createQuery("from Document", Document.class).getResultList();
    em.getTransaction().commit();
    return docs;
  }

  /**
   * Closes the EntityManager.
   */
  @After
  public void tearDown() {
    em.close();
  }
  
}
