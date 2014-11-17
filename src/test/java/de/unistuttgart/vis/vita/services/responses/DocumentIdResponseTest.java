package de.unistuttgart.vis.vita.services.responses;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructors, getter and setter of DocumentIdResponse.
 */
public class DocumentIdResponseTest {
  
  private static final String TEST_ID = "testId";
  
  protected DocumentIdResponse response;
  
  /**
   * Creates a DocumentIdResponse
   */
  @Before
  public void setUp() {
    response = new DocumentIdResponse();
  }
  
  /**
   * Sets response to null
   */
  @After
  public void tearDown() {
    response = null;
  }
  
  /**
   * Tests creating a DocumentIdResponse without an id.
   */
  @Test
  public void testCreateResponse() {
    assertNotNull(response);
  }
  
  /**
   * Tests creating a DocumentIdResponse with an id.
   */
  @Test
  public void testCreateResponseWithId() {
    response = new DocumentIdResponse(TEST_ID);
    assertEquals(TEST_ID, response.getId());
  }

  /**
   * Tests set and get the id of a DocumentIdResponse.
   */
  @Test
  public void testSetAndGetId() {
    response.setId(TEST_ID);
    assertEquals(TEST_ID, response.getId());
  }

}
