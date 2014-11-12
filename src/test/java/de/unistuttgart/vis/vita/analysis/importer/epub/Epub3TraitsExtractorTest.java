package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import de.unistuttgart.vis.vita.importer.epub.Epub3TraitsExtractor;
import de.unistuttgart.vis.vita.importer.epub.EpubFileImporter;
import de.unistuttgart.vis.vita.importer.epub.Epubline;

/**
 * JUnit test on Epub3TraitsExtractor
 * 
 *
 */
public class Epub3TraitsExtractorTest {

  private EpubFileImporter epubFileImporter;
  private Epub3TraitsExtractor epub3TraitsExtractor = new Epub3TraitsExtractor();
  private List<List<Epubline>> chaptersList = new ArrayList<List<Epubline>>();
  private List<List<List<Epubline>>> parts = new ArrayList<List<List<Epubline>>>();

  @Before
  public void setUp() throws URISyntaxException, IOException {
    Path testPath = Paths.get(getClass().getResource("moby-dick-mo-20120214-parts.epub").toURI());
    epubFileImporter = new EpubFileImporter(testPath);
    chaptersList = epub3TraitsExtractor.extractChapters(epubFileImporter.getEbook().getContents());
    parts = epub3TraitsExtractor.extractParts(epubFileImporter.getEbook().getContents());
  }

  @Test
  public void testExistenceOfPart() throws IOException {
    assertTrue(epub3TraitsExtractor.existsPartInEpub3(epubFileImporter.getEbook().getContents()));
  }

  @Test
  public void testChaptersSize() {
    assertEquals(136, chaptersList.size());
  }

  @Test
  public void testChaptersContent() {

    // first chapter: heading
    assertTrue(chaptersList.get(0).get(0).getEpubline().startsWith("Chapter 1. Loomings."));

    // first chapter: ending
    assertTrue(chaptersList.get(0).get(chaptersList.get(0).size() - 1).getEpubline().
        endsWith("one grand hooded phantom, like a snow hill in the air."));

    // eleventh chapter: heading
    assertTrue(chaptersList.get(10).get(1).getEpubline().startsWith("Chapter 11. Nightgown."));

    // eleventh chapter: ending
    assertTrue(chaptersList.get(10).get(chaptersList.get(10).size() - 1)
        .getEpubline().endsWith("may prove in the mere skeleton I give."));

    // last chapter: heading
    assertTrue(chaptersList.get(chaptersList.size() - 1).get(1).getEpubline().startsWith("Epilogue"));

    // last chapter: ending
    assertTrue(chaptersList.get(chaptersList.size() - 1)
        .get(chaptersList.get(chaptersList.size() - 1).size() - 1)
        .getEpubline().endsWith("only found another orphan."));
  }

  @Test
  public void testSizes() {

    // parts size
    assertEquals(3, parts.size());

    // part one chapters size
    assertEquals(1, parts.get(0).size());

    // part two chapters size
    assertEquals(48, parts.get(1).size());

    // part three chapters size
    assertEquals(87, parts.get(2).size());
  }

  @Test
  public void testPartsContent() {
    // part one

    // first/last chapter: heading
    assertTrue(parts.get(0).get(0).get(0).getEpubline().matches("Chapter 1. Loomings."));

    // first/last chapter: ending
    assertTrue(parts.get(0).get(0).get(chaptersList.get(0).size() - 1)
        .getEpubline().endsWith("one grand hooded phantom, like a snow hill in the air."));

    // part two

    // first chapter: heading
    assertTrue(parts.get(1).get(0).get(0).getEpubline().startsWith("Chapter 2. The Carpet-Bag."));

    // first chapter: ending
    assertTrue(parts.get(1).get(0).get(parts.get(1).get(0).size() - 1)
        .getEpubline().endsWith("sort of a place this “Spouter\" may be."));

    // last chapter: heading
    assertTrue(parts.get(1).get(parts.get(1).size() - 1).get(1).getEpubline().matches("Chapter 49. The Hyena."));

    // last chapter: ending
    assertTrue(parts.get(1).get(parts.get(1).size() - 1)
        .get(parts.get(1).get(parts.get(1).size() - 1).size() - 1)
        .getEpubline().endsWith("and the devil fetch the hindmost."));
    
    // part three
    
    // first chapter: heading
    assertTrue(parts.get(2).get(0).get(1).getEpubline().startsWith("Chapter 50. Ahab’s Boat and Crew. Fedallah."));
    
    // first chapter: ending
    assertTrue(parts.get(2).get(0).get(parts.get(2).get(0).size() - 1)
        .getEpubline().endsWith("indulged in mundane amours."));

    // last chapter: heading
    assertTrue(parts.get(2).get(parts.get(2).size() - 1).get(1).getEpubline().startsWith("Epilogue"));

    // last chapter: ending
    assertTrue(parts.get(2).get(parts.get(2).size() - 1)
        .get(parts.get(2).get(parts.get(2).size() - 1).size() - 1)
        .getEpubline().endsWith("only found another orphan."));
  }
}
