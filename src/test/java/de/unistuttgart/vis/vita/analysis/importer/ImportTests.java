package de.unistuttgart.vis.vita.analysis.importer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Test suite which runs all tests related to importer classes.
 */
@RunWith(Suite.class)
@SuiteClasses({MetadataAnalyzerTxtTest.class, TextSplitterTxtTest.class, FilterTxtTest.class,
    LineTxtTest.class, AdvancedBigHeadingChapterAnalyzerTxtTest.class,
    AutomatedChapterDetectionTxtTest.class, BigHeadingChapterAnalyzerTxtTest.class,
    ChapterBuilderTxtTest.class, ChapterPositionTxtTest.class, DocumentPartBuilderTxtTest.class,
    FullTextChapterAnalyzerTxtTest.class, MarkedHeadingChapterAnalyzerTxtTest.class,
    SimpleWhitelinesChapterAnalyzerTxtTest.class, SmallHeadingChapterAnalyzerTxtTest.class,
    TextFileImporterTxtTest.class, TextImportModuleTest.class})
public class ImportTests {

  // hidden constructor
  private ImportTests() {}
}
