package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.analyzers.AdvancedBigHeadingChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.analyzers.BigHeadingChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;

public class AdvancedBigHeadingChapterAnalyzerTxtTest {

  ChapterPosition chapterPosition;
  AdvancedBigHeadingChapterAnalyzer analyzer;

  @Before
  public void prepareChapterPosition() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("AdvancedBigHeadingChapters.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);
    BigHeadingChapterAnalyzer BigAnalyzer = new BigHeadingChapterAnalyzer(importer.getLines());
    this.analyzer =
        new AdvancedBigHeadingChapterAnalyzer(importer.getLines(), BigAnalyzer.call(),
            BigAnalyzer.getStartOfAnalysis());
    this.chapterPosition = this.analyzer.call();
  }

  @Test
  public void testSmallHeadingPercentage() {
    assertTrue(this.analyzer.getSmallHeadingsPercentage() == 4.0f / 10.0f);
  }

  @Test
  public void testStartPosition() {
    int startPosition = this.analyzer.getStartOfAnalysis();
    assertTrue(58 <= startPosition);
    assertTrue(startPosition <= 61);
  }

  @Test
  public void testFirstChapter() {
    int chapterNumber = 1;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 58);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 61);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 64);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 65);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 513);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 515);
  }

  @Test
  public void testSecondChapter() {
    int chapterNumber = 2;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 514);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 516);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 521);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 522);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 755);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 757);
  }

  @Test
  public void testThirdChapter() {
    int chapterNumber = 3;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 756);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 758);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 759);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 760);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 1231);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 1233);
  }

  @Test
  public void testFourthChapter() {
    int chapterNumber = 4;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 1232);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 1234);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 1235);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 1236);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 1642);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 1645);
  }

  @Test
  public void testFifthChapter() {
    int chapterNumber = 5;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 1643);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 1646);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 1649);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 1650);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 2022);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 2025);
  }

  @Test
  public void testSixthChapter() {
    int chapterNumber = 6;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 2023);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 2026);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 2027);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 2028);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 2034);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 2036);
  }

  @Test
  public void testSeventhChapter() {
    int chapterNumber = 7;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 2035);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 2037);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 2040);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 2041);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 2589);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 2591);
  }

  @Test
  public void testEighthChapter() {
    int chapterNumber = 8;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 2590);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 2592);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 2593);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 2594);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 2594);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 2597);
  }

  @Test
  public void testNinthChapter() {
    int chapterNumber = 9;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 2595);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 2598);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 2599);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 2600);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 2610);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 2612);
  }

  @Test
  public void testTenthChapter() {
    int chapterNumber = 10;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 2611);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 2613);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 2614);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 2615);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 2621);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 2622);
  }

  @Test
  public void testChapterSize() {
    assertTrue(chapterPosition.size() == 10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInput() {
    new BigHeadingChapterAnalyzer(null);
  }
}
