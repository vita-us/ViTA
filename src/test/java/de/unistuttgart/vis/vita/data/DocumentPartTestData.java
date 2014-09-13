package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Holds test data for document parts and methods to create test parts and check whether given data
 * matches the test data.
 */
public class DocumentPartTestData {
  
  public static final int TEST_DOCUMENT_PART_NUMBER = 1;
  public static final String TEST_DOCUMENT_PART_TITLE = "Book 1";
  
  /**
   * Creates a test document part including a test number and title.
   * 
   * @return test document part
   */
  public DocumentPart createTestDocumentPart() {
    DocumentPart part = new DocumentPart();
    part.setNumber(TEST_DOCUMENT_PART_NUMBER);
    part.setTitle(TEST_DOCUMENT_PART_TITLE);
    return part;
  }

  /**
   * Checks whether given data is not <code>null</code> and includes the test data.
   * 
   * @param readPart - the document which should be checked
   */
  public void checkData(DocumentPart readPart) {
    assertNotNull(readPart);
    assertEquals(TEST_DOCUMENT_PART_NUMBER, readPart.getNumber());
    assertEquals(TEST_DOCUMENT_PART_TITLE, readPart.getTitle());
  }

}
