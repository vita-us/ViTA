package de.unistuttgart.vis.vita.services;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.requests.DocumentRenameRequest;

/**
 * Performs some tests on the DocumentService to check whether GET, PUT and DELETE are working 
 * correctly.
 */
public class DocumentServiceTest extends ServiceTest {
  
  private String documentId;
  private String renameId;
  private DocumentTestData testData;
  private String deletionId;
  
  /**
   * Writes some test documents into the unit test database.
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    
    testData = new DocumentTestData();
    Document testDoc1 = testData.createTestDocument(1);
    documentId = testDoc1.getId();
    
    Document testDoc2 = testData.createTestDocument(2);
    renameId = testDoc2.getId();
    
    Document testDoc3 = testData.createTestDocument(2);
    deletionId = testDoc3.getId();
    
    em.getTransaction().begin();
    em.persist(testDoc1);
    em.persist(testDoc2);
    em.persist(testDoc3);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(DocumentService.class);
  }

  /**
   * Tests whether a document can be got using REST.
   */
  @Test
  public void testGetDocument() {
    Document responseDocument = target("documents/" + documentId).request().get(Document.class);
    
    // check data of the document
    assertNotNull(responseDocument);
    testData.checkData(responseDocument, 1);
  }
  
  /**
   * Tests whether a document can be renamed using REST.
   */
  @Test
  public void testPutDocument() {
    DocumentRenameRequest req = new DocumentRenameRequest(renameId, DocumentTestData.TEST_DOCUMENT_TITLE_1);
    Entity<DocumentRenameRequest> reqEntity = Entity.entity(req, MediaType.APPLICATION_JSON_TYPE);
    Response actualResponse = target("documents/" + renameId).request().put(reqEntity);
    
    // check whether HTTP status indicates "No Content"
    assertEquals(204, actualResponse.getStatus());
    
    // check if title has really changed
    Document renamedDocument = target("documents/" + renameId).request().get(Document.class);
    assertNotNull(renamedDocument);
    testData.checkData(renamedDocument, 1);
  }
  
  /**
   * Tests whether a document can be deleted using REST.
   */
  @Test
  public void testDeleteDocument() {
    Response actualResponse = target("documents/" + deletionId).request().delete();
    
    assertEquals(204, actualResponse.getStatus());
    
    // can not be caught anymore
    Response response = target("documents/" + deletionId).request().get();
    assertEquals(500, response.getStatus());
  }

}
