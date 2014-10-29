package de.unistuttgart.vis.vita.services.document;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.document.DocumentsService;
import de.unistuttgart.vis.vita.services.responses.DocumentIdResponse;
import de.unistuttgart.vis.vita.services.responses.DocumentsResponse;

/**
 * Performs tests on DocumentsService.
 */
public class DocumentsServiceTest extends ServiceTest {
  
  private static final String TEST_FILE_PATH = "src/main/resources/test_document.txt";
  
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
  
  @Override
  protected void configureClient(ClientConfig config) {
    config.register(MultiPartFeature.class);
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
  
  /**
   * Tests whether a document can be added by uploading it.
   */
  @Test
  public void testAddDocument() {
    FileDataBodyPart dataPart = new FileDataBodyPart("file", new File(TEST_FILE_PATH));

    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
    formDataMultiPart.bodyPart(dataPart);
    
    Entity<FormDataMultiPart> multiPartEntity = Entity.entity(formDataMultiPart, 
                                                              formDataMultiPart.getMediaType());

    DocumentIdResponse actualResponse = target("documents").request()
                                          .post(multiPartEntity, DocumentIdResponse.class);

    assertNotNull(actualResponse);
  }
  
}
