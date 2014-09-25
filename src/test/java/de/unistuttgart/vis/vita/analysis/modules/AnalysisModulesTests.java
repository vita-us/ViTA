package de.unistuttgart.vis.vita.analysis.modules;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Unit tests for analysis modules
 */
@RunWith(Suite.class)
@SuiteClasses({StanfordNLPModuleTest.class, ANNIEModuleTest.class})
public class AnalysisModulesTests {
  // conform checkstyle rule HideUtilityClassConstructor
  private AnalysisModulesTests() {
  }
}
