package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.analyzers.BigHeadingChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.analyzers.SmallHeadingChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.util.ChapterPosition;

/**
 * Test for Big- and Smallheading focusing on the functions of the
 * AbstractSubtypeExtendedChapterAnalyzer.
 */
public class SubTypeTxtTest {

  ChapterPosition chapterPositionBigWeak;
  ChapterPosition chapterPositionBigStrong;
  ChapterPosition chapterPositionSmallWeak;
  ChapterPosition chapterPositionSmallStrong;
  BigHeadingChapterAnalyzer bigAnalyzerWeak;
  BigHeadingChapterAnalyzer bigAnalyzerStrong;
  SmallHeadingChapterAnalyzer smallAnalyzerWeak;
  SmallHeadingChapterAnalyzer smallAnalyzerStrong;

  @Before
  public void prepareChapterPositions() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {

    Path testPath1 = Paths.get(getClass().getResource("subtypeBigWeak.txt").toURI());
    TextFileImporter importer1 = new TextFileImporter(testPath1);
    this.bigAnalyzerWeak = new BigHeadingChapterAnalyzer(importer1.getLines());
    this.chapterPositionBigWeak = bigAnalyzerWeak.call();

    Path testPath2 = Paths.get(getClass().getResource("subtypeBigStrong.txt").toURI());
    TextFileImporter importer2 = new TextFileImporter(testPath2);
    this.bigAnalyzerStrong = new BigHeadingChapterAnalyzer(importer2.getLines());
    this.chapterPositionBigStrong = bigAnalyzerStrong.call();

    Path testPath3 = Paths.get(getClass().getResource("subtypeSmallStrong.txt").toURI());
    TextFileImporter importer3 = new TextFileImporter(testPath3);
    this.smallAnalyzerStrong = new SmallHeadingChapterAnalyzer(importer3.getLines());
    this.chapterPositionSmallStrong = smallAnalyzerStrong.call();

    Path testPath4 = Paths.get(getClass().getResource("subtypeSmallWeak.txt").toURI());
    TextFileImporter importer4 = new TextFileImporter(testPath4);
    this.smallAnalyzerWeak = new SmallHeadingChapterAnalyzer(importer4.getLines());
    this.chapterPositionSmallWeak = smallAnalyzerWeak.call();
  }

  @Test
  public void testBigStrong() {
    assertEquals(6, chapterPositionBigStrong.size());

    int chapterNumber = 4;

    assertTrue(chapterPositionBigStrong.getStartOfHeading(chapterNumber) >= 61);
    assertTrue(chapterPositionBigStrong.getStartOfHeading(chapterNumber) <= 63);

    assertTrue(chapterPositionBigStrong.getStartOfText(chapterNumber) >= 64);
    assertTrue(chapterPositionBigStrong.getStartOfText(chapterNumber) <= 65);

    assertTrue(chapterPositionBigStrong.getEndOfText(chapterNumber) >= 102);
    assertTrue(chapterPositionBigStrong.getEndOfText(chapterNumber) <= 104);
  }

  @Test
  public void testBigWeak() {
    assertEquals(7, chapterPositionBigWeak.size());

    // TODO:
    System.out.println(chapterPositionBigWeak.getEndOfText(4));

    int chapterNumber = 4;

    assertTrue(chapterPositionBigWeak.getStartOfHeading(chapterNumber) >= 61);
    assertTrue(chapterPositionBigWeak.getStartOfHeading(chapterNumber) <= 63);

    assertTrue(chapterPositionBigWeak.getStartOfText(chapterNumber) >= 64);
    assertTrue(chapterPositionBigWeak.getStartOfText(chapterNumber) <= 65);

    assertTrue(chapterPositionBigWeak.getEndOfText(chapterNumber) >= 102);
    assertTrue(chapterPositionBigWeak.getEndOfText(chapterNumber) <= 104);

    chapterNumber = 5;

    assertTrue(chapterPositionBigWeak.getStartOfHeading(chapterNumber) >= 82);
    assertTrue(chapterPositionBigWeak.getStartOfHeading(chapterNumber) <= 84);

    assertTrue(chapterPositionBigWeak.getStartOfText(chapterNumber) >= 85);
    assertTrue(chapterPositionBigWeak.getStartOfText(chapterNumber) <= 86);

    assertTrue(chapterPositionBigWeak.getEndOfText(chapterNumber) >= 123);
    assertTrue(chapterPositionBigWeak.getEndOfText(chapterNumber) <= 125);
  }

  @Test
  public void testSmallStrong() {
    assertEquals(6, chapterPositionSmallStrong.size());

    int chapterNumber = 4;

    assertTrue(chapterPositionBigStrong.getStartOfHeading(chapterNumber) >= 61);
    assertTrue(chapterPositionBigStrong.getStartOfHeading(chapterNumber) <= 63);

    assertTrue(chapterPositionBigStrong.getStartOfText(chapterNumber) >= 64);
    assertTrue(chapterPositionBigStrong.getStartOfText(chapterNumber) <= 65);

    assertTrue(chapterPositionBigStrong.getEndOfText(chapterNumber) >= 102);
    assertTrue(chapterPositionBigStrong.getEndOfText(chapterNumber) <= 104);
  }

  @Test
  public void testSmallWeak() {
    assertEquals(7, chapterPositionSmallWeak.size());

    int chapterNumber = 4;

    assertTrue(chapterPositionBigStrong.getStartOfHeading(chapterNumber) >= 61);
    assertTrue(chapterPositionBigStrong.getStartOfHeading(chapterNumber) <= 63);

    assertTrue(chapterPositionBigStrong.getStartOfText(chapterNumber) >= 64);
    assertTrue(chapterPositionBigStrong.getStartOfText(chapterNumber) <= 65);

    assertTrue(chapterPositionBigStrong.getEndOfText(chapterNumber) >= 102);
    assertTrue(chapterPositionBigStrong.getEndOfText(chapterNumber) <= 104);

    chapterNumber = 5;

    assertTrue(chapterPositionBigStrong.getStartOfHeading(chapterNumber) >= 82);
    assertTrue(chapterPositionBigStrong.getStartOfHeading(chapterNumber) <= 84);

    assertTrue(chapterPositionBigStrong.getStartOfText(chapterNumber) >= 85);
    assertTrue(chapterPositionBigStrong.getStartOfText(chapterNumber) <= 86);

    assertTrue(chapterPositionBigStrong.getEndOfText(chapterNumber) >= 123);
    assertTrue(chapterPositionBigStrong.getEndOfText(chapterNumber) <= 125);
  }

}
