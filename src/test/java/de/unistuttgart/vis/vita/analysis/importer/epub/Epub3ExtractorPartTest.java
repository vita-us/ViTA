package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.epub.Epub3Extractor;
import de.unistuttgart.vis.vita.importer.epub.EpubFileImporter;
import de.unistuttgart.vis.vita.model.document.Document;

public class Epub3ExtractorPartTest {

  private Document document;

  @Before
  public void setUp() throws URISyntaxException, InvalidPathException, SecurityException,
      IOException {
    Path testPath = Paths.get(getClass().getResource("moby-dick-mo-20120214.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    Epub3Extractor epub3Extractor = new Epub3Extractor(epubFileImporter.getEbook());
    document = epub3Extractor.getDocument();
  }

  @Test
  public void testChaptersSize() {
    
    assertEquals(137, document.getContent().getParts().get(0).getChapters().size());
  }

  @Test
  public void testOnePartChapters() {

    // First chapter: starting
    assertTrue(document.getContent().getParts().get(0).getChapters().get(0).getText()
        .startsWith("Call me Ishmael. Some years ago—never mind"));

    // First chapter: ending
    assertTrue(document.getContent().getParts().get(0).getChapters().get(0).getText()
        .endsWith("one grand hooded phantom, like a snow hill in the air."));

    // Second chapter: starting
    assertTrue(document.getContent().getParts().get(0).getChapters().get(1).getText()
        .startsWith("I stuffed a shirt or three into my old carpet-bag"));

    // Second chapter: ending
    assertTrue(document.getContent().getParts().get(0).getChapters().get(1).getText()
        .endsWith("may be."));

    // Last chapter: starting
    assertTrue(document.getContent().getParts().get(0).getChapters()
        .get(document.getContent().getParts().get(0).getChapters().size() - 1).getText()
        .startsWith("“AND I ONLY AM ESCAPED ALONE TO TELL THEE”"));

    // Last chapter: ending
    assertTrue(document.getContent().getParts().get(0).getChapters()
        .get(document.getContent().getParts().get(0).getChapters().size() - 1).getText()
        .endsWith(" only found another orphan."));
  }


}
