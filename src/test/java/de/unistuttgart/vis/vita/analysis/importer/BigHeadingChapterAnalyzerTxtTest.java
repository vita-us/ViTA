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
import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;



public class BigHeadingChapterAnalyzerTxtTest {

  ChapterPosition chapterPosition;
  BigHeadingChapterAnalyzer analyzer;

  @Before
  public void prepareChapterPosition() throws URISyntaxException, InvalidPathException,
      UnsupportedEncodingException,
      FileNotFoundException, SecurityException {
    Path testPath = Paths.get(getClass().getResource("BigHeadingChapters.txt").toURI());
    TextFileImporter importer = new TextFileImporter(testPath);
    this.analyzer = new BigHeadingChapterAnalyzer(importer.getLines());
    this.chapterPosition = analyzer.call();
  }

  @Test
  public void testStartPosition() {
    int startPosition = this.analyzer.getStartOfAnalysis();
    // Special case: Index at the end
    assertTrue(0 <= startPosition);
    assertTrue(startPosition <= 0);
  }

  @Test
  public void testFirstChapter() {
    int chapterNumber = 1;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 0);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 0);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 7);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 8);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 287);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 289);
  }

  @Test
  public void testSecondChapter() {
    int chapterNumber = 2;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 288);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 290);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 291);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 291);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 437);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 439);
  }

  @Test
  public void testThirdChapter() {
    int chapterNumber = 3;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 438);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 440);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 441);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 442);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 567);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 569);
  }

  @Test
  public void testFourthChapter() {
    int chapterNumber = 4;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 568);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 570);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 573);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 575);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 1066);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 1068);
  }

  @Test
  public void testFifthChapter() {
    int chapterNumber = 5;

    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) >= 1067);
    assertTrue(chapterPosition.getStartOfHeading(chapterNumber) <= 1069);

    assertTrue(chapterPosition.getStartOfText(chapterNumber) >= 1070);
    assertTrue(chapterPosition.getStartOfText(chapterNumber) <= 1073);

    assertTrue(chapterPosition.getEndOfText(chapterNumber) >= 1140);
    assertTrue(chapterPosition.getEndOfText(chapterNumber) <= 1141);
  }

  @Test
  public void testChapterSize() {
    assertTrue(chapterPosition.size() == 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInput() {
    new BigHeadingChapterAnalyzer(null);
  }

}
