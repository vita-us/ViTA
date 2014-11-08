package de.unistuttgart.vis.vita.analysis.importer.epub;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



/**
 * Test suite which runs all tests related to importer epub classes.
 */
@RunWith(Suite.class)
@SuiteClasses({ChapterPositionMakerTest.class, ContentBuilderTest.class,
    EmptyLinesRemoverTest.class, Epub2ExtractorTest.class, Epub2IdsAndExtractorTest.class,
    Epub2TraitsExtractorTest.class, Epub3TraitsExtractorTest.class, EpubBookBuilderTest.class,
    EpubFileImporterTest.class, EpubImportModuleTest.class, EpubVersionDetectorTest.class,
    EpubVersionTest.class, PartsAndChaptersReviserTest.class, MetadataAnalyzerEpubTest.class})
public class EpubImportTests {

  // hidden constructor
  private EpubImportTests() {}
}
