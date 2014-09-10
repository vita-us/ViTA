package de.unistuttgart.vis.vita.analysis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unistuttgart.vis.vita.analysis.importer.ImportTests;
import de.unistuttgart.vis.vita.analysis.importer.TextImportModuleTest;

@RunWith(Suite.class)
@SuiteClasses({AnalysisControllerTest.class, TestModuleRegistry.class, ModuleClassTest.class,
    AnalysisSchedulerTest.class, ModuleExecutionStateTest.class, AnalysisExecutorTest.class,
    TextImportModuleTest.class, ImportTests.class})
public class AnalysisTests {
  // conform checkstyle rule HideUtilityClassConstructor
  private AnalysisTests() {}
}
