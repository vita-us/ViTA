package de.unistuttgart.vis.vita;

import de.unistuttgart.vis.vita.services.ServiceTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * A suite containing all back-end tests
 */
@RunWith(Suite.class)
@SuiteClasses({ServiceTests.class})
public class AllTests {

  // conform checkstyle rule HideUtilityClassConstructor
  private AllTests() {
  }
}
