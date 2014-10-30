package de.unistuttgart.vis.vita.services;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.ProgressTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

/**
 * Performs a test on the ProgressService, requesting the progress for a test document using GET.
 */
public class ProgressServiceTest extends ServiceTest {
  
  private String documentId;
  private String documentIdWithoutProgress;
  private ProgressTestData testData;
  
  /**
   * Persist test document for which the progress is requested.
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();
    // create test data instances
    testData = new ProgressTestData();
    AnalysisProgress testProgress = testData.createTestProgress();
    DocumentTestData documenttestData = new DocumentTestData();
    
    Document testDoc1 = documenttestData.createTestDocument(1);
    testDoc1.setProgress(testProgress);
    documentId = testDoc1.getId();
    
    Document testDoc2 = documenttestData.createTestDocument(2);
    documentIdWithoutProgress = testDoc2.getId();

    // persist test document
    EntityManager em = Model.createUnitTestModel().getEntityManager();
     
    em.getTransaction().begin();
    em.persist(testDoc1);
    em.persist(testProgress);
    em.persist(testDoc2);
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
    AnalysisProgress actualProgress = target(path).request().get(AnalysisProgress.class);

    // check data
    testData.checkData(actualProgress);
  }

  /**
   * Tests whether a zero progress report is generated if the document has no persisted progress
   */
  @Test
  public void testGetProgressDefaultsToZeroIfNotPersisted() {
    String path = "documents/" + documentIdWithoutProgress + "/progress/";
    AnalysisProgress actualProgress = target(path).request().get(AnalysisProgress.class);

    testData.assertIsZeroProgress(actualProgress);
  }

  @Test
  public void testGetProgressOfInvalidDocumentReturns404() {
    String path = "documents/not_existing_document_id/progress/";
    Response actualResponse = target(path).request().get();
    assertEquals(404, actualResponse.getStatus());
  }

}
