package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.epub.ContentBuilder;
import de.unistuttgart.vis.vita.importer.epub.EpubFileImporter;

public class ContentBuilderTest {

  private ContentBuilder contentBuilder = new ContentBuilder();
  private String contentBuilderResult;

  @Before
  public void setUp() throws URISyntaxException, IOException {
    Path testPath = Paths.get(getClass().getResource("moby-dick-mo-20120214.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    contentBuilderResult =
        contentBuilder.getStringFromInputStream(epubFileImporter.getEbook().getContents().get(6)
            .getInputStream());
  }

  @Test
  public void testStringContent(){
    assertTrue(contentBuilderResult.contains("<h1 id=\"c01h01\">Chapter 1. Loomings.</h1>"));
    assertTrue(contentBuilderResult.endsWith("and see what sort of a place this “Spouter\" may be.</p></section></body></html>"));  
  }
}
