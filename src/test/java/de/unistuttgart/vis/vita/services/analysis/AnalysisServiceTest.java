package de.unistuttgart.vis.vita.services.analysis;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.ServiceTest;

/**
 * Performs a test on the AnalysisService, checking whether an analysis of a document can be 
 * stopped.
 */
public class AnalysisServiceTest extends ServiceTest {

  private String documentId;
  
  private EntityManager em;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    em = getModel().getEntityManager();
    
    // need a document to stop its analysis
    Document testDocument = new DocumentTestData().createTestDocument(1);
    
    // save id for request
    documentId = testDocument.getId();
    
    // persist test document
    em.getTransaction().begin();
    em.persist(testDocument);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(AnalysisService.class);
  }

  /**
   * Checks whether analysis of an existing document can be stopped using REST.
   */
  @Test
  public void testStopAnalysis() {
    // set up request
    String path = "/documents/" + documentId + "/analysis/stop";
    
    // send request and get response
    Response actualResponse = target(path).request().put(Entity.json(new EmptyEntity()));
    
    // check response is empty (Status 204)
    assertNotNull(actualResponse);
    assertEquals(204, actualResponse.getStatus());
  }

}
