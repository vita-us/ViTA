package de.unistuttgart.vis.vita.analysis.importer.epub;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.unistuttgart.vis.vita.importer.epub.Epub2Extractor;
import de.unistuttgart.vis.vita.importer.epub.Epub3Extractor;
import de.unistuttgart.vis.vita.importer.epub.EpubFileImporter;
import de.unistuttgart.vis.vita.importer.epub.EpubVersion;
import de.unistuttgart.vis.vita.importer.epub.NoExtractorFoundException;

public class EpubVersionTest {

  @Test(expected = NoExtractorFoundException.class )
  public void testUnknown() throws IOException, NoExtractorFoundException{
    EpubVersion.getExtractorForVersion(EpubVersion.UNKNOWN,null);
  }
  
  @Test
  public void testStandard2() throws IOException, NoExtractorFoundException, URISyntaxException{
    Path testPath = Paths.get(getClass().getResource("moby-dick-mo-20120214.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    assertEquals(Epub2Extractor.class, EpubVersion.getExtractorForVersion(EpubVersion.STANDARD2,epubFileImporter.getEbook()).getClass());
  }
  
  @Test
  public void testStandard3() throws IOException, NoExtractorFoundException, URISyntaxException{
    Path testPath = Paths.get(getClass().getResource("moby-dick-mo-20120214.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    assertEquals(Epub3Extractor.class, EpubVersion.getExtractorForVersion(EpubVersion.STANDARD3,epubFileImporter.getEbook()).getClass());
  }
  
}
