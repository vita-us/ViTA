package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentPartTestData;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Performs tests whether instances of DocumentPart can be persisted correctly.
 */
public class DocumentPartPersistenceTest extends AbstractPersistenceTest {
  
  private DocumentPartTestData testData;
  
  @Override
  public void setUp() {
    super.setUp();
    
    this.testData = new DocumentPartTestData();
  }

  /**
   * Checks whether one DocumentPart can be persisted.
   */
  @Test
  public void testPersistOnePart() {
    // first set up DocumentPart
    DocumentPart part = testData.createTestDocumentPart();
    
    // persist this part
    em.persist(part);
    startNewTransaction();
    
    // read persisted parts from database
    List<DocumentPart> parts = readPartsFromDb();
    
    // check whether data is correct
    assertEquals(1, parts.size());
    DocumentPart readPart = parts.get(0);
    testData.checkData(readPart);
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
    DocumentPart testPart = testData.createTestDocumentPart();

    em.persist(testPart);
    startNewTransaction();

    // check Named Query finding all parts
    TypedQuery<DocumentPart> allQ =
        em.createNamedQuery("DocumentPart.findAllParts", DocumentPart.class);
    List<DocumentPart> allParts = allQ.getResultList();

    assertTrue(allParts.size() > 0);
    DocumentPart readPart = allParts.get(0);
    testData.checkData(readPart);

    String id = readPart.getId();

    // check Named Query finding document parts by id
    TypedQuery<DocumentPart> idQ =
        em.createNamedQuery("DocumentPart.findPartById", DocumentPart.class);
    idQ.setParameter("partId", id);
    DocumentPart idPart = idQ.getSingleResult();

    testData.checkData(idPart);

    // check Named Query finding document parts by title
    TypedQuery<DocumentPart> titleQ =
        em.createNamedQuery("DocumentPart.findPartByTitle", DocumentPart.class);
    titleQ.setParameter("partTitle", DocumentPartTestData.TEST_DOCUMENT_PART_TITLE);
    DocumentPart titlePart = titleQ.getSingleResult();
    
    testData.checkData(titlePart);
  }
  
}
