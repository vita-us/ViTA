package de.unistuttgart.vis.vita;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unistuttgart.vis.vita.analysis.AnalysisInfrastructureTests;
import de.unistuttgart.vis.vita.analysis.modules.AnalysisModulesTests;
import de.unistuttgart.vis.vita.model.ModelTests;
import de.unistuttgart.vis.vita.persistence.PersistenceTests;
import de.unistuttgart.vis.vita.services.ServiceTests;
import de.unistuttgart.vis.vita.services.responses.ResponseTests;

/**
 * A suite containing all back-end tests
 */
@RunWith(Suite.class)
@SuiteClasses({ServiceTests.class, ModelTests.class, PersistenceTests.class, ResponseTests.class,
    AnalysisInfrastructureTests.class, AnalysisModulesTests.class})
public class AllTests {

  // conform checkstyle rule HideUtilityClassConstructor
  private AllTests() {
  }
}
