package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Holds test data for document parts and methods to create test parts and check whether given data
 * matches the test data.
 */
public class DocumentPartTestData {
  
  private static final int DEFAULT_PART_NUMBER = 1;
  
  public static final int TEST_DOCUMENT_PART_1_NUMBER = 1;
  public static final String TEST_DOCUMENT_PART_1_TITLE = "Book 1";
  
  public static final int TEST_DOCUMENT_PART_2_NUMBER = 2;
  public static final String TEST_DOCUMENT_PART_2_TITLE = "Book 2";
  
  /**
   * Creates the default test DocumentPart and returns it.
   * 
   * @return default test DocumentPart
   */
  public DocumentPart createTestDocumentPart() {
    return createTestDocumentPart(DEFAULT_PART_NUMBER);
  }
  
  /**
   * Creates a test document part including a test number and title.
   * 
   * @param number - the number of the test DocumentPart to be created
   * @return test document part
   */
  public DocumentPart createTestDocumentPart(int number) {
    DocumentPart part = new DocumentPart();
    
    if (number == 1) {
      part.setNumber(TEST_DOCUMENT_PART_1_NUMBER);
      part.setTitle(TEST_DOCUMENT_PART_1_TITLE);
    } else if (number == 2) {
      part.setNumber(TEST_DOCUMENT_PART_2_NUMBER);
      part.setTitle(TEST_DOCUMENT_PART_2_TITLE);
    } else {
      throw new IllegalArgumentException("Unknown test documentpart number!");
    }

    return part;
  }
  
  /**
   * Checks whether the given DocumentPart is the default test DocumentPart.
   * 
   * @param partToCheck - the DocumentPart to be checked
   */
  public void checkData(DocumentPart partToCheck) {
    checkData(partToCheck, DEFAULT_PART_NUMBER);
  }

  /**
   * Checks whether given data is not <code>null</code> and includes the test data.
   * 
   * @param readPart - the document which should be checked
   * @param which test DocumentPart the given part should match
   */
  public void checkData(DocumentPart readPart, int number) {
    assertNotNull(readPart);
    
    if (number == 1) {
      assertEquals(TEST_DOCUMENT_PART_1_NUMBER, readPart.getNumber());
      assertEquals(TEST_DOCUMENT_PART_1_TITLE, readPart.getTitle());
    } else if (number == 2) {
      assertEquals(TEST_DOCUMENT_PART_2_NUMBER, readPart.getNumber());
      assertEquals(TEST_DOCUMENT_PART_2_TITLE, readPart.getTitle());
    }
    
  }

}
