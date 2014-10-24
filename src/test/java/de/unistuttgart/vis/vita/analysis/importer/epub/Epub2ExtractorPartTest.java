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

import de.unistuttgart.vis.vita.importer.epub.Epub2Extractor;
import de.unistuttgart.vis.vita.importer.epub.EpubFileImporter;
import de.unistuttgart.vis.vita.model.document.Document;

public class Epub2ExtractorPartTest {

  private Document document;

  @Before
  public void setUp() throws URISyntaxException, InvalidPathException, SecurityException,
      IOException {
    Path testPath = Paths.get(getClass().getResource("pg78.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    Epub2Extractor epub3Extractor =
        new Epub2Extractor(epubFileImporter.getEbook().getNcxResource(), epubFileImporter
            .getEbook().getContents());
    document = epub3Extractor.getDocument();
  }

  @Test
  public void testChaptersSize() {

    assertEquals(29, document.getContent().getParts().get(0).getChapters().size());
  }

  @Test
  public void testOnePartChapters() {

    // First chapter: starting
    assertTrue(document.getContent().getParts().get(0).getChapters().get(0).getText()
        .startsWith("I had this story from one who had no business to tell it to me"));

    // First chapter: ending
    assertTrue(document.getContent().getParts().get(0).getChapters().get(0).getText()
        .endsWith("tight and wait for whatever may come.\""));

    // Second chapter: starting
    assertTrue(document.getContent().getParts().get(0).getChapters().get(1).getText()
        .startsWith("Nor did they have long to wait,"));

    // Second chapter: ending
    assertTrue(document.getContent().getParts().get(0).getChapters().get(1).getText()
        .endsWith("screams, or the stealthy moving of great bodies beneath them."));

    // Fifteenth chapter: starting
    assertTrue(document.getContent().getParts().get(0).getChapters().get(16).getText()
        .startsWith("Several miles south of the cabin, upon"));

    // Fifteenth chapter: ending
    assertTrue(document.getContent().getParts().get(0).getChapters().get(16).getText()
        .endsWith("dragged them through the jungle as though they had been cows.\""));

    // Twenty-Second chapter: starting
    assertTrue(document.getContent().getParts().get(0).getChapters().get(23).getText()
        .startsWith("When D'Arnot regained consciousness,"));

    // Twenty-Second chapter: ending
    assertTrue(document.getContent().getParts().get(0).getChapters().get(23).getText()
        .endsWith("the crack of the door—and then he pulled the trigger."));

    // Last chapter: starting
    assertTrue(document.getContent().getParts().get(0).getChapters()
        .get(document.getContent().getParts().get(0).getChapters().size() - 1).getText()
        .startsWith("At the sight of Jane, cries of relief"));

    // Last chapter: ending
    assertTrue(document.getContent().getParts().get(0).getChapters()
        .get(document.getContent().getParts().get(0).getChapters().size() - 1).getText()
        .endsWith("I never knew who my father was.\""));
  }

}
