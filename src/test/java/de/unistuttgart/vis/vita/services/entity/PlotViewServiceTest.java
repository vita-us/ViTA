package de.unistuttgart.vis.vita.services.entity;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentPartTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.services.AnalysisAware;
import de.unistuttgart.vis.vita.services.ServiceTest;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Performs some simple tests on the PlotViewService.
 */
public class PlotViewServiceTest extends ServiceTest implements AnalysisAware {

  protected String path;

  @Override
  public void setUp() throws Exception {
    super.setUp();

    EntityManager em = getModel().getEntityManager();

    Document testDoc  = new DocumentTestData().createTestDocument(1);
    testDoc.getProgress().setStatus(getCurrentAnalysisStatus());

    DocumentPart testPart  = new DocumentPartTestData().createTestDocumentPart(1);
    testDoc.getContent().getParts().add(testPart);

    Chapter testChapter = new ChapterTestData().createTestChapter();
    testPart.getChapters().add(testChapter);

    path = "documents/" + testDoc.getId() + "/plotview";

    // persist the test document
    em.getTransaction().begin();
    em.persist(testChapter);
    em.persist(testPart);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(PlotViewService.class);
  }

  @Override
  public AnalysisStatus getCurrentAnalysisStatus() {
    return AnalysisStatus.FINISHED;
  }

  @Test
  public void getPlotView() {
    Response actualResponse = target(path).request().get();
    assertEquals(200, actualResponse.getStatus());
  }


}
