package de.unistuttgart.vis.vita.analysis.importer.epub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.modules.EpubImportModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.importer.epub.NoExtractorFoundException;

public class EpubImportModuleTest {

  // TODO: extend with epub2
  ImportResult epub2Result;
  ImportResult epub3Result;
  
  @Before
  public void setUp() throws URISyntaxException, FileNotFoundException, IOException, ParseException, NoExtractorFoundException {
    Path epub3TestPath = Paths.get(getClass().getResource("moby-dick-mo-20120214-parts.epub").toURI());
    EpubImportModule epub3ImportModule = new EpubImportModule(epub3TestPath);
    epub3Result = epub3ImportModule.execute(null, null);
    
    Path epub2TestPath = Paths.get(getClass().getResource("pg78.epub").toURI());
    EpubImportModule epub2ImportModule = new EpubImportModule(epub2TestPath);
    epub2Result = epub2ImportModule.execute(null, null);
  }
  @Test
  public void testEpub3Sizes() {

    // parts size
    assertEquals(3, epub3Result.getParts().size());

    // chapters size of first part
    assertEquals(1, epub3Result.getParts().get(0).getChapters().size());

    // chapters size of second part
    assertEquals(48, epub3Result.getParts().get(1).getChapters().size());

    // chapters size of third part
    assertEquals(87, epub3Result.getParts().get(2).getChapters().size());

  }

  @Test
  public void testEpub3Chapters() {

    // First Part first chapter: starting
    assertTrue(epub3Result.getParts().get(0).getChapters().get(0).getText()
        .startsWith("Call me Ishmael. Some years ago—never mind"));

    // First Part first chapter: ending
    assertTrue(epub3Result.getParts().get(0).getChapters().get(0).getText()
        .endsWith("one grand hooded phantom, like a snow hill in the air."));

    // Second Part third chapter: starting
    assertTrue(epub3Result.getParts().get(1).getChapters().get(2).getText()
        .startsWith("Upon waking next morning"));

    // Second Part third chapter: ending
    assertTrue(epub3Result.getParts().get(1).getChapters().get(2).getText()
        .endsWith("his harpoon like a marshal’s baton."));

    // Third Part eleventh chapter: starting
    assertTrue(epub3Result.getParts().get(2).getChapters().get(11).getText()
        .startsWith("With reference to the whaling"));

    // Third Part eleventh chapter: ending
    assertTrue(epub3Result.getParts().get(2).getChapters().get(11).getText()
        .endsWith("and not a harpoon, by your side."));
  }
}
