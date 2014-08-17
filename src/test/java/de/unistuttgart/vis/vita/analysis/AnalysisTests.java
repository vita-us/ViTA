package de.unistuttgart.vis.vita.analysis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * A suite containing all back-end tests
 */
@RunWith(Suite.class)
@SuiteClasses({AnalysisControllerTest.class, TestModuleRegistry.class, ModuleClassTest.class})
public class AnalysisTests {

  // conform checkstyle rule HideUtilityClassConstructor
  private AnalysisTests() {
  }
}
