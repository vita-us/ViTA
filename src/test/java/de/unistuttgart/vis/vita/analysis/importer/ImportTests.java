package de.unistuttgart.vis.vita.analysis.importer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Test suite which runs all tests related to importer classes.
 */
@RunWith(Suite.class)
@SuiteClasses({MetadataAnalyzerTxtTest.class, TextSplitterTxtTest.class, FilterTxtTest.class})
public class ImportTests {

  // hidden constructor
  private ImportTests() {}
}
