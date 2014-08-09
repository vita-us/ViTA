package de.unistuttgart.vis.vita.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unistuttgart.vis.vita.model.progress.FeatureProgressTest;

/**
 * Test suite which runs all tests related to model classes.
 */
@RunWith(Suite.class)
@SuiteClasses({EntityRelationTest.class, TextPositionTest.class, TextSpanTest.class,
    FeatureProgressTest.class, PersistenceTest.class})
public class ModelTests {

  // hidden constructor
  private ModelTests() {}

}
