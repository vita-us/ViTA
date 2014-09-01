package de.unistuttgart.vis.vita.services.responses;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DocumentRenameResponseTest extends DocumentIdResponseTest {

  private static final String TEST_ID = "eragonDocument";
  private static final String TEST_NAME = "Eragon";

  /**
   * Creates a DocumentRenameResponse.
   */
  @Before
  @Override
  public void setUp() {
    response = new DocumentRenameResponse();
  }

  /**
   * Tests creation of DocumentRenameResponse with name and id.
   */
  @Test
  public void testCreateResponseWithName() {
    DocumentRenameResponse renameResponse = new DocumentRenameResponse(TEST_ID, TEST_NAME);
    assertEquals(TEST_ID, renameResponse.getId());
    assertEquals(TEST_NAME, renameResponse.getName());
  }

  /**
   * Tests setter and getter of DocumentRenameResponse.
   */
  @Test
  public void testSetAndGetName() {
    DocumentRenameResponse renameResponse = new DocumentRenameResponse();
    renameResponse.setName(TEST_NAME);
    assertEquals(TEST_NAME, renameResponse.getName());
  }

}
