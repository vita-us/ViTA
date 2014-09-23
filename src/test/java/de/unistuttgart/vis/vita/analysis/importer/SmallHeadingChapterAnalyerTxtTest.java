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

import de.unistuttgart.vis.vita.importer.txt.BigHeadingChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.SmallHeadingChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.TextFileImporter;

public class SmallHeadingChapterAnalyerTxtTest {

  ChapterPosition chapterPosition;
  SmallHeadingChapterAnalyzer analyzer;

  @Before
  public void prepareChapterPosition() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException, FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("SmallHeadingChapters.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);
    this.analyzer = new SmallHeadingChapterAnalyzer(importer.getLines());
    this.chapterPosition = analyzer.call();
  }

  @Test
  public void testStartPosition() {
    int startPosition = this.analyzer.getStartOfAnalysis();
    assertTrue(startPosition >= 12);
    assertTrue(startPosition <= 16);

  }

  @Test
  public void testFirstChapter() {
    int chapterNumber = 1;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 23);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 27);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 28);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 30);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 1081);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 1085);
  }

  @Test
  public void testSecondChapter() {
    int chapterNumber = 2;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 1082);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 1086);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 1087);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 1089);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 1804);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 1807);
  }

  @Test
  public void testThirdChapter() {
    int chapterNumber = 3;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 1805);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 1808);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 1809);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 1811);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 2582);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 2586);
  }

  @Test
  public void testFourthChapter() {
    int chapterNumber = 4;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 2583);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 2587);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 2588);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 2590);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 3365);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 3375);
  }

  @Test
  public void testChapterSize() {
    assertTrue(chapterPosition.size() == 4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInput() {
    new BigHeadingChapterAnalyzer(null);
  }

}
