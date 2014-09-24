package de.unistuttgart.vis.vita.analysis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests for the analysis module infrastructure
 */
@RunWith(Suite.class)
@SuiteClasses({AnalysisControllerTest.class, TestModuleRegistry.class, ModuleClassTest.class,
    AnalysisSchedulerTest.class, ModuleExecutionStateTest.class, AnalysisExecutorTest.class})
public class AnalysisInfrastructureTests {

  // conform checkstyle rule HideUtilityClassConstructor
  private AnalysisInfrastructureTests() {
  }
}
