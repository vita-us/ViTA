package de.unistuttgart.vis.vita.services;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

/**
 * Performs a test on the ProgressService, requesting the progress for a test document using GET.
 */
public class ProgressServiceTest extends ServiceTest {
  
  private String documentId;
  
  /**
   * Persist test document for which the progress is requested.
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    
    DocumentTestData testData = new DocumentTestData();
    
    Document testDoc1 = testData.createTestDocument(1);
    
    documentId = testDoc1.getId();
    
    em.getTransaction().begin();
    em.persist(testDoc1);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(ProgressService.class);
  }

  /**
   * Tests whether progress is returned by progress service.
   */
  @Test
  public void testGetProgress() {
    String path = "documents/" + documentId + "/progress/";
    AnalysisProgress progress = target(path).request().get(AnalysisProgress.class);
    assertNotNull(progress);
  }

}
