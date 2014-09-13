package de.unistuttgart.vis.vita.services;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentPartTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.services.responses.DocumentPartsResponse;

/**
 * Performs a simple test on the PartsService.
 */
public class DocumentPartsServiceTest extends ServiceTest {
  
  private String docId;
  private DocumentPartTestData testData;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    
    // set up document parts test data
    testData = new DocumentPartTestData();
    DocumentPart testPart1 = testData.createTestDocumentPart(1);
    DocumentPart testPart2 = testData.createTestDocumentPart(2);
    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getContent().getParts().add(testPart1);
    
    docId = testDoc.getId();
    
    // persist test data in database
    em.getTransaction().begin();
    em.persist(testPart1);
    em.persist(testPart2);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(DocumentPartsService.class);
  }

  /**
   * Checks whether document parts can be caught using the REST interface.
   */
  @Test
  public void testGetParts() {
    String path = "documents/" + docId + "/parts";
    DocumentPartsResponse actualResponse = target(path).request().get(DocumentPartsResponse.class);
    
    assertEquals(1, actualResponse.getTotalCount());
    List<DocumentPart> parts = actualResponse.getParts();
    assertEquals(1, parts.size());
    testData.checkData(parts.get(0), 1);
  }

}
