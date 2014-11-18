package de.unistuttgart.vis.vita.services.responses;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Document;

/**
 * Performs some tests methods on DocumentsResponse to check whether its total count, constructor,
 * getters and setters work correctly.
 */
public class DocumentsResponseTest extends AbstractListResponseTest {
  
  private List<Document> testDocList;

  /**
   * Sets up test data for test methods.
   */
  @Before
  public void setUp() {
    response = new DocumentsResponse();
    testDocList = new ArrayList<>();
    testDocList.add(new DocumentTestData().createTestDocument(1));
  }

  /**
   * Checks whether constructor works correctly.
   */
  @Test
  public void testDocumentsResponse() {
    DocumentsResponse testResponse = new DocumentsResponse(testDocList);
    checkDocumentsList(testResponse);
  }

  /**
   * Checks whether list getter and setter work correctly and total count is adjusted to list size.
   */
  @Test
  public void testSetAndGetDocuments() {
    DocumentsResponse testResponse = new DocumentsResponse();
    testResponse.setDocuments(testDocList);
    checkDocumentsList(testResponse);
  }

  private void checkDocumentsList(DocumentsResponse testResponse) {
    assertEquals(1, testResponse.getTotalCount());
    assertEquals(1, testResponse.getDocuments().size());
    new DocumentTestData().checkData(testResponse.getDocuments().get(0), 1);
  }

}
