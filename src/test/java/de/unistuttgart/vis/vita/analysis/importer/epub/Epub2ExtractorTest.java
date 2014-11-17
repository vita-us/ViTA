package de.unistuttgart.vis.vita.analysis.importer.epub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.epub.Epub2Extractor;
import de.unistuttgart.vis.vita.importer.epub.EpubFileImporter;
import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.util.Line;

/**
 * JUnit test on Epub2Extractor
 * 
 *
 */
public class Epub2ExtractorTest {

  private Epub2Extractor epub2Extractor;
  private List<ChapterPosition> chapterPositionsPart = new ArrayList<ChapterPosition>();
  private List<List<Line>> part = new ArrayList<List<Line>>();
  private List<ChapterPosition> chapterPositionsParts = new ArrayList<ChapterPosition>();
  private List<List<Line>> parts = new ArrayList<List<Line>>();
  private List<String> partsTitles = new ArrayList<String>();

  @Before
  public void setUpEpub2Part() throws URISyntaxException, IOException {
    Path testPath = Paths.get(getClass().getResource("pg78.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    epub2Extractor = new Epub2Extractor(epubFileImporter.getEbook());
    chapterPositionsPart = epub2Extractor.getChapterPositionList();
    part = epub2Extractor.getPartList();

  }

  @Before
  public void setUpEpub2Parts() throws URISyntaxException, IOException {
    Path testPath = Paths.get(getClass().getResource("pg244.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    epub2Extractor = new Epub2Extractor(epubFileImporter.getEbook());
    chapterPositionsParts = epub2Extractor.getChapterPositionList();
    parts = epub2Extractor.getPartList();
    partsTitles = epub2Extractor.getTitleList();

  }

  @Test
  public void testPartEpub2() {
    // begin of part
    assertEquals("Chapter I", part.get(0).get(0).getText());
    assertEquals("", part.get(0).get(1).getText());
    assertEquals("Out to Sea", part.get(0).get(2).getText());
    assertTrue(part.get(0).get(4).getText()
        .startsWith("I had this story from one who had no business"));

    // end of part
    assertTrue(part.get(0).get(part.get(0).size() - 2).getText()
        .startsWith("\"I was born there,\" said Tarzan, quietly."));
    assertEquals("", part.get(0).get(part.get(0).size() - 1).getText());

    // begin of chapterpostion of the first chapter; regarding heading, textstart, textend
    assertEquals(0, chapterPositionsPart.get(0).getStartOfHeading(1));
    assertEquals(4, chapterPositionsPart.get(0).getStartOfText(1));

    // end of chapterpostion of the last chapter; regarding textend
    assertEquals(5434, chapterPositionsPart.get(0).getEndOfText(29));
  }

  @Test
  public void testParts() {
    assertEquals(2, partsTitles.size());
    assertEquals("PART I.", partsTitles.get(0));
    assertEquals("PART II. The Country of the Saints.", partsTitles.get(1));

    // begin of PART I.
    assertEquals("CHAPTER I. MR. SHERLOCK HOLMES.", parts.get(0).get(0).getText());
    assertTrue(parts.get(0).get(2).getText().startsWith("IN the year 1878 I took my degree"));

    // end of PART I.
    assertTrue(parts.get(0).get(parts.get(0).size() - 2).getText()
        .endsWith("there is no danger that I will refuse to answer them.\""));
    assertEquals("", parts.get(0).get(parts.get(0).size() - 1).getText());

    // begin of PART II. The Country of the Saints.
    assertEquals("CHAPTER I. ON THE GREAT ALKALI PLAIN.", parts.get(1).get(0).getText());
    assertTrue(parts.get(1).get(2).getText()
        .startsWith("IN the central portion of the great North American Continent"));

    // end of PART II. The Country of the Saints.
    System.out.println(parts.get(1).get(parts.get(1).size() - 2).getText());
    assertTrue(parts.get(1).get(parts.get(1).size() - 2).getText()
        .endsWith("Ipse domi simul ac nummos contemplor in arca.'\""));
    assertEquals("", parts.get(1).get(parts.get(1).size() - 1).getText());
  }

  @Test
  public void testChapterPositionsParts() {
    // PART I: begin of chapterpostion of the first chapter; regarding heading, textstart, textend
    assertEquals(0, chapterPositionsParts.get(0).getStartOfHeading(1));
    assertEquals(2, chapterPositionsParts.get(0).getStartOfText(1));

    // PART I: end of chapterpostion of the last chapter; regarding textend
    assertEquals(parts.get(0).indexOf(parts.get(0).get(parts.get(0).size() - 2)),
        chapterPositionsParts.get(0).getEndOfText(7));

    // PART II. The Country of the Saints. :begin of chapterpostion of the first chapter; regarding
    // heading, textstart, textend
    assertEquals(0, chapterPositionsParts.get(1).getStartOfHeading(1));
    assertEquals(2, chapterPositionsParts.get(1).getStartOfText(1));

    // PART II. The Country of the Saints. :end of chapterpostion of the last chapter; regarding
    // textend
    assertEquals(parts.get(1).indexOf(parts.get(1).get(parts.get(1).size() - 2)),
        chapterPositionsParts.get(1).getEndOfText(7));
  }
}
