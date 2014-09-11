package de.unistuttgart.vis.vita.analysis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unistuttgart.vis.vita.analysis.modules.LuceneModuleTest;

/**
 * A suite containing all back-end tests
 */
@RunWith(Suite.class)
@SuiteClasses({AnalysisControllerTest.class, TestModuleRegistry.class, ModuleClassTest.class,
    AnalysisSchedulerTest.class, ModuleExecutionStateTest.class, AnalysisExecutorTest.class, LuceneModuleTest.class})
public class AnalysisTests {

  // conform checkstyle rule HideUtilityClassConstructor
  private AnalysisTests() {
  }
}
