package de.unistuttgart.vis.vita.services.occurrence;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.unistuttgart.vis.vita.services.ServiceTest;

/**
 * Super class providing test methods to check illegal step and range values.
 */
public abstract class OccurrencesServiceTest extends ServiceTest {
  
  public static final int NEGATIVE_STEP_AMOUNT = -1;
  public static final int DEFAULT_STEP_AMOUNT = 200;
  public static final int TO_HIGH_STEP_AMOUNT = 2000;
  
  public static final double ILLEGAL_RANGE_START = -0.1;
  public static final double DEFAULT_RANGE_START = 0.0;
  public static final double LATE_START = 0.7;
  
  public static final double EARLY_END = 0.2;
  public static final double DEFAULT_RANGE_END = 1.0;
  public static final double ILLEGAL_RANGE_END = 1.1;

  /**
   * Checks whether the service returns a HTTP 500 response if the amount of steps is negative.
   */
  @Test
  public void testGetOccurrencesWithNegativeStepAmount() {
    Response actualResponse = prepareWebTarget(NEGATIVE_STEP_AMOUNT, 
                                                DEFAULT_RANGE_START, 
                                                DEFAULT_RANGE_END).request().get();
    assertEquals(500, actualResponse.getStatus());
  }

  /**
   * Checks whether the service returns a HTTP 500 response if the amount of steps is to high
   */
  @Test
  public void testGetOccurrencesWithTooHighStepAmount() {
    Response actualResponse = prepareWebTarget(TO_HIGH_STEP_AMOUNT, 
                                                DEFAULT_RANGE_START, 
                                                DEFAULT_RANGE_END).request().get();
    assertEquals(500, actualResponse.getStatus());
  }

  /**
   * Checks whether the service returns a HTTP 500 response if the start of a range is an illegal 
   * value.
   */
  @Test
  public void testGetOccurrencesWithIllegalRangeStart() {
    Response actualResponse = prepareWebTarget(DEFAULT_STEP_AMOUNT, 
                                                ILLEGAL_RANGE_START, 
                                                DEFAULT_RANGE_END).request().get();
    assertEquals(500, actualResponse.getStatus());
  }

  /**
   * Checks whether the service returns a HTTP 500 response if the end of a range is an illegal 
   * value.
   */
  @Test
  public void testGetOccurrencesWithIllegalRangeEnd() {
    Response actualResponse = prepareWebTarget(DEFAULT_STEP_AMOUNT, 
                                                DEFAULT_RANGE_START, 
                                                ILLEGAL_RANGE_END).request().get();
    assertEquals(500, actualResponse.getStatus());
  }

  /**
   * Checks whether the service returns a HTTP 500 response if the whole range is illegal (end 
   * lays before the start).
   */
  @Test
  public void testGetOccurrencesWithIllegalRange() {
    Response actualResponse = prepareWebTarget(DEFAULT_STEP_AMOUNT, 
                                                LATE_START, 
                                                EARLY_END).request().get();
    assertEquals(500, actualResponse.getStatus());
  }
  
  /**
   * @return the path of the service under test
   */
  protected abstract String getPath();
  
  /**
   * Returns a prepared WebTarget, if additional parameters should be added for each test, this 
   * method must be overridden!
   * 
   * @param steps - the amount of steps
   * @param rangeStart - the relative position of the range start
   * @param rangeEnd - the relative position of the range end 
   * @return the prepared WebTarget for the request
   */
  protected WebTarget prepareWebTarget(int steps, double rangeStart, double rangeEnd) {
    return target(getPath()).queryParam("steps", steps)
                            .queryParam("rangeStart", rangeStart)
                            .queryParam("rangeEnd", rangeEnd);
  }

}
