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

import de.unistuttgart.vis.vita.importer.epub.extractors.Epub3Extractor;
import de.unistuttgart.vis.vita.importer.epub.input.EpubFileImporter;
import de.unistuttgart.vis.vita.importer.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.util.Line;

/**
 * JUnit test on Epub3Extractor
 * 
 *
 */
public class Epub3ExtractorTest {

  private Epub3Extractor epub3Extractor;
  private List<ChapterPosition> chapterPositionsPart = new ArrayList<ChapterPosition>();
  private List<List<Line>> part = new ArrayList<List<Line>>();
  private List<ChapterPosition> chapterPositionsParts = new ArrayList<ChapterPosition>();
  private List<List<Line>> parts = new ArrayList<List<Line>>();
  private List<String> partsTitles = new ArrayList<String>();

  @Before
  public void setUpEpub3Part() throws URISyntaxException, IOException {
    Path testPath = Paths.get(getClass().getResource("moby-dick-mo-20120214.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    epub3Extractor = new Epub3Extractor(epubFileImporter.getEbook());
    chapterPositionsPart = epub3Extractor.getChapterPositionList();
    part = epub3Extractor.getPartList();

  }

  @Before
  public void setUpEpub3Parts() throws URISyntaxException, IOException {
    Path testPath = Paths.get(getClass().getResource("moby-dick-mo-20120214-parts.epub").toURI());
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    epub3Extractor = new Epub3Extractor(epubFileImporter.getEbook());
    chapterPositionsParts = epub3Extractor.getChapterPositionList();
    parts = epub3Extractor.getPartList();
    partsTitles = epub3Extractor.getTitleList();

  }

  @Test
  public void testPartEpub2() {
    // begin of part
    assertEquals("Chapter 1. Loomings.", part.get(0).get(0).getText());
    assertEquals("", part.get(0).get(1).getText());
    assertTrue(part.get(0).get(2).getText().startsWith("Call"));
    assertTrue(part.get(0).get(4).getText().startsWith("There now is your insular"));

    // end of part
    assertTrue(part.get(0).get(part.get(0).size() - 2).getText()
        .startsWith("It so chanced, that after"));
    assertEquals("", part.get(0).get(part.get(0).size() - 1).getText());

    // begin of chapterpostion of the first chapter; regarding heading, textstart, textend
    assertEquals(0, chapterPositionsPart.get(0).getStartOfHeading(1));
    assertEquals(2, chapterPositionsPart.get(0).getStartOfText(1));

    // end of chapterpostion of the last chapter; regarding textend
    assertEquals(part.get(0).indexOf(part.get(0).get(part.get(0).size() - 2)), chapterPositionsPart
        .get(0).getEndOfText(137));
  }

  @Test
  public void testParts() {
    assertEquals(3, partsTitles.size());
    assertEquals("Part I", partsTitles.get(0));
    assertEquals("Part Ib", partsTitles.get(1));
    assertEquals("Part II", partsTitles.get(2));


    // begin of Part I
    assertEquals("Chapter 1. Loomings.", parts.get(0).get(0).getText());
    assertTrue(parts.get(0).get(2).getText().startsWith("Call"));

    // end of Part I
    assertTrue(parts.get(0).get(parts.get(0).size() - 2).getText()
        .endsWith("like a snow hill in the air."));
    assertEquals("", parts.get(0).get(parts.get(0).size() - 1).getText());

    // begin of Part Ib
    assertEquals("Chapter 2. The Carpet-Bag.", parts.get(1).get(0).getText());
    assertTrue(parts.get(1).get(2).getText().startsWith("I stuffed a shirt or two into"));

    // end of Part Ib
    assertTrue(parts.get(1).get(parts.get(1).size() - 2).getText()
        .endsWith("and the devil fetch the hindmost."));
    assertEquals("", parts.get(1).get(parts.get(1).size() - 1).getText());

    // begin of Part II
    assertEquals("Chapter 50. Ahab’s Boat and Crew. Fedallah.", parts.get(2).get(0).getText());
    assertTrue(parts.get(2).get(2).getText().startsWith("“Who would have thought it, Flask!”"));

    // end of Part II
    assertTrue(parts.get(2).get(parts.get(2).size() - 2).getText()
        .endsWith("only found another orphan."));
    assertEquals("", parts.get(2).get(parts.get(2).size() - 1).getText());

  }

  @Test
  public void testChapterPositionsPart() {
    // Part I: begin of chapterpostion of the first chapter; regarding heading, textstart, textend
    assertEquals(0, chapterPositionsParts.get(0).getStartOfHeading(1));
    assertEquals(2, chapterPositionsParts.get(0).getStartOfText(1));

    // Part I: end of chapterpostion of the last chapter; regarding textend
    assertEquals(parts.get(0).indexOf(parts.get(0).get(parts.get(0).size() - 2)),
        chapterPositionsParts.get(0).getEndOfText(1));

    // Part Ib:begin of chapterpostion of the first chapter; regarding
    // heading, textstart, textend
    assertEquals(0, chapterPositionsParts.get(1).getStartOfHeading(1));
    assertEquals(2, chapterPositionsParts.get(1).getStartOfText(1));

    // Part Ib:end of chapterpostion of the last chapter; regarding
    // textend
    assertEquals(parts.get(1).indexOf(parts.get(1).get(parts.get(1).size() - 2)),
        chapterPositionsParts.get(1).getEndOfText(48));

    // Part II:begin of chapterpostion of the first chapter; regarding
    // heading, textstart, textend
    assertEquals(0, chapterPositionsParts.get(2).getStartOfHeading(1));
    assertEquals(2, chapterPositionsParts.get(2).getStartOfText(1));

    // Part II:end of chapterpostion of the last chapter; regarding
    // textend
    assertEquals(parts.get(2).indexOf(parts.get(2).get(parts.get(2).size() - 2)),
        chapterPositionsParts.get(2).getEndOfText(87));
  }
}
