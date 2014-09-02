package de.unistuttgart.vis.vita.services;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.responses.DocumentsResponse;

/**
 * Performs tests on DocumentsService.
 */
public class DocumentsServiceTest extends ServiceTest {
  
  private static final int TEST_DOCUMENT_COUNT = 10;
  private static final int TEST_OFFSET = 0;
  
  private DocumentTestData testData;
  
  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    
    testData = new DocumentTestData();
    
    em.getTransaction().begin();
    em.persist(testData.createTestDocument(1));
    em.persist(testData.createTestDocument(2));
    em.getTransaction().commit();
    em.close();
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(DocumentsService.class);
  }
  
  /**
   * Tests whether documents can be caught using the REST interface.
   */
  @Test
  public void testGetDocuments() {
    DocumentsResponse actualResponse = target("documents").queryParam("offset", TEST_OFFSET)
                                                          .queryParam("count", TEST_DOCUMENT_COUNT)
                                                          .request().get(DocumentsResponse.class);
    assertEquals(2, actualResponse.getTotalCount());
    List<Document> docs = actualResponse.getDocuments();
    assertEquals(2, docs.size());
    testData.checkData(docs.get(0), 1);
    testData.checkData(docs.get(1), 2);
  }
  
}
