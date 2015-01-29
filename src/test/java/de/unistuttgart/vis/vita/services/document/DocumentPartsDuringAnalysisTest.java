package de.unistuttgart.vis.vita.services.document;

import static org.junit.Assert.*;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.ServiceTest;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

/**
 * Checks whether document parts can be caught during analysis.
 */
public class DocumentPartsDuringAnalysisTest extends ServiceTest {

  private String docId;

  @Override
  public void setUp() throws Exception {
    super.setUp();

    EntityManager em = getModel().getEntityManager();

    // a document is needed to perform this request
    Document testDoc = new Document();
    testDoc.getProgress().setStatus(AnalysisStatus.RUNNING);

    // save id for the request path
    docId = testDoc.getId();

    // persist test document
    em.getTransaction().begin();
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(DocumentPartsService.class);
  }

  /**
   * Check whether requesting document parts during analysis results in HTTP 409 response.
   */
  @Test
  public void testGetParts() {
    String path = "documents/" + docId + "/parts";
    Response actualResponse = target(path).request().get();
    assertEquals(409, actualResponse.getStatus());
  }
}
