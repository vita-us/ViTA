package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Document;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Checks whether word clouds can be caught during analysis (response HTTP 409).
 */
public class WordCloudServiceDuringAnalysis extends ServiceTest {

  private String path;

  @Override
  public void setUp() throws Exception {
    super.setUp();

    EntityManager em = getModel().getEntityManager();

    // create a test document
    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getProgress().setStatus(AnalysisStatus.RUNNING);

    // save document id for
    path = "documents/" + testDoc.getId() + "/wordcloud";

    // persist test document
    em.getTransaction().begin();
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(WordCloudService.class);
  }

  /**
   * Checks whether requesting a global word cloud during analysis will result in a HTTP 409
   * Conflict response
   */
  @Test
  public void testGetGlobalWordCloud() {
    Response actualResponse = target(path).request().get();
    assertEquals(409, actualResponse.getStatus());
  }
}
