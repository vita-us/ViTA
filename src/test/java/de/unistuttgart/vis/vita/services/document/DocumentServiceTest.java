package de.unistuttgart.vis.vita.services.document;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.requests.DocumentRenameRequest;
import de.unistuttgart.vis.vita.services.responses.DocumentIdResponse;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Performs some tests on the DocumentService to check whether GET, PUT and DELETE are working 
 * correctly.
 */
public class DocumentServiceTest extends ServiceTest {
  
  private String documentId;
  private String renameId;
  private DocumentTestData testData;
  private String deletionId;
  private String notExistingId;
  private Document testDoc2;
  private Document testDoc3;
  private Path filePath;
  
  /**
   * Writes some test documents into the unit test database.
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = getModel().getEntityManager();

    Path testPath = Paths.get(System.getProperty("user.home")).resolve(RELATIVE_DIRECTORY_TEST);
    File testFile = new File(testPath.toFile(), "testing.file");
    filePath = testFile.toPath();

    testData = new DocumentTestData();
    Document testDoc1 = testData.createTestDocument(1);
    testDoc1.setFileName("file.txt");
    testDoc1.setFilePath(
        Paths.get(DocumentServiceTest.class.getResource("test_document.txt").toURI()));
    documentId = testDoc1.getId();

    testDoc2 = testData.createTestDocument(2);
    renameId = testDoc2.getId();
    testDoc2.setFilePath(filePath);
    testDoc2.setFileName("testing.file");

    testDoc3 = testData.createTestDocument(2);
    deletionId = testDoc3.getId();
    testDoc3.setFilePath(filePath);
    testDoc3.setFileName("testing.file");
    
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
  public void testRenameDocument() {
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
   * Tests whether renaming a not existing document causes an HTTP-404
   */
  @Test
  public void testRenameNotExistingDocument() {
    notExistingId = "notExistingID";
    DocumentRenameRequest req = new DocumentRenameRequest(notExistingId, "Renamed");
    Entity<DocumentRenameRequest> reqEntity = Entity.entity(req, MediaType.APPLICATION_JSON_TYPE);
    Response actualResponse = target("document/" + notExistingId).request().put(reqEntity);
    
    // should whether HTTP status indicates "Not Found"
    assertEquals(404, actualResponse.getStatus());
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
    assertEquals(404, response.getStatus());
  }

  /**
   * Tests whether a document can be deleted using REST.
   */
  @Test
  public void testDeleteDocumentFromHDD() throws IOException {
    File testFile = filePath.toFile();
    FileUtils.touch(testFile);

    target("documents/" + testDoc2.getId()).request().delete();
    assertThat(testFile.exists(), is(true));

    target("documents/" + testDoc3.getId()).request().delete();
    assertThat(testFile.exists(), is(false));
  }
  
  /**
   * Tests whether a HTTP-400 is returned when trying to delete a not existing document
   */
  @Test
  public void testDeleteNotExistingDocument() {
    // document does not exist
    assertFalse(canBeCaught(notExistingId));
    
    // try to delete it anyway
    Response actualResponse = target("documents/" + notExistingId).request().delete();
    assertEquals(404, actualResponse.getStatus());
    
    // document can still not be caught
    assertFalse(canBeCaught(notExistingId));
  }

  /**
   * Checks if the correct current parameters are returned.
   */
  @Test
  public void testGetParameters() {
    // set up request
    String path = "documents/" + documentId + "/parameters";

    // send request and get response
    AnalysisParameters actualResponse = target(path).request().get(AnalysisParameters.class);

    // check response for validity
    assertNotNull(actualResponse);
    assertThat(actualResponse.getRelationTimeStepCount(), is(20));
    assertThat(actualResponse.getWordCloudItemsCount(), is(100));
    assertThat(actualResponse.isStopWordListEnabled(), is(true));
  }

  /**
   * Restarts a document analysis with different parameters
   */
  @Test
  public void testDerive() {
    // try to delete it anyway
    AnalysisParameters newParameters = new AnalysisParameters();
    newParameters.setWordCloudItemsCount(42);
    Entity<AnalysisParameters> reqEntity =
        Entity.entity(newParameters, MediaType.APPLICATION_JSON_TYPE);
    DocumentIdResponse response = target("documents/" + documentId + "/derive").request()
        .post(reqEntity, DocumentIdResponse.class);

    AnalysisParameters retrievedParameters = target("documents/" + response.getId() + "/parameters")
        .request().get(AnalysisParameters.class);
    assertThat(retrievedParameters.getWordCloudItemsCount(), is(42));
  }
  
  /**
   * Returns whether a document with the given id can be caught using REST.
   * 
   * @param idToCheck - the id of the document to be caught
   * @return true if HTTP status is not 404, false otherwise
   */
  private boolean canBeCaught(String idToCheck) {
    java.util.logging.Logger.getLogger("test").info("test");
    Response response = target("documents/" + idToCheck).request().get();
    return (response.getStatus() != 404);
  }

}
