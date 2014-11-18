package de.unistuttgart.vis.vita.analysis.importer;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.importer.txt.analyzers.BigHeadingChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.analyzers.MarkedHeadingChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.util.ChapterPosition;

public class MarkedHeadingChapterAnalyzerTxtTest {

  ChapterPosition chapterPosition;
  MarkedHeadingChapterAnalyzer analyzer;

  @Before
  public void prepareChapterPosition() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("MarkedHeadingChapters.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);
    this.analyzer = new MarkedHeadingChapterAnalyzer(importer.getLines());
    this.chapterPosition = analyzer.call();
  }

  @Test
  public void testStartPosition() {
    int startPosition = this.analyzer.getStartOfAnalysis();
    assertTrue(startPosition == 0);
  }

  @Test
  public void testFirstChapter() {
    int chapterNumber = 1;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 7);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 15);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 24);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 25);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 94);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 98);
  }

  @Test
  public void testSecondChapter() {
    int chapterNumber = 2;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 95);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 99);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 100);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 102);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 980);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 980);
  }

  @Test
  public void testThirdChapter() {
    int chapterNumber = 3;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 981);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 981);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 982);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 982);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 1368);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 1372);
  }

  @Test
  public void testFourthChapter() {
    int chapterNumber = 4;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 1369);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 1373);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 1375);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 1378);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 2905);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 2909);
  }

  @Test
  public void testFifthChapter() {
    int chapterNumber = 5;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 2906);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 2910);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 2913);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 2915);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 8615);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 8616);
  }

  @Test
  public void testSixthChapter() {
    int chapterNumber = 6;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 8616);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 8617);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 8618);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 8619);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 8620);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 8621);
  }

  @Test
  public void testChapterSize() {
    assertTrue(chapterPosition.size() == 6);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInput() {
    new BigHeadingChapterAnalyzer(null);
  }

}
