package de.unistuttgart.vis.vita.model.progress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Performs some simple tests on FeatureProgress class, checking legal and illegal progress values.
 */
public class FeatureProgressTest {

  // constants
  private static final double DELTA = 0.001;

  // test data
  private static final double TOO_LOW_PROGRESS = -6.3;
  private static final double MINIMUM_PROGRESS = 0.0;
  private static final double TEST_PROGRESS = 0.25;
  private static final double MAXIMUM_PROGRESS = 1.0;
  private static final double TOO_HIGH_PROGRESS = 4.5;

  // attributes
  private FeatureProgress testProgress;

  /**
   * Instantiate a test progress.
   */
  @Before
  public void setUp() {
    testProgress = new FeatureProgress();
  }

  /**
   * Check whether an IllegalArgumentException is thrown creating a FeatureProgress with a too low
   * progress value.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateTooLowProgress() {
    new FeatureProgress(TOO_LOW_PROGRESS, false);
  }

  /**
   * Check whether an IllegalArgumentException is thrown creating a FeatureProgress with a too high
   * progress value.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateTooHighProgress() {
    new FeatureProgress(TOO_HIGH_PROGRESS, false);
  }

  /**
   * Check whether a FeatureProgress with minimum value can be created.
   */
  @Test
  public void testCreateMinimumProgress() {
    FeatureProgress prog = new FeatureProgress(MINIMUM_PROGRESS, false);
    assertEquals(MINIMUM_PROGRESS, prog.getProgress(), DELTA);
  }

  /**
   * Check whether a FeatureProgress with a valid value can be created.
   */
  @Test
  public void testCreateValidProgress() {
    FeatureProgress prog = new FeatureProgress(TEST_PROGRESS, false);
    assertEquals(TEST_PROGRESS, prog.getProgress(), DELTA);
  }

  /**
   * Check whether a FeatureProgress with maximum value can be created.
   */
  @Test
  public void testCreateMaximumProgress() {
    FeatureProgress prog = new FeatureProgress(MAXIMUM_PROGRESS, true);
    assertEquals(MAXIMUM_PROGRESS, prog.getProgress(), DELTA);
  }

  /**
   * Check whether an IllegalArgumentException is thrown setting the progress to a too low value.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetTooLowProgress() {
    testProgress.setProgress(TOO_LOW_PROGRESS);
  }

  /**
   * Check whether an IllegalArgumentException is thrown setting the progress to a too high value.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetTooHighProgress() {
    testProgress.setProgress(TOO_HIGH_PROGRESS);
  }

  /**
   * Check whether a FeatureProgress can be set to a minimum progress value.
   */
  @Test
  public void testSetMinimumProgress() {
    testProgress.setProgress(MINIMUM_PROGRESS);
    assertEquals(MINIMUM_PROGRESS, testProgress.getProgress(), DELTA);
  }

  /**
   * Check whether a FeatureProgress can be set to a valid progress value.
   */
  @Test
  public void testSetValidProgress() {
    testProgress.setProgress(TEST_PROGRESS);
    assertEquals(TEST_PROGRESS, testProgress.getProgress(), DELTA);
  }

  /**
   * Check whether a FeatureProgress can be set to a maximum progress value.
   */
  @Test
  public void testSetMaximumProgress() {
    testProgress.setProgress(MAXIMUM_PROGRESS);
    assertEquals(MAXIMUM_PROGRESS, testProgress.getProgress(), DELTA);
  }

  /**
   * Check whether the ready flag of a FeatureProgress can be set.
   */
  @Test
  public void testSetAndGetReadyFlag() {
    testProgress.setReady(true);
    assertTrue(testProgress.isReady());
  }

}
