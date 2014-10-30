package de.unistuttgart.vis.vita.analysis.importer.epub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.epub.Epub3Extractor;
import de.unistuttgart.vis.vita.importer.epub.EpubFileImporter;
import de.unistuttgart.vis.vita.model.document.Document;

public class Epub3ExtractorPartsTest {

  private Document document;

  @Before
  public void setUp() throws URISyntaxException, InvalidPathException, SecurityException,
      IOException {
    Path testPath = Paths.get(getClass().getResource("moby-dick-mo-20120214-parts.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    Epub3Extractor epub3Extractor = new Epub3Extractor(epubFileImporter.getEbook());
    //TODO:
    System.out.println(epubFileImporter.getEbook().getContents().size());
    System.out.println(epubFileImporter.getEbook().getTableOfContents().size());
    document = epub3Extractor.getDocument();
  }

  @Test
  public void testSizes() {

    // parts size
    assertEquals(3, document.getContent().getParts().size());

    // chapters size of first part
    assertEquals(1, document.getContent().getParts().get(0).getChapters().size());

    // chapters size of second part
    assertEquals(48, document.getContent().getParts().get(1).getChapters().size());

    // chapters size of third part
    assertEquals(87, document.getContent().getParts().get(2).getChapters().size());

  }

  @Test
  public void testPartsChapters() {

    // First Part first chapter: starting
    assertTrue(document.getContent().getParts().get(0).getChapters().get(0).getText()
        .startsWith("Call me Ishmael. Some years ago—never mind"));

    // First Part first chapter: ending
    assertTrue(document.getContent().getParts().get(0).getChapters().get(0).getText()
        .endsWith("one grand hooded phantom, like a snow hill in the air."));

    // Second Part third chapter: starting
    assertTrue(document.getContent().getParts().get(1).getChapters().get(2).getText()
        .startsWith("Upon waking next morning"));

    // Second Part third chapter: ending
    assertTrue(document.getContent().getParts().get(1).getChapters().get(2).getText()
        .endsWith("his harpoon like a marshal’s baton."));

    // Third Part eleventh chapter: starting
    assertTrue(document.getContent().getParts().get(2).getChapters().get(10).getText()
        .startsWith("With reference to the whaling"));

    // Third Part eleventh chapter: ending
    assertTrue(document.getContent().getParts().get(2).getChapters().get(10).getText()
        .endsWith("and not a harpoon, by your side."));
  }
}
