package de.unistuttgart.vis.vita.services;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({VersionServiceTest.class})
public class ServiceTests {
  @BeforeClass
  public static void setUp() {
    // Choose an available port. This allows multiple test executions at the same time
    System.setProperty("jersey.config.test.container.port", "0");
  }
}
