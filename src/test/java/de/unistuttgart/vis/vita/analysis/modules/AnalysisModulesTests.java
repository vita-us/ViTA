package de.unistuttgart.vis.vita.analysis.modules;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unistuttgart.vis.vita.analysis.importer.ImportTests;
import de.unistuttgart.vis.vita.analysis.importer.epub.EpubImportTests;
import de.unistuttgart.vis.vita.analysis.modules.nlp.ANNIEModuleTest;

/**
 * Unit tests for analysis modules
 */
@RunWith(Suite.class)
@SuiteClasses({StanfordNLPModuleTest.class, ANNIEModuleTest.class, ImportTests.class,
    LuceneModuleTest.class, EntityRecognitionModuleRealTest.class, TextFeatureModuleTest.class,
    EntityFeatureModuleTest.class, MainAnalysisModuleTest.class, EntityRecognitionModuleTest.class,
    EpubImportTests.class, WordCloudEntityTaggingModuleTest.class})
public class AnalysisModulesTests {
  // conform checkstyle rule HideUtilityClassConstructor
  private AnalysisModulesTests() {
  }
}
