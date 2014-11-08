package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.unistuttgart.vis.vita.importer.epub.ContentBuilder;
import de.unistuttgart.vis.vita.importer.epub.Epub2IdsAndTitlesExtractor;
import de.unistuttgart.vis.vita.importer.epub.EpubFileImporter;
import de.unistuttgart.vis.vita.importer.epub.Epubline;
import de.unistuttgart.vis.vita.importer.epub.Epub2TraitsExtractor;

/**
 * JUnit test on Epub2TraitsExtractor
 * 
 *
 */
public class Epub2TraitsExtractorTest {

  private Epub2TraitsExtractor epublineTraitsExtractor;
  private List<Epubline> epublines = new ArrayList<Epubline>();
  private ContentBuilder contentBuilder = new ContentBuilder();
  private List<Epubline> chapter = new ArrayList<Epubline>();

  @Before
  public void setUp() throws URISyntaxException, IOException {

    Path testPath = Paths.get(getClass().getResource("pg244.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    epublineTraitsExtractor = new Epub2TraitsExtractor(epubFileImporter.getEbook());
    Epub2IdsAndTitlesExtractor extractor =
        new Epub2IdsAndTitlesExtractor(epubFileImporter.getEbook());
    Resource resource = epubFileImporter.getEbook().getContents().get(3);
    Document document =
        Jsoup.parse(contentBuilder.getStringFromInputStream(resource.getInputStream()));

    Element currentElement = document.getElementById("pgepubid00015");
    chapter = epublineTraitsExtractor.extractChapterEpublines(currentElement, document, resource,
        extractor.getTocIds());
    fillEpublines();
  }

  private void fillEpublines() {
    List<Epubline> lines = new ArrayList<Epubline>();

    lines.add(new Epubline("Heading", "CHAPTER 1.", ""));
    lines.add(new Epubline("Textstart", "Begin text", ""));
    lines.add(new Epubline("Text", "Text a", ""));
    lines.add(new Epubline("Text", "Text b", ""));
    lines.add(new Epubline("Textend", "End text", ""));

    // add the correct Epubline object regarding "Heading", "TextStart" and "TextEnd"
    epublines.add(epublineTraitsExtractor.getHeading(lines));
    epublines.add(epublineTraitsExtractor.getTextStart(lines));
    epublines.add(epublineTraitsExtractor.getTextEnd(lines));
  }

  @Test
  public void testCorrectContentOfLines() {

    assertEquals("Heading", epublines.get(0).getMode());
    assertEquals("CHAPTER 1.", epublines.get(0).getEpubline());

    assertEquals("Textstart", epublines.get(1).getMode());
    assertEquals("Begin text", epublines.get(1).getEpubline());

    assertEquals("Textend", epublines.get(2).getMode());
    assertEquals("End text", epublines.get(2).getEpubline());

  }
  
  @Test
  public void testChapterContent(){
    
    //first line
    assertTrue(chapter.get(0).getEpubline().startsWith("ALL night their course"));
    assertTrue(chapter.get(0).getEpubline().endsWith("startled the weary horses into a gallop."));

    //last line
    assertTrue(chapter.get(chapter.size()-1).getEpubline().startsWith("Again the avenger had been foiled,"));
    assertTrue(chapter.get(chapter.size()-1).getEpubline().endsWith("to which we are already under such obligations."));
  }
}
