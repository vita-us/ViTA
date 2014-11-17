package de.unistuttgart.vis.vita.services.document;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentPartTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.document.DocumentPartsService;
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
    
    EntityManager em = getModel().getEntityManager();
    
    // set up document parts test data
    Document testDoc = new DocumentTestData().createTestDocument(1);
    
    testData = new DocumentPartTestData();
    DocumentPart testPart1 = testData.createTestDocumentPart(1);
    Chapter testChapter = new ChapterTestData().createTestChapter();
    testPart1.getChapters().add(testChapter);
    
    DocumentPart testPart2 = testData.createTestDocumentPart(2);
    
    testDoc.getContent().getParts().add(testPart1);
    
    docId = testDoc.getId();
    
    // persist test data in database
    em.getTransaction().begin();
    em.persist(testChapter);
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
    
    // check response
    assertEquals(1, actualResponse.getTotalCount());
    
    // check parts
    List<DocumentPart> parts = actualResponse.getParts();
    assertEquals(1, parts.size());
    DocumentPart firstPart = parts.get(0);
    testData.checkData(firstPart, 1);
    
    // check chapters
    List<Chapter> chapters = firstPart.getChapters();
    assertEquals(1, chapters.size());
    
    new ChapterTestData().checkData(chapters.get(0));
  }

}
