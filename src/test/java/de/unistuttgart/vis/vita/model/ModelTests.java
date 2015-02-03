package de.unistuttgart.vis.vita.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unistuttgart.vis.vita.model.document.ChapterTest;
import de.unistuttgart.vis.vita.model.document.DocumentTest;
import de.unistuttgart.vis.vita.model.document.OccurrenceTest;
import de.unistuttgart.vis.vita.model.document.SentenceTest;
import de.unistuttgart.vis.vita.model.progress.FeatureProgressTest;

/**
 * Test suite which runs all tests related to model classes.
 */
@RunWith(Suite.class)
@SuiteClasses({EntityRelationTest.class, TextPositionTest.class, RangeTest.class,
    FeatureProgressTest.class, TextRepositoryTest.class, DocumentTest.class, SentenceTest.class,
    OccurrenceTest.class, ChapterTest.class})
public class ModelTests {

  // hidden constructor
  private ModelTests() {}

}
