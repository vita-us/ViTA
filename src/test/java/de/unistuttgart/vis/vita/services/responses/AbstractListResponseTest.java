package de.unistuttgart.vis.vita.services.responses;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Provides a method which checks whether the total count of a AbstractListResponse is working 
 * correctly.
 */
public class AbstractListResponseTest {
  
  private static final int TEST_TOTAL_COUNT = 42;
  
  protected AbstractListResponse response;

  /**
   * Checks whether total count can be set and got.
   */
  @Test
  public void testSetAndGetTotalCount() {
    response.setTotalCount(TEST_TOTAL_COUNT);
    assertEquals(42, response.getTotalCount());
  }

}
