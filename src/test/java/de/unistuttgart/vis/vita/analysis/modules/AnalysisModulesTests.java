package de.unistuttgart.vis.vita.analysis.modules;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unistuttgart.vis.vita.analysis.importer.ImportTests;

/**
 * Unit tests for analysis modules
 */
@RunWith(Suite.class)
@SuiteClasses({StanfordNLPModuleTest.class, ANNIEModuleTest.class, ImportTests.class,
    LuceneModuleTest.class})
public class AnalysisModulesTests {
  // conform checkstyle rule HideUtilityClassConstructor
  private AnalysisModulesTests() {
  }
}
