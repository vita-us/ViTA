package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;

/**
 * Performs test on ChapterService, whether test chapter can be caught using GET.
 */
public class ChapterServiceTest extends ServiceTest {
  
  private ChapterTestData testData;
  
  private String documentId;
  private String chapterId;

  /**
   * Creates and persists a test chapter to be caught in the test method.
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    // create test data
    Document testDoc = new DocumentTestData().createTestDocument(1);
    documentId = testDoc.getId();
    testData = new ChapterTestData();
    Chapter testChapter = testData.createTestChapter();
    chapterId = testChapter.getId();
    
    // persist test data
    EntityManager em = getModel().getEntityManager();
    
    em.getTransaction().begin();
    em.persist(testDoc);
    em.persist(testChapter);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(ChapterService.class);
  }

  /**
   * Tests whether a test chapter can be caught using GET.
   */
  @Test
  public void testGetChapter() {
    String path = "documents/" + documentId + "/chapters/" + chapterId;
    Chapter actualChapter = target(path).request().get(Chapter.class);
    
    // check received data
    testData.checkData(actualChapter);
  }

}
