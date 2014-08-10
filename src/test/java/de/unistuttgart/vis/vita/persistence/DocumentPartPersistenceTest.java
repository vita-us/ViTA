package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Performs tests whether instances of DocumentPart can be persisted correctly.
 */
public class DocumentPartPersistenceTest {
  
  // test data
  private static final int TEST_DOCUMENT_PART_NUMBER = 1;
  private static final String TEST_DOCUMENT_PART_TITLE = "Book 1";

  // attributes
  private Model model;
  private EntityManager em;

  /**
   * Creates Model and EntityManager and starts first Transaction.
   */
  @Before
  public void setUp() {
    model = new Model();
    em = model.getEntityManager();
    em.getTransaction().begin();
  }

  /**
   * Checks whether one DocumentPart can be persisted.
   */
  @Test
  public void testPersistOnePart() {
    // first set up DocumentPart
    DocumentPart part = new DocumentPart();
    part.setNumber(TEST_DOCUMENT_PART_NUMBER);
    part.setTitle(TEST_DOCUMENT_PART_TITLE);
    
    // persist this part
    em.persist(part);
    em.getTransaction().commit();
    em.close();
    
    // read persisted parts from database
    List<DocumentPart> parts = readPartsFromDb();
    
    // check whether data is correct
    assertEquals(1, parts.size());
    DocumentPart readPart = parts.get(0);
    assertNotNull(readPart);
    assertEquals(TEST_DOCUMENT_PART_NUMBER, readPart.getNumber());
    assertEquals(TEST_DOCUMENT_PART_TITLE, readPart.getTitle());
  }

  /**
   * Reads DocumentParts from database and returns them.
   * 
   * @return list of document parts
   */
  private List<DocumentPart> readPartsFromDb() {
    em = model.getEntityManager();
    em.getTransaction().begin();
    TypedQuery<DocumentPart> query = em.createQuery("from DocumentPart", DocumentPart.class);
    List<DocumentPart> parts = query.getResultList();
    em.getTransaction().commit();
    return parts;
  }
  
  /**
   * Finally closes the EntityManager.
   */
  @After
  public void tearDown() {
    em.close();
  }

}
