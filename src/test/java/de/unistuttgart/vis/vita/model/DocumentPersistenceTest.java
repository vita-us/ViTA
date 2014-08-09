package de.unistuttgart.vis.vita.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

  // constants
  private static final String TEST_DOCUMENT_AUTHOR = "J. R. R. Tolkien";
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
    em = model.getEntityManager();
    em.getTransaction().begin();
    List<Document> docs = em.createQuery("from Document", Document.class).getResultList();
    em.getTransaction().commit();
    
    // check if all data is correct
    assertEquals(1, docs.size());
    Document savedDocument = docs.get(0);
    assertNotNull(savedDocument);
    
    DocumentMetadata savedMetadata = savedDocument.getMetadata();
    assertNotNull(savedMetadata);
    assertEquals(TEST_DOCUMENT_TITLE, savedMetadata.getTitle());
    assertEquals(TEST_DOCUMENT_AUTHOR, savedMetadata.getAuthor());
  }

  /**
   * Creates a test document using constants.
   * 
   * @return a test document
   */
  private Document createTestDocument() {
    Document testDoc = new Document();
    DocumentContent content = new DocumentContent();
    DocumentMetadata metaData = new DocumentMetadata(TEST_DOCUMENT_TITLE, TEST_DOCUMENT_AUTHOR);
    DocumentMetrics metrics = new DocumentMetrics();
    
    testDoc.setContent(content);
    testDoc.setMetadata(metaData);
    testDoc.setMetrics(metrics);
    
    return testDoc;
  }

  @After
  public void tearDown() {
    em.close();
  }

}
