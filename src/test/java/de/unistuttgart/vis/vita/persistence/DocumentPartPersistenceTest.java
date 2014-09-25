package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Performs tests whether instances of DocumentPart can be persisted correctly.
 */
public class DocumentPartPersistenceTest extends AbstractPersistenceTest {
  private static final int TEST_DOCUMENT_PART_NUMBER = 1;
  private static final String TEST_DOCUMENT_PART_TITLE = "Book 1";

  /**
   * Checks whether one DocumentPart can be persisted.
   */
  @Test
  public void testPersistOnePart() {
    // first set up DocumentPart
    DocumentPart part = createTestDocumentPart();
    
    // persist this part
    em.persist(part);
    startNewTransaction();
    
    // read persisted parts from database
    List<DocumentPart> parts = readPartsFromDb();
    
    // check whether data is correct
    assertEquals(1, parts.size());
    DocumentPart readPart = parts.get(0);
    checkData(readPart);
  }

  /**
   * Checks whether given data is not <code>null</code> and includes the test data.
   * 
   * @param readPart - the document which should be checked
   */
  private void checkData(DocumentPart readPart) {
    assertNotNull(readPart);
    assertEquals(TEST_DOCUMENT_PART_NUMBER, readPart.getNumber());
    assertEquals(TEST_DOCUMENT_PART_TITLE, readPart.getTitle());
  }
  
  /**
   * Creates a test document part including a test number and title.
   * 
   * @return test document part
   */
  private DocumentPart createTestDocumentPart() {
    DocumentPart part = new DocumentPart();
    part.setNumber(TEST_DOCUMENT_PART_NUMBER);
    part.setTitle(TEST_DOCUMENT_PART_TITLE);
    return part;
  }

  /**
   * Reads DocumentParts from database and returns them.
   * 
   * @return list of document parts
   */
  private List<DocumentPart> readPartsFromDb() {
    TypedQuery<DocumentPart> query = em.createQuery("from DocumentPart", DocumentPart.class);
    List<DocumentPart> parts = query.getResultList();
    return parts;
  }
  
  @Test
  public void testNamedQueries() {
    DocumentPart testPart = createTestDocumentPart();

    em.persist(testPart);
    startNewTransaction();

    // check Named Query finding all parts
    TypedQuery<DocumentPart> allQ =
        em.createNamedQuery("DocumentPart.findAllParts", DocumentPart.class);
    List<DocumentPart> allParts = allQ.getResultList();

    assertTrue(allParts.size() > 0);
    DocumentPart readPart = allParts.get(0);
    checkData(readPart);

    String id = readPart.getId();

    // check Named Query finding document parts by id
    TypedQuery<DocumentPart> idQ =
        em.createNamedQuery("DocumentPart.findPartById", DocumentPart.class);
    idQ.setParameter("partId", id);
    DocumentPart idPart = idQ.getSingleResult();

    checkData(idPart);

    // check Named Query finding document parts by title
    TypedQuery<DocumentPart> titleQ =
        em.createNamedQuery("DocumentPart.findPartByTitle", DocumentPart.class);
    titleQ.setParameter("partTitle", TEST_DOCUMENT_PART_TITLE);
    DocumentPart titlePart = titleQ.getSingleResult();
    
    checkData(titlePart);
  }
  
}
